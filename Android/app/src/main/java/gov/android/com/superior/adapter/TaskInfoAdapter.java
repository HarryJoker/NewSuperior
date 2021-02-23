package gov.android.com.superior.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.android.business.adapter.DataAdapterImpl;
import com.android.business.adapter.DataAdapterInterface;
import com.android.business.entity.ChannelInfo;
import com.android.business.exception.BusinessException;
import com.android.dahua.dhplaycomponent.PlayManagerProxy;
import com.android.dahua.dhplaycomponent.camera.RTCamera.DPSRTCamera;
import com.android.dahua.dhplaycomponent.camera.RTCamera.DPSRTCameraParam;
import com.android.dahua.dhplaycomponent.camera.inner.Camera;
import com.android.dahua.dhplaycomponent.camera.inner.RealInfo;
import com.android.dahua.dhplaycomponent.windowcomponent.window.PlayWindow;
import com.first.orient.base.AppContext;
import com.first.orient.base.adapter.BaseMapRecycleAdapter;
import com.first.orient.base.utils.JokerLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.R;
import gov.android.com.superior.config.Config;

public class TaskInfoAdapter extends BaseMapRecycleAdapter {

    private static Map<Integer, BaseMapRecycleAdapter.HolderMeta> mHolderMetas = new HashMap<Integer, HolderMeta>() {
        {
            put(1, new BaseMapRecycleAdapter.HolderMeta(1, TaskInfoViewHolder.class, R.layout.rc_item_map_property));
            put(2, new BaseMapRecycleAdapter.HolderMeta(2, VideoViewHolder.class, R.layout.rc_item_map_video));
        }
    };

