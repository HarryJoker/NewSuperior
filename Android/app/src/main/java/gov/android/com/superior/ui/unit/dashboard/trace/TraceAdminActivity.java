package gov.android.com.superior.ui.unit.dashboard.trace;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;

import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class TraceAdminActivity extends BaseToolBarActivity {

    private Map<Integer, Integer> verifyStatus = new HashMap<Integer, Integer>(){
        {
            put(R.id.rb_nomal, 71);
            put(R.id.rb_slow,  72);
            put(R.id.rb_fast,  73);
            put(R.id.rb_done,  91);
        }
    };

    private Map<Integer, String> veifyContents = new HashMap<Integer, String>() {
        {
            put(R.id.rb_nomal, "当前工作序时推进");
            put(R.id.rb_slow, "当前工作进展缓慢");
            put(R.id.rb_fast, "当前工作进展较快");
            put(R.id.rb_done, "当前工作调度已完成");
        }
    };


    private int unitTaskId;

    private RadioGroup rg_types;

//    private EditText etContent;

    private EditText etProgress;

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
        return R.layout.activity_trace_admin;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("审核工作");
    }

    @Override
    protected void onFindViews() {
        rg_types = findViewById(R.id.rg_types);

//        etContent =  findViewById(R.id.et_content);

        etProgress = findViewById(R.id.et_progress);
    }

    private RadioGroup.OnCheckedChangeListener mTypeCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_done) {
                etProgress.setText("100");
            }
        }
    };

    @Override
    public void onInitView() {
        rg_types.setOnCheckedChangeListener(mTypeCheckedChangeListener);
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }

    private boolean checked() {
//        if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
//            Toast.makeText(this, "请填写审核备注", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        if (rg_types.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择任务进展", Toast.LENGTH_SHORT).show();
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
        if (!checked()) return;
        int status = 0;
        if (verifyStatus.containsKey(rg_types.getCheckedRadioButtonId())) {
            status = verifyStatus.get(rg_types.getCheckedRadioButtonId());
        }
        if (status == 0) {
            showWarnTip("请选择正确的进展情况");
        }

        showLoading("请稍后...");
        HttpParams params = new HttpParams();
        params.put("unitTaskId", unitTaskId);
        params.put("content",  veifyContents.get(rg_types.getCheckedRadioButtonId()));
        params.put("userId", User.getInstance().getUserId());
        params.put("status", status);//审核状态：较快，正常，缓慢，完成
        params.put("progress", TextUtils.isEmpty(etProgress.getText().toString()) ? "0" : etProgress.getText().toString());
        params.put("unitId", User.getInstance().getUnitId());
        OkGo.<JSONObject>post(HttpUrl.NEW_VERIFY_TRACE).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_VERIFY_TRACE));
    }



    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        hideLoading();
        showSuccessTip("审核成功");
        setResult(RESULT_OK);
        finish();
    }
}
