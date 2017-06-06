package com.zhongdasoft.svwtrainnet.entity;

import java.util.List;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/16 11:37
 * 修改人：tony
 * 修改时间：2016/12/16 11:37
 * 修改备注：
 */

public class UnfinishedOnlineCourse {

    /**
     * ReturnCode : 0
     * Message : OK
     * Result : {"ApiOnlineCourse":[{"Id":60102,"Name":"经销商满意度内部管控ISM视窗使用指导","Progress":0},{"Id":60502,"Name":"五大核心竞品应对话术","Progress":0}]}
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
        private List<ApiOnlineCourseBean> ApiOnlineCourse;

        public List<ApiOnlineCourseBean> getApiOnlineCourse() {
            return ApiOnlineCourse;
        }

        public void setApiOnlineCourse(List<ApiOnlineCourseBean> ApiOnlineCourse) {
            this.ApiOnlineCourse = ApiOnlineCourse;
        }

        public static class ApiOnlineCourseBean {
            /**
             * Id : 60102
             * Name : 经销商满意度内部管控ISM视窗使用指导
             * Progress : 0
             */

            private int Id;
            private String Name;
            private int Progress;

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public int getProgress() {
                return Progress;
            }

            public void setProgress(int Progress) {
                this.Progress = Progress;
            }
        }
    }
}
