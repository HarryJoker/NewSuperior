package gov.android.com.superior.ui.unit.dashboard.trace;

import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.tools.CommonUtils;

public class TraceReportActivity extends BaseToolBarActivity {

    private RecyclerView rc_leaders;

    private LeaderAdapter mLeaderAdapter;

    private int unitTaskId;


    @Override
    public void onInitParams() {
        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);

        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("呈报领导");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_trace_report;
    }

    @Override
    protected void onFindViews() {
        rc_leaders = findViewById(R.id.rc_leaders);
        rc_leaders.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        requestUnits();
    }

    private void requestUnits() {
        showLoading("加载中...");
        OkGo.<List<JSONObject>>get(HttpUrl.UNIT_LEADER_LIST).tag(this).execute(getJsonArrayCallback(HttpUrl.UNIT_LEADER_LIST));
    }

    @Override
    protected void onJsonArrayCallBack(String action, List<JSONObject> data) {
        super.onJsonArrayCallBack(action, data);
        rc_leaders.setAdapter(mLeaderAdapter = new LeaderAdapter(TraceReportActivity.this, data));
    }

    public void reportClick(View v) {
        Logger.d(mLeaderAdapter.getCheckedUnits());
        String unitIds = mLeaderAdapter.getCheckedUnitIds();
        if (TextUtils.isEmpty(unitIds)) {
            Toast.makeText(this, "请选择要报送的领导", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading("请稍后...");
        newReportTrace();
    }

    private void newReportTrace() {
        HttpParams params = new HttpParams();
        params.put("unitTaskId", unitTaskId);
        params.put("content",  "已上报给【" + mLeaderAdapter.getCheckedUnitNames() + "】领导");
        params.put("status", 20);//已报送
        params.put("userId", User.getInstance().getUserId());
        params.put("unitId", User.getInstance().getUnitId());
        params.put("progress", 0);
        params.put("leaderUnitIds", mLeaderAdapter.getCheckedUnitIds());
        OkGo.<JSONObject>post(HttpUrl.NEW_REPORT_TRACE).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_REPORT_TRACE));
    }

    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        Toast.makeText(TraceReportActivity.this, "呈报成功", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }



    class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.LeaderViewHolder> {

        private List<JSONObject> checkedUnits = new ArrayList<>();

        private int imageWidth;

        private Activity context;

        private List<JSONObject> leaders = new ArrayList<>();

        public LeaderAdapter(Activity activity, List<JSONObject> array) {
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
            return new LeaderViewHolder(getLayoutInflater().inflate(R.layout.rc_item_leader_image, parent, false));
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
                    JSONObject leader = leaders.get(position);
                    if (isChecked) {
                        checkedUnits.add(leader);
                    } else {
                        checkedUnits.remove(leader);
                    }
                }
            }
        };

        class LeaderViewHolder extends RecyclerView.ViewHolder {

            RoundedImageView avatar;

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
                Map<String, Object> leader = leaders.get(position);

                mCheckBox.setTag(position);

                String logo = leader.get("logo").toString();
                Glide.with(TraceReportActivity.this).load(HttpUrl.ATTACHMENT + logo).placeholder(R.mipmap.ic_avatar).centerCrop().into(avatar);
                tvName.setText(leader.get("name").toString());
            }
        }
    }

}
