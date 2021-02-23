package gov.android.com.superior.advice;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.Map;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;

public class AdviceActivity extends BaseLoadActivity {

    private EditText et_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        et_content = (EditText)findViewById(R.id.et_content);

        setTitle("意见建议");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void adviceClick(View v) {
        if (TextUtils.isEmpty(et_content.getText().toString())) {
            Toast.makeText(this, "请输入意见建议", Toast.LENGTH_LONG).show();
        } else {
            showProgress("提交中");
            OkGo.<Map>post(Config.ADVICE_CREATE).tag(this).params("userId", User.getInstance().getUserId()).params("content", et_content.getText().toString()).execute(jsonCallback);
        }
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
            removeProgress();
            Toast.makeText(AdviceActivity.this, "提交成功", Toast.LENGTH_LONG).show();
        }
    };

}
