package gov.android.com.superior.home.unit.task;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harry.pulltorefresh.library.PullToRefreshBase;
import com.harry.pulltorefresh.library.PullToRefreshListView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.SpannableStringUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends BaseFragment {

    private int  category = 0;

    private int accept = 0;

    private int leaderId = 0;

    private int unitId = 0;

    private int progress = 0;

    private boolean isDivision = true;

    private boolean isVerify;

    private PullToRefreshListView taskListView;

    private TaskAdapter taskAdapter = new TaskAdapter();

    public TaskListFragment() {

    }

    /**
     * 通过Category查看所有任务
     * @param category
     * @return
     */
    public static TaskListFragment newDCInstance(int category, boolean verfiy) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("category", category);
        args.putBoolean("isVerify", verfiy);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 通过category查看某领导分管的任务
     * @param category
     * @param leaderId
     * @return
     */
    public static TaskListFragment newReporInstance(int category, int leaderId) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("category", category);
        args.putInt("leaderId", leaderId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 通过Category查看某单位（已申领或未申领）的任务
     * @param category
     * @param accept
     * @return
     */
    public static TaskListFragment newUnitInstance(int category, int accept) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("category", category);
        args.putInt("accept", accept);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 通过Category查看领导分管的任务
     * @param category
     * @param unitId
     * @param progress 领导分管工作根据不同进度筛选，progress：0不进行进度查询
     * @return
     */
    public static TaskListFragment newLeaderInstance(int category, int unitId, int progress, boolean isDivision) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("category", category);
        args.putInt("unitId", unitId);
        args.putInt("progress", progress);
        args.putBoolean("isDivision", isDivision);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.unitId = getArguments().getInt("unitId", -1);
        this.accept = getArguments().getInt("accept", -1);
        this.category = getArguments().getInt("category", 0);
        this.leaderId = getArguments().getInt("leaderId", 0);
        this.isVerify = getArguments().getBoolean("isVerify", false);
        this.progress = getArguments().getInt("progress", 0);
        this.isDivision = getArguments().getBoolean("isDivision", true);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        taskListView = view.findViewById(R.id.pull_refresh_listview);
        return view;
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            if (l == 0) return;

            Map<String, Object> task = taskAdapter.getItem(i - 1);

            int aInt = Integer.parseInt(task.get("accept").toString());

            int role = User.getInstance().getUserRole();

            //部门未领取的任务
            if (aInt == 0 && role == 4) {
                Intent intent = new Intent(getActivity(), TaskActivity.class);
                intent.putExtra("taskId", l);
                startActivityForResult(intent, 0x00F2);
            } else  {
                Intent intent = new Intent(getActivity(), TaskTraceActivity.class);
//                intent.putExtra("taskId", l);
                List tasks = taskAdapter.getGroupTasks(i - 1);
                if (tasks != null && tasks.size() == 0) {
                    tasks.add(task);
                }
                intent.putExtra("groupTasks", (Serializable) tasks);
                startActivityForResult(intent, 0x00F4);
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        taskListView.setOnItemClickListener(itemClickListener);
        taskListView.setAdapter(taskAdapter);
        taskListView.setOnRefreshListener(refreshListener);
        taskListView.setRefreshing();
    }

    private void asyncGetAllTask() {
        int role = User.getInstance().getUserRole();
        if (role == 4) {
            OkGo.<List>get(Config.TASK_BY_UNIT_AND_CATEGORY_WITH_ACCEPT + "/" + User.getInstance().get("unitId") + "/" + category + "/" + accept).tag(this).execute(jsonCallback);
        } else if (role == 3) {
            if (isVerify) {
                OkGo.<List>get(Config.TASK_VERIFY_BY_CATEGORY + "/" + category).tag(this).execute(jsonCallback);
            } else {
                OkGo.<List>get(Config.TASK_BY_CATEGORY + "/" + category).tag(this).execute(jsonCallback);
            }
        } else if (role < 3) {
            if (leaderId > 0) {
                //上报给领导的任务
                OkGo.<List>get(Config.TASK_GET_BY_USER_AND_CATEGORY + "/" + leaderId + "/" + category).tag(this).execute(jsonCallback);
            }
            if (unitId > 0){

                if (unitId == 2) {
                    if (!isDivision) {
                        //所有的工作
                        OkGo.<List>get(Config.TASK_GET_OFFICER_TASK_BY_UNIT_AND_CATEGORY + "/" + unitId + "/" + category + "/" + progress).tag(this).execute(jsonCallback);
                    } else {
                        //分管的工作
//                        OkGo.<List>get(Config.TASK_GET_OFFICER_TASK_BY_UNIT_AND_CATEGORY + "/" + unitId + "/" + category ).tag(this).execute(jsonCallback);
                        OkGo.<List>get(Config.TASK_GET_LEADER_TASK_BY_UNIT_AND_CATEGORY + "/" + unitId + "/" + category).tag(this).execute(jsonCallback);
                    }
                } else {
                    //副县长分管的任务
                    OkGo.<List>get(Config.TASK_GET_LEADER_TASK_BY_UNIT_AND_CATEGORY + "/" + unitId + "/" + category).tag(this).execute(jsonCallback);
                }
            }
        }
    }

    private JsonCallback<List> jsonCallback = new JsonCallback<List>() {

        @Override
        public void onError(Response<List> response) {
            super.onError(response);
            taskListView.onRefreshComplete();
        }

        @Override
        public void onSuccess(Response<List> response) {
            List data = response.body();

            taskAdapter.updateTasks(data);

            taskAdapter.notifyDataSetChanged();

            taskListView.onRefreshComplete();
        }
    };

    private PullToRefreshBase.OnRefreshListener refreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase pullToRefreshBase) {
            asyncGetAllTask();
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
                view = getActivity().getLayoutInflater().inflate(R.layout.item_null_tip, null);
                ((TextView)view).setText("暂无任务");
                return view;
            } else {
                if (role == 4) {
                    view = getActivity().getLayoutInflater().inflate(R.layout.item_task, null);
                    ((TextView) view.findViewById(R.id.tv_title)).setText(getItem(i).get("name").toString());
                    ((TextView) view.findViewById(R.id.tv_plan)).setText(getItem(i).get("plan").toString());
                } else {
                    view = getActivity().getLayoutInflater().inflate(R.layout.item_task_ext, null);
                    ((TextView) view.findViewById(R.id.tv_title)).setText(getItem(i).get("name").toString());
                    ((TextView) view.findViewById(R.id.tv_plan)).setText(getItem(i).get("plan").toString());
//                    ((TextView) view.findViewById(R.id.tv_unit)).setText(getItem(i).get("unitName").toString());
                    bindUnit(((TextView) view.findViewById(R.id.tv_unit)), i);
                }

                if (category == 4) {
                    Object object = getItem(i).get("taskLabel");
                    String taskLabel = object == null ? "" : "【" + object.toString() + "】";
                    ((TextView) view.findViewById(R.id.tv_title)).setText(taskLabel + getItem(i).get("name").toString());
                }

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

//        private ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                startActivity(new Intent(DetailActivity.this, VipActivity.class));
//            }
//        };

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

//    class UnitNameClickableSpan extends ClickableSpan {
//        private Map<String, Object>
//
//        public UnitNameClickableSpan(Map<String, Object> groupTask) {
//
//        }
//    }

}
