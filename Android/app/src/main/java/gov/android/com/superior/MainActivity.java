package gov.android.com.superior;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.zhouwei.library.CustomPopWindow;
import com.first.orient.base.callback.JsonObjectCallBack;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.fragment.BaseFragment;
import com.first.orient.base.utils.JokerLog;
import com.first.orient.base.utils.OkGoUpdateHttpUtil;
import com.first.orient.base.view.ImageTextView;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.listener.ExceptionHandler;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.adapter.PopWindowCategoryAdapter;
import gov.android.com.superior.entity.MyTabEntity;
import gov.android.com.superior.entity.TabEntity;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.dashboard.DashboardFragment;
import gov.android.com.superior.ui.unit.masses.MassesFragment;
import gov.android.com.superior.ui.unit.person.PersonFragment;

import static com.first.orient.base.fragment.BaseFragment.makeBundleToJson;

public class MainActivity extends AppCompatActivity {

    private ArrayList<CustomTabEntity> mTabEntitys = new ArrayList<CustomTabEntity>() {
        {
            add(new MyTabEntity("工作台", R.mipmap.ic_navigate_dashboard_selected, R.mipmap.ic_navigate_dashboard_unselect, 0, DashboardFragment.class));
            add(new MyTabEntity("群众督", R.mipmap.ic_navigate_masses_selected, R.mipmap.ic_navigate_masses_unselect, 1, MassesFragment.class));
            add(new MyTabEntity("我的", R.mipmap.ic_navigate_user_selected, R.mipmap.ic_navigate_user_unselect,2, PersonFragment.class));
        }
    };

    private Banner banner;

    private TextView tvTitle;

    private ImageTextView barFun;

    private CustomPopWindow mPopMenuWindow;

//    private NavHostController navController;

    private ViewPager viewPager;

    private CommonTabLayout commonTabLayout;

    private DashboardFragment mDashboardFragment;

    private PopWindowCategoryAdapter.MenuBean menuBean = new PopWindowCategoryAdapter.MenuBean(0, "政府工作报告", 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        findViewById(R.id.iv_base_bar_arrow).setVisibility(View.GONE);

        tvTitle = findViewById(R.id.tv_base_bar_title);

        tvTitle.setText("政府工作报告");

        barFun = findViewById(R.id.itv_base_bar_fun);

        barFun.setOnClickListener(mBarFuncClick);

        banner = findViewById(R.id.banner);

        barFun.getImageView().setImageResource(R.mipmap.ic_category_more);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(mPageChangeListener);
        commonTabLayout = findViewById(R.id.common_tablayout);
        commonTabLayout.setTabData(mTabEntitys);
        commonTabLayout.setOnTabSelectListener(mTabSelectListener);
        requestBanner();
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            refreshNavigation(position);
            JokerLog.e("viewpager select position: " + position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private OnTabSelectListener mTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position) {
            JokerLog.e("tab select position: " + position);
            viewPager.setCurrentItem(position);
        }

        @Override
        public void onTabReselect(int position) {

        }
    };

    private void refreshNavigationTitle() {
        if (tvTitle == null || menuBean == null || TextUtils.isEmpty(menuBean.title)) return;
        tvTitle.setText(menuBean.title);
    }

    private void refreshNavigation(int position) {
        tvTitle.setText(mTabEntitys.get(position).getTabTitle());
        if (position == 0) {
            refreshNavigationTitle();
        }
        barFun.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
        banner.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
        commonTabLayout.setCurrentTab(position);
    }

    private void requestBanner() {
        OkGo.<List<JSONObject>>get(HttpUrl.BANNER).tag(this).execute(new JsonObjectCallBack<List<JSONObject>>() {
            @Override
            public void onSuccess(Response<List<JSONObject>> response) {
                showBanner(response.body());
            }
        });
    }

    public void showBanner(List<JSONObject> banners) {
        banner.setAdapter(new BannerImageAdapter<JSONObject>(banners) {
            @Override
            public void onBindView(BannerImageHolder holder, JSONObject data, int position, int size) {
                JokerLog.d(HttpUrl.ATTACHMENT + data.getString("image"));
                //图片加载自己实现
                Glide.with(holder.itemView)
                        .load(HttpUrl.ATTACHMENT + data.getString("image"))
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(holder.imageView);
            }
        });
        //添加生命周期观察者
        banner.addBannerLifecycleObserver(this);
        banner.setIndicator(new CircleIndicator(this));
    }


    private View.OnClickListener mBarFuncClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopListView(v);
        }
    };


    private void showPopListView(View asDropDown){
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_pop_window_category,null);
        //处理popWindow 显示内容
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 25, getResources().getColor(R.color.colorPrimary)));
        recyclerView.setAdapter(new PopWindowCategoryAdapter(this, mMenuClick));
        //创建并显示popWindow
        mPopMenuWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
                .showAsDropDown(asDropDown,0,20);
    }

    private View.OnClickListener mMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mPopMenuWindow != null) {
                mPopMenuWindow.dissmiss();
            }
            if (view.getTag() != null && view.getTag() instanceof PopWindowCategoryAdapter.MenuBean) {
                menuBean = (PopWindowCategoryAdapter.MenuBean)view.getTag();
//                tvTitle.setText(gov.android.com.superior.config.Config.categoryTitles.get(menuBean.category));
                refreshNavigationTitle();

                Bundle bundle = new Bundle();
                bundle.putInt("category", menuBean.category);
//                if (mDashboardFragment == null) {
//                    getCurrentDashboardFragment();
//                }
//                if (mDashboardFragment == null) {
//                    throw new NullPointerException("Un find DashboardFragment..........");
//                }
                ((MyTabEntity)mTabEntitys.get(0)).getFragment().updateBundle(bundle);
//                mDashboardFragment.updateBundle(bundle);
            }
        }
    };

    private DashboardFragment getCurrentDashboardFragment() {
        List<Fragment> fagments = getSupportFragmentManager().getFragments();
        if (fagments.size() > 0) {
             Fragment fragment = fagments.get(0);
             if (fragment instanceof NavHostFragment) {
                 fagments = fragment.getChildFragmentManager().getFragments();
                 if (fagments.size() > 0) {
                     fragment = fagments.get(0);
                     if (fragment instanceof DashboardFragment) {
                         mDashboardFragment = (DashboardFragment) fragment;
                         return mDashboardFragment;
                     }
                 }
             }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateVersion();
    }

    /**
     * 强制更新
     */
    public void updateVersion() {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setPost(true)
                .setUpdateUrl(HttpUrl.GET_NEW_VERSION)
                //实现httpManager接口的对象
                .setHttpManager(new OkGoUpdateHttpUtil())
                //全局异常捕获
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setTopPic(R.mipmap.top_8)
                //为按钮，进度条设置颜色。
                .setThemeColor(0xffffac5d)
                .build()
                .update();
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mTabEntitys.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabEntitys.get(position).getTabTitle();
        }

        @Override
        public Fragment getItem(int position) {
            JokerLog.e("getItem: " + position + " ------------------------->");
            return ((MyTabEntity)mTabEntitys.get(position)).getFragment();
        }
    }
}
