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
import gov.android.com.superior.holder.UnitStayTaskViewHolder;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.dashboard.trace.TraceContentActivity;
import gov.android.com.superior.ui.unit.dashboard.trace.TraceUnitStayActivity;

public class UnitStayTasksActivity extends BaseRecylceActivity {
    public static final int UNIT_STAY_SIGN_TYPE     = 0X00F1;
    public static final int UNIT_STAY_COMPLETE_TYPE = 0X00F2;

    private int unitStayType = 0;
    private int category = 0;

    @Override
    public void onInitParams() {
        category = getIntent().getIntExtra("category", 0);
        unitStayType = getIntent().getIntExtra("unitStayType", 0);
        if (unitStayType == 0) {
            showError("数据错误");
            finish();
        }
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        if (unitStayType == UNIT_STAY_COMPLETE_TYPE) {
            barTitle.setText("待完善报送事项");
        }
        if (unitStayType == UNIT_STAY_SIGN_TYPE) {
            barTitle.setText("待手签报送事项");
        }
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public boolean isRefreshEnable() {
        return true;
    }

    @Override
    public void onBusiness() {
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (unitStayType == UNIT_STAY_COMPLETE_TYPE) {
            requestUnitStayCompleteTasks();
        }
        if (unitStayType == UNIT_STAY_SIGN_TYPE) {
            requestUnitStaySignTasks();
        }
    }

    private void requestUnitStaySignTasks() {
        OkGo.<List<JSONObject>>get(HttpUrl.UNIT_TASK_LIST_FOR_STAY_SIGN + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(getJsonArrayCallback(HttpUrl.UNIT_TASK_LIST_FOR_STAY_SIGN));
    }

    private void requestUnitStayCompleteTasks() {
        OkGo.<List<JSONObject>>get(HttpUrl.UNIT_TASK_LIST_FOR_STAY_COMPLETE + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(getJsonArrayCallback(HttpUrl.UNIT_TASK_LIST_FOR_STAY_COMPLETE));
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
        return new UnitStayTaskViewHolder(this, parent, category, unitStayType);
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        JSONObject task = getRecyclerAdapter().getJsonObject(position);
        if (!task.containsKey("unitTasks") || task.getJSONArray("unitTasks").size() != 1) {
            showWarnTip("该事项错误，请联系管理员");
            return;
        }
        JSONObject unitTask = task.getJSONArray("unitTasks").getJSONObject(0);
        if (unitStayType == UNIT_STAY_COMPLETE_TYPE) {
            Intent intent = new Intent(this, TraceContentActivity.class);
            intent.putExtra("unitTaskId", unitTask.getIntValue("id"));
            intent.putExtra("category", category);
            startActivityForResult(intent, 0);
        }
        if (unitStayType == UNIT_STAY_SIGN_TYPE) {
            if (User.getInstance().getUserRule() == 1) {
                Intent intent = new Intent(this, TraceUnitStayActivity.class);
                intent.putExtra("unitTaskId", unitTask.getIntValue("id"));
                startActivityForResult(intent, 0);
            } else {
                showWarnTip("您没有权限手签审核");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mSmartRefreshLayout.autoRefresh();
        }
    }
}
