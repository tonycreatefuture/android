package com.zhongdasoft.svwtrainnet.util;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/11 17:55
 * 修改人：Administrator
 * 修改时间：2016/7/11 17:55
 * 修改备注：
 */
public class EmailUtil {
    private static EmailUtil instance;

    private EmailUtil() {
    }

    public synchronized static EmailUtil getInstance() {
        if (null == instance) {
            instance = new EmailUtil();
        }
        return instance;
    }

    public String sendMailByJavaMail(String title, String content) {
        try {
            MailSender sender = new MailSender(MyProperty.getValueByKey("EmailUser", "tonycreatefuture"), MyProperty.getValueByKey("EmailPassword", "bluesky@"));
            sender.sendMail(title,    //主题
                    content,    //正文
                    MyProperty.getValueByKey("EmailFrom", "tonycreatefuture@sohu.com"),  //发送人
                    MyProperty.getValueByKey("EmailTo", "tonycreatefuture@sohu.com")); //收件人
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "ok";
    }
}
