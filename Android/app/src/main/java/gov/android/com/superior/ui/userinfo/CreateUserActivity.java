package gov.android.com.superior.ui.userinfo;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class CreateUserActivity extends BaseToolBarActivity {

    private RoundedImageView ivAvatar;
    private EditText et_name;
    private RadioGroup rg_sex;
    private EditText et_age;
    private EditText et_sign;

    private View layout_unit;

    private int userId;

    private int type = 0;

    private File mLogoFile;

    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("个人信息");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_user;
    }

    @Override
    protected void onFindViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        et_name = (EditText) findViewById(R.id.et_name);
        rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
        et_age = (EditText) findViewById(R.id.et_age);
        et_sign = (EditText) findViewById(R.id.et_sign);
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }


    public void avatarClick(View v) {
        mLogoFile = null;
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_default_style)
                .isAndroidQTransform(Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
                .isCompress(false)
                .maxSelectNum(1)
                .isEnableCrop(false)
                .isPreviewImage(false)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        if (result == null || result.size() == 0) return;
                        if (result.size() == 1) {
                            String path = "";
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                path = result.get(0).getAndroidQToPath();
                            } else {
                                path = result.get(0).getPath();
                            }
                            if (TextUtils.isEmpty(path) || TextUtils.isEmpty(result.get(0).getFileName())) {
                                showToast("未读取到图片信息");
                                return;
                            }
                            mLogoFile = new File(path);
                            refreshUser();
                        } else {
                            showToast("获取图片失败");
                        }
                    }

                    @Override
                    public void onCancel() {
                        // 取消
                    }
                });

    }

    @Override
    public void onBusiness() {
        refreshUser();
    }

    private void refreshUser() {
        if (mLogoFile != null) {
            Glide.with(this).load(mLogoFile).into(ivAvatar);
        }else if (TextUtils.isEmpty(User.getUser().getString("logo"))) {
            ivAvatar.setImageResource(R.mipmap.ic_avatar);
        } else {
            Glide.with(this).load(HttpUrl.makeAttachmentUrl(User.getUser().getString("logo"))).placeholder(R.mipmap.ic_avatar).into(ivAvatar);
        }
        et_name.setText(User.getUser().getString("name"));
        et_sign.setText(User.getUser().getString("sign"));
        et_name.setText(User.getInstance().get("name").toString());

        if (User.getUser().getIntValue("sex") == 1) {
            ((RadioButton) findViewById(R.id.rb_man)).setChecked(true);
        }
        if (User.getUser().getIntValue("sex") == 2){
            ((RadioButton) findViewById(R.id.rb_women)).setChecked(true);
        }
    }

    public void updateUserClick(View v) {
        if (checkParam()) {
            asyncUpdate();
        }
    }

    private boolean checkParam() {

        if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_LONG).show();
            return false;
        }

        if (rg_sex.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(et_age.getText().toString().trim())) {
            Toast.makeText(this, "请输入年龄", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void asyncUpdate() {
        showLoading("请稍后...");
        HttpParams params = new HttpParams();
        params.put("name", et_name.getText().toString());
        params.put("sex", rg_sex.getCheckedRadioButtonId() == R.id.rb_man ? 1 : 2);
        params.put("age", et_age.getText().toString());
        params.put("sign", et_sign.getEditableText().toString());
        if (mLogoFile != null) {
            params.put("logo", mLogoFile);
        }
        OkGo.<JSONObject>post(HttpUrl.USER_UPDATE + "/" + User.getInstance().getUserId()).params(params).isMultipart(true).tag(this).execute(getJsonObjectCallback(HttpUrl.USER_UPDATE));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        Toast.makeText(getApplicationContext(), "更新个人信息成功", Toast.LENGTH_LONG).show();
        User.getInstance().updateUser(data);
        setResult(RESULT_OK);
        finish();
    }
}
