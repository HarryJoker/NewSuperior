package com.first.orient.base.utils;

import android.content.Context;

/**
 * Author: harryjoker
 * Created on: 2019-11-12 21:21
 * Description:
 */
public class DpUtils {

    private static float density = 0.0f;

    public static float getSmallestScreenDimen(Context context, int resDimenId) {
        float dimen = context.getResources().getDimension(resDimenId);
        return dimen / getCurrentDensity(context);
    }

    public static int getSmallestScreenWidth(Context context, int resDimenId) {
        float dimen = context.getResources().getDimension(resDimenId);
        dimen = dimen / getCurrentDensity(context);
        return dp2px(context, dimen);
    }

    private static int dp2px(Context context, float dp) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        } catch (Exception e) {
            e.printStackTrace();
            return (int) 0;
        }
    }


    private static float getCurrentDensity (Context context) {
        if (density != 0) return density;
        return density = context.getResources().getDisplayMetrics().density;
    }

}
