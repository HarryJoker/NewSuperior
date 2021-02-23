package gov.android.com.superior.task.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.orhanobut.logger.Logger;

import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.trace.NewAcceptActivity;
import gov.android.com.superior.trace.NewContentActivity;
import gov.android.com.superior.trace.NewLeaderTraceActivity;
import gov.android.com.superior.trace.NewReportTraceActivity;
import gov.android.com.superior.trace.VerifyTraceActivity;

import static android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM;

public class UnitTaskActivity extends BaseActivity {

    public static final int TYPE_UNITTASK_ENTRY = 0X01;
    public static final int TYPE_UNITTASK_ARRAY = 0X02;

    public static final String KEY_CHILDTASK_ID = "childTaskId";
    public static final String KEY_UNITASK_TYPE = "type";
    public static final String KEY_UNITTASK_ID = "unitTaskId";

    private int unitTaskId = 0;
    private int childTaskId = 0;
    private int type = 0;

    private int status = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_task);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标

        setTitle("任务详情");

        type = getIntent().getIntExtra(KEY_UNITASK_TYPE, 0);
        childTaskId = getIntent().getIntExtra(KEY_CHILDTASK_ID, 0);
        unitTaskId = getIntent().getIntExtra(KEY_UNITTASK_ID, 0);

        BaseFragment fragment = null;

        if (type == TYPE_UNITTASK_ARRAY) {
            fragment = UnitTaskListFragment.newInstance(childTaskId, unitTaskId);
        } else {
            fragment = UnitTaskFragment.newInstance(childTaskId, unitTaskId);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_content, fragment);
        transaction.commit();
    }

    private static final int MENU_ACCEPT    = 0X0F01;   //领取任务
    private static final int MENU_VERIFY    = 0X0F02;   //审核
    private static final int MENU_LEADER    = 0X0F03;   //领导批示
    private static final int MENU_CONTENT   = 0X0F04;   //报送任务
    private static final int MENU_REPORT    = 0X0F05;   //审核


    public void refreshMenu(boolean isAccepted) {
        status = isAccepted ? 1 : 0;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (User.getInstance().getUserRole() <= 2) {
            menu.add(0x01, MENU_LEADER, 0, "批示示阅");
        }
        if (User.getInstance().getUserRole() == 3) {
            menu.add(0x01, MENU_REPORT, 0, "上报领取");

            menu.add(0x01, MENU_VERIFY, 0, "审核任务");
            menu.getItem(1).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        }

        if (User.getInstance().getUserRole() == 4) {
            if (status == 0) {
                menu.add(0x01, MENU_ACCEPT, 0, "领取任务");
            }
            if (status > 0) {
                menu.add(0x01, MENU_CONTENT, 0, "报送工作");
            }
        }
        if (menu.hasVisibleItems()) {
            menu.getItem(0).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_VERIFY) {
            Intent intent = new Intent(this, VerifyTraceActivity.class);
            intent.putExtra("unitTaskId", unitTaskId);
            startActivity(intent);
        }

        if (item.getItemId() == MENU_REPORT) {
            Intent intent = new Intent(this, NewReportTraceActivity.class);
            intent.putExtra("unitTaskId", unitTaskId);
            startActivity(intent);
        }

        if (item.getItemId() == MENU_LEADER) {
            Intent intent = new Intent(this, NewLeaderTraceActivity.class);
            intent.putExtra("unitTaskId", unitTaskId);
            startActivity(intent);
        }

        if (item.getItemId() == MENU_CONTENT) {
            Intent intent = new Intent(this, NewContentActivity.class);
            intent.putExtra("unitTaskId", unitTaskId);
            intent.putExtra("type", NewContentActivity.TYPE_NEW);
            startActivity(intent);
        }

        if (item.getItemId() == MENU_ACCEPT) {
            Intent intent = new Intent(this, NewAcceptActivity.class);
            intent.putExtra("unitTaskId", unitTaskId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
