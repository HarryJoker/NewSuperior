package gov.android.com.superior.ui.unit.masses.score.radar;

import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.first.orient.base.utils.JokerLog;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.R;

public class UnitRadarActivity extends BaseToolBarActivity {

    private List<Navigation> mNavigations = new ArrayList<Navigation>();

    private int unitId;

    private int rank;

    private JSONObject mUnit;

    private ViewPager mViewPager;

    private SlidingTabLayout mSlidingChildren;

    @Override
    public void onInitParams() {
        unitId = getIntent().getIntExtra("unitId", 0);
        mUnit = JSONObject.parseObject(getIntent().getStringExtra("unit"));
        rank = getIntent().getIntExtra("rank", 0);
        if (unitId == 0) {
            showToast("部门数据错误");
            finish();
        } else {
            makeNavigations();
        }
    }

    private void makeNavigations() {
        mNavigations.add(new Navigation("综合绩效", 0, unitId, UnitScoreRadarFragment.class));
        mNavigations.add(new Navigation("政府工作报告", 1, unitId, UnitCategoryFragment.class));
        mNavigations.add(new Navigation("7+3重点改革任务", 2, unitId, UnitCategoryFragment.class));
        mNavigations.add(new Navigation("建议提案", 3, unitId, UnitCategoryFragment.class));
        mNavigations.add(new Navigation("会议议定事项", 4, unitId, UnitCategoryFragment.class));
        mNavigations.add(new Navigation("领导批示件", 5, unitId, UnitCategoryFragment.class));
        mNavigations.add(new Navigation("群众线索", 9, unitId, UnitCategoryFragment.class));
    }

    public String getUnitLogo() {
        if (mUnit != null && mUnit.size() > 0) {
            return mUnit.getString("logo");
        }
        return "";
    }

    public String getUnitName() {
        if (mUnit != null && mUnit.size() > 0) {
            return mUnit.getString("name");
        }
        return "";
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_unit_radar;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("督查画像");
    }

    @Override
    protected void onFindViews() {
        mViewPager = findViewById(R.id.viewPage);

        mSlidingChildren = findViewById(R.id.sliding_tablayout);

    }

    @Override
    public void onInitView() {

        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        mSlidingChildren.setViewPager(mViewPager);
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            JokerLog.d("Child Navigation position:" + position);
            return mNavigations.get(position).makeFragment();
        }

        @Override
        public int getCount() {
            return mNavigations.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mNavigations.get(position).mTitle;
        }
    }


    class Navigation {
        String mTitle;
        int mCategory;
        int mUnitId;
        Class mClazz;
        Fragment fragment;

        public Navigation(String title, int category, int unitId, Class clazz) {
            this.mTitle = title;
            this.mCategory = category;
            this.mUnitId = unitId;
            this.mClazz = clazz;
        }

        private Fragment makeFragment() {
            if (fragment != null) return fragment;
            if (mClazz == null) return fragment;
            try {
                fragment = (Fragment) Class.forName(mClazz.getName()).newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("unitId", mUnitId);
                bundle.putInt("rank", rank);
                bundle.putInt("category", mCategory);
                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fragment;
        }
    }
}
