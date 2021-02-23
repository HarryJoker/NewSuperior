package gov.android.com.superior.http;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.business.adapter.DataAdapterImpl;
import com.android.business.adapter.DataAdapterInterface;
import com.android.business.entity.EnvironmentInfo;

import gov.android.com.superior.BuildConfig;

/**
 * Created by 26499 on 2019/1/15.
 */

public class EnvironmentHelper {
    private DataAdapterInterface dataAdapterInterface;
    private EnvironmentInfo envInfo;
    private String IMEI;
    private boolean isUseHttps = BuildConfig.isVCloud || BuildConfig.isExpress;
    private EnvironmentHelper() {
        dataAdapterInterface = DataAdapterImpl.getInstance();
        envInfo = new EnvironmentInfo();
    }

    public static class Instance {
        static EnvironmentHelper instance = new EnvironmentHelper();
    }

    public static EnvironmentHelper getInstance() {
        return Instance.instance;
    }
    public void initEnvironment(Context context) throws Exception {
        if (!(context instanceof Application)) {
            throw new Exception("context must instance application");
        }

        envInfo.setApplication(context);
        setVersionName(context);
        setCacheFile(context);
        setClientMac(context);
        setClientType("android-phone");
        setLanguage(context);
        envInfo.setHttps(isUseHttps());
        dataAdapterInterface.initEnvironmentInfo(envInfo);
    }

    private EnvironmentInfo setVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            envInfo.setVersionName(pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return envInfo;
    }

    private EnvironmentInfo setCacheFile(Context context) {
        String userAgent = "volley/0";
        envInfo.setCacheDir(context.getCacheDir());
        envInfo.setUserAgent(userAgent);
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return envInfo;
    }

    private EnvironmentInfo setClientMac(Context context) {
        if(TextUtils.isEmpty(IMEI)){
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                IMEI = tm.getDeviceId();
            }
        }
        if(TextUtils.isEmpty(IMEI)){
            IMEI = "11:22:33:44:55:66";
        }
        envInfo.setClientMac(IMEI);
        return envInfo;
    }

    public EnvironmentInfo setLanguage(Context context) {
        String language = context.getResources().getConfiguration().locale.getLanguage();
        envInfo.setLanguage(language);
        return envInfo;
    }

    private EnvironmentInfo setClientType(String clientType) {
        envInfo.setClientType(clientType);
        return envInfo;
    }

    public boolean isUseHttps() {
        return isUseHttps;
    }
}
