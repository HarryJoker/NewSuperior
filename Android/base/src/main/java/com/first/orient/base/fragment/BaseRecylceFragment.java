package com.first.orient.base.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.R;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.utils.JokerLog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

/**
 * BaseRecycleFragment
 *
 * 封装了RecylceView的Fragment
 *
 * 继承该fragment 只需实现ViewHolder及数据源设置即可实现
 *fragment的列表数据展示
 *
 *
 */
public abstract class BaseRecylceFragment extends BaseContentFragment implements OnRefreshListener, BaseRecyclerAdapter.OnRecyclerItemClickListener {

    protected RecyclerView mRecyclerView;

    protected SmartRefreshLayout mSmartRefreshLayout;

    private BaseRecyclerAdapter mRecyclerAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_recylce;
    }

    @Override
    public void onFindViews(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.recycler);
        mSmartRefreshLayout = rootView.findViewById(R.id.refreshLayout);
        JokerLog.d(mSmartRefreshLayout == null ? "null" : "not null");
    }

    @Override
    public void onInitViews() {
        mSmartRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecyclerAdapter = new RecyclAdapter(getActivity()));
        mRecyclerAdapter.setOnRecyclerItemClickListener(this);
    }

    protected void finishRefresh() {
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishRefresh();
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setDataSource(List<JSONObject> data) {
        mSmartRefreshLayout.finishRefresh();
        if (mRecyclerAdapter != null) {
            mRecyclerAdapter.setDataList(data);
        }
    }

    protected int onItemViewType(int position, JSONObject data) {
        return 0;
    }

    public void clearDataSource() {
        mRecyclerAdapter.clearAllItem();
    }

    public BaseRecyclerAdapter getRecyclerAdapter() {
        return mRecyclerAdapter;
    }

    protected abstract BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType);

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
            if (holder instanceof BaseViewHolder) {
                ( (BaseViewHolder)holder).bindViewHolder(position, getItemData(position));
            }
        }
    }
}
