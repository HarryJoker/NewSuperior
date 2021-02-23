package gov.android.com.superior.holder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;

import gov.android.com.superior.R;
import gov.android.com.superior.tools.DateUtil;

public class LeaderMonthSummaryViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

    TextView tvSumCount;

    TextView tvSlow;
    TextView tvNomal;
    TextView tvFast;
    TextView tvFinish;

    TextView tvOverdueCount;
    TextView tvSlowCount;
    TextView tvBackCount;
    TextView tvFastCount;

    TextView tvMore;

    TextView tvMonth;
    
    public LeaderMonthSummaryViewHolder(@NonNull Activity context, @NonNull ViewGroup parent) {
        this(context.getLayoutInflater().inflate(R.layout.rc_item_leader_month_summary, parent, false));
    }

    public LeaderMonthSummaryViewHolder(View itemView) {
        super(itemView);

        tvMore = itemView.findViewById(R.id.tv_more);
        tvMore.setVisibility(View.VISIBLE);
//        tvMore.setOnClickListener(mOnMonthClickListener);

        tvSumCount = itemView.findViewById(R.id.tv_sum_count);
        tvSlow = (TextView) itemView.findViewById(R.id.tv_slow);
        tvNomal = (TextView) itemView.findViewById(R.id.tv_nomal);
        tvFast = (TextView) itemView.findViewById(R.id.tv_fast);
        tvFinish = (TextView) itemView.findViewById(R.id.tv_finish);

        tvSlowCount = (TextView) itemView.findViewById(R.id.tv_slow_count);
        tvOverdueCount = (TextView) itemView.findViewById(R.id.tv_overdue_count);
        tvBackCount = (TextView) itemView.findViewById(R.id.tv_back_count);
        tvFastCount = (TextView) itemView.findViewById(R.id.tv_fast_count);

        tvMonth = itemView.findViewById(R.id.tv_month);
    }

    @Override
    public void bindViewHolder(int position, JSONObject data) {

        tvMonth.setText("我的月报（" + DateUtil.getNowMonth() + "月份）");

        JSONObject monthTaskSummary = data.getJSONObject("monthTaskSummary");
        JSONObject monthTraceSummary = data.getJSONObject("monthTraceSummary");

        tvOverdueCount.setText(monthTraceSummary.getString("overdueCount"));
        tvSlowCount.setText(monthTraceSummary.getString("slowCount"));
        tvBackCount.setText(monthTraceSummary.getString("backCount"));
        tvFastCount.setText(monthTraceSummary.getString("fastCount"));

        //Task的当前状态统计
        tvSumCount.setText("任务 " + monthTaskSummary.getString("taskCount") + " 项");

        tvSlow.setText("缓慢 " + monthTaskSummary.getString("slowCount"));
        tvNomal.setText("正常  " + monthTaskSummary.getString("nomalCount"));
        tvFast.setText("较快 " + monthTaskSummary.getString("fastCount"));
        tvFinish.setText("完成 " + monthTaskSummary.getString("doneCount"));
    }
}
