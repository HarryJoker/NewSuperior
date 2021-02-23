package gov.android.com.superior.home.unit.task;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.home.supervise.AttachmentAdapter;
import gov.android.com.superior.http.Config;

public class TraceActivity extends BaseLoadActivity {

    private long taskId;
    private long traceId;

    private TextView tv_task_title;
    private TextView tv_task_content;
    private TextView tv_task_plan;
    private TextView tv_verifyContent;
    private TextView tv_progress;

    private TextView tv_tip;

    private ListView gradeListView;

    private GridView gv_attachment;
    private AttachmentAdapter attachmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);

        setTitle("任务进展详情");

        taskId = getIntent().getLongExtra("taskId", 0);
        traceId = getIntent().getLongExtra("traceId", 0);

        tv_task_title = (TextView) findViewById(R.id.tv_title);
        tv_task_content = (TextView) findViewById(R.id.tv_content);
        tv_task_plan = (TextView) findViewById(R.id.tv_trace);
        tv_verifyContent = (TextView) findViewById(R.id.tv_verifyContent);

        tv_progress = (TextView)findViewById(R.id.tv_prgress);

        tv_tip = (TextView)findViewById(R.id.tv_tip);

        gradeListView = (ListView) findViewById(R.id.lv_grade);

        gv_attachment = (GridView) findViewById(R.id.gv_attachment);
        gv_attachment.setOnItemClickListener(itemClickListener);

        asyncTaskTrace();
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(TraceActivity.this, TransitionActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TraceActivity.this, view, "transition");
            intent.putExtra("url", Config.ATTACHMENT + "/" + attachmentAdapter.getItem(i));
            startActivity(intent, options.toBundle());
        }
    };


    private void asyncTaskTrace() {
        showProgress("加载中");
        OkGo.<Map>post(Config.TRACE_DETAIL + "/" + taskId + "/" + traceId).tag(this).execute(jsonCallback);
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
//    1：完成，2：快速，3：正常，4：缓慢

    private final static String[] progressTips = new String[] {"", "任务完成", "进展较快", "序时推进", "进展缓慢"};
    private final static int[] progressStates = new int[] {0, R.mipmap.state_finish, R.mipmap.state_fast, R.mipmap.state_nomal, R.mipmap.state_slow};

    private void refresh(Map<String, Object> data) {
        Map<String, Object> task = (Map<String, Object>) data.get("task");
        tv_task_title.setText(task.get("name").toString());
        tv_task_content.setText(task.get("plan").toString());


        Map<String, Object> trace = (Map<String, Object>) data.get("trace");
        tv_task_plan.setText(trace.get("content").toString());

        //提报任务审核过的
        if (Integer.parseInt(trace.get("type").toString()) == 0) {

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

            Drawable drawable = getResources().getDrawable(progressStates[Integer.parseInt(task.get("progress").toString())]);
            drawable.setBounds(0, 0, 35, 35);
            tv_progress.setCompoundDrawablePadding(10);
            tv_progress.setCompoundDrawables(drawable, null, null, null);
            tv_progress.setText(progressTips[Integer.parseInt(trace.get("progress").toString())]);

            if (Integer.parseInt(trace.get("status").toString()) == 0) {
                tv_verifyContent.setText("该任务报送未审核");
                tv_verifyContent.setTextColor(Color.GREEN);
                findViewById(R.id.tv_tip_score).setVisibility(View.GONE);
                gradeListView.setVisibility(View.GONE);
            } else {
                tv_verifyContent.setText(trace.get("verifyContent").toString());
                gradeListView.setAdapter(new GradeAdapter((List) trace.get("grades")));
            }
        } else {

            //其它Trace (1：系统自动生成督促，2：督查主动催报，3,任务领取，4：领导批示)
            String[] typeTips = new String[]{"", "系统催报", "督查催报", "任务领取", "领导批示"};
            findViewById(R.id.layout).setVisibility(View.GONE);

            tv_tip.setText(typeTips[Integer.parseInt(trace.get("type").toString())]);
            tv_task_plan.setText(trace.get("content").toString());

        }
    }

    private class GradeAdapter extends BaseAdapter {
        private List grades = new ArrayList();

        public GradeAdapter(List list) {
            if (list != null && list.size() > 0) grades.clear();
            grades.addAll(list);
        }

        @Override
        public int getCount() {
            return grades.size() == 0 ? 1 : grades.size();
        }

        @Override
        public Object getItem(int i) {
            return grades.size() == 0 ? null : grades.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Map grade = (Map) getItem(i);
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.item, viewGroup ,false);
            TextView textView = (TextView) layout.findViewById(R.id.text1);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 0);

            textView.setLayoutParams(lp);

            textView.setPadding(0, 15, 15, 16);

            textView.setTextColor(Color.parseColor("#696969"));

            if (grade == null) {
                textView.setText("此次报送工作未扣分");
            } else {

                String text = grade.get("name").toString();
                int category = Integer.parseInt(grade.get("category").toString());
                if (category == 6) {
                    text += "（加" + grade.get("grade").toString().replace("-", "") + "分）";
                } else {
                    text += "（扣" + grade.get("grade") + "分）";
                }
                textView.setText(text);
            }
            return layout;
        }
    }

}
