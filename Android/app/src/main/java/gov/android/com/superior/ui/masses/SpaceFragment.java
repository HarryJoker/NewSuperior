package gov.android.com.superior.ui.masses;


import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import gov.android.com.superior.ui.unit.masses.space.TownSpaceListFragment;
import gov.android.com.superior.ui.unit.masses.space.UnitSpaceListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpaceFragment extends BaseMassesFragment {

    @Override
    protected List<Navigation> getNavigations() {
        List<Navigation> navigations = new ArrayList<>();
        navigations.add(new Navigation("部门政创空间", UnitSpaceListFragment.class));
        navigations.add(new Navigation("街镇政创空间", TownSpaceListFragment.class));
        return navigations;
    }

}
