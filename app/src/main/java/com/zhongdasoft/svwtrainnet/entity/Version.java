package com.zhongdasoft.svwtrainnet.entity;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/15 13:33
 * 修改人：tony
 * 修改时间：2016/12/15 13:33
 * 修改备注：
 */

public class Version {
    /**
     * ReturnCode : 0
     * Message : OK
     * Result : {"Platform":"Android","AtLeastVersion":1,"LatestVersion":1,"UpdateUrl":"http://download.zhongdasoft.com/app/Android/svwtrainnet.apk","Instruduce":"修复了一些错误"}
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
        /**
         * Platform : Android
         * AtLeastVersion : 1
         * LatestVersion : 1
         * UpdateUrl : http://download.zhongdasoft.com/app/Android/svwtrainnet.apk
         * Instruduce : 修复了一些错误
         */

        private String Platform;
        private String AtLeastVersion;
        private String LatestVersion;
        private String UpdateUrl;
        private String Instruduce;

        public String getPlatform() {
            return Platform;
        }

        public void setPlatform(String Platform) {
            this.Platform = Platform;
        }

        public String getAtLeastVersion() {
            return AtLeastVersion;
        }

        public void setAtLeastVersion(String AtLeastVersion) {
            this.AtLeastVersion = AtLeastVersion;
        }

        public String getLatestVersion() {
            return LatestVersion;
        }

        public void setLatestVersion(String LatestVersion) {
            this.LatestVersion = LatestVersion;
        }

        public String getUpdateUrl() {
            return UpdateUrl;
        }

        public void setUpdateUrl(String UpdateUrl) {
            this.UpdateUrl = UpdateUrl;
        }

        public String getInstruduce() {
            return Instruduce;
        }

        public void setInstruduce(String Instruduce) {
            this.Instruduce = Instruduce;
        }
    }
}
