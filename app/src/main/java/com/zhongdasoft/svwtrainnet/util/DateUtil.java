package com.zhongdasoft.svwtrainnet.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 项目名称： android
 * 类描述：
 * 创建人：tony
 * 创建时间：2017/5/18 12:06
 * 修改人：tony
 * 修改时间：2017/5/18 12:06
 * 修改备注：
 */

public class DateUtil {
    public static String getSystemDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static String getSystemDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
}
