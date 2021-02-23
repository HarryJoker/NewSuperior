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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lid.lib.LabelTextView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
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
import gov.android.com.superior.task.list.MuliteTaskFragment;
import gov.android.com.superior.task.list.TaskListActivity;
import gov.android.com.superior.task.list.UnitTaskAdapter;
import gov.android.com.superior.task.list.UnitTaskFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UnitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnitFragment extends BaseFragment {

    private SmartRefreshLayout refreshlayout;

    private AvatarImageView lvLogo;
    private TextView tvUnit;
    private TextView tvName;

    private TextView tvSumCount;
    private TextView tvOverdueCount;
    private TextView tvBackCount;
    private TextView tvSlowCount;

    private TextView tvSlow;
    private TextView tvNomal;
    private TextView tvFast;
    private TextView tvFinish;

    private RecyclerView rc_unit_task;

    private TextView tvUpdate;

    private TextView tvVerify;

    private int category = 0;

    public static UnitFragment newInstance(int category) {
        UnitFragment fragment = new UnitFragment();
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
        return inflater.inflate(R.layout.fragment_unit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshlayout = getView().findViewById(R.id.refreshlayout);
        refreshlayout.setOnRefreshListener(mOnRefreshListener);

        lvLogo = getView().findViewById(R.id.lv_logo);
        tvUnit = getView().findViewById(R.id.tv_unit);
        tvName = getView().findViewById(R.id.tv_name);
        tvSumCount = getView().findViewById(R.id.tv_sum_count);
        tvOverdueCount = getView().findViewById(R.id.tv_overdue_count);
        tvBackCount = getView().findViewById(R.id.tv_back_count);
        tvSlowCount = getView().findViewById(R.id.tv_slow_count);
        tvSlow = getView().findViewById(R.id.tv_slow);
        tvNomal = getView().findViewById(R.id.tv_nomal);
        tvFast = getView().findViewById(R.id.tv_fast);
        tvFinish = getView().findViewById(R.id.tv_finish);
//        rc_sign_task = getView().findViewById(R.id.rc_sign_task);
        rc_unit_task = getView().findViewById(R.id.rc_unit_task);

        tvUpdate = getView().findViewById(R.id.tv_update);
        tvUpdate.setOnClickListener(updateClick);

        tvVerify = getView().findViewById(R.id.tv_veirfy);
        tvVerify.setOnClickListener(verifyClick);

        rc_unit_task.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshUnit();

        refreshlayout.autoRefresh();
    }

    private void intentTaskList(int status) {
        Intent intent = new Intent(getActivity(), TaskListActivity.class);
        intent.putExtra(MuliteTaskFragment.KEY_CATEGORY, category);
        intent.putExtra(MuliteTaskFragment.KEY_TYPE, status == 0 ? UnitTaskFragment.TASK_LIST_TYPE_UNINT_UPDATE : UnitTaskFragment.TASK_LIST_TYPE_UNIT_VERIFY);
        intent.putExtra(MuliteTaskFragment.KEY_UNIT, User.getInstance().getUnitId());
        intent.putExtra(MuliteTaskFragment.KEY_STATUS, status);
        String title = StatusConfig.categroyNames[category];
        title += status == 0 ? "(待完善报送事项)" : "(待手签报送事项)";
        intent.putExtra("title",  title);
        startActivity(intent);
    }

    private View.OnClickListener updateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intentTaskList(0);
        }
    };

    private View.OnClickListener verifyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intentTaskList(1);
        }
    };

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            requestSummary();
        }
    };

    private void refreshUnit() {
        Glide.with(this).load(Config.ATTACHMENT + User.getInstance().getUnitLogo()).into(lvLogo);
        tvName.setText(User.getInstance().get("name").toString());
        tvUnit.setText(User.getInstance().getUnitName());
    }

    private void requestSummary() {
        OkGo.<JSONObject>get(Config.TASK_SUMMARY_BY_UNIT + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(summaryCallback);
    }

    private JsonObjectCallBack<JSONObject> summaryCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            refreshSummary(response.body());
//            requestContentUnitTasks();
            requestUnitTasks();
        }

        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            refreshlayout.finishRefresh(true);
        }
    };

    private void refreshSummary(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.size() == 0) return;
        JSONObject taskCount = jsonObject.getJSONObject("taskCount");
        JSONObject traceCount = jsonObject.getJSONObject("traceCount");

        tvSumCount.setText(taskCount.getString("taskCount"));
        tvOverdueCount.setText(traceCount.getString("overdueCount"));
        tvBackCount.setText(traceCount.getString("backCount"));
        tvSlowCount.setText(traceCount.getString("slowCount"));

        tvSlow.setText("缓慢 " + taskCount.getString("slowCount"));
        tvNomal.setText("正常 " + taskCount.getString("nomalCount"));
        tvFast.setText("较快 " +taskCount.getString("fastCount"));
        tvFinish.setText("完成 " + taskCount.getString("doneCount"));

        JSONObject unitContentCount = jsonObject.getJSONObject("unitContentCount");
        if (unitContentCount != null && unitContentCount.size() > 0) {
            tvUpdate.setText("待完善报送事项(" + unitContentCount.getString("modifyCount") + "项)");
            tvVerify.setText("待手签报送事项(" + unitContentCount.getString("verifyCount") + "项)");
        }
    }

    private void requestUnitTasks() {
        OkGo.<JSONArray>get(Config.TASK_LIST_BY_UNIT + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(unitTasksCallback);
    }

    private JsonObjectCallBack<JSONArray> unitTasksCallback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {
            rc_unit_task.setAdapter(new UnitTaskAdapter(getActivity(), UnitTaskFragment.TASK_LIST_TYPE_ALL, response.body()));
            refreshlayout.finishRefresh(true);
        }

        @Override
        public void onError(Response<JSONArray> response) {
            super.onError(response);
            refreshlayout.finishRefresh(true);
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
