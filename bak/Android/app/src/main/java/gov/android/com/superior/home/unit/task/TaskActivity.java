package gov.android.com.superior.home.unit.task;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.home.supervise.AttachmentAdapter;
import gov.android.com.superior.http.Config;

public class TaskActivity extends BaseLoadActivity {

    private long taskId;

    private TextView tv_accept;

    private ListView listView;

    private EditText et_duty;

    private EditText et_second;

    private EditText et_primary;

    private TaskAdapter taskAdapter = new TaskAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        setTitle("申领任务");

        et_duty = (EditText)findViewById(R.id.et_duty);

        et_second = (EditText)findViewById(R.id.et_second);

        et_primary = (EditText)findViewById(R.id.et_primary);

        tv_accept = (TextView) findViewById(R.id.tv_acceptView);

        taskId = getIntent().getLongExtra("taskId", 0);

        listView = (ListView) findViewById(R.id.lv_task);

        listView.setAdapter(taskAdapter);

        asyncGetTask();

    }

    private void enableAcceptView(boolean enable) {
        tv_accept.setBackgroundColor(enable ? Color.parseColor("#0066CC") : Color.GRAY);
        tv_accept.setEnabled(enable);
    }

    private boolean check() {
        if (TextUtils.isEmpty(et_duty.getText().toString())) {
            Toast.makeText(this, "请填写责任人", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(et_second.getText().toString())) {
            Toast.makeText(this, "请填写分管责任人", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(et_primary.getText().toString())) {
            Toast.makeText(this, "请填写具体责任人", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void acceptClick(View v) {
        if (check()) {
            enableAcceptView(false);
            showProgress("申领中");
            HttpParams params = new HttpParams();
            params.put("category", taskAdapter.getItem(3).toString());
            params.put("unitId", taskAdapter.getValue("unitId").toString());
            //领取任务
            params.put("type", 3);

            String content = "主要负责人：" + et_duty.getText() + "\n";
            content += "分管负责人：" + et_second.getText() + "\n";
            content += "具体责任人：" + et_primary.getText();

            params.put("content", content);
            params.put("unitName", ((Map) User.getInstance().get("unit")).get("name").toString());

            OkGo.<Map>post(Config.TASK_ACCEPT + "/" + taskId + "/" + User.getInstance().getUserId()).tag(this).params(params).execute(acceptJsonCallback);
        }
    }

    private JsonCallback<Map> acceptJsonCallback = new JsonCallback<Map>() {
        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            showCallbckError(response);
            removeProgress();
            enableAcceptView(true);
        }

        @Override
        public void onSuccess(Response<Map> response) {
            removeProgress();
            enableAcceptView(false);
            Toast.makeText(TaskActivity.this, "申领成功", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }
    };

    private void asyncGetTask() {
        showProgress("加载中");
        OkGo.<Map>get(Config.TASK_GET + "/" + taskId).tag(this).execute(jsonCallback);
    }

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {
        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            showCallbckError(response);
            removeProgress();
        }

        @Override
        public void onSuccess(Response<Map> response) {
            removeProgress();
            taskAdapter.updateData(response.body());
            taskAdapter.notifyDataSetChanged();

            refreshTaskAttachment(response.body());

            int accept = taskAdapter.getTaskAccept();
            if (accept == 1) {
                enableAcceptView(false);
                Toast.makeText(TaskActivity.this, "该任务已经被申领", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void refreshTaskAttachment(Map task) {
        GridView gridView = (GridView) findViewById(R.id.gv_attachment);
        if (task != null && task.containsKey("attachmentids") && task.get("attachmentids") != null) {
            String attachmentStr = task.get("attachmentids").toString();
            if (attachmentStr.trim().length() > 0) {
                List<String> attachments = Arrays.asList(attachmentStr.split(","));
                gridView.setAdapter(new AttachmentAdapter(this, attachments, 0));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(TaskActivity.this, TransitionActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TaskActivity.this, view, "transition");
                        intent.putExtra("url", Config.ATTACHMENT + "/" + parent.getAdapter().getItem(position));
                        startActivity(intent, options.toBundle());
                    }
                });
            } else {
                gridView.setVisibility(View.GONE);
            }
        } else {
            gridView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent();
        intent.putExtra("accept", 21);

        setResult(RESULT_OK, intent);
        finish();
    }

    class TaskAdapter extends BaseAdapter {

        private final String[] weeks =  new String[] {"", "一", "二", "三", "四", "五", "六", "日"};

        private final String[] keys = new String[] {"name", "plan", "reportDate", "category", "priority"};
        private final String[] titles = new String[] {"重点工作：", "目标任务：", "提报时间：", "任务类别：", "优先级别："};

        private int[] priorityRes = new int[] {0, R.drawable.shape_state_warn, R.drawable.shape_state_return, R.drawable.shape_state_finish};

        private Map<String, Object> task = new HashMap<>();

        public void updateData(Map<String, Object> data) {
            if (data != null) task.clear();
            task.putAll(data);
        }

        public int getTaskAccept() {
            if (task.containsKey("accept")) {
                return Integer.parseInt(task.get("accept").toString());
            }
            return -1;
        }

        public Object getValue(String key) {
            if (key != null) return task.get(key);
            return "";
        }

        @Override
        public int getCount() {
            return task.size() == 0 ? 0 : titles.length;
        }

        @Override
        public Object getItem(int i) {
            return task.get(keys[i]);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(i == 4 ? R.layout.item_task4 : R.layout.item_task2, null);
            ((TextView) view.findViewById(R.id.tv_key)).setText(titles[i]);

            String content = getItem(i).toString();
            if (i == 2) {
                int type = Integer.parseInt(task.get("reportType").toString());
                if (type == 1) {
                    content = "每月" + content + "日 " + task.get("reportTime").toString();
                } else if (type == 3) {
                    int weekPosition = Integer.parseInt(task.get("reportDate").toString());
                    content = "每周" + (weekPosition > 0 && weekPosition < weeks.length ? weeks[weekPosition] : "") + " " + task.get("reportTime").toString();
                }
            } else if (i == 3) {
                content = SuperiorApplicaiton.titles[Integer.parseInt(content) - 1];
            } else if (i == 4) {
                int priority = Integer.parseInt(content);
                view.findViewById(R.id.tv_state).setBackgroundResource(priorityRes[priority]);
                content =  priority == 1 ? "优先" : priority == 2 ? "次要" : "最低";
            }
            ((TextView) view.findViewById(R.id.tv_value)).setText(content);
            return view;
        }
    }

}
