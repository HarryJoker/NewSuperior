package gov.android.com.superior.ui.unit.dashboard.roles;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import java.util.List;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.holder.LeaderMonthSummaryViewHolder;
import gov.android.com.superior.holder.LeaderSummaryViewHolder;
import gov.android.com.superior.holder.LeaderTaskNumSummaryViewHolder;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.tools.DateUtil;
import gov.android.com.superior.ui.unit.dashboard.tasks.TaskMonthActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderTasksFragment extends BaseRecylceFragment {

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
    public void onInitViews() {
        super.onInitViews();
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, 30, Color.TRANSPARENT));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public void updateBundle(Bundle bundle) {
        if (bundle != null) {
            category = bundle.getInt("category", 0);
        }
        mSmartRefreshLayout.autoRefresh();
    }

    private void requestAllLeadersSummary() {
        OkGo.<List<JSONObject>>get(HttpUrl.ALL_LEADERS_SUMMARY + "/" + category + "/" + DateUtil.getNowMonth()).tag(this).execute(getJsonArrayCallback(HttpUrl.ALL_LEADERS_SUMMARY));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }

    @Override
    protected int onItemViewType(int position, JSONObject data) {
        if (data.containsKey("monthTaskSummary")) return 1;
        if (data.containsKey("taskTotalNumSummary")) return 2;
        return 0;
    }

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) return new LeaderMonthSummaryViewHolder(getLayoutInflater().inflate(R.layout.rc_item_leader_month_summary, parent, false));
        if (viewType == 2) return new LeaderTaskNumSummaryViewHolder(getActivity(), parent, category);
        return new LeaderSummaryViewHolder(getLayoutInflater().inflate(R.layout.rc_item_leader_summary, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        int viewType = onItemViewType(position, data);
        if (position == 0) return;
        if (viewType == 1) {
            Intent intent = new Intent(getContext(), TaskMonthActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("month", DateUtil.getNowMonth());
            intent.putExtra("leaderUnitId", User.getInstance().getUnitId());
            intent.putExtra("unit", User.getInstance().getUserUnit().toJSONString());
            getContext().startActivity(intent);
        }

        if (viewType == 0) {
            Intent intent = new Intent(getContext(), TaskMonthActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("month", DateUtil.getNowMonth());
            intent.putExtra("leaderUnitId", data.getJSONObject("unit").getIntValue("id"));
            intent.putExtra("unit", data.getJSONObject("unit").toJSONString());
            getContext().startActivity(intent);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestAllLeadersSummary();
    }
}
