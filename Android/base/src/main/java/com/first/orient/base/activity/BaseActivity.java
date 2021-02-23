package com.first.orient.base.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.i18n.Language;
import com.first.orient.base.i18n.MyContextWrapper;

import java.util.Set;

/**
 *
 * BaseActivity中正常的Activity的生命周期的log
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG;

    public BaseActivity() {
        TAG = this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, " : onCreate--------------------->" +  "， Task Id：" + getTaskId() + "\n" + "Intent Params:" + makeBundleToJson(getIntent().getExtras()) + "\n" + "SavedInstanceState:" + makeBundleToJson(savedInstanceState));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,  "onResume--------------------->" + "， Task Id：" + getTaskId());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,  "onRestart--------------------->" + "， Task Id：" + getTaskId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,  "onPause--------------------->" + "， Task Id：" + getTaskId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, ": onStart--------------------->" + "， Task Id：" + getTaskId());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,  "onStop--------------------->" + "， Task Id：" + getTaskId());
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i(TAG,  "onTrimMemory--------------------->" + "， Task Id：" + getTaskId());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG,  "onLowMemory--------------------->" + "， Task Id：" + getTaskId());
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG,  "onDetachedFromWindow--------------------->" + "， Task Id：" + getTaskId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInput();
        Log.i(TAG,  "onDestroy--------------------->" + "， Task Id：" + getTaskId());
    }

    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void onNewIntent(Intent data) {
        super.onNewIntent(data);
        String msg = "Intent: null";
        if (data != null) {
            msg = "Action: " + data.getAction() + "\n" +
                    "Intent Uri: " + data.getData() + "\n" +
                    "Intent Data:" + makeBundleToJson(data.getExtras());
            Log.i(TAG,  "onNewIntent--------------------->" + "， Task Id：" + getTaskId() + "\n" + msg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String msg = "Intent: null";
        if (data != null) {
            msg = "Action: " + data.getAction() + "\n" +
                    "Intent Uri: " + data.getData() + "\n" +
                    "RequestCode: " + requestCode + "\n" +
                    "ResultCode: " + resultCode + "\n" +
                    "Intent Data:" + makeBundleToJson(data.getExtras());
            Log.i(TAG,  "onActivityResult--------------------->" + "， Task Id：" + getTaskId() + "\n" + msg);
        }
    }

    protected JSONObject makeBundleToJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        if (bundle == null) return json;
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                json.put(key, bundle.get(key));
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(MyContextWrapper.wrap(newBase, Language.Public.Language));
//        Log.i(TAG,  "attachBaseContext--------------------->" + "， Language：" + Language.Public.Language);
//    }
}
