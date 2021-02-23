package gov.android.com.superior.ui.masses;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.first.orient.base.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.ui.unit.masses.MassesFragment;
import gov.android.com.superior.ui.unit.masses.space.TownSpaceListFragment;
import gov.android.com.superior.ui.unit.masses.space.UnitSpaceListFragment;
import gov.android.com.superior.ui.unit.masses.supervise.ClueAccessFragment;
import gov.android.com.superior.ui.unit.masses.supervise.PublicTaskListFragment;
import gov.android.com.superior.ui.unit.masses.supervise.ReportTaskListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuperviseFragment extends BaseMassesFragment {

    @Override
    protected List<Navigation> getNavigations() {
        List<Navigation> navigations = new ArrayList<>();
        navigations.add(new Navigation("政府工作报告", ReportTaskListFragment.class));
        navigations.add(new Navigation("督查政务公开", PublicTaskListFragment.class));
        navigations.add(new Navigation("督查线索征集", ClueAccessFragment.class));
        return navigations;
    }

}
