package com.mm.dss.demo;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.business.adapter.DataAdapterImpl;
import com.android.business.adapter.DataAdapterInterface;
import com.android.business.entity.ChannelInfo;
import com.android.business.entity.RecordInfo;
import com.android.business.entity.UserInfo;
import com.android.business.exception.BusinessException;
import com.android.dahua.dhcommon.utils.TimeDataHelper;
import com.android.dahua.dhplaycomponent.IMediaPlayListener;
import com.android.dahua.dhplaycomponent.IOperationListener;
import com.android.dahua.dhplaycomponent.PlayManagerProxy;
import com.android.dahua.dhplaycomponent.camera.PBCamera.CloudBasePBCamera;
import com.android.dahua.dhplaycomponent.camera.PBCamera.CloudBasePBParam;
import com.android.dahua.dhplaycomponent.camera.PBCamera.DPSPBCamera;
import com.android.dahua.dhplaycomponent.camera.PBCamera.DPSPBCameraParam;
import com.android.dahua.dhplaycomponent.camera.PBCamera.ExpressPBCameraParam;
import com.android.dahua.dhplaycomponent.camera.PBCamera.ExpressPbCamera;
import com.android.dahua.dhplaycomponent.camera.inner.DPSRecordFile;
import com.android.dahua.dhplaycomponent.camera.inner.PlayBackInfo;
import com.android.dahua.dhplaycomponent.common.Err;
import com.android.dahua.dhplaycomponent.common.MutipleType;
import com.android.dahua.dhplaycomponent.common.PlayStatusType;
import com.android.dahua.dhplaycomponent.common.RecordType;
import com.android.dahua.dhplaycomponent.windowcomponent.entity.ControlType;
import com.android.dahua.dhplaycomponent.windowcomponent.window.PlayWindow;
import com.mm.dss.demo.base.BaseActivity;
import com.mm.dss.demo.base.CommonRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gov.android.com.superior.BuildConfig;
import gov.android.com.superior.R;

/**
 * Created by 26499 on 2017/11/16.
 */

public class PlayBackActivity extends BaseActivity implements View.OnClickListener{
    public static final int KEY_Handler_Stream_Start_Request = 0;
    public static final int KEY_Handler_Stream_Played = 1;
    public static final int KEY_Handler_First_Frame = 2;
    public static final int KEY_Handler_Net_Error = 3;
    public static final int KEY_Handler_Play_Failed = 4;
    public static final int KEY_Handler_Play_End = 5;
    public static final int KEY_Handler_Bad_File = 6;
    public static final int KEY_Handler_Seek_Success = 7;
    public static final int KEY_Handler_Seek_Cross_Border = 8;
    public static final int KEY_Handler_Seek_failed = 9;
    public static final int KEY_Handler_Play_Unknow = 15;
    private List<ChannelInfo> channelInfoList;
    private PlayWindow mPlayWin;
    private SeekBar mSeekBar;
    protected PlayManagerProxy mPlayManager;
    private DataAdapterInterface dataAdapterInterface;
    protected String[] recordPath;
    private List<RecordInfo> recordInfos = null;
    private List<DPSRecordFile> dpsRecordFiles;
    private RecordInfo.RecordResource recordResource = RecordInfo.RecordResource.Platform;
    private int Mutiple = 3;
    private long mRecordStartTime = -1;
    private long mRecordEndTime = -1;
    private Calendar mCalendar;
    private int mCurrentDay;
    private int mCurrentMonth;
    private int mCurrentYear;
    private DataListAdapter dataListAdapter;

