package gov.android.com.superior.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.mm.dss.demo.permission.PermissionUtil;
import com.mm.dss.demo.permission.constant.PermissionConstant;
import com.orhanobut.logger.Logger;
import com.vector.update_app.UpdateAppManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.MainActivity;
import gov.android.com.superior.MainDcActivity;
import gov.android.com.superior.MainFxzActivity;
import gov.android.com.superior.MainUnitActivity;
import gov.android.com.superior.MainXzActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.UpdateAppHttpUtil;
import gov.android.com.superior.tools.VersionUtils;
import gov.android.com.superior.trace.NewContentActivity;
import gov.android.com.superior.trace.VerifyContentActivity;
import gov.android.com.superior.trace.VerifyTraceActivity;
import gov.android.com.superior.view.GestureLockLayout;

import static gov.android.com.superior.http.Config.UPDATE_VERSION;

public class GestureLockLoginActivity extends BaseLoadActivity {

//    private TextView tv_phone;

    private TextView tvName;

    private AvatarImageView avatar;

    private GestureLockLayout gestureLockLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_lock_login);

        setTitle("登录");

        gestureLockLayout = (GestureLockLayout) findViewById(R.id.gesutrelocklayout);

        gestureLockLayout.setAnswer(getGestureLock());

        gestureLockLayout.setOnGestureLockViewListener(gestureLockViewListener);

//        tv_phone = (TextView)findViewById(R.id.tv_phone);

        tvName = findViewById(R.id.tv_name);

        avatar = findViewById(R.id.lv_avatar);

        refreshAccount();

        updateVersion();
    }


    private void updateVersion() {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setUpdateUrl(UPDATE_VERSION)
                //设置顶部图片
                .setTopPic(R.mipmap.update_theme)
                //post请求
                .setPost(true)
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                .build()
                .update();
    }

    private void asyncUser() {
        if (User.getInstance().getUserId() > 0 && User.getInstance().getUserPhone().toString().length() == 11) {
            showProgress("登录中...");
            OkGo.<Map>get(Config.USER_LOGIN + "/" + User.getInstance().getUserPhone()).execute(jsonCallback);
        }
    }

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {
        @Override
        public void onSuccess(Response<Map> response) {
            removeProgress();
            User.getInstance().updateUser(response.body());

            startActivity(new Intent(GestureLockLoginActivity.this, MainActivity.class));

            finish();
        }

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            removeProgress();
        }
    };

    private void refreshAccount() {
//        String phone = User.getInstance().get("phone").toString();
//        if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
//            phone = phone.replace(phone.substring(3, 7), "****");
//        }
//        this.tv_phone.setText(phone);

        tvName.setText(User.getInstance().get("name").toString());

        Glide.with(this).load(Config.ATTACHMENT + User.getInstance().get("logo")).into(avatar);
    }

    private Long[] getGestureLock() {
        if (User.getInstance().getUser().containsKey(User.KEY_GESTURE_PASSWORD)) {
            List<Integer> answers = (List<Integer>) User.getInstance().getUser().get(User.KEY_GESTURE_PASSWORD);
            if (answers == null || answers.size() == 0) return null;
            Long[] integers = new Long[answers.size()];//当泛型为Integer时，需要
            integers = answers.toArray(integers);
            return integers;
        }
        return null;
    }

    private GestureLockLayout.OnGestureLockViewListener gestureLockViewListener = new GestureLockLayout.OnGestureLockViewListener() {
        @Override
        public void onBlockSelected(long cId) {
            Logger.d("onBlockSelected------------------>" + cId);
        }

        @Override
        public void onGestureEvent(boolean matched) {
            Logger.d("matched: " + matched);

            if (matched) {
//                Class clazz = null;
//
//                int role = User.getInstance().getUserRole();
//                if (role == 1) {
//                    clazz = MainXzActivity.class;
//                } else if (role == 2) {
//                    clazz = MainFxzActivity.class;
//                } else if (role == 3) {
//                    clazz = MainDcActivity.class;
//                } else if (role == 4) {
//                    clazz = MainUnitActivity.class;
//                }

                asyncUser();

            } else {
                Toast.makeText(GestureLockLoginActivity.this, "密码错误请重试", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onUnmatchedExceedBoundary() {
            Logger.d("onUnmatchedExceedBoundary------------------>");
            Toast.makeText(GestureLockLoginActivity.this, "连续3次错误，请尝试帐号登录", Toast.LENGTH_LONG).show();
        }
    };

    public void loginViewClick(View v) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void fingerViewClick(View v) {
//        startActivity(new Intent(this, Ge));

        Toast.makeText(this, "该功能开发中。。。", Toast.LENGTH_LONG).show();
    }


    private boolean isPermissionsRequest = false;
    @Override
    protected void onResume() {
        super.onResume();
        if(!isPermissionsRequest) {
            requestAllPermissions();
            isPermissionsRequest = true;
        }
    }

    private void requestAllPermissions(){
        PermissionUtil permissionUtil = new PermissionUtil(new PermissionUtil.OnPermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {
                isPermissionsRequest = false;
            }

            @Override
            public void onPermissionSetting(boolean b) {
                isPermissionsRequest = false;
            }
        });
        List<String> permissionList = new ArrayList<>();
        permissionList.addAll(Arrays.asList(PermissionConstant.MICROPHONE));
        permissionList.addAll(Arrays.asList(PermissionConstant.STORAGE));
        permissionList.addAll(Arrays.asList(Manifest.permission.READ_PHONE_STATE));
        permissionUtil.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]));
    }

}
