package gov.android.com.superior.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("关于我们");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
