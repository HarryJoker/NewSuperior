package gov.android.com.superior.ui.unit.dashboard;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.business.entity.ChannelInfo;
import com.example.zhouwei.library.CustomPopWindow;
import com.first.orient.base.activity.BaseRecylceActivity;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.utils.JokerLog;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.io.Serializable;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.adapter.PopWindowMenuAdapter;
import gov.android.com.superior.adapter.TaskInfoAdapter;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.holder.TraceAttatchmentViewHolder;
import gov.android.com.superior.holder.TraceContentViewHolder;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.dashboard.trace.TraceAcceptActivity;
import gov.android.com.superior.ui.unit.dashboard.trace.TraceContentActivity;
import gov.android.com.superior.ui.unit.dashboard.trace.TraceLeaderActivity;

public class TaskDetailActivity extends BaseRecylceActivity {

    private int taskId = 0;

    private int category = 0;

    private int curUnitTaskId = 0;

    private ChannelInfo channelInfo;

    private JSONObject unitTask;

    private JSONObject taskInfo;

    private RecyclerView rc_units;

    private CustomPopWindow mPopMenuWindow;

    private TaskUnitAdapter mTaskUnitAdapter;

    private TaskInfoAdapter mTaskInfoAdapter;

    private RecyclerView rc_taskInfo;

