package com.zhongdasoft.svwtrainnet.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/8 10:28
 * 修改人：Administrator
 * 修改时间：2016/11/8 10:28
 * 修改备注：
 */

@Entity
public class UserSearchHistory {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String userName;
    @NotNull
    private String useWhere;
    @NotNull
    private String text;
    private Boolean isValid;
    @Generated(hash = 283301323)
    public UserSearchHistory(Long id, @NotNull String userName,
            @NotNull String useWhere, @NotNull String text, Boolean isValid) {
        this.id = id;
        this.userName = userName;
        this.useWhere = useWhere;
        this.text = text;
        this.isValid = isValid;
    }
    @Generated(hash = 525076550)
    public UserSearchHistory() {
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
    public String getUseWhere() {
        return this.useWhere;
    }
    public void setUseWhere(String useWhere) {
        this.useWhere = useWhere;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Boolean getIsValid() {
        return this.isValid;
    }
    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }
}
