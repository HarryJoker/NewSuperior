package gov.android.com.superior.ui.unit.dashboard.trace;

import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.first.orient.base.activity.BaseToolBarActivity;
import com.first.orient.base.utils.JokerLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.tools.Eutils;
import gov.android.com.superior.ui.ImageActivity;
import gov.android.com.superior.ui.PaintActivity;

public class TraceUnitStayActivity extends BaseToolBarActivity {

    public static final int TYPE_BACK   = 0X01;
    public static final int TYPE_COMMIT = 0X02;

    private LinearLayout layoutQuestion;
    private LinearLayout layoutStep;
    private EditText etContent;
    private EditText etProgress;
    private EditText etQuestion;
    private EditText etStep;
    private RecyclerView rcAttachment;

    private LinearLayout layoutRemark;

    private EditText etRemark;

    private Spinner mSpMonth;

    private MonthAdapter mMonthAdapter;

    private AttachmentAdapter mAttachmentAdapter;

    private int unitTaskId = 0;

    private int type = 0;

    private String signPath;

    private ImageView tvSign;

    private JSONObject mUnitStaySignTrace;

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
        barTitle.setText("审阅报送工作");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_trace_unitstay;
    }

    @Override
    protected void onFindViews() {

        layoutQuestion = findViewById(R.id.layout_question);

        layoutStep = findViewById(R.id.layout_step);

        etContent = (EditText) findViewById(R.id.et_content);

        etProgress = (EditText) findViewById(R.id.et_progress);

        etQuestion = (EditText)findViewById(R.id.et_question);

        etStep = (EditText)findViewById(R.id.et_step);

        rcAttachment = (RecyclerView) findViewById(R.id.rc_attachment);

        rcAttachment.setLayoutManager(new GridLayoutManager(this, 5));

        tvSign = findViewById(R.id.tv_sign);

        mSpMonth = findViewById(R.id.sp_task_month);

        layoutRemark = findViewById(R.id.layout_remark);

        etRemark = findViewById(R.id.et_remark);
    }

    @Override
    public void onInitView() {
        ((RadioGroup)findViewById(R.id.rg_types)).setOnCheckedChangeListener(mOnCheckedChangeListener);
        mSpMonth.setAdapter(mMonthAdapter = new MonthAdapter());
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        requestUnitContent();
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_back) {
                type = TYPE_BACK;
                layoutRemark.setVisibility(View.VISIBLE);
            }

            if (checkedId == R.id.rb_confirm) {
                type = TYPE_COMMIT;
            }
        }
    };

    private void requestUnitContent() {
        showLoading("加载中...");
        OkGo.<JSONObject>get(HttpUrl.GET_UNIT_STAY_SIGNREPORT_TRACE + "/" + unitTaskId).tag(this).execute(getJsonObjectCallback(HttpUrl.GET_UNIT_STAY_SIGNREPORT_TRACE));
    }

    private void refreshContent(JSONObject jsonObject) {
        String attachment = jsonObject.getString("attachments");
        if (!TextUtils.isEmpty(attachment)) {
            mAttachmentAdapter = new AttachmentAdapter(new ArrayList<String>(Arrays.asList(attachment.split(","))));

            rcAttachment.setAdapter(mAttachmentAdapter);
        }

        if (!TextUtils.isEmpty(jsonObject.getString("question"))) {
            layoutQuestion.setVisibility(View.VISIBLE);
            etQuestion.setText(jsonObject.getString("question"));
        }
        if (!TextUtils.isEmpty(jsonObject.getString("step"))) {
            layoutStep.setVisibility(View.VISIBLE);
            etStep.setText(jsonObject.getString("step"));
        }

        etContent.setText(jsonObject.getString("content"));

        etProgress.setText(jsonObject.getString("progress"));

        mMonthAdapter.setSelectDate(jsonObject.getString("taskDate"));
    }

    public boolean checked() {

        if (type == 0) {
            Toast.makeText(this, "请选择批阅操作：报送或退回", Toast.LENGTH_LONG).show();
            return false;
        }

        if (type == TYPE_COMMIT) {
            if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
                Toast.makeText(this, "请填写报送的工作内容", Toast.LENGTH_LONG).show();
                return false;
            }

            if (TextUtils.isEmpty(etProgress.getEditableText().toString())) {
                Toast.makeText(this, "请填写报送的工作内容", Toast.LENGTH_LONG).show();
                return false;
            }

            if (TextUtils.isEmpty(signPath) || !new File(signPath).exists()) {
                Toast.makeText(this, "请确认添加了手签批阅", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public void veriryClick(View view) {
        if (checked()) {
            if (type == TYPE_COMMIT) {
                commitContent();
            }
            if (type == TYPE_BACK) {
                verifyBack();
            }
        }
    }

    private void commitContent() {
        showLoading("请稍后...");
        HttpParams params = new HttpParams();
        params.put("unitTaskId", unitTaskId);
        params.put("userId", User.getInstance().getUserId());
        params.put("content", etContent.getText().toString());
        params.put("question", etQuestion.getText().toString());
        params.put("step", etStep.getText().toString());
        params.put("status", 31);//已报送
        params.put("progress", etProgress.getText().toString());
        params.put("attachments", mUnitStaySignTrace.getString("attachments"));
        params.put("file", new File(signPath));
        params.put("taskDate", mMonthAdapter.getItem(mSpMonth.getSelectedItemPosition()).getString("value"));
        params.put("unitId", User.getInstance().getUnitId());
        JokerLog.e(JSONObject.toJSONString(params));
        OkGo.<JSONObject>post(HttpUrl.NEW_UNIT_CONTENT_TRACE + "/" + mUnitStaySignTrace.getIntValue("id")).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_UNIT_CONTENT_TRACE));
    }

    //退回完善
    private void verifyBack() {
        showLoading("请稍后...");
        HttpParams params = new HttpParams();
        params.put("unitTaskId", unitTaskId);
        params.put("userId", User.getInstance().getUserId());
        params.put("content", etContent.getText().toString());
        params.put("question", etQuestion.getText().toString());
        params.put("step", etStep.getText().toString());
        params.put("progress", etProgress.getText().toString());
        params.put("taskDate", mMonthAdapter.getItem(mSpMonth.getSelectedItemPosition()).getString("value"));
        params.put("remark", etRemark.getEditableText().toString());
        params.put("unitId", User.getInstance().getUnitId());
        OkGo.<JSONObject>post(HttpUrl.UNIT_STAY_COMPLETE_TRACE + "/" + mUnitStaySignTrace.getString("id")).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.UNIT_STAY_COMPLETE_TRACE));
    }
    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        if (action.equals(HttpUrl.GET_UNIT_STAY_SIGNREPORT_TRACE)) {
            mUnitStaySignTrace = data;
            if (mUnitStaySignTrace == null) return;

            refreshContent(mUnitStaySignTrace);
        }

        if (action.equals(HttpUrl.NEW_UNIT_CONTENT_TRACE )) {
            Toast.makeText(TraceUnitStayActivity.this, "报送成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }

        if (action.equals(HttpUrl.UNIT_STAY_COMPLETE_TRACE)) {
            Toast.makeText(TraceUnitStayActivity.this, "退回成功,请等待完善重新审核", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    public static final int REQUEST_SIGN = 0X01;

    public void signClick(View view) {
        Intent intent = new Intent(this, PaintActivity.class);
        startActivityForResult(intent, REQUEST_SIGN);
    }

    private void refreshSign() {
        if (TextUtils.isEmpty(signPath)) return;
        File file = new File(signPath);
        if (file == null || !file.exists()) return;
        Glide.with(this).load(file).into(tvSign);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_SIGN == requestCode && resultCode == RESULT_OK) {
            signPath = data.getStringExtra("filePath");
            refreshSign();
        }
    }

    private View.OnClickListener mOnItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Integer) {
                int position = (Integer)v.getTag();
                String tImage = mAttachmentAdapter.getItem(position);
                if (tImage == null || TextUtils.isEmpty(tImage)) return;
                Intent intent = new Intent(TraceUnitStayActivity.this, ImageActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TraceUnitStayActivity.this, v, "transition");
                intent.putExtra("url", HttpUrl.ATTACHMENT + tImage);
                startActivity(intent, options.toBundle());
            }
        }
    };

    class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.BaseViewHolder> {
        public static final int TYPE_VIEW_CONTENT = 0X01;
        public static final int TYPE_VIEW_ADD     = 0X02;

        private List<String> attachments = new ArrayList<>();

        private int width = 0;

        public AttachmentAdapter(List<String> list){
            if (list != null) {
                width = makeImageWidth();
                attachments.addAll(list);
            }
        }

        private int makeImageWidth() {
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;     // 屏幕宽度（像素）
            int margin = CommonUtils.dip2px(20);
            return (width - margin) / 5 - 10;
        }


        public List<String> getAttachments() {
            return attachments;
        }

        public String getItem(int position) {
            if (position < attachments.size()) {
                return attachments.get(position);
            }
            return null;
        }

        public String getStringAttachments() {
            return Eutils.listToString(attachments, ',');
        }

        @Override
        public AttachmentAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AttachmentViewHolder(getLayoutInflater().inflate(R.layout.rc_item_image, parent, false));
        }

        @Override
        public void onBindViewHolder(AttachmentAdapter.BaseViewHolder holder, int position) {
            holder.bindViewHolder(position);
        }

        @Override
        public int getItemCount() {
            return attachments.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == attachments.size()) {
                return TYPE_VIEW_ADD;
            } else {
                return TYPE_VIEW_CONTENT;
            }
        }

        abstract class BaseViewHolder extends RecyclerView.ViewHolder {
            ImageView mImageView;

            public BaseViewHolder(View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.iv_attachment);

                ViewGroup.LayoutParams para = mImageView.getLayoutParams();
                if (para == null) {
                    para = new ViewGroup.LayoutParams(width, width);
                } else {
                    para.height = width;
                    para.width = width;
                }
                mImageView.setLayoutParams(para);
            }

            abstract void bindViewHolder(int position);
        }


        class AttachmentViewHolder extends AttachmentAdapter.BaseViewHolder {

            public AttachmentViewHolder(View itemView) {
                super(itemView);

                itemView.setClickable(true);
                itemView.setOnClickListener(mOnItemClick);
            }

            public void bindViewHolder(int position) {
                itemView.setTag(position);
                String tImage = attachments.get(position);
                Glide.with(getBaseContext()).load(HttpUrl.ATTACHMENT + tImage).placeholder(R.mipmap.ic_default_attachment).centerCrop().into(mImageView);
            }
        }
    }

