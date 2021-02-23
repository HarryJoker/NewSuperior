package gov.android.com.superior.home.unit.task;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.view.TabPageIndicator;

import static gov.android.com.superior.SuperiorApplicaiton.titles;

public class TaskListActivity extends BaseActivity {

    public static final int MODEL_UNACCEPT = 0X000000;
    public static final int MODEL_ACCEPTED = 0X000001;

    private String[] pross = new String[] {"", "已完成的工作", "进展较快的工作", "进展正常的工作", "进展缓慢的工作"};

    //部门单位，有已申领和未申领区别
    private int accept_model = -1;

    //县长查看副县长任务
    private int leaderId = 0;

    private int unitId = 0;

    private int progress = 0;

    private boolean isDivision = true;

    private boolean isVerify;

    private ViewPager mViewPager;
    private TabPageIndicator indicator;
    private List<BaseFragment> fragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_accept_task_list);

        accept_model = getIntent().getIntExtra("model", -1);

        leaderId = getIntent().getIntExtra("leaderId", 0);

        unitId = getIntent().getIntExtra("unitId", 0);

        isVerify = getIntent().getBooleanExtra("isVerify", false);

        progress = getIntent().getIntExtra("progress", 0);

        isDivision = getIntent().getBooleanExtra("isDivision", true);

        Logger.d("acceptModel:" + accept_model + "  leaderId:" + leaderId + "  unitId:" +unitId);

        int role = User.getInstance().getUserRole();
        if (role == 4) {
            setTitle(accept_model == 0 ? "未申领任务" : "我的任务");
        } else if (role == 3) {
            setTitle(isVerify ? "已审任务" : "所有任务");
        } else if (role < 3) {
            setTitle(leaderId > 0 ? "需审阅的任务" : "分管的任务");

            if (progress > 0) setTitle(pross[progress]);
        }

        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPage);

        initPager();

        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(mViewPager);

        int category = getIntent().getIntExtra("category", 0);
        if (category > 0) {
            mViewPager.setCurrentItem(category - 1, true);
        }

        setTabPagerIndicator();
    }

    private void setTabPagerIndicator() {
        indicator.setIndicatorMode(TabPageIndicator.IndicatorMode.MODE_NOWEIGHT_EXPAND_NOSAME);// 设置模式，一定要先设置模式
        indicator.setDividerPadding(CommonUtils.dip2px(6));
        indicator.setIndicatorColor(Color.parseColor("#EEEEEE"));// 设置底部导航线的颜色
        indicator.setTextColorSelected(Color.parseColor("#FFFFFF"));// 设置tab标题选中的颜色
        indicator.setTextColor(Color.parseColor("#CCCCCC"));// 设置tab标题未被选中的颜色
        indicator.setTextSize(CommonUtils.sp2px(15.0f));// 设置字体大小
        indicator.setUnderlineColor(Color.parseColor("#AAAAAA")); //设置底部默认横线颜色
    }


    private void initPager() {
        fragments = new ArrayList<BaseFragment>();

        int role = User.getInstance().getUserRole();
        if (role == 4) {
            fragments.add(TaskListFragment.newUnitInstance(1, accept_model));
            fragments.add(TaskListFragment.newUnitInstance(2, accept_model));
            fragments.add(TaskListFragment.newUnitInstance(3, accept_model));
            fragments.add(TaskListFragment.newUnitInstance(4, accept_model));
            fragments.add(TaskListFragment.newUnitInstance(5, accept_model));
            fragments.add(TaskListFragment.newUnitInstance(6, accept_model));
            fragments.add(TaskListFragment.newUnitInstance(7, accept_model));
        } else if (role == 3) {
            //督查室已审核or所有任务
            fragments.add(TaskListFragment.newDCInstance(1, isVerify));
            fragments.add(TaskListFragment.newDCInstance(2, isVerify));
            fragments.add(TaskListFragment.newDCInstance(3, isVerify));
            fragments.add(TaskListFragment.newDCInstance(4, isVerify));
            fragments.add(TaskListFragment.newDCInstance(5, isVerify));
            fragments.add(TaskListFragment.newDCInstance(6, isVerify));
            fragments.add(TaskListFragment.newDCInstance(7, isVerify));
        } else if (role < 3) {
            if (leaderId > 0) {
                //leaderId大于0查看上报的工作任务
                fragments.add(TaskListFragment.newReporInstance(1, leaderId));
                fragments.add(TaskListFragment.newReporInstance(2, leaderId));
                fragments.add(TaskListFragment.newReporInstance(3, leaderId));
                fragments.add(TaskListFragment.newReporInstance(4, leaderId));
                fragments.add(TaskListFragment.newReporInstance(5, leaderId));
                fragments.add(TaskListFragment.newReporInstance(6, leaderId));
                fragments.add(TaskListFragment.newReporInstance(7, leaderId));
            }
            if (unitId > 0) {
                //unitId大于0查看分管的任务
                fragments.add(TaskListFragment.newLeaderInstance(1, unitId, progress, isDivision));
                fragments.add(TaskListFragment.newLeaderInstance(2, unitId, progress, isDivision));
                fragments.add(TaskListFragment.newLeaderInstance(3, unitId, progress, isDivision));
                fragments.add(TaskListFragment.newLeaderInstance(4, unitId, progress, isDivision));
                fragments.add(TaskListFragment.newLeaderInstance(5, unitId, progress, isDivision));
                fragments.add(TaskListFragment.newLeaderInstance(6, unitId, progress, isDivision));
                fragments.add(TaskListFragment.newLeaderInstance(7, unitId, progress, isDivision));
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : fragments) {
            if (fragment.isVisible()) fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
