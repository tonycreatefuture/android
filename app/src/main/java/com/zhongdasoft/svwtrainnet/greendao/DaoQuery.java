package com.zhongdasoft.svwtrainnet.greendao;

import com.zhongdasoft.svwtrainnet.greendao.Cache.TestKey;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserFavorite;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserPaper;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserQuestion;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserQuestionOption;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserSearchHistory;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserFavoriteDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserMenuDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserPaperDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserQuestionDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserQuestionOptionDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserSearchHistoryDao;
import com.zhongdasoft.svwtrainnet.util.StringUtil;

import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/8 13:58
 * 修改人：Administrator
 * 修改时间：2016/11/8 13:58
 * 修改备注：
 */

public class DaoQuery {
    private static DaoQuery mInstance;

    private DaoQuery() {

    }

    public static DaoQuery getInstance() {
        if (mInstance == null) {
            mInstance = new DaoQuery();
        }
        return mInstance;
    }

    /**
     * 返回用户考试列表
     *
     * @author tony
     * created at 2016/11/9 13:24
     */
    public List<UserPaper> getUserPaperList(String userName, Boolean isFinished) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserPaperDao().queryBuilder();
        WhereCondition userNameWhere = UserPaperDao.Properties.UserName.like(userName);
        WhereCondition statusWhere;
        if (!isFinished) {
            statusWhere = UserPaperDao.Properties.Status.le(1);
        } else {
            statusWhere = UserPaperDao.Properties.Status.ge(2);
        }
        qb.where(userNameWhere, statusWhere);
        return qb.orderAsc(UserPaperDao.Properties.BeginTime).list();
    }

    /**
     * 返回用户考试
     *
     * @author tony
     * created at 2016/11/11 11:16
     */
    public UserPaper getUserPaperByDB(String dbName) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserPaperDao().queryBuilder();
        qb.where(UserPaperDao.Properties.DbName.like(dbName));
        List<UserPaper> userPaperList = qb.orderAsc(UserPaperDao.Properties.BeginTime).list();
        return userPaperList.get(0);
    }

    /**
     * 根据db查找用户考试信息
     *
     * @author tony
     * created at 2016/11/10 14:27
     */
    public UserPaper findUserPaperByDB(String dbName) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserPaperDao().queryBuilder();
        qb.where(UserPaperDao.Properties.DbName.like(dbName));
        List<UserPaper> userPaperList = qb.list();
        if (userPaperList.size() >= 1) {
            return userPaperList.get(0);
        }
        return null;
    }

    /**
     * 根据key查找用户考试信息
     *
     * @author tony
     * created at 2016/11/10 14:12
     */
    public UserPaper findUserPaperByKey(Object key) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserPaperDao().queryBuilder();
        qb.where(UserPaperDao.Properties.Id.eq(key));
        List<UserPaper> userPaperList = qb.orderAsc(UserPaperDao.Properties.BeginTime).list();
        if (userPaperList.size() >= 1) {
            return userPaperList.get(0);
        }
        return null;
    }

    /**
     * 获取用户问题列表
     *
     * @author tony
     * created at 2016/11/11 10:33
     */
    public List<UserQuestion> getUserQuestionList(String dbName) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserQuestionDao().queryBuilder();
        qb.where(UserQuestionDao.Properties.DbName.eq(dbName)).orderAsc(UserQuestionDao.Properties.QType);
        List<UserQuestion> userQuestionList = qb.list();
        return userQuestionList;
    }

    /**
     * 获取用户问题选项列表
     *
     * @author tony
     * created at 2016/11/11 11:41
     */
    public List<UserQuestionOption> getUserQuestionOptionList(Long uqId) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserQuestionOptionDao().queryBuilder();
        qb.where(UserQuestionOptionDao.Properties.UqId.eq(uqId));
        List<UserQuestionOption> userQuestionOptionList = qb.list();
        return userQuestionOptionList;
    }

    /**
     * 获取用户答案
     *
     * @author tony
     * created at 2016/11/10 17:57
     */
    public String getUserQuestionAnswer(String dbName) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserQuestionDao().queryBuilder();
        qb.where(UserQuestionDao.Properties.DbName.eq(dbName));
        List<UserQuestion> userQuestionList = qb.list();
        StringBuilder sbUAnswer = new StringBuilder();
        for (UserQuestion uq : userQuestionList) {
            if (sbUAnswer.length() == 0) {
                sbUAnswer.append("[");
            } else {
                sbUAnswer.append(",");
            }
            sbUAnswer.append("{");
            sbUAnswer.append("\"qId\"").append(":\"").append(uq.getQID()).append("\",");
            sbUAnswer.append("\"answer\"").append(":\"");
            if (StringUtil.isNullOrEmpty(uq.getAContent())) {
                sbUAnswer.append("");
            } else {
                if (uq.getAContent().endsWith(",,")) {
                    sbUAnswer.append(uq.getAContent().substring(0, uq.getAContent().length() - 2));
                } else {
                    sbUAnswer.append(uq.getAContent());
                }
            }
            sbUAnswer.append("\"");
            sbUAnswer.append("}");
        }
        sbUAnswer.append("]");
        return sbUAnswer.toString();
    }

    /**
     * 获取用户总分
     *
     * @author tony
     * created at 2016/11/10 17:49
     */
    public Float getUserQuestionScore(String dbName) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserQuestionDao().queryBuilder();
        WhereCondition dbNameWhere = UserQuestionDao.Properties.DbName.eq(dbName);
        qb.where(dbNameWhere);
        Float allScore = 0f;
        List<UserQuestion> userQuestionList = qb.list();
        for (UserQuestion uq : userQuestionList) {
            if (StringUtil.isNullOrEmpty(uq.getAContent())) {
                continue;
            }
            if ((TestKey.MultiQuestionType != uq.getQType() && uq.getAContent().equals(uq.getAnswerContent()))
                    || (TestKey.MultiQuestionType == uq.getQType() && uq.getAContent().equals(uq.getAnswerContent() + ",,"))) {
                allScore += Float.parseFloat(uq.getEssayMark());
            }
        }
        return allScore;
    }

    /**
     * 获取用户成绩信息
     *
     * @author tony
     * created at 2016/11/10 17:14
     */
    public String getUserQuestionScoreInfo(String dbName) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserQuestionDao().queryBuilder();
        Query query;
        StringBuilder sbScoreInfo = new StringBuilder();
        int[] qTypes = {TestKey.SingleQuestionType, TestKey.MultiQuestionType, TestKey.JudgeQuestionType};
        int rightCount;
        int scorePerQuestion;
        List<UserQuestion> userQuestionList;
        WhereCondition qTypeWhere = UserQuestionDao.Properties.QType.eq(0);
        WhereCondition dbNameWhere = UserQuestionDao.Properties.DbName.eq(dbName);
        query = qb.where(qTypeWhere, dbNameWhere).orderAsc(UserQuestionDao.Properties.QType).build();
        sbScoreInfo.append("[");
        for (int qType : qTypes) {
            query.setParameter(0, qType);
            userQuestionList = query.list();
            if (sbScoreInfo.length() > 1) {
                sbScoreInfo.append(",");
            }
            rightCount = 0;
            sbScoreInfo.append("{\"all\":\"").append(userQuestionList.size()).append("\"");
            if (userQuestionList.size() > 0) {
                scorePerQuestion = Integer.parseInt(userQuestionList.get(0).getEssayMark());
                for (UserQuestion uq : userQuestionList) {
                    if (StringUtil.isNullOrEmpty(uq.getAContent())) {
                        continue;
                    }
                    if ((TestKey.MultiQuestionType != uq.getQType() && uq.getAContent().equals(uq.getAnswerContent()))
                            || (TestKey.MultiQuestionType == uq.getQType() && uq.getAContent().equals(uq.getAnswerContent() + ",,"))) {
                        rightCount++;
                    }
                }
                sbScoreInfo.append(",\"right\":\"").append(rightCount);
                sbScoreInfo.append("\",\"wrong\":\"").append(userQuestionList.size() - rightCount);
                sbScoreInfo.append("\",\"score\":\"").append(scorePerQuestion * rightCount).append("\"}");
            } else {
                sbScoreInfo.append(",\"right\":\"0\",\"wrong\":\"0\",\"score\":\"0\"}");
            }
        }
        sbScoreInfo.append("]");
        return sbScoreInfo.toString();
    }

    /**
     * 存在未下载用户考试文件
     *
     * @author tony
     * created at 2016/11/9 17:23
     */
    public boolean existNotDownloadUserPaper(UserPaper userPaper) {
        UserPaperDao userPaperDao = GreenDaoManager.getInstance().getSession().getUserPaperDao();
        WhereCondition userNameWhere = UserPaperDao.Properties.UserName.like(userPaper.getUserName());
        WhereCondition planIdWhere = UserPaperDao.Properties.PlanId.eq(userPaper.getPlanId());
        WhereCondition paperIdWhere = UserPaperDao.Properties.PaperId.eq(userPaper.getPaperId());
        List<UserPaper> userPaperList = userPaperDao.queryBuilder().where(userNameWhere, planIdWhere, paperIdWhere).list();
        if (userPaperList.size() == 0) {
            return true;
        }
        for (UserPaper up : userPaperList) {
            if (StringUtil.isNullOrEmpty(up.getRealBeginTime())) {
                if (!StringUtil.isNullOrEmpty(userPaper.getBeginTime()) && !userPaper.getBeginTime().equals(up.getBeginTime())) {
                    up.setBeginTime(userPaper.getBeginTime());
                }
                if (!StringUtil.isNullOrEmpty(userPaper.getEndTime()) && !userPaper.getEndTime().equals(up.getEndTime())) {
                    up.setEndTime(userPaper.getEndTime());
                }
                if (null != userPaper.getDuration() && !userPaper.getDuration().equals(up.getDuration())) {
                    up.setDuration(userPaper.getDuration());
                }
                userPaperDao.update(up);
            }
        }
        return false;
    }

    /**
     * 获取用户考试状态
     *
     * @param dbName 数据库
     * @author tony
     * created at 2016/11/9 13:25
     */
    public int getUserPaperStatus(String dbName) {
        WhereCondition dbNameWhere = UserPaperDao.Properties.DbName.like(dbName);
        List<UserPaper> userPaperList = GreenDaoManager.getInstance().getSession().getUserPaperDao().queryBuilder().where(dbNameWhere).build().list();
        if (userPaperList.size() >= 1) {
            return userPaperList.get(0).getStatus();
        }
        return TestKey.TEST_PREPARED;
    }

    /**
     * 获取问题数
     *
     * @param dbName 数据库
     * @author tony
     * created at 2016/11/9 13:25
     */
    public Long getQuestionCount(String dbName) {
        UserQuestionDao userQuestionDao = GreenDaoManager.getInstance().getSession().getUserQuestionDao();
        WhereCondition dbNameWhere = UserQuestionDao.Properties.DbName.like(dbName);
        return userQuestionDao.queryBuilder().where(dbNameWhere).count();
    }

    /**
     * 获取用户菜单数
     *
     * @author tony
     * created at 2016/11/9 14:00
     */
    public Long getUserMenuCount() {
        UserMenuDao userMenuDao = GreenDaoManager.getInstance().getSession().getUserMenuDao();
        return userMenuDao.queryBuilder().count();
    }

    /**
     * 获取用户菜单列表
     *
     * @author tony
     * created at 2016/11/9 14:17
     */
    public List<UserMenu> getUserMenuListByActivityName(String activityName, Boolean isParent) {
        UserMenuDao userMenuDao = GreenDaoManager.getInstance().getSession().getUserMenuDao();
        WhereCondition isValidWhere = UserMenuDao.Properties.IsValid.eq(1);
        if (!isParent) {
            WhereCondition activityNameWhere = UserMenuDao.Properties.ActivityName.eq(activityName);
            return userMenuDao.queryBuilder().where(activityNameWhere, isValidWhere).list();
        } else {
            WhereCondition pActivityNameWhere = UserMenuDao.Properties.PActivityName.eq(activityName);
            return userMenuDao.queryBuilder().where(pActivityNameWhere, isValidWhere).list();
        }
    }

    /**
     * 获取用户菜单
     *
     * @author tony
     * created at 2016/11/11 16:38
     */
    public UserMenu getUserMenuByResName(String resName) {
        UserMenuDao userMenuDao = GreenDaoManager.getInstance().getSession().getUserMenuDao();
        WhereCondition resNameWhere = UserMenuDao.Properties.ResName.eq(resName);
        List<UserMenu> userMenuList = userMenuDao.queryBuilder().where(resNameWhere).list();
        if (userMenuList.size() >= 1) {
            return userMenuList.get(0);
        }
        return null;
    }

    /**
     * 获取常用功能的用户菜单
     *
     * @author tony
     * created at 2016/11/9 17:02
     */
    public List<UserMenu> getUserMenuListOfFavorite(String userName, Integer level) {
        QueryBuilder qbUserMenu = GreenDaoManager.getInstance().getSession().getUserMenuDao().queryBuilder();
        Join userMenuJoin = qbUserMenu.join(UserMenuDao.Properties.ActivityName, UserFavorite.class, UserFavoriteDao.Properties.MenuName);
        WhereCondition userNameWhere = UserFavoriteDao.Properties.UserName.like(userName);
        userMenuJoin.where(userNameWhere);
        qbUserMenu.where(UserMenuDao.Properties.Level.eq(level));
        return qbUserMenu.list();
    }

    /**
     * 获取已保存用户自定义菜单
     *
     * @author tony
     * @time 2017/6/13 14:26
     **/
    public List<UserMenu> getSavedUserMenuListOfFavorite(String userName) {
        QueryBuilder qbUserMenu = GreenDaoManager.getInstance().getSession().getUserMenuDao().queryBuilder();
        Join userMenuJoin = qbUserMenu.join(UserMenuDao.Properties.ActivityName, UserFavorite.class, UserFavoriteDao.Properties.MenuName);
        WhereCondition userNameWhere = UserFavoriteDao.Properties.UserName.like(userName);
        WhereCondition savedWhere = UserFavoriteDao.Properties.Saved.eq(true);
        userMenuJoin.where(userNameWhere, savedWhere);
        return qbUserMenu.list();
    }

    /**
     * 存在用户常用功能
     *
     * @author tony
     * created at 2016/11/11 17:29
     */
    public boolean existUserFavorite(String userName) {
        QueryBuilder qb = GreenDaoManager.getInstance().getSession().getUserFavoriteDao().queryBuilder();
        qb.where(UserFavoriteDao.Properties.UserName.like(userName));
        return 0 == qb.count() ? false : true;
    }


    /**
     * 存在未保存的常用功能
     *
     * @author tony
     * created at 2016/11/9 17:09
     */
    public boolean existUnsavedUserFavorite(String userName) {
        UserFavoriteDao userFavoriteDao = GreenDaoManager.getInstance().getSession().getUserFavoriteDao();
        WhereCondition userNameWhere = UserFavoriteDao.Properties.UserName.like(userName);
        WhereCondition unsavedMenuWhere = UserFavoriteDao.Properties.TmpSaved.eq(true);
        Long count = userFavoriteDao.queryBuilder().where(userNameWhere, unsavedMenuWhere).count();
        return 0 == count ? false : true;
    }

    /**
     * 获取搜索列表
     *
     * @author tony
     * created at 2016/11/9 17:55
     */
    public List<UserSearchHistory> getUserSearchHistoryList(String userName, String useWhere) {
        UserSearchHistoryDao searchHistoryDao = GreenDaoManager.getInstance().getSession().getUserSearchHistoryDao();
        WhereCondition userNameWhere = UserSearchHistoryDao.Properties.UserName.eq(userName);
        WhereCondition useWhereWhere = UserSearchHistoryDao.Properties.UseWhere.eq(useWhere);
        return searchHistoryDao.queryBuilder().where(userNameWhere, useWhereWhere).list();
    }

    /**
     * 是否存在搜索列表中
     *
     * @author tony
     * @time 2016/12/7 12:37
     **/
    public boolean existUserSearchHistory(String userName, String useWhere, String text) {
        UserSearchHistoryDao searchHistoryDao = GreenDaoManager.getInstance().getSession().getUserSearchHistoryDao();
        WhereCondition userNameWhere = UserSearchHistoryDao.Properties.UserName.eq(userName);
        WhereCondition useWhereWhere = UserSearchHistoryDao.Properties.UseWhere.eq(useWhere);
        WhereCondition textWhere = UserSearchHistoryDao.Properties.Text.eq(text);
        Long count = searchHistoryDao.queryBuilder().where(userNameWhere, useWhereWhere, textWhere).count();
        return 0 == count ? false : true;
    }
}
