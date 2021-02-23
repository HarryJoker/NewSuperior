package gov.android.com.superior;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.mm.dss.demo.LoadingActivity;

import java.util.Map;

import gov.android.com.superior.entity.User;
import gov.android.com.superior.login.GestureLockLoginActivity;
import gov.android.com.superior.login.LoginActivity;
import gov.android.com.superior.user.CreateUserActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN, WindowManager.LayoutParams. FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //延时跳转到主页面，splash用来做引导页
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> user = User.getInstance().getUser();
                //有手势密码
                if (user.containsKey(User.KEY_GESTURE_PASSWORD)) {
                    Intent intent = new Intent(SplashActivity.this, GestureLockLoginActivity.class);
                    startActivity(intent);
                }
                //有id
                else if (User.getInstance().getUserId() > 0) {
                    //有信息
                    if (user.containsKey("name")) {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else if (!user.containsKey("name")) {
                        Intent intent = new Intent(SplashActivity.this, CreateUserActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }

                //没有账号，注册
                else if (user.size() == 0) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                finish();

            }
        },1000  * 2);

    }
}