//    class UserAdapter extends BaseAdapter {
//        private JSONArray users = new JSONArray();
//
//        public UserAdapter(JSONArray array) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("name", "请选择报送的主要负责人");
//            jsonObject.put("id", 0);
//            users.add(0, jsonObject);
//
//            if (array != null) {
//                users.addAll(array);
//            }
//        }
//
//        @Override
//        public int getCount() {
//            return users.size();
//        }
//
//        @Override
//        public JSONObject getItem(int position) {
//            return users.getJSONObject(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            convertView = getLayoutInflater().inflate(R.layout.item_dropdown, parent, false);
//            ((TextView)convertView).setText(getItem(position).getString("name"));
//            return convertView;
//        }
//    }


    class MonthAdapter extends BaseAdapter {
        private JSONArray dates = new JSONArray() {
            {
                SimpleDateFormat valueFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy年MM月");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", "请选择要报送的月份");
                jsonObject.put("value", "");
                add(jsonObject);

                jsonObject = new JSONObject();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, -1);
                jsonObject.put("name", displayFormat.format(calendar.getTime()));
                jsonObject.put("value", valueFormat.format(calendar.getTime()));
                add(jsonObject);

                jsonObject = new JSONObject();
                jsonObject.put("name", displayFormat.format(new Date()));
                jsonObject.put("value", valueFormat.format(new Date()));
                add(jsonObject);

                jsonObject = new JSONObject();
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, 1);
                jsonObject.put("name", displayFormat.format(calendar.getTime()));
                jsonObject.put("value", valueFormat.format(calendar.getTime()));
                add(jsonObject);
            }
        };

        public MonthAdapter() {

        }

        public void setSelectDate(String date) {
            if (TextUtils.isEmpty(date)) return;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date curDate = format.parse(date);
                for (int n = 0; n < dates.size(); n++) {
                    String strDate = dates.getJSONObject(n).getString("value");
                    if (TextUtils.isEmpty(strDate)) continue;
                    Date distDate = format.parse(strDate);
                    if (equals(curDate, distDate)) {
                        mSpMonth.setSelection(n);
                        break;
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        private boolean equals(Date date1, Date date2) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(date1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(date2);
            int year1 = calendar1.get(Calendar.YEAR);
            int year2 = calendar2.get(Calendar.YEAR);
            int month1 = calendar1.get(Calendar.MONTH);
            int month2 = calendar2.get(Calendar.MONTH);
            System.out.println(year1+ "  " + month1);
            System.out.println(year2 + "  " + month2);
            return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
        }

        @Override
        public int getCount() {
            return dates.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return dates.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.item_dropdown, parent, false);
            ((TextView) convertView).setText(getItem(position).getString("name"));
            return convertView;
        }
    }

}
