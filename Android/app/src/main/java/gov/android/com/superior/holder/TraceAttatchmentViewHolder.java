package gov.android.com.superior.holder;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.lid.lib.LabelTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.adapter.AttachmentAdapter;

public class TraceAttatchmentViewHolder extends TraceContentViewHolder {

    Activity mContext;
    RecyclerView rc_attachments;

    public TraceAttatchmentViewHolder(@NonNull Activity context, @NonNull ViewGroup parent, int traceSize) {
        this(context, context.getLayoutInflater().inflate(R.layout.rc_item_trace_attachment, parent, false), traceSize);
    }

    public TraceAttatchmentViewHolder(Activity mContext, View convertView, int traceSize) {
        super(convertView, traceSize);
        this.mContext = mContext;
        rc_attachments = convertView.findViewById(R.id.rc_attachment);
        rc_attachments.setLayoutManager(new GridLayoutManager(mContext, 5));
    }

    @Override
    public void bindViewHolder(int position, JSONObject data) {
        super.bindViewHolder(position, data);
        if (data.containsKey("attachments") && !TextUtils.isEmpty(data.getString("attachments"))) {
            String strAttachments = data.getString("attachments");
            List<String> attachments = Arrays.asList(strAttachments.split(","));
            rc_attachments.setAdapter(new AttachmentAdapter(mContext, attachments));
        }
    }
}
