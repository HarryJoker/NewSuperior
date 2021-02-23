package gov.android.com.superior.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONObject;
import com.android.business.adapter.DataAdapterImpl;
import com.android.business.common.BroadCase;
import com.android.business.entity.ChannelInfo;
import com.android.business.entity.GroupInfo;
import com.android.business.entity.UserInfo;
import com.android.business.exception.BusinessException;
import com.first.orient.base.AppContext;
import com.first.orient.base.utils.JokerLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.android.com.superior.entity.CommonRecord;

/**
 * @author HarryJoker
 * @date :2020-11-14 11:29
 * @description:
 */
public class ChannelHelper {

    private static String ERROR_LOGIN = "登录大华视频失败";
    private static String ERROR_VIDEO = "获取视频失败";

    private static final int PORT = 9000;
    private static final String IP = "222.134.55.34";
    private static final String USERNAME = "zhengwuducha";
    private static final String PASSWORD = "12345678";

    private DeviceBroadcastReceiver mReceiver;

    private ChannelInfoCallback mChannelInfoCallback;

    public static Map<String, ChannelInfo> mChannelInfos = new HashMap<>();

    private static class Instance {
        static ChannelHelper instance = new ChannelHelper();
    }

    public static ChannelHelper getInstance() {
        return ChannelHelper.Instance.instance;
    }

    private ChannelHelper() {
        registerReciver();
    }

    private Handler handler = new Handler();

    public void setmChannelInfoCallback(ChannelInfoCallback channelInfoCallback) {
        this.mChannelInfoCallback = channelInfoCallback;
    }

    private boolean loginDSS() {
        try {
            DataAdapterImpl.getInstance().initServer(IP, PORT);
            CommonRecord.getInstance().setIp(IP);
            CommonRecord.getInstance().setPort(PORT);
            String clientMac = Settings.Secure.getString(AppContext.getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
            UserInfo info = DataAdapterImpl.getInstance().login(USERNAME, PASSWORD, "1", clientMac, 2);
            if (info != null && !TextUtils.isEmpty(info.userId)) {
                JokerLog.e("DSS Video Login: " + JSONObject.toJSONString(info));
                CommonRecord.getInstance().setUserInfo(info);
                return true;
            }
        } catch (BusinessException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void registerReciver() {
        mReceiver = new DeviceBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCase.Action.DEVICE_ACTION_PUSH_MODIFY_DEVICE);
        LocalBroadcastManager.getInstance(AppContext.getApplication()).registerReceiver(mReceiver,filter);
    }

    private void unRegisterReciver() {
        LocalBroadcastManager.getInstance(AppContext.getApplication()).unregisterReceiver(mReceiver);
    }

    public void requestChannelList() {
        if (loginDSS()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GroupHelper.getInstance().LoadAllGroup();
                }
            }).start();
        } else {
            if (mChannelInfoCallback != null) {
                mChannelInfoCallback.onError(ERROR_LOGIN);
            }
        }
    }

    private void getChannelInfos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GroupInfo groupInfo = GroupFactory.getInstance().getRootGroupInfo();
                List<ChannelInfo> channelInfos = ChannelFactory.getInstance().getAllChannelInfo(groupInfo);
                JokerLog.e(  groupInfo == null ? "group is null" : ("groupId:" + groupInfo.getGroupId() +  " groupName: " + groupInfo.getGroupName())  + "ChannelInfos:" + JSONObject.toJSONString(channelInfos));
                Map<String, ChannelInfo> infos = new HashMap<>();
                for (ChannelInfo info : channelInfos) {
                    infos.put(info.getName(), info);
                    JokerLog.e("ChannelInfo:" + JSONObject.toJSONString(info));
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mChannelInfoCallback != null) {
                            mChannelInfoCallback.onChannelInfosResult(infos);
                        }
                    }
                });
            }
        }).start();
    }

    class DeviceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())) return;
            switch(intent.getAction()){
                case BroadCase.Action.DEVICE_ACTION_PUSH_MODIFY_DEVICE:
                    getChannelInfos();
                    break;
                default:
                    break;
            }
        }
    }

    public interface ChannelInfoCallback {
        void onChannelInfosResult(Map<String, ChannelInfo> channelInfos);
        void onError(String error);
    }
}
