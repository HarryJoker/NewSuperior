package gov.android.com.superior.ui.unit.dashboard.trace;

import android.text.TextUtils;
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

public class TraceRushActivity extends BaseToolBarActivity {


    private int unitTaskId;

    private EditText etContent;

    @Override
    public void onInitParams() {
        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);

        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_trace_rush;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("任务催报");
    }

    @Override
    protected void onFindViews() {
        etContent = findViewById(R.id.et_content);
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
        if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
            Toast.makeText(this, "请填写催报描述", Toast.LENGTH_SHORT).show();
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
        showLoading("请稍后...");
        HttpParams params = new HttpParams();
        params.put("unitTaskId", unitTaskId);
        params.put("content", etContent.getText().toString());
        params.put("userId", User.getInstance().getUserId());
        params.put("status", 52);//已报送
        params.put("progress", 0);
        params.put("unitId", User.getInstance().getUnitId());
        OkGo.<JSONObject>post(HttpUrl.NEW_RUSH_TRACE).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_RUSH_TRACE));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        Toast.makeText(TraceRushActivity.this, "催报成功", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
