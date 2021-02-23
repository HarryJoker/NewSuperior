package gov.android.com.superior.ui.unit.dashboard.tasks;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.first.orient.base.activity.BaseToolBarActivity;

import gov.android.com.superior.R;
import gov.android.com.superior.ui.unit.dashboard.roles.AdminTasksFragment;

public class UnitTaskListActivity extends BaseToolBarActivity {

    private int listType = 0;
    private int unitId = 0;
    private int category = 0;
    private int status = 0;
    private int month = 0;

    private String title = "";

    @Override
    public void onInitParams() {
        listType = getIntent().getIntExtra(UnitTaskListFragment.KEY_TYPE, 0);
        category = getIntent().getIntExtra(UnitTaskListFragment.KEY_CATEGORY, 0);
        unitId = getIntent().getIntExtra(UnitTaskListFragment.KEY_PART_UNIT, 0);
        status = getIntent().getIntExtra(UnitTaskListFragment.KEY_STATUS, 0);
        month = getIntent().getIntExtra(UnitTaskListFragment.KEY_MONTH, 0);

        title = getIntent().getStringExtra("title");
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText(TextUtils.isEmpty(title) ? "博兴政务督查" : title);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_unit_task_list;
    }

    @Override
    protected void onFindViews() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (listType ==  UnitTaskListFragment.TASK_LIST_TYPE_ALL) {
            transaction.add(R.id.layout_content, AdminTasksFragment.newInstance(category));
        } else {
            transaction.add(R.id.layout_content, UnitTaskListFragment.newInstance(category, listType, unitId, month, status));
        }
        transaction.commit();
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }
    @Override
    public void onBusiness() {

    }



}
