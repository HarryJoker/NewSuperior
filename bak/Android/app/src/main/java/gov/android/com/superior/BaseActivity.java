package gov.android.com.superior;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends AppCompatActivity {

    String TAG = "";

    private View loading;

    private View statusBarView;

    public BaseActivity() {
        TAG = this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate--------------------->");

//        initStatusBar();
    }

    private void initStatusBar() {
        if (statusBarView == null) {
            int identifier = getResources().getIdentifier("statusBarBackground", "id", "android");
            statusBarView = getWindow().findViewById(identifier);
        }
        if (statusBarView != null) {
            statusBarView.setBackgroundResource(R.drawable.side_nav_bar2);
//            statusBarView.setBackgroundResource(R.color.colorPrimary);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume--------------------->");
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getName());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"onRestart--------------------->");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause--------------------->");
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart--------------------->");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop--------------------->");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i(TAG,"onTrimMemory--------------------->");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG,"onLowMemory--------------------->");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy--------------------->");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"onActivityResult--------------------->");
    }

    public void showLoading() {
        if (loading == null) {
            loading = findViewById(R.id.layout_progress);
        }
        if (loading != null) {
            loading.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if (loading != null) {
            loading.setVisibility(View.GONE);
        }
    }

    protected void hideInputMethod() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
