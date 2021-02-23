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

public class PopWindowCategoryAdapter extends RecyclerView.Adapter<PopWindowCategoryAdapter.MenuViewHolder> {

    private List<MenuBean> mMenuBeans = new LinkedList<MenuBean>() {
        {
            add(new MenuBean(R.mipmap.ic_avatar, "政府工作报告",  1));
            add(new MenuBean(R.mipmap.ic_avatar, "7+3重点改革任务",  2));
            add(new MenuBean(R.mipmap.ic_avatar, "建议提案",  3));
            add(new MenuBean(R.mipmap.ic_avatar, "会议议定事项",  4));
            add(new MenuBean(R.mipmap.ic_avatar, "领导批示",  5));
            add(new MenuBean(R.mipmap.ic_avatar, "专项督查",  6));
            add(new MenuBean(R.mipmap.ic_avatar, "重点项目",  7));
            add(new MenuBean(R.mipmap.ic_avatar, "民生实事",  8));
            add(new MenuBean(R.mipmap.ic_avatar, "群众线索",  9));
        }
    };

    private Activity mContext;
    private View.OnClickListener mMenuItemClick;

    public PopWindowCategoryAdapter(Activity activity, View.OnClickListener clickListener) {
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
            icon.setVisibility(View.GONE);
            title = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(mMenuItemClick);
        }

        public void bindViewHolder(int position) {
            MenuBean menuBean = mMenuBeans.get(position);
            itemView.setTag(menuBean);
//            if (menuBean.imgRes > 0) {
//                icon.setImageResource(menuBean.imgRes);
//            }
//            icon.setImageResource(menuBean.imgRes);
            title.setText(menuBean.title);
        }
    }

    public static class MenuBean {
        public int imgRes;
        public String title;
        public int category;

        public MenuBean(int img, String title, int category) {
            this.imgRes = img;
            this.title = title;
            this.category = category;
        }
    }
}



