package gov.android.com.superior.holder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;

import gov.android.com.superior.R;
import gov.android.com.superior.ui.unit.dashboard.tasks.UnitStayTasksActivity;

/**
 * 部门任务列表的ViewHolder
 */
public class UnitStayTaskViewHolder extends UnitTaskViewHolder {

    private int unitStayType;

    public UnitStayTaskViewHolder(@NonNull View itemView, int category, int unitStayType) {
        super(itemView, category);
        this.unitStayType = unitStayType;
    }

    public UnitStayTaskViewHolder(@NonNull Activity context, @NonNull ViewGroup parent, @NonNull int category, @NonNull int unitStayType) {
        this(context.getLayoutInflater().inflate(R.layout.rc_item_unit_task, parent, false), category, unitStayType);
    }


    @Override
    public void bindViewHolder(int position, JSONObject data) {
        super.bindViewHolder(position, data);
        tvLabel.setLabelText(makeLabelText());
    }

    private String makeLabelText() {
        if (unitStayType == UnitStayTasksActivity.UNIT_STAY_SIGN_TYPE) {
            return "待手签";
        }
        if (unitStayType == UnitStayTasksActivity.UNIT_STAY_COMPLETE_TYPE) {
            return "待完善";
        }
        return "";
    }
}
