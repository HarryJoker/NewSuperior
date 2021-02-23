package gov.android.com.superior.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.flyco.roundview.RoundTextView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.task.StatusConfig;
import gov.android.com.superior.task.list.ExpandTaskAdapter;
import gov.android.com.superior.task.list.MuliteTaskAdapter;
import gov.android.com.superior.task.list.MuliteTaskFragment;
import gov.android.com.superior.task.list.TaskListActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link LeaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderFragment extends BaseFragment {

    private int category = 0;

    private SmartRefreshLayout refreshlayout;

    private TaskSummaryView mTaskSummaryView;

    private TaskMonthView mTaskMonthView;

//    private RecyclerView rcTasks;

    private RoundTextView tvMySelft;

    private RoundTextView tvReportTask;

    public static LeaderFragment newInstance(int category) {
        LeaderFragment fragment =  new LeaderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category", category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt("category", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leader, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshlayout = (SmartRefreshLayout)getView().findViewById(R.id.refreshlayout);
        refreshlayout.setOnRefreshListener(mOnRefreshListener);


        mTaskMonthView = getView().findViewById(R.id.tmv_summary);

        mTaskMonthView.setCategory(category);

        mTaskSummaryView = getView().findViewById(R.id.tv_summary);

//        rcTasks = getView().findViewById(R.id.rc_tasks);

//        rcTasks.setLayoutManager(new LinearLayoutManager(getActivity()));

        tvMySelft = getView().findViewById(R.id.tv_my_selft);
        tvMySelft.setOnClickListener(myTaskClick);

        tvReportTask = getView().findViewById(R.id.tv_report_task);
        tvReportTask.setOnClickListener(reportTaskClick);

        refreshlayout.autoRefresh();
    }

    private View.OnClickListener reportTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), TaskListActivity.class);
            intent.putExtra(MuliteTaskFragment.KEY_TYPE, MuliteTaskFragment.TASK_LIST_TYPE_REPORT);
            intent.putExtra(MuliteTaskFragment.KEY_CATEGORY, category);
            intent.putExtra(MuliteTaskFragment.KEY_UNIT, User.getInstance().getUnitId());
            intent.putExtra("title", StatusConfig.categroyNames[category] +"上报事项");
            startActivity(intent);
        }
    };

    private View.OnClickListener myTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), TaskListActivity.class);
            intent.putExtra(MuliteTaskFragment.KEY_TYPE, MuliteTaskFragment.TASK_LIST_TYPE_UNIT);
            intent.putExtra(MuliteTaskFragment.KEY_CATEGORY, category);
            intent.putExtra(MuliteTaskFragment.KEY_UNIT, User.getInstance().getUnitId());
            intent.putExtra("title", StatusConfig.categroyNames[category] + "(分管事项)");
            startActivity(intent);
        }
    };

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            requestInfo();
        }
    };


    private void requestInfo() {
        OkGo.<JSONObject>get(Config.LEADER_TASK_ALL_INFO_BY_LEADER + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(summaryCallback);
    }

    private JsonObjectCallBack<JSONObject> summaryCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            refreshlayout.finishRefresh(true);

            if (response.body() == null) return;

            if (response.body().containsKey("taskSummary")) {
                JSONObject taskSummary = response.body().getJSONObject("taskSummary");
                JSONObject tasksSummary = taskSummary.getJSONObject("taskSummary");
                JSONObject traceSummary = taskSummary.getJSONObject("traceSummary");
                mTaskSummaryView.refreshTaskSummary(tasksSummary, traceSummary);

                tvMySelft.setText("分管任务(" + taskSummary.getJSONObject("taskSummary").getString("taskCount") + "项)");

                tvReportTask.setText("上报任务(" + taskSummary.getJSONObject("reportSummary").getString("taskCount") + "项)");
            }

            if (response.body().containsKey("monthSummary")) {
                JSONObject monthSummary = response.body().getJSONObject("monthSummary");
                JSONObject tasksSummary = monthSummary.getJSONObject("taskSummary");
                JSONObject traceSummary = monthSummary.getJSONObject("traceSummary");
                mTaskMonthView.refreshMonthTaskSummary(tasksSummary, traceSummary);
            }



//            JSONArray tasks = response.body().getJSONArray("tasks");
//
//            if (tasks != null && tasks.size() > 0) {
//                if (category == 1) {
//                    rcTasks.setAdapter(new MuliteTaskAdapter(getActivity(), tasks));
//                } else {
//                    rcTasks.setAdapter(new ExpandTaskAdapter(getActivity(), category, tasks));
//                }
//            }
        }

        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            refreshlayout.finishRefresh(true);
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
