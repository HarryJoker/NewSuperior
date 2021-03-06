package gov.android.com.superior.ui.unit.masses.score;


import android.content.Intent;
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
import gov.android.com.superior.ui.unit.masses.score.radar.UnitRadarActivity;

/**
 * A simple {@link Fragment} subclass.
 *
 * 督查画像列表
 */
public class UnitRadarFragment extends BaseRecylceFragment {

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_unit_radar;
    }

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
        return new RadarViewHolder(getLayoutInflater().inflate(R.layout.rc_item_unit_radar, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        Intent intent = new Intent(getActivity(), UnitRadarActivity.class);
        intent.putExtra("unitId", data.getJSONObject("unit").getIntValue("id"));
        intent.putExtra("rank", data.getIntValue("rank"));
        intent.putExtra("unit", data.getJSONObject("unit").toJSONString());
        startActivity(intent);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestUnitAppraisalList();
    }

    private void requestUnitAppraisalList() {
        OkGo.<List<JSONObject>>get(HttpUrl.DUTY_UNIT_SCORE_LIST).tag(this).execute(getJsonArrayCallback(HttpUrl.DUTY_UNIT_SCORE_LIST));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }


    class RadarViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

        private TextView tvRank;
        private TextView tvUnitName;
        private TextView tvScore;

        public RadarViewHolder(View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvUnitName = itemView.findViewById(R.id.tv_unit_name);
            tvScore = itemView.findViewById(R.id.tv_score);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
            tvRank.setText(data.getString("rank"));
            tvUnitName.setText(data.getJSONObject("unit").getString("name"));
            tvScore.setText(data.getFloatValue("synthesis") >= 90 ? "A" : "B");
        }
    }

}
