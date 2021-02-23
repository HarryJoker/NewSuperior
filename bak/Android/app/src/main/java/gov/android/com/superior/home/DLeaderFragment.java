package gov.android.com.superior.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
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
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.Calendar;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.task.StatusConfig;
import gov.android.com.superior.task.list.TaskListActivity;
import gov.android.com.superior.task.list.MuliteTaskFragment;
import gov.android.com.superior.task.month.TaskMonthActivity;
import gov.android.com.superior.tools.DateUtil;

import static gov.android.com.superior.http.Config.ATTACHMENT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DLeaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DLeaderFragment extends BaseFragment {

    final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;

    private int category = 0;

    private RecyclerView rc_summary;

    private SummaryAdater mSummaryAdater;

    private SmartRefreshLayout mSmartRefreshLayout;

    public static DLeaderFragment newInstance(int category) {
        DLeaderFragment fragment = new DLeaderFragment();
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
            Logger.d("Category: " + category);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dleader, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSmartRefreshLayout = getView().findViewById(R.id.refreshlayout);
        mSmartRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        rc_summary = getView().findViewById(R.id.rc_summary);
        rc_summary.setLayoutManager(new LinearLayoutManager(getContext()));
        rc_summary.addItemDecoration(new SpacesItemDecoration(15));

        rc_summary.setAdapter(mSummaryAdater = new SummaryAdater());

        mSmartRefreshLayout.autoRefresh();
    }

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            requestSummary();
        }
    };

    private void requestSummary() {
        OkGo.<JSONArray>get(Config.LEADER_SUMMARY_INFO + "/" + category).tag(this).execute(jsonCallback);
    }

    private JsonObjectCallBack<JSONArray> jsonCallback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {
            mSmartRefreshLayout.finishRefresh(200);

            if (response.body() != null){
                summarys.clear();
                summarys.addAll(response.body());
                mSummaryAdater.notifyDataSetChanged();
            }
        }

        @Override
        public void onError(Response<JSONArray> response) {
            super.onError(response);
            mSmartRefreshLayout.finishRefresh(200);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            String filePath = data.getStringExtra("filePath");
            System.out.println("sign bitmap: " + filePath + ", " + new File(filePath).exists());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private JSONArray summarys = new JSONArray();

    class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    private View.OnClickListener moreClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Integer) {
                int position = ((Integer)v.getTag());

                JSONObject jsonObject = summarys.getJSONObject(position);

                Intent intent = new Intent(getActivity(), TaskListActivity.class);
                intent.putExtra(MuliteTaskFragment.KEY_CATEGORY, category);
                intent.putExtra(MuliteTaskFragment.KEY_TYPE, MuliteTaskFragment.TASK_LIST_TYPE_UNIT);
                intent.putExtra(MuliteTaskFragment.KEY_UNIT, jsonObject.getJSONObject("unit").getIntValue("id"));
                intent.putExtra("title", StatusConfig.categroyNames[category] + "( " +jsonObject.getJSONObject("unit").getString("name") + " 分管任务)");
                startActivity(intent);

            }
        }
    };

    private View.OnClickListener mOnMonthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), TaskMonthActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("month", DateUtil.getNowMonth());
            intent.putExtra("leaderUnitId", User.getInstance().getUnitId());

            getContext().startActivity(intent);
        }
    };

    class SummaryAdater extends RecyclerView.Adapter<SummaryAdater.BaseViewHolder> {
        public static final int TYPE_SUMMARY = 0X01;
        public static final int TYPE_MONTH = 0X02;
        public static final int TYPE_OTHER = 0X03;
        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_SUMMARY)
                return new SummaryViewHolder(getLayoutInflater().inflate(R.layout.item_leader_card, parent, false));
            if (viewType == TYPE_MONTH)
                return new MonthViewHolder(getLayoutInflater().inflate(R.layout.item_month_card, parent, false));
            if (viewType == TYPE_OTHER)
                return new OtherViewHolder(getLayoutInflater().inflate(R.layout.item_leader_other, parent, false));
            return null;
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            if (holder != null) holder.bindView(position);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 1) return TYPE_MONTH;
            if (position == 2) return TYPE_OTHER;
            return TYPE_SUMMARY;
        }

        @Override
        public int getItemCount() {
            return summarys.size();
        }

        abstract class BaseViewHolder extends RecyclerView.ViewHolder {

            public BaseViewHolder(View itemView) {
                super(itemView);
            }

            abstract void bindView(int position);
        }

        class SummaryViewHolder extends BaseViewHolder {

            AvatarImageView lvLogo;
            TextView tvUnit;
            TextView tvName;

            TextView tvTaskCount;
            TextView tvFinishCount;
            TextView tvDoingCount;

            TextView tvSlow;
            TextView tvNomal;
            TextView tvFast;
            TextView tvFinish;

            TextView tvSumOverdueCount;
            TextView tvSumSlowCount;
            TextView tvSumBackCount;
            TextView tvSumFastCount;

            TextView tvMore;

            public SummaryViewHolder(View convertView) {
                super(convertView);

                tvMore = convertView.findViewById(R.id.tv_more);
                tvMore.setOnClickListener(moreClick);

                lvLogo = (AvatarImageView) convertView.findViewById(R.id.lv_logo);
                tvUnit = (TextView) convertView.findViewById(R.id.tv_unit);
                tvName = (TextView) convertView.findViewById(R.id.tv_name);

                tvTaskCount = (TextView) convertView.findViewById(R.id.tv_taskCount);
                tvFinishCount = (TextView) convertView.findViewById(R.id.tv_finish_count);
                tvDoingCount = (TextView) convertView.findViewById(R.id.tv_doing_count);

                tvSlow = (TextView) convertView.findViewById(R.id.tv_slow);
                tvNomal = (TextView) convertView.findViewById(R.id.tv_nomal);
                tvFast = (TextView) convertView.findViewById(R.id.tv_fast);
                tvFinish = (TextView) convertView.findViewById(R.id.tv_finish);

                tvSumOverdueCount = (TextView) convertView.findViewById(R.id.tv_sum_overdue_count);
                tvSumSlowCount = (TextView) convertView.findViewById(R.id.tv_sum_slow_count);
                tvSumBackCount = (TextView) convertView.findViewById(R.id.tv_sum_back_count);
                tvSumFastCount = (TextView) convertView.findViewById(R.id.tv_sum_fast_count);
            }

            @Override
            void bindView(int position) {

                tvMore.setTag(position);
                tvMore.setVisibility(position > 0 ? View.VISIBLE : View.GONE);

                JSONObject jsonObject = summarys.getJSONObject(position);

                JSONObject unit = jsonObject.getJSONObject("unit");
                Glide.with(DLeaderFragment.this).load(ATTACHMENT + unit.getString("logo")).into(lvLogo);
                tvName.setText(unit.getString("name"));

                JSONObject taskSummary = jsonObject.getJSONObject("taskSummary");
                tvTaskCount.setText("任务统计 " + taskSummary.getString("taskCount") + " 项");
                tvFinishCount.setText("已完成 " + taskSummary.getString("doneCount") + " 项");
                tvDoingCount.setText("未完成 " + taskSummary.getString("doingCount") + " 项");

                //Task的当前状态统计
                tvSlow.setText("缓慢 " + taskSummary.getString("slowCount"));
                tvNomal.setText("正常  " + taskSummary.getString("doingCount"));
                tvFast.setText("较快 " + taskSummary.getString("fastCount"));
                tvFinish.setText("完成 " + taskSummary.getString("doneCount"));


                JSONObject traceSummary = jsonObject.getJSONObject("traceSummary");
                tvSumOverdueCount.setText(traceSummary.getString("sumOverdueCount"));
                tvSumSlowCount.setText(traceSummary.getString("sumSlowCount"));
                tvSumBackCount.setText(traceSummary.getString("sumBackCount"));
                tvSumFastCount.setText(traceSummary.getString("sumFastCount"));
            }
        }

        class MonthViewHolder extends BaseViewHolder {

            TextView tvSumCount;

            TextView tvSlow;
            TextView tvNomal;
            TextView tvFast;
            TextView tvFinish;

            TextView tvOverdueCount;
            TextView tvSlowCount;
            TextView tvBackCount;
            TextView tvFastCount;

            TextView tvMore;

            TextView tvMonth;

            public MonthViewHolder(View convertView) {
                super(convertView);
                tvMore = convertView.findViewById(R.id.tv_more);
                tvMore.setVisibility(View.VISIBLE);
                tvMore.setOnClickListener(mOnMonthClickListener);

                tvSumCount = convertView.findViewById(R.id.tv_sum_count);
                tvSlow = (TextView) convertView.findViewById(R.id.tv_slow);
                tvNomal = (TextView) convertView.findViewById(R.id.tv_nomal);
                tvFast = (TextView) convertView.findViewById(R.id.tv_fast);
                tvFinish = (TextView) convertView.findViewById(R.id.tv_finish);

                tvSlowCount = (TextView) convertView.findViewById(R.id.tv_slow_count);
                tvOverdueCount = (TextView) convertView.findViewById(R.id.tv_overdue_count);
                tvBackCount = (TextView) convertView.findViewById(R.id.tv_back_count);
                tvFastCount = (TextView) convertView.findViewById(R.id.tv_fast_count);

                tvMonth = convertView.findViewById(R.id.tv_month);
            }

            @Override
            void bindView(int position) {
                
                tvMore.setTag(position);

                tvMonth.setText("我的月报（" + DateUtil.getNowMonth() + "月份）");

                JSONObject jsonObject = summarys.getJSONObject(position);
                JSONObject taskSummary = jsonObject.getJSONObject("taskSummary");
                JSONObject traceSummary = jsonObject.getJSONObject("traceSummary");

                tvOverdueCount.setText(traceSummary.getString("sumOverdueCount"));
                tvSlowCount.setText(traceSummary.getString("sumSlowCount"));
                tvBackCount.setText(traceSummary.getString("sumBackCount"));
                tvFastCount.setText(traceSummary.getString("sumFastCount"));

                //Task的当前状态统计
                tvSumCount.setText("任务 " + taskSummary.getString("taskCount") + " 项");

                tvSlow.setText("缓慢 " + taskSummary.getString("slowCount"));
                tvNomal.setText("正常  " + taskSummary.getString("doingCount"));
                tvFast.setText("较快 " + taskSummary.getString("fastCount"));
                tvFinish.setText("完成 " + taskSummary.getString("doneCount"));
            }
        }

        class OtherViewHolder extends BaseViewHolder {
            RoundTextView tvMySelft;
            RoundTextView tvAllTask;
            RoundTextView tvReportTask;

            public OtherViewHolder(View convertView) {
                super(convertView);
                tvMySelft = convertView.findViewById(R.id.tv_my_selft);
                tvMySelft.setOnClickListener(myTaskClick);

                tvAllTask = convertView.findViewById(R.id.tv_all_task);
                tvAllTask.setOnClickListener(allTaskClick);

                tvReportTask = convertView.findViewById(R.id.tv_report_task);
                tvReportTask.setOnClickListener(reportTaskClick);

            }

            View.OnClickListener reportTaskClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TaskListActivity.class);
                    intent.putExtra(MuliteTaskFragment.KEY_TYPE, MuliteTaskFragment.TASK_LIST_TYPE_REPORT);
                    intent.putExtra(MuliteTaskFragment.KEY_CATEGORY, category);
                    intent.putExtra(MuliteTaskFragment.KEY_UNIT, User.getInstance().getUnitId());
                    intent.putExtra("title", StatusConfig.categroyNames[category] + "(上报的任务)");
                    startActivity(intent);
                }
            };

            View.OnClickListener myTaskClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     Intent intent = new Intent(getActivity(), TaskListActivity.class);
                    intent.putExtra(MuliteTaskFragment.KEY_TYPE, MuliteTaskFragment.TASK_LIST_TYPE_UNIT);
                    intent.putExtra(MuliteTaskFragment.KEY_CATEGORY, category);
                    intent.putExtra(MuliteTaskFragment.KEY_UNIT, User.getInstance().getUnitId());
                    intent.putExtra("title", StatusConfig.categroyNames[category] + "(分管任务)");
                    startActivity(intent);
                }
            };

            View.OnClickListener allTaskClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TaskListActivity.class);
                    intent.putExtra(MuliteTaskFragment.KEY_CATEGORY, category);
                    intent.putExtra(MuliteTaskFragment.KEY_TYPE, MuliteTaskFragment.TASK_LIST_TYPE_ALL);
                    intent.putExtra(MuliteTaskFragment.KEY_UNIT, User.getInstance().getUnitId());
                    intent.putExtra("title", StatusConfig.categroyNames[category] + "(所有任务)");
                    startActivity(intent);
                }
            };

            @Override
            void bindView(int position) {
                JSONObject jsonObject = summarys.getJSONObject(position);
                JSONObject taskSummary = jsonObject.getJSONObject("taskSummary");
                JSONObject reportSummary = jsonObject.getJSONObject("reportSummary");

                tvMySelft.setText("分管任务(" + taskSummary.getString("taskCount") + "项)");
//                tvAllTask.setText("所有任务(" + taskSummary.getString("") + "项)");
                tvReportTask.setText("上报任务(" + reportSummary.getString("taskCount") + "项)");
            }
        }
    }
}
