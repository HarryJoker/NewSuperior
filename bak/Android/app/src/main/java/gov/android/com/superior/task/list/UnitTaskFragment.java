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
 *  Unitask List
 */
public class UnitTaskFragment extends BaseFragment {

    public static final String KEY_TYPE         = "listType";
    public static final String KEY_CATEGORY     = "category";
    public static final String KEY_UNIT         = "unitId";
    public static final String KEY_STATUS       = "status";

    public static final int TASK_LIST_TYPE_ALL             = 0xF1;     //部门全部列表
    public static final int TASK_LIST_TYPE_UNIT_VERIFY     = 0xF2;     //部门内部审核的列表
    public static final int TASK_LIST_TYPE_UNINT_UPDATE    = 0xF3;     //部门内部修改完善的列表


    private int category;

    private int listType = 0;

    private int unitId = 0;

    private int status = 0;

    private RecyclerView rc_muilte_task;

    private SmartRefreshLayout mSmartRefreshLayout;


//    public static UnitTaskFragment newInstance(int category, int listType, int unitId) {
//        UnitTaskFragment fragment = new UnitTaskFragment();
//        Bundle args = new Bundle();
//        args.putInt(KEY_CATEGORY, category);
//        args.putInt(KEY_TYPE, listType);
//        args.putInt(KEY_UNIT, unitId);
//        fragment.setArguments(args);
//        return fragment;
//    }

    /**
     * UnitContent的UnitTask list
     * @param category
     * @param listType
     * @param unitId
     * @param status
     * @return
     */
    public static UnitTaskFragment newInstance(int category, int listType, int unitId, int status) {
        UnitTaskFragment fragment = new UnitTaskFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_CATEGORY, category);
        args.putInt(KEY_TYPE, listType);
        args.putInt(KEY_UNIT, unitId);
        args.putInt(KEY_STATUS, status);
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
            status = getArguments().getInt(KEY_STATUS, 0);

            Logger.d("UnitTaskFragment Params,  category:" +category +", type:" + listType + ", unitId:" + unitId + ", status:" + status);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_mulite_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSmartRefreshLayout = getView().findViewById(R.id.refreshlayout);

        mSmartRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        rc_muilte_task = getView().findViewById(R.id.rc_muilte_task);

        rc_muilte_task.setLayoutManager(new LinearLayoutManager(getContext()));

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
            OkGo.<JSONArray>get(Config.TASK_LIST_BY_UNIT + "/" + category + "/" + User.getInstance().getUnitId()).tag(this).execute(callback);
        } else {
            OkGo.<JSONArray>get(Config.CONTENT_UNITTASK_LIST_BY_STATUS + "/" + category + "/" + unitId + "/" + status).tag(this).execute(callback);
        }
    }

    private JsonObjectCallBack<JSONArray> callback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {
            mSmartRefreshLayout.finishRefresh(true);
            if (response.body() != null) {
                rc_muilte_task.setAdapter(new UnitTaskAdapter(getActivity(), listType, response.body()));

                if (response.body().size() == 0) {
                    Toast.makeText(getContext(), "您已暂无工作任务", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onError(Response<JSONArray> response) {
            super.onError(response);
            mSmartRefreshLayout.finishRefresh(true);
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
