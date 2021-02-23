package gov.android.com.superior.holder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.flyco.roundview.RoundTextView;

import gov.android.com.superior.R;
import gov.android.com.superior.config.Config;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.ui.unit.dashboard.tasks.UnitTaskListActivity;
import gov.android.com.superior.ui.unit.dashboard.tasks.UnitTaskListFragment;

public class LeaderTaskNumSummaryViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

    private int category = 0;

    RoundTextView tvMySelft;
    RoundTextView tvAllTask;
    RoundTextView tvReportTask;

    public LeaderTaskNumSummaryViewHolder(@NonNull Activity context, @NonNull ViewGroup parent, int category) {
        this(context.getLayoutInflater().inflate(R.layout.rc_item_leader_tasknum_summary, parent, false), category);
    }

    public LeaderTaskNumSummaryViewHolder(View itemView, int category) {
        super(itemView);

        this.category = category;

        tvMySelft = itemView.findViewById(R.id.tv_my_selft);
        tvMySelft.setOnClickListener(myTaskClick);

        tvAllTask = itemView.findViewById(R.id.tv_all_task);
        if (User.getInstance().getUserRole() == 1) {
            tvAllTask.setOnClickListener(allTaskClick);
        } else {
            tvAllTask.setVisibility(View.GONE);
        }

        tvReportTask = itemView.findViewById(R.id.tv_report_task);
        tvReportTask.setOnClickListener(reportTaskClick);
    }

    View.OnClickListener reportTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), UnitTaskListActivity.class);
            intent.putExtra(UnitTaskListFragment.KEY_TYPE, UnitTaskListFragment.TASK_LIST_TYPE_REPORT);
            intent.putExtra(UnitTaskListFragment.KEY_CATEGORY, category);
            intent.putExtra(UnitTaskListFragment.KEY_PART_UNIT, User.getInstance().getUnitId());
            intent.putExtra("title", Config.categoryTitles.get(category) + "(上报的任务)");
            itemView.getContext().startActivity(intent);
        }
    };

    View.OnClickListener myTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), UnitTaskListActivity.class);
            intent.putExtra(UnitTaskListFragment.KEY_TYPE, UnitTaskListFragment.TASK_LIST_TYPE_PART);
            intent.putExtra(UnitTaskListFragment.KEY_CATEGORY, category);
            intent.putExtra(UnitTaskListFragment.KEY_PART_UNIT, User.getInstance().getUnitId());
            intent.putExtra("title", Config.categoryTitles.get(category) + "(分管任务)");
            itemView.getContext().startActivity(intent);
        }
    };

    View.OnClickListener allTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), UnitTaskListActivity.class);
            intent.putExtra(UnitTaskListFragment.KEY_CATEGORY, category);
            intent.putExtra(UnitTaskListFragment.KEY_TYPE, UnitTaskListFragment.TASK_LIST_TYPE_ALL);
//            intent.putExtra(UnitTaskListFragment.KEY_UNIT, User.getInstance().getUnitId());
            intent.putExtra("title", Config.categoryTitles.get(category) + "(所有任务)");
            itemView.getContext().startActivity(intent);
        }
    };

    @Override
    public void bindViewHolder(int position, JSONObject data) {

        JSONObject taskTotalNumSummary = data.getJSONObject("taskTotalNumSummary");
        JSONObject reportSummary = data.getJSONObject("taskReportNumSummary");

        tvMySelft.setText("分管任务(" + taskTotalNumSummary.getString("taskCount") + "项)");
        tvReportTask.setText("上报任务(" + reportSummary.getString("reportCount") + "项)");
    }
}
