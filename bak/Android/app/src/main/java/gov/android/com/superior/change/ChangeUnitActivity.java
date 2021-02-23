package gov.android.com.superior.change;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gov.android.com.superior.R;

public class ChangeUnitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeunit);

        setTitle("部门变更");
    }


    public void updateUserClick(View v) {

    }

}
