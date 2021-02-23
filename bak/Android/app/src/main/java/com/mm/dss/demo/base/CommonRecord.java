package com.mm.dss.demo.base;

import com.android.business.entity.UserInfo;

/**
 * Created by 26499 on 2018/4/9.
 */

public class CommonRecord {
    private UserInfo mUserInfo;
    private String ip;
    private int port;

    private static volatile CommonRecord _CommonRecord;
    private static final Object _CommonModuleProxyLock = new Object();
    public static CommonRecord getInstance() {
        if (_CommonRecord == null) {
            synchronized (_CommonModuleProxyLock) {
                if (_CommonRecord == null) {
                    _CommonRecord = new CommonRecord();
                }
            }
        }
        return _CommonRecord;
    }

    public void setUserInfo(UserInfo info) {
        mUserInfo = info;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
