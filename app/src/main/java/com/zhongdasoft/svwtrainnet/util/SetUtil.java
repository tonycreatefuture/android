package com.zhongdasoft.svwtrainnet.util;

import java.util.HashSet;
import java.util.TreeSet;

public class SetUtil {
    public static TreeSet<String> HashSet2TreeSet(HashSet<String> hs) {
        if (hs == null)
            return null;
        final TreeSet<String> ts = new TreeSet<>(hs);
        ts.comparator();
        return ts;
    }

}
