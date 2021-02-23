package gov.android.com.superior.trace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.TakePhotoLoadActivity;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonObjectCallBack;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.CommonUtils;

public class NewContentActivity extends TakePhotoLoadActivity {

    public static final int TYPE_NEW    = 0X01;
    public static final int TYPE_UPDATE = 0X02;

    private EditText etContent;
    private EditText etProgress;
    private RecyclerView rcAttachment;

    private Spinner mSpinner;

    private UserAdapter mUserAdapter;

    private AttachmentAdapter mAttachmentAdapter;

    private int unitTaskId = 0;

    private int type = 0;

//    private int userId = 0; //用户修改完善，已经选择的报送人

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_content);

        type = getIntent().getIntExtra("type", 0);

        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);

        if (unitTaskId == 0) {
            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        delegate.setTitle(type == TYPE_NEW ? "报送工作内容" : "更新报送内容");

        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etContent = (EditText) findViewById(R.id.et_content);
        etProgress = (EditText) findViewById(R.id.et_progress);

        rcAttachment = (RecyclerView) findViewById(R.id.rc_attachment);

        rcAttachment.setLayoutManager(new GridLayoutManager(this, 5));

        rcAttachment.setAdapter(mAttachmentAdapter = new AttachmentAdapter());

        mSpinner = findViewById(R.id.sp_users);

        if (type == TYPE_UPDATE) {
            requestUnitContent();
        }

        if (type == TYPE_NEW) {
            requestUnitUsers();
        }

        Logger.d("oncreate........................");
    }

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

