package gov.android.com.superior.ui.login;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.lzy.okgo.OkGo;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.sms.SMSSDK;
import cn.jpush.sms.listener.SmscheckListener;
import cn.jpush.sms.listener.SmscodeListener;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class RegisterActivity extends BaseToolBarActivity {

    private EditText et_code;
    private EditText et_phone;
    private TextView tv_code;

    private TimerTask timerTask;
    private Timer timer;
    private int timess;

    @Override
    public void onInitParams() {
        SMSSDK.getInstance().initSdk(this);
        SMSSDK.getInstance().setIntervalTime(60*1000);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_register;
    }

    @Override
    protected void onBarArrow(ImageView barArrow) {
        barArrow.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onFindViews() {
        et_code = (EditText) findViewById(R.id.et_code);
        et_phone = (EditText)findViewById(R.id.et_phone);
        tv_code = (TextView) findViewById(R.id.tv_codebtn);
    }

    @Override
    public void onInitView() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("注册账号");
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }

    private boolean checkPhone() {
        String phone = et_phone.getText().toString();
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean checkCode() {
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void registerViewClick(View v) {

        hideInput();
//        asyncCheckCode();
        asyncRegister();
    }

    public void codeViewClick(View v) {
        if (checkPhone()) {
            v.setEnabled(false);
            startTimer();
            SMSSDK.getInstance().getSmsCodeAsyn(et_phone.getText().toString(), "1", smscodeListener);
        }
    }

    private SmscodeListener smscodeListener = new SmscodeListener() {
        @Override
        public void getCodeSuccess(String s) {
            Toast.makeText(RegisterActivity.this, "验证码已成功发送到您的手机", Toast.LENGTH_LONG).show();
        }

        @Override
        public void getCodeFail(int i, String s) {
            hideLoading();
            stopTimer();
            Toast.makeText(RegisterActivity.this, "验证码发送失败，请稍后再试", Toast.LENGTH_LONG).show();
        }
    };

    private void asyncCheckCode() {

        if (checkPhone()) {
            if (checkCode()) {
                showLoading("验证中...");
                SMSSDK.getInstance().checkSmsCodeAsyn(et_phone.getText().toString(), et_code.getText().toString(), smscheckListener);
            }
        }
    }

    private SmscheckListener smscheckListener = new SmscheckListener() {
        @Override
        public void checkCodeSuccess(String s) {
            //验证码验证成功，进行注册。

            asyncRegister();

        }

        @Override
        public void checkCodeFail(int i, String s) {
            hideLoading();
            Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
        }
    };

    private void asyncRegister() {
        showLoading("请稍后...");
        OkGo.<JSONObject>post(HttpUrl.USER_CREATE).tag(this).params("phone", et_phone.getText().toString()).execute(getJsonObjectCallback(HttpUrl.USER_CREATE));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        User.getInstance().updateUser(data);

        Intent intent = new Intent(RegisterActivity.this, PatterLockSettingActivity.class);
//        intent.putExtra("userId",  User.getInstance().getUserId());
        startActivity(intent);
        finish();
    }

    private void startTimer(){
        timess = (int) (SMSSDK.getInstance().getIntervalTime()/1000);
        tv_code.setText(timess+"秒");
        if(timerTask==null){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timess--;
                            if(timess<=0){
                                stopTimer();
                                return;
                            }
                            tv_code.setText(timess+"秒");
                        }
                    });
                }
            };
        }
        if(timer==null){
            timer = new Timer();
        }
        timer.schedule(timerTask, 1000, 1000);
    }
    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        if(timerTask!=null){
            timerTask.cancel();
            timerTask=null;
        }
        tv_code.setText("重新获取");
        tv_code.setClickable(true);
    }

}
