package com.zhongdasoft.svwtrainnet.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhongdasoft.svwtrainnet.service.MyService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/22 10:37
 * 修改人：Administrator
 * 修改时间：2016/7/22 10:37
 * 修改备注：
 */
public class NotifyUtil {
    public static final String SendNotify = "SendNotify";
    //提前发送时间（分钟）
    private static final int BeforeSendTime = 30;
    //millionSecond转换为分钟
    private static final int MillionTrans = 1000 * 60;
    //4类信息
    private static final int typeCount = 4;

    public static void sendNotify(Context mContext, ArrayList<String> notifyList) {
        if (null == notifyList || notifyList.size() <= 0) {
            return;
        }
        String[] keys = new String[notifyList.size() / typeCount];
        String[] titles = new String[notifyList.size() / typeCount];
        String[] messages = new String[notifyList.size() / typeCount];
        String[] contents = new String[notifyList.size() / typeCount];
        String[] sendTimes = new String[notifyList.size() / typeCount];
        for (int i = 0, j = 0; i < notifyList.size(); i += typeCount, j++) {
            titles[j] = notifyList.get(i);
            messages[j] = notifyList.get(i + 1);
            sendTimes[j] = notifyList.get(i + 2);
            contents[j] = notifyList.get(i + 3);
            keys[j] = titles[j] + sendTimes[j];
        }

        String currentTime = MySharedPreferences.getInstance().getCurrentTime();
        String[] times = calculateTime(sendTimes, currentTime);
//        Log.e("Notify", "time=" + time);
        Intent intent = new Intent(mContext, MyService.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray("time", times);
        bundle.putStringArray("title", titles);
        bundle.putStringArray("message", messages);
        bundle.putStringArray("content", contents);
        bundle.putStringArray("key", keys);
        intent.putExtras(bundle);
        mContext.stopService(intent);
        mContext.startService(intent);
    }

    private static String[] calculateTime(String[] sendTimes, String currentTime) {
        String[] times = new String[sendTimes.length];
        for (int i = 0; i < sendTimes.length; i++) {
            //设置发送时间
//        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            Calendar c = Calendar.getInstance();
//        Log.e("Notify", "currentTime=" + currentTime + ",sendTime=" + sendTime + ",localtime=" + formatter.format(new Date()));
            c.setTime(StringUtil.strToDate(currentTime));
            long serverTime = c.getTimeInMillis();
            c.setTime(new Date());
            long diffTime = serverTime - c.getTimeInMillis();
            c.setTime(StringUtil.strToDate(sendTimes[i]));
            long realSendTime = c.getTimeInMillis();
            //信息已过期，不能发送
            if (realSendTime <= serverTime) {
                times[i] = "-1";
            } else {
                if ((int) ((realSendTime - serverTime) / MillionTrans) >= BeforeSendTime) {
                    c.add(Calendar.MINUTE, -1 * BeforeSendTime);
                    c.add(Calendar.MINUTE, -1 * (int) (diffTime / MillionTrans));
                    times[i] = c.getTimeInMillis() + "";
                } else {
                    times[i] = "0";
                }
            }
        }
        return times;
    }
}
