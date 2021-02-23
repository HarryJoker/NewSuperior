package gov.android.com.superior.ui.unit.person;

import android.widget.TextView;
import com.first.orient.base.activity.BaseToolBarActivity;
import gov.android.com.superior.R;

public class AboutActivity extends BaseToolBarActivity {

    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("关于我们");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_about;
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
}
