package gov.android.com.superior.task.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.http.Config;

public class ExpandTaskFragment extends BaseFragment {

    public static final String KEY_TYPE         = "listType";
    public static final String KEY_CATEGORY     = "category";
    public static final String KEY_UNIT         = "unitId";
    public static final String KEY_STATUS       = "status";
    public static final String KEY_MONTH        = "month";


    public static final int TASK_LIST_TYPE_ALL              = 0x01;     //全部列表
    public static final int TASK_LIST_TYPE_UNIT             = 0x02;     //分管列表
    public static final int TASK_LIST_TYPE_REPORT           = 0x03;     //上报列表
    public static final int TASK_LIST_TYPE_STATUS           = 0x04;     //根据状态（缓慢，正常，退回，完成）

    private int category;

    private int listType = 0;

    private int unitId = 0;

    private int month = 0;

    private int status = 0;

    private RecyclerView rc_expand_task;

    private SmartRefreshLayout mSmartRefreshLayout;



    public static ExpandTaskFragment newInstance(int category, int listType, int unitId) {
        ExpandTaskFragment fragment = new ExpandTaskFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_CATEGORY, category);
        args.putInt(KEY_TYPE, listType);
        args.putInt(KEY_UNIT, unitId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ExpandTaskFragment newInstance(int category, int listType, int unitId, int status, int month) {
        ExpandTaskFragment fragment = new ExpandTaskFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_CATEGORY, category);
        args.putInt(KEY_TYPE, listType);
        args.putInt(KEY_UNIT, unitId);
        args.putInt(KEY_STATUS, status);
        args.putInt(KEY_MONTH, month);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt(KEY_CATEGORY, 0);
            listType = getArguments().getInt(KEY_TYPE, 0);
            unitId = getArguments().getInt(KEY_UNIT, 0);
            month = getArguments().getInt(KEY_MONTH, 0);
            status = getArguments().getInt(KEY_STATUS, 0);

            Logger.d("MuilteTaskListFragment Params,  category:" +category +", type:" + listType + ", unitId:" + unitId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_expand_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSmartRefreshLayout = getView().findViewById(R.id.refreshlayout);

        mSmartRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        rc_expand_task = getView().findViewById(R.id.rc_expand_task);

        rc_expand_task.setLayoutManager(new LinearLayoutManager(getContext()));

        mSmartRefreshLayout.autoRefresh();
    }

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            requestTasks();
        }
    };

    private void requestTasks() {
        if (listType == TASK_LIST_TYPE_ALL) {
            OkGo.<JSONArray>get(Config.TASK_LIST_BY_CATEGORY + "/" + category).tag(this).execute(callback);
        }
        if (listType == TASK_LIST_TYPE_UNIT) {
            OkGo.<JSONArray>get(Config.TASK_LIST_BY_LEADER_UNIT + "/" + category + "/" + unitId).tag(this).execute(callback);
        }
        if (listType == TASK_LIST_TYPE_REPORT) {
            OkGo.<JSONArray>get(Config.TASK_LIST_BY_REPORT_UNIT + "/" + category + "/" + unitId).tag(this).execute(callback);
        }

        if (listType == TASK_LIST_TYPE_STATUS) {
            OkGo.<JSONArray>get(Config.TASK_LIST_BY_LEADER_AND_STATUS_MONTH + "/" + category + "/" + unitId + "/" + status + "/" + month).tag(this).execute(callback);
        }
    }

    private JsonObjectCallBack<JSONArray> callback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {
            mSmartRefreshLayout.finishRefresh(200);
            if (response.body() != null) {
                if (response.body().size() > 0) {
                    rc_expand_task.setAdapter(new ExpandTaskAdapter(getActivity(), category, response.body()));
                } else {
                    Toast.makeText(getContext(), "您暂无工作任务", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onError(Response<JSONArray> response) {
            super.onError(response);
            mSmartRefreshLayout.finishRefresh(200);
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
    }




//    private MuliteRecycleAdapter.OnItemClickListener mOnItemClickListener = new MuliteRecycleAdapter.OnItemClickListener() {
//        @Override
//        public void onItemClick(int type, int position, JSONObject item) {
//            int childTaskId = item.getIntValue("childTaskId");
//            int unitTaskId = item.getIntValue("unitTaskId");
//            Intent intent = new Intent(getActivity(), UnitTaskActivity.class);
//            intent.putExtra(UnitTaskActivity.KEY_CHILDTASK_ID, childTaskId);
//            intent.putExtra(UnitTaskActivity.KEY_UNITTASK_ID, unitTaskId);
//            intent.putExtra(UnitTaskActivity.KEY_UNITASK_TYPE, UnitTaskActivity.TYPE_UNITTASK_ARRAY);
//            startActivity(intent);
//        }
//    };

}
