package gov.android.com.superior.home.unit.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.home.supervise.AttachmentAdapter;
import gov.android.com.superior.http.Config;

public class TaskDetailActivity extends BaseLoadActivity {

    private long taskId;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        setTitle("任务信息");

        taskId = getIntent().getLongExtra("taskId", 0);

        listView = (ListView) findViewById(R.id.lv_task);

        asyncGetTask();

    }

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
            listView.setAdapter(new TaskAdapter(response.body()));
            refreshTaskAttachment(response.body());
        }
    };

    private void refreshTaskAttachment(Map task) {
        if (task != null && task.containsKey("attachmentids") && task.get("attachmentids") != null) {
            String attachmentStr = task.get("attachmentids").toString();
            if (attachmentStr.trim().length() > 0) {
                List<String> attachments = Arrays.asList(attachmentStr.split(","));
                GridView gridView = (GridView) findViewById(R.id.gv_attachment);
                gridView.setAdapter(new AttachmentAdapter(this, attachments, 0));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(TaskDetailActivity.this, TransitionActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TaskDetailActivity.this, view, "transition");
                        intent.putExtra("url", Config.ATTACHMENT + "/" + parent.getAdapter().getItem(position));
                        startActivity(intent, options.toBundle());
                    }
                });
            } else {
                findViewById(R.id.layout_attachment).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.layout_attachment).setVisibility(View.GONE);
        }
    }

    class TaskAdapter extends BaseAdapter {

        private final String[] weeks =  new String[] {"", "一", "二", "三", "四", "五", "六", "日"};

        private final String[] keys = new String[] {"name", "plan", "reportDate", "category", "priority"};
        private final String[] titles = new String[] {"重点工作：", "目标任务：", "提报时间：", "任务类别：", "优先级别："};

        private final String[] reportKeys = new String[] {"name", "plan", "planDetail", "reportDate", "category", "priority"};
        private final String[] reportTitles = new String[] {"重点工作：", "目标任务：", "推进计划：", "提报时间：", "任务类别：", "优先级别："};

        private final String[] deployKeys = new String[] {"name", "plan", "sequence", "reportDate", "category", "priority"};
        private final String[] deployTitles = new String[] {"重点工作：", "目标任务：", "排名情况：", "提报时间：", "任务类别：", "优先级别："};

        private int[] priorityRes = new int[] {0, R.drawable.shape_state_warn, R.drawable.shape_state_return, R.drawable.shape_state_finish};

        private Map<String, Object> task = new HashMap<>();

        private int category = 0;

        public void updateData(Map<String, Object> data) {
            if (data != null) task.clear();
            task.putAll(data);
        }

        public TaskAdapter(Map<String, Object> data) {
            if (data != null) {
                task.putAll(data);
                category = Integer.parseInt(data.get("category").toString());
            }
        }

        @Override
        public int getCount() {
            if (task.size() == 0) {
                return 0;
            } else {
                if (category == 1) {
                    return reportTitles.length;
                } else if (category == 2) {
                    return deployTitles.length;
                } else {
                    return titles.length;
                }
            }
        }

        @Override
        public Object getItem(int i) {
            if (category == 1) {
                return task.get(reportKeys[i]);
            } else if (category == 2) {
                return task.get(deployKeys[i]);
            } else {
                return task.get(keys[i]);
            }
        }

        public String getItemTitle(int i) {
            if (category == 1) {
                return reportTitles[i];
            } else if (category == 2) {
                return deployTitles[i];
            } else {
                return titles[i];
            }
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            String key = null;
            if (category == 1) {
                key = reportKeys[i];
            } else if (category == 2) {
                key = deployKeys[i];
            } else {
                key =keys[i];
            }

            view = getLayoutInflater().inflate(key.equals("priority") ? R.layout.item_task4 : R.layout.item_task2, null);
            ((TextView) view.findViewById(R.id.tv_key)).setText(getItemTitle(i));

            String content = getItem(i).toString();
            if (key.equals("reportDate")) {
                int type = Integer.parseInt(task.get("reportType").toString());
                if (type == 1) {
                    content = "每月" + content + "日 " + task.get("reportTime").toString();
                } else if (type == 3) {
                    int weekPosition = Integer.parseInt(task.get("reportDate").toString());
                    content = "每周" + (weekPosition > 0 && weekPosition < weeks.length ? weeks[weekPosition] : "") + " " + task.get("reportTime").toString();
                }
            } else if (key.equals("category")) {
                content = SuperiorApplicaiton.titles[Integer.parseInt(content) - 1];
            } else if (key.equals("priority")) {
                int priority = Integer.parseInt(content);
                view.findViewById(R.id.tv_state).setBackgroundResource(priorityRes[priority]);
                content =  priority == 1 ? "优先" : priority == 2 ? "次要" : "最低";
            } else if (key.equals("sequence")) {
                content = "第" + content + "名";
            }
            ((TextView) view.findViewById(R.id.tv_value)).setText(content);
            return view;
        }
    }

}
