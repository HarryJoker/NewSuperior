package gov.android.com.superior.ui.unit.masses.livelihood;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.flyco.roundview.RoundTextView;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class CreateOpinionActivity extends BaseToolBarActivity {

    private Map<Integer, Integer> mOpinionTypes = new HashMap<Integer, Integer>() {
        {
            put(R.id.rb_city, 0);
            put(R.id.rb_education, 1);
            put(R.id.rb_economic, 2);
            put(R.id.rb_road, 3);
            put(R.id.rb_culture, 4);
        }
    };

    private RadioGroup rgOpinionType;
    private EditText etContent;
    private EditText etName;
    private EditText etPhone;
    private EditText etCode;
    private RoundTextView tvGetCode;
    private RoundTextView tvNewClue;

    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("提交线索");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_opinion;
    }

    @Override
    protected void onFindViews() {
        rgOpinionType = findViewById(R.id.rg_opinion_types);
        etContent = (EditText) findViewById(R.id.et_content);
        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etCode = (EditText) findViewById(R.id.et_code);
        tvGetCode = (RoundTextView) findViewById(R.id.tv_get_code);
        tvNewClue = (RoundTextView) findViewById(R.id.tv_new_clue);
    }

    @Override
    public void onInitView() {
        tvGetCode.setOnClickListener(mGetCodeClick);
        tvNewClue.setOnClickListener(mNewClick);
    }

    private View.OnClickListener mGetCodeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener mNewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateSmsCode()) {
                requestNewClue(makeParams());
            }
        }
    };

    private boolean validateSmsCode() {
        if (rgOpinionType.getCheckedRadioButtonId() == -1) {
            showToast("请选择建议问题类型");
            return false;
        }
        if (etContent.getEditableText().toString().length() == 0) {
            showToast("请填写具体的建议描述");
            return false;
        }
        if (etName.getEditableText().toString().length() == 0) {
            showToast("请填写您的真实姓名");
            return false;
        }
        if (etPhone.getEditableText().toString().length() == 0) {
            showToast("请填写您的手机号码");
            return false;
        }

        return true;
    }

    private Map<String, String> makeParams() {
        Map<String, String> params = new HashMap<>();
        params.put("content", etContent.getEditableText().toString());
        params.put("category", mOpinionTypes.get(rgOpinionType.getCheckedRadioButtonId()) + "");
        params.put("userName", etName.getEditableText().toString());
        params.put("phone", etPhone.getEditableText().toString());
        params.put("userId", User.getInstance().getUserId() + "");
        return params;
    }

    private void requestNewClue(Map<String, String> params) {
        showLoading("请稍后...");
        OkGo.<JSONObject>post(HttpUrl.NEW_OPINIION).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_OPINIION));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        showSuccessTip("提交成功,请等待审核");
        finish();
    }


    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }
}
