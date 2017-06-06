package com.zhongdasoft.svwtrainnet.greendao;

import android.content.Context;

import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.greendao.gen.DaoMaster;
import com.zhongdasoft.svwtrainnet.greendao.gen.DaoSession;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/8 9:54
 * 修改人：Administrator
 * 修改时间：2016/11/8 9:54
 * 修改备注：
 */

public class GreenDaoManager {
    private final static String defaultDBName = "local-db";
    private static GreenDaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;


    private GreenDaoManager() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(TrainNetApp.getContext(), defaultDBName, null);
        DaoMaster mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new GreenDaoManager();
        }
        return mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }

    public Database getReadableDatabase(String dbName) {
        OpenReadHelper openReadHelper = new OpenReadHelper(TrainNetApp.getContext(), dbName, DaoMaster.SCHEMA_VERSION);
        return openReadHelper.getReadableDb();
    }

    public void closeReadableDatabase(Database db) {
        if (null != db) {
            db.close();
        }
    }

    class OpenReadHelper extends DatabaseOpenHelper {

        public OpenReadHelper(Context context, String name, int version) {
            super(context, name, version);
        }
    }
}
