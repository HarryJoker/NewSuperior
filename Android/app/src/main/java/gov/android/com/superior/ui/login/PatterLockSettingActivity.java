package gov.android.com.superior.ui.login;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
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

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class PatterLockSettingActivity extends BaseToolBarActivity {

    private TextView tvName;

    private RoundedImageView avatar;

    private TextView tvMsg;

    private PatternIndicatorView patternIndicatorView;

    private PatternLockerView patternLockerView;

    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarArrow(ImageView barArrow) {
        barArrow.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("设置手势密码");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_setting_lock;
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
                boolean isValidated = validateForSetting(list);
                patternIndicatorView.updateState(list, !isValidated);
                patternLockerView.updateStatus(!isValidated);
            }

            @Override
            public void onClear(@NotNull PatternLockerView patternLockerView) {
                JokerLog.d("onClear ----------------------------");
            }
        });
    }

    private static final int MAX_SIZE = 5;

    private String patterLockPwd;

    private boolean validateForSetting(List<Integer> hitIndexList) {
        if (hitIndexList == null || hitIndexList.size() < MAX_SIZE) {
            updateMsg(true, "手势密码太短");
            return false;
        }
        //1. draw first time
        if (TextUtils.isEmpty(patterLockPwd)) {
            patterLockPwd =  hitIndexList.toString();
            updateMsg(false, "请再次绘制解锁图案");
            return true;
        }
        //2. draw second times
        String confimLockPwd = hitIndexList.toString();
        if (patterLockPwd.equals(confimLockPwd)) {
            User.getInstance().update(User.KEY_GESTURE_PASSWORD, confimLockPwd);
            updateMsg(false, "手势解锁图案设置成功！");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(PatterLockSettingActivity.this, PatterLockLoginActivity.class));
                    finish();
                }
            }, 700);
            return true;
        } else {
            patterLockPwd = null;
            updateMsg(true, "与上次绘制密码不一致，请重新绘制");
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

    private void refreshAccount() {
        tvName.setText(User.getInstance().get("name").toString());

        Glide.with(this).load(HttpUrl.ATTACHMENT + User.getInstance().get("logo")).placeholder(R.mipmap.ic_avatar).into(avatar);
    }
}
