package gov.android.com.superior.ui.unit.dashboard;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.first.orient.base.activity.BaseMapRecylceActivity;
import com.first.orient.base.adapter.BaseMapRecycleAdapter;
import com.first.orient.base.utils.JokerLog;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import gov.android.com.superior.R;
import gov.android.com.superior.adapter.AttachmentAdapter;
import gov.android.com.superior.http.HttpUrl;

/**
 * Task主体任务
 */
public class TaskIntroActivity extends BaseMapRecylceActivity {

    private static Map<Integer, List<BaseMapRecycleAdapter.MetaBean>> mCategoryMetaBeans = new HashMap<Integer, List<BaseMapRecycleAdapter.MetaBean>>() {
        {
            List<BaseMapRecycleAdapter.MetaBean> metaBeans = new ArrayList<BaseMapRecycleAdapter.MetaBean>();
            metaBeans.add(new BaseMapRecycleAdapter.MetaBean("category", "任务类型", 0));
            metaBeans.add(new BaseMapRecycleAdapter.MetaBean("labelType", "任务类别", 0));
            metaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "重点工作", 0));
            metaBeans.add(new BaseMapRecycleAdapter.MetaBean("content", "推进计划", 0));
            metaBeans.add(new BaseMapRecycleAdapter.MetaBean("reportCycle", "调度周期", 0));
            metaBeans.add(new BaseMapRecycleAdapter.MetaBean("attachmentids", "附件图片", 1));
            put(1, metaBeans);
        }
    };

    private static String[] labelTypes = new String[] {"预期目标类", "试点验收类", "定量类", "定性类"};

    private Map<Integer, BaseMapRecycleAdapter.HolderMeta> mHolderMetas = new HashMap<Integer, BaseMapRecycleAdapter.HolderMeta>() {
        {
            put(0, new BaseMapRecycleAdapter.HolderMeta(0, ContentViewHolder.class, R.layout.rc_item_task_intro));
            put(1, new BaseMapRecycleAdapter.HolderMeta(1, AttachmentsViewHolder.class, R.layout.rc_item_task_intro_attachments));
        }
    };

    private int taskId;

    private int category;

    @Override
    public List<BaseMapRecycleAdapter.MetaBean> getMetaBeans() {
        return mCategoryMetaBeans.containsKey(category) ? mCategoryMetaBeans.get(category) : null;
    }

    @Override
    public Map<Integer, BaseMapRecycleAdapter.HolderMeta> getHolderMetas() {
        return mHolderMetas;
    }

    @Override
    public void onInitParams() {
        taskId = getIntent().getIntExtra("taskId", 0);
        category = getIntent().getIntExtra("category", 0);
        if (taskId == 0 || category == 0) {
            showToast("数据错误");
            finish();
        }
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("任务详情");
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        mSmartRefreshLayout.autoRefresh();
//        requestTaskDetail();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestTaskDetail();
    }

    private void requestTaskDetail() {
        showLoading();
        OkGo.<JSONObject>get(HttpUrl.TASK_INTRO + "/" + taskId).tag(this).execute(getJsonObjectCallback(HttpUrl.TASK_INTRO));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        if (data != null && data.containsKey("attachmentids")) {
            String attachments = data.getString("attachmentids");
            if (TextUtils.isEmpty(attachments)) {
                mCategoryMetaBeans.get(category).remove(mCategoryMetaBeans.get(category).size() - 1);
            }
        }
        setDataSource(data);
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public static class ContentViewHolder extends BaseMapRecycleAdapter.BaseViewHolder {

        private TextView tvTitle;
        private TextView tvContent;

        private TaskIntroActivity introActivity;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            introActivity = (TaskIntroActivity) itemView.getContext();
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            JokerLog.d("new Content Item View.....");
        }

        @Override
        public void bindViewHolderForValue(int position, String key, Object content) {
            if (content == null || TextUtils.isEmpty(content.toString())) {
                tvContent.setText("");
                return;
            }
            tvContent.setText(content.toString());
            if (key.equals("labelType")) {
                int index = Integer.parseInt(content.toString());
                if (index < labelTypes.length) {
                    tvContent.setText(labelTypes[index]);
                }
            }

            if (key.equals("category") && gov.android.com.superior.config.Config.categoryTitles.containsKey(Integer.parseInt(content.toString()))) {
                tvContent.setText(gov.android.com.superior.config.Config.categoryTitles.get(Integer.parseInt(content.toString())));
            }

            if (key.equals("reportCycle")) {
                int reportCycle = Integer.parseInt(content.toString());
                if (reportCycle == 1) {
                    tvContent.setText("每月" + introActivity.getRecyclerAdapter().getObjectByKey("reportDate") + "号");
                } else {
                    tvContent.setText(introActivity.getRecyclerAdapter().getObjectByKey("reportDate").toString());
                }
            }
        }

        @Override
        public void bindViewHolderForTitle(int position, String key, BaseMapRecycleAdapter.MetaBean metaBean) {
            tvTitle.setText(metaBean.getTitle());
        }
    }

    public static class AttachmentsViewHolder extends BaseMapRecycleAdapter.BaseViewHolder {

        private TextView tvTitle;
        private RecyclerView rcAttachment;

        public AttachmentsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            rcAttachment = itemView.findViewById(R.id.rc_attachment);
        }

        @Override
        public void bindViewHolderForValue(int position, String key, Object content) {
            if (content == null || TextUtils.isEmpty(content.toString())) return;
            JokerLog.d(content == null ? "attachemnts is null" : content.toString());
            List<String> attahments = Arrays.asList(content.toString().split(","));
            rcAttachment.setLayoutManager(new GridLayoutManager(itemView.getContext(), 5));
            rcAttachment.setAdapter(new AttachmentAdapter((Activity) itemView.getContext(), attahments));
        }

        @Override
        public void bindViewHolderForTitle(int position, String key, BaseMapRecycleAdapter.MetaBean metaBean) {
            tvTitle.setText(metaBean.getTitle());
        }
    }
}
