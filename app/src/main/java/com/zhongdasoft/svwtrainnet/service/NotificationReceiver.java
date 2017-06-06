package com.zhongdasoft.svwtrainnet.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhongdasoft.svwtrainnet.module.home.CalendarActivity;
import com.zhongdasoft.svwtrainnet.util.NotifyUtil;
import com.zhongdasoft.svwtrainnet.util.SystemUtil;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/26 9:41
 * 修改人：Administrator
 * 修改时间：2016/7/26 9:41
 * 修改备注：
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("Notify", NotifyUtil.SendNotify.equals(intent.getAction()) + "");
        if (NotifyUtil.SendNotify.equals(intent.getAction())) {
            //判断app进程是否存活
            if (SystemUtil.isAppAlive(context, context.getPackageName())) {
                Intent mainIntent = new Intent(context, CalendarActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainIntent);
            } else {
                Intent launchIntent = context.getPackageManager().
                        getLaunchIntentForPackage(context.getPackageName());
                launchIntent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(launchIntent);
            }
        } else {

        }
    }
}
