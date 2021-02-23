package gov.android.com.superior.home.unit.task;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.harry.pulltorefresh.library.PullToRefreshBase;
import com.harry.pulltorefresh.library.PullToRefreshListView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.home.supervise.AttachmentAdapter;
import gov.android.com.superior.home.supervise.ReportActivity;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.Eutils;

public class TaskTraceActivity extends BaseLoadActivity {

    private View layoutUnit;

    private Spinner sp_units;

    private PullToRefreshListView listView;

    private Map<String, Object> curTask;

    private List<Map<String, Object>> groupTasks = new ArrayList<>();

    private TaskTraceAdapter taskAdapter = new TaskTraceAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_trace);

        setTitle("任务进展");

        layoutUnit = findViewById(R.id.layout_unit);
        sp_units = (Spinner) findViewById(R.id.sp_units);

        listView = (PullToRefreshListView) findViewById(R.id.listview);
        listView.setOnRefreshListener(refreshListener);
        listView.setOnItemClickListener(traceItemClick);
        listView.setAdapter(taskAdapter);

        Serializable serializable = getIntent().getSerializableExtra("groupTasks");
        if (serializable != null && serializable instanceof List) {
            groupTasks.addAll((List<Map<String,Object>>)serializable);
        }

        Logger.d(groupTasks);

        if (groupTasks.size() > 0) curTask = groupTasks.get(0);

        refreshView();

        asyncGetTaskTrace();
    }

    private void refreshView() {
        layoutUnit.setVisibility(groupTasks.size() > 1 ? View.VISIBLE : View.GONE);
        if (groupTasks.size() > 1) {
            sp_units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    curTask = groupTasks.get(position);
                    listView.setRefreshing();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            sp_units.setAdapter(new UnitAdapter());
        }
    }

    private AdapterView.OnItemClickListener traceItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 1) {
                Intent intent = new Intent(TaskTraceActivity.this, TaskDetailActivity.class);
                intent.putExtra("taskId",  Long.parseLong(curTask.get("id").toString()));
                startActivity(intent);
            } else if (i > 1) {
                Intent intent = new Intent(TaskTraceActivity.this, TraceActivity.class);
                intent.putExtra("taskId", Long.parseLong(curTask.get("id").toString()));
                intent.putExtra("traceId", l);
                startActivity(intent);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int role = User.getInstance().getUserRole();
        getMenuInflater().inflate(role == 3 ? R.menu.report_add : R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {

            int role = User.getInstance().getUserRole();

            if (role == 4) {
                Intent intent = new Intent(this, NewTraceActivity.class);
                intent.putExtra("taskId", Long.parseLong(curTask.get("id").toString()));
                intent.putExtra("task", Eutils.map2Bundle(taskAdapter.getItem(0)));
                startActivityForResult(intent, 0xFFF1);
            } else if (role < 4) {
                Intent intent = new Intent(this, ReplyTraceActivity.class);
                intent.putExtra("taskId", Long.parseLong(curTask.get("id").toString()));
                //type(1：系统自动生成督促，2：督查主动催报，4：领导批示)
                intent.putExtra("type", role == 3 ? 2 : 4);
                intent.putExtra("task", Eutils.map2Bundle(taskAdapter.getItem(0)));
                startActivityForResult(intent, 0xFFF1);
            }
            return true;
        }
//        else if (item.getItemId() == R.id.action_report) {
//            Intent intent = new Intent(this, ReportActivity.class);
//            intent.putExtra("taskId", Long.parseLong(curTask.get("id").toString()));
//            intent.putExtra("task", Eutils.map2Bundle(taskAdapter.getItem(0)));
//            startActivityForResult(intent, 0xFFF1);
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        listView.setRefreshing();

    }

    private void asyncGetTaskTrace() {
        if (curTask != null && curTask.containsKey("id")) {
            showProgress("加载中");
            OkGo.<List<Map<String, Object>>>get(Config.TASK_GET_TASKTRACE + "/" + curTask.get("id")).tag(this).execute(jsonCallback);
        } else {
            Toast.makeText(this, "任务参数错误", Toast.LENGTH_SHORT).show();
        }
    }

    private JsonCallback<List<Map<String, Object>>> jsonCallback = new JsonCallback<List<Map<String, Object>>>() {
        @Override
        public void onError(Response<List<Map<String, Object>>> response) {
            super.onError(response);
            showCallbckError(response);
            removeProgress();
            listView.onRefreshComplete();
        }

        @Override
        public void onSuccess(Response<List<Map<String, Object>>> response) {
            removeProgress();
            taskAdapter.updateData(response.body());
            taskAdapter.notifyDataSetChanged();

            listView.onRefreshComplete();
        }
    };

    private PullToRefreshBase.OnRefreshListener refreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase pullToRefreshBase) {
            asyncGetTaskTrace();
        }
    };

    class UnitAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return groupTasks.size();
        }

        @Override
        public Map<String, Object> getItem(int position) {
            return groupTasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(getItem(position).get("id").toString());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.item_spinner, null);
            textView.setText(getItem(position).get("unitName").toString());
            return textView;
        }
    }

    class TaskTraceAdapter extends BaseAdapter {
        private final String[] progressTips = new String[] {"", "任务已完成", "进展较快", "序时推进", "进展缓慢"};
        private final int[] progressStates = new int[] {0, R.mipmap.state_finish, R.mipmap.state_fast, R.mipmap.state_nomal, R.mipmap.state_slow};

        private String[] statusTip =  new String[] {"未审核", "有序推进", "退回重报", "任务完成", "逾期未报"};
        private int[] statusColors =  new int[] {R.drawable.shape_state_start,
                                                R.drawable.shape_state_verify,
                                                R.drawable.shape_state_return,
                                                R.drawable.shape_state_finish,
                                                R.drawable.shape_state_warn};
        private int[] textColors =  new int[] {R.color.green, R.color.teal, R.color.purple, R.color.colorPrimaryDark, R.color.red};

        private List<Map<String, Object>> taskTraces = new ArrayList<>();

        public void updateData(List<Map<String, Object>> data) {
            if (data != null) taskTraces.clear();
            taskTraces.addAll(data);
        }

        @Override
        public int getCount() {
            return taskTraces.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return taskTraces.get(i);
        }

        @Override
        public long getItemId(int i) {
            return Long.parseLong(getItem(i).get("id").toString());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (i == 0) {
                view = getLayoutInflater().inflate(R.layout.item_task3, null);
                Map<String, Object> task = getItem(0);
                ((TextView) view.findViewById(R.id.tv_title)).setText(task.get("name").toString());
                ((TextView) view.findViewById(R.id.tv_plan)).setText(task.get("plan").toString());
            } else {
                view = getLayoutInflater().inflate(R.layout.item_trace, null);
                Map<String, Object> trace = getItem(i);

                //0：正常的上报工作，1：系统自动生成督促，2：督查主动催报，4：领导批示
                int type = Integer.parseInt(trace.get("type").toString());
                String[] titles = new String[] {"任务报送", "系统催报", "督查催报", "任务领取", "领导批示"};
                //status:[0：进行中（默认0），1：已审核，2：退回，3：完成， 4：过期未提报]
                int status = Integer.parseInt(trace.get("status").toString());
                String[] stateTtiles = new String[] {"任务报送", "任务审核", "任务退回", "任务完成", "任务逾期"};

                String section = titles[type];
                if (type == 0) {
                    section = stateTtiles[status];
                }
                ((TextView) view.findViewById(R.id.tv_section)).setText(section);
                ((TextView) view.findViewById(R.id.tv_time)).setText(trace.get("createtime").toString());
                ((TextView) view.findViewById(R.id.tv_trace)).setText(trace.get("content").toString());

                AvatarImageView imageView = view.findViewById(R.id.lv_logo);

                String logo = trace.get("logo").toString();

                if (TextUtils.isEmpty(logo)) {
                    String unitName = trace.get("unitName").toString();
                    if (unitName.length() == 0) {
                        imageView.setImageResource(R.mipmap.icon_system);
                    } else {
                        imageView.setTextAndColor(unitName.length() > 0 ? unitName.substring(0, 1) : "无", Color.parseColor("#dcdcdc"));
                    }
                } else {
                    Glide.with(TaskTraceActivity.this)
                            .load(Config.ATTACHMENT + trace.get("logo"))
                            .centerCrop()
                            .crossFade()
                            .into(imageView);
                }

                ((TextView) view.findViewById(R.id.tv_address)).setText(trace.get("address").toString());
                view.findViewById(R.id.layout_adrress).setVisibility(trace.get("address").toString().isEmpty() ? View.GONE : View.VISIBLE);

                bindAttchementView(trace, view);

                //报送的任务，设置status状态效果
                if (type == 0) {
                    ((TextView) view.findViewById(R.id.tv_state)).setBackgroundResource(statusColors[status]);
                    ((TextView) view.findViewById(R.id.tv_stateinfo)).setText(statusTip[status]);
                    ((TextView) view.findViewById(R.id.tv_stateinfo)).setTextColor(getResources().getColor(textColors[status]));

                    //审核的trace，设置审核状态
                    if (status == 1) {
                        ((TextView) view.findViewById(R.id.tv_stateinfo)).setText(progressTips[Integer.parseInt(trace.get("progress").toString())]);
                    }

                } else {
                    view.findViewById(R.id.layout_state).setVisibility(view.GONE);
                }
                if (i == getCount() - 1) {
                    view.findViewById(R.id.tv_vertical).setVisibility(View.INVISIBLE);
                }

            }
            return view;
        }

        private void bindAttchementView(Map<String, Object> trace, View view) {
            GridView gridView = view.findViewById(R.id.gv_attachment);
            if (!trace.get("attachment").toString().trim().isEmpty()) {
                List<String> attachments = Arrays.asList(trace.get("attachment").toString().split(","));
                gridView.setAdapter(new AttachmentAdapter(TaskTraceActivity.this, attachments, 80));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(TaskTraceActivity.this, TransitionActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TaskTraceActivity.this, view, "transition");
                        intent.putExtra("url", Config.ATTACHMENT + "/" + adapterView.getAdapter().getItem(i));
                        startActivity(intent, options.toBundle());
                    }
                });
            } else {
                gridView.setVisibility(View.GONE);
            }
        }
    }
}
