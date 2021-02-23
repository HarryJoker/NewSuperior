package gov.android.com.superior.ui.unit.masses.livelihood;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.first.orient.base.utils.DpUtils;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.masses.TaskInformActivity;

/**
 * A simple {@link Fragment} subclass.
 * 完成情况事项列表
 */
public class StatusListFragment extends BaseRecylceFragment {

    @Override
    public void onInitViews() {
        super.onInitViews();
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL, DpUtils.getSmallestScreenWidth(getContext(), R.dimen.dp_0_5), getContext().getResources().getColor(R.color.avatar_default_border)));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout.autoRefresh();
    }

    private void requestPeopleOpinions() {
        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_FOR_PEOPLE).tag(this).execute(getJsonArrayCallback(HttpUrl.TASK_LIST_FOR_PEOPLE));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new OpinionStatusViewHolder(getLayoutInflater().inflate(R.layout.rc_item_people_opinion_stats, parent, false));
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

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestPeopleOpinions();
    }

    class OpinionStatusViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

        TextView tvTitle;
        TextView tvContent;
        TextView tvStatusContent;

        public OpinionStatusViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvStatusContent = (TextView) itemView.findViewById(R.id.tv_status_content);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
            tvTitle.setText(data.getString("title"));
            tvContent.setText(data.getString("content"));
            JSONArray unitTasks = data.getJSONArray("unitTasks");
            if (unitTasks.size() == 1) {
                JSONObject unitTask = unitTasks.getJSONObject(0);
                tvStatusContent.setText(gov.android.com.superior.config.Config.informstatus.get(unitTask.getString("progressStatus")));
            }
        }
    }
}
