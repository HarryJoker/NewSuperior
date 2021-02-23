package gov.android.com.superior.ui.unit.dashboard.tasks;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.flyco.tablayout.SlidingTabLayout;
import com.lzy.okgo.OkGo;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.tools.DateUtil;

public class TaskMonthActivity extends BaseToolBarActivity {

    private Spinner spMonth;

    private RoundedImageView lvLogo;
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

    private JSONObject unit;

    private Map<String, Fragment> fragments = new HashMap<>();

    @Override
    public void onInitParams() {
        category = getIntent().getIntExtra("category", 0);
        month = getIntent().getIntExtra("month", 0);
        leaderUnitId = getIntent().getIntExtra("leaderUnitId", 0);
        String unitStr = getIntent().getStringExtra("unit");
        if (!TextUtils.isEmpty(unitStr)) {
            unit = JSONObject.parseObject(unitStr);
        }
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText(gov.android.com.superior.config.Config.categoryTitles.get(category) + "(月报)");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_task_month;
    }

    @Override
    protected void onFindViews() {
        spMonth = (Spinner) findViewById(R.id.sp_month);

        lvLogo = (RoundedImageView) findViewById(R.id.lv_logo);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvMonth = (TextView) findViewById(R.id.tv_month);
        tvTaskCount = (TextView) findViewById(R.id.tv_taskCount);

        tvDoneCount = (TextView) findViewById(R.id.tv_doneCount);
        tvOverdueCount = (TextView) findViewById(R.id.tv_overdueCount);
        tvSlowCount = (TextView) findViewById(R.id.tv_slowCount);
        tvBackCount = (TextView) findViewById(R.id.tv_backCount);

        slidingTab = (SlidingTabLayout) findViewById(R.id.sliding_tablayout);

        viewPage = (ViewPager) findViewById(R.id.viewPage);
    }

    @Override
    public void onInitView() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, makeMonths());

        spinnerAdapter.setDropDownViewResource(R.layout.item_dropdown);

        spMonth.setAdapter(spinnerAdapter);

        spMonth.setOnItemSelectedListener(mOnMonthSelectedListener);

        spMonth.setSelection(DateUtil.getNowMonth());
    }

    @Override
    public void onInitPresenter() {

    }

    private void refreshUnitView() {
        if (unit == null || unit.size() == 0) return;
        tvName.setText(unit.getString("name"));
        Glide.with(this).load(HttpUrl.ATTACHMENT + "/" + unit.getString("logo")).placeholder(R.mipmap.ic_avatar).into(lvLogo);
    }

    @Override
    public void onBusiness() {
       refreshUnitView();
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

    private List<String> makeMonths() {
        List<String> months = new ArrayList<>();
        months.add("请选择月报月份");
        for (int n = 1; n <= DateUtil.getNowMonth(); n++) {
            months.add(n + "月份月报");
        }
        return months;
    }

    private void requestMonthSummary() {
        OkGo.<JSONObject>get(HttpUrl.LEADER_MONTH_SUMMARY_INFO + "/" + category + "/" + leaderUnitId + "/" + month).tag(this).execute(getJsonObjectCallback());
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        refreshTaskSummary(data.getJSONObject("monthTaskSummary"));
    }

    private void refreshTaskSummary(JSONObject monthTaskSummary) {
        if (monthTaskSummary == null) return;

        tvMonth.setText("您的" + month + "月份月报");

        tvTaskCount.setText("您分管的部门共承担" + monthTaskSummary.getString("taskCount") + "项");

        tvDoneCount.setText("按时达标完成" + monthTaskSummary.getString("nomalCount") + "项");
        tvOverdueCount.setText("进展缓慢工作" + monthTaskSummary.getString("slowCount") + "项");
        tvSlowCount.setText("进展较快工作" + monthTaskSummary.getString("fastCount") + "项");
        tvBackCount.setText("已完成工作" + monthTaskSummary.getString("doneCount") + "项");
    }


    private void refreshViewPager() {
        fragments.clear();
        viewPage.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        slidingTab.setViewPager(viewPage);
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        private int[] status = new int[] {71, 72, 73, 91};

        private String[] sections = new String[] {"按时达标", "进展缓慢", "进展较快", "已完成"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (!fragments.containsKey(position + "")) {
                fragments.put(position + "", UnitTaskListFragment.newInstance(category, UnitTaskListFragment.TASK_LIST_TYPE_STATUS, leaderUnitId, month, status[position]));
            }
            return fragments.get(position + "");
        }

        @Override
        public int getCount() {
            return sections.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sections[position];
        }
    }

}
