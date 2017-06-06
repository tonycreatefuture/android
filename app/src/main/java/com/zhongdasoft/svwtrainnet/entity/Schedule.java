package com.zhongdasoft.svwtrainnet.entity;

import java.util.List;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/20 10:25
 * 修改人：tony
 * 修改时间：2016/12/20 10:25
 * 修改备注：
 */

public class Schedule {

    /**
     * ReturnCode : 0
     * Message : OK
     * Result : {"ApiSchedule":[{"Id":"201612A0010001","StartTime":"2016-12-20T08:30:00","FinishTime":"2016-12-20T17:00:00","Subject":"常规课程测试","ScheduleType":"OfflineTraining","Place":"PPG 上海培训中心"},{"Id":"201612A0010001","StartTime":"2016-12-20T08:30:00","FinishTime":"2016-12-20T17:00:00","Subject":"常规课程测试","ScheduleType":"OfflineTraining","Place":"PPG 上海培训中心"}]}
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
        private List<ApiScheduleBean> ApiSchedule;

        public List<ApiScheduleBean> getApiSchedule() {
            return ApiSchedule;
        }

        public void setApiSchedule(List<ApiScheduleBean> ApiSchedule) {
            this.ApiSchedule = ApiSchedule;
        }

        public static class ApiScheduleBean {
            /**
             * Id : 201612A0010001
             * StartTime : 2016-12-20T08:30:00
             * FinishTime : 2016-12-20T17:00:00
             * Subject : 常规课程测试
             * ScheduleType : OfflineTraining
             * Place : PPG 上海培训中心
             */

            private String Id;
            private String StartTime;
            private String FinishTime;
            private String Subject;
            private String ScheduleType;
            private String Place;

            public String getId() {
                return Id;
            }

            public void setId(String Id) {
                this.Id = Id;
            }

            public String getStartTime() {
                return StartTime;
            }

            public void setStartTime(String StartTime) {
                this.StartTime = StartTime;
            }

            public String getFinishTime() {
                return FinishTime;
            }

            public void setFinishTime(String FinishTime) {
                this.FinishTime = FinishTime;
            }

            public String getSubject() {
                return Subject;
            }

            public void setSubject(String Subject) {
                this.Subject = Subject;
            }

            public String getScheduleType() {
                return ScheduleType;
            }

            public void setScheduleType(String ScheduleType) {
                this.ScheduleType = ScheduleType;
            }

            public String getPlace() {
                return Place;
            }

            public void setPlace(String Place) {
                this.Place = Place;
            }
        }
    }
}
