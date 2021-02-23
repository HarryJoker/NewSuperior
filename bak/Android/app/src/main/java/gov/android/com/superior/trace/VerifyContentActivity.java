package gov.android.com.superior.trace;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.PaintActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.tools.Eutils;

public class VerifyContentActivity extends BaseLoadActivity {

    public static final int TYPE_BACK   = 0X01;
    public static final int TYPE_COMMIT = 0X02;

    private EditText etContent;
    private EditText etProgress;
    private RecyclerView rcAttachment;

    private AttachmentAdapter mAttachmentAdapter;

    private int unitTaskId = 0;

    private int type = 0;

    private String signPath;

    private ImageView tvSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_content);

        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);

        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle("审阅报送工作");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((RadioGroup)findViewById(R.id.rg_types)).setOnCheckedChangeListener(mOnCheckedChangeListener);

        etContent = (EditText) findViewById(R.id.et_content);

        etProgress = (EditText) findViewById(R.id.et_progress);

        rcAttachment = (RecyclerView) findViewById(R.id.rc_attachment);

        rcAttachment.setLayoutManager(new GridLayoutManager(this, 5));

        tvSign = findViewById(R.id.tv_sign);

//        mSpinner = findViewById(R.id.sp_users);

        requestUnitContent();

    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_back) {
                type = TYPE_BACK;
            }

            if (checkedId == R.id.rb_confirm) {
                type = TYPE_COMMIT;
            }
        }
    };

    private void requestUnitContent() {
        showProgress("加载中...");
        OkGo.<JSONObject>get(Config.UNITCONTENT_BY_UNITTASK + "/" + unitTaskId).tag(this).execute(contentCallback);
    }

    private JsonObjectCallBack<JSONObject> contentCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            removeProgress();
            JSONObject jsonObject = response.body();
            if (jsonObject == null) return;

            refreshContent(response.body());
        }

        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            removeProgress();
        }
    };

    private void refreshContent(JSONObject jsonObject) {
        String attachment = jsonObject.getString("attachments");
        if (!TextUtils.isEmpty(attachment)) {
            mAttachmentAdapter = new AttachmentAdapter(new ArrayList<String>(Arrays.asList(attachment.split(","))));

            rcAttachment.setAdapter(mAttachmentAdapter);
        }

        etContent.setText(jsonObject.getString("content"));

        etProgress.setText(jsonObject.getString("progress"));

        if (jsonObject.getIntValue("userId") != User.getInstance().getUserId()) {
            findViewById(R.id.tv_create_click).setVisibility(View.GONE);
            Toast.makeText(this, "您没有权限手签批阅报送内容", Toast.LENGTH_LONG).show();
        }
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
        showProgress("报送中...");
        HttpParams params = new HttpParams();
        params.put("unitTaskId", unitTaskId);
//            params.put("content", etContent.getText().toString().replace("%", "/a25a/"));
        params.put("content", etContent.getText().toString());
        params.put("status", 31);//已报送
        params.put("progress", etProgress.getText().toString());
        params.put("attachments", mAttachmentAdapter == null ? "" : mAttachmentAdapter.getStringAttachments());
        params.put("file", new File(signPath));
        params.put("unitId", User.getInstance().getUnitId());
        OkGo.<JSONObject>post(Config.TRACE_NEW).headers("accept-encoding", "gzip").params(params).tag(this).execute(commitCallback);
    }

    private void verifyBack() {
        showProgress("退回中...");
        HttpParams params = new HttpParams();
        params.put("status", 0);//已报送
        params.put("userId", 0);
        OkGo.<JSONObject>post(Config.UNITCONTENT_COMMIT_BACK + "/" + unitTaskId).headers("accept-encoding", "gzip").params(params).tag(this).execute(backCallback);
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

    private JsonObjectCallBack<JSONObject> backCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            removeProgress();
            Toast.makeText(VerifyContentActivity.this, "退回成功", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            removeProgress();
            Toast.makeText(VerifyContentActivity.this, "退回失败", Toast.LENGTH_SHORT).show();
        }
    };

    private JsonObjectCallBack<JSONObject> commitCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            removeProgress();
            Toast.makeText(VerifyContentActivity.this, "报送成功", Toast.LENGTH_SHORT).show();
            finish();
        }
        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            removeProgress();
            Toast.makeText(VerifyContentActivity.this, "报送失败", Toast.LENGTH_SHORT).show();
        }
    };


    private View.OnClickListener mOnItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Integer) {
                int position = (Integer)v.getTag();
                String tImage = mAttachmentAdapter.getItem(position);
                if (tImage == null || TextUtils.isEmpty(tImage)) return;
                Intent intent = new Intent(VerifyContentActivity.this, TransitionActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(VerifyContentActivity.this, v, "transition");
                intent.putExtra("url", Config.ATTACHMENT + tImage);
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
            return new AttachmentViewHolder(getLayoutInflater().inflate(R.layout.item_image, parent, false));
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
                Glide.with(getBaseContext()).load(Config.ATTACHMENT + tImage).placeholder(R.mipmap.background_attachment).centerCrop().into(mImageView);
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


}
