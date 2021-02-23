package gov.android.com.superior.trace;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.task.StatusConfig;

public class TraceDetailActivity extends BaseActivity {

    private TextView tvStatus;
    private TextView tvConetnt;
    private LinearLayout layoutAttachment;
    private RecyclerView rcAttachment;

    private LinearLayout layProgress;

    private TextView tvProgressLabel;

    private ProgressBar mProgressBar;

    private TextView tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("进展详情");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_trace_detail);

        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvConetnt = (TextView) findViewById(R.id.tv_conetnt);
        layoutAttachment = (LinearLayout) findViewById(R.id.layout_attachment);
        rcAttachment = (RecyclerView) findViewById(R.id.rc_attachment);

        layProgress = findViewById(R.id.progress);

        tvProgressLabel = findViewById(R.id.tv_progress_label);

        mProgressBar = findViewById(R.id.pb_progress);

        tvProgress = findViewById(R.id.tv_gress);

        String param = getIntent().getStringExtra("trace");
        if (TextUtils.isEmpty(param)) {
            throw new NullPointerException("trace object null........");
        }

        JSONObject trace = JSONObject.parseObject(param);

        tvConetnt.setText(trace.getString("content"));

        tvStatus.setText(StatusConfig.STATUS.get(trace.getString("status")));


        int status = trace.getIntValue("status");
        if (status == 31 || status == 71 || status == 72 || status == 73) {
            layProgress.setVisibility(View.VISIBLE);
            tvProgressLabel.setText(status == 31 ? "报送进度：" : "审核进度：");
            tvProgress.setText(trace.getString("progress") + "%");
            mProgressBar.setProgress(trace.getIntValue("progress"));
        }

        if (!TextUtils.isEmpty(trace.getString("attachments"))) {
            layoutAttachment.setVisibility(View.VISIBLE);
            rcAttachment.setLayoutManager(new GridLayoutManager(this, 5));
            List<String> attachments = new ArrayList<>(Arrays.asList(trace.getString("attachments").split(",")));
            rcAttachment.setAdapter(new AttachmentAdapter(this, attachments));
        }
    }
}
