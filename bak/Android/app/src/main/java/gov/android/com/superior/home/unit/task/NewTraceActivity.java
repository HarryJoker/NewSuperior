package gov.android.com.superior.home.unit.task;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.TakePhotoLoadActivity;
import gov.android.com.superior.TransitionActivity;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.entity.User;
import gov.android.com.superior.http.Config;
import gov.android.com.superior.tools.CommonUtils;
import gov.android.com.superior.tools.FileReader;
import gov.android.com.superior.tools.FileUtils;

public class NewTraceActivity extends TakePhotoLoadActivity {

    private static final int FILE_SELECT_CODE = 0xFF00F1;

    private static final int DIALOG_UPLOAD = 0XFF00F2;

    private long taskId;

    private GridView gridView;
    private EditText et_content;
    private TextView tv_gpsAddress;

    private TextView tv_create;

    private Map<String, String> locate = new HashMap<>();

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private TakePhoto takePhoto;

    private AttachmentAdapter attachmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trace);

        setTitle("任务提报");

        taskId = getIntent().getLongExtra("taskId", 0);

        gridView = (GridView)findViewById(R.id.gv_attachment);

        tv_create = (TextView) findViewById(R.id.tv_create_click);

        registerForContextMenu(gridView);

        et_content = (EditText)findViewById(R.id.et_content);

        tv_gpsAddress = (TextView)findViewById(R.id.tv_gpsAddress);

        tv_gpsAddress.setText("定位中....");

        takePhoto = getTakePhoto();

        takePhoto.onEnableCompress(buildCompressConfig(), false);

        initLocation();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //获取GridView的item对象
        TImage image = (TImage) gridView.getItemAtPosition(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
        attachmentAdapter.remove(image);
        attachmentAdapter.notifyDataSetChanged();

        return super.onContextItemSelected(item);
    }


    private boolean check() {
        if (TextUtils.isEmpty(et_content.getText().toString())) {
            Toast.makeText(this, "请填写工作内容", Toast.LENGTH_LONG).show();
            return false;
        }

//        if (locate.size() != 2) {
//            Toast.makeText(this, "请手动定位当前位置", Toast.LENGTH_LONG).show();
//            return false;
//        }
        return true;
    }

    public void readClick(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
           try{
               String filePath = FileUtils.getPath(this, data.getData());
               if (filePath == null || filePath.isEmpty()) {

                   MobclickAgent.reportError(this, new NullPointerException("Select File Read onResult FilePath Null"));
                   Toast.makeText(this, "文件读取失败", Toast.LENGTH_LONG).show();
               } else {
                   et_content.setText(FileReader.ReadTxtFile(this, filePath));
               }
           } catch (Exception exception) {
               exception.printStackTrace();

               MobclickAgent.reportError(this, exception);

               Toast.makeText(this, "文件读取失败", Toast.LENGTH_LONG).show();
           }
        }
    }

    public void newTraceClick(View v) {

        if (check()) {
            showProgress("提报中");

            HttpParams params = new HttpParams();
            params.put("taskId", taskId);
            params.put("userId", User.getInstance().getUserId());
            params.put("content", et_content.getText().toString().replace("%", "/a25a/"));
            Map<String, Object> unit = (Map)User.getInstance().get("unit");
            params.put("unitId", unit.get("id").toString());
            params.put("unitName", unit.get("name").toString());
            params.put("category", getIntent().getBundleExtra("task").get("category").toString());

            params.put("address", (!locate.containsKey("address") || locate.get("address") == null ? "" : locate.get("address").toString()));
            params.put("location", (!locate.containsKey("location") || locate.get("location") == null ? "" : locate.get("location").toString()));

            Logger.d(params);
//            0：正常的上报工作，1：系统自动生成督促，2：督查主动催报，3,接收任务 4：领导批示
            params.put("type", 0);
            if (attachmentAdapter != null) {
                params.put(attachmentAdapter.getAttachmentParams());
            }

            OkGo.<Map>post(Config.TRACE_CREATE).headers("accept-encoding", "gzip").params(params).tag(this).execute(jsonCallback);
        }
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
            Toast.makeText(NewTraceActivity.this, "提报成功", Toast.LENGTH_LONG).show();
            tv_create.setBackgroundColor(Color.parseColor("#d3d3d3"));
            tv_create.setEnabled(false);
            setResult(RESULT_OK);
            finish();
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(NewTraceActivity.this, TransitionActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(NewTraceActivity.this, view, "transition");
            intent.putExtra("path", attachmentAdapter.getOriginalPath(i));
            startActivity(intent, options.toBundle());
        }
    };

    //-----------------------------------------Take photo---------------------------------------------//
    public void addAttachmentClick(View v) {
//        showDialog(DIALOG_UPLOAD);
        takePhoto.onEnableCompress(buildCompressConfig(), true);
        takePhoto.onPickMultiple(5);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_UPLOAD:
                return new AlertDialog.Builder(this).setItems(new String[]{"从手机相册选择"}, dialogClickListener).create();
//            return new AlertDialog.Builder(this).setItems(new String[]{"拍照", "从手机相册选择"}, dialogClickListener).create();
            default:
                break;
        }
        return super.onCreateDialog(id);
    }

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == 1) {
                takePhoto.onEnableCompress(buildCompressConfig(), true);
                takePhoto.onPickFromCapture(buildImageUri());
            }
            if (which == 0) {
                takePhoto.onEnableCompress(buildCompressConfig(), true);
                takePhoto.onPickMultiple(5);
            }
        }
    };

    private CompressConfig buildCompressConfig () {
        return new CompressConfig.Builder()
                .setMaxSize(1024 * 200)
                .setMaxPixel(200)
                .enableReserveRaw(true)
                .create();
    }

    private Uri buildImageUri() {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        return Uri.fromFile(file);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        Logger.d(result.getImage());
        Logger.d(result.getImages());

        if (attachmentAdapter == null) {
            attachmentAdapter = new AttachmentAdapter();
            gridView.setAdapter(attachmentAdapter);
            gridView.setOnItemClickListener(itemClickListener);
        }

        if (result.getImages() != null) {
            attachmentAdapter.addAll(result.getImages());
        }

        attachmentAdapter.notifyDataSetChanged();
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


    //-----------------------------------------GSP---------------------------------------------//

    public void gpsLocationClick(View v) {
        tv_gpsAddress.setText("定位中....");
        locationClient.startLocation();
    }

    private void initLocation(){
        AMapLocationClient.setApiKey("448c1e218fb67cc92704ed35a086669e");

        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        locationClient.startLocation();
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                locate.put("address", location.getAddress());
                locate.put("location", location.getLongitude() + "," + location.getLatitude());

                Logger.d(locate);

                tv_gpsAddress.setText(location.getAddress());

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
//                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
                //定位之后的回调时间
//                sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                //解析定位结果，
                String result = sb.toString();
//                tvResult.setText(result);
                Logger.d(result);
            } else {
//                tvResult.setText("定位失败，loc is null");
                Logger.d("定位失败");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    class AttachmentAdapter extends BaseAdapter {

        private int imageWidth;

        private List<TImage> attachments = new ArrayList<>();

        public AttachmentAdapter() {
            imageWidth = makeImageWidth();
        }

        private int makeImageWidth() {
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;     // 屏幕宽度（像素）
            int margin = CommonUtils.dip2px(20);
            return (width - margin) / 5 - 10;
        }

        public HttpParams getAttachmentParams() {
            HttpParams params = new HttpParams();
            for (TImage image : attachments) {
                params.put(System.currentTimeMillis() + "" + image, new File(image.getOriginalPath()));
            }
            return params;
        }

        public void addAll(List<TImage> images) {
            if (images != null && images.size() > 0) {
                attachments.addAll(images);
            }
        }

        public void remove(TImage image) {
            if (image != null) attachments.remove(image);
        }


        @Override
        public int getCount() {
            return attachments.size();
        }

        @Override
        public TImage getItem(int i) {
            return attachments.get(i);
        }

        public String getOriginalPath(int i) {
            return attachments.get(i).getOriginalPath();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.item_image, null);
            ImageView imageView = view.findViewById(R.id.iv_attachment);
            ViewGroup.LayoutParams para = imageView.getLayoutParams();
            if (para == null) para = new ViewGroup.LayoutParams(imageWidth, imageWidth);
            para.height = imageWidth;
            para.width = imageWidth;
            imageView.setLayoutParams(para);

            Glide.with(getBaseContext()).load(new File(getItem(i).getCompressPath())).placeholder(R.mipmap.background_attachment).centerCrop().into(imageView);
            return view;
        }
    }
}
