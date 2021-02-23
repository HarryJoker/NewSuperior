package gov.android.com.superior.ui.userinfo;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.first.orient.base.utils.GlideEngine;
import com.first.orient.base.utils.JokerLog;
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

public class UnitActivity extends BaseToolBarActivity {

    private RoundedImageView ivAvatar;
    private TextView tvUnitName;
    private EditText etDuty;

    private File mLogoFile;

    private JSONObject mUnit;

    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("部门信息");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_unit;
    }

    @Override
    protected void onFindViews() {
        etDuty = findViewById(R.id.et_duty);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvUnitName = findViewById(R.id.tv_unit_name);
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }

    public void chooseLogoClick(View v) {
        mLogoFile = null;
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_default_style)
                .imageEngine(GlideEngine.createGlideEngine())
                .isCompress(false)
                .isAndroidQTransform(Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
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
                            refreshUnitView();
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
        requestUnit();
    }

    private void refreshUnitView() {
        if (mLogoFile != null) {
            Glide.with(this).load(mLogoFile).into(ivAvatar);
        } else if (mUnit != null && !TextUtils.isEmpty(mUnit.getString("logo"))) {
            Glide.with(this).load(HttpUrl.makeAttachmentUrl(mUnit.getString("logo"))).placeholder(R.mipmap.ic_avatar).into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.mipmap.ic_avatar);
        }
        tvUnitName.setText(User.getInstance().getUnitName());
        if (mUnit != null) {
            etDuty.setText(mUnit.getString("intro"));
        } else {
            etDuty.setText(User.getInstance().getUserUnit().getString("intro"));
        }
    }

    private void requestUnit() {
        showLoading();
        OkGo.<JSONObject>get(HttpUrl.GET_UNIT+ "/" + User.getInstance().getUnitId()).tag(this).execute(getJsonObjectCallback(HttpUrl.GET_UNIT));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        if (action.equals(HttpUrl.GET_UNIT)) {
            mUnit = data;
            refreshUnitView();
            hideLoading();
        }

        if (action.equals(HttpUrl.UPDATE_UNIT)) {
            hideLoading();
            User.getInstance().update("unit", data);
            JokerLog.d(User.getUser().toJSONString());
            Toast.makeText(getApplicationContext(), "更新部门信息成功", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    public void updateUserClick(View v) {
        if (etDuty.getEditableText().toString().length() == 0) {
            showToast("请填写部门职责描述");
            return;
        }
        asyncUpdate();
    }

    private void asyncUpdate() {
        showLoading("请稍后...");
        HttpParams params = new HttpParams();
        params.put("intro", etDuty.getEditableText().toString());
        if (mLogoFile != null) {
            params.put("logo", mLogoFile);
        }
        OkGo.<JSONObject>post(HttpUrl.UPDATE_UNIT + "/" + User.getInstance().getUnitId()).isMultipart(true).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.UPDATE_UNIT));
    }
}
