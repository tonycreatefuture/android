package com.zhongdasoft.svwtrainnet.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.NotifyUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/22 14:47
 * 修改人：Administrator
 * 修改时间：2016/7/22 14:47
 * 修改备注：
 */
public class AlarmReceiver extends BroadcastReceiver {
    //通知的唯一标识，在一个应用程序中不同的通知要区别开来
//    private static final int Schedule = 100;
    private static final long _30Seconds = 30 * 1000 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NotifyUtil.SendNotify.equals(intent.getAction())) {
            String[] keys = intent.getStringArrayExtra("key");
            String[] times = intent.getStringArrayExtra("time");
            String[] titles = intent.getStringArrayExtra("title");
            String[] messages = intent.getStringArrayExtra("message");
            String[] contents = intent.getStringArrayExtra("content");
            boolean isEnd = true;
            for (int i = 0; i < keys.length; i++) {
//                Log.e("Notify", "key=" + keys[i]);
                String send = MySharedPreferences.getInstance().getString(keys[i]);
//                Log.e("Notify", "send=" + send);
                if (!StringUtil.isNullOrEmpty(send)) {
                    continue;
                } else {
                    isEnd = false;
                    notifyUser(context, i, keys[i], times[i], titles[i], messages[i], contents[i]);
                }
            }
            if (isEnd) {
                Intent serviceIntent = new Intent(context, MyService.class);
                context.stopService(serviceIntent);
            }
        } else {
            //ToastUtil.show(context, intent.getAction());
        }
    }

    private void notifyUser(Context context, int id, String key, String timeStr, String title, String message, String content) {
        long time = Long.parseLong(timeStr);
        if (time == -1) {
            MySharedPreferences.getInstance().setStoreString(key, "1");
            return;
        }
//        Log.e("Notify", "time=" + time + ",current=" + System.currentTimeMillis() + ";" + (time <= System.currentTimeMillis() && time + _30Seconds > System.currentTimeMillis()));
        if (time == 0 || (time <= System.currentTimeMillis() && time + _30Seconds > System.currentTimeMillis())) {
            Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
            broadcastIntent.setAction(NotifyUtil.SendNotify);
            PendingIntent pendingIntent = PendingIntent.
                    getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(title)
                    .setContentText(message)
                    .setContentInfo(content)
                    .setTicker(title + ":" + message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.mipmap.logo);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(id, builder.build());
            MySharedPreferences.getInstance().setStoreString(key, "1");
        }
    }
}
