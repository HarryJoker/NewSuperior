package gov.android.com.superior.ui.unit.dashboard.tasks;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.holder.UnitTaskViewHolder;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.dashboard.TaskDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnitTaskListFragment extends BaseRecylceFragment {

    public static final String KEY_TYPE         = "listType";
    public static final String KEY_CATEGORY     = "category";
    public static final String KEY_PART_UNIT    = "leaderUnitId";
    public static final String KEY_STATUS       = "status";
    public static final String KEY_MONTH        = "month";


    public static final int TASK_LIST_TYPE_ALL              = 0x01;     //全部列表
    public static final int TASK_LIST_TYPE_PART             = 0x02;     //分管列表
    public static final int TASK_LIST_TYPE_REPORT           = 0x03;     //上报列表
    public static final int TASK_LIST_TYPE_STATUS           = 0x04;     //根据状态（缓慢，正常，退回，完成）


    private int category;

    private int listType = 0;

    private int unitId = 0;

    private int status = 0;

    private int month = 0;

    public static UnitTaskListFragment newInstance(int category, int listType, int unitId,int month, int status) {
        UnitTaskListFragment fragment = new UnitTaskListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_CATEGORY, category);
        args.putInt(KEY_TYPE, listType);
        args.putInt(KEY_PART_UNIT, unitId);
        args.putInt(KEY_STATUS, status);
        args.putInt(KEY_MONTH, month);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt(KEY_CATEGORY, 0);
            listType = getArguments().getInt(KEY_TYPE, 0);
            unitId = getArguments().getInt(KEY_PART_UNIT, 0);
            month = getArguments().getInt(KEY_MONTH, 0);
            status = getArguments().getInt(KEY_STATUS, 0);
            month = getArguments().getInt(KEY_MONTH, 0);

            Logger.d("MuilteTaskListFragment Params,  category:" +category +", type:" + listType + ", unitId:" + unitId + ", month:" + month + ", status:" + status);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new UnitTaskViewHolder(getActivity(), parent, category);
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        JSONObject task = getRecyclerAdapter().getJsonObject(position);
        Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
        intent.putExtra("taskId", task.getIntValue("id"));
        if (task.containsKey("unitTasks") && task.getJSONArray("unitTasks").size() == 1) {
            intent.putExtra("unitTaskId", task.getJSONArray("unitTasks").getJSONObject(0).getIntValue("id"));
        }
        intent.putExtra("category", task.getIntValue("category"));
        startActivity(intent);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (listType == TASK_LIST_TYPE_ALL) {
            requestAllTasksByCategory();
        }
        if (listType == TASK_LIST_TYPE_PART) {
            requestAllTasksByLeaderUnit();
        }
        if (listType == TASK_LIST_TYPE_REPORT) {
            requestReportTasksByLeaderUnit();
        }
        if (listType == TASK_LIST_TYPE_STATUS) {
            requestTasksByStatus();
        }
    }

    private void requestAllTasksByCategory() {
        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_BY_CATEGORY + "/" + category).tag(this).execute(getJsonArrayCallback(HttpUrl.TASK_LIST_BY_CATEGORY));
    }

    private void requestAllTasksByLeaderUnit() {
        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_BY_LEADER_UNIT + "/" + category + "/" + unitId).tag(this).execute(getJsonArrayCallback(HttpUrl.TASK_LIST_BY_LEADER_UNIT));
    }

    private void requestReportTasksByLeaderUnit() {
        OkGo.<List<JSONObject>>get(HttpUrl.REPORT_LIST_BY_LEADER_UNIT + "/" + category + "/" + unitId).tag(this).execute(getJsonArrayCallback(HttpUrl.REPORT_LIST_BY_LEADER_UNIT));
    }

    private void requestTasksByStatus() {
        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_BY_STATUS_AND_MONTH + "/" + category + "/" + unitId + "/" + status + "/" + month).tag(this).execute(getJsonArrayCallback(HttpUrl.TASK_LIST_BY_STATUS_AND_MONTH));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }
}
