package gov.android.com.superior.ui.masses;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.first.orient.base.fragment.BaseFragment;
import com.first.orient.base.utils.JokerLog;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseMassesFragment extends BaseFragment {

    private ViewPager mViewPager;

    private SlidingTabLayout mSlidingTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_masses_base, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewPager = getView().findViewById(R.id.viewPage);

        mSlidingTabLayout = getView().findViewById(R.id.sliding_tablayout);

        mViewPager.setAdapter(new MyPagerAdapter(getParentFragmentManager(), getNavigations()));

        mSlidingTabLayout.setViewPager(mViewPager);
    }

    protected abstract List<Navigation> getNavigations();

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<Navigation> mNavigations = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm, List<Navigation> navigations) {
            super(fm);
            if (navigations != null) {
                mNavigations.addAll(navigations);
            }
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
        Class mClass;
        Fragment mFragment;

        public Navigation(String title, Class clazz) {
            this.mTitle = title;
            this.mClass = clazz;
        }

        private Fragment makeFragment() {
            if (mFragment != null) return mFragment;
            if (mClass == null) return mFragment;
            try {
                mFragment = (Fragment) Class.forName(mClass.getName()).newInstance();
//                Bundle bundle = new Bundle();
//                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mFragment;
        }
    }

}
