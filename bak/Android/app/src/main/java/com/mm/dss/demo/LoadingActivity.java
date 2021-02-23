package com.mm.dss.demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import com.android.business.adapter.DataAdapterImpl;
import com.android.business.adapter.DataAdapterInterface;
import com.android.business.entity.UserInfo;
import com.android.business.exception.BusinessException;
import com.mm.dss.demo.base.BaseActivity;
import com.mm.dss.demo.base.CommonRecord;
import com.mm.dss.demo.permission.PermissionUtil;
import com.mm.dss.demo.permission.constant.PermissionConstant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import gov.android.com.superior.R;

public class LoadingActivity extends BaseActivity {

    private DataAdapterInterface dataAdapterInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dss_activity_loading);

        dataAdapterInterface = DataAdapterImpl.getInstance();

        setIPPort("222.134.55.34", "9000");

        login("zhengwuducha", "12345678");
    }


    private void setIPPort(String ip, String port) {
        try {
            dataAdapterInterface.initServer(ip, Integer.parseInt(port));

            CommonRecord.getInstance().setIp(ip);
            CommonRecord.getInstance().setPort(Integer.valueOf(port));
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler();

    private void login(final String username, final String password) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                String clientMac = tm.getDeviceId();
                UserInfo info = null;
                try {
                    info = dataAdapterInterface.login(username, password, "1", clientMac, 2);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                CommonRecord.getInstance().setUserInfo(info);

                if (info != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                        }
                    });
                }
            }
        }).start();
    }


    private boolean isPermissionsRequest = false;
    @Override
    protected void onResume() {
        super.onResume();
        if(!isPermissionsRequest) {
            requestAllPermissions();
            isPermissionsRequest = true;
        }
    }

    private void requestAllPermissions(){
        PermissionUtil permissionUtil = new PermissionUtil(new PermissionUtil.OnPermissionRequestListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {
                isPermissionsRequest = false;
            }

            @Override
            public void onPermissionSetting(boolean b) {
                isPermissionsRequest = false;
            }
        });
        List<String> permissionList = new ArrayList<>();
        permissionList.addAll(Arrays.asList(PermissionConstant.MICROPHONE));
        permissionList.addAll(Arrays.asList(PermissionConstant.STORAGE));
        permissionList.addAll(Arrays.asList(Manifest.permission.READ_PHONE_STATE));
        permissionUtil.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]));
    }

}
