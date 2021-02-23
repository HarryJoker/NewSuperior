package gov.android.com.superior;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSON;
import com.android.business.adapter.DataAdapterImpl;
import com.android.business.adapter.DataAdapterInterface;
import com.android.business.entity.UserInfo;
import com.android.business.exception.BusinessException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.Response;
import com.mm.dss.demo.EnvironmentHelper;
import com.mm.dss.demo.LoadingActivity;
import com.mm.dss.demo.MainActivity;
import com.mm.dss.demo.base.CommonRecord;
import com.mm.dss.demo.push.DSSPush;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.task.UnitManager;
import okhttp3.OkHttpClient;

/**
 * Created by wanghua on 17/7/12.
 */

public class SuperiorApplicaiton extends Application {

    public static final int VERSION = 26;

    public static final String[] titles = new String[] {"政府工作报告", "市委市政府重大决策部署", "建议提案", "会议议定事项","领导批示", "专项督查", "重点项目"};

//    1：完成，2：快速，3：正常，4：缓慢
    public static final int[] taskStates = new int[] {
                                                    R.mipmap.icon_light_yellow,
                                                    R.mipmap.state_finish,
                                                    R.mipmap.icon_light_green,
                                                    R.mipmap.icon_light_yellow,
                                                    R.mipmap.icon_light_red,
                                                    R.mipmap.state_finish};

    private static SuperiorApplicaiton applicaiton;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.bg_white, R.color.gray);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
//        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
//            @Override
//            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                //指定为经典Footer，默认是 BallPulseFooter
//                return new ClassicsFooter(context).setDrawableSize(20);
//            }
//        });
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicaiton = this;

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        JPushInterface.setAlias(this, "alias", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
            }
        });

        JPushInterface.setAliasAndTags(this, "", null, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });

        Logger.addLogAdapter(new AndroidLogAdapter(){
            @Override
            public boolean isLoggable(int priority, String tag) {
                return true;
            }
        });

        MobclickAgent.setDebugMode(true);

        OkGo.getInstance().init(this);

        UnitManager.getInstace().aysncCacheUnits();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);


        CrashReport.initCrashReport(getApplicationContext(), "bbcb5d6878", true);


        initDSSSdk();

        Logger.d("Applicaiton create------------------------>");
    }

    public void asyncSetAlisAndTag() {
        //已审核过的用户，没有设置别名情况下
        if (User.getInstance().getUserId() > 0 && Integer.parseInt(User.getInstance().get("verify").toString()) == 1 && !User.getInstance().hasAlisAndTag()) {
            Set<String> tags = new HashSet<>();
            tags.add(User.getInstance().get("unitId").toString());
            JPushInterface.setAliasAndTags(this, User.getInstance().getUserId() + "", tags, tagAliasCallback);
        }
    }

    private TagAliasCallback tagAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int i, String s, Set<String> set) {
            Logger.d("tagAliasCallback:[code:" + i + ", alis:" + s + ", tags:" + set + "]");
            if (i == 0) {
                User.getInstance().update("hasAliaAndTag", true);
            }
        }
    };



    private void initDSSSdk() {
        try {
            DataAdapterImpl.getInstance().createDataAdapter(BuildConfig.sdkPackage);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        loadLibrary();

        initDSSPush();

//        loginDSS();
    }

    private void loadLibrary(){
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("CommonSDK");
        System.loadLibrary("Encrypt");
        if(BuildConfig.isVCloud) {
            System.loadLibrary("DSSCloudStream");
        } else if(BuildConfig.isExpress){
            System.loadLibrary("DPRTSPSDK");
            System.loadLibrary("DPExpressStream");
            System.loadLibrary("dsl");
            System.loadLibrary("dslalien");
        } else {
            System.loadLibrary("DPSStream");
            System.loadLibrary("dsl");
            System.loadLibrary("dslalien");
            System.loadLibrary("PlatformSDK");
            System.loadLibrary("DPRTSPSDK");
        }
    }

    private void initDSSPush() {
        try {
            DSSPush mDSSPush = new DSSPush();
            mDSSPush.init(this.getApplicationContext());
            EnvironmentHelper.getInstance().initEnvironment(this.getApplicationContext());
        } catch (BusinessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SuperiorApplicaiton getContext() {
        return applicaiton;
    }

}
