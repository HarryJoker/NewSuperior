package gov.android.com.superior;

import android.content.Intent;
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
import gov.android.com.superior.home.official.XZHomeFragment;
import gov.android.com.superior.home.unit.task.TaskListActivity;
import gov.android.com.superior.message.Messageragment;

public class MainXzActivity extends BaseMainActivity implements NavigationView.OnNavigationItemSelectedListener {

    private XZHomeFragment homeFragment;
    private Messageragment messageragment;
    private FragmentManager fragmentManager;
    private GatherUnitsFragment gatherUnitsFragment;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.d("onCreate------------------------->");

        setContentView(R.layout.activity_main_xz);
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
        switch (item.getItemId()) {
//            case R.id.nav_task:
//                Intent intent = new Intent(this, TaskListActivity.class);
//                intent.putExtra("leaderId", User.getInstance().getUserId());
//                startActivity(intent);
//                break;
//
//            case R.id.nav_my_task:
//                intent = new Intent(this, TaskListActivity.class);
//                intent.putExtra("unitId", Integer.parseInt(User.getInstance().get("unitId").toString()));
//                startActivity(intent);
//                break;
//            case R.id.nav_about:
//                startActivity(new Intent(this, AboutActivity.class));
//                break;
//
//            case R.id.nav_advice:
//                startActivity(new Intent(this, AdviceActivity.class));
//                break;

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
        if (homeFragment != null){
            transaction.hide(homeFragment);
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

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null && fragment.isVisible()) fragment.onActivityResult(requestCode, resultCode, data);
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
                    if (homeFragment == null) {
                        homeFragment = new XZHomeFragment();
                        transaction.add(R.id.content, homeFragment);
                    }
                    transaction.show(homeFragment);
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
