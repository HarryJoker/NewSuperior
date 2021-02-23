package gov.android.com.superior.home.supervise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.Eutils;
import gov.android.com.superior.view.NestExpandableListView;

public class VerifyActivity extends BaseLoadActivity {


    private int taskId;
    private int traceId;

    private boolean isFinish;
    private int progress = 0;

    private TextView tv_task_title;
    private TextView tv_task_content;
    private TextView tv_task_plan;
    private TextView tv_plan;
    private EditText et_content;
    private RadioGroup rg_progress;

    private GridView gv_attachment;
    private AttachmentAdapter attachmentAdapter;

    private NestExpandableListView expandableListView;

    private MyExpandableListViewAdapter gradeAdapter = new MyExpandableListViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        setTitle("审核任务");

        taskId = getIntent().getIntExtra("taskId", 0);
        traceId = getIntent().getIntExtra("traceId", 0);

        tv_task_title = (TextView) findViewById(R.id.tv_title);
        tv_task_content = (TextView) findViewById(R.id.tv_content);
        tv_task_plan = (TextView) findViewById(R.id.tv_trace);
        tv_plan = (TextView) findViewById(R.id.tv_plan);
        et_content = (EditText) findViewById(R.id.et_content);
        rg_progress = (RadioGroup) findViewById(R.id.rg_progrss);
        rg_progress.setOnCheckedChangeListener(checkedChangeListener);

        gv_attachment = (GridView) findViewById(R.id.gv_attachment);
        gv_attachment.setOnItemClickListener(itemClickListener);

        expandableListView = (NestExpandableListView) findViewById(R.id.elv_grade);
        expandableListView.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        expandableListView.setOnGroupExpandListener(onGroupExpandListener);
        expandableListView.setOnChildClickListener(childClickListener);
        expandableListView.setAdapter(gradeAdapter);

