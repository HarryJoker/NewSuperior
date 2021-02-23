package gov.android.com.superior.task.list;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lid.lib.LabelTextView;
import com.orhanobut.logger.Logger;
import gov.android.com.superior.R;
import gov.android.com.superior.task.LabelUtils;
import gov.android.com.superior.task.StatusConfig;
import gov.android.com.superior.task.detail.UnitTaskActivity;
import gov.android.com.superior.trace.NewContentActivity;
import gov.android.com.superior.trace.VerifyContentActivity;

public class UnitTaskAdapter extends RecyclerView.Adapter<UnitTaskAdapter.BaseViewHolder> {

    private int type;

    private Activity mActivity;

    private JSONArray mJSONArray = new JSONArray();

    public UnitTaskAdapter(Activity activity, int type) {
        this.type = type;
        this.mActivity = activity;
    }

    public UnitTaskAdapter(Activity activity, int type, JSONArray array) {
        this.type = type;
        this.mActivity = activity;
        mJSONArray.clear();
        if (array != null) {
            mJSONArray.addAll(array);
        }
    }

    public void setJSONArray(JSONArray array) {
        mJSONArray.clear();;
        if (array != null) {
            mJSONArray.addAll(array);
        }
        notifyDataSetChanged();
    }

    @Override
    public UnitTaskAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UnitTaskAdapter.UnitTaskViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_rc_unittask, parent, false));
    }

    @Override
    public void onBindViewHolder(UnitTaskAdapter.BaseViewHolder holder, int position) {
        holder.bindViewHolder(position);
    }

    @Override
    public int getItemCount() {
        return mJSONArray.size();
    }

    private View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Integer) {
                int position = (Integer) v.getTag();
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                Logger.d(jsonObject.toJSONString());
                JSONObject contentJson = jsonObject.getJSONObject(type == UnitTaskFragment.TASK_LIST_TYPE_ALL ? "unitTask" : "unitContent");
                if (contentJson == null || !contentJson.containsKey("unitTaskId")) return;

                if (type == UnitTaskFragment.TASK_LIST_TYPE_ALL) {
                    int childTaskId = contentJson.getIntValue("childTaskId");
                    int unitTaskId = contentJson.getIntValue("unitTaskId");
                    Intent intent = new Intent(mActivity, UnitTaskActivity.class);
                    intent.putExtra(UnitTaskActivity.KEY_CHILDTASK_ID, childTaskId);
                    intent.putExtra(UnitTaskActivity.KEY_UNITTASK_ID, unitTaskId);
                    intent.putExtra(UnitTaskActivity.KEY_UNITASK_TYPE, UnitTaskActivity.TYPE_UNITTASK_ENTRY);
                    mActivity.startActivity(intent);
                }

                if (type == UnitTaskFragment.TASK_LIST_TYPE_UNINT_UPDATE) {
                    Intent intent = new Intent(mActivity, NewContentActivity.class);
                    intent.putExtra("type", NewContentActivity.TYPE_UPDATE);
                    intent.putExtra("unitTaskId", contentJson.getIntValue("unitTaskId"));
                    mActivity.startActivity(intent);

                    Logger.d("Adapter start NewContentActivity..............");

                }

                if (type == UnitTaskFragment.TASK_LIST_TYPE_UNIT_VERIFY) {
                    Intent intent = new Intent(mActivity, VerifyContentActivity.class);
                    intent.putExtra("unitTaskId", contentJson.getIntValue("unitTaskId"));
                    mActivity.startActivity(intent);
                }
            }
        }
    };

    abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        abstract void bindViewHolder(int position);
    }

    class UnitTaskViewHolder extends UnitTaskAdapter.BaseViewHolder {

        TextView tvTitle;
        TextView tvPlan;
        TextView tvContent;
        LabelTextView tvLabel;


        public UnitTaskViewHolder(View convertView) {
            super(convertView);
            convertView.setClickable(true);
            convertView.setOnClickListener(itemClick);

            tvTitle =  convertView.findViewById(R.id.tv_title);
            tvPlan = convertView.findViewById(R.id.tv_plan);
            tvContent = convertView.findViewById(R.id.tv_content);
            tvLabel = convertView.findViewById(R.id.tv_label);
        }

        @Override
        void bindViewHolder(int position) {
            itemView.setTag(position);

            JSONObject jsonObject = mJSONArray.getJSONObject(position);

            Logger.d(jsonObject.toJSONString());

            int category = jsonObject.getJSONObject("task").getIntValue("category");
            int childCategory = jsonObject.getJSONObject("task").getIntValue("childCategory");

            tvTitle.setText(LabelUtils.makeTaskTitleLabel(category) + LabelUtils.makeChildCategoryLabel(category, childCategory) + jsonObject.getJSONObject("task").getString("title"));
            tvPlan.setText(LabelUtils.makeChildTaskTitleLabel(category) + jsonObject.getJSONObject("childTask").getString("content"));

            JSONObject contentJson = jsonObject.getJSONObject(type == UnitTaskFragment.TASK_LIST_TYPE_ALL ? "unitTask" : "unitContent");

            tvContent.setText("当前进展：" + contentJson.getString("content"));

            String labelTxt = "";
            if (type == UnitTaskFragment.TASK_LIST_TYPE_UNINT_UPDATE) {
                labelTxt = "待完善";
            }
            if (type == UnitTaskFragment.TASK_LIST_TYPE_UNIT_VERIFY) {
                labelTxt = "待签阅";
            }
            if (type == UnitTaskFragment.TASK_LIST_TYPE_ALL) {
                labelTxt = StatusConfig.STATUS.get(contentJson.getString("status"));
            }
            tvLabel.setLabelText(labelTxt);
        }
    }
}
