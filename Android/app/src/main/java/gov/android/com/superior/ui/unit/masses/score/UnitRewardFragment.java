package gov.android.com.superior.ui.unit.masses.score;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.first.orient.base.utils.DpUtils;
import com.lzy.okgo.OkGo;
import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.LinkWebActivity;

/**
 * A simple {@link Fragment} subclass.
 * 督查奖励列表
 */
public class UnitRewardFragment extends BaseRecylceFragment {

    @Override
    public void onInitViews() {
        super.onInitViews();
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL, DpUtils.getSmallestScreenWidth(getContext(), R.dimen.dp_1), getContext().getResources().getColor(R.color.avatar_default_border)));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestUnitSpaceList();
    }

    private void requestUnitSpaceList() {
        OkGo.<List<JSONObject>>get(HttpUrl.SPACE_LIST_BY_TYPE + "/" + 2).tag(this).execute(getJsonArrayCallback(HttpUrl.SPACE_LIST_BY_TYPE));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new SpaceViewHolder(getLayoutInflater().inflate(R.layout.rc_item_space, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        Intent intent = new Intent(getActivity(), LinkWebActivity.class);
        intent.putExtra("title", "督查激励");
        intent.putExtra("url", getRecyclerAdapter().getJsonObject(position).getString("link"));
        startActivity(intent);
    }

    class SpaceViewHolder extends BaseRecyclerAdapter.BaseViewHolder {
        RoundedImageView ivImage;
        TextView tvTitle;
        TextView tvContent;

        public SpaceViewHolder(View itemView) {
            super(itemView);
            ivImage = (RoundedImageView) itemView.findViewById(R.id.iv_image);
            tvTitle = (TextView)  itemView.findViewById(R.id.tv_title);
            tvContent = (TextView)  itemView.findViewById(R.id.tv_content);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
            Glide.with(itemView)
                    .load(HttpUrl.ATTACHMENT + data.getString("image"))
                    .into(ivImage);
            tvTitle.setText(data.getString("title"));
            tvContent.setText(data.getString("intro"));
        }
    }

}
