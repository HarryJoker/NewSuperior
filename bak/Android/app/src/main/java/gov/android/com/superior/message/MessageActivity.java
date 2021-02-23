package gov.android.com.superior.message;

import android.os.Bundle;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.util.Map;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.http.Config;

public class MessageActivity extends BaseLoadActivity {

    private long messageId;

    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        setTitle("通知消息");

        messageId = getIntent().getLongExtra("messageId", 0);

        tv_content = (TextView) findViewById(R.id.tv_content);

        asyncGetMessage();
    }

    private void asyncGetMessage() {
        showProgress("加载中");
        OkGo.<Map>get(Config.MESSAGE_GET + "/" + messageId).tag(this).execute(jsonCallback);
    }

    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            removeProgress();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<Map> response) {

            Logger.d(response.body());
            tv_content.setText(response.body().get("content").toString());
            removeProgress();

        }
    };

}
