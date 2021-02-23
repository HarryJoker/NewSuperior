package gov.android.com.superior.ui.unit.dashboard.trace;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class TraceAcceptActivity_bak extends BaseToolBarActivity {

    public static final int REQUEST_ACCEPT_TASK = 0X00F1;

    private Spinner spLeader;
    private Spinner spDeputyLeader;
    private Spinner spDoer;

    private UserAdapter mLeaderAdapter;

    private UserAdapter mDeputyAdapter;

    private UserAdapter mDonerAdapter;

    private int  unitTaskId = 0;

    @Override
    public void onInitParams() {
        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);
        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_trace_accept;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("申领责任事项");
    }

    @Override
    protected void onFindViews() {
        spLeader =  findViewById(R.id.sp_leader);
        spDeputyLeader =  findViewById(R.id.sp_deputy_leader);
        spDoer =  findViewById(R.id.sp_doer);
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        requestUnitUsers();
    }

    private void requestUnitUsers() {
        showLoading("加载中...");
        OkGo.<List<JSONObject>>get(HttpUrl.USER_LIST_BY_UNIT + "/" + User.getInstance().getUnitId()).tag(this).execute(getJsonArrayCallback(HttpUrl.USER_LIST_BY_UNIT));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        hideLoading();
        spLeader.setAdapter(mLeaderAdapter = new UserAdapter("请选择主要责任人", data));

        spDeputyLeader.setAdapter(mDeputyAdapter = new UserAdapter("请选择分管责任人", data));

        spDoer.setAdapter(mDonerAdapter = new UserAdapter("请选择具体责任人", data));
    }

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

            JSONObject responsibility = mLeaderAdapter.getItem(spLeader.getSelectedItemPosition());
            params.put("responsibilityUserId", responsibility.getIntValue("id"));
            params.put("responsibilityUserName", responsibility.getString("name"));
            JSONObject partResponsibility = mDeputyAdapter.getItem(spDeputyLeader.getSelectedItemPosition());
            params.put("partReponsibilityUserId",partResponsibility .getIntValue("id"));
            params.put("partReponsibilityUserName", partResponsibility.getString("name"));
            JSONObject handle = mDonerAdapter.getItem(spDoer.getSelectedItemPosition());
            params.put("handleUserId", handle.getIntValue("id"));
            params.put("handleUserName", handle.getString("name"));
            OkGo.<JSONObject>post(HttpUrl.NEW_ACCEPT_TASK_TRACE).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_ACCEPT_TASK_TRACE));
        }
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        Toast.makeText(TraceAcceptActivity_bak.this, "申领任务成功", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    class UserAdapter extends BaseAdapter {
        private List<JSONObject> users = new ArrayList<>();

        public UserAdapter(String tip, List<JSONObject> array) {
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
            return users.get(position);
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
