package gov.android.com.superior.task.detail;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.trace.AttachmentAdapter;

public class TaskDetailActivity extends BaseActivity {

    private TaskView mTaskView;

    private RecyclerView mRcAttachments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail2);

        setTitle("任务详情");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTaskView = findViewById(R.id.taskView);

        String param = getIntent().getStringExtra("task");

        if (TextUtils.isEmpty(param)) {
            throw new NullPointerException(" task object null..........");
        }

        JSONObject task = JSONObject.parseObject(param);

        param = getIntent().getStringExtra("childTask");
        if (TextUtils.isEmpty(param)) {
            throw new NullPointerException(" childTask object null..........");
        }
        JSONObject childTask =  JSONObject.parseObject(param);

        mTaskView.refreshTaskView(task, childTask);

        if (task != null && task.containsKey("attachmentids") && !TextUtils.isEmpty(task.getString("attachmentids"))) {
            String attachment = task.getString("attachmentids");
            List<String> attachments = new ArrayList<>(Arrays.asList(attachment.split(",")));

            if (attachments.size() > 0) {

                findViewById(R.id.layout_attachment).setVisibility(View.VISIBLE);
                mRcAttachments = findViewById(R.id.rc_attachment);
                mRcAttachments.setLayoutManager(new GridLayoutManager(this, 5));
                mRcAttachments.setAdapter(new AttachmentAdapter(this, attachments));
            }
        }
    }

}
