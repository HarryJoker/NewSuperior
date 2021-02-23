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
public class UnitTaskListFragment extends BaseFragment {

    private TaskView mTaskView;

    private RecyclerView rc_units;

    private RecyclerView rc_traces;

    private SmartRefreshLayout mSmartRefreshLayout;

    private TraceAdapter mTraceAdapter;

    private int childTaskId;
    private int curUnitTaskId;


    public static UnitTaskListFragment newInstance(int childTaskId, int unitTaskId) {
        UnitTaskListFragment fragment = new UnitTaskListFragment();
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
        return inflater.inflate(R.layout.fragment_unit_task_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSmartRefreshLayout = getView().findViewById(R.id.refreshlayout);

        mSmartRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mTaskView = getView().findViewById(R.id.taskView);

        rc_units = getView().findViewById(R.id.rc_units);

        rc_units.setLayoutManager(new LinearLayoutManager(getContext()));

        rc_traces = getView().findViewById(R.id.rc_traces);

        rc_traces.setLayoutManager(new LinearLayoutManager(getContext()));

        rc_traces.setAdapter(mTraceAdapter = new TraceAdapter(getActivity()));

        requestUnitTasks();
    }

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            requestTraces();
        }
    };

    private void requestTraces() {
        OkGo.<JSONArray>get(Config.TRACE_LIST_BY_UNITTASK + "/" + curUnitTaskId).tag(this).execute(tracesCallback);
    }

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

    private JsonObjectCallBack<JSONArray> tracesCallback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {

            refreshMenu(response.body());

            if (response.body() != null && mTraceAdapter != null) {
                mTraceAdapter.setJSONArray(response.body());
            }
            hideLoading();

            mSmartRefreshLayout.finishRefresh(true);
        }

        @Override
        public void onError(Response<JSONArray> response) {
            super.onError(response);
            hideLoading();
            mSmartRefreshLayout.finishRefresh(true);
        }
    };

    private void requestUnitTasks() {
        showLoading();
        OkGo.<JSONObject>get(Config.UNITASK_LIST_BY_CHILDTASK + "/" + childTaskId).tag(this).execute(unitTasksCallback);
    }

    private JsonObjectCallBack<JSONObject> unitTasksCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            JSONObject jsonObject = response.body();
            JSONArray unitTasks = jsonObject.getJSONArray("unitTasks");

            mTaskView.refreshTaskView(jsonObject.getJSONObject("task"), jsonObject.getJSONObject("childTask"));

            if (unitTasks != null) {
                mUnitTasks.clear();;
                mUnitTasks.addAll(unitTasks);

                rc_units.setAdapter(new UnitTaskAdapter());
            }

            mSmartRefreshLayout.autoRefresh();
        }

        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            hideLoading();
        }
    };

    private View.OnClickListener mUnitItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View buttonView) {
            if (buttonView instanceof CheckBox) {
                if (buttonView.getTag() != null && buttonView.getTag() instanceof Integer) {
                    int position = (Integer) buttonView.getTag();
                    Logger.d("click: " + position);

                    JSONObject jsonObject = mUnitTasks.getJSONObject(position);

                    curUnitTaskId = jsonObject.getIntValue("unitTaskId");

                    rc_units.getAdapter().notifyDataSetChanged();

                    requestTraces();
                }
            }
        }
    };

    private JSONArray mUnitTasks = new JSONArray();

    class UnitTaskAdapter extends RecyclerView.Adapter<UnitTaskAdapter.UnitTaskViewHolder> {

        @Override
        public UnitTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UnitTaskViewHolder(getLayoutInflater().inflate(R.layout.item_unittask_unit, parent, false));
        }

        @Override
        public void onBindViewHolder(UnitTaskViewHolder holder, int position) {
            holder.bindView(position);
        }

        @Override
        public int getItemCount() {
            return mUnitTasks == null ? 0 : mUnitTasks.size();
        }

        class UnitTaskViewHolder extends RecyclerView.ViewHolder {
            CheckBox mCheckBox;
            public UnitTaskViewHolder(View itemView) {
                super(itemView);
                mCheckBox = (CheckBox) itemView;
                mCheckBox.setOnClickListener(mUnitItemClickListener);
            }

            public void bindView(int posiiton) {
                mCheckBox.setTag(posiiton);
                mCheckBox.setEnabled(true);
                mCheckBox.setText("-----");

                JSONObject jsonObject = mUnitTasks.getJSONObject(posiiton);

                mCheckBox.setChecked(jsonObject.getIntValue("unitTaskId") == curUnitTaskId);

                if (jsonObject.containsKey("unit")) {
                    jsonObject = jsonObject.getJSONObject("unit");
                    if (jsonObject.containsKey("name")) {
                        mCheckBox.setText(jsonObject.getString("name"));
                    }
                }
            }
        }
    }
}
