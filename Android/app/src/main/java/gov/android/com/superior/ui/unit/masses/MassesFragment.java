package gov.android.com.superior.ui.unit.masses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.first.orient.base.fragment.BaseFragment;
import com.first.orient.base.utils.DpUtils;
import com.first.orient.base.utils.JokerLog;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.TabEntity;
import gov.android.com.superior.ui.unit.masses.livelihood.CommentListFragment;
import gov.android.com.superior.ui.unit.masses.livelihood.OpinionListFragment;
import gov.android.com.superior.ui.unit.masses.livelihood.StatusListFragment;
import gov.android.com.superior.ui.unit.masses.livelihood.VoteListFragment;
import gov.android.com.superior.ui.unit.masses.score.UnitAppraisalFragment;
import gov.android.com.superior.ui.unit.masses.score.UnitRadarFragment;
import gov.android.com.superior.ui.unit.masses.score.UnitRewardFragment;
import gov.android.com.superior.ui.unit.masses.space.TownSpaceListFragment;
import gov.android.com.superior.ui.unit.masses.space.UnitSpaceListFragment;
import gov.android.com.superior.ui.unit.masses.supervise.ClueAccessFragment;
import gov.android.com.superior.ui.unit.masses.supervise.PublicTaskListFragment;
import gov.android.com.superior.ui.unit.masses.supervise.ReportTaskListFragment;

public class MassesFragment extends BaseFragment {

    private Navigation[] navigations = new Navigation[]{
            new Navigation("政务督查", new String[]{"政府工作报告", "督查政务公开", "督查线索征集"}, new Class[]{ReportTaskListFragment.class, PublicTaskListFragment.class, ClueAccessFragment.class}),
            new Navigation("绩效督查", new String[]{"绩效考评", "督查激励", "部门画像"}, new Class[]{UnitAppraisalFragment.class, UnitRewardFragment.class, UnitRadarFragment.class}),
            new Navigation("政创空间", new String[]{"部门政创空间", "街镇政创空间"}, new Class[]{UnitSpaceListFragment.class, TownSpaceListFragment.class}),
            new Navigation("民生在线", new String[]{"意见征集", "网络投票", "完成情况", "公众评价"}, new Class[]{OpinionListFragment.class, VoteListFragment.class, StatusListFragment.class, CommentListFragment.class}),
    };

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<CustomTabEntity>() {
        {
            add(new TabEntity(navigations[0].mTitle, 0, 0));
            add(new TabEntity(navigations[1].mTitle, 0, 0));
            add(new TabEntity(navigations[2].mTitle, 0, 0));
            add(new TabEntity(navigations[3].mTitle, 0, 0));
        }
    };

    private ViewPager mViewPager;

    private CommonTabLayout mCommonTabLayout;

    private SlidingTabLayout mSlidingChildren;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_masses, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCommonTabLayout = getView().findViewById(R.id.common_tablayout);

        mViewPager = getView().findViewById(R.id.viewPage);

        mSlidingChildren = getView().findViewById(R.id.sliding_tablayout);

        mCommonTabLayout.setTextsize(DpUtils.getSmallestScreenDimen(getContext(), R.dimen.sp_15));

        mCommonTabLayout.setDividerPadding(DpUtils.getSmallestScreenDimen(getContext(), R.dimen.dp_10));

        mCommonTabLayout.setOnTabSelectListener(onCommonTabSelectListener);

        mSlidingChildren.setOnTabSelectListener(onSlidingTabSelectListener);

        mCommonTabLayout.setTabData(mTabEntities);

        mViewPager.setAdapter(new MyPagerAdapter(getParentFragmentManager(), 0));

        mSlidingChildren.setViewPager(mViewPager);
    }

    private OnTabSelectListener onCommonTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position) {
            JokerLog.d("onTabSelect:" + position);
            mCommonTabLayout.getTitleView(position).getPaint().setFakeBoldText(true);

            mViewPager.setAdapter(new MyPagerAdapter(getParentFragmentManager(), position));

            //选中上次打开的子页面
            PagerAdapter pagerAdapter = mViewPager.getAdapter();
            if (pagerAdapter != null && pagerAdapter instanceof MyPagerAdapter) {
                mSlidingChildren.setCurrentTab(((MyPagerAdapter) pagerAdapter).getSaveChildNavigationIndex(), true);
            }

            mSlidingChildren.notifyDataSetChanged();
        }

        @Override
        public void onTabReselect(int position) {
            JokerLog.d("onTabReselect:" + position);
        }
    };

    private OnTabSelectListener onSlidingTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position) {
            JokerLog.d("onTabSelect Child:" + position);

            PagerAdapter pagerAdapter = mViewPager.getAdapter();
            if (pagerAdapter != null && pagerAdapter instanceof MyPagerAdapter) {
                ((MyPagerAdapter) pagerAdapter).saveChildNavigationIndex(position);
            }
        }

        @Override
        public void onTabReselect(int position) {
            JokerLog.d("onTabReselect:" + position);
        }
    };

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        private Navigation mCurNavigation;

        public MyPagerAdapter(FragmentManager fm, int index) {
            super(fm);
            mCurNavigation = navigations[index];
        }

        public void changeNavigation(int index) {
            if (index < navigations.length && index >= 0) {
                mCurNavigation = navigations[index];
            }
        }

        public int getSaveChildNavigationIndex() {
            return mCurNavigation.mChildIndex;
        }

        public void saveChildNavigationIndex(int childIndex) {
            mCurNavigation.mChildIndex = childIndex;
        }

        @Override
        public Fragment getItem(int position) {
            JokerLog.d("Child Navigation position:" + position);
            return mCurNavigation.getChildFragment(position);
        }

        @Override
        public int getCount() {
            return mCurNavigation.mChildFraments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCurNavigation.mChildTitles[position];
        }
    }

    class Navigation {
        String mTitle;
        String[] mChildTitles;
        Class[] mChildFraments;
        int mChildIndex;
        Map<Integer, Fragment> fragments = new HashMap<>();

        public Navigation(String title, String[] childTitles, Class[] childFraments) {
            this.mTitle = title;
            this.mChildTitles = childTitles;
            this.mChildFraments = childFraments;
        }

        public Fragment getChildFragment(int position) {
            if (!fragments.containsKey(position)) {
                Fragment fragment = makeNewInstance(mChildFraments[position], mChildTitles[position]);
                if (fragment == null)
                    throw new NullPointerException("Can not Class for Child Navigation Fragment....");
                fragments.put(position, fragment);
            }
            JokerLog.d("Child Navigation position:" + position);
            return fragments.get(position);
        }

        private Fragment makeNewInstance(Class clazz, String title) {
            Fragment fragment = null;
            if (clazz == null) return fragment;
            try {
                fragment = (Fragment) Class.forName(clazz.getName()).newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("txt", title);
                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fragment;
        }
    }
}