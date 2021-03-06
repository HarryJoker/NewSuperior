package gov.android.com.superior;

import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import gov.android.com.superior.about.AboutActivity;
import gov.android.com.superior.advice.AdviceActivity;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.gather.GatherUnitsFragment;
import gov.android.com.superior.home.supervise.DCHomeFragment;
import gov.android.com.superior.home.supervise.TraceListActivity;
import gov.android.com.superior.home.unit.task.TaskListActivity;
import gov.android.com.superior.message.Messageragment;

public class MainDcActivity extends BaseMainActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DCHomeFragment dcHomeFragment;
    private Messageragment messageragment;
    private FragmentManager fragmentManager;
    private GatherUnitsFragment gatherUnitsFragment;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.d("onCreate------------------------->");

        setContentView(R.layout.activity_main_dc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        validateHeader(navigationView);

        int role = User.getInstance().getUserRole();

        if (role > 0 && role <= 4) {
            fragmentManager = getSupportFragmentManager();
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            disableShiftMode(bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } else {
            Toast.makeText(this, "请让后台管理员审核您的信息", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setCheckable(false);
        Intent intent;
        switch (item.getItemId()) {
//            case R.id.nav_virify_task:
//                startActivity(new Intent(this, TraceListActivity.class));
//                break;
//            case R.id.nav_verify_trace:
//                intent = new Intent(this, TaskListActivity.class);
//                intent.putExtra("isVerify", true);
//                startActivity(intent);
//                break;
//            case R.id.nav_all_task:
//                intent = new Intent(this, TaskListActivity.class);
//                startActivity(intent);
//                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;

            case R.id.nav_advice:
                startActivity(new Intent(this, AdviceActivity.class));
                break;

//            case R.id.nav_login:
//                User.getInstance().logoutUser();
//                finish();
//                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 隐藏当前fragment
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction){
        if (dcHomeFragment != null){
            transaction.hide(dcHomeFragment);
        }
        if (gatherUnitsFragment != null){
            transaction.hide(gatherUnitsFragment);
        }
        if (messageragment != null){
            transaction.hide(messageragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (bottomNavigationView.getSelectedItemId()) {
            case R.id.navigation_home:
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null && fragment.isVisible()) fragment.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case R.id.navigation_gather:
                gatherUnitsFragment.onActivityResult(requestCode, resultCode, data);
                break;
            case R.id.navigation_message:
                messageragment.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            //开启事务隐藏当前Fragment
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            hideFragment(transaction);

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (dcHomeFragment == null) {
                        dcHomeFragment = new DCHomeFragment();
                        transaction.add(R.id.content, dcHomeFragment);
                    }
                    transaction.show(dcHomeFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_gather:
                    if (gatherUnitsFragment == null) {
                        gatherUnitsFragment = GatherUnitsFragment.newInstance(-1);
                        transaction.add(R.id.content, gatherUnitsFragment);
                    }
                    transaction.show(gatherUnitsFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_message:
                    if (messageragment == null) {
                        messageragment = Messageragment.newInstance();
                        transaction.add(R.id.content, messageragment);
                    }
                    transaction.show(messageragment);
                    transaction.commit();
                    return true;
            }
            return false;
        }

    };

}
