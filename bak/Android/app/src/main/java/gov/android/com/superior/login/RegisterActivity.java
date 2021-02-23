package gov.android.com.superior.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.sms.SMSSDK;
import cn.jpush.sms.listener.SmscheckListener;
import cn.jpush.sms.listener.SmscodeListener;
import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.user.CreateUserActivity;

public class RegisterActivity extends BaseLoadActivity {

    private EditText et_code;
    private EditText et_phone;
    private TextView tv_code;

    private TimerTask timerTask;
    private Timer timer;
    private int timess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("注册账号");

        et_code = (EditText) findViewById(R.id.et_code);
        et_phone = (EditText)findViewById(R.id.et_phone);
        tv_code = (TextView) findViewById(R.id.tv_codebtn);

        SMSSDK.getInstance().initSdk(this);
        SMSSDK.getInstance().setIntervalTime(60*1000);

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

        hideInputMethod();
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
            removeProgress();
            stopTimer();
            Toast.makeText(RegisterActivity.this, "验证码发送失败，请稍后再试", Toast.LENGTH_LONG).show();
        }
    };

    private void asyncCheckCode() {

        if (checkPhone()) {
            if (checkCode()) {
                showProgress("验证中");
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
            removeProgress();
            Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
        }
    };

    private void asyncRegister() {
        showProgress("注册中");
        OkGo.<Map>post(Config.USER_CREATE).tag(this).params("phone", et_phone.getText().toString()).execute(jsonCallback);
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
            Logger.d(response.body());

            removeProgress();
            Map<String, Object> map = response.body();

            User.getInstance().updateUser(map);

            Intent intent = new Intent(RegisterActivity.this, CreateUserActivity.class);
            intent.putExtra("userId",  Integer.parseInt(map.get("id").toString()));
            startActivity(intent);
        }
    };

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
