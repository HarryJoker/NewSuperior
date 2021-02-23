package gov.android.com.superior.ui.unit.masses;

import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.lzy.okgo.OkGo;

import gov.android.com.superior.R;
import gov.android.com.superior.config.Config;
import gov.android.com.superior.http.HttpUrl;

public class TaskInformActivity extends BaseToolBarActivity {

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvUnitName;
    private TextView tvProgress;
    private TextView tvStatus;

    private int mTaskId;

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("进展通报");
    }

    @Override
    public void onInitParams() {
        mTaskId = getIntent().getIntExtra("taskId", 0);
        if (mTaskId == 0) {
            showToast("数据错误");
            finish();
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_task_inform;
    }

    @Override
    protected void onFindViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvUnitName = (TextView) findViewById(R.id.tv_unit_name);

        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        requestTaskDetail();
    }

    private void requestTaskDetail() {
        showLoading();
        OkGo.<JSONObject>get(HttpUrl.TASK_DETAIL + "/" + mTaskId).tag(this).execute(getJsonObjectCallback(HttpUrl.TASK_DETAIL));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        hideLoading();
        refreshOpinion(data);
    }

    private void refreshOpinion(JSONObject data) {
        tvTitle.setText(data.getString("title"));
        tvContent.setText(data.getString("content"));

        JSONObject unitTask = data.getJSONArray("unitTasks").getJSONObject(0);
        tvUnitName.setText(unitTask.getString("unitName"));
        if (Config.informstatus.containsKey(unitTask.getString("progressStatus"))) {
            tvStatus.setText(Config.informstatus.get(unitTask.getString("progressStatus")));
        } else {
            tvStatus.setText("未读取到该任务进展情况");
        }


        if (!TextUtils.isEmpty(unitTask.getString("verifyProgress"))) {
            tvProgress.setText("当前已完成" + unitTask.getString("verifyProgress") + "%");
        } else {
            tvProgress.setText("未读取到该任务的完成进度");
        }
    }

}