//            userId = jsonObject.getIntValue("userId");

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
            mAttachmentAdapter.appendImages(new ArrayList<Object>(Arrays.asList(attachment.split(","))));
        }

        etContent.setText(jsonObject.getString("content"));

        etProgress.setText(jsonObject.getString("progress"));

        requestUnitUsers();
    }

    private void requestUnitUsers() {
        showProgress("加载中...");
        OkGo.<JSONArray>get(Config.USER_LIST_BY_UNIT + "/" + User.getInstance().getUnitId()).tag(this).execute(usersCallback);
    }

    private JsonObjectCallBack<JSONArray> usersCallback = new JsonObjectCallBack<JSONArray>() {
        @Override
        public void onSuccess(Response<JSONArray> response) {
            removeProgress();
            mSpinner.setAdapter(mUserAdapter = new UserAdapter(response.body()));
        }

        @Override
        public void onError(Response<JSONArray> response) {
            super.onError(response);
            removeProgress();
        }
    };

    public boolean checked() {
        if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
            Toast.makeText(this, "请填写报送的工作内容", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(etProgress.getEditableText().toString())) {
            Toast.makeText(this, "请填写报送的工作内容", Toast.LENGTH_LONG).show();
            return false;
        }

        JSONObject user = mUserAdapter.getItem(mSpinner.getSelectedItemPosition());
        if (user.getIntValue("id") == 0) {
            Toast.makeText(this, "请选择报送的主要负责人", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void newTraceClick(View view) {
        if (checked()) {
            showProgress("提交中...");
            HttpParams params = new HttpParams();
            params.put("unitTaskId", unitTaskId);
            params.put("userId", mUserAdapter.getItem(mSpinner.getSelectedItemPosition()).getString("id"));
//            params.put("content", etContent.getText().toString().replace("%", "/a25a/"));
            params.put("content", etContent.getText().toString());
            params.put("progress", etProgress.getText().toString());
            String attachments = "";
            for (Object image : mAttachmentAdapter.getAttachments()) {
                if (image instanceof TImage) {
                    params.put(System.currentTimeMillis() + "" + image, new File(((TImage)image).getOriginalPath()));
                }
                if (image instanceof String) {
                    attachments += attachments.length() > 0 ? "," : "";
                    attachments += image;
                }
            }
            if (attachments.length() > 0) {
                params.put("attachments", attachments);
            }

            if (type == TYPE_NEW) {
                OkGo.<JSONObject>post(Config.UNITCONTENT_CREATE).headers("accept-encoding", "gzip").params(params).tag(this).execute(newCallback);
            }

            if (type == TYPE_UPDATE) {
                OkGo.<JSONObject>post(Config.UNITCONTENT_RE_COMMIT + "/" + unitTaskId).isMultipart(true).params(params).tag(this).execute(updateCallback);
            }
        }
    }

    private JsonObjectCallBack<JSONObject> newCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            Toast.makeText(NewContentActivity.this, "提交成功，等待主要负责人手签上报", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private JsonObjectCallBack<JSONObject> updateCallback = new JsonObjectCallBack<JSONObject>() {
        @Override
        public void onSuccess(Response<JSONObject> response) {
            Toast.makeText(NewContentActivity.this, "更新成功，等待主要负责人手签上报", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onError(Response<JSONObject> response) {
            super.onError(response);
            Toast.makeText(NewContentActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
        }
    };

    class UserAdapter extends BaseAdapter {
        private JSONArray users = new JSONArray();

        public UserAdapter(JSONArray array) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "请选择报送的主要负责人");
            jsonObject.put("id", 0);
            users.add(0, jsonObject);

            if (array != null) {
                users.addAll(array);
            }
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return users.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.item_dropdown, parent, false);
            ((TextView)convertView).setText(getItem(position).getString("name"));
            return convertView;
        }
    }


    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        mAttachmentAdapter.appendImages(result.getImages());
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    private TakePhoto takePhoto;

    private View.OnClickListener mAddAttachmentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (takePhoto == null) {
                takePhoto = getTakePhoto();
            }
            takePhoto.onEnableCompress(buildCompressConfig(), false);
            takePhoto.onPickMultiple(5);
        }
    };

    private CompressConfig buildCompressConfig () {
        return new CompressConfig.Builder()
                .setMaxSize(1024 * 200)
                .setMaxPixel(200)
                .enableReserveRaw(true)
                .create();
    }

    private View.OnClickListener mOnItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Integer) {
                int position = (Integer)v.getTag();
                Object object = mAttachmentAdapter.getItem(position);
                if (object == null) return;
                Intent intent = new Intent(NewContentActivity.this, TransitionActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(NewContentActivity.this, v, "transition");
                if (object instanceof TImage) {
                    intent.putExtra("path", ((TImage)object).getOriginalPath());
                }
                if (object instanceof String) {
                    intent.putExtra("url", Config.ATTACHMENT + object.toString());
                }
                startActivity(intent, options.toBundle());
            }
        }
    };

    class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.BaseViewHolder> {
        public static final int TYPE_VIEW_CONTENT = 0X01;
        public static final int TYPE_VIEW_ADD     = 0X02;

        private List<Object> attachments = new ArrayList<>();

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

        public void appendImage(Object image) {
            attachments.add(image);
            notifyDataSetChanged();
        }

        public void appendImages(ArrayList<?> images) {
            if (images != null) {
                attachments.addAll(images);
                notifyDataSetChanged();
            }
        }

        public List<Object> getAttachments() {
            return attachments;
        }

        public Object getItem(int position) {
            if (position < attachments.size()) {
                return attachments.get(position);
            }
            return null;
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_VIEW_CONTENT) {
                return new AttachmentViewHolder(getLayoutInflater().inflate(R.layout.item_image, parent, false));
            }
            if (viewType == TYPE_VIEW_ADD) {
                return new AddViewHolder(getLayoutInflater().inflate(R.layout.item_image, parent, false));
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
                Object object = attachments.get(position);
                if (object instanceof TImage) {
                    Glide.with(getBaseContext()).load(new File(((TImage)object).getCompressPath())).placeholder(R.mipmap.background_attachment).centerCrop().into(mImageView);
                }
                if (object instanceof String) {
                    Glide.with(getBaseContext()).load(Config.ATTACHMENT + object.toString()).placeholder(R.mipmap.background_attachment).centerCrop().into(mImageView);
                }
            }
        }

    }

}
