package gov.android.com.superior.ui.unit.dashboard.trace;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class TraceAcceptActivity extends BaseToolBarActivity {

    public static final int REQUEST_ACCEPT_TASK = 0X00F1;

    private EditText etLeader;
    private EditText etParter;
    private EditText etDoner;

    private int  unitTaskId = 0;

    @Override
    public void onInitParams() {
        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);
        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_trace_accept;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("申领责任事项");
    }

    @Override
    protected void onFindViews() {
        etDoner = findViewById(R.id.et_doer);
        etLeader = findViewById(R.id.et_leader);
        etParter = findViewById(R.id.et_parter);
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
    }


    private boolean checked() {
        if (etLeader.getEditableText().toString().length() == 0) {
            Toast.makeText(this, "请填写主要责任人", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etParter.getEditableText().toString().length() == 0) {
            Toast.makeText(this, "请填写分管责任人", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etDoner.getEditableText().toString().length() == 0) {
            Toast.makeText(this, "请填写具体责任人", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String makeContent() {
        String content = "主要负责人：";
        content += etLeader.getEditableText().toString();
        content += "\n";

        content += "分管负责人：" + etParter.getEditableText().toString();
        content += "\n";

        content += "具体负责人：" + etDoner.getEditableText().toString();

        return content;
    }

    public void acceptClick(View view) {
        if (checked()) {
            HttpParams params = new HttpParams();
            params.put("unitTaskId", unitTaskId);
            params.put("userId", User.getInstance().getUserId());
            params.put("content", makeContent());
            params.put("progress", "0");
            params.put("status", 1);
            params.put("unitId", User.getInstance().getUnitId());

            params.put("responsibilityUserId", 0);
            params.put("responsibilityUserName", etLeader.getEditableText().toString());
            params.put("partReponsibilityUserId",0);
            params.put("partReponsibilityUserName", etParter.getEditableText().toString());
            params.put("handleUserId", 0);
            params.put("handleUserName", etDoner.getEditableText().toString());
            OkGo.<JSONObject>post(HttpUrl.NEW_ACCEPT_TASK_TRACE).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_ACCEPT_TASK_TRACE));
        }
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        Toast.makeText(TraceAcceptActivity.this, "申领任务成功", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

}
