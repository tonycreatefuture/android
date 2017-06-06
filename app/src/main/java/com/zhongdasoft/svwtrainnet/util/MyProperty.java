package com.zhongdasoft.svwtrainnet.util;

import java.io.InputStream;
import java.util.Properties;

public class MyProperty {

    public static long MaxValue = 10000000;
    public static String PropFile = "/assets/config.properties";

    public static Properties loadConfig() {
        Properties properties = new Properties();
        try {
            InputStream s = MyProperty.class.getResourceAsStream(PropFile);
            properties.load(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String getCurrentValue(String key) {
        String PublishMode = loadConfig().getProperty("PublishMode");
        String Formal = loadConfig().getProperty("Formal");
        String Test = loadConfig().getProperty("Test");
        if ("0".equals(PublishMode)) {
            return Test + key;
        } else {
            return Formal + key;
        }
    }

    public static String getVersion() {
        String PublishMode = loadConfig().getProperty("PublishMode");
        if ("0".equals(PublishMode)) {
            return "测试版";
        } else {
            return "";
        }
    }

    public static boolean isPublished() {
        String PublishMode = loadConfig().getProperty("PublishMode");
        if ("0".equals(PublishMode)) {
            return false;
        } else {
            return true;
        }
    }

    public static String getValueByKey(String key) {
        String value = loadConfig().getProperty(key);
        return value;
    }

    public static String getValueByKey(String key, String defaultValue) {
        String value = loadConfig().getProperty(key);
        if (StringUtil.isNullOrEmpty(value)) {
            return defaultValue;
        }
        return value;
    }
}
