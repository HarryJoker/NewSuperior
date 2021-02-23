package gov.android.com.superior.trace;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.home.supervise.ReportActivity;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.tools.Eutils;

public class NewReportTraceActivity extends BaseLoadActivity {

    private RecyclerView rc_leaders;

    private LeaderAdapter mLeaderAdapter;

    private int unitTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report_trace);

        setTitle("上报领导批阅");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);

        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rc_leaders = findViewById(R.id.rc_leaders);

        rc_leaders.setLayoutManager(new GridLayoutManager(this, 4));

        requestUnits();
    }

    private void requestUnits() {
        showProgress("加载中...");
        OkGo.<JSONArray>get(Config.UNIT_LEADER_LIST).tag(this).execute(callback);
    }

    private JsonObjectCallBack<JSONArray> callback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {
            removeProgress();
            if (response.body() != null) {
                rc_leaders.setAdapter(mLeaderAdapter = new LeaderAdapter(NewReportTraceActivity.this, response.body()));
            }
        }

        @Override
        public void onError(Response<JSONArray> response) {
            super.onError(response);
            removeProgress();
        }
    };


    public void reportClick(View v) {
        Logger.d(mLeaderAdapter.getCheckedUnits());
        String unitIds = mLeaderAdapter.getCheckedUnitIds();
        if (TextUtils.isEmpty(unitIds)) {
            Toast.makeText(this, "请选择要报送的领导", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress("上报中...");
        OkGo.<Map>post(Config.UNITTASK_REPORT).tag(this).params("unitTaskId", unitTaskId).params("unitIds", unitIds).execute(reportCallback);
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
             newReportTrace();
        }
    };

    private void newReportTrace() {
        HttpParams params = new HttpParams();
        params.put("unitTaskId", unitTaskId);
        params.put("content",  "已上报给【" + mLeaderAdapter.getCheckedUnitNames() + "】领导");
        params.put("status", 20);//已报送
        params.put("progress", 0);
        params.put("userId", User.getInstance().getUserId());
        params.put("unitId", User.getInstance().getUnitId());
        params.put("progress", 0);
        OkGo.<JSONObject>post(Config.TRACE_NEW).headers("accept-encoding", "gzip").params(params).tag(this).execute(commitCallback);
    }

    private JsonObjectCallBack<JSONObject> commitCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            removeProgress();
            Toast.makeText(NewReportTraceActivity.this, "报送成功", Toast.LENGTH_SHORT).show();
            finish();
        }
        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            removeProgress();
            Toast.makeText(NewReportTraceActivity.this, "报送失败", Toast.LENGTH_SHORT).show();
        }
    };

    class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.LeaderViewHolder> {

        private List<JSONObject> checkedUnits = new ArrayList<>();

        private int imageWidth;

        private Activity context;

        private JSONArray leaders = new JSONArray();

        public LeaderAdapter(Activity activity, JSONArray array) {
            this.context = activity;
            if (array != null) {
                leaders.addAll(array);
            }
            this.imageWidth = makeImageWidth();
        }

        private int makeImageWidth() {
            DisplayMetrics metric = new DisplayMetrics();
            this.context.getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;     // 屏幕宽度（像素）
            int margin = CommonUtils.dip2px(20);
            return (width - margin) / 4 - 60;
        }

        public List<JSONObject> getCheckedUnits() {
            return checkedUnits;
        }

        public String getCheckedUnitIds() {
            String ids = "";
            for (JSONObject unit : checkedUnits) {
                ids += ids.length() > 0 ? "," : "";
                ids += unit.getString("id");
            }
            return ids;
        }

        public String getCheckedUnitNames() {
            String names = "";
            for (JSONObject unit : checkedUnits) {
                names += names.length() > 0 ? "," : "";
                names += unit.getString("name");
            }
            return names;
        }

        @Override
        public LeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LeaderViewHolder(getLayoutInflater().inflate(R.layout.item_leader, parent, false));
        }

        @Override
        public void onBindViewHolder(LeaderViewHolder holder, int position) {
            holder.bindLeaderViewHolder(position);
        }


        @Override
        public int getItemCount() {
            return leaders.size();
        }

        CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.getTag() != null && buttonView.getTag() instanceof Integer) {
                    int position = (Integer)buttonView.getTag();
                    JSONObject leader = leaders.getJSONObject(position);
                    if (isChecked) {
                        checkedUnits.add(leader);
                    } else {
                        checkedUnits.remove(leader);
                    }
                }
            }
        };

        class LeaderViewHolder extends RecyclerView.ViewHolder {

            AvatarImageView avatar;

            CheckBox mCheckBox;

            TextView tvName;

            public LeaderViewHolder(View itemView) {
                super(itemView);
                avatar = itemView.findViewById(R.id.profile_image);
                mCheckBox = itemView.findViewById(R.id.checkbox);
                mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
                tvName = itemView.findViewById(R.id.tv_name);

                ViewGroup.LayoutParams para = avatar.getLayoutParams();
                if (para == null) para = new ViewGroup.LayoutParams(imageWidth, imageWidth);
                para.height = imageWidth;
                para.width = imageWidth;
                avatar.setLayoutParams(para);

            }

            public void bindLeaderViewHolder(int position) {
                Map<String, Object> leader = leaders.getJSONObject(position);

                mCheckBox.setTag(position);

                String logo = leader.get("logo").toString();
                Glide.with(NewReportTraceActivity.this).load(Config.ATTACHMENT + logo).centerCrop().into(avatar);
                tvName.setText(leader.get("name").toString());
            }
        }
    }

}
