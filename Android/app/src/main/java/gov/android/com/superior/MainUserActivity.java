package gov.android.com.superior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.zhouwei.library.CustomPopWindow;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.first.orient.base.callback.JsonObjectCallBack;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.utils.JokerLog;
import com.first.orient.base.utils.OkGoUpdateHttpUtil;
import com.first.orient.base.view.ImageTextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.listener.ExceptionHandler;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.List;

import gov.android.com.superior.adapter.PopWindowCategoryAdapter;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.masses.MassesPersonActivity;

import static com.first.orient.base.fragment.BaseFragment.makeBundleToJson;

public class MainUserActivity extends AppCompatActivity {

    private Banner banner;

    private TextView tvTitle;

    private ImageTextView barFun;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        findViewById(R.id.iv_base_bar_arrow).setVisibility(View.GONE);

        tvTitle = findViewById(R.id.tv_base_bar_title);

        tvTitle.setText("群众督");

        barFun = findViewById(R.id.itv_base_bar_fun);

        barFun.getImageView().setImageResource(R.mipmap.ic_user_setup);

        barFun.setOnClickListener(mBarFuncClick);

        banner = findViewById(R.id.banner);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_dashboard, R.id.navigation_masses, R.id.navigation_person).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        requestBanner();
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
            startActivity(new Intent(MainUserActivity.this, MassesPersonActivity.class));
        }
    };

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
}
