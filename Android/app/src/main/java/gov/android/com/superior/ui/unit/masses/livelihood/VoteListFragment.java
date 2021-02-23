package gov.android.com.superior.ui.unit.masses.livelihood;


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
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.first.orient.base.utils.DpUtils;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.config.Config;
import gov.android.com.superior.http.HttpUrl;

/**
 * A simple {@link Fragment} subclass.
 * 投票事项列表
 */
public class VoteListFragment extends BaseRecylceFragment {

    @Override
    public void onInitViews() {
        super.onInitViews();
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL, DpUtils.getSmallestScreenWidth(getContext(), R.dimen.dp_0_5), getContext().getResources().getColor(R.color.avatar_default_border)));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSmartRefreshLayout.autoRefresh();
    }

    private void requestPeopleOpinions() {
        OkGo.<List<JSONObject>>get(HttpUrl.VOTE_OPINION_LIST).tag(this).execute(getJsonArrayCallback(HttpUrl.VOTE_OPINION_LIST));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestPeopleOpinions();
    }

    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new VoteOpinionViewHolder(getLayoutInflater().inflate(R.layout.rc_item_people_vote, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        Intent intent = new Intent(getActivity(), TaskVoteActivity.class);
        intent.putExtra("opinionId", data.getIntValue("id"));
        startActivity(intent);
    }

    class VoteOpinionViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

        TextView tvTitle;
        TextView tvContent;
        TextView tvLevel;
        TextView tvVoteCount;
        TextView tvPercent;


        public VoteOpinionViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvLevel = (TextView) itemView.findViewById(R.id.tv_level);
            tvVoteCount = (TextView) itemView.findViewById(R.id.tv_vote_count);
            tvPercent = (TextView) itemView.findViewById(R.id.tv_percent);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
            int category = data.getIntValue("category");
            String type = Config.opinionTypes.containsKey(category) ? Config.opinionTypes.get(category) : "未知问题类型";
            tvTitle.setText(type);

            tvContent.setText(data.getString("content"));
            tvLevel.setText(data.getString("rank"));
            tvVoteCount.setText(data.getString("voteCount"));
            tvPercent.setText(data.getString("ratio"));
        }
    }
}
