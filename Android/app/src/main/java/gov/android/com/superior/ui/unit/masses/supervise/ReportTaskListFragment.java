package gov.android.com.superior.ui.unit.masses.supervise;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.holder.PersonUnitTaskViewHolder;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.masses.TaskInformActivity;

/**
 * A simple {@link Fragment} subclass.
 * 群众端：政府工作报告
 */
public class ReportTaskListFragment extends BaseRecylceFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestUnitTasks();
    }

    private void requestUnitTasks() {
        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_BY_CATEGORY + "/" + 1).tag(this).execute(getJsonArrayCallback(HttpUrl.TASK_LIST_BY_CATEGORY));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }


    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new PersonUnitTaskViewHolder(getActivity(), parent); //new PersonUnitTaskViewHolder();
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        JSONObject task = getRecyclerAdapter().getJsonObject(position);
        Intent intent = new Intent(getActivity(), TaskInformActivity.class);
        intent.putExtra("taskId", task.getIntValue("id"));
        if (task.containsKey("unitTasks") && task.getJSONArray("unitTasks").size() == 1) {
            intent.putExtra("unitTaskId", task.getJSONArray("unitTasks").getJSONObject(0).getIntValue("id"));
        }
        startActivity(intent);
    }
}
