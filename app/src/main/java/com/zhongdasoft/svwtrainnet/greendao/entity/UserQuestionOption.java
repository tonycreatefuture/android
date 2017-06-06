package com.zhongdasoft.svwtrainnet.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.zhongdasoft.svwtrainnet.greendao.gen.DaoSession;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserQuestionOptionDao;
import com.zhongdasoft.svwtrainnet.greendao.gen.UserQuestionDao;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/10 10:53
 * 修改人：Administrator
 * 修改时间：2016/11/10 10:53
 * 修改备注：
 */
@Entity
public class UserQuestionOption {
    @Id(autoincrement = true)
    private Long uqoId;
    @NotNull
    private Long uqId;
    @NotNull
    private String qID;
    @NotNull
    private String optionID;
    @NotNull
    private String content;
    @ToOne(joinProperty = "uqId")
    private UserQuestion uQuestion;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 220927339)
    private transient UserQuestionOptionDao myDao;
    @Generated(hash = 409556559)
    private transient Long uQuestion__resolvedKey;
    @Generated(hash = 357653758)
    public UserQuestionOption(Long uqoId, @NotNull Long uqId, @NotNull String qID,
            @NotNull String optionID, @NotNull String content) {
        this.uqoId = uqoId;
        this.uqId = uqId;
        this.qID = qID;
        this.optionID = optionID;
        this.content = content;
    }
    @Generated(hash = 1850371016)
    public UserQuestionOption() {
    }
    public Long getUqoId() {
        return this.uqoId;
    }
    public void setUqoId(Long uqoId) {
        this.uqoId = uqoId;
    }
    public Long getUqId() {
        return this.uqId;
    }
    public void setUqId(Long uqId) {
        this.uqId = uqId;
    }
    public String getQID() {
        return this.qID;
    }
    public void setQID(String qID) {
        this.qID = qID;
    }
    public String getOptionID() {
        return this.optionID;
    }
    public void setOptionID(String optionID) {
        this.optionID = optionID;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1722354015)
    public UserQuestion getUQuestion() {
        Long __key = this.uqId;
        if (uQuestion__resolvedKey == null
                || !uQuestion__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserQuestionDao targetDao = daoSession.getUserQuestionDao();
            UserQuestion uQuestionNew = targetDao.load(__key);
            synchronized (this) {
                uQuestion = uQuestionNew;
                uQuestion__resolvedKey = __key;
            }
        }
        return uQuestion;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 464700161)
    public void setUQuestion(@NotNull UserQuestion uQuestion) {
        if (uQuestion == null) {
            throw new DaoException(
                    "To-one property 'uqId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.uQuestion = uQuestion;
            uqId = uQuestion.getUqId();
            uQuestion__resolvedKey = uqId;
        }
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
    @Generated(hash = 1789474159)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserQuestionOptionDao() : null;
    }
}
