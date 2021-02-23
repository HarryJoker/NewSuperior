package com.first.orient.base;

import android.app.Application;

public abstract class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.setInstance(this);
    }
}
