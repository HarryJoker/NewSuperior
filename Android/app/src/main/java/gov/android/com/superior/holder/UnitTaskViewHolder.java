package gov.android.com.superior.holder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.utils.JokerLog;
import com.lid.lib.LabelTextView;

import java.util.HashMap;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.config.Config;

/**
 * 部门任务列表的ViewHolder
 */
public class UnitTaskViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

    private int category = 0;

    ImageView ivStatus;
    TextView tvTitle;
    TextView tvTitleLabel;
    TextView tvContent;
    TextView tvContentLabel;
    TextView tvUnit;
    TextView tvProgress;
    ProgressBar pbProgress;
    LabelTextView tvLabel;

    public UnitTaskViewHolder(@NonNull Activity context, @NonNull ViewGroup parent, @NonNull int category) {
        this(context.getLayoutInflater().inflate(R.layout.rc_item_unit_task, parent, false), category);
    }

    public UnitTaskViewHolder(@NonNull View itemView, int category) {
        super(itemView);
        this.category = category;
        ivStatus = itemView.findViewById(R.id.iv_status);
        tvTitle =  itemView.findViewById(R.id.tv_title);
        tvTitleLabel = itemView.findViewById(R.id.tv_title_label);
        tvContent = itemView.findViewById(R.id.tv_content);
        tvContentLabel = itemView.findViewById(R.id.tv_content_label);
        tvUnit = itemView.findViewById(R.id.tv_unit);
        tvProgress = itemView.findViewById(R.id.tv_progress);
        pbProgress = itemView.findViewById(R.id.pb_progress);
        tvLabel = itemView.findViewById(R.id.tv_label);
    }

    @Override
    public void bindViewHolder(int position, JSONObject data) {
        if (Config.labelMetas.containsKey(category)) {
            tvTitleLabel.setText(Config.labelMetas.get(category)[1]);
            tvContentLabel.setText(Config.labelMetas.get(category)[2]);
        }
        tvTitle.setText(data.getString("title"));
        tvContent.setText(data.getString("content"));
        JSONArray unitTasks = data.getJSONArray("unitTasks");
        if (unitTasks.size() >= 1) {
            JSONObject unitTask = unitTasks.getJSONObject(0);
            tvUnit.setText(unitTask.getString("unitName"));
            tvProgress.setText(unitTask.getString("verifyProgress") + "%");
            pbProgress.setProgress(unitTask.getIntValue("verifyProgress"));
            String label = Config.STATUS.get(unitTask.getString("status"));
            label = label == null ? "未识别" : label;
            JokerLog.d(label);
            tvLabel.setLabelText(label);

            if (category == 1) {
                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(Config.lightStatus.containsKey(unitTask.getString("progressStatus")) ? Config.lightStatus.get(unitTask.getString("progressStatus")) : R.mipmap.icon_light_yellow);
            }
        }
    }
}
