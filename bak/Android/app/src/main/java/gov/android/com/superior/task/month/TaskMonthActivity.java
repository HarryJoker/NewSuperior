package gov.android.com.superior.task.month;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.kevin.slidingtab.SlidingTabLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.MainActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.SuperiorApplicaiton;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.home.DLeaderFragment;
import gov.android.com.superior.home.LeaderFragment;
import gov.android.com.superior.home.UnitFragment;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.task.StatusConfig;
import gov.android.com.superior.task.list.ExpandTaskFragment;
import gov.android.com.superior.task.list.MuliteTaskFragment;
import gov.android.com.superior.tools.DateUtil;

public class TaskMonthActivity extends BaseActivity {

    private Spinner spMonth;

    private AvatarImageView lvLogo;
    private TextView tvName;
    private TextView tvMonth;
    private TextView tvTaskCount;

    private TextView tvDoneCount;
    private TextView tvOverdueCount;
    private TextView tvSlowCount;
    private TextView tvBackCount;

    private SlidingTabLayout slidingTab;
    private ViewPager viewPage;

    private int category;

    private int leaderUnitId;

    private int month;

    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_month);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标

        category = getIntent().getIntExtra("category", 0);
        month = getIntent().getIntExtra("month", 0);
        leaderUnitId = getIntent().getIntExtra("leaderUnitId", 0);

        setTitle(StatusConfig.categroyNames[category] + "(月报)");

        spMonth = (Spinner) findViewById(R.id.sp_month);

        lvLogo = (AvatarImageView) findViewById(R.id.lv_logo);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvMonth = (TextView) findViewById(R.id.tv_month);
        tvTaskCount = (TextView) findViewById(R.id.tv_taskCount);

        tvDoneCount = (TextView) findViewById(R.id.tv_doneCount);
        tvOverdueCount = (TextView) findViewById(R.id.tv_overdueCount);
        tvSlowCount = (TextView) findViewById(R.id.tv_slowCount);
        tvBackCount = (TextView) findViewById(R.id.tv_backCount);
        slidingTab = (SlidingTabLayout) findViewById(R.id.sliding_tab);
        viewPage = (ViewPager) findViewById(R.id.viewPage);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, makeMonths());

        spinnerAdapter.setDropDownViewResource(R.layout.item_dropdown);

        spMonth.setAdapter(spinnerAdapter);


        spMonth.setOnItemSelectedListener(mOnMonthSelectedListener);

        refreshUnit();

        spMonth.setSelection(DateUtil.getNowMonth());
    }

    private AdapterView.OnItemSelectedListener mOnMonthSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            month = position;
            requestMonthSummary();

            refreshViewPager();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void refreshUnit() {
        Glide.with(this).load(Config.ATTACHMENT + User.getInstance().getUnitLogo()).into(lvLogo);
        tvName.setText(User.getInstance().get("name").toString());
    }

    private List<String> makeMonths() {
        List<String> months = new ArrayList<>();
        months.add("请选择月报月份");
        for (int n = 1; n <= DateUtil.getNowMonth(); n++) {
            months.add(n + "月份月报");
        }
        return months;
    }

    private void requestMonthSummary() {
        OkGo.<JSONObject>get(Config.LEADER_MONTH_SUMMARY_INFO + "/" + category + "/" + leaderUnitId + "/" + month).tag(this).execute(monthSummaryCallback);
    }

    private JsonObjectCallBack<JSONObject> monthSummaryCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            refreshTaskSummary(response.body().getJSONObject("taskSummary"));
        }
    };

    private void refreshTaskSummary(JSONObject taskSummary) {
        if (taskSummary == null) return;

        tvMonth.setText("您的" + month + "月份月报");

        tvTaskCount.setText("您分管的部门共承担" + taskSummary.getString("taskCount") + "项");

        tvDoneCount.setText("按时达标完成" + taskSummary.getString("doneCount") + "项");
        tvOverdueCount.setText("逾期报送工作" + taskSummary.getString("overdueCount") + "项");
        tvSlowCount.setText("进展缓慢工作" + taskSummary.getString("slowCount") + "项");
        tvBackCount.setText("退回重报工作" + taskSummary.getString("backCount") + "项");
    }


    private void refreshViewPager() {
        initPager();
        viewPage.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        slidingTab.setupWithViewPager(viewPage);
    }

    private int[] status = new int[] {0, 72, 50, 74, 91};

    private void initPager() {
        fragments.clear();
        if (category == 1) {
            fragments.add(MuliteTaskFragment.newInstance(category, MuliteTaskFragment.TASK_LIST_TYPE_STATUS, leaderUnitId, status[1], month));
            fragments.add(MuliteTaskFragment.newInstance(category, ExpandTaskFragment.TASK_LIST_TYPE_STATUS, leaderUnitId, status[2], month));
            fragments.add(MuliteTaskFragment.newInstance(category, ExpandTaskFragment.TASK_LIST_TYPE_STATUS, leaderUnitId, status[3], month));
            fragments.add(MuliteTaskFragment.newInstance(category, ExpandTaskFragment.TASK_LIST_TYPE_STATUS, leaderUnitId, status[4], month));
        } else {
            fragments.add(ExpandTaskFragment.newInstance(category, MuliteTaskFragment.TASK_LIST_TYPE_STATUS, leaderUnitId, status[1], month));
            fragments.add(ExpandTaskFragment.newInstance(category, ExpandTaskFragment.TASK_LIST_TYPE_STATUS, leaderUnitId, status[2], month));
            fragments.add(ExpandTaskFragment.newInstance(category, ExpandTaskFragment.TASK_LIST_TYPE_STATUS, leaderUnitId, status[3], month));
            fragments.add(ExpandTaskFragment.newInstance(category, ExpandTaskFragment.TASK_LIST_TYPE_STATUS, leaderUnitId, status[4], month));

        }
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        private String[] sections = new String[] {"进度缓慢工作", "逾期报送任务", "退回重报工作", "按时达标工作"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sections[position];
        }
    }


}
