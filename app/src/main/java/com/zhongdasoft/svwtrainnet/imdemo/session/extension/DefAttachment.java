package com.zhongdasoft.svwtrainnet.imdemo.session.extension;


import com.alibaba.fastjson.JSONObject;

/**
 * 项目名称： android
 * 类描述：
 * 创建人：tony
 * 创建时间：2017/1/10 16:01
 * 修改人：tony
 * 修改时间：2017/1/10 16:01
 * 修改备注：
 */

public class DefAttachment extends CustomAttachment {
    private String Url;
    private String Title;
    private String Description;

    private JSONObject data;

    public DefAttachment() {
        super(CustomAttachmentType.Def);
    }

    @Override
    protected void parseData(JSONObject data) {
        Title = data.getString("Title");
        Description = data.getString("Description");
        Url = data.getString("Url");
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("Title", Title);
        data.put("Description", Description);
        data.put("Url", Url);
        return data;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }
}
