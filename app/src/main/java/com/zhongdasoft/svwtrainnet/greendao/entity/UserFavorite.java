package com.zhongdasoft.svwtrainnet.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/9 14:30
 * 修改人：Administrator
 * 修改时间：2016/11/9 14:30
 * 修改备注：
 */

@Entity
public class UserFavorite {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String userName;
    @NotNull
    private String menuName;
    @NotNull
    private Boolean saved;
    @NotNull
    private Boolean tmpSaved;
    @NotNull
    private Boolean fixed;
    @Generated(hash = 2012358009)
    public UserFavorite(Long id, @NotNull String userName, @NotNull String menuName,
            @NotNull Boolean saved, @NotNull Boolean tmpSaved,
            @NotNull Boolean fixed) {
        this.id = id;
        this.userName = userName;
        this.menuName = menuName;
        this.saved = saved;
        this.tmpSaved = tmpSaved;
        this.fixed = fixed;
    }
    @Generated(hash = 665530754)
    public UserFavorite() {
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
    public String getMenuName() {
        return this.menuName;
    }
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
    public Boolean getSaved() {
        return this.saved;
    }
    public void setSaved(Boolean saved) {
        this.saved = saved;
    }
    public Boolean getTmpSaved() {
        return this.tmpSaved;
    }
    public void setTmpSaved(Boolean tmpSaved) {
        this.tmpSaved = tmpSaved;
    }
    public Boolean getFixed() {
        return this.fixed;
    }
    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }
}
