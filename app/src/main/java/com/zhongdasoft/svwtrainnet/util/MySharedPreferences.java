package com.zhongdasoft.svwtrainnet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Calendar;
import java.util.Map;

public class MySharedPreferences {
    private SharedPreferences sharedPreferences;
    private Map<String, Object> paramMap;

    private static MySharedPreferences instance;

    private MySharedPreferences() {
    }

    /**
     * 单例模式
     */
    public synchronized static MySharedPreferences getInstance() {
        if (null == instance) {
            instance = new MySharedPreferences();
        }
        return instance;
    }

    private void setSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("userInfo",
                Context.MODE_PRIVATE);
        paramMap = (Map<String, Object>) sharedPreferences.getAll();
    }

    public String getString(String key, Context context) {
        setSharedPreferences(context);
        if (paramMap.containsKey(key)) {
            if (paramMap.get(key) != null) {
                return paramMap.get(key).toString();
            }
            return "";
        }
        return "";
    }

    public void setStoreString(String key, String value,
                               Context context) {
        setSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void removeString(String key, Context context) {
        setSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void removeAll(Context context) {
        setSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getAccessToken(Context context) {
        return instance.getString("AccessToken", context);
    }

    public String getUserName(Context context) {
        return instance.getString("userName", context);
    }

    public String getName(Context context) {
        return instance.getString("Name", context);
    }

    public String getCurrentTime(Context context) {
        String timer = instance.getString("countTimer", context);
        if (StringUtil.isNullOrEmpty(timer)) {
            timer = "0";
            instance.setStoreString("countTimer", timer, context);
        }
        String currentTime = instance.getString("currentTime", context);
        currentTime = currentTime.replace("T", " ");
        Calendar cal = Calendar.getInstance();
        cal.setTime(StringUtil.strToDate(currentTime));
        cal.add(Calendar.SECOND, Integer.parseInt(timer));
        String value = StringUtil.dateToStr(cal.getTime());
        return value;
    }

    public String getPost(Context context) {
        return instance.getString("post", context);
    }

    public String getDealer(Context context) {
        return instance.getString("DealerName", context);
    }

}
