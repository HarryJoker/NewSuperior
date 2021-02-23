package gov.android.com.superior.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.view.GestureLockLayout;

public class GestureLockActivity extends BaseActivity {

    public static final String NAME = "Superior";

    private GestureLockLayout gestureLockLayout;

    private int current = 1;

    private Long[] firstLock;

    private Long[] secondLock;

//    private TextView tv_phone;

    private TextView tvName;

    private AvatarImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_lock);

        gestureLockLayout = (GestureLockLayout) findViewById(R.id.gesutrelocklayout);

        gestureLockLayout.setmOnGestureLockAnswer(gestureLockAnswer);

//        tv_phone = (TextView)findViewById(R.id.tv_phone);

        setTitle("设置手势密码");

        tvName = findViewById(R.id.tv_name);

        avatar = findViewById(R.id.lv_avatar);

        refreshAccount();
    }

    private void refreshAccount() {
//        String phone = User.getInstance().get("phone").toString();
//        if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
//            phone = phone.replace(phone.substring(3, 7), "****");
//        }
//        this.tv_phone.setText(phone);

        tvName.setText(User.getInstance().get("name").toString());

        Glide.with(this).load(Config.ATTACHMENT + User.getInstance().get("logo")).into(avatar);
    }

    /**
     * 检查用户绘制的手势是否正确
     * @return
     */
    private boolean checkAnswer()
    {
        if (firstLock.length != secondLock.length)
            return false;

        for (int i = 0; i < firstLock.length; i++)
        {
            if (firstLock[i] != secondLock[i])
                return false;
        }

        return true;
    }


    private void reset() {
        current = 1;
        firstLock = null;
        secondLock = null;
    }

    private void saveGestureLock() {
        User.getInstance().update(User.KEY_GESTURE_PASSWORD, Arrays.asList(firstLock));
    }


    private GestureLockLayout.OnGestureLockAnswer gestureLockAnswer = new GestureLockLayout.OnGestureLockAnswer() {
        @Override
        public void onLockAnswer(Long[] answers) {
            Logger.d("onLockAnswer -----------> " + answers);

            if (current == 1) {
                firstLock = answers;
            } else if (current == 2) {
                secondLock = answers;
            }

            if (current == 1) {
                current++;
                Toast.makeText(GestureLockActivity.this, "请再确认一次手势密码", Toast.LENGTH_SHORT).show();
            } else if (current == 2) {
                if (!checkAnswer()) {

                    Logger.d("check : no");
                    reset();
                    Toast.makeText(GestureLockActivity.this, "两次手势不一致，请重新设置", Toast.LENGTH_SHORT).show();
                } else {

                    Logger.d("check : ok");
                    saveGestureLock();
                    //.....;

                    startActivity(new Intent(GestureLockActivity.this, GestureLockLoginActivity.class));
                    finish();
                }
            }

        }
    };
}
