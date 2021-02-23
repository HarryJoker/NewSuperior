package gov.android.com.superior.ui.unit.dashboard.trace;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.first.orient.base.utils.GlideEngine;
import com.first.orient.base.utils.JokerLog;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.HttpUrl;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.ui.ImageActivity;

import static gov.android.com.superior.http.HttpUrl.ATTACHMENT;

public class TraceContentActivity extends BaseToolBarActivity {

    private EditText etContent;
    private EditText etProgress;
    private EditText etQuestion;
    private EditText etStep;
    private RecyclerView rcAttachment;

    private LinearLayout layoutRemark;

    private TextView tvRemark;

    private Spinner mSpMonth;

    private MonthAdapter mMonthAdapter;

    private AttachmentAdapter mAttachmentAdapter;

    private int unitTaskId = 0;

    private JSONObject mCompleteTrace;

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
    public int getLayoutRes() {
        return R.layout.activity_trace_content;
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("");
    }

    @Override
    protected void onFindViews() {
        etContent = (EditText) findViewById(R.id.et_content);
        etProgress = (EditText) findViewById(R.id.et_progress);
        etQuestion = (EditText)findViewById(R.id.et_question);
        etStep = (EditText)findViewById(R.id.et_step);

        rcAttachment = (RecyclerView) findViewById(R.id.rc_attachment);

        rcAttachment.setLayoutManager(new GridLayoutManager(this, 5));

        rcAttachment.setAdapter(mAttachmentAdapter = new AttachmentAdapter());

        mSpMonth = findViewById(R.id.sp_task_month);

        layoutRemark = findViewById(R.id.layout_remark);

        tvRemark = findViewById(R.id.tv_remark);
    }

    @Override
    public void onInitView() {
        mSpMonth.setAdapter(mMonthAdapter = new MonthAdapter());
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        requestUnitStayCompleteTrace();
    }

    private void requestUnitStayCompleteTrace() {
        showLoading("加载中...");
        OkGo.<JSONObject>get(HttpUrl.GET_UNIT_STAY_TRACE + "/" + unitTaskId).tag(this).execute(getJsonObjectCallback(HttpUrl.GET_UNIT_STAY_TRACE));
    }

    private void refreshContent(JSONObject jsonObject) {
        String remark = jsonObject.getString("remark");
        if (!TextUtils.isEmpty(remark)) {
            layoutRemark.setVisibility(View.VISIBLE);
            tvRemark.setText(remark);
        }

        mAttachmentAdapter.appendImages(jsonObject.getString("attachments"));

        etContent.setText(jsonObject.getString("content"));

        etProgress.setText(jsonObject.getString("progress"));

        etQuestion.setText(TextUtils.isEmpty(jsonObject.getString("question")) ? "" : jsonObject.getString("question"));

        etStep.setText(TextUtils.isEmpty(jsonObject.getString("step")) ? "" : jsonObject.getString("step"));

        mMonthAdapter.setSelectDate(jsonObject.getString("taskDate"));

    }

