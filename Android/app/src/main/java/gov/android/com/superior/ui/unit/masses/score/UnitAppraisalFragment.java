package gov.android.com.superior.ui.unit.masses.score;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 *  督查考评列表
 */
public class UnitAppraisalFragment extends BaseRecylceFragment {

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_unit_appraisal;
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
        return new AppraisalViewHolder(getLayoutInflater().inflate(R.layout.rc_item_unit_appraisal, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestUnitAppraisalList();
    }

    private void requestUnitAppraisalList() {
        OkGo.<List<JSONObject>>get(HttpUrl.DUTY_UNIT_SCORE_LIST ).tag(this).execute(getJsonArrayCallback(HttpUrl.DUTY_UNIT_SCORE_LIST));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }


    class AppraisalViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

        private TextView tvId;
        private TextView tvUnitName;
        private ImageView ivScore;

        public AppraisalViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvUnitName = itemView.findViewById(R.id.tv_unit_name);
            ivScore = itemView.findViewById(R.id.iv_score);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
            tvId.setText((position + 1) + "");
            tvUnitName.setText(data.getJSONObject("unit").getString("name"));
            ivScore.setImageResource(data.getFloatValue("synthesis") >= 90 ? R.mipmap.score_good : R.mipmap.score_common);
        }
    }

}
