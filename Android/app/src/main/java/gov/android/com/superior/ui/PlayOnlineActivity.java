package gov.android.com.superior.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.android.business.adapter.DataAdapterImpl;
import com.android.business.adapter.DataAdapterInterface;
import com.android.business.entity.ChannelInfo;
import com.android.business.entity.UserInfo;
import com.android.business.entity.VictoryKey;
import com.android.business.exception.BusinessException;
import com.android.dahua.dhcommon.utils.Base64Helpr;
import com.android.dahua.dhcommon.utils.FileStorageUtil;
import com.android.dahua.dhcommon.utils.MicHelper;
import com.android.dahua.dhplaycomponent.IMediaPlayListener;
import com.android.dahua.dhplaycomponent.IOperationListener;
import com.android.dahua.dhplaycomponent.IPTZListener;
import com.android.dahua.dhplaycomponent.ITalkListener;
import com.android.dahua.dhplaycomponent.PlayManagerProxy;
import com.android.dahua.dhplaycomponent.audiotalk.param.CloudBaseTalk;
import com.android.dahua.dhplaycomponent.audiotalk.param.DPSTalk;
import com.android.dahua.dhplaycomponent.audiotalk.param.ExpressTalk;
import com.android.dahua.dhplaycomponent.audiotalk.param.TalkParam;
import com.android.dahua.dhplaycomponent.audiotalk.param.inner.CloudBaseTalkParam;
import com.android.dahua.dhplaycomponent.audiotalk.param.inner.DPSTalkParam;
import com.android.dahua.dhplaycomponent.audiotalk.param.inner.ExpressTalkParam;
import com.android.dahua.dhplaycomponent.camera.RTCamera.CloudBaseRTCamera;
import com.android.dahua.dhplaycomponent.camera.RTCamera.CloudBaseRTCameraParam;
import com.android.dahua.dhplaycomponent.camera.RTCamera.CloudRTCamera;
import com.android.dahua.dhplaycomponent.camera.RTCamera.DPSRTCamera;
import com.android.dahua.dhplaycomponent.camera.RTCamera.DPSRTCameraParam;
import com.android.dahua.dhplaycomponent.camera.RTCamera.ExpressRTCamera;
import com.android.dahua.dhplaycomponent.camera.RTCamera.ExpressRTCameraParam;
import com.android.dahua.dhplaycomponent.camera.inner.Camera;
import com.android.dahua.dhplaycomponent.camera.inner.RealInfo;
import com.android.dahua.dhplaycomponent.common.Err;
import com.android.dahua.dhplaycomponent.common.PlayStatusType;
import com.android.dahua.dhplaycomponent.common.PtzOperation;
import com.android.dahua.dhplaycomponent.common.PtzZoomState;
import com.android.dahua.dhplaycomponent.common.RecordType;
import com.android.dahua.dhplaycomponent.common.TalkResultType;
import com.android.dahua.dhplaycomponent.windowcomponent.entity.ControlType;
import com.android.dahua.dhplaycomponent.windowcomponent.window.PlayWindow;
import com.first.orient.base.utils.JokerLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.BuildConfig;
import gov.android.com.superior.R;
import gov.android.com.superior.entity.CommonRecord;
import gov.android.com.superior.http.EnvironmentHelper;


/**
 * Created by 26499 on 2017/11/15.
 */

public class PlayOnlineActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = PlayOnlineActivity.class.getSimpleName();
    public static final int Stream_Main_Type = 1;		//主码流 // the main stream
    public static final int Stream_Assist_Type = 2;		//辅码流 // auxiliary stream
    public static final int Stream_Third_Type = 3;		//三码流 // three stream

    public static final int KEY_Handler_Stream_Played = 1;
    public static final int KEY_Handler_First_Frame = 2;
    public static final int KEY_Handler_Net_Error = 3;
    public static final int KEY_Handler_Play_Failed = 4;
    public static final int KEY_Handler_Talk_Success = 7;
    public static final int KEY_Handler_Talk_failed = 8;

    private PlayWindow mPlayWin;
    protected PlayManagerProxy mPlayManager;
    private List<ChannelInfo> channelInfoList;
    protected Map<Integer, ChannelInfo> channelInfoMap = new HashMap<>();
    private DataAdapterInterface dataAdapterInterface;
    protected String[] recordPath;
    private boolean isFull = false;
    private String encryptKey = "";

