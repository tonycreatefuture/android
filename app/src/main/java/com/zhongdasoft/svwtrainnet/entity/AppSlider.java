package com.zhongdasoft.svwtrainnet.entity;

import java.util.List;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/20 10:35
 * 修改人：tony
 * 修改时间：2016/12/20 10:35
 * 修改备注：
 */

public class AppSlider {

    /**
     * ReturnCode : 0
     * Message : OK
     * Result : {"ApiSlider":[{"PictureUrl":"http://svwtrainnet.csvw.com/UploadFiles/APP/Slide/2016/09/20160923110504Z17.jpg","ContentUrl":"http://mp.weixin.qq.com/mp/homepage?__biz=MjM5NjY5ODc2Mg==&hid=9&sn=edc74af05b80dc664f93a40c3487d905#wechat_redirect"},{"PictureUrl":"http://svwtrainnet.csvw.com/UploadFiles/APP/Slide/2016/11/20161130152243Z06.jpg","ContentUrl":"http://www1.cool-pro.com/case/2016/AllNewTiguanL/1120/home.html"},{"PictureUrl":"http://svwtrainnet.csvw.com/UploadFiles/APP/Slide/2016/09/20160923110217Z01.jpg","ContentUrl":"http://mp.weixin.qq.com/s?__biz=MjM5NjY5ODc2Mg==&mid=2658870495&idx=4&sn=59eb47f8625570de4ec288f5c08fd055#rd"},{"PictureUrl":"http://svwtrainnet.csvw.com/UploadFiles/APP/Slide/2016/09/20160923141532Z03.jpg","ContentUrl":"http://mp.weixin.qq.com/s?__biz=MjM5NjY5ODc2Mg==&mid=2658870502&idx=1&sn=587cce07bc36e95fb5f7874a4498f168#rd"},{"PictureUrl":"http://svwtrainnet.csvw.com/UploadFiles/APP/Slide/2016/09/20160923143912Z03.png","ContentUrl":"http://mp.weixin.qq.com/s?__biz=MjM5NjY5ODc2Mg==&mid=2658870573&idx=2&sn=2ac7c474414122ca8231f577411f4f31#rd"},{"PictureUrl":"http://svwtrainnet.csvw.com/UploadFiles/APP/Slide/2016/10/20161013101712Z15.jpg","ContentUrl":"http://mp.weixin.qq.com/s?__biz=MjM5NjY5ODc2Mg==&mid=2658870829&idx=4&sn=d93dee7d60ddd5a6db9320002f42aa62&chksm=bd6ac9c88a1d40deddf0b4887837ee5f38cba8ab85764430c2bae0751cfaecd135207f829df1#rd"}]}
     */

    private int ReturnCode;
    private String Message;
    private ResultBean Result;

    public int getReturnCode() {
        return ReturnCode;
    }

    public void setReturnCode(int ReturnCode) {
        this.ReturnCode = ReturnCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        private List<ApiSliderBean> ApiSlider;

        public List<ApiSliderBean> getApiSlider() {
            return ApiSlider;
        }

        public void setApiSlider(List<ApiSliderBean> ApiSlider) {
            this.ApiSlider = ApiSlider;
        }

        public static class ApiSliderBean {
            /**
             * PictureUrl : http://svwtrainnet.csvw.com/UploadFiles/APP/Slide/2016/09/20160923110504Z17.jpg
             * ContentUrl : http://mp.weixin.qq.com/mp/homepage?__biz=MjM5NjY5ODc2Mg==&hid=9&sn=edc74af05b80dc664f93a40c3487d905#wechat_redirect
             */

            private String PictureUrl;
            private String ContentUrl;

            public String getPictureUrl() {
                return PictureUrl;
            }

            public void setPictureUrl(String PictureUrl) {
                this.PictureUrl = PictureUrl;
            }

            public String getContentUrl() {
                return ContentUrl;
            }

            public void setContentUrl(String ContentUrl) {
                this.ContentUrl = ContentUrl;
            }
        }
    }
}
