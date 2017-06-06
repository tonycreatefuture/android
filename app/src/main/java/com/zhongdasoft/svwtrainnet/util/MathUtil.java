package com.zhongdasoft.svwtrainnet.util;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/6 11:42
 * 修改人：Administrator
 * 修改时间：2016/7/6 11:42
 * 修改备注：
 */
public class MathUtil {
    public final static int RoundZero = 0;
    public final static int RoundOne = 1;
    public final static int RoundTwo = 2;

    public static String getRound(String str, int round) {
        int index = str.indexOf(".");
        if (index < 0) {
            return str;
        }
        if (str.length() - index <= round) {
            return str;
        } else {
            return str.substring(0, index + round + 1);
        }
    }
}
