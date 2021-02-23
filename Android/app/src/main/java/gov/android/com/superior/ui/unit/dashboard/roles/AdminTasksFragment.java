package gov.android.com.superior.ui.unit.dashboard.roles;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.callback.JsonObjectCallBack;
import com.first.orient.base.fragment.BaseFragment;
import com.first.orient.base.utils.JokerLog;
import com.lid.lib.LabelTextView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.dashboard.TaskDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 * 督查角色或县长角色的任务列表（一对多可折叠式）
 */
public class AdminTasksFragment extends BaseFragment implements OnRefreshListener {

    private int category = 0;

    private ExpandableListView mExpandableListView;

    private SmartRefreshLayout mSmartRefreshLayout;

    private TaskListAdapter mTaskListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt("category", 0);
        }
    }

    public static AdminTasksFragment newInstance(int category) {
        AdminTasksFragment fragment = new AdminTasksFragment();
        Bundle args = new Bundle();
        args.putInt("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.fragment_admin_tasks, container, false);
    }

    @Override
    public void updateBundle(Bundle bundle) {
        if (bundle != null) {
            category = bundle.getInt("category", 0);
        }
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout = getView().findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mExpandableListView = getView().findViewById(R.id.ex_listview);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setOnChildClickListener(mChildClickListener);
        mExpandableListView.setOnGroupClickListener(mGroupClickListener);
        mExpandableListView.setAdapter(mTaskListAdapter = new TaskListAdapter());
        mSmartRefreshLayout.autoRefresh();
    }

    private ExpandableListView.OnGroupClickListener mGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            JokerLog.d("parent click:" + groupPosition + ", " + mTaskListAdapter.getGroup(groupPosition).toJSONString());
            JSONObject task = mTaskListAdapter.getGroup(groupPosition);
            JSONObject unitTask = task.containsKey("unitTasks") && task.getJSONArray("unitTasks").size() == 1 ? task.getJSONArray("unitTasks").getJSONObject(0) : null;
            intentTaskDetail(task, unitTask);
            return false;
        }
    };

    private ExpandableListView.OnChildClickListener mChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            JokerLog.d("child click:" + childPosition + ", " + mTaskListAdapter.getChild(groupPosition, childPosition).toJSONString());
            JSONObject task = mTaskListAdapter.getGroup(groupPosition);
            JSONObject unitTask = mTaskListAdapter.getChild(groupPosition, childPosition);
            intentTaskDetail(task, unitTask);
            return false;
        }
    };

    private void intentTaskDetail(JSONObject task, JSONObject unitTask) {
        if (task == null || task.size() == 0 || unitTask == null || unitTask.size() == 0) return;
        Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
        intent.putExtra("taskId", task.getIntValue("id"));
        intent.putExtra("unitTaskId", unitTask.getIntValue("id"));
        intent.putExtra("category", task.getIntValue("category"));
        startActivity(intent);
    }


