package gov.android.com.superior.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.R;
import gov.android.com.superior.TakePhotoLoadActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.login.LoginActivity;

public class CreateUserActivity extends TakePhotoLoadActivity {

    private EditText et_name;
    private EditText et_duty;
    private Spinner sp_unit;
    private RadioGroup rg_sex;

    private View layout_unit;

    private AvatarImageView ivAvatar;

    private int userId;

    private int type = 0;

    private String avatarPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        type = getIntent().getIntExtra("type", 0);

        delegate.getSupportActionBar().setTitle(type == 0 ? "创建个人信息" : "更新个人信息");

        if (type == 1) {
            delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        userId = getIntent().getIntExtra("userId", User.getInstance().getUserId());

        et_name = (EditText) findViewById(R.id.et_name);
        et_duty = (EditText) findViewById(R.id.et_duty);

        rg_sex = (RadioGroup) findViewById(R.id.rg_sex);

        sp_unit = (Spinner) findViewById(R.id.sp_unit);

        layout_unit = findViewById(R.id.layout_unit);

        ivAvatar = findViewById(R.id.iv_avatar);

        if (type == 1) {
            layout_unit.setVisibility(View.GONE);
            refreshUser();
        } else {
            asyncGetAllUnits();
        }
    }

    private void refreshUser() {
        Glide.with(this).load(Config.ATTACHMENT + User.getInstance().get("logo")).into(ivAvatar);
        et_name.setText(User.getInstance().get("name").toString());
        et_duty.setText(User.getInstance().get("duty").toString());
        if (User.getInstance().get("sex") == "男") {
            ((RadioButton)findViewById(R.id.rb_man)).setChecked(true);
        } else {
            ((RadioButton)findViewById(R.id.rb_women)).setChecked(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private TakePhoto takePhoto;

    public void avatarClick(View v) {
        takePhoto = getTakePhoto();
        takePhoto.onEnableCompress(buildCompressConfig(), true);
        takePhoto.onPickMultiple(1);
    }


    private CompressConfig buildCompressConfig () {
        return new CompressConfig.Builder()
                .setMaxSize(1024 * 200)
                .setMaxPixel(200)
                .enableReserveRaw(true)
                .create();
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        Logger.d(result.getImage());
        Logger.d(result.getImages());

        if (result != null && result.getImage() != null) {
            avatarPath = result.getImage().getCompressPath();
            Glide.with(this).load(new File(avatarPath)).into(ivAvatar);
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }



    private void asyncGetAllUnits() {
        showProgress("加载中");
        OkGo.<List>get(Config.UNIT_ALL).tag(this).execute(jsonUntisCallback);
    }

    private JsonCallback<List> jsonUntisCallback = new JsonCallback<List>() {
        @Override
        public void onError(Response<List> response) {
            super.onError(response);
            removeProgress();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<List> response) {
            removeProgress();

            sp_unit.setAdapter(new UnitAdapter(response.body()));

        }
    };

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

        if (TextUtils.isEmpty(et_duty.getText().toString().trim())) {
            Toast.makeText(this, "请输入职务", Toast.LENGTH_LONG).show();
            return false;
        }

        if (type == 0) {
            if (sp_unit.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "请选择所属部门", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    private void asyncUpdate() {

        showProgress("提交中");

        HttpParams params = new HttpParams();
        params.put("name", et_name.getText().toString());
        params.put("sex", rg_sex.getCheckedRadioButtonId() == R.id.rb_man ? "男" : "女");
        params.put("duty", et_duty.getText().toString());
//        params.put("card", et_card.getText().toString());
//        params.put("jobDetail", et_jobDetail.getText().toString());
        if (type == 0) {
            params.put("unitId", sp_unit.getSelectedItemId());
        }

        if (!TextUtils.isEmpty(avatarPath)) {
            params.put("file", new File(avatarPath));
        }

        OkGo.<Map>post(Config.USER_UPDATE + "/" + userId).params(params).tag(this).execute(jsonCallback);
    }

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            removeProgress();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<Map> response) {
            Logger.d("onSuccess: " +  response.body());
            User.getInstance().updateUser(response.body());

            removeProgress();

            if (type == 0) {
                startActivity(new Intent(CreateUserActivity.this, LoginActivity.class));
            }

            finish();
            Toast.makeText(getApplicationContext(), "更新个人信息成功", Toast.LENGTH_LONG).show();
        }
    };

    class UnitAdapter extends BaseAdapter {

        private List units = new ArrayList();


        public UnitAdapter(List list) {
            if (list != null) units.clear();
            units.addAll(list);
            Map<String, String> nullUnit = new HashMap<>();
            nullUnit.put("id", 0 + "");
            nullUnit.put("name", "请选择单位部门");
            units.add(0, nullUnit);
        }

        @Override
        public int getCount() {
            return units.size();
        }

        @Override
        public Map getItem(int i) {
            return (Map) units.get(i);
        }

        @Override
        public long getItemId(int i) {
            return Long.parseLong(getItem(i).get("id").toString());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = (TextView)  getLayoutInflater().inflate(R.layout.item_spinner, viewGroup, false);
            textView.setText(getItem(i).get("name").toString());
            return textView;
        }
    }

}