    private TextView tvArrow;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_task_detail;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("进展详情");
    }

    private void refreshBarFunView() {
        if (User.getInstance().getUserRole() <= 2) {
            getmBarFun().getImageView().setImageBitmap(null);
            getmBarFun().getTextView().setText("批示工作");
        }
        if (User.getInstance().getUserRole() == 3) {
            getmBarFun().getImageView().setImageResource(R.mipmap.ic_handle_more);
        }
        if (User.getInstance().getUserRole() == 4) {
            getmBarFun().getImageView().setImageBitmap(null);
            unitTask = findUnitTask();
            if (unitTask == null) return;
            int status = unitTask.getIntValue("status");
            if (status == 0) {
                getmBarFun().getTextView().setText("领取任务");
            } else {
                getmBarFun().getTextView().setText("报送工作");
            }
        }
    }

    private void showPopListView(View asDropDown){
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_pop_window_handle,null);
        //处理popWindow 显示内容
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 25, getResources().getColor(R.color.colorPrimary)));
        recyclerView.setAdapter(new PopWindowMenuAdapter(this, mMenuClick));
        //创建并显示popWindow
        mPopMenuWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
                .showAsDropDown(asDropDown,0,20);
    }

    private View.OnClickListener mMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mPopMenuWindow != null) {
                mPopMenuWindow.dissmiss();
            }

            if (view.getTag() != null && view.getTag() instanceof PopWindowMenuAdapter.MenuBean) {
                PopWindowMenuAdapter.MenuBean menuBean = (PopWindowMenuAdapter.MenuBean) view.getTag();
                if (menuBean.clazz != null) {
                    Intent intent = new Intent(TaskDetailActivity.this, menuBean.clazz);
                    intent.putExtra("unitTaskId", curUnitTaskId);
                    startActivityForResult(intent, menuBean.requestCode);
                }
            }
        }
    };

    public void taskIntroClick(View v) {
        Intent intent = new Intent(this, TaskIntroActivity.class);
        intent.putExtra("taskId", taskId);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    @Override
    protected void onBarFunClick(View v) {
        super.onBarFunClick(v);
        if (User.getInstance().getUserRole() <= 2) {
            Intent intent = new Intent(this, TraceLeaderActivity.class);
            intent.putExtra("unitTaskId", curUnitTaskId);
            startActivity(intent);
        }
        if (User.getInstance().getUserRole() == 3) {
            showPopListView(v);
        }
        if (User.getInstance().getUserRole() == 4) {
            if (unitTask == null) return;
            int status = unitTask.getIntValue("status");
            if (status == 0) {
                Intent intent = new Intent(this, TraceAcceptActivity.class);
                intent.putExtra("unitTaskId", curUnitTaskId);
                startActivityForResult(intent, TraceAcceptActivity.REQUEST_ACCEPT_TASK);
            } else {

                JSONObject firtTrace = getRecyclerAdapter().getJsonObject(0);

                if (firtTrace.getIntValue("status") == -1) {
                    Toast.makeText(TaskDetailActivity.this, "已报送过领导，请等待领导手签审核", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, TraceContentActivity.class);
                    intent.putExtra("unitTaskId", curUnitTaskId);
                    intent.putExtra("type", 0);
                    intent.putExtra("category", category);
                    startActivityForResult(intent, 0x0001);
                }
            }
        }
    }

    private JSONObject findUnitTask() {
        if (taskInfo == null) return null;
        JSONArray unitTasks = taskInfo.getJSONArray("unitTasks");
        for (int n = 0; n < unitTasks.size(); n++) {
            if (unitTasks.getJSONObject(n).getIntValue("id") == curUnitTaskId) {
                return unitTasks.getJSONObject(n);
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TraceAcceptActivity.REQUEST_ACCEPT_TASK && resultCode == RESULT_OK) {
            if (unitTask != null) {
                unitTask.put("status", 1);
            }
        }

        if (resultCode == RESULT_OK) {
            refreshBarFunView();
            mSmartRefreshLayout.autoRefresh();
        }
    }

    @Override
    public void onInitParams() {
        taskId = getIntent().getIntExtra("taskId", 0);
        curUnitTaskId = getIntent().getIntExtra("unitTaskId", 0);
        category = getIntent().getIntExtra("category", 0);
        Serializable serializable = getIntent().getSerializableExtra("channelInfo");
        if (serializable != null && serializable instanceof ChannelInfo) {
            channelInfo = (ChannelInfo)serializable;
        }

        JokerLog.e("ChannelInfo: " + (channelInfo == null ? " is Null" : JSON.toJSONString(channelInfo)));

        if (taskId == 0) {
            showError("数据错误");
        }
    }

    @Override
    protected void onFindViews() {
        super.onFindViews();
        tvArrow = findViewById(R.id.tv_arrow);
        rc_taskInfo = findViewById(R.id.rc_taskInfo);
        rc_units = findViewById(R.id.rc_units);
    }

    @Override
    public void onInitView() {
        super.onInitView();
        tvArrow.setVisibility(category == 7 ? View.GONE : View.VISIBLE);
        rc_taskInfo.setLayoutManager(new LinearLayoutManager(this));
        rc_units.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        requestTaskDetail();
    }

    @Override
    public boolean isRefreshEnable() {
        return true;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        JokerLog.d("Refresh Traces ..................");
        requestUnitTaskTraces(curUnitTaskId);
    }

    @Override
    protected EmptyEntity getEmptyEntry() {
        return null;
    }

    private void requestTaskDetail() {
        showLoading("加载中");
        OkGo.<JSONObject>get(HttpUrl.TASK_DETAIL + "/" + taskId).tag(this).execute(getJsonObjectCallback(HttpUrl.TASK_DETAIL));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        taskInfo = data;
        if (taskInfo == null) return;
        taskInfo.put("channelInfo", channelInfo);
        refreshBarFunView();
        rc_taskInfo.setAdapter(mTaskInfoAdapter = new TaskInfoAdapter(TaskDetailActivity.this, taskInfo));
        initTaskUnits(taskInfo.getJSONArray("unitTasks"));
        mSmartRefreshLayout.autoRefresh();
    }

    /**
     * 构造多个责任部门列表
     * @param unitTasks
     */
    private void initTaskUnits(JSONArray unitTasks) {
        if (unitTasks == null) return;

        //部门自己的任务
        if (User.getInstance().getUserRole() == 4) {
            rc_units.setVisibility(View.GONE);
            return;
        }

        //单部门任务
        if (unitTasks.size() == 1) {
            rc_units.setVisibility(View.GONE);
            curUnitTaskId = unitTasks.getJSONObject(0).getIntValue("id");
        }

        //多部门任务
        if (unitTasks.size() > 1) {
            rc_units.setAdapter(mTaskUnitAdapter = new TaskUnitAdapter(unitTasks));
        }
    }

    private void requestUnitTaskTraces(int unitTaskId) {
        OkGo.<List<JSONObject>>get(HttpUrl.UNIT_TASK_TRACES + "/"  + unitTaskId + "/" + (User.getInstance().getUserRole() == 4 ? 1 : 0)).tag(this).execute(getJsonArrayCallback(HttpUrl.UNIT_TASK_TRACES));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
        mSmartRefreshLayout.finishRefresh(true);
    }

    public static final int TRACE_TYPE_CONTENT    = 0x00F0;
    public static final int TRACE_TYPE_ATTACHMENT = 0x00F1;

    @Override
    protected int onItemViewType(int position, JSONObject data) {
        return TextUtils.isEmpty(data.getString("attachments")) ? TRACE_TYPE_CONTENT : TRACE_TYPE_ATTACHMENT;
    }

    @Override
    public BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TRACE_TYPE_ATTACHMENT) {
            return new TraceAttatchmentViewHolder(this, parent, getRecyclerAdapter().getItemCount());
        }
        if (viewType == TRACE_TYPE_CONTENT) {
            return new TraceContentViewHolder(this, parent, getRecyclerAdapter().getItemCount());
        }
        return null;
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {

    }

    @Override
    protected void onDestroy() {
        if (mTaskInfoAdapter != null) {
            RecyclerView.ViewHolder viewHolder = rc_taskInfo.findViewHolderForAdapterPosition(0);
            if (viewHolder != null && viewHolder instanceof TaskInfoAdapter.VideoViewHolder) {
                ((TaskInfoAdapter.VideoViewHolder)viewHolder).destroy();
            }
        }
        super.onDestroy();
    }

    class TaskUnitAdapter extends RecyclerView.Adapter<TaskUnitAdapter.TaskUnitViewHolder> {

        private JSONArray mUnitTasks;

        public TaskUnitAdapter(JSONArray array) {
            this.mUnitTasks = array;
        }

        @Override
        public TaskUnitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TaskUnitViewHolder(getLayoutInflater().inflate(R.layout.rc_item_task_unit, parent, false));
        }

        @Override
        public void onBindViewHolder(TaskUnitViewHolder holder, int position) {
            holder.bindView(position);
        }

        @Override
        public int getItemCount() {
            return mUnitTasks == null ? 0 : mUnitTasks.size();
        }

        private View.OnClickListener mTaskUnitClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                if (buttonView instanceof CheckBox) {
                    if (buttonView.getTag() != null && buttonView.getTag() instanceof Integer) {
                        int position = (Integer) buttonView.getTag();
                        JSONObject unitTask = mUnitTasks.getJSONObject(position);
                        curUnitTaskId = unitTask.getIntValue("id");
                        rc_units.getAdapter().notifyDataSetChanged();
                        mSmartRefreshLayout.autoRefresh();
                    }
                }
            }
        };

        class TaskUnitViewHolder extends RecyclerView.ViewHolder {
            CheckBox mCheckBox;
            public TaskUnitViewHolder(View itemView) {
                super(itemView);
                mCheckBox = (CheckBox) itemView;
                mCheckBox.setOnClickListener(mTaskUnitClickListener);
            }

            public void bindView(int posiiton) {
                mCheckBox.setTag(posiiton);
                mCheckBox.setEnabled(true);
                mCheckBox.setText("-----");

                JSONObject unitTask = mUnitTasks.getJSONObject(posiiton);
                mCheckBox.setText(unitTask.getString("unitName"));
                mCheckBox.setChecked(unitTask.getIntValue("id") == curUnitTaskId);
            }
        }
    }
}
