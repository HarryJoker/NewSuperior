package com.first.orient.base;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

public class AppContext {

   private static BaseApplication instance;

   public static void setInstance(BaseApplication application) {
       instance = application;
   }

    public static Application getApplication() {
        return instance;
    }
}
