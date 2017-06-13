package com.zhongdasoft.svwtrainnet.util;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhongdasoft.svwtrainnet.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.Iterator;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/14 17:50
 * 修改人：tony
 * 修改时间：2016/12/14 17:50
 * 修改备注：
 */

public class JsonUtil {

    public static String xml2JSON(String xml) {
        try {
            JSONObject obj = XML.toJSONObject(xml);
            return obj.toString();
        } catch (JSONException e) {
            System.err.println("xml->json失败" + e.getLocalizedMessage());
            return "";
        }
    }

    public static String xml2JSON(String xml, String xmlPrefix, String xmlPostfix, String listKey) {
        try {
            JSONObject obj = XML.toJSONObject(xml.replace(xmlPrefix, "").replace(xmlPostfix, ""));
            if (!StringUtil.isNullOrEmpty(listKey)) {
                Iterator<?> it = obj.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    if (key.equals(listKey)) {
                        String value = obj.getString(key);
                        obj.remove(key);
                        obj.accumulate(key, "[" + value + "]");
                    } else {
                        it = obj.getJSONObject(key).keys();
                    }
                }
            }
            return obj.toString();
        } catch (JSONException e) {
            System.err.println("xml->json失败" + e.getLocalizedMessage());
            return "";
        }
    }

    public static JsonObject getJsonObject(Context context, String xml, String iName, String listKey) {
        String xmlPrefix = context.getResources().getString(R.string.xmlPrefix, iName);
        String xmlPostfix = context.getResources().getString(R.string.xmlPostfix, iName);
        String json = xml2JSON(xml, xmlPrefix, xmlPostfix, listKey);
        JsonParser parse = new JsonParser();
        JsonObject jo = parse.parse(json).getAsJsonObject();
        return jo;
    }

//    public static String getWizlongJsonString(Context context, String data) {
//        String userId = MySharedPreferences.getInstance().getString("userName", context);
//        String name = MySharedPreferences.getInstance().getString("Name", context);
//        String position = MySharedPreferences.getInstance().getString("post", context);
//        String dealer = MySharedPreferences.getInstance().getString("DealerName", context);
//
//        com.alibaba.fastjson.JSONObject jsonData = new com.alibaba.fastjson.JSONObject();
//        jsonData.put("headParams", data);
//        jsonData.put("userid", userId);
//        jsonData.put("name", name);
//        jsonData.put("position", position);
//        jsonData.put("dealer", dealer);
//
//        return jsonData.toString();
//    }
}
