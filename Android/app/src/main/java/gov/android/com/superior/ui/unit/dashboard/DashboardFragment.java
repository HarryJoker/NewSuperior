package gov.android.com.superior.ui.unit.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.first.orient.base.fragment.BaseFragment;
import com.orhanobut.logger.Logger;
import java.util.HashMap;
import java.util.Map;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.ui.unit.dashboard.roles.AdminTasksFragment;
import gov.android.com.superior.ui.unit.dashboard.roles.DeputyTasksFragment;
import gov.android.com.superior.ui.unit.dashboard.roles.LeaderTasksFragment;
import gov.android.com.superior.ui.unit.dashboard.roles.UnitTasksFragment;

public class DashboardFragment extends BaseFragment {

    private Map<Integer, Class> roleFragments = new HashMap<Integer, Class>() {
        {
            put(1, LeaderTasksFragment.class);
            put(2, DeputyTasksFragment.class);
            put(3, AdminTasksFragment.class);
            put(4, UnitTasksFragment.class);
        }
    };

    private int category = 1;

    private BaseFragment mRoleFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt("category", 1);
            Logger.d("Category: " + category);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void updateBundle(Bundle bundle) {
        super.updateBundle(bundle);
        if (mRoleFragment != null) {
            category = bundle.getInt("category", 0);
            mRoleFragment.updateBundle(bundle);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRoleFragment = makeDashboardRoleFragment();
        if (mRoleFragment == null) return;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.layout_content, mRoleFragment);
        transaction.commit();
    }

    private BaseFragment makeDashboardRoleFragment() {
        int role = User.getInstance().getUserRole();
        Class clazz = roleFragments.get(role);
        try {
            BaseFragment fragment = (BaseFragment) Class.forName(clazz.getName()).newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("category", category);
            fragment.setArguments(bundle);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}