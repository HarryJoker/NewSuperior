//package gov.android.com.superior.ui.login;
//
//import android.Manifest;
//import android.content.Intent;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSONObject;
//import com.bumptech.glide.Glide;
//import com.first.orient.base.activity.BaseToolBarActivity;
//import com.lzy.okgo.OkGo;
//import com.makeramen.roundedimageview.RoundedImageView;
//import com.orhanobut.logger.Logger;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import gov.android.com.superior.MainActivity;
//import gov.android.com.superior.R;
//import gov.android.com.superior.entity.User;
//import gov.android.com.superior.http.HttpUrl;
//import gov.android.com.superior.permission.PermissionUtil;
//import gov.android.com.superior.permission.constant.PermissionConstant;
//import gov.android.com.superior.view.GestureLockLayout;
//
//public class GestureLockLoginActivity extends BaseToolBarActivity {
//
//    private TextView tvName;
//
//    private RoundedImageView avatar;
//
//    private GestureLockLayout gestureLockLayout;
//
//    @Override
//    public void onInitParams() {
//
//    }
//
//    @Override
//    public int getLayoutRes() {
//        return R.layout.activity_gesture_lock_login;
//    }
//
//    @Override
//    protected void onFindViews() {
//        gestureLockLayout = (GestureLockLayout) findViewById(R.id.gesutrelocklayout);
//
//        gestureLockLayout.setAnswer(getGestureLock());
//
//        gestureLockLayout.setOnGestureLockViewListener(gestureLockViewListener);
//
////        tv_phone = (TextView)findViewById(R.id.tv_phone);
//
//        tvName = findViewById(R.id.tv_name);
//
//        avatar = findViewById(R.id.lv_avatar);
//    }
//
//    @Override
//    public void onInitView() {
//
//    }
//
//    @Override
//    protected void onBarTitle(TextView barTitle) {
//        barTitle.setText("手势登录");
//    }
//
//    @Override
//    public void onInitPresenter() {
//
//    }
//
//    @Override
//    public void onBusiness() {
//        refreshAccount();
////        updateVersion();
//    }
//
//
////    private void updateVersion() {
////        new UpdateAppManager
////                .Builder()
////                //当前Activity
////                .setActivity(this)
////                //更新地址
////                .setUpdateUrl(UPDATE_VERSION)
////                //设置顶部图片
////                .setTopPic(R.mipmap.update_theme)
////                //post请求
////                .setPost(true)
////                //实现httpManager接口的对象
////                .setHttpManager(new UpdateAppHttpUtil())
////                .build()
////                .update();
////    }
//
//    private void asyncUser() {
//        if (User.getInstance().getUserId() > 0 && User.getInstance().getUserPhone().toString().length() == 11) {
//            showLoading("请稍后...");
//            OkGo.<JSONObject>get(HttpUrl.USER_LOGIN + "/" + User.getInstance().getUserPhone()).execute(getJsonObjectCallback(HttpUrl.USER_LOGIN));
//        }
//    }
//
//    @Override
//    protected void onJsonObjectCallBack(String action, JSONObject data) {
//        super.onJsonObjectCallBack(action, data);
//        User.getInstance().updateUser(data);
//
//        startActivity(new Intent(GestureLockLoginActivity.this, MainActivity.class));
//
//        finish();
//    }
//
//    private void refreshAccount() {
////        String phone = User.getInstance().get("phone").toString();
////        if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
////            phone = phone.replace(phone.substring(3, 7), "****");
////        }
////        this.tv_phone.setText(phone);
//
//        tvName.setText(User.getInstance().get("name").toString());
//
//        Glide.with(this).load(HttpUrl.ATTACHMENT + User.getInstance().get("logo")).into(avatar);
//    }
//
//    private Long[] getGestureLock() {
//        if (User.getInstance().getUser().containsKey(User.KEY_GESTURE_PASSWORD)) {
//            List<Integer> answers = (List<Integer>) User.getInstance().getUser().get(User.KEY_GESTURE_PASSWORD);
//            if (answers == null || answers.size() == 0) return null;
//            Long[] integers = new Long[answers.size()];//当泛型为Integer时，需要
//            for (int n = 0; n <answers.size(); n++) {
//                integers[n] = Long.parseLong(String.valueOf(answers.get(n)));
//            }
//            System.out.println(Arrays.toString(integers));
//            return integers;
//        }
//        return null;
//    }
//
//    private GestureLockLayout.OnGestureLockViewListener gestureLockViewListener = new GestureLockLayout.OnGestureLockViewListener() {
//        @Override
//        public void onBlockSelected(long cId) {
//            Logger.d("onBlockSelected------------------>" + cId);
//        }
//
//        @Override
//        public void onGestureEvent(boolean matched) {
//            Logger.d("matched: " + matched);
//
//            if (matched) {
////                Class clazz = null;
////
////                int role = User.getInstance().getUserRole();
////                if (role == 1) {
////                    clazz = MainXzActivity.class;
////                } else if (role == 2) {
////                    clazz = MainFxzActivity.class;
////                } else if (role == 3) {
////                    clazz = MainDcActivity.class;
////                } else if (role == 4) {
////                    clazz = MainUnitActivity.class;
////                }
//
//                asyncUser();
//
//            } else {
//                Toast.makeText(GestureLockLoginActivity.this, "密码错误请重试", Toast.LENGTH_LONG).show();
//            }
//        }
//
//        @Override
//        public void onUnmatchedExceedBoundary() {
//            Logger.d("onUnmatchedExceedBoundary------------------>");
//            Toast.makeText(GestureLockLoginActivity.this, "连续3次错误，请尝试帐号登录", Toast.LENGTH_LONG).show();
//        }
//    };
//
//    public void loginViewClick(View v) {
//        startActivity(new Intent(this, SmsLoginActivity.class));
//        finish();
//    }
//
//    public void fingerViewClick(View v) {
////        startActivity(new Intent(this, Ge));
//
//        Toast.makeText(this, "该功能开发中。。。", Toast.LENGTH_LONG).show();
//    }
//
//
//    private boolean isPermissionsRequest = false;
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(!isPermissionsRequest) {
//            requestAllPermissions();
//            isPermissionsRequest = true;
//        }
//    }
//
//    private void requestAllPermissions(){
//        PermissionUtil permissionUtil = new PermissionUtil(new PermissionUtil.OnPermissionRequestListener() {
//            @Override
//            public void onPermissionGranted() {
//
//            }
//
//            @Override
//            public void onPermissionDenied() {
//                isPermissionsRequest = false;
//            }
//
//            @Override
//            public void onPermissionSetting(boolean b) {
//                isPermissionsRequest = false;
//            }
//        });
//        List<String> permissionList = new ArrayList<>();
//        permissionList.addAll(Arrays.asList(PermissionConstant.MICROPHONE));
//        permissionList.addAll(Arrays.asList(PermissionConstant.STORAGE));
//        permissionList.addAll(Arrays.asList(Manifest.permission.READ_PHONE_STATE));
//        permissionUtil.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]));
//    }
//
//}
