package com.zhongdasoft.svwtrainnet.util;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/5/26 10:24
 * 修改人：Administrator
 * 修改时间：2016/5/26 10:24
 * 修改备注：
 */
public class ClassUtil {

    private static ClassUtil instance;

    private ClassUtil() {
    }

    public synchronized static ClassUtil getInstance() {
        if (null == instance) {
            instance = new ClassUtil();
        }
        return instance;
    }

    public Class getClass(String activityName) {
        Class c;
        try {
            c = Class.forName(activityName);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            c = BaseActivity.class;
        }
        return c;
    }
}
