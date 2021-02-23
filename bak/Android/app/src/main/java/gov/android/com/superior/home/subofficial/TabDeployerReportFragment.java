package gov.android.com.superior.home.subofficial;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.harry.pulltorefresh.library.PullToRefreshBase;
import com.harry.pulltorefresh.library.PullToRefreshScrollView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.home.unit.task.TaskTraceActivity;
import gov.android.com.superior.http.Config;

/**
 * 决策重大部署月报
 */
public class TabDeployerReportFragment extends BaseFragment {

    private int category;

    private String date;

    private AvatarImageView iv_logo;

    private TextView tv_name;

    private TextView tv_date;

    private TextView tv_content;

    private List<ListView> listViews = new ArrayList<>();

    private PullToRefreshScrollView refreshScrollView;

    public TabDeployerReportFragment() {

    }

    public static TabDeployerReportFragment newInstance(int category) {
        TabDeployerReportFragment pageFragment =  new TabDeployerReportFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category", category);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getArguments().getInt("category", 0);
    }

    private void asyncGetAllTask() {

        String url = Config.TASK_GET_Deployer_REPORT + "/" + User.getInstance().get("unitId") + "/" + category + "/" + date;

        OkGo.<Map>get(url).tag(this).execute(jsonCallback);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (l == 0)return;
            Intent intent = new Intent(getActivity(), TaskTraceActivity.class);
//            intent.putExtra("taskId", l);
            List tasks = new ArrayList();
            tasks.add(((TaskAdapter)adapterView.getAdapter()).getItem(i));
            intent.putExtra("groupTasks", (Serializable) tasks);
            startActivity(intent);
        }
    };

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            refreshScrollView.onRefreshComplete();
        }

        @Override
        public void onSuccess(Response<Map> response) {
            Map<String,Object> data = response.body();


            refreshUserView();

            StringBuffer buffer = new StringBuffer();

            int sumCount = 0;
            for (int position = 0; position < 7; position++) {
                Object object = data.get("" + (position + 1));
                int count = object != null && object instanceof List ? ((List)object).size() : 0;
                buffer.append("第" + (position + 1) + "名：" + count + "项，");
                listViews.get(position).setAdapter(new TaskAdapter(object != null && object instanceof List ? (List)object : null, "未承办第" + (position + 1) + "名的工作"));
                sumCount += count;
            }

            String content = User.getInstance().get("name").toString() + ",";

            content += Calendar.getInstance().get(Calendar.MONTH) + "月份您分管的部门共承担" + sumCount + "项" + SuperiorApplicaiton.titles[category - 1] + "。";

            content += buffer.toString();

            tv_content.setText(content);

            refreshScrollView.onRefreshComplete();

        }
    };

    private void refreshUserView() {

        String logo = ((Map)User.getInstance().get("unit")).get("logo").toString();

        if (TextUtils.isEmpty(logo)) {
            String unitName = User.getInstance().get("name").toString();
            iv_logo.setTextAndColor(unitName.length() > 0 ? unitName.substring(0, 1) : "无", Color.parseColor("#f8f8ff"));
        } else {
            Glide.with(getActivity())
                    .load(Config.ATTACHMENT + logo)
                    .centerCrop()
                    .crossFade()
                    .into(iv_logo);
        }

        tv_name.setText(User.getInstance().get("name").toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_deployer_report, container, false);

        refreshScrollView = view.findViewById(R.id.pull_refresh_scrollview);

        iv_logo = view.findViewById(R.id.lv_logo);

        tv_name = view.findViewById(R.id.tv_name);

        tv_content = view.findViewById(R.id.tv_content);

        tv_date = view.findViewById(R.id.tv_date);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listViews.add((ListView) getView().findViewById(R.id.lv_one));
        listViews.add((ListView) getView().findViewById(R.id.lv_two));
        listViews.add((ListView) getView().findViewById(R.id.lv_three));
        listViews.add((ListView) getView().findViewById(R.id.lv_four));
        listViews.add((ListView) getView().findViewById(R.id.lv_five));
        listViews.add((ListView) getView().findViewById(R.id.lv_six));
        listViews.add((ListView) getView().findViewById(R.id.lv_seven));

        for (ListView listView : listViews) {
            if (listView != null) listView.setOnItemClickListener(itemClickListener);
        }

        tv_date.setOnClickListener(dateSelectClick);

        refreshScrollView.setOnRefreshListener(refreshListener);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());//获取当前时间
        calendar.add(Calendar.MONTH, -1);
        calendar.getTime();//获取一个月前的时间
        tv_date.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月");

        date = makeDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        asyncGetAllTask();

    }

    private View.OnClickListener dateSelectClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();//获取当前时间
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -1);
            calendar.getTime();//获取一个月前的时间

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setTitle("选择月报日期");
            //设置时间范围
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            ((ViewGroup)((ViewGroup) datePickerDialog.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);

            datePickerDialog.show();
        }
    };


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            tv_date.setText(year + "年" + (month + 1) + "月");
            date = makeDate(year, month, day);

            asyncGetAllTask();
        }
    };

    private String makeDate(int mYear, int mMonth, int mDay) {
        return new StringBuilder()
                .append(mYear)
                .append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
                .append("-")
                .append((mDay < 10) ? "0" + mDay : mDay).toString();

    }

    private PullToRefreshBase.OnRefreshListener refreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase pullToRefreshBase) {
            asyncGetAllTask();
        }
    };

    class TaskAdapter extends BaseAdapter {

        private String emptyTip = "";
        private List<Map<String,Object>> tasks = new ArrayList<>();

        public TaskAdapter(List<Map<String, Object>> list, String tip)
        {
            if (tip != null) emptyTip = tip;
            if (list != null) tasks.addAll(list);
        }

        public void updateTasks(List<Map<String, Object>> list) {
            if (list != null) tasks.clear();
            tasks.addAll(list);

            Logger.d(tasks);
        }

        @Override
        public int getCount() {
            return tasks.size() == 0 ? 1 : tasks.size() > 3 ? 3 : tasks.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return tasks.size() == 0 ? null : tasks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return tasks.size() == 0 ? 0 : Long.valueOf(getItem(i).get("id").toString());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (tasks.size() == 0) {
                view = getActivity().getLayoutInflater().inflate(R.layout.item_null_tip, null);
                ((TextView)view).setText(emptyTip);
                return view;
            } else {
                view = getActivity().getLayoutInflater().inflate(R.layout.item_task, null);
                int progress = Integer.parseInt(getItem(i).get("progress").toString());
                ((ImageView) view.findViewById(R.id.iv_logo)).setImageResource(SuperiorApplicaiton.taskStates[progress]);
                ((TextView) view.findViewById(R.id.tv_title)).setText(getItem(i).get("name").toString());
                ((TextView) view.findViewById(R.id.tv_plan)).setText(getItem(i).get("plan").toString());
                return view;
            }
        }
    }
}