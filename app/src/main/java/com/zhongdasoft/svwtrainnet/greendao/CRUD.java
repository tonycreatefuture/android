package com.zhongdasoft.svwtrainnet.greendao;

import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.Cache.TestKey;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserFavorite;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserPaper;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserQuestion;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserQuestionOption;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserSearchHistory;
import com.zhongdasoft.svwtrainnet.greendao.gen.DaoSession;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserFavoriteDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserMenuDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserPaperDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserQuestionDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserQuestionOptionDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserSearchHistoryDao;
import com.zhongdasoft.svwtrainnet.util.StringUtil;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/8 13:59
 * 修改人：Administrator
 * 修改时间：2016/11/8 13:59
 * 修改备注：
 */

public class CRUD {
    private static CRUD mInstance;

    private CRUD() {

    }

    public static CRUD getInstance() {
        if (mInstance == null) {
            mInstance = new CRUD();
        }
        return mInstance;
    }

    /**
     * 更新用户问题
     *
     * @author tony
     * created at 2016/11/11 11:47
     */
    public void UpdateUserQuestion(UserQuestion userQuestion) {
        UserQuestionDao userQuestionDao = GreenDaoManager.getInstance().getSession().getUserQuestionDao();
        userQuestionDao.update(userQuestion);
    }

    /**
     * 新增搜索数据
     *
     * @author tony
     * created at 2016/11/9 17:57
     */
    public void InsertUserSearchHistory(UserSearchHistory userSearchHistory) {
        UserSearchHistoryDao userSearchHistoryDao = GreenDaoManager.getInstance().getSession().getUserSearchHistoryDao();
        userSearchHistoryDao.insert(userSearchHistory);
    }

    /**
     * 删除搜索数据
     *
     * @author tony
     * created at 2016/11/11 15:03
     */
    public void DeleteUserSearchHistory(List<UserSearchHistory> userSearchHistoryList) {
        UserSearchHistoryDao userSearchHistoryDao = GreenDaoManager.getInstance().getSession().getUserSearchHistoryDao();
        userSearchHistoryDao.deleteInTx(userSearchHistoryList);
    }

    /**
     * 新增用户常用菜单
     *
     * @author tony
     * created at 2016/11/9 15:21
     */
    public void InsertUserFavorite(String userName, String curMenu, Boolean isSaved) {
        UserFavoriteDao userFavoriteDao = GreenDaoManager.getInstance().getSession().getUserFavoriteDao();
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUserName(userName);
        userFavoriteDao.insert(userFavorite);
    }

    /**
     * 新增用户考试信息
     *
     * @author tony
     * created at 2016/11/9 17:28
     */
    public void InsertUserPaper(List<UserPaper> userPaperList) {
        for (UserPaper up : userPaperList) {
            InsertUserQuestion(up.getDbName());
        }
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserPaperDao userPaperDao = daoSession.getUserPaperDao();
        userPaperDao.insertInTx(userPaperList);
    }

    /**
     * 更新用户考试信息
     *
     * @author tony
     * created at 2016/11/9 10:34
     */
    public void UpdateUserPaper(UserPaper userPaper) {
        UserPaperDao userPaperDao = GreenDaoManager.getInstance().getSession().getUserPaperDao();
        List<UserPaper> userPaperList;
        if (!StringUtil.isNullOrEmpty(userPaper.getDbName())) {
            WhereCondition dbNameWhere = UserPaperDao.Properties.DbName.like(userPaper.getDbName());
            userPaperList = userPaperDao.queryBuilder().where(dbNameWhere).list();
        } else {
            userPaperList = userPaperDao.queryBuilder().list();
        }
        for (UserPaper up : userPaperList) {
            up.setStatus(userPaper.getStatus());
            up.setRealBeginTime(null == userPaper.getRealBeginTime() ? up.getRealBeginTime() : userPaper.getRealBeginTime());
            up.setRealEndTime(null == userPaper.getRealEndTime() ? up.getRealEndTime() : userPaper.getRealEndTime());
            up.setScore(null == userPaper.getScore() ? up.getScore() : userPaper.getScore());
            up.setScoreInfo(null == userPaper.getScoreInfo() ? up.getScoreInfo() : userPaper.getScoreInfo());
        }
        userPaperDao.updateInTx(userPaperList);
    }

