package gov.android.com.superior.ui.unit.person.message;

import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import com.first.orient.base.activity.BaseToolBarActivity;
import gov.android.com.superior.R;

public class MessageListActivity extends BaseToolBarActivity {

    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("我的消息");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_message_list;
    }

    @Override
    protected void onFindViews() {

    }

    @Override
    public void onInitView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_content, Messageragment.newInstance());
        transaction.commit();
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }
}
