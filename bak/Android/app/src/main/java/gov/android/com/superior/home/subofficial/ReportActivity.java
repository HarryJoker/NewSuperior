package gov.android.com.superior.home.subofficial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.view.TabPageIndicator;

import static gov.android.com.superior.SuperiorApplicaiton.titles;

public class ReportActivity extends BaseActivity {

    private ViewPager mViewPager;
    private TabPageIndicator indicator;
    private List<BaseFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report2);

        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPage);

        initPager();

        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(mViewPager);

        setTabPagerIndicator();

        int curCategory = getIntent().getIntExtra("curCategory", 0);
        mViewPager.setCurrentItem(curCategory > 0 ? curCategory - 1 : 0, true);

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
        fragments.add(TabWorkReportFragment.newInstance(1));
        fragments.add(TabDeployerReportFragment.newInstance(2));
        fragments.add(TabProposalReportFragment.newInstance(3));
        fragments.add(TabOtherReportFragment.newInstance(4));
        fragments.add(TabOtherReportFragment.newInstance(5));
        fragments.add(TabOtherReportFragment.newInstance(6));
        fragments.add(TabOtherReportFragment.newInstance(7));
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