//    private RelativeLayout rlTitle;
    private LinearLayout llControlLayout;

    private TextView tvCapture;
    private TextView tvRecord;
    private TextView tvTalk;
    private TextView tvSound;
    private TextView tvPlay;
    private TextView tvCloud;
    private TextView tvStreamMain;
    private TextView tvStreamAssist;
    private TextView tvStreamThird;
    private TextView tvRemove;
    private TextView tvFull;

    protected Handler mPlayOnlineHander = new Handler() {
        public void handleMessage(Message msg) {
            dissmissProgressDialog();
            switch (msg.what){
                case KEY_Handler_Stream_Played:
                    int winIndex = (Integer) msg.obj;
                    tvPlay.setText(R.string.play_stop);
                    if(winIndex != mPlayManager.getSelectedWindowIndex()) return;
                    if(channelInfoList != null && channelInfoList.size() == 1) {
                        mPlayManager.maximizeWindow(winIndex);
                        mPlayManager.setEZoomEnable(winIndex, true);
                    }
                    if(mPlayManager.isNeedOpenAudio(winIndex)) openAudio(winIndex);
                    refreshBtnState();
                    break;
                case KEY_Handler_First_Frame:
                    break;
                case KEY_Handler_Net_Error:
                    toast(R.string.play_net_error);
                    winIndex = (Integer) msg.obj;
                    stopPlay(winIndex);
                    break;
                case KEY_Handler_Play_Failed:
                    winIndex = (Integer) msg.obj;
                    stopPlay(winIndex);
                    break;
                case KEY_Handler_Talk_Success:
                    dissmissProgressDialog();
                    toast(R.string.play_talk_start);
                    tvTalk.setSelected(true);
                    break;
                case KEY_Handler_Talk_failed:
                    dissmissProgressDialog();
                    closeTalk(mPlayManager.getSelectedWindowIndex());
                    toast(R.string.play_talk_failed);
                    tvTalk.setSelected(false);
                    break;
                case 10086:
                    mPlayManager.addCameras(getCameras());
                    mPlayManager.playCurpage();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dss_activity_play_online);

        setTitle("实时监控画面");

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this, R.style.wait_dialog);
        mProgressDialog.setCanceledOnTouchOutside(false);

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayManager != null) {
            mPlayManager.unitPlayManager();
            mPlayManager = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        replay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAll();
    }

    private void initView() {
//        rlTitle = (RelativeLayout) findViewById(R.id.playonline_title);
        llControlLayout = (LinearLayout) findViewById(R.id.playonline_control_layout);
        mPlayWin = (PlayWindow) findViewById(R.id.play_window);
        tvCapture = (TextView) findViewById(R.id.capture);
        tvRecord = (TextView) findViewById(R.id.record);
        tvTalk = (TextView) findViewById(R.id.talk);
        tvPlay = (TextView) findViewById(R.id.play);
        tvSound = (TextView) findViewById(R.id.sound);
        tvCloud = (TextView) findViewById(R.id.cloud);
        tvStreamMain = (TextView) findViewById(R.id.stream_main);
        tvStreamAssist = (TextView) findViewById(R.id.stream_assist);
        tvStreamThird = (TextView) findViewById(R.id.stream_third);
        tvRemove = (TextView) findViewById(R.id.remove);
        tvFull = (TextView) findViewById(R.id.full);

        tvCapture.setOnClickListener(this);
        tvRecord.setOnClickListener(this);
        tvTalk.setOnClickListener(this);
        tvSound.setOnClickListener(this);
        tvPlay.setOnClickListener(this);
        tvCloud.setOnClickListener(this);
        tvStreamMain.setOnClickListener(this);
        tvStreamAssist.setOnClickListener(this);
        tvStreamThird.setOnClickListener(this);
        tvRemove.setOnClickListener(this);
        tvFull.setOnClickListener(this);
    }

    private void initData() {
        dataAdapterInterface = DataAdapterImpl.getInstance();
        //初始化playManager
        // initialize playManager.
        mPlayManager = new PlayManagerProxy();
        //初始化窗口数量，默认显示4个窗口，最多16窗口，若设置单窗口均设置为1
        // the number of initialization window, the default display is 4 Windows, up to 16 Windows, if the setting of single window is set to 1.
        mPlayManager.init(this, 16, 1, mPlayWin);
        //设置对讲监听
        // set the intercom monitor.
        mPlayManager.setOnTalkListener(iTalkListener);
        //设置播放监听
        // set play monitor.
        mPlayManager.setOnMediaPlayListener(iMediaPlayListener);
        //设置云台监听
        // set the cloud monitor.
        mPlayManager.setOnPTZListener(iptzListener);
        //设置窗口操作监听
        // set window operation to listen.
        mPlayManager.setOnOperationListener(iOperationListener);
        //初始化窗口大小
        // initialization window size.
        initCommonWindow();
        channelInfoList = (List<ChannelInfo>) getIntent().getSerializableExtra("channel_info_list");
        JokerLog.e("intent channelInfos:" + JSONObject.toJSONString(channelInfoList));
        int index = 0;
        if(channelInfoList != null) {
            for(ChannelInfo channelInfo : channelInfoList) {
                channelInfoMap.put(index, channelInfo);
                index ++;
            }
        }

        playBatch();
    }

    /**
     * 初始化视频窗口
     *
     */
    public void initCommonWindow() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int mScreenWidth = metric.widthPixels; // 屏幕宽度（像素）
        int mScreenHeight = metric.heightPixels; // 屏幕高度（像素）
        mScreenHeight = mScreenWidth * 3 / 4;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mPlayWin.getLayoutParams();
        lp.width = mScreenWidth;
        lp.height = mScreenHeight;
        mPlayWin.setLayoutParams(lp);
        mPlayWin.forceLayout(mScreenWidth, mScreenHeight);
    }

    private void getEncryptKey() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VictoryKey victoryKey = dataAdapterInterface.getCurrentMediaVK(channelInfoMap.get(0).getDeviceUuid());
                    if(victoryKey != null) encryptKey = coverEncryptKey(victoryKey.getVkId(), victoryKey.getVkValue());
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                mPlayOnlineHander.sendEmptyMessage(10086);
            }
        }).start();
    }

    public static String coverEncryptKey(String encryptId, String encryptKey) {
        if(TextUtils.isEmpty(encryptId) || TextUtils.isEmpty(encryptKey)) {
            return "";
        }
        byte[] inId = Base64Helpr.decode(encryptId);
        byte[] inKey = Base64Helpr.decode(encryptKey);
        byte[] outKey = new byte[inId.length + inKey.length + 1];
        outKey[0] = 0x01;
        for(int i = 0; i < inId.length; i++) {
            outKey[i + 1] = inId[i];
        }
        for(int i = 0; i < inKey.length; i++) {
            outKey[i + inId.length+ 1] = inKey[i];
        }
        //TODO
        return Base64Helpr.encode(outKey).replaceAll("\n", "");
    }

    /**
     * 批量播放
     */
    private void playBatch(){
        if(BuildConfig.isExpress) {
            showProgressDialog();
            getEncryptKey();
        } else {
            mPlayManager.addCameras(getCameras());
            mPlayManager.playCurpage();
        }
    }

    private List<Camera> getCameras(){
        List<Camera> cameras = new ArrayList<>();
        if(channelInfoList != null) {
            for(ChannelInfo channelInfo : channelInfoList){
                cameras.add(getCamera(channelInfo));
            }
        }
        return cameras;
    }

    /**
     * 设置播放camera的参数
     * @param channelInfo
     * @return
     */
    private Camera getCamera(ChannelInfo channelInfo){
        if(BuildConfig.isVCloud) {
            //创建播放Camera参数
            // create playback Camera parameters.
            CloudBaseRTCameraParam cloudBaseCameraParam = new CloudBaseRTCameraParam();
            //设置窗口要播放的通道ID
            // set the channel ID to play in the window.
            cloudBaseCameraParam.setCameraID(channelInfo.getChnSncode());
            //获取码流类型
            // access code flow type.
            int mStreamType = ChannelInfo.ChannelStreamType.getValue(channelInfo.getStreamType());
            if(mStreamType > Stream_Assist_Type) mStreamType = Stream_Assist_Type;
            cloudBaseCameraParam.setStreamType(mStreamType - 1);
            UserInfo mUserInfo = CommonRecord.getInstance().getUserInfo();
            cloudBaseCameraParam.setDpRestToken((String)mUserInfo.getExtandAttributeValue("token"));
            cloudBaseCameraParam.setServerIp(CommonRecord.getInstance().getIp());
            cloudBaseCameraParam.setServerPort(CommonRecord.getInstance().getPort());

            cloudBaseCameraParam.setRoute(false);
            cloudBaseCameraParam.setUserId("");
            cloudBaseCameraParam.setDomainId("");
            cloudBaseCameraParam.setRegionId("");
            cloudBaseCameraParam.setLocation("");
//            cloudBaseCameraParam.setUseHttps(EnvironmentHelper.getInstance().isUseHttps() ? 1 : 0);
            CloudBaseRTCamera camera = new CloudBaseRTCamera(cloudBaseCameraParam);
            return camera;
        } else if(BuildConfig.isExpress) {
            ExpressRTCameraParam expressRTCameraParam = new ExpressRTCameraParam();
            expressRTCameraParam.setCameraID(channelInfo.getChnSncode());
            int mStreamType = ChannelInfo.ChannelStreamType.getValue(channelInfo.getStreamType());
            if(mStreamType > Stream_Assist_Type) mStreamType = Stream_Assist_Type;
            expressRTCameraParam.setStreamType(mStreamType);
            UserInfo mUserInfo = CommonRecord.getInstance().getUserInfo();
            expressRTCameraParam.setDpRestToken((String)mUserInfo.getExtandAttributeValue("token"));
            expressRTCameraParam.setServerIP(CommonRecord.getInstance().getIp());
            expressRTCameraParam.setServerPort(CommonRecord.getInstance().getPort());

            if(!TextUtils.isEmpty(encryptKey)) {
                expressRTCameraParam.setEncrypt(true);
                expressRTCameraParam.setPsk(encryptKey);
            }

            expressRTCameraParam.setLocation("");
            expressRTCameraParam.setUseHttps(EnvironmentHelper.getInstance().isUseHttps() ? 1 : 0);

            ExpressRTCamera camera = new ExpressRTCamera(expressRTCameraParam);
            return camera;
        } else {
            DPSRTCameraParam dpsrtCameraParam = new DPSRTCameraParam();
            dpsrtCameraParam.setCameraID(channelInfo.getChnSncode());
            try {
                dpsrtCameraParam.setDpHandle(String.valueOf(dataAdapterInterface.getDPSDKEntityHandle()));
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            RealInfo realInfo = new RealInfo();
            int mStreamType = ChannelInfo.ChannelStreamType.getValue(channelInfo.getStreamType());
            if(mStreamType > Stream_Assist_Type) mStreamType = Stream_Assist_Type;
            realInfo.setStreamType(mStreamType);
            realInfo.setMediaType(3);
            realInfo.setTrackID("601");
            realInfo.setStartChannelIndex(0);
            realInfo.setSeparateNum("1");
            realInfo.setCheckPermission(true);
//            dpsrtCameraParam.setEncrypt();
//            dpsrtCameraParam.setPsk();
            dpsrtCameraParam.setRealInfo(realInfo);
            DPSRTCamera camera = new DPSRTCamera(dpsrtCameraParam);
            return camera;
        }
    }

    private void startPlay(int winIndex){
        mPlayManager.playSingle(winIndex);
    }

    private void replay(){
        mPlayManager.playCurpage();
    }

    private void stopPlay(int winIndex){
        mPlayManager.stopSingle(winIndex);
        tvPlay.setText(R.string.play_play);
    }

    private void stopAll(){
        mPlayManager.stopAll();
    }


    private ITalkListener iTalkListener = new ITalkListener() {
        @Override
        public void onTalkResult(int winIndex, TalkResultType talkResultType, String code, int type) {
            if(talkResultType == TalkResultType.eTalkSuccess){
                if(mPlayOnlineHander != null) mPlayOnlineHander.sendEmptyMessage(KEY_Handler_Talk_Success);
            }else if(talkResultType == TalkResultType.eTalkFailed){
                if(mPlayOnlineHander != null) mPlayOnlineHander.sendEmptyMessage(KEY_Handler_Talk_failed);
            }
        }
    };

    private IMediaPlayListener iMediaPlayListener = new IMediaPlayListener() {
        @Override
        public void onPlayeStatusCallback(int winIndex, PlayStatusType type, int code) {
            Log.d(TAG, "onPlayeStatusCallback:" + type + " winIndex: " + winIndex);
            Message msg = Message.obtain();
            msg.obj = winIndex;
            if(type == PlayStatusType.eStreamPlayed){
                msg.what = KEY_Handler_Stream_Played;
                if(mPlayOnlineHander != null) mPlayOnlineHander.sendMessage(msg);
            }else if(type == PlayStatusType.ePlayFirstFrame){
                msg.what = KEY_Handler_First_Frame;
                if(mPlayOnlineHander != null) mPlayOnlineHander.sendMessage(msg);
            }else if(type == PlayStatusType.eNetworkaAbort){
                msg.what = KEY_Handler_Net_Error;
                if(mPlayOnlineHander != null) mPlayOnlineHander.sendMessage(msg);
            }else if(type == PlayStatusType.ePlayFailed){
                msg.what = KEY_Handler_Play_Failed;
                if(mPlayOnlineHander != null) mPlayOnlineHander.sendMessage(msg);
            }
        }
    };

    private IOperationListener iOperationListener = new IOperationListener() {
        @Override
        public void onWindowSelected(int position) {
            Log.d(TAG, "onWindowSelected" + position);
            refreshBtnState();
        }

        @Override
        public void onPageChange(int newPage, int prePage, int type) {
            Log.d(TAG, "onPageChange" + newPage + prePage + type);
            if(type == 0){
                if(mPlayManager.getPageCellNumber() == 1){
                    mPlayManager.setEZoomEnable(prePage, false);
                    mPlayManager.setEZoomEnable(newPage, true);
                }
                refreshBtnState();
            }
        }

        @Override
        public void onSplitNumber(int nCurCellNumber, int nCurPage, int nPreCellNumber, int nPrePage) {
            Log.d(TAG, "onSplitNumber" + nCurCellNumber);
        }

        @Override
        public void onControlClick(int nWinIndex, ControlType type) {
            Log.d(TAG, "onControlClick" + type);
            if (type == ControlType.Control_Open) {
                //add channel
            }else if(type == ControlType.Control_Reflash){
                startPlay(nWinIndex);
            }
        }

        @Override
        public void onSelectWinIndexChange(int newWinIndex, int oldWinIndex) {
            Log.d(TAG, "onSelectWinIndexChange:" + newWinIndex + ":" + oldWinIndex);
            if(!mPlayManager.hasTalking()) {
                if(mPlayManager.isOpenAudio(oldWinIndex)){
                    mPlayManager.closeAudio(oldWinIndex);
                    mPlayManager.setNeedOpenAudio(oldWinIndex, true);
                }

                if(mPlayManager.isPlaying(newWinIndex) && mPlayManager.isNeedOpenAudio(newWinIndex)) mPlayManager.openAudio(newWinIndex);
                refreshBtnState();
            }
        }

        @Override
        public void onWindowDBClick(int winIndex, int type) {
            Log.d(TAG, "onWindowDBClick" + type + " winIndex:" + winIndex + " isWindowMax:" + mPlayManager.isWindowMax(winIndex));
            if(mPlayManager.isOpenPTZ(winIndex)){
                if(mPlayManager.setPTZEnable(winIndex, false) == Err.OK){
                    tvCloud.setSelected(false);
                    mPlayManager.setResumeFlag(winIndex, false);
                }
            }
            mPlayManager.setEZoomEnable(winIndex, type == 0);
        }

        @Override
        public void onMoveWindowBegin(int winIndex) {
            Log.d(TAG, "onMoveWindowBegin");
        }

        @Override
        public void onMovingWindow(int winIndex, float x, float y) {
            Log.d(TAG, "onMovingWindow x:" + x + " y:" + y);
        }

        @Override
        public boolean onMoveWindowEnd(int winIndex, float x, float y) {
            Log.d(TAG, "onMoveWindowEnd x:" + x + " y:" + y);
            return false;
        }
    };

    private IPTZListener iptzListener = new IPTZListener() {
        @Override
        public void onPTZControl(int winIndex, PtzOperation oprType, boolean isStop, boolean isLongPress) {
            Log.d(TAG, "onPTZControl oprType:" + oprType.toString());
            sendPTZOperation(getPtzOperation(oprType), isStop);
        }

        @Override
        public void onPTZZooming(int winIndex, float scale, PtzOperation oprType, PtzZoomState state) {
            Log.d(TAG, "onPTZZooming oprType:" + oprType.toString()
                    + " state:" + state.toString() + " scale:" + scale);
        }
    };

    private void sendPTZOperation(final ChannelInfo.PtzOperation operation, final boolean isStop){
        if(operation == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataAdapterInterface.operatePTZ(operation, channelInfoList.get(mPlayManager.getSelectedWindowIndex()).getUuid(), 4, isStop);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static ChannelInfo.PtzOperation getPtzOperation(PtzOperation oprType){
        ChannelInfo.PtzOperation operation              = ChannelInfo.PtzOperation.stop;
        if(oprType == PtzOperation.up)        operation = ChannelInfo.PtzOperation.up;
        if(oprType == PtzOperation.down)      operation = ChannelInfo.PtzOperation.down;
        if(oprType == PtzOperation.left)      operation = ChannelInfo.PtzOperation.left;
        if(oprType == PtzOperation.right)     operation = ChannelInfo.PtzOperation.right;
        if(oprType == PtzOperation.leftUp)    operation = ChannelInfo.PtzOperation.leftUp;
        if(oprType == PtzOperation.leftDown)  operation = ChannelInfo.PtzOperation.leftDown;
        if(oprType == PtzOperation.rightUp)   operation = ChannelInfo.PtzOperation.rightUp;
        if(oprType == PtzOperation.rightDown) operation = ChannelInfo.PtzOperation.rightDown;
        return operation;
                            // 若想用摄像头的放大和缩小，operation可以按如下设置：
                            // operation = ChannelInfo.PtzOperation.zoomAdd; 放大
                            // operation = ChannelInfo.PtzOperation.zoomReduce;缩小
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture:
                onClickCapture();
                break;
            case R.id.record:
                onClickRecord();
                break;
            case R.id.talk:
                onClickTalk();
                break;
            case R.id.sound:
                onClickSound();
                break;
            case R.id.play:
                int winIndex = mPlayManager.getSelectedWindowIndex();
                if(mPlayManager.isPlaying(winIndex)) {
                    stopPlay(winIndex);
                } else if(mPlayManager.getWindowChannelInfo(winIndex) != null){
                    startPlay(winIndex);
                }
                break;
            case R.id.cloud:
                onClickCloud();
                break;
            case R.id.stream_main:
                changeModeStream(Stream_Main_Type);
                break;
            case R.id.stream_assist:
                changeModeStream(Stream_Assist_Type);
                break;
            case R.id.stream_third:
                changeModeStream(Stream_Third_Type);
                break;
            case R.id.remove:
                removePlay(mPlayManager.getSelectedWindowIndex());
                break;
            case R.id.full:
                isFull = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                rlTitle.setVisibility(View.GONE);
                llControlLayout.setVisibility(View.GONE);
                initCommonWindow();
                setFullScreen(this);
                break;
        }
    }

    private void onClickCapture(){
        if(!mPlayManager.isStreamPlayed(mPlayManager.getSelectedWindowIndex())) return;
        int currentWindowIndex = mPlayManager.getSelectedWindowIndex();
        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getPath() + "/Pictures/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
        int ret = mPlayManager.snapShot(currentWindowIndex, path, true);
        if (ret == Err.OK) {
            toast(getText(R.string.play_capture_success) + path);
            MediaScannerConnection.scanFile(this, new String[]{path}, null, null);
        } else {
            toast(R.string.play_capture_failed);
        }
    }

    private void onClickRecord(){
        if(mPlayManager.isRecording(mPlayManager.getSelectedWindowIndex())){
            stopRecord();
        }else{
            if(mPlayManager.hasRecording()){
                toast(R.string.play_back_recording);
            }
            startRecord();
        }
    }

    private void startRecord(){
        if(!mPlayManager.isStreamPlayed(mPlayManager.getSelectedWindowIndex())) return;
        int currentWindowIndex = mPlayManager.getSelectedWindowIndex();
        recordPath = new String[2];
        recordPath[0] = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getPath() + "/Records/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".dav";
        recordPath[1] = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getPath() + "/Pictures/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
        FileStorageUtil.createFilePath(null, recordPath[0]);
        FileStorageUtil.createFilePath(null, recordPath[1]);
        int ret = mPlayManager.startRecord(currentWindowIndex, recordPath, RecordType.RECORDER_TYPE_DAV);
        if(ret == Err.OK){
            toast(R.string.play_record_start);
            MediaScannerConnection.scanFile(this, recordPath, null, null);
            tvRecord.setSelected(true);
        }
    }

    protected void stopRecord(){
        int ret = mPlayManager.stopRecord(mPlayManager.getSelectedWindowIndex());
        if(ret == Err.OK){
            toast(getText(R.string.play_record_stop) + recordPath[0]);
            MediaScannerConnection.scanFile(this, recordPath, null, null);
            tvRecord.setSelected(false);
        }
    }

    private void onClickSound(){
        int currentWindowIndex = mPlayManager.getSelectedWindowIndex();
        if(!mPlayManager.isPlaying(currentWindowIndex)) return;
        tvTalk.setSelected(false);
        if(mPlayManager.isOpenAudio(currentWindowIndex) && closeAudio(currentWindowIndex)){
            tvSound.setSelected(false);
        }else {
            if(mPlayManager.hasTalking()) toast(R.string.play_talk_close);
            if(openAudio(currentWindowIndex)){
                tvSound.setSelected(true);
            }
        }
    }

    public boolean openAudio(int winIndex) {
        // TODO:check state
        return mPlayManager.openAudio(winIndex) == Err.OK;
    }

    public boolean closeAudio(int winIndex) {
        // TODO:check state
        return mPlayManager.closeAudio(winIndex) == Err.OK;
    }

    private void onClickTalk(){
        int winIndex = mPlayManager.getSelectedWindowIndex();
        if(!mPlayManager.isPlaying(winIndex)) return;
        tvSound.setSelected(false);
        if(mPlayManager.isTalking(winIndex)){
            closeTalk(winIndex);
        }else{
            if(!MicHelper.isVoicePermission()) {
                toast(R.string.play_talk_no_permission);
                return;
            }
            openTalk(winIndex);
        }
    }

    private void openTalk(int winIndex){
        if(!mPlayManager.isStreamPlayed(mPlayManager.getSelectedWindowIndex())) return;
        if(BuildConfig.isVCloud) {
            CloudBaseTalkParam cloudBaseTalkParam = new CloudBaseTalkParam();
            cloudBaseTalkParam.setCameraID(channelInfoMap.get(mPlayManager.getSelectedWindowIndex()).getDeviceUuid());
            UserInfo mUserInfo = CommonRecord.getInstance().getUserInfo();
            cloudBaseTalkParam.setDpRestToken((String)mUserInfo.getExtandAttributeValue("token"));
            cloudBaseTalkParam.setServerIp(CommonRecord.getInstance().getIp());
            cloudBaseTalkParam.setServerPort(CommonRecord.getInstance().getPort());
            cloudBaseTalkParam.setTalkType(1);
            cloudBaseTalkParam.setTransMode(1);
            cloudBaseTalkParam.setSampleRate(TalkParam.AUDIO_SAMPLE_RATE_8000);
            cloudBaseTalkParam.setSampleDepth(TalkParam.AUDIO_SAMPLE_DEPTH_16);
            cloudBaseTalkParam.setEncodeType(TalkParam.AUDIO_ENCODE_G711A);
            cloudBaseTalkParam.setUseHttps(EnvironmentHelper.getInstance().isUseHttps() ? 1 : 0);
            cloudBaseTalkParam.setRoute(false);
            cloudBaseTalkParam.setUserId("");
            cloudBaseTalkParam.setDomainId("");
            cloudBaseTalkParam.setRegionId("");

            CloudBaseTalk talk = new CloudBaseTalk(cloudBaseTalkParam);
            mPlayManager.startTalk(winIndex, talk);
        } else if(BuildConfig.isExpress) {
            ExpressTalkParam expressTalkParam = new ExpressTalkParam();
            expressTalkParam.setCameraID(channelInfoMap.get(mPlayManager.getSelectedWindowIndex()).getDeviceUuid());
            UserInfo mUserInfo = CommonRecord.getInstance().getUserInfo();
            expressTalkParam.setDpRestToken((String)mUserInfo.getExtandAttributeValue("token"));
            expressTalkParam.setServerIp(CommonRecord.getInstance().getIp());
            expressTalkParam.setServerPort(CommonRecord.getInstance().getPort());
            expressTalkParam.setTalkType(1);
            expressTalkParam.setTransMode(1);
            expressTalkParam.setSampleRate(TalkParam.AUDIO_SAMPLE_RATE_8000);
            expressTalkParam.setSampleDepth(TalkParam.AUDIO_SAMPLE_DEPTH_16);
            expressTalkParam.setEncodeType(TalkParam.AUDIO_ENCODE_G711A);

            ExpressTalk talk = new ExpressTalk(expressTalkParam);
            mPlayManager.startTalk(winIndex, talk);
        } else {
            DPSTalkParam dpsTalkParam = new DPSTalkParam();
            dpsTalkParam.setCameraID(channelInfoMap.get(mPlayManager.getSelectedWindowIndex()).getDeviceUuid());
            try {
                dpsTalkParam.setDpHandle(String.valueOf(dataAdapterInterface.getDPSDKEntityHandle()));
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            dpsTalkParam.setEncodeType(TalkParam.AUDIO_ENCODE_G711A);
            dpsTalkParam.setSampleDepth(TalkParam.AUDIO_SAMPLE_DEPTH_16);
            dpsTalkParam.setSampleRate(TalkParam.AUDIO_SAMPLE_RATE_8000);
            dpsTalkParam.setTalkType(1);
            dpsTalkParam.setTransMode(1);
            DPSTalk talk = new DPSTalk(dpsTalkParam);
            mPlayManager.startTalk(winIndex, talk);
        }
        showProgressDialog();
    }

    private void closeTalk(int winIndex){
        if(mPlayManager.stopTalk(winIndex) == Err.OK){
            toast(R.string.play_talk_close);
            tvTalk.setSelected(false);
        }
    }

    private void onClickCloud(){
        if(!mPlayManager.isStreamPlayed(mPlayManager.getSelectedWindowIndex())) return;
        int windowIndex = mPlayManager.getSelectedWindowIndex();
        if(!mPlayManager.isPlaying(windowIndex)) return;
        if(mPlayManager.isOpenPTZ(windowIndex)){
            if(mPlayManager.setPTZEnable(windowIndex, false) == Err.OK){
                tvCloud.setSelected(false);
                if(mPlayManager.isResumeFlag(windowIndex)){
                    mPlayManager.setResumeFlag(windowIndex, false);
                    mPlayManager.resumeWindow(windowIndex);
                }
            }
        }else{
            if(mPlayManager.setPTZEnable(windowIndex, true) == Err.OK){
                tvCloud.setSelected(true);
                if(!mPlayManager.isWindowMax(windowIndex)){
                    mPlayManager.setResumeFlag(windowIndex, true);
                    mPlayManager.maximizeWindow(windowIndex);
                }
            }
        }
    }

    private void changeModeStream(int streamType){
        if(mPlayManager.isOpenPTZ(mPlayManager.getSelectedWindowIndex())) {
            tvCloud.setSelected(false);
        }
        if(mPlayManager.hasRecording()) {
            toast(R.string.play_record_stop_tips);
            tvRecord.setSelected(false);
        }
        int index = mPlayManager.getSelectedWindowIndex();
        mPlayManager.stopSingle(index);

        if(mPlayManager.getWindowChannelInfo(index) != null) {
            if(BuildConfig.isVCloud) {
                CloudBaseRTCamera cloudBaseRTCamera = (CloudBaseRTCamera) mPlayManager.getWindowChannelInfo(index).getCameraParam();
                CloudBaseRTCameraParam cloudBaseCameraParam = new CloudBaseRTCameraParam();
                cloudBaseCameraParam.setCameraID(cloudBaseRTCamera.getCameraParam().getCameraID());
                cloudBaseCameraParam.setStreamType(streamType - 1);

                UserInfo mUserInfo = CommonRecord.getInstance().getUserInfo();
                cloudBaseCameraParam.setDpRestToken((String)mUserInfo.getExtandAttributeValue("token"));
                cloudBaseCameraParam.setServerIp(CommonRecord.getInstance().getIp());
                cloudBaseCameraParam.setServerPort(CommonRecord.getInstance().getPort());

                cloudBaseCameraParam.setRoute(false);
                cloudBaseCameraParam.setUserId("");
                cloudBaseCameraParam.setDomainId("");
                cloudBaseCameraParam.setRegionId("");
                cloudBaseCameraParam.setLocation("");

                cloudBaseCameraParam.setUseHttps(EnvironmentHelper.getInstance().isUseHttps() ? 1 : 0);

                CloudBaseRTCamera camera = new CloudBaseRTCamera(cloudBaseCameraParam);

                mPlayManager.addCamera(index, camera);
                mPlayManager.playSingle(index);
            } else if(BuildConfig.isExpress) {
                ExpressRTCamera expressRTCamera = (ExpressRTCamera) mPlayManager.getWindowChannelInfo(index).getCameraParam();
                ExpressRTCameraParam expressRTCameraParam = new ExpressRTCameraParam();
                expressRTCameraParam.setCameraID(expressRTCamera.getCameraParam().getCameraID());
                expressRTCameraParam.setStreamType(streamType);
                UserInfo mUserInfo = CommonRecord.getInstance().getUserInfo();
                expressRTCameraParam.setDpRestToken((String)mUserInfo.getExtandAttributeValue("token"));
                expressRTCameraParam.setServerIP(CommonRecord.getInstance().getIp());
                expressRTCameraParam.setServerPort(CommonRecord.getInstance().getPort());

                if(!TextUtils.isEmpty("")) {
                    expressRTCameraParam.setEncrypt(true);
                    expressRTCameraParam.setPsk("");
                }

                expressRTCameraParam.setLocation("");
                expressRTCameraParam.setUseHttps(EnvironmentHelper.getInstance().isUseHttps() ? 1 : 0);

                ExpressRTCamera camera = new ExpressRTCamera(expressRTCameraParam);
                mPlayManager.addCamera(index, camera);
                mPlayManager.playSingle(index);
            } else {
                DPSRTCamera dpsrtCamera = (DPSRTCamera) mPlayManager.getWindowChannelInfo(index).getCameraParam();
                DPSRTCameraParam dpsrtCameraParam = new DPSRTCameraParam();
                dpsrtCameraParam.setCameraID(dpsrtCamera.getCameraParam().getCameraID());
                try {
                    dpsrtCameraParam.setDpHandle(String.valueOf(dataAdapterInterface.getDPSDKEntityHandle()));
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                RealInfo realInfo = new RealInfo();
                realInfo.setStreamType(streamType);
                realInfo.setMediaType(3);
                realInfo.setTrackID("601");
                realInfo.setStartChannelIndex(0);
                realInfo.setSeparateNum("1");
                realInfo.setCheckPermission(true);
//            dpsrtCameraParam.setEncrypt();
//            dpsrtCameraParam.setPsk();
                dpsrtCameraParam.setRealInfo(realInfo);

                DPSRTCamera camera = new DPSRTCamera(dpsrtCameraParam);
                mPlayManager.addCamera(index, camera);
                mPlayManager.playSingle(index);
            }

        }
    }

    private void removePlay(int winIndex){
        if(channelInfoMap.containsKey(winIndex)) {
            channelInfoMap.remove(winIndex);
            channelInfoList.remove(winIndex);
            mPlayManager.removeCamera(winIndex);
            refreshBtnState();
        }
    }

    private void refreshBtnState() {
        int winIndex = mPlayManager.getSelectedWindowIndex();
        tvRecord.setSelected(mPlayManager.isRecording(winIndex));
        tvTalk.setSelected(mPlayManager.isTalking(winIndex));
        tvCloud.setSelected(mPlayManager.isOpenPTZ(winIndex));
        tvPlay.setText(mPlayManager.isPlaying(winIndex) ? R.string.play_stop : R.string.play_play);
        tvSound.setSelected(mPlayManager.isOpenAudio(winIndex));
        if(channelInfoMap.containsKey(winIndex)) {
            ChannelInfo channelInfo = channelInfoMap.get(winIndex);
            setSupportStreamTag(ChannelInfo.ChannelStreamType.getValue(channelInfo.getStreamType()));
        } else {
            setSupportStreamTag(-1);
        }
        if(mPlayManager.getWindowChannelInfo(winIndex) == null
                || mPlayManager.getWindowChannelInfo(winIndex).getCameraParam() == null
                || (mPlayManager.getWindowChannelInfo(winIndex).getCameraParam() instanceof CloudRTCamera && ((CloudBaseRTCamera) mPlayManager.getWindowChannelInfo(winIndex).getCameraParam()).getCameraParam() == null)
                || (mPlayManager.getWindowChannelInfo(winIndex).getCameraParam() instanceof ExpressRTCamera && ((ExpressRTCamera) mPlayManager.getWindowChannelInfo(winIndex).getCameraParam()).getCameraParam() == null)
                || (mPlayManager.getWindowChannelInfo(winIndex).getCameraParam() instanceof DPSRTCamera && ((DPSRTCamera) mPlayManager.getWindowChannelInfo(winIndex).getCameraParam()).getCameraParam() == null)) {
            tvStreamMain.setSelected(false);
            tvStreamAssist.setSelected(false);
            tvStreamThird.setSelected(false);
            return;
        }

        int streamType = -1;
        if(BuildConfig.isVCloud) {
            streamType = ((CloudBaseRTCamera) mPlayManager.getWindowChannelInfo(winIndex).getCameraParam()).getCameraParam().getStreamType() + 1;
        } else if(BuildConfig.isExpress) {
            streamType = ((ExpressRTCamera) mPlayManager.getWindowChannelInfo(winIndex).getCameraParam()).getCameraParam().getStreamType();
        } else {
            streamType = ((DPSRTCamera) mPlayManager.getWindowChannelInfo(winIndex).getCameraParam()).getCameraParam().getRealInfo().getStreamType();
        }

        switch (streamType) {
            case Stream_Main_Type:
                tvStreamMain.setSelected(true);
                tvStreamAssist.setSelected(false);
                tvStreamThird.setSelected(false);
                break;
            case Stream_Assist_Type:
                tvStreamMain.setSelected(false);
                tvStreamAssist.setSelected(true);
                tvStreamThird.setSelected(false);
                break;
            case Stream_Third_Type:
                tvStreamMain.setSelected(false);
                tvStreamAssist.setSelected(false);
                tvStreamThird.setSelected(true);
                break;
        }
    }

    public void setSupportStreamTag(int supportStreamTag){
        switch (supportStreamTag) {
            case Stream_Main_Type:
                tvStreamMain.setEnabled(true);
                tvStreamAssist.setEnabled(false);
                tvStreamThird.setEnabled(false);
                break;
            case Stream_Assist_Type:
                tvStreamMain.setEnabled(true);
                tvStreamAssist.setEnabled(true);
                tvStreamThird.setEnabled(false);
                break;
            case Stream_Third_Type:
                tvStreamMain.setEnabled(true);
                tvStreamAssist.setEnabled(true);
                tvStreamThird.setEnabled(true);
                break;

            default:
                tvStreamMain.setEnabled(false);
                tvStreamAssist.setEnabled(false);
                tvStreamThird.setEnabled(false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(isFull) {
            isFull = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            rlTitle.setVisibility(View.VISIBLE);
            llControlLayout.setVisibility(View.VISIBLE);
            initCommonWindow();
            quitFullScreen(this);
        } else {
            this.finish();
        }
    }

    public static void setFullScreen(Activity activity) {
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    public static void quitFullScreen(Activity activity) {
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected Context mContext;
    private Toast mToast;
    private ProgressDialog mProgressDialog;

    protected void showProgressDialog() {
        if(mProgressDialog != null && !mProgressDialog.isShowing()){
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.dss_common_request_layout);
        }
    }

    protected void dissmissProgressDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    protected void toast(int res) {
        if (mToast == null){
            mToast = Toast.makeText(mContext, res, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(res);
        }
        mToast.show();
    }

    protected void toast(String str) {
        if (mToast == null){
            mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(str);
        }
        mToast.show();
    }
}
