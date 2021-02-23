package gov.android.com.superior;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kevin.slidingtab.SlidingTabLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.vector.update_app.UpdateAppManager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.about.AboutActivity;
import gov.android.com.superior.advice.AdviceActivity;
import gov.android.com.superior.banner.BannerActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.home.DLeaderFragment;
import gov.android.com.superior.home.LeaderFragment;
import gov.android.com.superior.home.UnitFragment;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.message.MessageListActivity;
import gov.android.com.superior.setup.CacheActivity;
import gov.android.com.superior.task.list.ExpandTaskFragment;
import gov.android.com.superior.task.list.MuliteTaskFragment;
import gov.android.com.superior.tools.GlideImageLoader;
import gov.android.com.superior.tools.UpdateAppHttpUtil;
import gov.android.com.superior.tools.VersionUtils;
import gov.android.com.superior.user.CreateUserActivity;
import gov.android.com.superior.user.UnitActivity;

import static android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM;
import static gov.android.com.superior.http.Config.UPDATE_VERSION;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Banner banner;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        banner = findViewById(R.id.banner);

        initNavigationMenu();

        initBanner();

        initViewPager();

        refreshUnit();

        asyncVersion();
    }

    private void refreshUnit() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        if (headerView == null) return;
        AvatarImageView avatar = headerView.findViewById(R.id.iv_logo);
        Glide.with(this).load(Config.ATTACHMENT + User.getInstance().getUnitLogo()).into(avatar);
        TextView tvName = headerView.findViewById(R.id.tv_name);
        tvName.setText(User.getInstance().get("name").toString());
        TextView tvUnit = headerView.findViewById(R.id.tv_unit_name);
        tvUnit.setText(User.getInstance().getUnitName());
        if (User.getInstance().getUserRole() <= 2) {
            tvUnit.setText("博兴县人民办公室");
        }
        TextView tvIntro = headerView.findViewById(R.id.tv_content);
        tvIntro.setText(User.getInstance().get("duty").toString());
    }

    public View.OnClickListener logoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            User.getInstance().logoutUser();
            finish();
        }
    };

    private void initNavigationMenu() {
//        int role = User.getInstance().getUserRole();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.findViewById(R.id.tv_logout).setOnClickListener(logoutClick);
        navigationView.setNavigationItemSelectedListener(this);
        //通用menu设置
        getMenuInflater().inflate(R.menu.drawer_unit, navigationView.getMenu());
//        switch (role) {
//            case 1:
//                getMenuInflater().inflate(R.menu.drawer_xz, navigationView.getMenu());
//                break;
//            case 2:
//                getMenuInflater().inflate(R.menu.drawer_fxz, navigationView.getMenu());
//                break;
//            case 3:
//                getMenuInflater().inflate(R.menu.drawer_dc, navigationView.getMenu());
//                break;
//            case 4:
//                getMenuInflater().inflate(R.menu.drawer_unit, navigationView.getMenu());
//                break;
//        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add("hhahhhhh");
//        return super.onCreateOptionsMenu(menu);
//    }

    private void initViewPager() {
        mSlidingTabLayout = findViewById(R.id.sliding_tab);
        mViewPager = findViewById(R.id.viewPage);
        initPager();
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mSlidingTabLayout.setupWithViewPager(mViewPager);
    }


    private void initPager() {
        int role = User.getInstance().getUserRole();

        switch (role) {
            case 1:
                fragments.add(DLeaderFragment.newInstance(1));
                fragments.add(DLeaderFragment.newInstance(2));
                fragments.add(DLeaderFragment.newInstance(3));
                fragments.add(DLeaderFragment.newInstance(4));
                fragments.add(DLeaderFragment.newInstance(5));
                fragments.add(DLeaderFragment.newInstance(6));
                fragments.add(DLeaderFragment.newInstance(7));
                break;
            case 2:
                fragments.add(LeaderFragment.newInstance(1));
                fragments.add(LeaderFragment.newInstance(2));
                fragments.add(LeaderFragment.newInstance(3));
                fragments.add(LeaderFragment.newInstance(4));
                fragments.add(LeaderFragment.newInstance(5));
                fragments.add(LeaderFragment.newInstance(6));
                fragments.add(LeaderFragment.newInstance(7));
                break;
            case 3:
                fragments.add(MuliteTaskFragment.newInstance(1, MuliteTaskFragment.TASK_LIST_TYPE_ALL, 0));
                fragments.add(ExpandTaskFragment.newInstance(2, ExpandTaskFragment.TASK_LIST_TYPE_ALL, 0));
                fragments.add(ExpandTaskFragment.newInstance(3, ExpandTaskFragment.TASK_LIST_TYPE_ALL, 0));
                fragments.add(ExpandTaskFragment.newInstance(4, ExpandTaskFragment.TASK_LIST_TYPE_ALL, 0));
                fragments.add(ExpandTaskFragment.newInstance(5, ExpandTaskFragment.TASK_LIST_TYPE_ALL, 0));
                fragments.add(ExpandTaskFragment.newInstance(6, ExpandTaskFragment.TASK_LIST_TYPE_ALL, 0));
                fragments.add(ExpandTaskFragment.newInstance(7, ExpandTaskFragment.TASK_LIST_TYPE_ALL, 0));
                break;
            case 4:
                fragments.add(UnitFragment.newInstance(1));
                fragments.add(UnitFragment.newInstance(2));
                fragments.add(UnitFragment.newInstance(3));
                fragments.add(UnitFragment.newInstance(4));
                fragments.add(UnitFragment.newInstance(5));
                fragments.add(UnitFragment.newInstance(6));
                fragments.add(UnitFragment.newInstance(7));
                break;
        }
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return SuperiorApplicaiton.titles[position];
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
            case R.id.nav_user:
                intent = new Intent(this, CreateUserActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case R.id.nav_unit:
                intent = new Intent(this, UnitActivity.class);
                 startActivity(intent);
                break;
            case R.id.nav_history:

                break;
            case R.id.nav_cache:
                intent = new Intent(this, CacheActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_message:
                intent = new Intent(this, MessageListActivity.class);
                startActivity(intent);
                break;
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













    /*************************Banner***************************/

    private List<String> titles = new ArrayList<>();
    private List<String> urls = new ArrayList<>();
    private List<String> attachments = new ArrayList<>();

    private void invalidateBanner(List<String> titles, List<String> images) {

        //设置图片集合
        banner.setImages(images);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titles);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    public void asyncBanner() {
        OkGo.<List<Map<String, String>>>get(Config.BANNER).tag(this).cacheTime(3 * 60 *60 * 1000).cacheKey("banner").cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST).execute(bannerCallback);
    }

    private JsonCallback<List<Map<String, String>>> bannerCallback = new JsonCallback<List<Map<String, String>>>() {
        @Override
        public void onSuccess(Response<List<Map<String, String>>> response) {
            for (Map<String, String> banner : response.body()) {
                titles.add(banner.get("title"));
                urls.add(banner.get("link"));
                attachments.add(Config.ATTACHMENT + banner.get("image"));
            }
            invalidateBanner(titles, attachments);
        }
    };

    private void initBanner() {
        try {
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);

            //设置图片加载器
            banner.setImageLoader(new GlideImageLoader());

            banner.setDelayTime(3 * 1000);

            banner.setOnBannerListener(bannerListener);

            banner.setIndicatorGravity(BannerConfig.RIGHT);

            asyncBanner();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private OnBannerListener bannerListener = new OnBannerListener() {
        @Override
        public void OnBannerClick(int position) {
            Intent intent = new Intent(MainActivity.this, BannerActivity.class);
            intent.putExtra("url", urls.get(position));
            intent.putExtra("title", titles.get(position));
            startActivity(intent);
        }
    };


    /****************************版本更新********************************/

    private void asyncVersion() {
        OkGo.<Map>get(Config.VERSION).tag(this).execute(jsonCallback);
    }

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {
        @Override
        public void onSuccess(Response<Map> response) {
            Map data = response.body();
            if (data != null && data.containsKey("version"))
                if (Integer.parseInt(data.get("version").toString()) > SuperiorApplicaiton.VERSION) {
                    showUpdateDialog();
                }
        }
    };

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请更新版本");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showProgressDialog();

            }
        });

        builder.create().show();
    }
    private void asyncDownApk() {
        OkGo.<File>get(Config.ATTACHMENT + "app.apk").tag(this).execute(fileCallback);
    }

    private FileCallback fileCallback = new FileCallback() {

        @Override
        public void onSuccess(Response<File> response) {
            Logger.d(response.body());

            progressDialog.dismiss();

            //判读版本是否在7.0以上
            if(Build.VERSION.SDK_INT >= 24) {
                Uri apkUri = FileProvider.getUriForFile(MainActivity.this, "gov.android.com.superior.fileprovider", response.body());//在AndroidManifest中的android:authorities值
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                startActivity(install);
            } else {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(Uri.fromFile(response.body()), "application/vnd.android.package-archive");
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(install);
            }
        }

        @Override
        public void downloadProgress(Progress progress) {
            super.downloadProgress(progress);

            if (progressDialog != null) {
                progressDialog.setProgress((int) (progress.fraction * 100));

            }
        }
    };

    private ProgressDialog progressDialog;

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("下载最新版本中");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setOnKeyListener(keyListener);
        progressDialog.show();
        asyncDownApk();
    }

    private DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
            return keyCode==KeyEvent.KEYCODE_BACK;
        }
    };
}
