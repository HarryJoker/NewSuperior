package gov.android.com.superior.ui.unit.person;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.lzy.okgo.OkGo;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class AdviceActivity extends BaseToolBarActivity {

    private EditText et_content;

    @Override
    public void onInitParams() {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_advice;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("意见建议");
    }

    @Override
    protected void onFindViews() {
        et_content = (EditText)findViewById(R.id.et_content);
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

    public void adviceClick(View v) {
        if (TextUtils.isEmpty(et_content.getText().toString())) {
            Toast.makeText(this, "请输入意见建议", Toast.LENGTH_LONG).show();
        } else {
            showLoading("请稍后...");
            OkGo.<JSONObject>post(HttpUrl.ADVICE_CREATE).tag(this).params("userId", User.getInstance().getUserId()).params("content", et_content.getText().toString()).execute(getJsonObjectCallback());
        }
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        showToast("提交成功");
    }

}
