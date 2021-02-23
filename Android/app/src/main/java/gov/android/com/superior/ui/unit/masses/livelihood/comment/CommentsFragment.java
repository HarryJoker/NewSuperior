package gov.android.com.superior.ui.unit.masses.livelihood.comment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.lzy.okgo.OkGo;
import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * A simple {@link Fragment} subclass.
 * 评价列表
 */
public class CommentsFragment extends BaseRecylceFragment {

    private int commentType = 0;

    private int unitTaskId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commentType = getArguments().getInt("type", 0);
        unitTaskId = getArguments().getInt("unitTaskId", 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout.autoRefresh();
    }

    private void requestComments() {
        OkGo.<List<JSONObject>>get(HttpUrl.COMMENT_LIST_BY_TYPE + "/" + unitTaskId + "/" + commentType).tag(this).execute(getJsonArrayCallback(HttpUrl.COMMENT_LIST_BY_TYPE));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(getLayoutInflater().inflate(R.layout.rc_item_comment, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestComments();
    }

    class CommentViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

        private RoundedImageView ivAvatar;
        private TextView tvName;
        private MaterialRatingBar rbScore;
        private TextView tvContent;
        private TextView tvTime;

        public CommentViewHolder(View itemView) {
            super(itemView);

            ivAvatar = (RoundedImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            rbScore =   itemView.findViewById(R.id.rb_score);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
            rbScore.setRating(data.getFloatValue("score"));
            tvContent.setText(data.getString("comment"));
            Glide.with(getActivity()).load(HttpUrl.makeAttachmentUrl(data.getString("logo"))).placeholder(R.mipmap.ic_avatar).into(ivAvatar);
            tvName.setText(data.getString("name"));
            tvTime.setText(data.getString("createtime"));
        }
    }
}
