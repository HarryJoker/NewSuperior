package gov.android.com.superior.task.list;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.R;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.task.LabelUtils;
import gov.android.com.superior.task.StatusConfig;
import gov.android.com.superior.task.detail.UnitTaskActivity;

public class MuliteTaskAdapter extends MuliteRecycleAdapter {

    private int listType;

    private int category;

    public MuliteTaskAdapter(Activity context, int type, JSONArray array) {
        super(context, array);
        this.listType = type;
    }

    @Override
    public void onConfigGroupKeys(List mChildGroupKeys) {
        mChildGroupKeys.add("childTasks");
        mChildGroupKeys.add("unitTasks");
    }

    @Override
    public void onSelectedGroupIndex(List mExpandPositions) {
        mExpandPositions.add(0);
        mExpandPositions.add(0);
    }

    @Override
    public BaseItemViewHolder onCreateMuliteViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new OneViewHolder(getContext().getLayoutInflater().inflate(R.layout.item_mulite_task, parent, false));
            case 1:
                return new TwoViewHolder(getContext().getLayoutInflater().inflate(R.layout.item_mulite_childtask, parent, false));
            case 2:
                return new ThreeViewHolder(getContext().getLayoutInflater().inflate(R.layout.item_mulite_unittask, parent, false));
        }
        return null;
    }

    @Override
    public void onBindMuliteViewHolder(@NonNull BaseItemViewHolder holder, int position, JSONObject item) {
        if (holder == null) return;
        holder.bindView(position);
    }

    @Override
    protected boolean onItemClick(int type, int position, JSONObject item) {
//        return super.onItemClick(type, position, item);
        Logger.d("muilteTask adapter item click, type:" + type + ", position:" + position + ", item:" + item.toJSONString());

        int childTaskId = item.getIntValue("childTaskId");
        int unitTaskId = item.getIntValue("unitTaskId");
        Intent intent = new Intent(getContext(), UnitTaskActivity.class);
        intent.putExtra(UnitTaskActivity.KEY_CHILDTASK_ID, childTaskId);
        intent.putExtra(UnitTaskActivity.KEY_UNITTASK_ID, unitTaskId);
        intent.putExtra(UnitTaskActivity.KEY_UNITASK_TYPE, UnitTaskActivity.TYPE_UNITTASK_ARRAY);
        getContext().startActivity(intent);
        return true;
    }

    class OneViewHolder extends BaseItemViewHolder {
        ImageView ivIndicator;
        TextView tvName;

        public OneViewHolder(@NonNull View convertView) {
            super(convertView);

            ivIndicator = (ImageView) convertView.findViewById(R.id.iv_indicator);

            tvName = (TextView) convertView.findViewById(R.id.tv_name);
        }

        @Override
        void bindView(int position) {
            JSONObject jsonObject = getItem(position);
            category = jsonObject.getIntValue("category");
            int childCategory = jsonObject.getIntValue("childCategory");
            tvName.setText(LabelUtils.makeTaskTitleLabel(category) + LabelUtils.makeChildCategoryLabel(category, childCategory) + jsonObject.getString("title"));
        }
    }
    

    class TwoViewHolder extends BaseItemViewHolder {
        ImageView ivIndicator;
        TextView tvName;

        public TwoViewHolder(@NonNull View convertView) {
            super(convertView);

            ivIndicator = (ImageView) convertView.findViewById(R.id.iv_indicator);

            tvName = (TextView) convertView.findViewById(R.id.tv_name);
        }

        @Override
        void bindView(int position) {
            JSONObject jsonObject = getItem(position);
            tvName.setText(LabelUtils.makeChildTaskTitleLabel(category) + jsonObject.getString("content"));
        }
    }

    class ThreeViewHolder extends BaseItemViewHolder {
        AvatarImageView lvLogo;
        TextView tvName;
        TextView tvContent;
        TextView tvStatus;
        ProgressBar pbProgress;
        TextView tvProgress;


        public ThreeViewHolder(@NonNull View convertView) {
            super(convertView);

            lvLogo = (AvatarImageView) convertView.findViewById(R.id.lv_logo);
            tvName = (TextView) convertView.findViewById(R.id.tv_name);

            tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            tvStatus = (TextView) convertView.findViewById(R.id.tv_status);

            pbProgress = (ProgressBar) convertView.findViewById(R.id.pb_progress);
            tvProgress = (TextView) convertView.findViewById(R.id.tv_progress);

        }

        @Override
        void bindView(int position) {
            JSONObject jsonObject = getItem(position);

            tvContent.setText(jsonObject.getString("content"));

            tvStatus.setText(StatusConfig.STATUS.get(jsonObject.getString("status")));

            Glide.with(getContext()).load(Config.ATTACHMENT + jsonObject.getJSONObject("unit").getString("logo")).into(lvLogo);
            tvName.setText(jsonObject.getJSONObject("unit").getString("name"));

            tvProgress.setText(jsonObject.getString("progress") + "%");

            pbProgress.setProgress(jsonObject.getIntValue("progress"));
        }
    }

}
