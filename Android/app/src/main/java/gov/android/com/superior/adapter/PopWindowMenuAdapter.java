package gov.android.com.superior.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.ui.unit.dashboard.trace.TraceAdminActivity;
import gov.android.com.superior.ui.unit.dashboard.trace.TraceBackActivity;
import gov.android.com.superior.ui.unit.dashboard.trace.TraceReportActivity;
import gov.android.com.superior.ui.unit.dashboard.trace.TraceRushActivity;

public class PopWindowMenuAdapter extends RecyclerView.Adapter<PopWindowMenuAdapter.MenuViewHolder> {

    private List<MenuBean> mMenuBeans = new LinkedList<MenuBean>() {
        {
            add(new MenuBean(R.mipmap.ic_avatar, "呈报领导",  1111, TraceReportActivity.class));
            add(new MenuBean(R.mipmap.ic_avatar, "审核任务",  1111, TraceAdminActivity.class));
            add(new MenuBean(R.mipmap.ic_avatar, "任务催报",  1111, TraceRushActivity.class));
            add(new MenuBean(R.mipmap.ic_avatar, "退回重报",  1111, TraceBackActivity.class));
        }
    };

    private Activity mContext;
    private View.OnClickListener mMenuItemClick;

    public PopWindowMenuAdapter(Activity activity, View.OnClickListener clickListener) {
        this.mContext = activity;
        this.mMenuItemClick = clickListener;
    }

    public List<MenuBean> getmMenuBeans() {
        return mMenuBeans;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuViewHolder(mContext.getLayoutInflater().inflate(R.layout.rc_item_popwindow_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.bindViewHolder(position);
    }

    @Override
    public int getItemCount() {
        return mMenuBeans.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_icon);
            title = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(mMenuItemClick);
        }

        public void bindViewHolder(int position) {
            MenuBean menuBean = mMenuBeans.get(position);
            itemView.setTag(menuBean);
            if (menuBean.imgRes > 0) {
                icon.setImageResource(menuBean.imgRes);
            }
            title.setText(menuBean.title);
            icon.setImageResource(menuBean.imgRes);
        }
    }

    public static class MenuBean {
        public int imgRes;
        public String title;
        public int requestCode;
        public Class clazz;

        public MenuBean(int img, String title, int code, Class clazz) {
            this.imgRes = img;
            this.title = title;
            this.requestCode = code;
            this.clazz = clazz;
        }
    }
}



