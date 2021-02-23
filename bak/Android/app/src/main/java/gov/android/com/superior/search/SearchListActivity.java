package gov.android.com.superior.search;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.home.unit.task.TaskActivity;
import gov.android.com.superior.home.unit.task.TaskTraceActivity;
import gov.android.com.superior.http.Config;

public class SearchListActivity extends BaseLoadActivity {

    private String keyword;

    private ListView listView;

    private TaskAdapter taskAdapter = new TaskAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        listView = (ListView) findViewById(R.id.lv_search);

        listView.setAdapter(taskAdapter);
        listView.setOnItemClickListener(itemClickListener);

        setTitle("搜索结果");

        keyword = getIntent().getStringExtra("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            asyncSearch();
        } else {
            Toast.makeText(this, "请输入查询关键字", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void asyncSearch() {
        showProgress("搜索中");
        OkGo.<List<Map<String, Object>>>post(Config.SEARCH).tag(this).params("keyword", keyword).params("unitId", User.getInstance().get("unitId").toString()).params("role", User.getInstance().getUserRole() + "").execute(jsonCallback);
    }

    private JsonCallback<List<Map<String, Object>>> jsonCallback = new JsonCallback<List<Map<String, Object>>>() {
        @Override
        public void onError(Response<List<Map<String, Object>>> response) {
            super.onError(response);
            removeProgress();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<List<Map<String, Object>>> response) {
            removeProgress();

            taskAdapter.updateTasks(response.body());

            taskAdapter.notifyDataSetChanged();
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            if (l == 0) return;

            Map<String, Object> task = taskAdapter.getItem(i);

            int aInt = Integer.parseInt(task.get("accept").toString());

            int role = User.getInstance().getUserRole();

            //部门未领取的任务
            if (aInt == 0 && role == 4) {
                Intent intent = new Intent(SearchListActivity.this, TaskActivity.class);
                intent.putExtra("taskId", l);
                startActivityForResult(intent, 0x00F2);
            } else  {
                Intent intent = new Intent(SearchListActivity.this, TaskTraceActivity.class);
//                intent.putExtra("taskId", l);
                List tasks = taskAdapter.getGroupTasks(i);
                if (tasks != null && tasks.size() == 0) {
                    tasks.add(task);
                }
                intent.putExtra("groupTasks", (Serializable) tasks);
                startActivityForResult(intent, 0x00F4);
            }
        }
    };

    class TaskAdapter extends BaseAdapter {

        private int role = User.getInstance().getUserRole();
        private List<Map<String,Object>> tasks = new ArrayList<>();

        public void updateTasks(List<Map<String, Object>> list) {
            if (list != null) tasks.clear();
            tasks.addAll(list);
        }

        @Override
        public int getCount() {
            return tasks.size() == 0 ? 1 : tasks.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return tasks.size() == 0 ? null : tasks.get(i);
        }

        public List<Map<String, Object>> getGroupTasks(int i) {
            Map<String, Object> task = getItem(i);
            if (task != null && task.containsKey("groupTasks")) {
                Object object = task.get("groupTasks");
                if (object != null && object instanceof List) {
                    return (List) object;
                }
            }
            return new ArrayList<>();
        }

        @Override
        public long getItemId(int i) {
            return tasks.size() == 0 ? 0 : Long.valueOf(getItem(i).get("id").toString());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (tasks.size() == 0) {
                view = getLayoutInflater().inflate(R.layout.item_null_tip, null);
                ((TextView)view).setText("暂未搜索到结果");
                return view;
            } else {
                if (role == 4) {
                    view = getLayoutInflater().inflate(R.layout.item_task, null);
                    ((TextView) view.findViewById(R.id.tv_title)).setText(getItem(i).get("name").toString());
                    ((TextView) view.findViewById(R.id.tv_plan)).setText(getItem(i).get("plan").toString());
                } else {
                    view = getLayoutInflater().inflate(R.layout.item_task_ext, null);
                    ((TextView) view.findViewById(R.id.tv_title)).setText(getItem(i).get("name").toString());
                    ((TextView) view.findViewById(R.id.tv_plan)).setText(getItem(i).get("plan").toString());
//                    ((TextView) view.findViewById(R.id.tv_unit)).setText(getItem(i).get("unitName").toString());
                    bindUnit(((TextView) view.findViewById(R.id.tv_unit)), i);
                }

                int category = Integer.parseInt(getItem(i).get("category").toString());

                if (category > 2) {
                    view.findViewById(R.id.iv_logo).setVisibility(View.GONE);
                } else {
                    if (category == 1) {
                        int progress = Integer.parseInt(getItem(i).get("progress").toString());
                        ((ImageView) view.findViewById(R.id.iv_logo)).setImageResource(SuperiorApplicaiton.taskStates[progress]);
                    } else if (category == 2) {
                        view.findViewById(R.id.iv_logo).setVisibility(View.GONE);
                        AvatarImageView avatarImageView = ((AvatarImageView) view.findViewById(R.id.iv_logo2));
                        avatarImageView.setVisibility(View.VISIBLE);
                        avatarImageView.setTextAndColor(getItem(i).get("sequence").toString(), Color.parseColor("#f8f8ff"));

                    }
                }
                return view;
            }
        }

        private void bindUnit(TextView textView, int position) {
            if (textView == null) return;

            List<Map<String, Object>> groupTasks = getGroupTasks(position);
            if (groupTasks == null || groupTasks.size() == 0) {
                textView.setText(getItem(position).get("unitName").toString());
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (Map<String, Object> groupTask : groupTasks) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append("，");
                        stringBuilder.append(groupTask.get("unitName").toString());
                    } else {
                        stringBuilder.append(groupTask.get("unitName").toString());
                    }
                }
                textView.setText(stringBuilder.toString());
            }
        }
    }

}
