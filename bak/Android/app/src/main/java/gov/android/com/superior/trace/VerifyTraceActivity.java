package gov.android.com.superior.trace;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;

public class VerifyTraceActivity extends BaseLoadActivity {

    private Map<Integer, Integer> status = new HashMap<Integer, Integer>(){
        {
            put(R.id.rb_nomal, 71);
            put(R.id.rb_slow,  72);
            put(R.id.rb_fast,  73);
            put(R.id.rb_back,  74);
            put(R.id.rb_press, 52);
            put(R.id.rb_done,  91);
        }
    };

    private int unitTaskId;

    private RadioGroup rg_types;

    private EditText etContent;

    private EditText etProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_trace);

        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);

        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle("审核报送工作");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rg_types = ((RadioGroup)findViewById(R.id.rg_types));

        rg_types.setOnCheckedChangeListener(mOnCheckedChangeListener);

        etContent = (EditText) findViewById(R.id.et_content);

        etProgress = (EditText) findViewById(R.id.et_progress);
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_nomal || checkedId == R.id.rb_slow || checkedId == R.id.rb_fast || checkedId == R.id.rb_done) {
                findViewById(R.id.progress).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.progress).setVisibility(View.GONE);
            }

            if (checkedId == R.id.rb_done) {
                etProgress.setText("100");
            } else {
                etProgress.setText("");
            }
        }
    };

    private boolean checked() {
        if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
            Toast.makeText(this, "请填写审核备注", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rg_types.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择审核类型", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (findViewById(R.id.progress).getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(etProgress.getEditableText().toString())) {
                Toast.makeText(this, "请填写审核进度", Toast.LENGTH_SHORT).show();
                return false;
            }
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
        params.put("status", status.get(rg_types.getCheckedRadioButtonId()));//已报送
        params.put("progress", TextUtils.isEmpty(etProgress.getText().toString()) ? "0" : etProgress.getText().toString());
        params.put("unitId", User.getInstance().getUnitId());
        OkGo.<JSONObject>post(Config.TRACE_NEW).headers("accept-encoding", "gzip").params(params).tag(this).execute(commitCallback);
    }

    private JsonObjectCallBack<JSONObject> commitCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            removeProgress();
            Toast.makeText(VerifyTraceActivity.this, "审核成功", Toast.LENGTH_SHORT).show();
            finish();
        }
        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            removeProgress();
            Toast.makeText(VerifyTraceActivity.this, "审核失败", Toast.LENGTH_SHORT).show();
        }
    };
}