    /**
     * 异步更新用户考试的剩余时间
     *
     * @author tony
     * created at 2016/11/9 10:33
     */
    public void UpdateUserPaperAsyn(UserPaper userPaper) {
        final UserPaperDao userPaperDao = GreenDaoManager.getInstance().getSession().getUserPaperDao();
        WhereCondition dbNameWhere = UserPaperDao.Properties.DbName.like(userPaper.getDbName());
        WhereCondition statusWhere = UserPaperDao.Properties.Status.notEq(TestKey.TEST_FINISHED);
        final List<UserPaper> userPaperList = userPaperDao.queryBuilder().where(dbNameWhere, statusWhere).list();
        for (UserPaper up : userPaperList) {
            up.setStatus(TestKey.TEST_RUNNING);
            up.setLeftTime(userPaper.getLeftTime());
        }
        GreenDaoManager.getInstance().getSession().startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                userPaperDao.updateInTx(userPaperList);
            }
        });

    }

    /**
     * 更新用户常用功能
     *
     * @author tony
     * created at 2016/11/11 18:00
     */
    public void UpdateUserFavorite(String userName, String menuName, Boolean saved, Boolean unSaved) {
        UserFavoriteDao userFavoriteDao = GreenDaoManager.getInstance().getSession().getUserFavoriteDao();
        WhereCondition userNameWhere = UserFavoriteDao.Properties.UserName.like(userName);
        WhereCondition menuNameWhere = UserFavoriteDao.Properties.MenuName.eq(menuName);
        WhereCondition fixedWhere = UserFavoriteDao.Properties.Fixed.eq(false);
        List<UserFavorite> userFavoriteList = userFavoriteDao.queryBuilder().where(userNameWhere, menuNameWhere, fixedWhere).list();
        for (UserFavorite uf : userFavoriteList) {
            uf.setSaved(null == saved ? uf.getSaved() : saved);
            uf.setTmpSaved(null == unSaved ? uf.getTmpSaved() : unSaved);
        }
        userFavoriteDao.updateInTx(userFavoriteList);
    }

    /**
     * 更新用户常用功能
     *
     * @author tony
     * created at 2016/11/12 9:31
     */
    public void UpdateUserFavorite(String userName, Boolean saved) {
        UserFavoriteDao userFavoriteDao = GreenDaoManager.getInstance().getSession().getUserFavoriteDao();
        WhereCondition userNameWhere = UserFavoriteDao.Properties.UserName.like(userName);
        WhereCondition fixedWhere = UserFavoriteDao.Properties.Fixed.eq(false);
        List<UserFavorite> userFavoriteList = userFavoriteDao.queryBuilder().where(userNameWhere, fixedWhere).list();
        for (UserFavorite uf : userFavoriteList) {
            if (saved) {
                if (uf.getTmpSaved()) {
                    uf.setSaved(true);
                    uf.setTmpSaved(false);
                } else {
                    uf.setSaved(false);
                }
            } else {
                uf.setTmpSaved(false);
            }
        }
        userFavoriteDao.updateInTx(userFavoriteList);
    }

    /**
     * 新增用户常用功能
     *
     * @author tony
     * created at 2016/11/11 17:33
     */
    public void InitUserFavorite(String userName, String fixMenu) {
        UserMenuDao userMenuDao = GreenDaoManager.getInstance().getSession().getUserMenuDao();
        WhereCondition isValidWhere = UserMenuDao.Properties.IsValid.eq(1);
        List<UserMenu> userMenuList = userMenuDao.queryBuilder().where(isValidWhere).list();
        UserFavoriteDao userFavoriteDao = GreenDaoManager.getInstance().getSession().getUserFavoriteDao();
        UserFavorite userFavorite;
        List<UserFavorite> userFavoriteList = new ArrayList<>();
        for (UserMenu um : userMenuList) {
            userFavorite = new UserFavorite();
            userFavorite.setUserName(userName);
            userFavorite.setMenuName(um.getActivityName());
            if (fixMenu.equals(um.getActivityName())) {
                userFavorite.setFixed(true);
                userFavorite.setSaved(true);
            } else {
                userFavorite.setFixed(false);
                userFavorite.setSaved(false);
            }
            userFavorite.setTmpSaved(false);
            userFavoriteList.add(userFavorite);
        }
        userFavoriteDao.insertInTx(userFavoriteList);
    }


    /**
     * 更新用户菜单
     *
     * @author tony
     * created at 2016/11/11 15:43
     */
    public void UpdateUserMenu(UserMenu UserMenu) {
        UserMenuDao userMenuDao = GreenDaoManager.getInstance().getSession().getUserMenuDao();
        userMenuDao.update(UserMenu);
    }

    /**
     * 初始化用户菜单
     *
     * @author tony
     * created at 2016/11/9 10:34
     */
    public void InitUserMenu(Resources res) {
        int[] resId = {R.string.TrainNormal, R.string.TrainExtra, R.string.TrainInner, R.string.TrainOnline, R.string.TrainNetTest, R.string.OnlineTv, R.string.Meeting, R.string.TrainPlan, R.string.CourseList, R.string.TrainApply, R.string.TrainCallBack, R.string.TrainScore, R.string.PersonSettings, R.string.AboutUs, R.string.FuncAdd};
        int[] drawableId = {R.drawable.trainnet_train_normal, R.drawable.trainnet_train_extra, R.drawable.trainnet_train_inner, R.drawable.trainnet_train_online, R.drawable.trainnet_train_nettest, R.drawable.trainnet_online_tv, R.drawable.trainnet_train_live, R.drawable.trainnet_train_extra, R.drawable.trainnet_normal_courselist, R.drawable.trainnet_normal_apply, R.drawable.trainnet_normal_callback, R.drawable.trainnet_normal_score, R.drawable.trainnet_more_personsettings, R.drawable.trainnet_more_aboutus, R.drawable.trainnet_train_funcadd};
        String[] clazz = {ActivityKey.Normal, ActivityKey.Extra, ActivityKey.Inner, ActivityKey.OnLine, ActivityKey.Exam, ActivityKey.OnLineTV, ActivityKey.Meeting, ActivityKey.Plan, ActivityKey.CourseList, ActivityKey.Apply, ActivityKey.CallBack, ActivityKey.Score, ActivityKey.Setting, ActivityKey.AboutUs, ActivityKey.Add};
        String[] p_clazz = {ActivityKey.MainTrain, ActivityKey.MainTrain, ActivityKey.MainTrain, ActivityKey.MainTrain, ActivityKey.MainTrain, ActivityKey.MainTrain, ActivityKey.MainTrain, ActivityKey.MainTrain, ActivityKey.Normal, ActivityKey.Normal, ActivityKey.Normal, ActivityKey.Normal, ActivityKey.MainMore, ActivityKey.MainMore, ActivityKey.MainFavorite};
        int[] level = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, -1};
        int[] isLeaf = {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        int[] isValid = {1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1};
        int[] state = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        UserMenuDao userMenuDao = GreenDaoManager.getInstance().getSession().getUserMenuDao();
        userMenuDao.deleteAll();
        List<UserMenu> userMenuList = new ArrayList<>();
        UserMenu userMenu;
        for (int i = 0; i < resId.length; i++) {
            userMenu = new UserMenu();
            userMenu.setResName(res.getString(resId[i]));
            userMenu.setDrawableName(res.getResourceName(drawableId[i]));
            userMenu.setActivityName(clazz[i]);
            userMenu.setPActivityName(p_clazz[i]);
            userMenu.setLevel(level[i]);
            userMenu.setIsLeaf(isLeaf[i]);
            userMenu.setIsValid(isValid[i]);
            userMenu.setState(state[i]);
            userMenuList.add(userMenu);
        }
        userMenuDao.insertInTx(userMenuList);
    }

    private void InsertUserQuestion(String dbName) {
        Database db = GreenDaoManager.getInstance().getReadableDatabase(dbName);
        StringBuilder qSql = new StringBuilder();
        qSql.append("select a.qid,paperid,qorder,essaymark,chapterid,courseid,level,qtype,content,aid,answercontent,refermark");
        qSql.append(" from exam_paper_question a left join exam_questions b on a.qid=b.qid left join exam_questionAnswers c  on b.qid=c.qid");
        String[] qParams = {};
        Cursor qCursor = db.rawQuery(qSql.toString(), qParams);
        StringBuilder qoSql = new StringBuilder();
        qoSql.append("select qid,optionid,content from exam_questionOptions where qid=?");
        Cursor qoCursor;
        List<UserQuestionOption> uqoList;
        List<List<UserQuestionOption>> uqoAllList = new ArrayList<>();
        List<UserQuestion> uqList = new ArrayList<>();
        UserQuestion uq;
        UserQuestionOption uqo;
        while (qCursor.moveToNext()) {
            uq = new UserQuestion();
            uq.setQID(qCursor.getString(0));
            uq.setPaperID(qCursor.getString(1));
            uq.setQOrder(qCursor.getString(2));
            uq.setEssayMark(qCursor.getString(3));
            uq.setDbName(dbName);
            uq.setCheckSum(0);

            uq.setChapterID(qCursor.getString(4));
            uq.setCourseID(qCursor.getString(5));
            uq.setLevel(qCursor.getInt(6));
            uq.setQType(qCursor.getInt(7));
            uq.setContent(qCursor.getString(8));
            uq.setAID(qCursor.getString(9));
            uq.setAnswerContent(qCursor.getString(10));
            uq.setReferMark(qCursor.getString(11));
            uqoList = new ArrayList<>();
            qoCursor = db.rawQuery(qoSql.toString(), new String[]{uq.getQID()});
            while (qoCursor.moveToNext()) {
                uqo = new UserQuestionOption();
                uqo.setQID(qoCursor.getString(0));
                uqo.setOptionID(qoCursor.getString(1));
                uqo.setContent(qoCursor.getString(2));
                uqoList.add(uqo);
            }
            qoCursor.close();
            uqoAllList.add(uqoList);
            uqList.add(uq);
        }
        qCursor.close();
        GreenDaoManager.getInstance().closeReadableDatabase(db);

        UserQuestionDao userQuestionDao = GreenDaoManager.getInstance().getSession().getUserQuestionDao();
        UserQuestionOptionDao userQuestionOptionDao = GreenDaoManager.getInstance().getSession().getUserQuestionOptionDao();
        Long key;
        for (int i = 0; i < uqList.size(); i++) {
            key = userQuestionDao.insert(uqList.get(i));
            for (UserQuestionOption uqoObj : uqoAllList.get(i)) {
                uqoObj.setUqId(key);
            }
            userQuestionOptionDao.insertInTx(uqoAllList.get(i));
        }
    }

//    /**
//     * 删除未开始的考试
//     *
//     * @author tony
//     * @time 2016/12/22 11:54
//     **/
//    public void DeleteUserQuestion(String dbName) {
//        UserQuestionDao userQuestionDao = GreenDaoManager.getInstance().getSession().getUserQuestionDao();
//        UserQuestionOptionDao userQuestionOptionDao = GreenDaoManager.getInstance().getSession().getUserQuestionOptionDao();
//
//        List<UserQuestion> userQuestionList = DaoQuery.getInstance().getUserQuestionList(dbName);
//        List<UserQuestionOption> userQuestionOptionList = DaoQuery.getInstance().getUserQuestionOptionList(userQuestionList.get(0).getUqId());
//        for (UserQuestionOption uqo : userQuestionOptionList) {
//            userQuestionOptionDao.delete(uqo);
//        }
//        userQuestionDao.delete(userQuestionList.get(0));
//    }

}
