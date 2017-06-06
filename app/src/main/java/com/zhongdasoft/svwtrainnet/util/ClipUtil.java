package com.zhongdasoft.svwtrainnet.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/6/21 14:32
 * 修改人：Administrator
 * 修改时间：2016/6/21 14:32
 * 修改备注：
 */
public class ClipUtil {
    public static void CopyData(Context context, String string) {
        ClipboardManager myClipboard;
        myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip;
        myClip = ClipData.newPlainText("string", string);
        myClipboard.setPrimaryClip(myClip);
    }
}
