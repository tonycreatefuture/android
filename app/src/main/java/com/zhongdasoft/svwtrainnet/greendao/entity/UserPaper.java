package com.zhongdasoft.svwtrainnet.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/8 9:59
 * 修改人：Administrator
 * 修改时间：2016/11/8 9:59
 * 修改备注：
 */

@Entity
public class UserPaper {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String userName;
    @NotNull
    private String examineeId;
    @NotNull
    private String dbName;
    @NotNull
    private String planId;
    @NotNull
    private String paperId;
    @NotNull
    private Integer paperScore;
    @NotNull
    private String testName;
    @NotNull
    private Boolean showScore;
    @NotNull
    private Integer duration;
    @NotNull
    private Integer status;
    private String realBeginTime;
    private String realEndTime;
    private String beginTime;
    private String endTime;
    private Long leftTime;
    private Float score;
    private String scoreInfo;
    private String userAnswer;
    @Generated(hash = 445423941)
    public UserPaper(Long id, @NotNull String userName, @NotNull String examineeId,
            @NotNull String dbName, @NotNull String planId, @NotNull String paperId,
            @NotNull Integer paperScore, @NotNull String testName,
            @NotNull Boolean showScore, @NotNull Integer duration,
            @NotNull Integer status, String realBeginTime, String realEndTime,
            String beginTime, String endTime, Long leftTime, Float score,
            String scoreInfo, String userAnswer) {
        this.id = id;
        this.userName = userName;
        this.examineeId = examineeId;
        this.dbName = dbName;
        this.planId = planId;
        this.paperId = paperId;
        this.paperScore = paperScore;
        this.testName = testName;
        this.showScore = showScore;
        this.duration = duration;
        this.status = status;
        this.realBeginTime = realBeginTime;
        this.realEndTime = realEndTime;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.leftTime = leftTime;
        this.score = score;
        this.scoreInfo = scoreInfo;
        this.userAnswer = userAnswer;
    }
    @Generated(hash = 17376738)
    public UserPaper() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getExamineeId() {
        return this.examineeId;
    }
    public void setExamineeId(String examineeId) {
        this.examineeId = examineeId;
    }
    public String getDbName() {
        return this.dbName;
    }
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    public String getPlanId() {
        return this.planId;
    }
    public void setPlanId(String planId) {
        this.planId = planId;
    }
    public String getPaperId() {
        return this.paperId;
    }
    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }
    public Integer getPaperScore() {
        return this.paperScore;
    }
    public void setPaperScore(Integer paperScore) {
        this.paperScore = paperScore;
    }
    public String getTestName() {
        return this.testName;
    }
    public void setTestName(String testName) {
        this.testName = testName;
    }
    public Boolean getShowScore() {
        return this.showScore;
    }
    public void setShowScore(Boolean showScore) {
        this.showScore = showScore;
    }
    public Integer getDuration() {
        return this.duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public Integer getStatus() {
        return this.status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getRealBeginTime() {
        return this.realBeginTime;
    }
    public void setRealBeginTime(String realBeginTime) {
        this.realBeginTime = realBeginTime;
    }
    public String getRealEndTime() {
        return this.realEndTime;
    }
    public void setRealEndTime(String realEndTime) {
        this.realEndTime = realEndTime;
    }
    public String getBeginTime() {
        return this.beginTime;
    }
    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }
    public String getEndTime() {
        return this.endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public Long getLeftTime() {
        return this.leftTime;
    }
    public void setLeftTime(Long leftTime) {
        this.leftTime = leftTime;
    }
    public Float getScore() {
        return this.score;
    }
    public void setScore(Float score) {
        this.score = score;
    }
    public String getScoreInfo() {
        return this.scoreInfo;
    }
    public void setScoreInfo(String scoreInfo) {
        this.scoreInfo = scoreInfo;
    }
    public String getUserAnswer() {
        return this.userAnswer;
    }
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
