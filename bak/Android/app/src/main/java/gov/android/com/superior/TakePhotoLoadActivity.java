package gov.android.com.superior;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.jph.takephoto.app.TakePhotoActivity;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.model.Response;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class TakePhotoLoadActivity extends TakePhotoActivity implements AppCompatCallback {

    public AppCompatDelegate delegate;

    private View progressView;
    private FrameLayout screenLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //let's create the delegate, passing the activity at both arguments (Activity, AppCompatCallback)
        delegate = AppCompatDelegate.create(this, this);
        //we need to call the onCreate() of the AppCompatDelegate
        delegate.onCreate(savedInstanceState);

//        delegate.setTitle("任务提报");

//        delegate.getSupportActionBar().setTitle("任务提报");

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (layoutResID <= 0) {
            throw new  IllegalArgumentException("layoutResID le 0, can not find this Resource xml");
        } else {
            screenLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_base_load, null);

            View contentView = getLayoutInflater().inflate(layoutResID, screenLayout, false);

            screenLayout.addView(contentView, 0);

            //we use the delegate to inflate the layout
            delegate.setContentView(screenLayout);

//            delegate.getSupportActionBar().setTitle("提交个人信息");

            progressView = screenLayout.findViewById(R.id.layout_progress);
            progressView.bringToFront();
        }

    }

    protected void showProgress(String progressMsg) {
        if (progressView == null) progressView = screenLayout.findViewById(R.id.layout_progress);
        if (progressView != null) {
            if (progressMsg != null && progressMsg.trim().length() > 0) {
                ((TextView)progressView.findViewById(R.id.tv_screen_loading)).setText(progressMsg);
            }
            progressView.setVisibility(View.VISIBLE);
        }
    }

    protected void removeProgress() {
        if (progressView != null) progressView.setVisibility(View.INVISIBLE);
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
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }


}
