package gov.android.com.superior.holder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;

import gov.android.com.superior.R;
import gov.android.com.superior.config.Config;

public class PersonUnitTaskViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

    TextView tvTitle;
    TextView tvTitleLabel;
    TextView tvContent;
    TextView tvContentLabel;

    TextView tvStatusContent;

    TextView tvTime;
    TextView tvUnit;

    public PersonUnitTaskViewHolder(@NonNull Activity context, @NonNull ViewGroup parent) {
        this(context.getLayoutInflater().inflate(R.layout.rc_item_people_unit_task, parent, false));
    }

    public PersonUnitTaskViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTitle =  itemView.findViewById(R.id.tv_title);
        tvTitleLabel = itemView.findViewById(R.id.tv_title_label);
        tvContent = itemView.findViewById(R.id.tv_content);
        tvContentLabel = itemView.findViewById(R.id.tv_content_label);

        tvStatusContent = itemView.findViewById(R.id.tv_status_content);

        tvTime = itemView.findViewById(R.id.tv_time);
        tvUnit = itemView.findViewById(R.id.tv_unit);
    }

    @Override
    public void bindViewHolder(int position, JSONObject data) {
        int category = data.getIntValue("category");
        if (Config.labelMetas.containsKey(category)) {
            tvTitleLabel.setText(Config.labelMetas.get(category)[1]);
            tvContentLabel.setText(Config.labelMetas.get(category)[2]);
        }
        tvTitle.setText(data.getString("title"));
        tvContent.setText(data.getString("content"));
        JSONArray unitTasks = data.getJSONArray("unitTasks");
        if (unitTasks.size() == 1) {
            JSONObject unitTask = unitTasks.getJSONObject(0);
            tvUnit.setText(unitTask.getString("unitName"));
            tvTime.setText(unitTask.getString("progressTime"));
            String progressStatus = unitTask.getString("progressStatus");
            tvStatusContent.setText(Config.informstatus.containsKey(progressStatus) ? Config.informstatus.get(progressStatus) : "未获取到进展通报");
        }
    }
}
