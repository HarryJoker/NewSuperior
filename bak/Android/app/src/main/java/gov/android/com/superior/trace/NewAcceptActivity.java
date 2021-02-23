package gov.android.com.superior.trace;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jph.takephoto.model.TImage;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.io.File;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;

public class NewAcceptActivity extends BaseLoadActivity {

    private Spinner spLeader;
    private Spinner spDeputyLeader;
    private Spinner spDoer;

    private UserAdapter mLeaderAdapter;

    private UserAdapter mDeputyAdapter;

    private UserAdapter mDonerAdapter;

    private int  unitTaskId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_accept);

        setTitle("申领责任事项");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spLeader = (Spinner) findViewById(R.id.sp_leader);
        spDeputyLeader = (Spinner) findViewById(R.id.sp_deputy_leader);
        spDoer = (Spinner) findViewById(R.id.sp_doer);

        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);

        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        requestUnitUsers();
    }

    private void requestUnitUsers() {
        showProgress("加载中...");
        OkGo.<JSONArray>get(Config.USER_LIST_BY_UNIT + "/" + User.getInstance().getUnitId()).tag(this).execute(usersCallback);
    }

    private JsonObjectCallBack<JSONArray> usersCallback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {
            removeProgress();
            spLeader.setAdapter(mLeaderAdapter = new UserAdapter("请选择主要责任人", response.body()));

            spDeputyLeader.setAdapter(mDeputyAdapter = new UserAdapter("请选择分管责任人", response.body()));

            spDoer.setAdapter(mDonerAdapter = new UserAdapter("请选择具体责任人", response.body()));

        }

        @Override
        public void onError(Response<JSONArray> response) {
            super.onError(response);
            removeProgress();
        }
    };

    private boolean checked() {
        if (spLeader.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "请选择主要责任人", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spDeputyLeader.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "请选择分管责任人", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spDeputyLeader.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "请选择具体责任人", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String makeContent() {
        String content = "主要负责人：";
        content += mLeaderAdapter.getItem(spLeader.getSelectedItemPosition()).getString("name");
        content += "\n";

        content += "分管负责人：" + mDeputyAdapter.getItem(spDeputyLeader.getSelectedItemPosition()).getString("name");
        content += "\n";

        content += "具体负责人：" + mDonerAdapter.getItem(spDoer.getSelectedItemPosition()).getString("name");

        return content;
    }

    public void acceptClick(View view) {
        if (checked()) {
            HttpParams params = new HttpParams();
            params.put("unitTaskId", unitTaskId);
            params.put("userId", User.getInstance().getUserId());
            params.put("content", makeContent());
            params.put("progress", "0");
            params.put("status", 1);
            params.put("unitId", User.getInstance().getUnitId());

            OkGo.<JSONObject>post(Config.TRACE_NEW).headers("accept-encoding", "gzip").params(params).tag(this).execute(jsonCallback);

        }
    }

    private JsonObjectCallBack<JSONObject> jsonCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            Toast.makeText(NewAcceptActivity.this, "申领任务成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    class UserAdapter extends BaseAdapter {
        private JSONArray users = new JSONArray();

        public UserAdapter(String tip, JSONArray array) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", TextUtils.isEmpty(tip) ? "选择对应的责任人" : tip);
            jsonObject.put("id", 0);
            users.add(0, jsonObject);

            if (array != null) {
                users.addAll(array);
            }
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return users.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.item_dropdown, parent, false);
            ((TextView)convertView).setText(getItem(position).getString("name"));
            return convertView;
        }
    }

}
