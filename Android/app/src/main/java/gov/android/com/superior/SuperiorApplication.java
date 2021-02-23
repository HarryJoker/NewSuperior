package gov.android.com.superior;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONObject;
import com.android.business.adapter.DataAdapteeImpl;
import com.android.business.adapter.DataAdapterImpl;
import com.android.business.adapter.DataAdapterInterface;
import com.android.business.adapter.DeviceWithChannelList;
import com.android.business.common.BroadCase;
import com.android.business.entity.ChannelInfo;
import com.android.business.entity.GroupInfo;
import com.android.business.entity.UserInfo;
import com.android.business.exception.BusinessException;
import com.first.orient.base.AppContext;
import com.first.orient.base.BaseApplication;
import com.first.orient.base.utils.JokerLog;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.List;
import java.util.Map;

import gov.android.com.superior.entity.CommonRecord;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.ChannelFactory;
import gov.android.com.superior.http.ChannelHelper;
import gov.android.com.superior.http.GroupFactory;
import gov.android.com.superior.http.GroupHelper;

public class SuperiorApplication extends BaseApplication {

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
    private static SuperiorApplication applicaiton;

    @Override
    public void onCreate() {
        super.onCreate();
        applicaiton = this;

        JokerLog.d(User.getInstance().getUser().toJSONString());

        float dimen = getResources().getDimension(R.dimen.dp_375);
        JokerLog.d("dimen:" + dimen);

        initDSSSdk();
    }

    public static SuperiorApplication getContext() {
        return applicaiton;
    }


    private void initDSSSdk() {
        try {
            loadLibrary();
            DataAdapterImpl.getInstance().createDataAdapter(BuildConfig.sdkPackage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void testGetChannelInfos() {
        ChannelHelper.getInstance().setmChannelInfoCallback(new ChannelHelper.ChannelInfoCallback() {
            @Override
            public void onChannelInfosResult(Map<String, ChannelInfo> channelInfos) {
                JokerLog.e("result infos:" + JSONObject.toJSONString(channelInfos));
            }

            @Override
            public void onError(String error) {
                JokerLog.e("result error:" + error);
            }
        });
        ChannelHelper.getInstance().requestChannelList();
    }

}
