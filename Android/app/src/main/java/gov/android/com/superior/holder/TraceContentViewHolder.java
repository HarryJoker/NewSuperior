package gov.android.com.superior.holder;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.utils.JokerLog;
import com.lid.lib.LabelTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import gov.android.com.superior.R;
import gov.android.com.superior.config.Config;
import gov.android.com.superior.http.HttpUrl;

public class TraceContentViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

    private int mTraceSize = 0;

    RoundedImageView ivAvatar;
    TextView tvUnitName;
    TextView tvLineTop;
    TextView tvLineBottom;

    LabelTextView tvTrace;

    LinearLayout layoutQuestion;
    TextView tvQuestion;

    LinearLayout layoutStep;
    TextView tvStep;

    LinearLayout layoutProgress;
    TextView tvProgressLabel;
    TextView tvProgress;
    ProgressBar pbProgress;

    TextView tvTime;
    TextView tvUserName;

    public TraceContentViewHolder(@NonNull Activity context, @NonNull ViewGroup parent, int traceSize) {
        this(context.getLayoutInflater().inflate(R.layout.rc_item_trace_content, parent, false), traceSize);
    }

    public TraceContentViewHolder(View convertView, int size) {
        super(convertView);
        this.mTraceSize = size;
        ivAvatar = convertView.findViewById(R.id.lv_logo);
        tvUnitName = convertView.findViewById(R.id.tv_unit_name);
        tvLineTop =  convertView.findViewById(R.id.tv_vertical_top);
        tvLineBottom = convertView.findViewById(R.id.tv_vertical_bottom);

        tvTrace =  convertView.findViewById(R.id.tv_trace);

        layoutQuestion = convertView.findViewById(R.id.layout_question);
        tvQuestion = convertView.findViewById(R.id.tv_question);

        layoutStep = convertView.findViewById(R.id.layout_step);
        tvStep = convertView.findViewById(R.id.tv_step);

        layoutProgress = convertView.findViewById(R.id.layout_progress);
        tvProgressLabel = convertView.findViewById(R.id.tv_progress_label);
        tvProgress = convertView.findViewById(R.id.tv_progress);
        pbProgress = convertView.findViewById(R.id.pb_progress);

        tvTime =  convertView.findViewById(R.id.tv_time);
        tvUserName = convertView.findViewById(R.id.tv_userName);
    }

    @Override
    public void bindViewHolder(int position, JSONObject data) {
        if (data == null) return;
        if (data.getIntValue("userId") == 0 || data.getString("unitName").equals("督查室")) {
            Glide.with(itemView.getContext()).load(HttpUrl.makeAttachmentUrl("logo_dc.png")).into(ivAvatar);
            tvUnitName.setText("督查室");
            tvUserName.setText("督查室");
        } else if (data.getIntValue("userId") == -1) {
            ivAvatar.setImageResource(R.mipmap.ic_avatar);
            tvUnitName.setText("机器人");
            tvUserName.setText("机器人");
        } else {
            Glide.with(itemView.getContext()).load(HttpUrl.makeAttachmentUrl(data.getString("unitLogo"))).into(ivAvatar);
            tvUnitName.setText(data.getString("unitName"));
            tvUserName.setText(data.getString("userName"));
        }

        tvLineTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        tvLineBottom.setVisibility(position == mTraceSize - 1 ? View.INVISIBLE : View.VISIBLE);

        tvTrace.setText(data.getString("content"));
        tvTrace.setLabelText(Config.STATUS.get(data.getString("status")));

        layoutQuestion.setVisibility(View.GONE);
        layoutStep.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(data.getString("question"))) {
            layoutQuestion.setVisibility(View.VISIBLE);
            tvQuestion.setText(data.getString("question"));
        }
        if (!TextUtils.isEmpty(data.getString("step"))) {
            layoutStep.setVisibility(View.VISIBLE);
            tvStep.setText(data.getString("step"));
        }
        int status = data.getIntValue("status");
//        "31", "已报送" ,"71", "已审核" ,"72", "进度缓慢", "73", "进度较快"
        if (status == 31 || status == 71 || status == 72 | status == 73) {
            layoutProgress.setVisibility(View.VISIBLE);
            tvProgressLabel.setText(status == 31 ? "报送进度：" : "审核进度");
            tvProgress.setText(data.getString("progress") + "%");
            pbProgress.setProgress(data.getIntValue("progress"));
        } else {
            layoutProgress.setVisibility(View.GONE);
        }
        tvTime.setText(data.getString("createtime"));
    }
}
