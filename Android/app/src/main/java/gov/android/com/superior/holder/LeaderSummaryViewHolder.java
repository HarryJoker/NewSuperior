package gov.android.com.superior.holder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.makeramen.roundedimageview.RoundedImageView;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;

public class LeaderSummaryViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

    private RoundedImageView lvLogo;
    private TextView tvUnit;
    private TextView tvName;
    private TextView tvMore;
    private TextView tvTaskCount;
    private TextView tvFinishCount;
    private TextView tvDoingCount;
    private TextView tvSlow;
    private TextView tvNomal;
    private TextView tvFast;
    private TextView tvFinish;
    private TextView tvSumOverdueCount;
//    private TextView tvSumSlowCount;
    private TextView tvSumBackCount;
//    private TextView tvSumFastCount;

    public LeaderSummaryViewHolder(@NonNull Activity context, @NonNull ViewGroup parent) {
        this(context.getLayoutInflater().inflate(R.layout.rc_item_leader_summary, parent, false));
    }

    public LeaderSummaryViewHolder(View itemView) {
        super(itemView);

        lvLogo = (RoundedImageView) itemView.findViewById(R.id.lv_logo);
        tvUnit = (TextView) itemView.findViewById(R.id.tv_unit);
        tvName = (TextView) itemView.findViewById(R.id.tv_name);
        tvMore = (TextView) itemView.findViewById(R.id.tv_more);

        tvTaskCount = (TextView) itemView.findViewById(R.id.tv_taskCount);
        tvFinishCount = (TextView) itemView.findViewById(R.id.tv_finish_count);
        tvDoingCount = (TextView) itemView.findViewById(R.id.tv_doing_count);

        tvSlow = (TextView) itemView.findViewById(R.id.tv_slow);
        tvNomal = (TextView) itemView.findViewById(R.id.tv_nomal);
        tvFast = (TextView) itemView.findViewById(R.id.tv_fast);
        tvFinish = (TextView) itemView.findViewById(R.id.tv_finish);

        tvSumOverdueCount = (TextView) itemView.findViewById(R.id.tv_sum_overdue_count);
//        tvSumSlowCount = (TextView) itemView.findViewById(R.id.tv_sum_slow_count);
        tvSumBackCount = (TextView) itemView.findViewById(R.id.tv_sum_back_count);
//        tvSumFastCount = (TextView) itemView.findViewById(R.id.tv_sum_fast_count);
    }

    @Override
    public void bindViewHolder(int position, JSONObject data) {
        if (data == null || data.size() == 0) return;
        if (data.containsKey("unit")) {
            bindUnitViewHolder(data.getJSONObject("unit"));
        }
        if (data.containsKey("taskSummary")) {
            bindTaskSummaryViewHolder(data.getJSONObject("taskSummary"));
        }
        if (data.containsKey("traceSummary")) {
            bindTraceSummaryViewHolder(data.getJSONObject("traceSummary"));
        }
    }

    private void bindUnitViewHolder(JSONObject unit) {
        if (unit == null || unit.size() == 0) return;
        Glide.with(itemView).load(HttpUrl.ATTACHMENT + unit.getString("logo")).placeholder(R.mipmap.ic_avatar).into(lvLogo);
        tvName.setText(unit.get("name").toString());
        tvUnit.setText("县长");
    }

    public void bindTaskSummaryViewHolder(JSONObject taskSummary) {
        if (taskSummary == null || taskSummary.size() == 0) return;
        //Task中的任务数统计
        tvTaskCount.setText(taskSummary.getString("taskCount"));
        tvFinishCount.setText("已完成 " + taskSummary.getString("doneCount") + " 项");
        tvDoingCount.setText("未完成 " + (taskSummary.getIntValue("taskCount") - taskSummary.getIntValue("doneCount")) + " 项");

        //Task的当前状态统计
        tvSlow.setText( taskSummary.getString("slowCount"));
        tvNomal.setText( taskSummary.getString("nomalCount"));
        tvFast.setText( taskSummary.getString("fastCount"));
        tvFinish.setText( taskSummary.getString("doneCount"));
    }

    public void bindTraceSummaryViewHolder(JSONObject traceSummary) {
        if (traceSummary == null || traceSummary.size() == 0) return;
        //trace中的状态统计
        tvSumOverdueCount.setText("共逾期" + traceSummary.getString("overdueCount") +"次");
//        tvSumSlowCount.setText(traceSummary.getString("slowCount"));
        tvSumBackCount.setText("共退回"  +traceSummary.getString("backCount")+ "次");
//        tvSumFastCount.setText(traceSummary.getString("fastCount"));
    }

}
