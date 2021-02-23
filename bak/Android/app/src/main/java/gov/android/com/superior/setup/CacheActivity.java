package gov.android.com.superior.setup;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;

public class CacheActivity extends BaseLoadActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        setTitle("清空缓存");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void clearClick(View v) {
        showProgress("清除中...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeProgress();
                Toast.makeText(getApplicationContext(), "已清空缓存", Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }
}
