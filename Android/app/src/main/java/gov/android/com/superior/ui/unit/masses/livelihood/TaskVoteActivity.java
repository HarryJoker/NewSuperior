package gov.android.com.superior.ui.unit.masses.livelihood;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.flyco.roundview.RoundTextView;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.config.Config;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;

public class TaskVoteActivity extends BaseToolBarActivity {

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvLevel;
    private TextView tvVoteCount;
    private TextView tvPercent;
    private LinearLayout layoutExplan;
    private TextView tvExplan;
    private RoundTextView tvNewVote;

    private int opinionId;

    @Override
    public void onInitParams() {
        opinionId = getIntent().getIntExtra("opinionId", 0);
        if (opinionId == 0) {
            showToast("数据错误");
            finish();
        }
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("投票");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_task_vote;
    }

    @Override
    protected void onFindViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvLevel = (TextView) findViewById(R.id.tv_level);
        tvVoteCount = (TextView) findViewById(R.id.tv_vote_count);
        tvPercent = (TextView) findViewById(R.id.tv_percent);
        layoutExplan = findViewById(R.id.layout_explan);
        tvExplan = (TextView) findViewById(R.id.tv_explan);
        tvNewVote = (RoundTextView) findViewById(R.id.tv_new_vote);
    }

    @Override
    public void onInitView() {
        tvNewVote.setOnClickListener(mVoteClick);
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        requestVoteOpinion();
    }

    //获取投票事项
    private void requestVoteOpinion() {
        showLoading();
        OkGo.<JSONObject>post(HttpUrl.VOTE_OPINION + "/" + opinionId).tag(this).execute(getJsonObjectCallback(HttpUrl.VOTE_OPINION));
    }

    //投票
    private void requestOpinionVote() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", User.getInstance().getUserId() + "");
        params.put("opinionId", opinionId + "");
        showLoading("投票中...");
        OkGo.<JSONObject>post(HttpUrl.OPINION_VOTE ).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.OPINION_VOTE));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        hideLoading();
        if (action.equals(HttpUrl.VOTE_OPINION)) {
            refreshOpinion(data);
        }

        if (action.equals(HttpUrl.OPINION_VOTE)) {
            showToast("投票成功");
            finish();
        }
    }

    private void refreshOpinion(JSONObject data) {
        int category = data.getIntValue("category");
        String type = Config.opinionTypes.containsKey(category) ? Config.opinionTypes.get(category) : "未知问题类型";
        tvTitle.setText(type);

        tvContent.setText(data.getString("content"));
        tvLevel.setText(data.getString("rank"));
        tvVoteCount.setText(data.getString("voteCount"));
        tvPercent.setText(data.getString("ratio"));

        int status = data.getIntValue("status");
        if (status == 4) {
            tvNewVote.setVisibility(View.VISIBLE);
        } else {
            layoutExplan.setVisibility(View.VISIBLE);
            if (status <= 3) {
                tvExplan.setText("该意见事项暂未开启投票");
            }
        }
    }

    private View.OnClickListener mVoteClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestOpinionVote();
        }
    };
}