        asyncTaskTrace();

    }

    private void asyncTaskTrace() {
        showProgress("加载中");
        OkGo.<Map>post(Config.TRACE_GET_WITH_TASK + "/" + traceId).tag(this).execute(jsonCallback);
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
            removeProgress();
            refresh(response.body());

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.finish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_finish) {
            if (!isFinish) {
                if (check()) verify();
            } else {
                Toast.makeText(this, "您已成功审核该任务提报", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

//    select t.* , u.logo from tbl_trace t, tbl_unit u, tbl_task tt where t.unitId = u.id and t.taskId = tt.id and status = 0 and type = 0 and t.category = 7 order by t.createtime


    private void refresh(Map<String, Object> data) {
        Map<String, Object> task = (Map<String, Object>) data.get("task");
        tv_task_title.setText(task.get("name").toString());
        tv_task_content.setText(task.get("plan").toString());

        if (Integer.parseInt(task.get("category").toString()) == 1) {
            tv_plan.setText(task.get("planDetail").toString());
            findViewById(R.id.layout_plan).setVisibility(View.VISIBLE);
        }

        Map<String, Object> trace = (Map<String, Object>) data.get("trace");
        tv_task_plan.setText(trace.get("content").toString());

        String attachment = trace.get("attachment").toString();
        if (attachment.trim().length() > 0) {
            String[] attachments = attachment.split(",");
            if (attachmentAdapter == null) {
                attachmentAdapter = new AttachmentAdapter(this);
                gv_attachment.setAdapter(attachmentAdapter);
            }
            attachmentAdapter.update(attachments);
            attachmentAdapter.notifyDataSetChanged();
        }

        gradeAdapter.update((Map<String, Object>) data.get("grade"));
        gradeAdapter.notifyDataSetChanged();

    }

    private boolean check() {

        if (progress == 0) {
            Toast.makeText(this, "请选择任务进度", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private Map<String, String> makeScore() {
        double score = 0.0;
        int stuts = 1; //(0：进行中(新提报），1：已审核，2：退回，3：完成，4：过期未提报)
        List<Integer> ids = new ArrayList<>();
        Map<String, Object> grade = gradeAdapter.getExpandData();
        for (int i=0; i < grade.keySet().size(); i++) {
            for (Map<String, Object> map : (List<Map<String, Object>>)grade.get(grade.keySet().toArray()[i])) {
                if (map.containsKey("selected")) {
                    score += Double.parseDouble(map.get("grade").toString());
                    ids.add(Integer.parseInt(map.get("id").toString()));

                    //选择退回，则设置退出status状态
                    if (map.get("categoryName").toString().equals("任务退回")) {
                        stuts = 2;
                    }
                }
            }
        }

        Map<String, String> data = new HashMap<>();

        data.put("status", stuts + "");
        data.put("cutScore", score > 0 ? score + "" : "0.0");
        data.put("cutScoreDetail", ids.size() > 0 ? Eutils.listToString(ids, ',') : "");
        return data;
    }


    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == R.id.rb_finish) progress = 1;
            if (i == R.id.rb_fast) progress = 2;
            if (i == R.id.rb_nomal) progress = 3;
            if (i == R.id.rb_slow) progress = 4;
        }
    };

    private void verify() {

        showProgress("审核中");

        OkGo.<Map>post(Config.TRACE_VERIFY + "/" + traceId + "/" + taskId).tag(this).params(makeScore()).params("content", et_content.getText().toString()).params("progress", progress).execute(verifyCallback);
    }

    private JsonCallback<Map> verifyCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            removeProgress();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<Map> response) {
            removeProgress();
            Toast.makeText(VerifyActivity.this, "审核成功", Toast.LENGTH_LONG).show();
            isFinish = true;
            setResult(RESULT_OK);
            finish();
        }
    };

    private ExpandableListView.OnChildClickListener childClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
            Map<String, Object> child = gradeAdapter.getChild(i, i1);
            child.put("selected", child.containsKey("selected") ? !((Boolean)child.get("selected")) : true);

            gradeAdapter.notifyDataSetChanged();

            return false;
        }
    };


    private ExpandableListView.OnGroupExpandListener onGroupExpandListener = new ExpandableListView.OnGroupExpandListener() {
        @Override
        public void onGroupExpand(int groupPosition) {
            for (int i = 0; i < gradeAdapter.getGroupCount(); i++) {
                if (groupPosition != i) {
                    expandableListView.collapseGroup(i);
                }
            }
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(VerifyActivity.this, TransitionActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(VerifyActivity.this, view, "transition");
            intent.putExtra("url", Config.ATTACHMENT + "/" + attachmentAdapter.getItem(i));
            startActivity(intent, options.toBundle());
        }
    };

    class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        private Map<String, Object> expandData = new HashMap<>();

        public void update(Map<String, Object> data) {
            if (data != null && data.size() > 0) expandData.clear();
            expandData.putAll(data);
        }

        public Map<String, Object> getExpandData() {
            return expandData;
        }

        @Override
        public int getGroupCount() {
            return expandData.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return getGroup(i).size();
        }

        @Override
        public List<Map<String, Object>> getGroup(int i) {
            return (List<Map<String, Object>>) expandData.get((i + 1) + "");
        }

        @Override
        public Map<String, Object> getChild(int i, int i1) {
            return getGroup(i).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i + 1;
        }

        @Override
        public long getChildId(int i, int i1) {
            return Long.parseLong(getChild(i, i1).get("id").toString());
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.item, null);
            TextView textView = view.findViewById(R.id.text1);
            textView.setText(getChild(i, 0).get("categoryName").toString());
            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            Map<String, Object> child = getChild(i, i1);
            view = getLayoutInflater().inflate(R.layout.item_child, null);
            TextView textView =  view.findViewById(R.id.checkedTextView);
            textView.setText(child.get("name").toString() + "（" + child.get("grade") + "分）");
            CheckBox checkBox = view.findViewById(R.id.checkbox);
            checkBox.setChecked(child.containsKey("selected") ? (Boolean)child.get("selected") : false);
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

}
