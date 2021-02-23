package com.first.orient.base.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.View;
import androidx.annotation.Nullable;
import com.first.orient.base.utils.statusbar.Eyes;

/**
 * Author: harryjoker
 * Created on: 2020-01-02 18:32
 * Description:
 */
public abstract class StatusBarActivity extends BaseActivity {

    private View statusBarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Eyes.setStatusBarLightMode(this, Color.WHITE);

//        delaySetupStatusBar();
    }

    private void delaySetupStatusBar() {
        //延时加载数据.
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                if (isStatusBar()) {
                    initStatusBar();
                    getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            initStatusBar();
                        }
                    });
                }
                //只走一次
                return false;
            }
        });
    }

    protected boolean isStatusBar() {
        return true;
    }

    private void initStatusBar() {
        if (statusBarView == null) {
            int identifier = getResources().getIdentifier("statusBarBackground", "id", "android");
            statusBarView = getWindow().findViewById(identifier);
        }
        if (statusBarView != null) {
//            statusBarView.setBackgroundResource(R.drawable.shape_main);
            statusBarView.setBackgroundColor(Color.WHITE);
        }
    }
}
