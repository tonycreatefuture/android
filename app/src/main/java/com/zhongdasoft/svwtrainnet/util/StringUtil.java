package com.zhongdasoft.svwtrainnet.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class StringUtil {
    /**
     * 判断是否为null或空值或空集合
     *
     * @param str String
     * @return true or false
     */
    public static boolean isNullOrEmptyOrEmptySet(String str) {
        return str == null || str.trim().length() == 0 || "[]".equals(str);
    }

    /**
     * 判断是否为null或空值
     *
     * @param str String
     * @return true or false
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断str1和str2是否相同
     *
     * @param str1 str1
     * @param str2 str2
     * @return true or false
     */
    public static boolean equals(String str1, String str2) {
        return str1 == str2 || (str1 != null && str1.equals(str2));
    }

    /**
     * 判断str1和str2是否相同(不区分大小写)
     *
     * @param str1 str1
     * @param str2 str2
     * @return true or false
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null && str1.equalsIgnoreCase(str2);
    }

    /**
     * 判断字符串str1是否包含字符串str2
     *
     * @param str1 源字符串
     * @param str2 指定字符串
     * @return true源字符串包含指定字符串，false源字符串不包含指定字符串
     */
    public static boolean contains(String str1, String str2) {
        return str1 != null && str1.contains(str2);
    }

    /**
     * 判断字符串是否为空，为空则返回一个空值，不为空则返回原字符串
     *
     * @param str 待判断字符串
     * @return 判断后的字符串
     */
    public static String getString(String str) {
        return str == null ? "" : str;
    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return str
     */
    public static String dateToStr(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    /**
     * 对象转换成十进制
     *
     * @param obj
     * @return str
     */
    public static String objectToDecimal(Object obj) {

        if (null == obj) {
            return "0.00";
        }
        return obj.toString();
    }

    /**
     * 对象转换成布尔型
     *
     * @param obj
     * @return str
     */
    public static String objectToBoolean(Object obj) {

        if (null == obj) {
            return "";
        }
        return Boolean.parseBoolean(obj.toString()) ? "是" : "否";
    }

    /**
     * 对象转换成字符串
     *
     * @param obj
     * @return str
     */
    public static String objectToStr(Object obj) {

        if (null == obj) {
            return "";
        }
        return obj.toString();
    }

    /**
     * 对象转换成字符串
     *
     * @param obj
     * @param defaultStr
     * @return str
     */
    public static String objectToStr(Object obj, String defaultStr) {

        if (null == obj) {
            return defaultStr;
        }
        return obj.toString();
    }

    /**
     * 对象转换成字符串
     *
     * @param obj1
     * @param obj2
     * @return str
     */
    public static String objectToStr(Object obj1, Object obj2) {

        if (null == obj1) {
            return "";
        }
        if (null == obj2) {
            return obj1.toString();
        } else {
            return obj1.toString().replace("T", " ").substring(0, 16) + "至" + obj2.toString().replace("T", " ").substring(0, 16);
        }
    }

    /**
     * 对象转换成字符串
     *
     * @param obj
     * @param str
     * @return str
     */
    public static String objectToStr1(Object obj, String str) {
        String postFix = "";
        if (str.contains("/")) {
            postFix = str.split("/")[0];
            str = str.split("/")[1];
        }
        if (null == obj) {
            return str;
        }
        return obj.toString() + postFix + "/" + str;
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date strToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * long转换成字符串
     *
     * @param l
     * @return String
     */
    public static String longToStr(Long l) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);

        int year = c.get(Calendar.YEAR);    //获取年
        int month = c.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份
        int day = c.get(Calendar.DAY_OF_MONTH);    //获取当前天数
        int first = c.getActualMinimum(c.DAY_OF_MONTH);    //获取本月最小天数
        int last = c.getActualMaximum(c.DAY_OF_MONTH);    //获取本月最大天数
        int time = c.get(Calendar.HOUR_OF_DAY);       //获取当前小时
        int min = c.get(Calendar.MINUTE);          //获取当前分钟
        int xx = c.get(Calendar.SECOND);          //获取当前秒

        return year + "-" + month + "-" + day + " " + time + ":" + min + ":" + xx;
    }

    /**
     * 计算两个日期之间相差的秒数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差秒数
     * @throws ParseException
     */
    public static int secondsBetween(Date smdate, Date bdate)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_seconds = (time2 - time1) / (1000);

        return Integer.parseInt(String.valueOf(between_seconds));
    }

    public static int myCompareTo(String first, String second) {
        String[] firstArr = first.replace(".", ",").split(",");
        String[] secondArr = second.replace(".", ",").split(",");
        int iFirst = 0;
        int iSecond = 0;
        for (int i = 0; i < firstArr.length; ++i) {
            iFirst = Integer.parseInt(firstArr[i]);
            iSecond = Integer.parseInt(secondArr[i]);
            if (iFirst == iSecond) {
                continue;
            } else if (iFirst > iSecond) {
                return 1;
            } else {
                return -1;
            }
        }
        if (iFirst == iSecond) {
            return 0;
        }

        return 0;
    }

    /**
     * 获取当前时间的long值
     *
     * @param currentTime
     * @param location    0当前位置，向前-1，向后1
     * @return long
     */
    public static long getTime(String currentTime, int location) {
        Calendar c = Calendar.getInstance();
        Date date;
        if (0 == location) {
            date = strToDate(currentTime.replace("T", " ").substring(0, 19));
        } else {
            if (-1 == location) {
                date = strToDate(currentTime.substring(0, 10) + " 00:00:00");
            } else {
                date = strToDate(currentTime.substring(0, 10) + " 23:59:59");
            }
        }
        c.setTime(date);
        return c.getTimeInMillis();
    }
}
