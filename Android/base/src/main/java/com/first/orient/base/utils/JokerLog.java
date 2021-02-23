package com.first.orient.base.utils;

import android.util.Log;

/**
 * Author: harryjoker
 * Created on: 2020-01-20 21:35
 * Description:
 */
public class JokerLog {

    private static boolean isDebug = true;

    private static String TAG = "JokerLog";

    private static void init(String tagName) {
        TAG = tagName;
    }

    private static void init(String tagName, boolean debug) {
        TAG = tagName;
        isDebug = debug;
    }


    public static void debug(boolean debug) {
        isDebug = debug;
    }


    public static void i(String msg) {
        if (!isDebug) return;
        Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (!isDebug) return;
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (!isDebug) return;
        Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (!isDebug) return;
        Log.v(TAG, msg);
    }

}
