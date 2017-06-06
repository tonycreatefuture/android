package com.zhongdasoft.svwtrainnet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/13 11:09
 * 修改人：Administrator
 * 修改时间：2016/7/13 11:09
 * 修改备注：
 */
public class CollectionUtil {
    public static final int IntegerType = 100;
    public static final int StringType = 101;
    public static final int DateType = 102;
    public static final int OrderAsc = 200;
    public static final int OrderDesc = 201;

    public static void sortString(ArrayList<HashMap<String, Object>> data, final String keyName, final int orderDesc) {
        Collections.sort(data, new Comparator<HashMap>() {
            public int compare(HashMap o1, HashMap o2) {
                String str1, str2;
                str1 = StringUtil.objectToStr(o1.get(keyName));
                str2 = StringUtil.objectToStr(o2.get(keyName));
                switch (orderDesc) {
                    case OrderAsc:
                        return str1.compareTo(str2);
                    case OrderDesc:
                        return str2.compareTo(str1);
                }
                return 1;
            }
        });
    }

    public static void sortString(ArrayList<HashMap<String, Object>> data, final String keyName1, final int order1, final String keyName2, final int order2) {
        Collections.sort(data, new Comparator<HashMap>() {
            public int compare(HashMap o1, HashMap o2) {
                String str1, str2;
                str1 = StringUtil.objectToStr(o1.get(keyName1));
                str2 = StringUtil.objectToStr(o2.get(keyName1));
                switch (order1) {
                    case OrderAsc:
                        return str1.compareTo(str2);
                    case OrderDesc:
                        return str2.compareTo(str1);
                }
                return 1;
            }
        });

        Collections.sort(data, new Comparator<HashMap>() {
            public int compare(HashMap o1, HashMap o2) {
                String strP1, strP2, str1, str2;
                strP1 = StringUtil.objectToStr(o1.get(keyName1));
                strP2 = StringUtil.objectToStr(o2.get(keyName1));
                str1 = StringUtil.objectToStr(o1.get(keyName2));
                str2 = StringUtil.objectToStr(o2.get(keyName2));
                if (strP1.equals(strP2)) {
                    switch (order2) {
                        case OrderAsc:
                            return str1.compareTo(str2);
                        case OrderDesc:
                            return str2.compareTo(str1);
                    }
                } else {
                    switch (order1) {
                        case OrderAsc:
                            return strP1.compareTo(strP2);
                        case OrderDesc:
                            return strP2.compareTo(strP1);
                    }
                }
                return 1;
            }
        });
    }
}
