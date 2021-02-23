package gov.android.com.superior.home;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;

public class TaskSummaryView extends LinearLayout {

    private AvatarImageView lvLogo;
    private TextView tvUnit;
    private TextView tvName;

    private TextView tvTaskCount;
    private TextView tvFinishCount;
    private TextView tvDoingCount;

    private TextView tvSlow;
    private TextView tvNomal;
    private TextView tvFast;
    private TextView tvFinish;

    private TextView tvSumOverdueCount;
    private TextView tvSumSlowCount;
    private TextView tvSumBackCount;
    private TextView tvSumFastCount;
    
    public TaskSummaryView(Context context) {
        super(context);
        initView();
    }

    public TaskSummaryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TaskSummaryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.item_leader_card, this);

        lvLogo = (AvatarImageView)findViewById(R.id.lv_logo);
        tvUnit = (TextView)findViewById(R.id.tv_unit);
        tvName = (TextView)findViewById(R.id.tv_name);

        tvTaskCount = findViewById(R.id.tv_taskCount);
        tvFinishCount = (TextView)findViewById(R.id.tv_finish_count);
        tvDoingCount = (TextView)findViewById(R.id.tv_doing_count);

        tvSlow = (TextView)findViewById(R.id.tv_slow);
        tvNomal = (TextView)findViewById(R.id.tv_nomal);
        tvFast = (TextView)findViewById(R.id.tv_fast);
        tvFinish = (TextView)findViewById(R.id.tv_finish);

        tvSumOverdueCount = (TextView) findViewById(R.id.tv_sum_overdue_count);
        tvSumSlowCount = (TextView) findViewById(R.id.tv_sum_slow_count);
        tvSumBackCount = (TextView) findViewById(R.id.tv_sum_back_count);
        tvSumFastCount = (TextView) findViewById(R.id.tv_sum_fast_count);

        refreshUnit();
    }

    private void refreshUnit() {
        Glide.with(getContext()).load(Config.ATTACHMENT + User.getInstance().getUnitLogo()).into(lvLogo);
        tvName.setText(User.getInstance().get("name").toString());
        tvUnit.setText("博兴县人民办公室");
    }

    public void refreshTaskSummary(JSONObject taskSummary, JSONObject traceSummary) {
        if (taskSummary != null) {
            //Task中的任务数统计
            tvTaskCount.setText("任务统计 " + taskSummary.getString("taskCount") + " 项");
            tvFinishCount.setText("已完成 " + taskSummary.getString("doneCount") + " 项");
            tvDoingCount.setText("进行中 " + taskSummary.getString("doingCount") + " 项");

            //Task的当前状态统计
            tvSlow.setText("缓慢 " + taskSummary.getString("slowCount"));
            tvNomal.setText("正常  " + taskSummary.getString("doingCount"));
            tvFast.setText("较快 " + taskSummary.getString("fastCount"));
            tvFinish.setText("完成 " + taskSummary.getString("doneCount"));
        }

        if (traceSummary != null) {
            //trace中的状态统计
            tvSumOverdueCount.setText(traceSummary.getString("sumOverdueCount"));
            tvSumSlowCount.setText(traceSummary.getString("sumSlowCount"));
            tvSumBackCount.setText(traceSummary.getString("sumBackCount"));
            tvSumFastCount.setText(traceSummary.getString("sumFastCount"));
        }
    }
}
