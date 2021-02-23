package gov.android.com.superior.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.ui.ImageActivity;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.BaseViewHolder> {

    private List<String> attachments = new ArrayList<>();

    private int width = 0;

    private Activity mActivity;

    public AttachmentAdapter(Activity activity){
        mActivity = activity;
        width = makeImageWidth();
    }

    public AttachmentAdapter(Activity activity, List<String> list){
        this.mActivity = activity;
        width = makeImageWidth();
        if (list != null) {
            attachments.addAll(list);
        }
    }

    public void setAttachments(List<String> list) {
        attachments.clear();
        if (list != null) {
            attachments.addAll(list);
        }
        notifyDataSetChanged();
    }


    private int makeImageWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int margin = CommonUtils.dip2px(20);
        return (width - margin) / 5 - 10;
    }

    public String getItem(int position) {
        if (position < attachments.size()) {
            return attachments.get(position);
        }
        return null;
    }

    @Override
    public AttachmentAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AttachmentAdapter.AttachmentViewHolder(mActivity.getLayoutInflater().inflate(R.layout.rc_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(AttachmentAdapter.BaseViewHolder holder, int position) {
        holder.bindViewHolder(position);
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }


    abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        public BaseViewHolder(View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.iv_attachment);

            ViewGroup.LayoutParams para = mImageView.getLayoutParams();
            if (para == null) {
                para = new ViewGroup.LayoutParams(width, width);
            } else {
                para.height = width;
                para.width = width;
            }
            mImageView.setLayoutParams(para);
        }

        abstract void bindViewHolder(int position);
    }


    class AttachmentViewHolder extends AttachmentAdapter.BaseViewHolder {

        public AttachmentViewHolder(View itemView) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(mOnItemClick);
        }

        public void bindViewHolder(int position) {
            itemView.setTag(position);
            String tImage = attachments.get(position);
            Glide.with(mActivity).load(HttpUrl.ATTACHMENT + tImage).placeholder(R.mipmap.ic_default_attachment).centerCrop().into(mImageView);
        }
    }

    private View.OnClickListener mOnItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Integer) {
                int position = (Integer)v.getTag();
                String tImage = getItem(position);
                if (tImage == null || TextUtils.isEmpty(tImage)) return;
                Intent intent = new Intent(mActivity, ImageActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, v, "transition");
                intent.putExtra("url", HttpUrl.ATTACHMENT + tImage);
                mActivity.startActivity(intent, options.toBundle());
            }
        }
    };
}
