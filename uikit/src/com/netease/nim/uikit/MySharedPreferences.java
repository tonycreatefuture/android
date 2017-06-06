package com.netease.nim.uikit;

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
}
