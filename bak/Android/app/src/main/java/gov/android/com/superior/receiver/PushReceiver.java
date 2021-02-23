package gov.android.com.superior.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import gov.android.com.superior.home.unit.task.TaskActivity;
import gov.android.com.superior.home.unit.task.TaskTraceActivity;
import gov.android.com.superior.message.MessageActivity;
import gov.android.com.superior.tools.GsonUtil;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by wanghua on 17/8/24.
 */

public class PushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Logger.d(intent.getAction());

        if (intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {
            int bageNum = context.getSharedPreferences("bage", Context.MODE_PRIVATE).getInt("bageNum", 0) + 1;
            SharedPreferences.Editor editor = context.getSharedPreferences("bage", Context.MODE_PRIVATE).edit();
            editor.putInt("bageNum", bageNum);
            editor.commit();

            ShortcutBadger.applyCount(context, bageNum); //for 1.1.4+
        }

        if (intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {

            String string = intent.getExtras().getString(JPushInterface.EXTRA_EXTRA);
            Map<String, Object> extras = GsonUtil.fromJson(string, new TypeToken<Map<String, Object>>(){});
            Logger.d(extras);

            if (extras.containsKey("type"))
            {
                int type = Integer.parseInt(extras.get("type").toString());
                //type, 1:发布通知消息，2：任务督促
                if (type == 1) {
                    Intent toIntent = new Intent(context, MessageActivity.class);
                    toIntent.putExtra("messageId", Long.parseLong(extras.get("id").toString()));
                    toIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(toIntent);
                } else if (type == 2) {
                    Intent toIntent = new Intent(context, TaskActivity.class);
                    toIntent.putExtra("taskId", Long.parseLong(extras.get("id").toString()));
                    toIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(toIntent);
                } else if (type == 3) {
                    Intent toIntent = new Intent(context, TaskTraceActivity.class);
                    List tasks = new ArrayList();
                    Map<String, Object> task = new HashMap<>();
                    task.put("id", extras.get("id"));
                    tasks.add(task);
                    toIntent.putExtra("groupTasks", (Serializable) tasks);
//                    toIntent.putExtra("taskId", Long.parseLong(extras.get("id").toString()));
                    toIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(toIntent);
                }
            }
        }
    }
}
