package gov.android.com.superior.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.login.LoginActivity;

public class UnitActivity extends BaseLoadActivity {

    private AvatarImageView ivAvatar;
    private TextView tvName;
    private TextView tvUnitName;
    private Spinner spUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        setTitle("更换部门");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivAvatar = (AvatarImageView) findViewById(R.id.iv_avatar);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvUnitName = (TextView) findViewById(R.id.tv_unit_name);
        spUnit = (Spinner) findViewById(R.id.sp_unit);

        refreshUser();

        asyncGetAllUnits();
    }

    private void refreshUser() {
        Glide.with(this).load(Config.ATTACHMENT + User.getInstance().get("logo")).into(ivAvatar);
        tvName.setText(User.getInstance().get("name").toString());
        tvUnitName.setText(User.getInstance().getUnitName());
    }

    private void asyncGetAllUnits() {
        showLoading();
        OkGo.<List>get(Config.UNIT_ALL).tag(this).execute(jsonUntisCallback);
    }

    private JsonCallback<List> jsonUntisCallback = new JsonCallback<List>() {
        @Override
        public void onSuccess(Response<List> response) {
            removeProgress();
            spUnit.setAdapter(new UnitAdapter(response.body()));
        }
    };


    public void updateUserClick(View v) {
        asyncUpdate();
    }

    private void asyncUpdate() {

        if (spUnit.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "请选择所属部门", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("提交中");

        HttpParams params = new HttpParams();
        params.put("unitId", spUnit.getSelectedItemId());

        OkGo.<Map>post(Config.USER_UPDATE_UNIT + "/" + User.getInstance().getUnitId()).params(params).tag(this).execute(jsonCallback);
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

            Intent intent = new Intent(getApplication(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), "更换部门成功,请重新登录", Toast.LENGTH_LONG).show();
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
