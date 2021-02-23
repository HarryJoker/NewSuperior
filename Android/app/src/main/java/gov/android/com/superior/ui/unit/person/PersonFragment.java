package gov.android.com.superior.ui.unit.person;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.first.orient.base.AppContext;
import com.first.orient.base.fragment.BaseFragment;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vector.update_app.utils.AppUpdateUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.login.SmsLoginActivity;
import gov.android.com.superior.ui.unit.person.message.MessageListActivity;
import gov.android.com.superior.ui.userinfo.CreateUserActivity;
import gov.android.com.superior.ui.userinfo.UnitActivity;

public class PersonFragment extends BaseFragment {

    private LinearLayout layoutUnit;
    private RoundedImageView ivUnitAvatar;
    private TextView tvUnitName;
    private TextView tvUnitIntro;

    private LinearLayout layoutUser;
    private RoundedImageView ivUserAvatar;
    private TextView tvUserName;
    private TextView tvAge;
    private ImageView ivSex;
    private TextView tvId;
    private RecyclerView rcFuns;
    private TextView tvLogout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layoutUnit = (LinearLayout) getView().findViewById(R.id.layout_unit);
        ivUnitAvatar = (RoundedImageView) getView().findViewById(R.id.iv_unit_avatar);
        tvUnitName = (TextView) getView().findViewById(R.id.tv_unit_name);
        tvUnitIntro = (TextView) getView().findViewById(R.id.tv_unit_intro);

        layoutUser = (LinearLayout) getView().findViewById(R.id.layout_user);
        ivUserAvatar = (RoundedImageView) getView().findViewById(R.id.iv_user_avatar);
        tvUserName = (TextView) getView().findViewById(R.id.tv_user_name);
        tvAge = (TextView) getView().findViewById(R.id.tv_age);
        ivSex = (ImageView) getView().findViewById(R.id.iv_sex);
        tvId = (TextView) getView().findViewById(R.id.tv_id);
        rcFuns = (RecyclerView) getView().findViewById(R.id.rc_funs);
        tvLogout = (TextView)getView().findViewById(R.id.tv_logout);
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.getInstance().logoutUser();
                Intent intent = new Intent(getContext(), SmsLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        layoutUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), UnitActivity.class), 0x00f1);
            }
        });

        layoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), CreateUserActivity.class), 0x00f2);
            }
        });
        refreshUnitInfo();
        refreshUserInfo();
        refreshUserFuncs();
    }

    private void refreshUnitInfo() {
        if (User.getInstance().getUserRole() == 0) {
            layoutUnit.setVisibility(View.GONE);
        } else {
            Map<String, Object> unit = User.getInstance().getUserUnit();
            if (unit == null) return;
            Glide.with(this)
                    .load(HttpUrl.ATTACHMENT + unit.get("logo"))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(ivUnitAvatar);
            tvUnitName.setText(User.getInstance().getUnitName());
            Object obj = unit.get("intro");
            tvUnitIntro.setText(obj == null ? "你部门好懒，还没有部门介绍呢..." : obj.toString());
        }
    }

    private void refreshUserInfo() {
        String logo = User.getUser().getString("logo");
        if (TextUtils.isEmpty(logo)) {
           ivUserAvatar.setImageResource(R.mipmap.ic_avatar);
        } else {
            Glide.with(this)
                    .load(HttpUrl.ATTACHMENT + User.getInstance().get("logo"))
                    .into(ivUserAvatar);
        }
        String name = User.getUser().getString("name");
        tvUserName.setText(TextUtils.isEmpty(name) ? "未设置" : name);
        int age = User.getUser().getIntValue("age");
        tvAge.setText(age + "岁");
        int[] sexImgs = new int[]{R.mipmap.ic_sex_unknow, R.mipmap.ic_sex_man, R.mipmap.ic_sex_woman};
        ivSex.setImageResource(sexImgs[User.getUser().getIntValue("sex")]);
        tvId.setText("ID：" + User.getInstance().getUserId());
    }

    private void refreshUserFuncs() {
        rcFuns.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
//        rcFuns.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, DpUtils.getSmallestScreenWidth(getContext(), R.dimen.dp_10), Color.TRANSPARENT));
        rcFuns.setAdapter(new FuncAdapter(makeFuncBeans()));
    }

    private List<FuncBean> makeFuncBeans() {
        List<FuncBean> funcs = new LinkedList<>();
        funcs.add(new FuncBean(0, "我的消息", MessageListActivity.class, 0));
        funcs.add(new FuncBean(0, "我的关注", MyFavoriteActivity.class, 0));
        funcs.add(new FuncBean(0, "我的投票", MyVoteActivity.class, 0));
        funcs.add(new FuncBean(0, "我的线索", MyClueActivity.class, 0));
        funcs.add(new FuncBean(0, "我的建议", MyOpinionActivity.class, 0));
        funcs.add(new FuncBean(0, "我的评价", MyCommentActivity.class, 0));

        if (User.getInstance().getUserRole() <= 2) {
            funcs.add(new FuncBean(0, "我的批示", MyNotationActivity.class, 0));
        }

        funcs.add(new FuncBean(0, "修改密码", EditPasswordActivity.class, 0));
        funcs.add(new FuncBean(0, "缓存设置", CacheActivity.class, 0));
        funcs.add(new FuncBean(0, "更新版本", JSONObject.class, 0));
        funcs.add(new FuncBean(0, "意见建议", AdviceActivity.class, 0));
        funcs.add(new FuncBean(0, "关于我们", AboutActivity.class, 0));
        return funcs;
    }

    private View.OnClickListener onFuncItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof FuncBean) {
                FuncBean bean = (FuncBean)v.getTag();
                if (bean.mClasz.getName().contains("Activity")) {
                    Intent intent = new Intent(getActivity(), bean.mClasz);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            refreshUnitInfo();
            refreshUserInfo();
        }
    }

    class FuncAdapter extends RecyclerView.Adapter<FuncViewHolder> {

        private List<FuncBean> mFuncBeans;

        public FuncAdapter(List<FuncBean> funcs) {
            this.mFuncBeans = funcs;
        }

        @NonNull
        @Override
        public FuncViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FuncViewHolder(getLayoutInflater().inflate(R.layout.rc_item_func, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FuncViewHolder holder, int position) {
            holder.bindViewHolder(position, mFuncBeans.get(position));
        }

        @Override
        public int getItemCount() {
            return mFuncBeans == null ? 0 : mFuncBeans.size();
        }
    }

    class FuncViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;
        TextView content;

        public FuncViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(onFuncItemClick);
            icon = itemView.findViewById(R.id.iv_icon);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
        }

        public void bindViewHolder(int position, FuncBean bean) {
//            icon.setImageResource(bean.mFuncImg);
            itemView.setTag(bean);
            title.setText(bean.mTitle);
            if (!bean.mClasz.getName().contains("Activity")) {
                content.setText(AppUpdateUtils.getVersionName(AppContext.getApplication()));
            } else {
                content.setText("");
            }
        }
    }

    class FuncBean {
        int mFuncImg;
        String mTitle;
        Class mClasz;
        int mTypeParam;
        public FuncBean(int img, String title, Class clasz, int param) {
            this.mFuncImg = img;
            this.mTitle = title;
            this.mClasz = clasz;
            this.mTypeParam = param;
        }

        @NonNull
        @Override
        public String toString() {
            return mTitle + ", " + mClasz.getName();
        }
    }

}