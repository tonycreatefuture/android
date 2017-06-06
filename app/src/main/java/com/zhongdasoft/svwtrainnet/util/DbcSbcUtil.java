package com.zhongdasoft.svwtrainnet.util;

public class DbcSbcUtil {

    /**
     * 半角、全角字符判断
     *
     * @param c 字符
     * @return true：半角； false：全角
     */
    public static boolean isDbcCase(char c) {
        // 基本拉丁字母（即键盘上可见的，空格、数字、字母、符号）
        if (c >= 32 && c <= 127) {
            return true;
        }
        // 日文半角片假名和符号
        else if (c >= 65377 && c <= 65439) {
            return true;
        }
        return false;
    }

    /**
     * 字符串长度取得（区分半角、全角）
     *
     * @param str 字符串
     * @return 字符串长度
     */
    public static int getLength(String str) {
        int len = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (isDbcCase(c)) { // 半角
                len = len + 1;
            } else { // 全角
                len = len + 2;
            }
        }
        return len;
    }

    /**
     * 字符串截取（区分半角、全角）
     *
     * @param str   字符串
     * @param limit 长度
     * @return
     */
    public static String left(String str, int limit) {
        if (getLength(str) <= limit) {
            return str;
        }
        char[] chars = str.toCharArray();
        int charLenSum = 0;
        String result = "";
        for (int i = 0; i < chars.length; i++) {
            int charLen = isDbcCase(chars[i]) ? 1 : 2;
            if (charLenSum + charLen > limit) {
                return result;
            }
            charLenSum += charLen;
            result += chars[i];
            if (charLenSum == limit) {
                return result;
            }
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(left("全角文字", 10));
        System.out.println(left("全角文字判", 10));
        System.out.println(left("全角文字12", 10));
        System.out.println(left("全角文字123", 10));
        System.out.println(left("全角文字判a", 10));
        System.out.println(left("全角文字判断", 10));
    }
}
