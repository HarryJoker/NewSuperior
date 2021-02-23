package gov.android.com.superior.ui.unit.dashboard.roles;


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
public class AdminTasksFragment_bak extends BaseRecylceFragment {

    private int category = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt("category", 0);
            Logger.d("Category: " + category);
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
        Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
        intent.putExtra("taskId", data.getIntValue("id"));
        if (data.containsKey("unitTasks") && data.getJSONArray("unitTasks").size() >= 1) {
            intent.putExtra("unitTaskId", data.getJSONArray("unitTasks").getJSONObject(0).getIntValue("id"));
        }
        intent.putExtra("category", data.getIntValue("category"));
        startActivity(intent);
    }

    private void requestUnitTasks() {
        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_BY_CATEGORY + "/" + category).tag(this).execute(getJsonArrayCallback(HttpUrl.TASK_LIST_BY_CATEGORY));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestUnitTasks();
    }
}
