package gov.android.com.superior.ui.login;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.first.orient.base.utils.JokerLog;
import com.github.ihsg.patternlocker.OnPatternChangeListener;
import com.github.ihsg.patternlocker.PatternIndicatorView;
import com.github.ihsg.patternlocker.PatternLockerView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import gov.android.com.superior.MainActivity;
import gov.android.com.superior.MainUserActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class PatterLockLoginActivity extends BaseToolBarActivity {

    private TextView tvName;

    private RoundedImageView avatar;

    private TextView tvMsg;

    private PatternIndicatorView patternIndicatorView;

    private PatternLockerView patternLockerView;

    private static final int MAX_SIZE = 5;

    private String patterLockPwd;


    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("手势密码登录");
    }

    @Override
    protected void onBarArrow(ImageView barArrow) {
        barArrow.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_patter_lock_login;
    }

    @Override
    protected void onFindViews() {
        tvName = findViewById(R.id.tv_name);

        avatar = findViewById(R.id.lv_avatar);

        tvMsg = findViewById(R.id.textMsg);

        patternLockerView = findViewById(R.id.patternLockerView);

        patternIndicatorView = findViewById(R.id.patternIndicatorView);

        refreshAccount();
    }

    private void refreshAccount() {
        tvName.setText(User.getInstance().get("name").toString());

        Glide.with(this).load(HttpUrl.ATTACHMENT + User.getInstance().get("logo")).placeholder(R.mipmap.ic_avatar).into(avatar);
    }

    @Override
    public void onInitView() {
        patternLockerView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(@NotNull PatternLockerView patternLockerView) {
                JokerLog.d("onStart............");
                tvMsg.setText("");
            }

            @Override
            public void onChange(@NotNull PatternLockerView patternLockerView, @NotNull List<Integer> list) {
                JokerLog.d("onChange:" + Arrays.deepToString(list.toArray()));
            }

            @Override
            public void onComplete(@NotNull PatternLockerView patternLockerView, @NotNull List<Integer> list) {
                JokerLog.d("onComplete:" + Arrays.deepToString(list.toArray()));
                boolean isValidated = validateForChecking(list);
                patternIndicatorView.updateState(list, !isValidated);
                patternLockerView.updateStatus(!isValidated);
            }

            @Override
            public void onClear(@NotNull PatternLockerView patternLockerView) {
                JokerLog.d("onClear ----------------------------");
            }
        });
    }
    private int times;
    private int MAX_TIME = 5;
    public boolean validateForChecking(List<Integer> hitIndexList) {
        times++;
        String storePwd = User.getInstance().get(User.KEY_GESTURE_PASSWORD).toString();
        String lockPwd =  null == hitIndexList ? "" : hitIndexList.toString();
        if (storePwd.equals(lockPwd)) {
            updateMsg(false, "登录成功");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(PatterLockLoginActivity.this, User.getInstance().getUserRole() == 0 ? MainUserActivity.class : MainActivity.class));
                    finish();
                }
            }, 700);
            return true;
        } else {
            updateMsg(true, "密码错误，还剩" + (MAX_TIME - times)  + "次机会");
            return false;
        }
    }

    private void updateMsg(boolean isError, String msg) {
        tvMsg.setText(msg);
        tvMsg.setTextColor(getResources().getColor(isError ? R.color.color_red : R.color.colorPrimaryDark));
    }
    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }

    public void acountLoginClick(View v) {
        startActivity(new Intent(this, AcountLoginActivity.class));
        finish();
    }

    public void smsLoginClick(View v) {
        startActivity(new Intent(this, SmsLoginActivity.class));
        finish();
    }

}
