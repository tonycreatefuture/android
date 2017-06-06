package com.zhongdasoft.svwtrainnet.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.zhongdasoft.svwtrainnet.greendao.gen.DaoSession;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserQuestionOptionDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserQuestionDao;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/10 10:49
 * 修改人：Administrator
 * 修改时间：2016/11/10 10:49
 * 修改备注：
 */

@Entity
public class UserQuestion {
    @Id(autoincrement = true)
    private Long uqId;
    @NotNull
    private String dbName;
    @NotNull
    private String paperID;
    @NotNull
    private String qID;
    @NotNull
    private String qOrder;
    @NotNull
    private String essayMark;
    @NotNull
    private String courseID;
    private String chapterID;
    @NotNull
    private Integer level;
    @NotNull
    private Integer qType;
    @NotNull
    private String content;
    @NotNull
    private String aID;
    @NotNull
    private String answerContent;
    private String aContent;
    private String referMark;
    private Integer checkSum;
    @ToMany(referencedJoinProperty = "uqId")
    private List<UserQuestionOption> qOptions;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 2013746855)
    private transient UserQuestionDao myDao;
    @Generated(hash = 134536975)
    public UserQuestion(Long uqId, @NotNull String dbName, @NotNull String paperID,
            @NotNull String qID, @NotNull String qOrder, @NotNull String essayMark,
            @NotNull String courseID, String chapterID, @NotNull Integer level,
            @NotNull Integer qType, @NotNull String content, @NotNull String aID,
            @NotNull String answerContent, String aContent, String referMark,
            Integer checkSum) {
        this.uqId = uqId;
        this.dbName = dbName;
        this.paperID = paperID;
        this.qID = qID;
        this.qOrder = qOrder;
        this.essayMark = essayMark;
        this.courseID = courseID;
        this.chapterID = chapterID;
        this.level = level;
        this.qType = qType;
        this.content = content;
        this.aID = aID;
        this.answerContent = answerContent;
        this.aContent = aContent;
        this.referMark = referMark;
        this.checkSum = checkSum;
    }
    @Generated(hash = 1260243596)
    public UserQuestion() {
    }
    public Long getUqId() {
        return this.uqId;
    }
    public void setUqId(Long uqId) {
        this.uqId = uqId;
    }
    public String getDbName() {
        return this.dbName;
    }
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    public String getPaperID() {
        return this.paperID;
    }
    public void setPaperID(String paperID) {
        this.paperID = paperID;
    }
    public String getQID() {
        return this.qID;
    }
    public void setQID(String qID) {
        this.qID = qID;
    }
    public String getQOrder() {
        return this.qOrder;
    }
    public void setQOrder(String qOrder) {
        this.qOrder = qOrder;
    }
    public String getEssayMark() {
        return this.essayMark;
    }
    public void setEssayMark(String essayMark) {
        this.essayMark = essayMark;
    }
    public String getCourseID() {
        return this.courseID;
    }
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }
    public String getChapterID() {
        return this.chapterID;
    }
    public void setChapterID(String chapterID) {
        this.chapterID = chapterID;
    }
    public Integer getLevel() {
        return this.level;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
    public Integer getQType() {
        return this.qType;
    }
    public void setQType(Integer qType) {
        this.qType = qType;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getAID() {
        return this.aID;
    }
    public void setAID(String aID) {
        this.aID = aID;
    }
    public String getAnswerContent() {
        return this.answerContent;
    }
    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }
    public String getAContent() {
        return this.aContent;
    }
    public void setAContent(String aContent) {
        this.aContent = aContent;
    }
    public String getReferMark() {
        return this.referMark;
    }
    public void setReferMark(String referMark) {
        this.referMark = referMark;
    }
    public Integer getCheckSum() {
        return this.checkSum;
    }
    public void setCheckSum(Integer checkSum) {
        this.checkSum = checkSum;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1927852709)
    public List<UserQuestionOption> getQOptions() {
        if (qOptions == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserQuestionOptionDao targetDao = daoSession.getUserQuestionOptionDao();
            List<UserQuestionOption> qOptionsNew = targetDao
                    ._queryUserQuestion_QOptions(uqId);
            synchronized (this) {
                if (qOptions == null) {
                    qOptions = qOptionsNew;
                }
            }
        }
        return qOptions;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1632426354)
    public synchronized void resetQOptions() {
        qOptions = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 818961487)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserQuestionDao() : null;
    }
}
