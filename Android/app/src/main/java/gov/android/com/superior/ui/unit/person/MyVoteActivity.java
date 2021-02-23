package gov.android.com.superior.ui.unit.person;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseRecylceActivity;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.callback.JsonObjectCallBack;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import gov.android.com.superior.holder.PersonUnitTaskViewHolder;
import gov.android.com.superior.ui.unit.masses.TaskInformActivity;


public class MyVoteActivity extends BaseRecylceActivity {

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("我投票的事项");
    }

    @Override
    public void onInitParams() {

    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestUnitTasks();
    }

    private void requestUnitTasks() {
//        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_BY_UNIT + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(unitTasksCallback);
    }

    private JsonObjectCallBack<List<JSONObject>> unitTasksCallback = new JsonObjectCallBack<List<JSONObject>>() {
        @Override
        public void onSuccess(Response<List<JSONObject>> response) {
            setDataSource(response.body());
            mSmartRefreshLayout.finishRefresh(true);
        }

        @Override
        public void onError(Response<List<JSONObject>> response) {
            super.onError(response);
            mSmartRefreshLayout.finishRefresh(true);
        }
    };

    @Override
    public boolean isRefreshEnable() {
        return true;
    }

    @Override
    protected EmptyEntity getEmptyEntry() {
        return null;
    }

    @Override
    public BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new PersonUnitTaskViewHolder(this, parent);
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        Intent intent = new Intent(this, TaskInformActivity.class);
        JSONObject task = getRecyclerAdapter().getJsonObject(position);
        intent.putExtra("taskId", task.getIntValue("id"));
        if (task.containsKey("unitTasks") && task.getJSONArray("unitTasks").size() == 1) {
            intent.putExtra("unitTaskId", task.getJSONArray("unitTasks").getJSONObject(0).getIntValue("id"));
        }
        startActivity(intent);
    }
}
