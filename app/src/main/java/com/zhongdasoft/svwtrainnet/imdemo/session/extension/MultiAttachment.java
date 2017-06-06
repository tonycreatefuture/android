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

public class MultiAttachment extends CustomAttachment {
    private String webUrl = "http://www.zhongdasoft.com";
    private String imgUrl = "http://img0.imgtn.bdimg.com/it/u=1737766921,271555379&fm=21&gp=0.jpg";//这个图片没使用，在布局里面放张默认图片
    private String content = "内容xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private String title = "标题xxxx";

    public MultiAttachment() {
        super(CustomAttachmentType.Multi);
    }

    @Override
    protected void parseData(JSONObject data) {
        title = data.getString("title");
        content = data.getString("content");
        imgUrl = data.getString("imgUrl");
        webUrl = data.getString("webUrl");
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("title", title);
        data.put("content", content);
        data.put("imgUrl", imgUrl);
        data.put("webUrl", webUrl);
        return data;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
