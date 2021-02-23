package gov.android.com.superior.task.detail;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lid.lib.LabelTextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.R;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.task.StatusConfig;
import gov.android.com.superior.task.UnitManager;
import gov.android.com.superior.trace.AttachmentAdapter;
import gov.android.com.superior.trace.TraceDetailActivity;

public class TraceAdapter extends RecyclerView.Adapter<TraceAdapter.BaseViewHolder> {

    public static final int TYPE_VIEW_CONTENT    = 0x01;
    public static final int TYPE_VIEW_ATTACHMENT = 0x02;

    private Activity mContext;

    private JSONArray mJSONArray = new JSONArray();

    public TraceAdapter(Activity context) {
        this.mContext = context;
    }

    public TraceAdapter(Activity context, JSONArray array) {
        this.mContext = context;
        if (array != null) {
            mJSONArray.addAll(array);
        }
    }

    public void setJSONArray(JSONArray array) {
        mJSONArray.clear();
        if (array != null) {
            mJSONArray.addAll(array);
        }
        notifyDataSetChanged();
    }

    private View.OnClickListener traceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Integer) {
                int position = (Integer)v.getTag();
                JSONObject trace = mJSONArray.getJSONObject(position);
                Intent intent = new Intent(mContext, TraceDetailActivity.class);
                intent.putExtra("trace", trace.toJSONString());
                mContext.startActivity(intent);
            }
        }
    };

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_VIEW_ATTACHMENT) {
            return new AttachmentViewHolder(mContext.getLayoutInflater().inflate(R.layout.item_rc_attachment, parent, false));
        } else {
            return new ContentViewHolder(mContext.getLayoutInflater().inflate(R.layout.item_rc_trace, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bindViewHolder(position);
    }

    @Override
    public int getItemCount() {
        return mJSONArray.size();
    }

    @Override
    public int getItemViewType(int position) {
        JSONObject jsonObject = mJSONArray.getJSONObject(position);
        return TextUtils.isEmpty(jsonObject.getString("attachments")) ? TYPE_VIEW_CONTENT : TYPE_VIEW_ATTACHMENT;
    }

    abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        abstract void bindViewHolder(int position);
    }

    class ContentViewHolder extends BaseViewHolder {
        AvatarImageView lvLogo;
        TextView tvVerticalTop;
        TextView tvVerticalBottom;
        LabelTextView tvTrace;
        TextView tvTime;
        TextView tvUnitName;

        View layTrace;

        public ContentViewHolder(View convertView) {
            super(convertView);
            lvLogo = convertView.findViewById(R.id.lv_logo);
            tvVerticalTop =  convertView.findViewById(R.id.tv_vertical_top);
            tvVerticalBottom = convertView.findViewById(R.id.tv_vertical_bottom);
            tvTrace =  convertView.findViewById(R.id.tv_trace);
            tvTime =  convertView.findViewById(R.id.tv_time);
            tvUnitName = convertView.findViewById(R.id.tv_unit_name);

            layTrace = convertView.findViewById(R.id.layout_trace);

            layTrace.setOnClickListener(traceClick);
        }

        @Override
        void bindViewHolder(int position) {
            JSONObject trace = mJSONArray.getJSONObject(position);

            JSONObject unit = UnitManager.getInstace().getUnit(trace.getString("unitId"));
            if (unit != null) {
                Glide.with(mContext).load(Config.ATTACHMENT +  unit.getString("logo")).into(lvLogo);
                tvUnitName.setText(unit.getString("name"));
            } else {
                lvLogo.setBackgroundResource(R.mipmap.icon_mayun);
                tvUnitName.setText("");
            }

            tvVerticalTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
            tvVerticalBottom.setVisibility(position == mJSONArray.size() - 1 ? View.INVISIBLE : View.VISIBLE);
            tvTrace.setText(trace.getString("content"));
            tvTime.setText("创建时间：" + trace.getString("createtime"));
            tvTrace.setLabelText(StatusConfig.STATUS.get(trace.getString("status")));

            layTrace.setTag(position);
        }
    }

    class AttachmentViewHolder extends ContentViewHolder {

        RecyclerView rcAttachments;

        public AttachmentViewHolder(View itemView) {
            super(itemView);
            rcAttachments = itemView.findViewById(R.id.rc_attachment);
            rcAttachments.setLayoutManager(new GridLayoutManager(mContext, 5));
        }

        @Override
        void bindViewHolder(int position) {
            super.bindViewHolder(position);

            JSONObject trace = mJSONArray.getJSONObject(position);

            if (trace.containsKey("attachments") && !TextUtils.isEmpty(trace.getString("attachments"))) {
                List<String> attachments = new ArrayList<>(Arrays.asList(trace.getString("attachments").split(",")));
                rcAttachments.setAdapter(new AttachmentAdapter(mContext, attachments));
            }
        }
    }

}
