package gov.android.com.superior;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.vector.update_app.UpdateAppManager;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.search.SearchActivity;
import me.leolin.shortcutbadger.ShortcutBadger;

public class BaseMainActivity extends BaseActivity {

    private static final String UPDATE_TAG = "UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        asyncVersion();

        checkAccount();

        ShortcutBadger.removeCount(this); //for 1.1.4+
    }

    private void asyncVersion() {
        OkGo.<Map>get(Config.VERSION).tag(UPDATE_TAG).execute(jsonCallback);
    }

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {
        @Override
        public void onSuccess(Response<Map> response) {
            Map data = response.body();
            if (data != null && data.containsKey("version"))
            if (Integer.parseInt(data.get("version").toString()) > SuperiorApplicaiton.VERSION) {
                showUpdateDialog();
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        OkGo.getInstance().cancelTag(UPDATE_TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(UPDATE_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            Logger.d("search action ..........");

            startActivity(new Intent(this, SearchActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    protected void validateHeader(NavigationView navigationView) {
        if (navigationView == null) return;
        View headerView = navigationView.getHeaderView(0);
        if (headerView == null) return;

        Map<String, Object> unit = (Map)User.getInstance().get("unit");
        String name = unit.get("name").toString() + "（" + User.getInstance().get("name") + "）";
        ((TextView) headerView.findViewById(R.id.tv_name)).setText(name);
        ((TextView) headerView.findViewById(R.id.tv_content)).setText(User.getInstance().get("duty").toString());

        AvatarImageView avatarImageView = headerView.findViewById(R.id.iv_logo);

        String logo = unit.get("logo").toString();

        if (TextUtils.isEmpty(logo)) {
            String unitName = unit.get("name").toString();
            avatarImageView.setTextAndColor(unitName.length() > 0 ? unitName.substring(0, 1) : "无", Color.WHITE);
        } else {
            Glide.with(this)
                    .load(Config.ATTACHMENT + unit.get("logo"))
                    .centerCrop()
                    .crossFade()
                    .into(avatarImageView);
        }
    }


    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请更新版本");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showProgressDialog();

            }
        });

        builder.create().show();
    }

    private void asyncDownApk() {
        OkGo.<File>get(Config.ATTACHMENT + "app.apk").tag(this).execute(fileCallback);
    }

    private FileCallback fileCallback = new FileCallback() {

        @Override
        public void onSuccess(Response<File> response) {
            Logger.d(response.body());

            progressDialog.dismiss();

            //判读版本是否在7.0以上
            if(Build.VERSION.SDK_INT >= 24) {
                Uri apkUri = FileProvider.getUriForFile(BaseMainActivity.this, "gov.android.com.superior.fileprovider", response.body());//在AndroidManifest中的android:authorities值
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                startActivity(install);
            } else {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(Uri.fromFile(response.body()), "application/vnd.android.package-archive");
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(install);
            }
        }

        @Override
        public void downloadProgress(Progress progress) {
            super.downloadProgress(progress);

            if (progressDialog != null) {
                progressDialog.setProgress((int) (progress.fraction * 100));

            }
        }
    };

    private ProgressDialog progressDialog;

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("下载最新版本中");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setOnKeyListener(keyListener);
        progressDialog.show();
        asyncDownApk();
    }

    private DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
          return keyCode==KeyEvent.KEYCODE_BACK;
        }
    };

    protected void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认退出吗？");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void checkAccount() {
        int verify = User.getInstance().getVerify();
        if (verify != 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(verify == 0 ? "您帐号审核未通过，请与管理员联系！" : "您的帐号被冻结，请与管理员联系！");

            builder.setTitle("您已下线");

            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keycode, KeyEvent keyEvent) {
                    if (keycode == KeyEvent.KEYCODE_BACK) {
                        dialogInterface.dismiss();
                        finish();
                    }
                    return false;
                }
            });

            builder.create().show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showExitDialog();
        }
        return false;
    }

    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
}
