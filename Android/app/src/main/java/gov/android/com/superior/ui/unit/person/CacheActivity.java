package gov.android.com.superior.ui.unit.person;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.first.orient.base.activity.BaseToolBarActivity;

import gov.android.com.superior.R;

public class CacheActivity extends BaseToolBarActivity {

    @Override
    public void onInitParams() {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_cache;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("清空缓存");
    }

    @Override
    protected void onFindViews() {

    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }


    public void clearClick(View v) {
        showLoading("清除中...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoading();
                Toast.makeText(getApplicationContext(), "已清空缓存", Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }
}
