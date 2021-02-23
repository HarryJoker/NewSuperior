package gov.android.com.superior.home.unit.task;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.Map;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;

public class ReplyTraceActivity extends BaseLoadActivity {

    private int type;

    private long taskId;

    private TextView tv_key;

    private EditText et_content;

    private TextView tv_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_trace);

        type = getIntent().getIntExtra("type", 0);
        taskId = getIntent().getLongExtra("taskId", 0);

        setTitle(type == 2 ? "任务催报" : "领导批示");

        tv_key = (TextView) findViewById(R.id.tv_key);

        et_content = (EditText) findViewById(R.id.et_content);

        tv_create = (TextView) findViewById(R.id.tv_create_click);

        refresh();

    }

    private void refresh() {
        tv_key.setText(type == 2 ? "催报内容" : "批示内容");
        et_content.setHint(type == 2 ? "请填写催报内容" : "请填写批示内容");
        tv_create.setText(type == 2 ? "催报任务" : "批示任务");
    }

    private boolean check() {
        if (TextUtils.isEmpty(et_content.getText().toString())) {
            Toast.makeText(this, type == 2 ? "请填写催报内容" : "请填写批示内容", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public void newTraceClick(View v) {

        hideInputMethod();

        if (check()) {
            showProgress(type == 2 ? "催报中" : "批示中");

            Bundle taskBundle = getIntent().getBundleExtra("task");

            HttpParams params = new HttpParams();
            params.put("taskId", taskId);
            params.put("userId", User.getInstance().getUserId());
            params.put("content", et_content.getText().toString());
            params.put("unitId", User.getInstance().get("unitId").toString());
            params.put("unitName", ((Map)(User.getInstance().get("unit"))).get("name").toString());
            params.put("category", taskBundle.get("category").toString());
            params.put("address", "");
            params.put("location", "");
//            0：正常的上报工作，1：系统自动生成督促，2：督查主动催报，4：领导批示
            params.put("type", type);

            OkGo.<Map>post(Config.TRACE_CREATE).params(params).tag(this).execute(jsonCallback);
        }
    }

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            removeProgress();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<Map> response) {
            removeProgress();
            Toast.makeText(ReplyTraceActivity.this, type == 2 ? "催报成功" : "批示成功", Toast.LENGTH_LONG).show();
            tv_create.setBackgroundColor(Color.parseColor("#d3d3d3"));
            tv_create.setEnabled(false);

            setResult(RESULT_OK);
            finish();
        }
    };
}
