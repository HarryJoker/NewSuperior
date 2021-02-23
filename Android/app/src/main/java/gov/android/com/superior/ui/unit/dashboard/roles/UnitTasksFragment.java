package gov.android.com.superior.ui.unit.dashboard.roles;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.android.business.entity.ChannelInfo;
import com.bumptech.glide.Glide;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.first.orient.base.utils.JokerLog;
import com.lzy.okgo.OkGo;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.holder.UnitTaskViewHolder;
import gov.android.com.superior.http.ChannelHelper;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.dashboard.TaskDetailActivity;
import gov.android.com.superior.ui.unit.dashboard.tasks.UnAcceptTasksActivity;
import gov.android.com.superior.ui.unit.dashboard.tasks.UnitStayTasksActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnitTasksFragment extends BaseRecylceFragment {

    private int category = 0;

    private RoundedImageView ivAvatar;
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

    private TextView tvUpdate;
    private TextView tvVerify;
    private TextView tvUnAccept;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt("category", 0);
            Logger.d("Category: " + category);
        }
    }

    @Override
    public void updateBundle(Bundle bundle) {
        if (bundle != null) {
            category = bundle.getInt("category", 0);
        }
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_unit_tasks;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ivAvatar = getView().findViewById(R.id.lv_logo);
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

        tvUpdate = getView().findViewById(R.id.tv_update);
        tvUpdate.setOnClickListener(updateClick);

        tvVerify = getView().findViewById(R.id.tv_veirfy);
        tvVerify.setOnClickListener(verifyClick);

        tvUnAccept = getView().findViewById(R.id.tv_unAccept);
        tvUnAccept.setOnClickListener(unAcceptClick);

        int rule = User.getInstance().getUserRule();
        if (rule == 1) {
            tvUpdate.setVisibility(View.GONE);
            tvUnAccept.setVisibility(View.GONE);
        } else {
            tvVerify.setVisibility(View.GONE);
        }
        refreshUnit();
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestSummary();
    }

    private View.OnClickListener updateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intentUnitStayTasks(UnitStayTasksActivity.UNIT_STAY_COMPLETE_TYPE);
        }
    };

    private View.OnClickListener verifyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intentUnitStayTasks(UnitStayTasksActivity.UNIT_STAY_SIGN_TYPE);
        }
    };

    private View.OnClickListener unAcceptClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), UnAcceptTasksActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        }
    };

    private void intentUnitStayTasks(int unitStayType) {
        Intent intent = new Intent(getActivity(), UnitStayTasksActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("unitStayType", unitStayType);
        startActivity(intent);
    }

    private void refreshUnit() {
        Glide.with(this).load(HttpUrl.ATTACHMENT + User.getInstance().getUnitLogo()).into(ivAvatar);
        tvName.setText(User.getInstance().get("name").toString());
        tvUnit.setText(User.getInstance().getUnitName());
    }

    private void requestSummary() {
        OkGo.<JSONObject>get(HttpUrl.TASK_SUMMARY_BY_UNIT + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(getJsonObjectCallback(HttpUrl.TASK_SUMMARY_BY_UNIT));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        refreshSummary(data);
        requestUnitTasks();
    }

    private void refreshSummary(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.size() == 0) return;
        JSONObject taskSummary = jsonObject.getJSONObject("taskSummary");
        JSONObject traceSummary = jsonObject.getJSONObject("traceSummary");
        JSONObject unitStaySummary = jsonObject.getJSONObject("staySummary");
        JSONObject unAcceptSummary = jsonObject.getJSONObject("unAcceptSummary");

        tvSumCount.setText(taskSummary.getString("taskCount"));
        tvOverdueCount.setText(traceSummary.getString("overdueCount"));
        tvBackCount.setText(traceSummary.getString("backCount"));
        tvSlowCount.setText(traceSummary.getString("slowCount"));

        tvSlow.setText("缓慢 " + taskSummary.getString("slowCount"));
        tvNomal.setText("正常 " + taskSummary.getString("nomalCount"));
        tvFast.setText("较快 " +taskSummary.getString("fastCount"));
        tvFinish.setText("完成 " + taskSummary.getString("doneCount"));

        if (unitStaySummary != null && unitStaySummary.size() > 0) {
            tvUpdate.setText("待完善报送事项(" + unitStaySummary.getString("stayCompleteCount") + "项)");
            tvVerify.setText("待手签报送事项(" + unitStaySummary.getString("staySignCount") + "项)");
            tvUnAccept.setText("待领取事项(" + unAcceptSummary.getString("unAcceptCount") + "项)");
        }
    }

    private void requestUnitTasks() {
        if (category == 7 && (ChannelHelper.mChannelInfos == null || ChannelHelper.mChannelInfos.size() == 0)) {
            //取视频
            ChannelHelper.getInstance().setmChannelInfoCallback(new ChannelHelper.ChannelInfoCallback() {
                @Override
                public void onChannelInfosResult(Map<String, ChannelInfo> channelInfos) {
                    JokerLog.e("result infos:" + JSONObject.toJSONString(channelInfos));
                    if (channelInfos != null) {
                        ChannelHelper.mChannelInfos.putAll(channelInfos);
                    }
                    OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_BY_UNIT + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(getJsonArrayCallback(HttpUrl.TASK_LIST_BY_UNIT));
                }

                @Override
                public void onError(String error) {
                    JokerLog.e("result error:" + error);
                }
            });
            ChannelHelper.getInstance().requestChannelList();
        } else {
            OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_BY_UNIT + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(getJsonArrayCallback(HttpUrl.TASK_LIST_BY_UNIT));
        }
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new UnitTaskViewHolder(getActivity(), parent, category);
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        JSONObject task = getRecyclerAdapter().getJsonObject(position);
        Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
        intent.putExtra("taskId", task.getIntValue("id"));
        if (task.containsKey("unitTasks") && task.getJSONArray("unitTasks").size() == 1) {
            intent.putExtra("unitTaskId", task.getJSONArray("unitTasks").getJSONObject(0).getIntValue("id"));
        }
        intent.putExtra("category", task.getIntValue("category"));

        if (task.getIntValue("category") == 7) {
            if (ChannelHelper.mChannelInfos.containsKey(task.getString("title"))) {
                intent.putExtra("channelInfo", ChannelHelper.mChannelInfos.get(task.getString("title")));
            }else {
                showToast("获取大华视频失败");
                return;
            }
        }
        startActivity(intent);
    }
}
