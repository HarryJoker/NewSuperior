package com.first.orient.base.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.R;
import com.first.orient.base.callback.JsonObjectCallBack;
import com.first.orient.base.view.StatusView;
import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Author: harryjoker
 * Created on: 2020-01-02 19:51
 * Description:
 */
public abstract class BaseContentActivity extends StatusBarActivity {

//    private ViewGroup layoutTip;
//    private ViewGroup layoutLoading;
    private ViewGroup layoutContent;

    private StatusView layoutStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.i(TAG + " : onInitParams--------------->");
        onInitParams();

        int layRes = getBaseLayoutRes();
        super.setContentView(layRes <= 0 ? R.layout.layout_content : layRes);

        findRegionView();

        setChildContentView();

        Logger.i(TAG + " : onFindViews--------------->");
        onFindViews();

        Logger.i(TAG + " : onInitView--------------->");
        onInitView();

        Logger.i(TAG + " : onInitPresenter--------------->");
        onInitPresenter();

        onBusiness();
    }

    protected int getBaseLayoutRes() {
        return 0;
    }

    protected void findRegionView() {
        layoutContent = findViewById(R.id.layout_main);
    }

    private void setChildContentView() {
        Logger.i(TAG + " : inflate Layout Res --------------->");
        if (getLayoutRes() > 0) {
            setContentView(getLayoutRes());
        }
    }

    /**
     * 初始化参数
     */
    public abstract void onInitParams();

    /**
     * 当前页面布局文件：LayoutRes
     * @return
     */
    public abstract int getLayoutRes();

    /**
     * 当前布局文件进行所有view的find
     */
    protected abstract void onFindViews();

    /**
     * 设置view初始化：如（setClick，setAdapter...）
     */
    public abstract void onInitView();

    /**
     * 构造初始化用到的所有Presenter
     */
    public abstract void onInitPresenter();

    /**
     * 开始业务处理：如网络请求...
     */
    public abstract void onBusiness();

    @Override
    public void setContentView(View view) {
        Logger.i(TAG +":  setChildContentView ------------------->");
        if (view != null && layoutContent != null) {
            layoutContent.addView(view, 0);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutRes) {
        if (layoutContent != null) {
            setContentView(getLayoutInflater().inflate(layoutRes, layoutContent, false));
        }
    }

    /**
     * 成功tip提示
     * @param tip
     */
    public void showSuccessTip(String tip) {
        showTip(tip, R.mipmap.ic_success);
    }

    /**
     * 警告tip提示
     * @param tip
     */
    public void showWarnTip(String tip) {
        showTip(tip, R.mipmap.ic_warn);
    }

    /**
     * 错误tip提示
     * @param tip
     */
    public void showErrorTip(String tip) {
        showTip(tip, R.mipmap.ic_error);
    }

    /**
     * 成功tip提示
     * 重写onDestoryTip(tipId)回调,tip结束事件通知（根据id进行分别事件）
     * @param tip
     * @param tipId
     */
    public void showSuccessTip(String tip, int tipId) {
        showTip(tip, R.mipmap.ic_success, tipId);
    }

    public void showWarnTip(String tip, int tipId) {
        showTip(tip, R.mipmap.ic_warn, tipId);
    }

    public void showErrorTip(String tip, int tipId) {
        showTip(tip, R.mipmap.ic_error, tipId);
    }

    /**
     * 自定义Tip展示
     * @param tip       提示信息
     * @param iconRes   提示图标
     */
    private void showTip(String tip, int iconRes) {
        showTip(tip, iconRes, -1);
    }

    private void initLayoutStatus() {
        if (layoutContent == null) {
            throw new NullPointerException("Content Layout can not inflate out");
        }
        if (layoutStatus == null) {
            layoutStatus = (StatusView) getLayoutInflater().inflate(R.layout.layout_status_view, layoutContent, false);
            layoutContent.addView(layoutStatus, layoutContent.getChildCount());
        }
    }

    /**
     * 展示tip
     * @param tip
     * @param iconRes
     * @param tipId
     */
    private void showTip(String tip, int iconRes, int tipId) {
        initLayoutStatus();
        if (layoutStatus == null) return;
        layoutStatus.showTip(tip, iconRes, tipId);
    }

    protected void onDestroyTip(int tipId) {

    }

    public void showError() {
        showError("请求数据错误了");
    }

    public void showError(String error) {
        showError(error, R.mipmap.ic_error);
    }

    public void showError(String error, int imgRes) {
        initLayoutStatus();
        if (layoutStatus == null) return;
        layoutStatus.showError(imgRes, error, "");
    }

    public void showUnNet() {
        showUnNet("您的网络好像走丢了");
    }

    public void showUnNet(String msg) {
        showUnNet(msg, R.drawable.icon_no_wifi);
    }

    public void showUnNet(String msg, int imgRes) {
        initLayoutStatus();
        if (layoutStatus == null) return;
        layoutStatus.showUnNet(imgRes, msg, "");
    }



    public void showEmpty() {
        showEmpty("哎呀，空空如也呢");
    }

    public void showEmpty(String empty) {
        showEmpty(R.drawable.icon_empty, empty);
    }

    public void showEmpty(int imgRes, String empty) {
        showEmpty(imgRes, empty, "");
    }

    public void showEmpty(int imgRes, String empty, String subEmpty) {
        initLayoutStatus();
        if (layoutStatus == null) return;
        layoutStatus.showEmpty(imgRes, empty, subEmpty);
    }

    public void showLoading() {
        showLoading(getString(R.string.loading));
    }

    /**
     * 显示加载状态
     * @param msg
     */
    public void showLoading(String msg) {
        showLoading(R.drawable.loading, msg);
    }

    /**
     * 显示加载状态
     * @param imgRes
     * @param msg
     */
    public void showLoading(int imgRes, String msg) {
        initLayoutStatus();
        if (layoutStatus == null) return;
        layoutStatus.showLoading(imgRes, msg);
    }

    /**
     * 隐藏加载状态
     */
    public void hideLoading() {
        if (layoutStatus != null && layoutStatus.getVisibility() == View.VISIBLE) {
            layoutStatus.hideStatusView();
        }
    }

    public void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showCallbckError(Response response) {
        if (response == null || response.getException() == null) return;
        String msg = "";
        if (response.getException() instanceof ConnectException || response.getException() instanceof UnknownHostException) {
            msg = "未连接网络，请检查网络设置";
        } else if (response.getException() instanceof SocketTimeoutException) {
            msg = "请求服务器超时";
        } else if (response.getException() instanceof HttpException) {
            msg = "服务器无响应";
        } else  if (response.getException() instanceof StorageException) {
            msg = "SD卡不存在或没有写权限";
        } else if (response.getException() instanceof IllegalStateException) {
            msg = response.getException().getMessage();
        } else if (response.getException() instanceof JsonSyntaxException) {
            msg = "数据出错了";
        }
        if (msg != null && msg.length() > 0) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {

    }

    protected void onJsonObjectCallBack(String action, JSONObject data) {

    }

    protected void onErrorResponse() {

    }

    protected JsonObjectCallBack<List<JSONObject>> getJsonArrayCallback() {
        return getJsonArrayCallback("default");
    }

    protected JsonObjectCallBack<JSONObject> getJsonObjectCallback() {
        return getJsonObjectCallback("default");
    }

    protected JsonObjectCallBack<List<JSONObject>> getJsonArrayCallback(final String action) {
        return new JsonObjectCallBack<List<JSONObject>>() {
            @Override
            public void onSuccess(Response<List<JSONObject>> response) {
                hideLoading();
                onJsonArrayCallBack(action, response.body());
            }

            @Override
            public void onError(Response<List<JSONObject>> response) {
                super.onError(response);
                hideLoading();
                onErrorResponse();
                showCallbckError(response);
            }
        };
    }

    protected JsonObjectCallBack<JSONObject> getJsonObjectCallback(final String action) {
        return new JsonObjectCallBack<JSONObject>() {
            @Override
            public void onSuccess(Response<JSONObject> response) {
                hideLoading();
                onJsonObjectCallBack(action, response.body());
            }
            @Override
            public void onError(Response<JSONObject> response) {
                super.onError(response);
                hideLoading();
                onErrorResponse();
                showCallbckError(response);
            }
        };
    }

}
