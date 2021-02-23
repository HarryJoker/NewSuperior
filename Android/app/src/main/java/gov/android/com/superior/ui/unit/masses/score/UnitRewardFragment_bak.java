package gov.android.com.superior.ui.unit.masses.score;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.first.orient.base.utils.DpUtils;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;

/**
 * A simple {@link Fragment} subclass.
 * 督查奖励列表
 */
public class UnitRewardFragment_bak extends BaseRecylceFragment {

    @Override
    public void onInitViews() {
        super.onInitViews();
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL, DpUtils.getSmallestScreenWidth(getContext(), R.dimen.dp_1), getContext().getResources().getColor(R.color.avatar_default_border)));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new RewardViewHolder(getLayoutInflater().inflate(R.layout.rc_item_unit_reward, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestUnitAppraisalList();
    }

    private void requestUnitAppraisalList() {
        OkGo.<List<JSONObject>>get(HttpUrl.GET_UNITTASK_REWARDS).tag(this).execute(getJsonArrayCallback(HttpUrl.GET_UNITTASK_REWARDS));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }


    class RewardViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

        private TextView tvTitle;
        private TextView tvReward;
        private TextView tvUnitName;

        public RewardViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvReward = itemView.findViewById(R.id.tv_reward);
            tvUnitName = itemView.findViewById(R.id.tv_unit_name);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
            tvTitle.setText(data.getString("title"));
            JSONObject unitTask = data.getJSONArray("unitTasks").getJSONObject(0);
            tvUnitName.setText(unitTask.getString("unitName"));
            tvReward.setText(unitTask.getString("reward"));
        }
    }

}
