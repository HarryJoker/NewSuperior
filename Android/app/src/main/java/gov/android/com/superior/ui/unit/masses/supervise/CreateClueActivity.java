package gov.android.com.superior.ui.unit.masses.supervise;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.flyco.roundview.RoundTextView;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

/**
 * 群众线索提交
 */
public class CreateClueActivity extends BaseToolBarActivity {

    private Map<Integer, Integer> mClueTypes = new HashMap<Integer, Integer>() {
        {
            put(R.id.rb_person, 0);
            put(R.id.rb_goverment, 1);
            put(R.id.rb_company, 2);
        }
    };

    private RadioGroup rgClueType;
    private EditText etTitle;
    private EditText etContent;
    private RecyclerView rcUploadAttachments;
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
        return R.layout.activity_create_clue;
    }

    @Override
    protected void onFindViews() {
        rgClueType = findViewById(R.id.rg_clue_types);
        etTitle = (EditText) findViewById(R.id.et_title);
        etContent = (EditText) findViewById(R.id.et_content);
        rcUploadAttachments = (RecyclerView) findViewById(R.id.rc_upload_attachments);
        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etCode = (EditText) findViewById(R.id.et_code);
        tvGetCode = (RoundTextView) findViewById(R.id.tv_get_code);
        tvNewClue = (RoundTextView) findViewById(R.id.tv_new_clue);
    }

    @Override
    public void onInitView() {
        rcUploadAttachments.setLayoutManager(new GridLayoutManager(this, 4));
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
        if (rgClueType.getCheckedRadioButtonId() == -1) {
            showToast("请选择线索问题类型");
            return false;
        }
        if (etTitle.getEditableText().toString().length() == 0) {
            showToast("请填写线索问题");
            return false;
        }
        if (etContent.getEditableText().toString().length() == 0) {
            showToast("请填写具体的问题描述");
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
        params.put("title", etTitle.getEditableText().toString());
        params.put("category", mClueTypes.get(rgClueType.getCheckedRadioButtonId()) + "");
        params.put("userName", etName.getEditableText().toString());
        params.put("phone", etPhone.getEditableText().toString());
        params.put("userId", User.getInstance().getUserId() + "");
        return params;
    }

    private void requestNewClue(Map<String, String> params) {
        showLoading("请稍后...");
        OkGo.<JSONObject>post(HttpUrl.NEW_CLUE).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_CLUE));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        showSuccessTip("提交成功");
        finish();
    }

    private List<JSONObject> mergeTasks(List<JSONObject> goods, List<JSONObject> bads) {
        List<JSONObject> tasks = new ArrayList();
        int maxCount = goods.size() > bads.size() ? goods.size() : bads.size();
        if (maxCount == 0) return tasks;
        for (int n = 0; n < maxCount; n++) {
            JSONObject item = new JSONObject();
            item.put("goodTask",n < goods.size() ? goods.get(n) : null);
            item.put("badTask", n < bads.size() ? bads.get(n) : null);
            tasks.add(item);
        }
        return tasks;
    }


    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }
}
