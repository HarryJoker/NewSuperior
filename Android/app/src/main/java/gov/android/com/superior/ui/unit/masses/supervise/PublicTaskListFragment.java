package gov.android.com.superior.ui.unit.masses.supervise;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.callback.JsonObjectCallBack;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.masses.TaskInformActivity;

/**
 * A simple {@link Fragment} subclass.
 * 群众端：政务督查公开
 */
public class PublicTaskListFragment extends BaseRecylceFragment {

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_public_task_list;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestGoodPublicTasks();
    }

    private void requestGoodPublicTasks() {
        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_FOR_GOVERMENT_PUBLIC + "/" + 73).tag(this).execute(new JsonObjectCallBack<List<JSONObject>>() {
            @Override
            public void onSuccess(Response<List<JSONObject>> response) {
                requestBadPublicTasks(response.body());
            }

            @Override
            public void onError(Response<List<JSONObject>> response) {
                super.onError(response);
                showCallbckError(response);
                mSmartRefreshLayout.finishRefresh(true);
            }
        });
    }

    private void requestBadPublicTasks(List<JSONObject> goodTasks) {
        OkGo.<List<JSONObject>>get(HttpUrl.TASK_LIST_FOR_GOVERMENT_PUBLIC + "/" + 72).tag(this).execute(new JsonObjectCallBack<List<JSONObject>>() {
            @Override
            public void onSuccess(Response<List<JSONObject>> response) {
                setDataSource(mergeTasks(goodTasks, response.body()));
                mSmartRefreshLayout.finishRefresh(true);
            }

            @Override
            public void onError(Response<List<JSONObject>> response) {
                super.onError(response);
                mSmartRefreshLayout.finishRefresh(true);
            }
        });
    }

    private List<JSONObject> mergeTasks(List<JSONObject> goods, List<JSONObject> bads) {
        List<JSONObject> tasks = new ArrayList();
        int maxCount = goods.size() > bads.size() ? goods.size() : bads.size();
        if (maxCount == 0) return tasks;
        for (int n = 0; n < maxCount; n++) {
            JSONObject item = new JSONObject();
            item.put("goodTask",n < goods.size() ? goods.get(n) : null);
            item.put("badTask", n < bads.size() ? bads.get(n) : null);
            tasks.add(item);
        }
        return tasks;
    }

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new PublicUnitTaskViewHolder(getLayoutInflater().inflate(R.layout.rc_item_public_unit_task, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {

    }

    private View.OnClickListener mUnitTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            JokerLog.d(v.getTag() == null ? "null" : v.getTag().toString());
//            JSONObject task = getRecyclerAdapter().getJsonObject(position);
            if (v.getTag() == null || !(v.getTag() instanceof JSONObject)) return;
            JSONObject task = (JSONObject)v.getTag();
            Intent intent = new Intent(getActivity(), TaskInformActivity.class);
            intent.putExtra("taskId", task.getIntValue("id"));
            if (task.containsKey("unitTasks") && task.getJSONArray("unitTasks").size() == 1) {
                intent.putExtra("unitTaskId", task.getJSONArray("unitTasks").getJSONObject(0).getIntValue("id"));
            }
            startActivity(intent);
        }
    };

    class PublicUnitTaskViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

        private LinearLayout layoutGood;
        private TextView tvGoodTitle;
        private TextView tvGoodStatusContent;
        private TextView tvGoodTime;
        private TextView tvGoodUnit;

        private LinearLayout layotBad;
        private TextView tvBadTitle;
        private TextView tvBadStatusContent;
        private TextView tvBadTime;
        private TextView tvBadUnit;

        public PublicUnitTaskViewHolder(View itemView) {
            super(itemView);
            layoutGood = (LinearLayout) itemView.findViewById(R.id.layout_good);
            layoutGood.setOnClickListener(mUnitTaskClick);
            tvGoodTitle = (TextView) itemView.findViewById(R.id.tv_good_title);
            tvGoodStatusContent = (TextView) itemView.findViewById(R.id.tv_good_status_content);
            tvGoodTime = (TextView) itemView.findViewById(R.id.tv_good_time);
            tvGoodUnit = (TextView) itemView.findViewById(R.id.tv_good_unit);

            layotBad = (LinearLayout) itemView.findViewById(R.id.layot_bad);
            layotBad.setOnClickListener(mUnitTaskClick);
            tvBadTitle = (TextView) itemView.findViewById(R.id.tv_bad_title);
            tvBadStatusContent = (TextView) itemView.findViewById(R.id.tv_bad_status_content);
            tvBadTime = (TextView) itemView.findViewById(R.id.tv_bad_time);
            tvBadUnit = (TextView) itemView.findViewById(R.id.tv_bad_unit);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
            JSONObject goodTask = data.getJSONObject("goodTask");
            JSONObject badTask = data.getJSONObject("badTask");
            bindGoodTaskViewHolder(goodTask);
            bindBadTaskViewHolder(badTask);
        }

        private void bindGoodTaskViewHolder(JSONObject goodTask) {
            layoutGood.setTag(goodTask);
            layoutGood.setVisibility(goodTask == null ? View.INVISIBLE : View.VISIBLE);
            if (goodTask == null) return;
            tvGoodTitle.setText(goodTask.getString("title"));
            JSONArray unitTasks = goodTask.getJSONArray("unitTasks");
            if (unitTasks.size() == 1) {
                JSONObject unitTask = unitTasks.getJSONObject(0);
                tvGoodUnit.setText(unitTask.getString("unitName"));
                tvGoodTime.setText(unitTask.getString("progressTime"));
                String progressStatus = unitTask.getString("progressStatus");
                tvGoodStatusContent.setText(gov.android.com.superior.config.Config.informstatus.containsKey(progressStatus) ? gov.android.com.superior.config.Config.informstatus.get(progressStatus) : "未获取到进展通报");
            }
        }

        private void bindBadTaskViewHolder(JSONObject badTask) {
            layotBad.setVisibility(badTask == null ? View.INVISIBLE : View.VISIBLE);
            layotBad.setTag(badTask);
            if (badTask == null) return;
            tvBadTitle.setText(badTask.getString("title"));
            JSONArray unitTasks = badTask.getJSONArray("unitTasks");
            if (unitTasks.size() == 1) {
                JSONObject unitTask = unitTasks.getJSONObject(0);
                tvBadUnit.setText(unitTask.getString("unitName"));
                tvBadTime.setText(unitTask.getString("progressTime"));
                String progressStatus = unitTask.getString("progressStatus");
                tvBadStatusContent.setText(gov.android.com.superior.config.Config.informstatus.containsKey(progressStatus) ? gov.android.com.superior.config.Config.informstatus.get(progressStatus) : "未获取到进展通报");
            }
        }
    }
}
