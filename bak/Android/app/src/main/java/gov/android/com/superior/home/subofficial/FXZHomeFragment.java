package gov.android.com.superior.home.subofficial;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.view.TabPageIndicator;

import static gov.android.com.superior.SuperiorApplicaiton.titles;

/**
 * A simple {@link Fragment} subclass.
 */
public class FXZHomeFragment extends Fragment {

    private ViewPager mViewPager;
    private TabPageIndicator indicator;
    private List<Fragment> fragments = new ArrayList<>();


    public FXZHomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_dchome, container, false);

        indicator = (TabPageIndicator) view.findViewById(R.id.indicator);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPage);

        initPager();

        mViewPager.setAdapter(new FXZHomeFragment.MyPagerAdapter(getFragmentManager()));
        indicator.setViewPager(mViewPager);
        setTabPagerIndicator();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Logger.d(data);

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
        fragments = new ArrayList<Fragment>();
        fragments.add(TabFragment.newInstance(1));
        fragments.add(TabFragment.newInstance(2));
        fragments.add(TabFragment.newInstance(3));
        fragments.add(TabFragment.newInstance(4));
        fragments.add(TabFragment.newInstance(5));
        fragments.add(TabFragment.newInstance(6));
        fragments.add(TabFragment.newInstance(7));
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
