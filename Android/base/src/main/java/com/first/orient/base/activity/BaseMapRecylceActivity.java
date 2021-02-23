package com.first.orient.base.activity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.R;
import com.first.orient.base.adapter.BaseMapRecycleAdapter;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * 单一类型列表的抽象activity
 * 子类实现：
 * 1，对应的ViewHolder
 * 2，数据源请求
 * 3，click事件
 */
public abstract class BaseMapRecylceActivity extends BaseToolBarActivity implements OnRefreshListener, BaseMapRecycleAdapter.OnRecyclerItemClickListener, BaseMapRecycleAdapter.OnMetaValueChangeCallback {

    private RecyclerView mRecyclerView;

    protected SmartRefreshLayout mSmartRefreshLayout;

    private BaseMapRecycleAdapter mRecyclerAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_recylce;
    }

    @Override
    protected void onFindViews() {
        mRecyclerView = findViewById(R.id.recycler);
        mSmartRefreshLayout = findViewById(R.id.refreshLayout);
    }

    @Override
    public void onInitView() {
        mSmartRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecyclerAdapter = new BaseMapRecycleAdapter(this, getMetaBeans(), getHolderMetas()));
        mRecyclerAdapter.setOnMetaValueChangeCallback(this);
        mRecyclerAdapter.setOnRecyclerItemClickListener(this);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setDataSource(JSONObject data) {
        mSmartRefreshLayout.finishRefresh();
        if (mRecyclerAdapter != null && data != null && data.size() > 0) {
            mRecyclerAdapter.setDataSource(data);
        }
    }

    public void updateDataSource(String key, String value, int index) {
        mRecyclerAdapter.updateDataSource(key, value, index);
    }

    @Override
    public String onMetaValue(String key, String value) {
        return value;
    }

    public void clearDataSource() {
        mRecyclerAdapter.clear();
    }

    public BaseMapRecycleAdapter getRecyclerAdapter() {
        return mRecyclerAdapter;
    }

    public abstract List<BaseMapRecycleAdapter.MetaBean> getMetaBeans();

    public abstract Map<Integer, BaseMapRecycleAdapter.HolderMeta> getHolderMetas();

}
