package gov.android.com.superior.ui.unit.masses.livelihood;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.adapter.BaseRecyclerAdapter;
import com.first.orient.base.divider.RecycleViewDivider;
import com.first.orient.base.fragment.BaseRecylceFragment;
import com.first.orient.base.utils.DpUtils;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import gov.android.com.superior.R;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.ui.unit.masses.livelihood.comment.TaskCommentActivity;

/**
 * A simple {@link Fragment} subclass.
 * 评价事项列表
 */
public class CommentListFragment extends BaseRecylceFragment {

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

    private void requestCommentTasks() {
        OkGo.<List<JSONObject>>get(HttpUrl.COMMENT_TASK_LIST ).tag(this).execute(getJsonArrayCallback(HttpUrl.COMMENT_TASK_LIST));
    }

    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        setDataSource(data);
    }
    @Override
    protected BaseRecyclerAdapter.BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new CommentTaskViewHolder(getLayoutInflater().inflate(R.layout.rc_item_comment_task, parent, false));
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        Intent intent = new Intent(getActivity(), TaskCommentActivity.class);
        JSONObject unitTask = data.getJSONArray("unitTasks").getJSONObject(0);
        intent.putExtra("unitTaskId", unitTask.getIntValue("id"));
        intent.putExtra("task", data.toJSONString());
        startActivity(intent);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestCommentTasks();
    }

    class CommentTaskViewHolder extends BaseRecyclerAdapter.BaseViewHolder {

        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvTotal;
        private TextView tvGood;
        private TextView tvBad;


        public CommentTaskViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvTotal = (TextView) itemView.findViewById(R.id.tv_total);
            tvGood = (TextView) itemView.findViewById(R.id.tv_good);
            tvBad = (TextView) itemView.findViewById(R.id.tv_bad);
        }

        @Override
        public void bindViewHolder(int position, JSONObject data) {
            tvTitle.setText(data.getString("title"));
            tvContent.setText(data.getString("content"));
            if (data.getJSONArray("unitTasks").getJSONObject(0).containsKey("commentSummary")) {
                JSONObject commentSummary = data.getJSONArray("unitTasks").getJSONObject(0).getJSONObject("commentSummary");
                tvTotal.setText(commentSummary.getString("total"));
                tvBad.setText(commentSummary.getString("badNum"));
                tvGood.setText(commentSummary.getString("goodNum"));
            }
        }
    }
}
