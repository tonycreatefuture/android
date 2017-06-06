package com.zhongdasoft.svwtrainnet.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/8 10:31
 * 修改人：Administrator
 * 修改时间：2016/11/8 10:31
 * 修改备注：
 */

@Entity
public class UserMenu {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String resName;
    @NotNull
    private String drawableName;
    @NotNull
    private String activityName;
    @NotNull
    private String pActivityName;
    @NotNull
    private Integer level;
    @NotNull
    private Integer state;
    @NotNull
    private Integer isLeaf;
    @NotNull
    private Integer isValid;
    @Generated(hash = 1894668836)
    public UserMenu(Long id, @NotNull String resName, @NotNull String drawableName,
            @NotNull String activityName, @NotNull String pActivityName,
            @NotNull Integer level, @NotNull Integer state, @NotNull Integer isLeaf,
            @NotNull Integer isValid) {
        this.id = id;
        this.resName = resName;
        this.drawableName = drawableName;
        this.activityName = activityName;
        this.pActivityName = pActivityName;
        this.level = level;
        this.state = state;
        this.isLeaf = isLeaf;
        this.isValid = isValid;
    }
    @Generated(hash = 1289143148)
    public UserMenu() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getResName() {
        return this.resName;
    }
    public void setResName(String resName) {
        this.resName = resName;
    }
    public String getDrawableName() {
        return this.drawableName;
    }
    public void setDrawableName(String drawableName) {
        this.drawableName = drawableName;
    }
    public String getActivityName() {
        return this.activityName;
    }
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public String getPActivityName() {
        return this.pActivityName;
    }
    public void setPActivityName(String pActivityName) {
        this.pActivityName = pActivityName;
    }
    public Integer getLevel() {
        return this.level;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
    public Integer getState() {
        return this.state;
    }
    public void setState(Integer state) {
        this.state = state;
    }
    public Integer getIsLeaf() {
        return this.isLeaf;
    }
    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }
    public Integer getIsValid() {
        return this.isValid;
    }
    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }
}