//
//    @Override
//    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
//        return new UnitTaskViewHolder(getActivity(), parent, category);
//    }
//    @Override
//    public void onItemClick(View view, int position, JSONObject data) {
//        Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
//        intent.putExtra("taskId", data.getIntValue("id"));
//        if (data.containsKey("unitTasks") && data.getJSONArray("unitTasks").size() >= 1) {
//            intent.putExtra("unitTaskId", data.getJSONArray("unitTasks").getJSONObject(0).getIntValue("id"));
//        }
//        intent.putExtra("category", data.getIntValue("category"));
//        startActivity(intent);
//    }

    private void requestUnitTasks() {
        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_BY_CATEGORY + "/" + category).tag(this).execute(new JsonObjectCallBack<List<JSONObject>>() {
            @Override
            public void onSuccess(Response<List<JSONObject>> response) {
                mSmartRefreshLayout.finishRefresh();
                mTaskListAdapter.setmTasks(response.body());
            }

            @Override
            public void onError(Response<List<JSONObject>> response) {
                super.onError(response);
                mSmartRefreshLayout.finishRefresh();
                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestUnitTasks();
    }

    class TaskListAdapter extends BaseExpandableListAdapter {

        List<JSONObject> mTasks = new ArrayList<>();

        public TaskListAdapter() {
        }

        public TaskListAdapter(List<JSONObject> tasks) {
            mTasks.clear();
            mTasks.addAll(tasks);
        }

        public void setmTasks(List<JSONObject> tasks) {
            mTasks.clear();
            if (tasks != null) {
                mTasks.addAll(tasks);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {//返回第一级List长度
            return mTasks.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {//返回指定groupPosition的第二级List长度
            JSONArray childs = mTasks.get(groupPosition).getJSONArray("unitTasks");
            return childs == null || childs.size() <=  1 ? 0 : childs.size();
        }

        @Override
        public JSONObject getGroup(int groupPosition) {//返回一级List里的内容
            return mTasks.get(groupPosition);
        }

        @Override
        public JSONObject getChild(int groupPosition, int childPosition) {//返回二级List的内容
            JSONArray childs = mTasks.get(groupPosition).getJSONArray("unitTasks");
            return childs == null || childPosition >= childs.size() ? null : childs.getJSONObject(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {//返回一级View的id 保证id唯一
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {//返回二级View的id 保证id唯一
            return groupPosition + childPosition;
        }

        /**
         * 指示在对基础数据进行更改时子ID和组ID是否稳定
         *
         * @return
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }



        /**
         * 返回一级父View
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            JokerLog.d("groupView  position:" + groupPosition + ", isExpand:" + isExpanded);
            JSONArray childs = mTasks.get(groupPosition).getJSONArray("unitTasks");
            if (childs != null && childs.size() > 1) {
                return makeExpandGroupView(groupPosition, isExpanded, convertView, parent);
            } else {
                return makeUnExpandGroupView(groupPosition, convertView, parent);
            }
        }

        private View makeUnExpandGroupView(int groupPosition, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_task_parent_unexpand, parent, false);
            ImageView ivStatus = convertView.findViewById(R.id.iv_status);
            TextView tvTitleLabel = convertView.findViewById(R.id.tv_title_label);
            TextView tvTitle = convertView.findViewById(R.id.tv_title);
            TextView tvContentLabel = convertView.findViewById(R.id.tv_content_label);
            TextView tvContent = convertView.findViewById(R.id.tv_content);
            TextView tvUnit = convertView.findViewById(R.id.tv_unit);
            TextView tvProgress = convertView.findViewById(R.id.tv_progress);
            ProgressBar pbProgress = convertView.findViewById(R.id.pb_progress);
            LabelTextView tvLabel = convertView.findViewById(R.id.tv_label);

            if (gov.android.com.superior.config.Config.labelMetas.containsKey(category)) {
                tvTitleLabel.setText(gov.android.com.superior.config.Config.labelMetas.get(category)[1]);
                tvContentLabel.setText(gov.android.com.superior.config.Config.labelMetas.get(category)[2]);
            }
            JSONObject task = getGroup(groupPosition);
            tvTitle.setText(task.getString("title"));
            tvContent.setText(task.getString("content"));
            JSONArray unitTasks = task.getJSONArray("unitTasks");
            if (unitTasks.size() == 1) {
                JSONObject unitTask = unitTasks.getJSONObject(0);
                tvUnit.setText(unitTask.getString("unitName"));
                tvProgress.setText(unitTask.getString("verifyProgress") + "%");
                pbProgress.setProgress(unitTask.getIntValue("verifyProgress"));
                String label = gov.android.com.superior.config.Config.STATUS.get(unitTask.getString("status"));
                label = label == null ? "未识别" : label;
                tvLabel.setLabelText(label);

                if (category == 1) {
                    ivStatus.setVisibility(View.VISIBLE);
                    ivStatus.setImageResource(gov.android.com.superior.config.Config.lightStatus.containsKey(unitTask.getString("progressStatus")) ? gov.android.com.superior.config.Config.lightStatus.get(unitTask.getString("progressStatus")) : R.mipmap.icon_light_yellow);
                }
            }
            return convertView;
        }

        private View makeExpandGroupView(int groupPosition, boolean isExpand, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_task_parent_expand, parent, false);
            TextView tvTitleLabel = convertView.findViewById(R.id.tv_title_label);
            TextView tvTitle = convertView.findViewById(R.id.tv_title);
            TextView tvContentLabel = convertView.findViewById(R.id.tv_content_label);
            TextView tvContent = convertView.findViewById(R.id.tv_content);
            ImageView ivIndicator = convertView.findViewById(R.id.iv_indicator);
            ivIndicator.setSelected(isExpand);
            if (gov.android.com.superior.config.Config.labelMetas.containsKey(category)) {
                tvTitleLabel.setText(gov.android.com.superior.config.Config.labelMetas.get(category)[1]);
                tvContentLabel.setText(gov.android.com.superior.config.Config.labelMetas.get(category)[2]);
            }
            JSONObject task = getGroup(groupPosition);
            tvTitle.setText(task.getString("title"));
            tvContent.setText(task.getString("content"));
            return convertView;
        }


        /**
         * 返回二级子View
         */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_task_child, parent, false);
            ImageView ivStatus = convertView.findViewById(R.id.iv_status);
            TextView tvUnit = convertView.findViewById(R.id.tv_unit);
            TextView tvProgress = convertView.findViewById(R.id.tv_progress);
            ProgressBar pbProgress = convertView.findViewById(R.id.pb_progress);
            LabelTextView tvLabel = convertView.findViewById(R.id.tv_label);

            JSONObject unitTask = getChild(groupPosition, childPosition);
            tvUnit.setText(unitTask.getString("unitName"));
            tvProgress.setText(unitTask.getString("verifyProgress") + "%");
            pbProgress.setProgress(unitTask.getIntValue("verifyProgress"));
            String label = gov.android.com.superior.config.Config.STATUS.get(unitTask.getString("status"));
            label = label == null ? "未识别" : label;
            JokerLog.d(label);
            tvLabel.setLabelText(label);
            if (category == 1) {
                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(gov.android.com.superior.config.Config.lightStatus.containsKey(unitTask.getString("progressStatus")) ? gov.android.com.superior.config.Config.lightStatus.get(unitTask.getString("progressStatus")) : R.mipmap.icon_light_yellow);
            }

            return convertView;
        }

        /**
         * 指定位置的子项是否可选
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


}
