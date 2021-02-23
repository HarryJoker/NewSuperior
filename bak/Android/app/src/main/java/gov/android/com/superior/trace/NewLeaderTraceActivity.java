package gov.android.com.superior.trace;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;

public class NewLeaderTraceActivity extends BaseLoadActivity {

    private int unitTaskId;

    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_leader_trace);

        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);

        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle("任务事项批示");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etContent = (EditText) findViewById(R.id.et_content);

    }

    private boolean checked() {
        if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
            Toast.makeText(this, "请填写审核备注", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void veriryTraceClick(View v) {
        if (checked()) {
            newVerifyTrace();
        }
    }

    private void newVerifyTrace() {
        showProgress("报送中...");
        HttpParams params = new HttpParams();
        params.put("unitTaskId", unitTaskId);
//            params.put("content", etContent.getText().toString().replace("%", "/a25a/"));
        params.put("content", etContent.getText().toString());
        params.put("userId", User.getInstance().getUserId());
        params.put("status", 21);//已报送
        params.put("progress", "0");
        params.put("unitId", User.getInstance().getUnitId());
        OkGo.<JSONObject>post(Config.TRACE_NEW).headers("accept-encoding", "gzip").params(params).tag(this).execute(commitCallback);
    }

    private JsonObjectCallBack<JSONObject> commitCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            removeProgress();
            Toast.makeText(NewLeaderTraceActivity.this, "批示成功", Toast.LENGTH_SHORT).show();
            finish();
        }
        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            removeProgress();
            Toast.makeText(NewLeaderTraceActivity.this, "批示失败", Toast.LENGTH_SHORT).show();
        }
    };
}