    private static Map<Integer, List<MetaBean>> categoryMetaBeans = new HashMap<Integer, List<MetaBean>>() {
        {
            List<BaseMapRecycleAdapter.MetaBean> mMetaBeans = new ArrayList<>();
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("labelType", "任务类别：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "主要任务：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("content", "推进计划：", 1));
            put(1, mMetaBeans);
            mMetaBeans = new ArrayList<>();
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("labelType", "任务排名：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "具体项目：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("content", "办理细则：", 1));
            put(2, mMetaBeans);
            mMetaBeans = new ArrayList<>();
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("labelType", "任务类别：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "建议提案：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("content", "办理要求：", 1));
            put(3, mMetaBeans);
            mMetaBeans = new ArrayList<>();
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("labelType", "任务类别：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "议定事项：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("content", "工作要求：", 1));
            put(4, mMetaBeans);
            mMetaBeans = new ArrayList<>();
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "交办事项：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("content", "办理要求：", 1));
            put(5, mMetaBeans);
            mMetaBeans = new ArrayList<>();
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "督查事项：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("content", "具体要求：", 1));
            put(6, mMetaBeans);
            mMetaBeans = new ArrayList<>();
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("channelInfo", "项目名称：", 2));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "项目名称：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("totalInvest", "总投资额：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("cumulativeInvest", "累计投资额：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("yearPlanInvest", "年度计划投资：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("yearCumulativeDoneInvest", "年度累计完成投资：", 1));
            put(7, mMetaBeans);
            mMetaBeans = new ArrayList<>();
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "民生事项：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("content", "具体描述：", 1));
            put(8, mMetaBeans);
            mMetaBeans = new ArrayList<>();
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("title", "群众线索：", 1));
            mMetaBeans.add(new BaseMapRecycleAdapter.MetaBean("content", "具体描述：", 1));
            put(9, mMetaBeans);
        }
    };

    public TaskInfoAdapter(Context context, JSONObject dataSource) {
        super(context, categoryMetaBeans.get(dataSource.getIntValue("category")), mHolderMetas, dataSource);
    }

    @Override
    protected void onInitViewHolder(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder != null && viewHolder instanceof TaskInfoViewHolder) {
            ((TaskInfoViewHolder)viewHolder).setCategory(getMetaValue("category"));
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        JokerLog.e("onViewDetachedFromWindow: " + holder);
        if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder)holder).destroy();
        }
    }

    public static class TaskInfoViewHolder extends BaseViewHolder {

        TextView title;
        TextView value;

        private String mCategory = "";

        public void setCategory(String category) {
            this.mCategory = category;
        }

        public TaskInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            value = itemView.findViewById(R.id.tv_value);
        }

        @Override
        public void bindViewHolderForValue(int position, String key, Object valueObj) {
            value.setText(valueObj.toString());
            if (key.equals("labelType") && Config.categoryTaskTypeLabels.containsKey(mCategory)) {
                String[] typeLabels = Config.categoryTaskTypeLabels.get(mCategory);
                int type = Integer.parseInt(valueObj.toString());
                if (type < typeLabels.length) {
                    value.setText(typeLabels[type]);
                }
            }
        }

        @Override
        public void bindViewHolderForTitle(int position, String key, BaseMapRecycleAdapter.MetaBean metaBean) {
            title.setText(metaBean.getTitle());
        }
    }

    public static class VideoViewHolder extends BaseViewHolder {

        public static final int Stream_Main_Type = 1;		//主码流 // the main stream
        public static final int Stream_Assist_Type = 2;		//辅码流 // auxiliary stream
        public static final int Stream_Third_Type = 3;		//三码流 // three stream

        private PlayWindow mPlayWin;
        protected PlayManagerProxy mPlayManager;
        private DataAdapterInterface dataAdapterInterface;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            mPlayWin = itemView.findViewById(R.id.play_window);

            initData();
        }

        private void initVideoWindow() {
            DisplayMetrics metric = new DisplayMetrics();
            WindowManager wm = (WindowManager) AppContext.getApplication().getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metric);
            int mScreenWidth = metric.widthPixels; // 屏幕宽度（像素）
            int mScreenHeight = metric.heightPixels; // 屏幕高度（像素）
            mScreenHeight = mScreenWidth * 3 / 4;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mPlayWin.getLayoutParams();
            lp.width = mScreenWidth;
            lp.height = mScreenHeight;
            mPlayWin.setLayoutParams(lp);
            mPlayWin.forceLayout(mScreenWidth, mScreenHeight);
        }

        private void initData() {
            dataAdapterInterface = DataAdapterImpl.getInstance();
            //初始化playManager
            // initialize playManager.
            mPlayManager = new PlayManagerProxy();
            //初始化窗口数量，默认显示4个窗口，最多16窗口，若设置单窗口均设置为1
            // the number of initialization window, the default display is 4 Windows, up to 16 Windows, if the setting of single window is set to 1.
            mPlayManager.init(itemView.getContext(), 16, 1, mPlayWin);
            //设置对讲监听
            // set the intercom monitor.
//            mPlayManager.setOnTalkListener(iTalkListener);
            //设置播放监听
            // set play monitor.
//            mPlayManager.setOnMediaPlayListener(iMediaPlayListener);
            //设置云台监听
            // set the cloud monitor.
//            mPlayManager.setOnPTZListener(iptzListener);
            //设置窗口操作监听
            // set window operation to listen.
//            mPlayManager.setOnOperationListener(iOperationListener);
            //初始化窗口大小
            // initialization window size.
//            initCommonWindow();
            initVideoWindow();
//            channelInfoList = (List<ChannelInfo>) getIntent().getSerializableExtra("channel_info_list");
//            int index = 0;
//            if(channelInfoList != null) {
//                for(ChannelInfo channelInfo : channelInfoList) {
//                    channelInfoMap.put(index, channelInfo);
//                    index ++;
//                }
//            }
        }

        private DPSRTCamera getCamera(ChannelInfo channelInfo) {
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

        @Override
        public void bindViewHolderForValue(int position, String key, Object valueObj) {
            if (valueObj != null && valueObj instanceof ChannelInfo) {
                ChannelInfo channelInfo = (ChannelInfo)valueObj;
                List<Camera> cameras = new ArrayList<>();
                cameras.add(getCamera(channelInfo));
                mPlayManager.addCameras(cameras);
                mPlayManager.playCurpage();
            }
        }

        public void destroy() {
            JokerLog.e("VideoViewHolder onDestroy..............................................");
            if (mPlayManager != null) {
                mPlayManager.stopAll();
                mPlayManager.unitPlayManager();
                mPlayManager = null;
            }
        }

        @Override
        public void bindViewHolderForTitle(int position, String key, BaseMapRecycleAdapter.MetaBean metaBean) {
        }
    }

}
