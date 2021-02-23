package gov.android.com.superior.ui.unit.dashboard.tasks;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseRecylceActivity;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.entity.User;
import gov.android.com.superior.holder.UnitTaskViewHolder;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.dashboard.TaskDetailActivity;

/**
 * 部门未领取任务列表
 */
public class UnAcceptTasksActivity extends BaseRecylceActivity {

    private int category;

    @Override
    public void onInitParams() {
        category = getIntent().getIntExtra("category", 0);
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("未领取事项");
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestUnitStayCompleteTasks();
    }

    private void requestUnitStayCompleteTasks() {
        OkGo.<List<JSONObject>>get(HttpUrl.UNIT_UN_ACCEPT_TASK_LIST + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(getJsonArrayCallback(HttpUrl.UNIT_TASK_LIST_FOR_STAY_COMPLETE));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }

    @Override
    protected EmptyEntity getEmptyEntry() {
        return null;
    }

    @Override
    public BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new UnitTaskViewHolder(this, parent, category);
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        JSONObject task = getRecyclerAdapter().getJsonObject(position);
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("taskId", task.getIntValue("id"));
        if (task.containsKey("unitTasks") && task.getJSONArray("unitTasks").size() == 1) {
            intent.putExtra("unitTaskId", task.getJSONArray("unitTasks").getJSONObject(0).getIntValue("id"));
        }
        intent.putExtra("category", task.getIntValue("category"));
        startActivity(intent);
    }
}
