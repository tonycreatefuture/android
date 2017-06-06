package com.zhongdasoft.svwtrainnet.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.zhongdasoft.svwtrainnet.util.NotifyUtil;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/24 13:50
 * 修改人：Administrator
 * 修改时间：2016/7/24 13:50
 * 修改备注：
 */
public class MyService extends Service {

    //    private static final String TAG = "MyService";
    private PendingIntent pendingIntent;
    private AlarmManager alarm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.w(TAG, "in onCreate");
//        Log.e("Notify", "onCreate=" + System.currentTimeMillis());
    }

    @Override
    public int onStartCommand(Intent serviceIntent, int flags, int startId) {
        if (null != serviceIntent) {
//        Log.e("Notify", "onStartCommand=" + System.currentTimeMillis());
            final String[] time = serviceIntent.getStringArrayExtra("time");
            final String[] title = serviceIntent.getStringArrayExtra("title");
            final String[] message = serviceIntent.getStringArrayExtra("message");
            final String[] content = serviceIntent.getStringArrayExtra("content");
            final String[] key = serviceIntent.getStringArrayExtra("key");
            Intent intent = new Intent(MyService.this, AlarmReceiver.class);
            intent.setAction(NotifyUtil.SendNotify);
            Bundle bundle = new Bundle();
            bundle.putStringArray("time", time);
            bundle.putStringArray("title", title);
            bundle.putStringArray("message", message);
            bundle.putStringArray("content", content);
            bundle.putStringArray("key", key);
            intent.putExtras(bundle);
            pendingIntent = PendingIntent.getBroadcast(MyService.this, 0, intent, 0);
            alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, pendingIntent);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.w(TAG, "in onDestroy");
        if (null != alarm) {
            alarm.cancel(pendingIntent);
        }
        if (null != pendingIntent) {
//            Log.e("Notify", "pendingIntent=" + pendingIntent);
            pendingIntent.cancel();
        }
//        Log.e("Notify", "onDestroy=" + System.currentTimeMillis());
    }
}