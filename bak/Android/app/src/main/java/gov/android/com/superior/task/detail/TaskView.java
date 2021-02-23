package gov.android.com.superior.task.detail;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;

import gov.android.com.superior.R;
import gov.android.com.superior.task.LabelUtils;

public class TaskView extends LinearLayout {

    public static final int TYPE_DETAIL = 0X01;
    public static final int TYPE_THUMB  = 0X02;


    protected String[] categoryNames = new String[] {"", "政府工作报告", "市委市政府重大决策部署", "建议提案", "会议议定事项", "领导批示", "专项督查", "重点项目"};

    private TextView tvCategory;

    private TextView tvTitleLabel;
    private TextView tvTitle;

    private TextView tvContentLabel;
    private TextView tvContent;

    private View layoutTaskPlan;
    private TextView tvPlan;

    private View layoutTaskInfo;
    private TextView tvTaskInfo;

    private TextView tvArrow;

    private int type = 0;

    public TaskView(Context context) {
        super(context);
        initView();
    }

    public TaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TaskView);
        type = ta.getInt(R.styleable.TaskView_type, 0);

        initView();
    }

    public TaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TaskView);
        type = ta.getInt(R.styleable.TaskView_type, 0);

        initView();
    }

    private void initView() {

        inflate(getContext(), R.layout.layout_task, this);

        tvCategory =  findViewById(R.id.tv_category);

        tvTitleLabel =  findViewById(R.id.tv_title_label);
        tvTitle =  findViewById(R.id.tv_title);

        tvContentLabel =  findViewById(R.id.tv_content_label);
        tvContent = findViewById(R.id.tv_content);

        layoutTaskPlan = findViewById(R.id.layout_task_plan);
        tvPlan = findViewById(R.id.tv_plan);

        layoutTaskInfo = findViewById(R.id.layout_taskinfo);
        tvTaskInfo = findViewById(R.id.tv_task_info);

        tvArrow = findViewById(R.id.tv_arrow);

        if (type == TYPE_THUMB) {
            tvTitle.setLines(1);
            tvTitle.setEllipsize(TextUtils.TruncateAt.END);

            tvContent.setLines(1);
            tvContent.setEllipsize(TextUtils.TruncateAt.END);

            tvPlan.setLines(1);
            tvPlan.setEllipsize(TextUtils.TruncateAt.END);

            tvTaskInfo.setLines(1);
            tvTaskInfo.setEllipsize(TextUtils.TruncateAt.END);
        }

        if (type == TYPE_DETAIL) {
            tvArrow.setVisibility(GONE);
        }
    }


    public void refreshTaskView(final JSONObject task, final JSONObject childTask) {
        if (task == null || childTask == null || task.size() == 0 || childTask.size() == 0) return;
        int category = task.getIntValue("category");
        tvTitleLabel.setText(LabelUtils.makeTaskTitleLabel(category));
        tvTitle.setText(task.getString("title"));

        tvContentLabel.setText(LabelUtils.makeChildTaskTitleLabel(category));
        tvContent.setText(childTask.getString("content"));

        if (task.containsKey("content") && !TextUtils.isEmpty(task.getString("content"))) {
            tvPlan.setText(task.getString("content"));
            layoutTaskPlan.setVisibility(VISIBLE);
        }

        if (task.containsKey("taskIntro") && !TextUtils.isEmpty(task.getString("taskIntro"))) {
            tvTaskInfo.setText(task.getString("taskIntro"));
            layoutTaskInfo.setVisibility(VISIBLE);
        }

        tvCategory.setText(categoryNames[task.getIntValue("category")]);

        tvArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                intent.putExtra("task", task.toJSONString());
                intent.putExtra("childTask", childTask.toJSONString());
                getContext().startActivity(intent);
            }
        });
    }

}
