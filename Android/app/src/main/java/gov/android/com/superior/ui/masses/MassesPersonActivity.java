package gov.android.com.superior.ui.masses;

import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.first.orient.base.activity.BaseToolBarActivity;

import gov.android.com.superior.R;
import gov.android.com.superior.ui.unit.person.PersonFragment;
import gov.android.com.superior.ui.unit.person.message.Messageragment;

public class MassesPersonActivity extends BaseToolBarActivity {

    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("个人中心");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_masses_person;
    }

    @Override
    protected void onFindViews() {

    }

    @Override
    public void onInitView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_content, new PersonFragment());
        transaction.commit();
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }
}
