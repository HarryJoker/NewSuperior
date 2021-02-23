package gov.android.com.superior.ui.masses;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.first.orient.base.utils.DpUtils;
import com.first.orient.base.utils.JokerLog;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.TabEntity;
import gov.android.com.superior.ui.unit.masses.MassesFragment;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveFragment extends BaseMassesFragment {

    @Override
    protected List<Navigation> getNavigations() {
        List<Navigation> navigations = new ArrayList<>();
        navigations.add(new Navigation("意见征集", OpinionListFragment.class));
        navigations.add(new Navigation("网络投票", VoteListFragment.class));
        navigations.add(new Navigation("完成情况", StatusListFragment.class));
        navigations.add(new Navigation("公众评价", CommentListFragment.class));
        return navigations;
    }

}