    public boolean checked() {
        if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
            Toast.makeText(this, "请填写报送的工作内容", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(etProgress.getEditableText().toString())) {
            Toast.makeText(this, "请填写自评工作进度", Toast.LENGTH_LONG).show();
            return false;
        }

        JSONObject month = mMonthAdapter.getItem(mSpMonth.getSelectedItemPosition());
        if (month.getString("value").length() == 0) {
            Toast.makeText(this, "请选择报送的月份", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void newTraceClick(View view) {
        if (checked()) {
            showLoading("请稍后...");
            HttpParams params = new HttpParams();
            params.put("unitTaskId", unitTaskId);
            params.put("unitId", User.getInstance().getUnitId());
            params.put("userId", User.getInstance().getUserId());
            params.put("content", etContent.getText().toString());
            params.put("progress", etProgress.getText().toString());
            params.put("step", etStep.getText().toString());
            params.put("question", etQuestion.getText().toString());
            params.put("taskDate", mMonthAdapter.getItem(mSpMonth.getSelectedItemPosition()).getString("value"));
            params.put("attachments", mAttachmentAdapter.makeExistAttachments());
            params.put(mAttachmentAdapter.makeFileParams());

            if (mCompleteTrace == null) {
                OkGo.<JSONObject>post(HttpUrl.UNIT_STAY_SIGNREPORT_TRACE).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.UNIT_STAY_SIGNREPORT_TRACE));
            }

            if (mCompleteTrace != null) {
                OkGo.<JSONObject>post(HttpUrl.UNIT_STAY_SIGNREPORT_TRACE + "/" + mCompleteTrace.getString("id")).isMultipart(true).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.UNIT_STAY_SIGNREPORT_TRACE + "/" + mCompleteTrace.getString("id")));
            }
        }
    }


    @Override
    protected void onJsonObjectCallBack(String action, JSONObject data) {
        super.onJsonObjectCallBack(action, data);
        if (action == null || action.length() == 0) return;
        if (action.equals(HttpUrl.GET_UNIT_STAY_TRACE)) {
            if (data.containsKey("status") && data.getIntValue("status") == -1) {
                Toast.makeText(TraceContentActivity.this, "已报送过领导，请等待领导手签审核", Toast.LENGTH_SHORT).show();
                finish();
            }
            setBarTitle(data.size() < 2 ? "报送工作内容" : "更新报送内容");
            if (data.size() < 2) return;
            mCompleteTrace = data;
            if (mCompleteTrace == null) return;
            refreshContent(mCompleteTrace);
        }
        if (action.equals(HttpUrl.UNIT_STAY_SIGNREPORT_TRACE)) {
            Toast.makeText(TraceContentActivity.this, "提交成功，等待主要负责人手签上报", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
        if (action.startsWith(HttpUrl.UNIT_STAY_SIGNREPORT_TRACE + "/")) {
            Toast.makeText(TraceContentActivity.this, "更新成功，等待主要负责人手签上报", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
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
//            ((TextView) convertView).setText(getItem(position).getString("name"));
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

    private View.OnClickListener mAddAttachmentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PictureSelector.create(TraceContentActivity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .theme(R.style.picture_default_style)
                    .imageEngine(GlideEngine.createGlideEngine())
                    .isCompress(false)
                    .isAndroidQTransform(Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
                    .maxSelectNum(5)
                    .isEnableCrop(false)
                    .isPreviewImage(false)
                    .forResult(new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(List<LocalMedia> result) {
                            if (result == null || result.size() == 0) return;
                            for(LocalMedia media : result) {
                                String path = "";
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                    path = media.getAndroidQToPath();
                                } else {
                                    path = media.getPath();
                                }
                                if (TextUtils.isEmpty(path) || TextUtils.isEmpty(media.getFileName())) {
                                    showToast("未读取到图片信息");
                                    return;
                                }
                                JSONObject image = new JSONObject();
                                image.put("filePath", path);
                                image.put("fileName", media.getFileName());
                                mAttachmentAdapter.appendImage(image);
                            }
                        }

                        @Override
                        public void onCancel() {
                            // 取消
                        }
                    });

        }
    };

    private View.OnClickListener mOnItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Integer) {
                int position = (Integer) v.getTag();
                JSONObject image = mAttachmentAdapter.getItem(position);
                Intent intent = new Intent(TraceContentActivity.this, ImageActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TraceContentActivity.this, v, "transition");
                if (image.containsKey("fileName") && !TextUtils.isEmpty(image.getString("fileName"))) {
                    intent.putExtra("path", image.getString("filePath"));
                }
                if (image.containsKey("imageName") && !TextUtils.isEmpty(image.getString("imageName"))) {
                    intent.putExtra("url", HttpUrl.ATTACHMENT + image.getString("imageName"));
                }
                startActivity(intent, options.toBundle());
            }
        }
    };

    class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.BaseViewHolder> {
        public static final int TYPE_VIEW_CONTENT = 0X01;
        public static final int TYPE_VIEW_ADD = 0X02;

        private List<JSONObject> attachments = new ArrayList<>();


        private int width = 0;

        public AttachmentAdapter() {
            width = makeImageWidth();
        }

        private int makeImageWidth() {
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;     // 屏幕宽度（像素）
            int margin = CommonUtils.dip2px(20);
            return (width - margin) / 5 - 10;
        }

        public HttpParams makeFileParams() {
            HttpParams httpParams = new HttpParams();
            for(JSONObject image : attachments) {
                if (image.containsKey("fileName") && !TextUtils.isEmpty(image.getString("fileName"))) {
                    httpParams.put(System.currentTimeMillis() + image.getString("fileName"), new File(image.getString("filePath")));
                }
            }
            return httpParams;
        }

        public String makeExistAttachments() {
            String exsitAttachments = "";
            for (JSONObject image : attachments) {
                if (image.containsKey("imageName") && !TextUtils.isEmpty(image.getString("imageName"))) {
                    exsitAttachments += exsitAttachments.length() > 0 ? "," : "";
                    exsitAttachments += image.getString("imageName");
                }
            }
            return exsitAttachments;
        }

        public void appendImage(JSONObject image) {
            attachments.add(image);
            notifyDataSetChanged();
        }

        public void appendImages(ArrayList<JSONObject> images) {
            if (images != null) {
                attachments.addAll(images);
                notifyDataSetChanged();
            }
        }

        public void appendImages(String images) {
            if (TextUtils.isEmpty(images)) return;
            String[] attr = images.split(",");
            for (String str : attr) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("imageName", str);
                attachments.add(jsonObject);
            }
            notifyDataSetChanged();
        }

        public List<JSONObject> getAttachments() {
            return attachments;
        }

        public JSONObject getItem(int position) {
            if (position < attachments.size()) {
                return attachments.get(position);
            }
            return null;
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_VIEW_CONTENT) {
                return new AttachmentViewHolder(getLayoutInflater().inflate(R.layout.rc_item_image, parent, false));
            }
            if (viewType == TYPE_VIEW_ADD) {
                return new AddViewHolder(getLayoutInflater().inflate(R.layout.rc_item_image, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            holder.bindViewHolder(position);
        }

        @Override
        public int getItemCount() {
            return attachments.size() + 1;
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

        class AddViewHolder extends BaseViewHolder {

            public AddViewHolder(View itemView) {
                super(itemView);

                mImageView.setImageResource(R.mipmap.icon_add);

                itemView.setClickable(true);

                itemView.setOnClickListener(mAddAttachmentClickListener);
            }

            @Override
            void bindViewHolder(int position) {

            }
        }

        class AttachmentViewHolder extends BaseViewHolder {


            public AttachmentViewHolder(View itemView) {
                super(itemView);

                itemView.setClickable(true);
                itemView.setOnClickListener(mOnItemClick);
            }

            public void bindViewHolder(int position) {
                itemView.setTag(position);
                JSONObject image = attachments.get(position);
                if (!TextUtils.isEmpty(image.getString("imageName"))) {
                    Glide.with(getBaseContext()).load(ATTACHMENT + "/" + image.getString("imageName")).placeholder(R.mipmap.ic_default_attachment).centerCrop().into(mImageView);
                } else if (!TextUtils.isEmpty(image.getString("filePath"))) {
                    Glide.with(getBaseContext()).load(new File(image.getString("filePath"))).placeholder(R.mipmap.ic_default_attachment).centerCrop().into(mImageView);
                }
            }
        }

    }

}
