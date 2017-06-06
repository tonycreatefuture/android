package com.netease.nim.uikit.common.util;

import android.graphics.Paint;

/**
 * 项目名称： android
 * 类描述：
 * 创建人：tony
 * 创建时间：2017/2/17 11:46
 * 修改人：tony
 * 修改时间：2017/2/17 11:46
 * 修改备注：
 */

public class TextUtil {
    public static int getCharacterWidth(String text, float size) {
        if (null == text || "".equals(text)){
            return 0;

        }
        Paint paint = new Paint();
        paint.setTextSize(size);
        int text_width = (int) paint.measureText(text);// 得到总体长度
        // int width = text_width/text.length();//每一个字符的长度
        return text_width;
    }
}
