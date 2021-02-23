package gov.android.com.superior.home.supervise;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.tools.Eutils;

public class ReportActivity extends BaseLoadActivity {

    private long taskId;

    private TextView tv_task_title;
    private TextView tv_task_content;
    private TextView tv_task_plan;

    private TextView tv_report;

    private GridView gv_leaders;
    private LeaderAdapter leaderAdapter;

    private GridView gv_attachment;
    private AttachmentAdapter attachmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setTitle("上报领导");

        taskId = getIntent().getLongExtra("taskId", 0);

        tv_task_title = (TextView) findViewById(R.id.tv_title);
        tv_task_content = (TextView) findViewById(R.id.tv_content);
        tv_task_plan = (TextView) findViewById(R.id.tv_trace);

        tv_report = (TextView) findViewById(R.id.tv_report);

        gv_attachment = (GridView) findViewById(R.id.gv_attachment);
        gv_attachment.setOnItemClickListener(itemClickListener);

        gv_leaders = (GridView) findViewById(R.id.gv_leaders);
        leaderAdapter = new LeaderAdapter(this);
        gv_leaders.setOnItemClickListener(leaderItemClickListener);
        gv_leaders.setAdapter(leaderAdapter);

        asyncTaskTrace();

    }

    private void asyncTaskTrace() {
        showProgress("加载中");
        OkGo.<Map>post(Config.TASK_GET_WITH_LAST_TRACE_AND_LEADERS + "/" + taskId).tag(this).execute(jsonCallback);
    }


    private JsonCallback<Map> jsonCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            removeProgress();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<Map> response) {
            removeProgress();
            refresh(response.body());

        }
    };

    private void refresh(Map<String, Object> data) {
        Map<String, Object> task = (Map<String, Object>) data.get("task");
        tv_task_title.setText(task.get("name").toString());
        tv_task_content.setText(task.get("plan").toString());
        Map<String, Object> trace = (Map<String, Object>) data.get("trace");
        tv_task_plan.setText(trace.get("content").toString());

        String attachment = trace.get("attachment").toString();
        if (attachment.trim().length() > 0) {
            String[] attachments = attachment.split(",");
            if (attachmentAdapter == null) {
                attachmentAdapter = new AttachmentAdapter(this);
                gv_attachment.setAdapter(attachmentAdapter);
            }
            attachmentAdapter.update(attachments);
            attachmentAdapter.notifyDataSetChanged();
        }

        List<Map<String, Object>> leaders = (List<Map<String, Object>>) data.get("users");
        leaderAdapter.update(leaders);
        leaderAdapter.notifyDataSetChanged();
    }

    private void report() {
        showProgress("上报领导中");
        OkGo.<Map>post(Config.TASK_UPGRADE).tag(this).params("taskId", taskId).params("userIds", Eutils.listToString(leaderAdapter.getUserIds(), ',')).execute(reportCallback);
    }

    private JsonCallback<Map> reportCallback = new JsonCallback<Map>() {

        @Override
        public void onError(Response<Map> response) {
            super.onError(response);
            removeProgress();
            showCallbckError(response);
        }

        @Override
        public void onSuccess(Response<Map> response) {
            removeProgress();
            Toast.makeText(ReportActivity.this, "上报领导成功", Toast.LENGTH_LONG).show();
            tv_report.setBackgroundColor(Color.parseColor("#d3d3d3"));
            tv_report.setEnabled(false);
            setResult(RESULT_OK);
            finish();
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(ReportActivity.this, TransitionActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ReportActivity.this, view, "transition");
            intent.putExtra("url", Config.ATTACHMENT + "/" + attachmentAdapter.getItem(i));
            startActivity(intent, options.toBundle());
        }
    };

    private AdapterView.OnItemClickListener leaderItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Map<String, Object> leader = leaderAdapter.getItem(i);
            leader.put("checked", leader.containsKey("checked") ? !((Boolean)leader.get("checked")) : true);
            leaderAdapter.notifyDataSetChanged();
        }
    };

    public void reportClick(View v) {
        report();
    }


    class LeaderAdapter extends BaseAdapter {

        private int imageWidth;

        private Activity context;

        private List<Map<String, Object>> leaders = new ArrayList<>();

        public LeaderAdapter(Activity activity) {
            this.context = activity;
            this.imageWidth = makeImageWidth();
        }

        private int makeImageWidth() {
            DisplayMetrics metric = new DisplayMetrics();
            this.context.getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;     // 屏幕宽度（像素）
            int margin = CommonUtils.dip2px(20);
            return (width - margin) / 4 - 60;
        }

        public void update(List<Map<String, Object>> list) {
            if (list != null && list.size() > 0) leaders.clear();
            leaders.addAll(list);
        }

        public List<String> getUserIds() {
            List<String> ids = new ArrayList<>();
            for (Map<String, Object> user : leaders) {
                if (user.containsKey("checked")) {
                    if ((Boolean) user.get("checked")) {
                        ids.add(user.get("id").toString());
                    }
                }
            }
            return ids;
        }

        @Override
        public int getCount() {
            return leaders.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return leaders.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            Map<String, Object> leader = getItem(i);

            view = this.context.getLayoutInflater().inflate(R.layout.item_leader, null);
            AvatarImageView imageView = view.findViewById(R.id.profile_image);
            ViewGroup.LayoutParams para = imageView.getLayoutParams();
            if (para == null) para = new ViewGroup.LayoutParams(imageWidth, imageWidth);
            para.height = imageWidth;
            para.width = imageWidth;
            imageView.setLayoutParams(para);

            String logo = leader.get("logo").toString();

            if (TextUtils.isEmpty(logo)) {
                String unitName = leader.get("name").toString();
                imageView.setTextAndColor(unitName.length() > 0 ? unitName.substring(0, 1) : "无", Color.parseColor("#dcdcdc"));
            } else {
                Glide.with(ReportActivity.this)
                        .load(Config.ATTACHMENT + logo)
                        .centerCrop()
                        .crossFade()
                        .into(imageView);
            }

            ((TextView)view.findViewById(R.id.tv_name)).setText(leader.get("name").toString());
            ((CheckBox)view.findViewById(R.id.checkbox)).setChecked(leader.containsKey("checked") ? (Boolean) leader.get("checked") : false);

            return view;
        }
    }

}
