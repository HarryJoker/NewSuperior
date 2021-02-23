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
import gov.android.com.superior.MainActivity;
import gov.android.com.superior.MainDcActivity;
import gov.android.com.superior.MainFxzActivity;
import gov.android.com.superior.MainUnitActivity;
import gov.android.com.superior.MainXzActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;

public class LoginActivity extends BaseLoadActivity {

    private EditText et_code;
    private EditText et_phone;
    private TextView tv_code;

    private TimerTask timerTask;
    private Timer timer;
    private int timess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("登录");

        SMSSDK.getInstance().initSdk(this);
        SMSSDK.getInstance().setIntervalTime(60*1000);

        et_phone = ((EditText)findViewById(R.id.et_phone));
        et_phone.setText(User.getInstance().get("phone").toString());

        et_code = (EditText)findViewById(R.id.et_code);
        tv_code = (TextView)findViewById(R.id.tv_codebtn);

        refreshView();
    }

    private void refreshView() {

//        boolean isVeriry = User.getInstance().isVerify();
        boolean hasGesture = User.getInstance().getUser().containsKey(User.KEY_GESTURE_PASSWORD);
//            findViewById(R.id.tv_tip).setVisibility(isVeriry ? View.GONE : View.VISIBLE);
        findViewById(R.id.tv_split).setVisibility(hasGesture ? View.VISIBLE : View.GONE);
        findViewById(R.id.tv_gesturelogin).setVisibility(hasGesture ? View.VISIBLE : View.GONE);
//        findViewById(R.id.tv_codebtn).setEnabled(isVeriry);
//        findViewById(R.id.tv_register).setEnabled(isVeriry);
    }

    public void registerViewClick(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void gestureLockViewClick(View v) {
        startActivity(new Intent(this, GestureLockLoginActivity.class));
        finish();
    }

    public void loginViewClick(View v) {
        hideInputMethod();
//        asyncCheckCode();

        asyncLogin();
    }

    public void codeViewClick(View v) {
        if (checkPhone()) {
            v.setEnabled(false);
            startTimer();
            SMSSDK.getInstance().getSmsCodeAsyn(et_phone.getText().toString(), "1", smscodeListener);
        }
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

    private SmscodeListener smscodeListener = new SmscodeListener() {
        @Override
        public void getCodeSuccess(String s) {
            Toast.makeText(LoginActivity.this, "验证码已成功发送到您的手机", Toast.LENGTH_LONG).show();
        }

        @Override
        public void getCodeFail(int i, String s) {
            Logger.d(s);
            stopTimer();
            removeProgress();
            Toast.makeText(LoginActivity.this, "验证码发送失败，请稍后再试", Toast.LENGTH_LONG).show();
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

            asyncLogin();

        }

        @Override
        public void checkCodeFail(int i, String s) {
            removeProgress();
            Toast.makeText(LoginActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
        }
    };

    private void asyncLogin() {
        if (checkPhone()) {
            //checkcode
            showProgress("登录中");
            OkGo.<Map>post(Config.USER_LOGIN + "/" + et_phone.getText().toString()).tag(this).execute(jsonCallback);
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
            Logger.d(response.body());

            Map<String, Object> map = response.body();

            User.getInstance().updateUser(map);

            SuperiorApplicaiton.getContext().asyncSetAlisAndTag();

            if (User.getInstance().getUser().containsKey(User.KEY_GESTURE_PASSWORD)) {

                Class clazz = null;

                int role = User.getInstance().getUserRole();
                if (role == 1) {
                    clazz = MainXzActivity.class;
                } else if (role == 2) {
                    clazz = MainFxzActivity.class;
                } else if (role == 3) {
                    clazz = MainDcActivity.class;
                } else if (role == 4) {
                    clazz = MainUnitActivity.class;
                }

//                if (clazz != null) startActivity(new Intent(LoginActivity.this, clazz));


                if (clazz != null) startActivity(new Intent(LoginActivity.this, MainActivity.class));

            } else {
                startActivity(new Intent(LoginActivity.this, GestureLockActivity.class));
            }
            removeProgress();
            finish();
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
