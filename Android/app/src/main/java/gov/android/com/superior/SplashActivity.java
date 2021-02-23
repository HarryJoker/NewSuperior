package gov.android.com.superior;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import gov.android.com.superior.entity.User;
import gov.android.com.superior.ui.login.AcountLoginActivity;
import gov.android.com.superior.ui.login.PatterLockLoginActivity;
import gov.android.com.superior.ui.login.PatterLockSettingActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //延时跳转到主页面，splash用来做引导页
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> user = User.getInstance().getUser();
                if (User.getInstance().getUserId() > 0 && user.size() > 0) {
                    //有手势密码
                    if (user.containsKey(User.KEY_GESTURE_PASSWORD) ) {
                        startActivity(new Intent(SplashActivity.this, PatterLockLoginActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, PatterLockSettingActivity.class));
                    }
                } else {
                        Intent intent = new Intent(SplashActivity.this, AcountLoginActivity.class);
                        startActivity(intent);
                }
                finish();
            }
        },1000  * 2);
    }
}
