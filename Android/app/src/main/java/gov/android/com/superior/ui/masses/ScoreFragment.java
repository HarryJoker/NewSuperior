package gov.android.com.superior.ui.masses;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.R;
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
public class ScoreFragment extends BaseMassesFragment {


    @Override
    protected List<Navigation> getNavigations() {
        List<Navigation> navigations = new ArrayList<>();
        navigations.add(new Navigation("绩效考评", UnitAppraisalFragment.class));
        navigations.add(new Navigation("督查激励", UnitRewardFragment.class));
        navigations.add(new Navigation("部门画像", UnitRadarFragment.class));
        return navigations;
    }
}
