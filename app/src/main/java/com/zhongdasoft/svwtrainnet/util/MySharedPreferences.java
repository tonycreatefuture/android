package com.zhongdasoft.svwtrainnet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zhongdasoft.svwtrainnet.TrainNetApp;

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

    private void setSharedPreferences() {
        sharedPreferences = TrainNetApp.getContext().getSharedPreferences("userInfo",
                Context.MODE_PRIVATE);
        paramMap = (Map<String, Object>) sharedPreferences.getAll();
    }

    public String getString(String key) {
        setSharedPreferences();
        if (paramMap.containsKey(key)) {
            if (paramMap.get(key) != null) {
                return paramMap.get(key).toString();
            }
            return "";
        }
        return "";
    }

    public void setStoreString(String key, String value) {
        setSharedPreferences();
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void removeString(String key) {
        setSharedPreferences();
        Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void removeAll() {
        setSharedPreferences();
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getAccessToken() {
        return instance.getString("AccessToken");
    }

    public String getUserName() {
        return instance.getString("userName");
    }

    public String getName() {
        return instance.getString("Name");
    }

    public String getCurrentTime() {
        String timer = instance.getString("countTimer");
        if (StringUtil.isNullOrEmpty(timer)) {
            timer = "0";
            instance.setStoreString("countTimer", timer);
        }
        String currentTime = instance.getString("currentTime");
        currentTime = currentTime.replace("T", " ");
        Calendar cal = Calendar.getInstance();
        cal.setTime(StringUtil.strToDate(currentTime));
        cal.add(Calendar.SECOND, Integer.parseInt(timer));
        String value = StringUtil.dateToStr(cal.getTime());
        return value;
    }

    public String getPost() {
        return instance.getString("post");
    }

    public String getDealer() {
        return instance.getString("DealerName");
    }

}
