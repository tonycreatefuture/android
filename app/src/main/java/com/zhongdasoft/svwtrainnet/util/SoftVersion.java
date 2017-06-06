package com.zhongdasoft.svwtrainnet.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class SoftVersion {
    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {
        String versionCode = "1.0.0";
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

//    public static ActivityInfo[] getActivityInfo(Context context) {
//        String packageName;
//        ActivityInfo[] actInfo = null;
//        try {
//            packageName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
//            actInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).activities;
//        } catch (NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return actInfo;
//    }
}
