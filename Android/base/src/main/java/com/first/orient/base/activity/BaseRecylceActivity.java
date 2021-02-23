package com.first.orient.base.activity;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

/**
 * 单一类型列表的抽象activity
 * 子类实现：
 * 1，对应的ViewHolder
 * 2，数据源请求
 * 3，click事件
 */
public abstract class BaseRecylceActivity extends BaseToolBarActivity implements OnLoadMoreListener, OnRefreshListener, BaseRecyclerAdapter.OnRecyclerItemClickListener {

    protected RecyclerView mRecyclerView;

    protected SmartRefreshLayout mSmartRefreshLayout;

    private BaseRecyclerAdapter mRecyclerAdapter;

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
        mSmartRefreshLayout.setEnableRefresh(isRefreshEnable());
        mSmartRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecyclerAdapter = new RecyclAdapter(this));
        mRecyclerAdapter.setOnRecyclerItemClickListener(this);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setDataSource(List<JSONObject> data) {
        mSmartRefreshLayout.finishRefresh();
        if (mRecyclerAdapter != null) {
            mRecyclerAdapter.setDataList(data);
        }
        if (data == null || data.size() == 0) {
            EmptyEntity emptyEntity = getEmptyEntry();
            if (emptyEntity != null) {
                showEmpty(emptyEntity.resImg, emptyEntity.title, emptyEntity.subTitle);
            }
        }
    }

    protected abstract EmptyEntity getEmptyEntry();

    public void clearDataSource() {
        mRecyclerAdapter.clearAllItem();
    }

    public BaseRecyclerAdapter getRecyclerAdapter() {
        return mRecyclerAdapter;
    }

    public boolean isRefreshEnable() {
        return true;
    }

    @Override
    protected void onErrorResponse() {
        mSmartRefreshLayout.finishRefresh();
    }

    class RecyclAdapter extends BaseRecyclerAdapter<JSONObject> {

        public RecyclAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewType(int position) {
            return onItemViewType(position, getItemData(position));
        }

        @Override
        public BaseViewHolder onCreateBaseViewHolder(@NonNull ViewGroup parent, int viewType) {
            BaseViewHolder holder = getViewHolder(parent, viewType);
            if (holder == null) throw new NullPointerException("ViewHolder Null.....");
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder != null && holder instanceof BaseViewHolder) {
                ( (BaseViewHolder)holder).bindViewHolder(position, getItemData(position));
            }
        }
    }

    protected int onItemViewType(int position, JSONObject data) {
        return 0;
    }

    public abstract BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType);

    public class EmptyEntity {
        int resImg;
        String title;
        String subTitle;
        public EmptyEntity(int imgRes, String title, String subTitle) {
            this.resImg = imgRes;
            this.title = title;
            this.subTitle = subTitle;
        }
    }

}
