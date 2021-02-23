package gov.android.com.superior.task.detail;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnitTaskFragment extends BaseFragment {

    private TaskView mTaskView;

    private RecyclerView rc_traces;

    private SmartRefreshLayout mSmartRefreshLayout;

    private TraceAdapter mTraceAdapter;

    private int childTaskId;
    private int curUnitTaskId;

    public static UnitTaskFragment newInstance(int childTaskId, int unitTaskId) {
        UnitTaskFragment fragment = new UnitTaskFragment();
        Bundle args = new Bundle();
        args.putInt("childTaskId", childTaskId);
        args.putInt("unitTaskId", unitTaskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            childTaskId = getArguments().getInt("childTaskId", 0);
            curUnitTaskId = getArguments().getInt("unitTaskId", 0);

            Logger.d("UnitTaskListFragment Params,  childTaskId:" + childTaskId + ", unitTaskId:" + curUnitTaskId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_unit_task, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSmartRefreshLayout = getView().findViewById(R.id.refreshlayout);

        mSmartRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mTaskView = getView().findViewById(R.id.taskView);

        rc_traces = getView().findViewById(R.id.rc_traces);

        rc_traces.setLayoutManager(new LinearLayoutManager(getContext()));

        rc_traces.setAdapter(mTraceAdapter = new TraceAdapter(getActivity()));

        requestUnitTask();
    }

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            requestTraces();
        }
    };

    private void requestUnitTask() {
        showLoading();
        OkGo.<JSONObject>get(Config.UNITTASK_BY_ID + "/" + curUnitTaskId).tag(this).execute(unitTaskCallback);
    }

    private JsonObjectCallBack<JSONObject> unitTaskCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            mTaskView.refreshTaskView(response.body().getJSONObject("task"), response.body().getJSONObject("childTask"));

            mSmartRefreshLayout.autoRefresh();
        }

        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            hideLoading();
        }
    };

    private void requestTraces() {
        OkGo.<JSONArray>get(Config.TRACE_LIST_BY_UNITTASK + "/" + curUnitTaskId).tag(this).execute(tracesCallback);
    }

    private JsonObjectCallBack<JSONArray> tracesCallback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {

            refreshMenu(response.body());

            if (response.body() != null && mTraceAdapter != null) {
                mTraceAdapter.setJSONArray(response.body());
                hideLoading();
            }

            mSmartRefreshLayout.finishRefresh(true);
        }

        @Override
        public void onError(Response<JSONArray> response) {
            super.onError(response);
            hideLoading();
            mSmartRefreshLayout.finishRefresh(true);
        }
    };

    private void refreshMenu(JSONArray traces) {
        if (User.getInstance().getUserRole() != 4) return;
        if (getActivity() != null && getActivity() instanceof UnitTaskActivity) {
            ((UnitTaskActivity)getActivity()).refreshMenu(isAccept(traces));
        }
    }

    private boolean isAccept(JSONArray traces) {
        if (traces != null && traces.size() > 0) {
            for (int n=0; n<traces.size(); n++) {
                int status = traces.getJSONObject(n).getIntValue("status");
                if (status == 1) return true;
            }
        }
        return false;
    }
}