package gov.android.com.superior.ui.login;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class AcountLoginActivity extends BaseToolBarActivity {


    private EditText etUserAccount;
    private EditText etUserPassword;

    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("账号登录");
    }

    @Override
    protected void onBarArrow(ImageView barArrow) {
        barArrow.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_acount_login;
    }

    @Override
    protected void onFindViews() {
        etUserAccount = (EditText) findViewById(R.id.et_user_account);
        etUserPassword = (EditText) findViewById(R.id.et_user_password);
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

    public void loginViewClick(View v) {
        if (etUserAccount.getEditableText().toString().length() == 0) {
            showToast("请输入您的登录账号");
            return;
        }
        if (etUserPassword.getEditableText().toString().length() == 0) {
            showToast("请输入您的登录密码");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("account", etUserAccount.getEditableText().toString());
        params.put("password", etUserPassword.getEditableText().toString());
        OkGo.<JSONObject>post(HttpUrl.USER_LOGIN_BY_ACCOUNT).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.USER_LOGIN_BY_ACCOUNT));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        User.getInstance().updateUser(data);
        showToast("登录成功");
        Intent intent = new Intent(AcountLoginActivity.this, PatterLockSettingActivity.class);
        startActivity(intent);
        finish();
    }

    public void registerViewClick(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void gestureLockViewClick(View v) {

    }

    public void smsLoginClick(View v) {
        startActivity(new Intent(this, SmsLoginActivity.class));
        finish();
    }

}
