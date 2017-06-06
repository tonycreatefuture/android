package com.zhongdasoft.svwtrainnet.entity;

import java.util.List;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/19 15:11
 * 修改人：tony
 * 修改时间：2016/12/19 15:11
 * 修改备注：
 */

public class PendingAuditList {

    /**
     * ReturnCode : 0
     * Message : OK
     * Result : {"ApiPendingAudit":[{"CourseName":"常规课程测试","ApplyDate":"2016-12-19T14:59:00","DealerApproveStatus":"已批准","DealerApproveDate":"2016-12-19T15:00:00","DealerApproveComment":"","ApproveStatus":"待批准","ApproveDate":"0001-01-01T00:00:00","ApproveComment":""},{"CourseName":"新开业经销商助力计划ISM辅导","ApplyDate":"2016-12-19T14:40:00","DealerApproveStatus":"已批准","DealerApproveDate":"2016-12-19T14:45:00","DealerApproveComment":"","ApproveStatus":"已批准","ApproveDate":"2016-12-19T14:46:00","ApproveComment":""}]}
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
        private List<ApiPendingAuditBean> ApiPendingAudit;

        public List<ApiPendingAuditBean> getApiPendingAudit() {
            return ApiPendingAudit;
        }

        public void setApiPendingAudit(List<ApiPendingAuditBean> ApiPendingAudit) {
            this.ApiPendingAudit = ApiPendingAudit;
        }

        public static class ApiPendingAuditBean {
            /**
             * CourseName : 常规课程测试
             * ApplyDate : 2016-12-19T14:59:00
             * DealerApproveStatus : 已批准
             * DealerApproveDate : 2016-12-19T15:00:00
             * DealerApproveComment :
             * ApproveStatus : 待批准
             * ApproveDate : 0001-01-01T00:00:00
             * ApproveComment :
             */

            private String CourseName;
            private String ApplyDate;
            private String DealerApproveStatus;
            private String DealerApproveDate;
            private String DealerApproveComment;
            private String ApproveStatus;
            private String ApproveDate;
            private String ApproveComment;

            public String getCourseName() {
                return CourseName;
            }

            public void setCourseName(String CourseName) {
                this.CourseName = CourseName;
            }

            public String getApplyDate() {
                return ApplyDate;
            }

            public void setApplyDate(String ApplyDate) {
                this.ApplyDate = ApplyDate;
            }

            public String getDealerApproveStatus() {
                return DealerApproveStatus;
            }

            public void setDealerApproveStatus(String DealerApproveStatus) {
                this.DealerApproveStatus = DealerApproveStatus;
            }

            public String getDealerApproveDate() {
                return DealerApproveDate;
            }

            public void setDealerApproveDate(String DealerApproveDate) {
                this.DealerApproveDate = DealerApproveDate;
            }

            public String getDealerApproveComment() {
                return DealerApproveComment;
            }

            public void setDealerApproveComment(String DealerApproveComment) {
                this.DealerApproveComment = DealerApproveComment;
            }

            public String getApproveStatus() {
                return ApproveStatus;
            }

            public void setApproveStatus(String ApproveStatus) {
                this.ApproveStatus = ApproveStatus;
            }

            public String getApproveDate() {
                return ApproveDate;
            }

            public void setApproveDate(String ApproveDate) {
                this.ApproveDate = ApproveDate;
            }

            public String getApproveComment() {
                return ApproveComment;
            }

            public void setApproveComment(String ApproveComment) {
                this.ApproveComment = ApproveComment;
            }
        }
    }
}