    private TextView tvCapture;
    private TextView tvRecord;
    private TextView tvSound;
    private TextView tvPlay;
    private TextView tvPause;
    private TextView tvMutipleAdd;
    private TextView tvMutipleReduce;
    private TextView tvPreMonth;
    private TextView tvNextMonth;
    private TextView tvDataTime;
    private TextView tvCenter;
    private TextView tvDevice;
    private RecyclerView rvDataList;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            dissmissProgressDialog();
            switch (msg.what) {
                case 0:
                    if(recordInfos != null && recordInfos.size() > 0) {
                        if(recordResource == RecordInfo.RecordResource.Device) {
                            mRecordStartTime = recordInfos.get(mPlayManager.getSelectedWindowIndex()).getStartTime();
                            mRecordEndTime = recordInfos.get(recordInfos.size() - 1).getEndTime();
                        } else {
                            recordToDpsRecord();
                        }

                        startPlayBack();
                    }
                    break;
                case 1:
                    boolean[] maskArray = (boolean[]) msg.obj;
                    if(dataListAdapter != null && maskArray.length > 1) {
                        dataListAdapter.setDataSet(maskArray, mCalendar);
                        dataListAdapter.notifyDataSetChanged();
                    } else {

                    }
                    break;
                case 2:
                    long time = (long) msg.obj;
                    mSeekBar.setProgress((int)time);
                    break;
            }
        }
    };

    protected Handler mPlayBackHander = new Handler() {
        public void handleMessage(Message msg) {
            dissmissProgressDialog();
            switch (msg.what) {
                case KEY_Handler_Stream_Start_Request:
                    break;
                case KEY_Handler_Stream_Played:
                    openAudio(mPlayManager.getSelectedWindowIndex());
                    break;
                case KEY_Handler_First_Frame:
                    break;
                case KEY_Handler_Net_Error:
                    toast(R.string.play_net_error);
                    stopPlay(mPlayManager.getSelectedWindowIndex());
                    break;
                case KEY_Handler_Play_Failed:
                case KEY_Handler_Bad_File:
                case KEY_Handler_Play_Unknow:
                    stopPlay(mPlayManager.getSelectedWindowIndex());
                    break;
                case KEY_Handler_Play_End:
                    refreshBtnState();
                    break;
                case KEY_Handler_Seek_Success:
                    break;
                case KEY_Handler_Seek_failed:
                    break;
                case KEY_Handler_Seek_Cross_Border:
                    toast(R.string.play_back_seek_cross_border);
                    stopPlay(mPlayManager.getSelectedWindowIndex());
                    break;
            }
            refreshBtnState();
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dss_activity_play_back);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlay(mPlayManager.getSelectedWindowIndex());
    }

    private void initView() {
        mPlayWin = (PlayWindow) findViewById(R.id.play_window);
        mSeekBar = (SeekBar) findViewById(R.id.progressbar);
        tvCapture = (TextView) findViewById(R.id.capture);
        tvRecord = (TextView) findViewById(R.id.record);
        tvPlay = (TextView) findViewById(R.id.play);
        tvSound = (TextView) findViewById(R.id.sound);
        tvPause = (TextView) findViewById(R.id.pause);
        tvMutipleAdd = (TextView) findViewById(R.id.speedAdd);
        tvMutipleReduce = (TextView) findViewById(R.id.speedReduce);
        tvPreMonth = (TextView) findViewById(R.id.preMonth);
        tvNextMonth = (TextView) findViewById(R.id.nextMonth);
        tvDataTime = (TextView) findViewById(R.id.dataTime);
        tvCenter = (TextView) findViewById(R.id.center);
        tvDevice = (TextView) findViewById(R.id.device);
        rvDataList = (RecyclerView) findViewById(R.id.data_list);

        tvCapture.setOnClickListener(this);
        tvRecord.setOnClickListener(this);
        tvSound.setOnClickListener(this);
        tvPlay.setOnClickListener(this);
        tvPause.setOnClickListener(this);
        tvMutipleAdd.setOnClickListener(this);
        tvMutipleReduce.setOnClickListener(this);
        tvPreMonth.setOnClickListener(this);
        tvNextMonth.setOnClickListener(this);
        tvCenter.setOnClickListener(this);
        tvDevice.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvDataList.setLayoutManager(linearLayoutManager);
        initCommonWindow();
    }

    private void initData() {
        channelInfoList = (List<ChannelInfo>) getIntent().getSerializableExtra("channel_info_list");
        dataAdapterInterface = DataAdapterImpl.getInstance();
        mPlayManager = new PlayManagerProxy();
        mPlayManager.init(this, 1, 1, mPlayWin);

        mSeekBar.setMax(3600 * 24);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("2649999", "onProgressChanged:" + progress);
                if (fromUser) {
                    mPlayManager.seekByTime(mPlayManager.getSelectedWindowIndex(), progress + TimeDataHelper.getStartTimeByDay(mCalendar) / 1000L);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mCalendar = Calendar.getInstance();
        mCurrentYear = mCalendar.get(Calendar.YEAR);
        mCurrentMonth = mCalendar.get(Calendar.MONTH) + 1;
        mCurrentDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        dataListAdapter = new DataListAdapter(this);
        dataListAdapter.setOnItemClickLinstener(new DataListAdapter.OnItemClickLinstener() {
            @Override
            public void onItemClick(int position) {
                mCalendar.set(Calendar.DAY_OF_MONTH, position + 1);
                long startTime = TimeDataHelper.getStartTimeByDay(mCalendar) / 1000L;
                long endTime = TimeDataHelper.getEndTimeByDay(mCalendar) / 1000L;
                queryRecord(startTime, endTime);
            }
        });
        rvDataList.setAdapter(dataListAdapter);

        mPlayManager.setOnOperationListener(iOperationListener);
        mPlayManager.setOnMediaPlayListener(iMediaPlayListener);

        tvCenter.setSelected(true);
        tvDevice.setSelected(false);
        getRecordMask();
    }

    /**
     * 初始化视频窗口
     *
     */
    public void initCommonWindow() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        int mScreenWidth = metric.widthPixels; // 屏幕宽度（像素） // screen width (pixels)
        int mScreenHeight = metric.heightPixels; // 屏幕高度（像素） // screen height (pixel)
        mScreenHeight = mScreenWidth * 3 / 4;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mPlayWin.getLayoutParams();
        lp.width = mScreenWidth;
        lp.height = mScreenHeight;
        mPlayWin.setLayoutParams(lp);
        mPlayWin.forceLayout(mScreenWidth, mScreenHeight);
    }

    private void queryRecord(final long beginTime, final long endTime) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    recordInfos = dataAdapterInterface.queryRecord(channelInfoList.get(mPlayManager.getSelectedWindowIndex()).getChnSncode(),
                            recordResource,
                            RecordInfo.RecordEventType.All,
                            beginTime, endTime,
                            RecordInfo.StreamType.All_Type);

                    mHandler.sendEmptyMessage(0);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPlayBack(){
        if(recordResource == RecordInfo.RecordResource.Device &&
                (channelInfoList.size() == 0 || mRecordStartTime == -1 || mRecordEndTime == -1)) return;
        if(recordResource == RecordInfo.RecordResource.Platform && (dpsRecordFiles == null || dpsRecordFiles.size() == 0)) return;
        if(mPlayManager != null && mPlayManager.isPlaying(mPlayManager.getSelectedWindowIndex())){
            stopPlay(mPlayManager.getSelectedWindowIndex());
        }

        if(BuildConfig.isVCloud) {
            CloudBasePBParam cloudBasePBParam = new CloudBasePBParam();
            String chnSnCode = null;
            if(mPlayManager != null) {
                chnSnCode = channelInfoList.get(mPlayManager.getSelectedWindowIndex()).getChnSncode();
                cloudBasePBParam.setNeedBeginTime((int)(mPlayManager.getCurrentProgress(mPlayManager.getSelectedWindowIndex())));
            }
            cloudBasePBParam.setCameraID(chnSnCode);
            cloudBasePBParam.setStreamType(0);

            UserInfo mUserInfo = CommonRecord.getInstance().getUserInfo();
            cloudBasePBParam.setDpRestToken((String)mUserInfo.getExtandAttributeValue("token"));
            cloudBasePBParam.setServerIP(CommonRecord.getInstance().getIp());
            cloudBasePBParam.setServerPort(CommonRecord.getInstance().getPort());

            if(recordResource == RecordInfo.RecordResource.Device){
                cloudBasePBParam.setLocation("device");
                cloudBasePBParam.setPlayBackByTime(true);
                cloudBasePBParam.setBeginTime((int)mRecordStartTime);
                cloudBasePBParam.setEndTime((int)mRecordEndTime);
            }else{
                cloudBasePBParam.setLocation("cloud");
                cloudBasePBParam.setPlayBackByTime(false);
                cloudBasePBParam.setDPSRecordFiles(dpsRecordFiles);
            }

            cloudBasePBParam.setBackPlay(false);
            cloudBasePBParam.setUseHttps(EnvironmentHelper.getInstance().isUseHttps() ? 1 : 0);

            CloudBasePBCamera camera = new CloudBasePBCamera(cloudBasePBParam);

            if(mPlayManager != null) {
                mPlayManager.addCamera(mPlayManager.getSelectedWindowIndex(), camera);
                mPlayManager.playSingle(mPlayManager.getSelectedWindowIndex());
            }
        } else if(BuildConfig.isExpress) {
            ExpressPBCameraParam expressPBCameraParam = new ExpressPBCameraParam();
            String chnSnCode = null;
            if(mPlayManager != null) {
                chnSnCode = channelInfoList.get(mPlayManager.getSelectedWindowIndex()).getChnSncode();
                expressPBCameraParam.setNeedBeginTime((int)(mPlayManager.getCurrentProgress(mPlayManager.getSelectedWindowIndex())));
            }
            expressPBCameraParam.setCameraID(chnSnCode);
            expressPBCameraParam.setStreamType(1);

            UserInfo mUserInfo = CommonRecord.getInstance().getUserInfo();
            expressPBCameraParam.setDpRestToken((String)mUserInfo.getExtandAttributeValue("token"));
            expressPBCameraParam.setServerIP(CommonRecord.getInstance().getIp());
            expressPBCameraParam.setServerPort(CommonRecord.getInstance().getPort());
            //for GDPR
            if(!TextUtils.isEmpty("")) {
                expressPBCameraParam.setEncrypt(true);
                expressPBCameraParam.setPsk("");
            }

            if(recordResource == RecordInfo.RecordResource.Device){
                expressPBCameraParam.setLocation("device");
                expressPBCameraParam.setPlayBackByTime(true);
                expressPBCameraParam.setBeginTime((int)mRecordStartTime);
                expressPBCameraParam.setEndTime((int)mRecordEndTime);
            }else{
                expressPBCameraParam.setLocation("cloud");
                expressPBCameraParam.setPlayBackByTime(false);
                expressPBCameraParam.setDPSRecordFiles(dpsRecordFiles);
            }

            expressPBCameraParam.setBackPlay(false);
            expressPBCameraParam.setUseHttps(EnvironmentHelper.getInstance().isUseHttps() ? 1 : 0);

            ExpressPbCamera camera = new ExpressPbCamera(expressPBCameraParam);

            if(mPlayManager != null) {
                mPlayManager.addCamera(mPlayManager.getSelectedWindowIndex(), camera);
                mPlayManager.playSingle(mPlayManager.getSelectedWindowIndex());
            }
        } else {
            DPSPBCameraParam dpspbCameraParam = new DPSPBCameraParam();
            String chnSnCode = null;
            if(mPlayManager != null) {
                chnSnCode = channelInfoList.get(mPlayManager.getSelectedWindowIndex()).getChnSncode();
            }
            dpspbCameraParam.setCameraID(chnSnCode);
            //for GDPR
//            if(!TextUtils.isEmpty(encryptKey)) {
//                dpspbCameraParam.setEncrypt(true);
//                dpspbCameraParam.setPsk(encryptKey);
//            }
            try {
                dpspbCameraParam.setDpHandle(String.valueOf(dataAdapterInterface.getDPSDKEntityHandle()));
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            PlayBackInfo info = new PlayBackInfo();
            info.setIsBack(false);
            if(mPlayManager != null) info.setNeedBeginTime((int)(mPlayManager.getCurrentProgress(mPlayManager.getSelectedWindowIndex())));
            if(recordResource == RecordInfo.RecordResource.Device){
                info.setPlayBackByTime(true);
                info.setBeginTime((int)mRecordStartTime);
                info.setEndTime((int)mRecordEndTime);
            }else{
                info.setPlayBackByTime(false);
                info.setRecordFileList(dpsRecordFiles);
            }
            dpspbCameraParam.setPlayBackInfo(info);

            DPSPBCamera camera = new DPSPBCamera(dpspbCameraParam);
            if(mPlayManager != null) {
                mPlayManager.addCamera(mPlayManager.getSelectedWindowIndex(), camera);
                mPlayManager.playSingle(mPlayManager.getSelectedWindowIndex());
            }
        }

        Mutiple = 3;
        changeMutiple();
        showProgressDialog();
    }

    private void recordToDpsRecord(){
        if(dpsRecordFiles == null){
            dpsRecordFiles = new ArrayList<>();
        }else {
            dpsRecordFiles.clear();
        }

        for(RecordInfo recordInfo : recordInfos){
            DPSRecordFile dpsRecordFile = new DPSRecordFile();
            dpsRecordFile.setSsId(recordInfo.getSsId());
            dpsRecordFile.setFileHandler(recordInfo.getFileHandle());
            dpsRecordFile.setDiskId(recordInfo.getDiskId() == null ? "" : recordInfo.getDiskId());
            dpsRecordFile.setFileName(recordInfo.getFileName());
            dpsRecordFile.setRecordSource(3);
            dpsRecordFile.setBeginTime((int)recordInfo.getStartTime());
            dpsRecordFile.setEndTime((int)recordInfo.getEndTime());
            dpsRecordFiles.add(dpsRecordFile);
        }
    }

    private void startPlay(int winIndex){
        mPlayManager.playSingle(winIndex);
    }

    private void stopPlay(int winIndex){
        mPlayManager.stopSingle(winIndex);
        tvPlay.setText(R.string.play_play);
    }

    private IMediaPlayListener iMediaPlayListener = new IMediaPlayListener() {
        @Override
        public void onPlayeStatusCallback(int winIndex, final PlayStatusType type, int code) {
            dissmissProgressDialog();
            if(type == PlayStatusType.eStreamStartRequest){
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Stream_Start_Request);
            }else if(type == PlayStatusType.eStreamPlayed){
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Stream_Played);
            }else if(type == PlayStatusType.ePlayFirstFrame){
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_First_Frame);
            }else if(type == PlayStatusType.ePlayEnd){
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Play_End);
            }else if(type == PlayStatusType.eNetworkaAbort){
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Net_Error);
            }else if(type == PlayStatusType.ePlayFailed){
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Play_Failed);
            }else if(type == PlayStatusType.eBadFile){
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Bad_File);
            }else if(type == PlayStatusType.eStatusUnknow) {
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Play_Unknow);
            }else if(type == PlayStatusType.eSeekSuccess) {
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Seek_Success);
            }else if(type == PlayStatusType.eSeekFailed) {
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Seek_failed);
            }else if(type == PlayStatusType.eSeekCrossBorder) {
                if(mPlayBackHander != null) mPlayBackHander.sendEmptyMessage(KEY_Handler_Seek_Cross_Border);
            }
        }

        @Override
        public void onPlayTime(int winIndex, final long time) {

        }

        @Override
        public void onPlayerTimeAndStamp(int winIndex, long time, long timeStamp) {
            super.onPlayerTimeAndStamp(winIndex, time, timeStamp);
            Message msg = new Message();
            msg.what = 2;
            msg.obj = time - TimeDataHelper.getStartTimeByDay(mCalendar) / 1000L;
            mHandler.sendMessage(msg);
        }
    };

    private IOperationListener iOperationListener = new IOperationListener() {
        @Override
        public void onWindowSelected(int position) {

        }

        @Override
        public void onControlClick(int nWinIndex, ControlType type) {
            if (type == ControlType.Control_Open) {
                //select channel
            } else if(type == ControlType.Control_Reflash) {
                onClickPausePlay();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture:
                onClickCapture();
                break;
            case R.id.record:
                onClickRecord();
                break;
            case R.id.sound:
                onClickSound();
                break;
            case R.id.play:
                int winIndex = mPlayManager.getSelectedWindowIndex();
                if(mPlayManager.isPlaying(winIndex)) {
                    stopPlay(winIndex);
                } else {
                    startPlay(winIndex);
                }
                break;
            case R.id.pause:
                onClickPausePlay();
                break;
            case R.id.speedAdd:
                if(Mutiple >= 6) {
                    return;
                }
                Mutiple++;
                changeMutiple();
                break;
            case R.id.speedReduce:
                if(Mutiple <= 0) {
                    return;
                }
                Mutiple--;
                changeMutiple();
                break;
            case R.id.preMonth:
                onClickMonthPre();
                break;
            case R.id.nextMonth:
                onClickMonthNext();
                break;
            case R.id.center:
                tvCenter.setSelected(true);
                tvDevice.setSelected(false);
                recordResource = RecordInfo.RecordResource.Platform;
                getRecordMask();
                break;
            case R.id.device:
                tvCenter.setSelected(false);
                tvDevice.setSelected(true);
                recordResource = RecordInfo.RecordResource.Device;
                getRecordMask();
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

    private void onClickPausePlay(){
        if(channelInfoList.size() == 0) return;
        if(mPlayManager.isPause(mPlayManager.getSelectedWindowIndex())){
            if(mPlayManager.resume(mPlayManager.getSelectedWindowIndex()) == Err.OK) {
                tvPause.setText(R.string.play_back_pause);
            }
        }else if(mPlayManager.isPlaying(mPlayManager.getSelectedWindowIndex())){
            if(mPlayManager.pause(mPlayManager.getSelectedWindowIndex()) == Err.OK) {
                tvPause.setText(R.string.play_back_resume);
            }
        }else{
            startPlay(mPlayManager.getSelectedWindowIndex());
        }
        refreshBtnState();
    }

    private void changeMutiple(){
        float multiple = MutipleType.MULTIPLE_1X;
        switch (Mutiple){
            case 0:
                multiple = MutipleType.MULTIPLE_1_8X;
                break;
            case 1:
                multiple = MutipleType.MULTIPLE_1_4X;
                break;
            case 2:
                multiple = MutipleType.MULTIPLE_1_2X;
                break;
            case 3:
                multiple = MutipleType.MULTIPLE_1X;
                break;
            case 4:
                multiple = MutipleType.MULTIPLE_2X;
                break;
            case 5:
                multiple = MutipleType.MULTIPLE_4X;
                break;
            case 6:
                multiple = MutipleType.MULTIPLE_8X;
                break;
        }

        if(mPlayManager != null) mPlayManager.setPlaySpeed(mPlayManager.getSelectedWindowIndex(), multiple);
        if(multiple != MutipleType.MULTIPLE_1X){
            tvSound.setSelected(false);
        }else {
            refreshBtnState();
        }
    }

    private void onClickMonthPre(){
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mCalendar.add(Calendar.MONTH, -1);
        setDateLineText();
        getRecordMask();
    }

    private void onClickMonthNext(){
        if(mCurrentYear == mCalendar.get(Calendar.YEAR) && mCurrentMonth <= mCalendar.get(Calendar.MONTH) + 1){
            toast(R.string.play_back_record_not_create);
            return;
        }

        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mCalendar.add(Calendar.MONTH, 1);
        setDateLineText();
        getRecordMask();
    }

    private void setDateLineText(){
        tvDataTime.setText(String.format("%04d-%02d",
                mCalendar.get(Calendar.YEAR), (mCalendar.get(Calendar.MONTH) + 1)));
    }

    private void clearPlayInfo() {
        mRecordStartTime = -1;
        mRecordEndTime = -1;
        if(dpsRecordFiles != null) dpsRecordFiles.clear();
    }

    private void getRecordMask() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = (Calendar) mCalendar.clone();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date date = calendar.getTime();
                ChannelInfo chnlInfo = channelInfoList.get(mPlayManager.getSelectedWindowIndex());
                String dates = null;
                try {
                    dates = dataAdapterInterface.queryRecordDate(chnlInfo.getChnSncode(), recordResource, RecordInfo.RecordEventType.All, date.getTime()/ 1000);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                boolean[] maskArray = new boolean[31];
                if(!TextUtils.isEmpty(dates)) {
                    String[] datesArray = dates.split(",");
                    maskArray = new boolean[datesArray.length];
                    for (int i = 0; i < datesArray.length; i++) {
                        maskArray[i] = datesArray[i].equals("1");
                    }
                }

                Log.d("Tag", "get mark: " + (maskArray == null ? null : Arrays.toString(maskArray)));
                Message msg = new Message();
                msg.what = 1;
                msg.obj = maskArray;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private void refreshBtnState() {
        if(mPlayManager != null) {
            int winIndex = mPlayManager.getSelectedWindowIndex();
            tvRecord.setSelected(mPlayManager.isRecording(winIndex));
            tvPlay.setText(mPlayManager.isPlaying(winIndex) ? R.string.play_stop : R.string.play_play);
            tvSound.setSelected(mPlayManager.isOpenAudio(winIndex));
        }
    }
}
