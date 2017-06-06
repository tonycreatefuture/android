package com.zhongdasoft.svwtrainnet.util;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;

import org.xml.sax.XMLReader;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/6 16:31
 * 修改人：Administrator
 * 修改时间：2016/7/6 16:31
 * 修改备注：
 */
public class HtmlUtil {
    public static String deleteHtml(String source) {
        if (StringUtil.isNullOrEmpty(source)) {
            return "";
        }
        String tmpResult = Html.fromHtml(source, null, new TagHandler()).toString();
        while (!tmpResult.equals(source)) {
//            Log.d("HtmlUtil", "source=" + source + "tmpResult=" + tmpResult);
            source = tmpResult;
            tmpResult = Html.fromHtml(source, null, new TagHandler()).toString();
            if (!StringUtil.isNullOrEmpty(source) && StringUtil.isNullOrEmpty(tmpResult)) {
                tmpResult = source;
                break;
            }
        }
        return tmpResult;
    }

    public static Spanned fromHtml(String source) {
        if (StringUtil.isNullOrEmpty(source)) {
            return null;
        }
        return Html.fromHtml(source, null, new TagHandler());
    }

    static class TagHandler implements Html.TagHandler {

        public TagHandler() {
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
//            Log.d("HtmlUtil", "tag=" + tag + "output=" + output.toString());
            if (!"html".equalsIgnoreCase(tag) && !"body".equalsIgnoreCase(tag) && ChineseToEnglish.isChineseCharacter(tag)) {
                if (opening) {
                    output.append("<").append(tag).append(">");
                }
            }
        }
    }
}
