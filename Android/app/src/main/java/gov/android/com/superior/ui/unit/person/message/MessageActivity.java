package gov.android.com.superior.ui.unit.person.message;

import android.widget.TextView;

import com.first.orient.base.activity.BaseToolBarActivity;
import com.first.orient.base.callback.JsonCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;

public class MessageActivity extends BaseToolBarActivity {

    private long messageId;

    private TextView tv_content;

    @Override
    public void onInitParams() {
        messageId = getIntent().getLongExtra("messageId", 0);
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("我的消息");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_message;
    }

    @Override
    protected void onFindViews() {
        tv_content = (TextView) findViewById(R.id.tv_content);
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        asyncGetMessage();
    }

    private void asyncGetMessage() {
        showLoading("请稍后...");
        OkGo.<Map>get(HttpUrl.MESSAGE_GET + "/" + messageId).tag(this).execute(jsonCallback);
    }

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            hideLoading();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<Map> response) {
             tv_content.setText(response.body().get("content").toString());
            hideLoading();
        }
    };

}
