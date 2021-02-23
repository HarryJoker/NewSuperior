package gov.android.com.superior.home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.task.month.TaskMonthActivity;
import gov.android.com.superior.tools.DateUtil;

public class TaskMonthView extends LinearLayout {

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

    private int category;

    public TaskMonthView(Context context) {
        super(context);

        initView();
    }

    public TaskMonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TaskMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setCategory(int category) {
        this.category = category;
    }

    private void initView() {
        inflate(getContext(), R.layout.item_month_card, this);

        tvSumCount = findViewById(R.id.tv_sum_count);

        tvSlow = (TextView) findViewById(R.id.tv_slow);
        tvNomal = (TextView) findViewById(R.id.tv_nomal);
        tvFast = (TextView) findViewById(R.id.tv_fast);
        tvFinish = (TextView) findViewById(R.id.tv_finish);

        tvSlowCount = (TextView) findViewById(R.id.tv_slow_count);
        tvOverdueCount = (TextView) findViewById(R.id.tv_overdue_count);
        tvBackCount = (TextView) findViewById(R.id.tv_back_count);
        tvFastCount = (TextView) findViewById(R.id.tv_fast_count);

        tvMore = findViewById(R.id.tv_more);

        tvMore.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), TaskMonthActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("month", DateUtil.getNowMonth());
            intent.putExtra("leaderUnitId", User.getInstance().getUnitId());

            getContext().startActivity(intent);
        }
    };

    public void refreshMonthTaskSummary(JSONObject taskSummary, JSONObject traceSummary) {
        if (traceSummary != null && traceSummary.size() > 0) {
            tvOverdueCount.setText(traceSummary.getString("sumOverdueCount"));
            tvSlowCount.setText(traceSummary.getString("sumSlowCount"));
            tvBackCount.setText(traceSummary.getString("sumBackCount"));
            tvFastCount.setText(traceSummary.getString("sumFastCount"));
        }

        if (taskSummary != null) {
            //Task的当前状态统计
            tvSumCount.setText("任务 " + taskSummary.getString("taskCount") + " 项");
            tvSlow.setText("缓慢 " + taskSummary.getString("slowCount"));
            tvNomal.setText("正常  " + taskSummary.getString("doingCount"));
            tvFast.setText("较快 " + taskSummary.getString("fastCount"));
            tvFinish.setText("完成 " + taskSummary.getString("doneCount"));
        }
    }
}
