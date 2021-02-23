package gov.android.com.superior.task.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;

import com.mm.dss.demo.devices.fragments.DeviceFragment;
import com.orhanobut.logger.Logger;

import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;

public class TaskListActivity extends BaseActivity {

    private int listType = 0;
    private int unitId = 0;
    private int category = 0;
    private int status = 0;
    private int month = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标

        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        listType = getIntent().getIntExtra(MuliteTaskFragment.KEY_TYPE, 0);
        category = getIntent().getIntExtra(MuliteTaskFragment.KEY_CATEGORY, 0);
        unitId = getIntent().getIntExtra(MuliteTaskFragment.KEY_UNIT, 0);
        status = getIntent().getIntExtra(MuliteTaskFragment.KEY_STATUS, 0);
        month = getIntent().getIntExtra(MuliteTaskFragment.KEY_MONTH, 0);

        Fragment fragment = null;

        if (listType == UnitTaskFragment.TASK_LIST_TYPE_ALL || listType == UnitTaskFragment.TASK_LIST_TYPE_UNINT_UPDATE || listType == UnitTaskFragment.TASK_LIST_TYPE_UNIT_VERIFY) {
            fragment = UnitTaskFragment.newInstance(category, listType, unitId, status);
        } else {

            if (category ==  7 && User.getInstance().getUserRole() <= 3) {
                //重点项目
                fragment = DeviceFragment.newInstance("", "");
            } else {
                if (category == 1) {
                    //政府工作报告
                    fragment = MuliteTaskFragment.newInstance(category, listType, unitId, status, month);
                } else {
                    fragment = ExpandTaskFragment.newInstance(category, listType, unitId, status, month);
                }
            }
        }

        Logger.d("category:" + category + ", fragment:" + fragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_content, fragment);
        transaction.commit();
    }
}
