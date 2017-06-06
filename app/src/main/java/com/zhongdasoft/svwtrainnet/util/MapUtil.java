package com.zhongdasoft.svwtrainnet.util;

import android.content.Context;

import com.zhongdasoft.svwtrainnet.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapUtil {
    public static Map<String, String> sortMapByKey(Map<String, String> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, String> sortedMap = new TreeMap<>(
                new Comparator<String>() {
                    public int compare(String key1, String key2) {
                        int intKey1, intKey2;
                        try {
                            intKey1 = getInt(key1);
                            intKey2 = getInt(key2);
                        } catch (Exception e) {
                            intKey1 = 0;
                            intKey2 = 0;
                        }
                        return intKey1 - intKey2;
                    }
                });
        sortedMap.putAll(oriMap);
        return sortedMap;
    }

    public static Map<Integer, Integer> sortMapByIntegerKey1(
            Map<Integer, Integer> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<Integer, Integer> sortedMap = new TreeMap<>(
                new Comparator<Integer>() {
                    public int compare(Integer key1, Integer key2) {
                        int intKey1, intKey2;
                        try {
                            intKey1 = key1;
                            intKey2 = key2;
                        } catch (Exception e) {
                            intKey1 = 0;
                            intKey2 = 0;
                        }
                        return intKey1 - intKey2;
                    }
                });
        sortedMap.putAll(oriMap);
        return sortedMap;
    }

    public static Map<Integer, String> sortMapByIntegerKey2(
            Map<Integer, String> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<Integer, String> sortedMap = new TreeMap<>(
                new Comparator<Integer>() {
                    public int compare(Integer key1, Integer key2) {
                        int intKey1, intKey2;
                        try {
                            intKey1 = key1;
                            intKey2 = key2;
                        } catch (Exception e) {
                            intKey1 = 0;
                            intKey2 = 0;
                        }
                        return intKey1 - intKey2;
                    }
                });
        sortedMap.putAll(oriMap);
        return sortedMap;
    }

    private static int getInt(String str) {
        int i = 0;
        try {
            Pattern p = Pattern.compile("^\\d+");
            Matcher m = p.matcher(str);
            if (m.find()) {
                i = Integer.valueOf(m.group());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static Map<String, String> sortMapByValue(Map<String, String> oriMap) {
        Map<String, String> sortedMap = new LinkedHashMap<>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<String, String>> entryList = new ArrayList<>(
                    oriMap.entrySet());
            Collections.sort(entryList,
                    new Comparator<Map.Entry<String, String>>() {
                        public int compare(Entry<String, String> entry1,
                                           Entry<String, String> entry2) {
                            int value1, value2;
                            try {
                                value1 = getInt(entry1.getValue());
                                value2 = getInt(entry2.getValue());
                            } catch (NumberFormatException e) {
                                value1 = 0;
                                value2 = 0;
                            }
                            return value2 - value1;
                        }
                    });
            Iterator<Map.Entry<String, String>> iterator = entryList.iterator();
            Map.Entry<String, String> tmpEntry;
            while (iterator.hasNext()) {
                tmpEntry = iterator.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }

    public static Map<Integer, String> sortMapByStringValue(
            Map<Integer, String> oriMap) {
        Map<Integer, String> sortedMap = new LinkedHashMap<>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<Integer, String>> entryList = new ArrayList<>(
                    oriMap.entrySet());
            Collections.sort(entryList,
                    new Comparator<Map.Entry<Integer, String>>() {
                        public int compare(Entry<Integer, String> entry1,
                                           Entry<Integer, String> entry2) {
                            int value1, value2;
                            try {
                                value1 = getInt(entry1.getValue());
                                value2 = getInt(entry2.getValue());
                            } catch (NumberFormatException e) {
                                value1 = 0;
                                value2 = 0;
                            }
                            return value2 - value1;
                        }
                    });
            Iterator<Map.Entry<Integer, String>> iterator = entryList.iterator();
            Map.Entry<Integer, String> tmpEntry;
            while (iterator.hasNext()) {
                tmpEntry = iterator.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }

    public static Map<Integer, Integer> sortMapByIntegerValue(
            Map<Integer, Integer> oriMap) {
        Map<Integer, Integer> sortedMap = new LinkedHashMap<>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(
                    oriMap.entrySet());
            Collections.sort(entryList,
                    new Comparator<Map.Entry<Integer, Integer>>() {
                        public int compare(Entry<Integer, Integer> entry1,
                                           Entry<Integer, Integer> entry2) {
                            int value1, value2;
                            try {
                                value1 = entry1.getValue();
                                value2 = entry2.getValue();
                            } catch (NumberFormatException e) {
                                value1 = 0;
                                value2 = 0;
                            }
                            return value2 - value1;
                        }
                    });
            Iterator<Map.Entry<Integer, Integer>> iterator = entryList.iterator();
            Map.Entry<Integer, Integer> tmpEntry;
            while (iterator.hasNext()) {
                tmpEntry = iterator.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }

    public static String getString(Context context, Map<String, Object> map) {
        StringBuilder sbStr = new StringBuilder();
        int i = 0;
        for (String key : map.keySet()) {
            if (context.getResources().getString(R.string.SoapObjectTypeName).equals(map.get(key).getClass()
                    .getName())) {
                continue;
            }
            if (map.get(key) == null) {
                continue;
            }
            if (i > 0) {
                sbStr.append(",,");
            }
            sbStr.append(key).append(",,").append(map.get(key));
            i++;
        }
        return sbStr.toString();
    }
}
