package com.first.orient.base.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.first.orient.base.R;
import com.flyco.roundview.RoundLinearLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: harryjoker
 * Created on: 2020-01-09 15:34
 * Description: 组合自定义状态StatusView
 * 用于展示loading，Empty，Error，Success等状态提示
 */
public class StatusView extends LinearLayout {
    private static final int DELAY_INVISIBLE = 1850;

    public static final int VIEW_STATUS_ERROR       = 0X01;
    public static final int VIEW_STATUS_WARN        = 0X05;
    public static final int VIEW_STATUS_SUCCESS     = 0X06;
    public static final int VIEW_STATUS_UNNET       = 0X02;
    public static final int VIEW_STATUS_EMPTY       = 0X03;
    public static final int VIEW_STATUS_LOADING     = 0X04;
    public static final int VIEW_STATUS_INVISIBLE   = 0X10;

    private Map<Integer, Integer> statusBgColors = new HashMap<Integer, Integer>() {
        {
            put(VIEW_STATUS_ERROR, Color.parseColor("#DDFF0062"));
            put(VIEW_STATUS_WARN, Color.parseColor("#DDFF0062"));
            put(VIEW_STATUS_SUCCESS, Color.parseColor("#DDFF0062"));
            put(VIEW_STATUS_EMPTY, Color.TRANSPARENT);
            put(VIEW_STATUS_LOADING, Color.parseColor("#E4E8E6E6"));
        }
    };


    private static final int MSG_FINISH_TIP = 0xF01;

    private ImageView mStatusImage;
    private TextView mTvTitle;
    private TextView mTvSubTitle;

    public StatusView(Context context) {
        super(context);
        init();
    }

    public StatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        inflate(getContext(), R.layout.layout_status, this);
        mTvTitle = findViewById(R.id.tv_title);
        mStatusImage = findViewById(R.id.iv_status_src);
        mTvSubTitle = findViewById(R.id.tv_sub_title);

        Log.d("StatusView", "title:" + mTvTitle + ", subTitle:" + mTvSubTitle + ", image:" + mStatusImage);
    }

    /**
     * 成功tip提示
     * @param tip
     */
    public void showSuccessTip(String tip) {
//        showTip(tip, R.mipmap.ic_success);
        showStatusView(VIEW_STATUS_SUCCESS, R.mipmap.ic_success, tip, "");
    }

    /**
     * 警告tip提示
     * @param tip
     */
    public void showWarnTip(String tip) {
//        showTip(tip, R.mipmap.ic_warn);
        showStatusView(VIEW_STATUS_WARN, R.mipmap.ic_warn, tip, "");
    }

    /**
     * 错误tip提示
     * @param tip
     */
    public void showErrorTip(String tip) {
//        showTip(tip, R.mipmap.ic_error);
        showStatusView(VIEW_STATUS_ERROR, R.mipmap.ic_error, tip, "");
    }

    /**
     * 成功tip提示
     * 重写onDestoryTip(tipId)回调,tip结束事件通知（根据id进行分别事件）
     * @param tip
     * @param tipId
     */
    public void showSuccessTip(String tip, int tipId) {
//        showTip(tip, R.mipmap.ic_success, tipId);
        showStatusView(VIEW_STATUS_SUCCESS, R.mipmap.ic_success, tip, "");
        Message msg = mHandler.obtainMessage(MSG_FINISH_TIP, tipId, 0);
        mHandler.sendMessageDelayed(msg, DELAY_INVISIBLE);
    }

    public void showWarnTip(String tip, int tipId) {
//        showTip(tip, R.mipmap.ic_warn, tipId);
        showStatusView(VIEW_STATUS_WARN, R.mipmap.ic_warn, tip, "");
        Message msg = mHandler.obtainMessage(MSG_FINISH_TIP, tipId, 0);
        mHandler.sendMessageDelayed(msg, DELAY_INVISIBLE);
    }

    public void showErrorTip(String tip, int tipId) {
//        showTip(tip, R.mipmap.ic_error, tipId);
        showStatusView(VIEW_STATUS_ERROR, R.mipmap.ic_error, tip, "");
        Message msg = mHandler.obtainMessage(MSG_FINISH_TIP, tipId, 0);
        mHandler.sendMessageDelayed(msg, DELAY_INVISIBLE);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_FINISH_TIP) {
                setVisibility(View.GONE);
                onDestroyTip(msg.arg1);
            }
        }
    };

    /**
     * 展示tip
     * @param tip
     * @param iconRes
     * @param tipId
     */
    public void showTip(String tip, int iconRes, int tipId) {
//        mTvTitle.setText(tip == null || tip.trim().length() == 0 ? "Success" : tip);
//        mStatusImage.setImageResource(iconRes);
        showStatusView(VIEW_STATUS_ERROR, iconRes, tip, "");

        Message msg = mHandler.obtainMessage(MSG_FINISH_TIP, tipId, 0);
        mHandler.sendMessageDelayed(msg, DELAY_INVISIBLE);
        setVisibility(VISIBLE);
    }

    protected void onDestroyTip(int tipId) {

    }

    public void showLoading() {
        showLoading(getResources().getString(R.string.loading));
    }


    public void showError() {
        showStatusView(VIEW_STATUS_ERROR, R.drawable.icon_failed, "哎呀，请求数据出现错误了", "");
    }

    public void showError(int imgRes, String title, String subTitle) {
        showStatusView(VIEW_STATUS_ERROR, imgRes, title, subTitle);
    }

    public void showUnNet() {
        showStatusView(VIEW_STATUS_UNNET, R.drawable.icon_no_wifi, "哎呀，您的网络好像走丢了", "");
    }

    public void showUnNet(int imgRes, String title, String subTitle) {
        showStatusView(VIEW_STATUS_UNNET, imgRes, title, subTitle);
    }

    public void showEmpty() {
        showStatusView(VIEW_STATUS_EMPTY, R.drawable.icon_empty, "哎呀，空空如也呢", "");
    }

    public void showEmpty(int imgRes, String title, String subTitle) {
        showStatusView(VIEW_STATUS_EMPTY, imgRes, title, subTitle);
    }

    private void showStatusView(int status, int imgRes, String title, String subTitle) {
        if (status == VIEW_STATUS_INVISIBLE) {
            setVisibility(GONE);
            return;
        }
        if (imgRes > 0) {
            mStatusImage.setImageResource(imgRes);
        }
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(subTitle)) {
            mTvSubTitle.setText(subTitle);
        }
        mTvSubTitle.setVisibility(TextUtils.isEmpty(subTitle) ? GONE : VISIBLE);

        int color = Color.TRANSPARENT;
        if (statusBgColors.containsKey(status)) {
            color = statusBgColors.get(status);
        }
        setBackgroundColor(color);
        setVisibility(VISIBLE);
    }


    /**
     * 显示加载状态
     * @param msg
     */
    public void showLoading(String msg) {
        showLoading(R.drawable.loading, msg == null || msg.trim().length() == 0 ? getResources().getString(R.string.loading) : msg);
    }

    /**
     * 显示加载状态
     * @param msg
     */
    public void showLoading(int imgRes, String msg) {
        showStatusView(VIEW_STATUS_LOADING, imgRes, msg == null || msg.trim().length() == 0 ? getResources().getString(R.string.loading) : msg, "");
    }

    /**
     * 隐藏加载状态
     */
    public void hideStatusView() {
        setVisibility(View.GONE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MSG_FINISH_TIP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
