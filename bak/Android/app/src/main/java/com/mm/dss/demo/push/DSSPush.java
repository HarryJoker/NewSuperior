package com.mm.dss.demo.push;

import android.content.Context;
import android.os.Bundle;

import com.android.business.adapter.DataAdapterImpl;
import com.android.business.adapter.DataAdapterInterface;
import com.android.business.callback.IMessageCallback;
import com.android.business.common.BroadCase;
import com.android.business.exception.BusinessException;
import com.dahua.logmodule.LogHelperEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by 17801 on 2017/3/6.
 */
public class DSSPush implements IMessageCallback {

    private static final String TAG = DSSPush.class.getSimpleName();
    private Context mContext;
    /**把通知和报警消息队列分开，防止报警太多，通知消息处理不及时的问题；**/
    /**通知消息队列**/
    private LinkedList<String> msgList = new LinkedList<>();
    private final byte[] threadLock = new byte[]{};
    private final byte[] alarmLock = new byte[]{};

    private boolean bQuit = false;

    public boolean init(Context context) throws BusinessException {
        mContext = context;
        DataAdapterInterface dataAdapterInterface = DataAdapterImpl.getInstance();
        try {
            dataAdapterInterface.registerMessageCallback(this);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        initMsgLooper();
        return true;
    }

    private void initMsgLooper() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!bQuit) {
                    String callMsg = "";
                    boolean isSleep = false;
                    synchronized (threadLock) {
                        if (msgList.size() > 0) {
                            callMsg  = msgList.pop();
                        }
                        isSleep = msgList.isEmpty();
                    }
                    if (!callMsg.equals("")) {
                        dealCallbackMsg(callMsg);
                    }
                    callMsg = "";
                    if (!callMsg.equals("")) {
                        dealCallbackMsg(callMsg);
                    }

                    if (isSleep) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void callback(String jsonMsg) {
        synchronized (threadLock) {
            String msg = new StringBuilder().append(jsonMsg).toString();
            msgList.push(msg);
        }
    }

    private void dealCallbackMsg(String jsonMsg) {
        int cmd ;
        try {
            JSONObject jsonObj = new JSONObject(jsonMsg);
            cmd = jsonObj.optInt("cmd");
            notify(cmd, jsonMsg);
        } catch (JSONException e) {
            e.printStackTrace();

        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设备推送和报警消息推送
     */
    private void notify(int type, String content) throws BusinessException {
        try {
            JSONObject json = new JSONObject(content);
            PushMessageCode cmd = PushMessageCode.valueOf(type);
            if(cmd != null) {
                switch (cmd) {
                    case DPSDK_CMD_CHNL_STATUS_NOTIFY:
                    {
                        String channelId = json.optString("id");
                        int state = json.optInt("status");
                        LogHelperEx.debug("DPSDK_CMD_CHNL_STATUS_NOTIFY : " + "channelId: " + channelId + " state: " + state);
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("channelId", channelId);
                        bundle.putInt("state", state);
                        BroadCase.send(DssBroadCase.DEVICE_ACTION_PUSH_MODIFY_DEVICE_STATE, bundle, mContext);
                    }
                    break;
                    case DPSDK_CMD_CMS_CLOSE://cms服务断开，表示与服务器断开连接，需要重新登陆才能使用功能
                        //TODO 设置广播通知UI进行重登陆操作
                        LogHelperEx.debug("DPSDK_CMD : " + "DPSDK_CMD_CMS_CLOSE ");

                        break;
                    case DPSDK_CMD_REPORT_ALARM:
                        //接收所有报警推送，此处区分各类报警
                        LogHelperEx.debug("DPSDK_CMD_REPORT_ALARM : " + "DPSDK_CMD_REPORT_ALARM ");

                        break;
                }
            }
        } catch (JSONException e) {
            LogHelperEx.debug("device notify jsonException:" + e.toString());
        }
    }

}
