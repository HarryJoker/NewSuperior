package gov.android.com.superior.ui.unit.masses.livelihood.comment;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * 提交评价
 */
public class CreateCommentActivity extends BaseToolBarActivity {

    private int unitTaskId;

    private MaterialRatingBar rbScore;
    private EditText etContent;

    private TextView tvScore;

    @Override
    public void onInitParams() {
        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_comment;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("评价");
    }

    @Override
    protected void onFindViews() {
        rbScore =  findViewById(R.id.rb_score);
        tvScore = findViewById(R.id.tv_score);
        etContent = findViewById(R.id.et_content);
    }

    @Override
    public void onInitView() {
        rbScore.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                tvScore.setText(rating == 0 ? "" : (rating + "分"));
            }
        });
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

    }

    public void newCommentClick(View v) {
        if (rbScore.getRating() == 0) {
            showToast("请进行评价打分");
            return;
        }
        if (etContent.getEditableText().toString().length() == 0) {
            showToast("请填写您的评价");
            return;
        }
        requestNewComment();
    }

    //投票
    private void requestNewComment() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", User.getInstance().getUserId() + "");
        params.put("unitTaskId", unitTaskId + "");
        params.put("score", rbScore.getRating() + "");
        params.put("comment", etContent.getEditableText().toString());
        showLoading("请稍后...");
        OkGo.<JSONObject>post(HttpUrl.NEW_COMMENT).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_COMMENT));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        hideLoading();
        showToast("评价成功");
        finish();
    }
}
