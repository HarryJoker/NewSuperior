package gov.android.com.superior.home.supervise;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.CommonUtils;

/**
 * Created by wanghua on 17/8/13.
 */

 public class AttachmentAdapter extends BaseAdapter {

    private int imageWidth;

    private Activity context;

    private List<String> attachments = new ArrayList<>();

    public AttachmentAdapter(Activity activity) {
        this.context = activity;
        this.imageWidth = makeImageWidth(20);
    }

    public AttachmentAdapter(Activity activity, int margin) {
        this.context = activity;
        this.imageWidth = makeImageWidth(margin);
    }

    public AttachmentAdapter(Activity activity, List<String> list, int margin) {
        this(activity, margin);
        if (list != null) {
            this.attachments.clear();
            this.attachments.addAll(list);
        }
    }

    private int makeImageWidth(int margin) {
        DisplayMetrics metric = new DisplayMetrics();
        this.context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        margin = CommonUtils.dip2px(margin);
        return (width - margin) / 5 - 10;
    }

    public void update(String[] arr) {
        if (arr != null && arr.length > 0) attachments.clear();
        attachments.addAll(Arrays.asList(arr));
    }


    @Override
    public int getCount() {
        return attachments.size();
    }

    @Override
    public String getItem(int i) {
        return attachments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = this.context.getLayoutInflater().inflate(R.layout.item_image, null);
        ImageView imageView = view.findViewById(R.id.iv_attachment);
        ViewGroup.LayoutParams para = imageView.getLayoutParams();
        if (para == null) para = new ViewGroup.LayoutParams(imageWidth, imageWidth);
        para.height = imageWidth;
        para.width = imageWidth;
        imageView.setLayoutParams(para);
        Glide.with(context).load(Config.ATTACHMENT + "/" + getItem(i)).override(150, 150).centerCrop().placeholder(R.mipmap.background_attachment).centerCrop().into(imageView);
        return view;
    }
}