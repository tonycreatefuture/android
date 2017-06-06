package com.zhongdasoft.svwtrainnet.entity;

import java.util.List;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/16 12:00
 * 修改人：tony
 * 修改时间：2016/12/16 12:00
 * 修改备注：
 */

public class ConfirmList {


    /**
     * ReturnCode : 0
     * Message : OK
     * Result : {"ApiApply":[{"ApplyId":"201600003F","Confirmed":false,"Course":{"TypeId":0,"SubTypeId":0,"CourseNo":"A001","CourseName":"常规课程测试","Days":0,"Fee":0,"CarBrand":"","CanApply":false,"InternalExpiry":"0001-01-01T00:00:00","Form":"Normal"},"StartDate":"2016-12-20T08:30:00","EndDate":"2016-12-20T17:00:00","Grade":0,"Pass":false,"PlanId":"201612A0010001"},{"ApplyId":"201600003F","Confirmed":false,"Course":{"TypeId":0,"SubTypeId":0,"CourseNo":"A001","CourseName":"常规课程测试","Days":0,"Fee":0,"CarBrand":"","CanApply":false,"InternalExpiry":"0001-01-01T00:00:00","Form":"Normal"},"StartDate":"2016-12-20T08:30:00","EndDate":"2016-12-20T17:00:00","Grade":0,"Pass":false,"PlanId":"201612A0010001"}]}
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
        private List<ApiApplyBean> ApiApply;

        public List<ApiApplyBean> getApiApply() {
            return ApiApply;
        }

        public void setApiApply(List<ApiApplyBean> ApiApply) {
            this.ApiApply = ApiApply;
        }

        public static class ApiApplyBean {
            /**
             * ApplyId : 201600003F
             * Confirmed : false
             * Course : {"TypeId":0,"SubTypeId":0,"CourseNo":"A001","CourseName":"常规课程测试","Days":0,"Fee":0,"CarBrand":"","CanApply":false,"InternalExpiry":"0001-01-01T00:00:00","Form":"Normal"}
             * StartDate : 2016-12-20T08:30:00
             * EndDate : 2016-12-20T17:00:00
             * Grade : 0
             * Pass : false
             * PlanId : 201612A0010001
             */

            private String ApplyId;
            private boolean Confirmed;
            private CourseBean Course;
            private String StartDate;
            private String EndDate;
            private int Grade;
            private boolean Pass;
            private String PlanId;

            public String getApplyId() {
                return ApplyId;
            }

            public void setApplyId(String ApplyId) {
                this.ApplyId = ApplyId;
            }

            public boolean isConfirmed() {
                return Confirmed;
            }

            public void setConfirmed(boolean Confirmed) {
                this.Confirmed = Confirmed;
            }

            public CourseBean getCourse() {
                return Course;
            }

            public void setCourse(CourseBean Course) {
                this.Course = Course;
            }

            public String getStartDate() {
                return StartDate;
            }

            public void setStartDate(String StartDate) {
                this.StartDate = StartDate;
            }

            public String getEndDate() {
                return EndDate;
            }

            public void setEndDate(String EndDate) {
                this.EndDate = EndDate;
            }

            public int getGrade() {
                return Grade;
            }

            public void setGrade(int Grade) {
                this.Grade = Grade;
            }

            public boolean isPass() {
                return Pass;
            }

            public void setPass(boolean Pass) {
                this.Pass = Pass;
            }

            public String getPlanId() {
                return PlanId;
            }

            public void setPlanId(String PlanId) {
                this.PlanId = PlanId;
            }

            public static class CourseBean {
                /**
                 * TypeId : 0
                 * SubTypeId : 0
                 * CourseNo : A001
                 * CourseName : 常规课程测试
                 * Days : 0
                 * Fee : 0
                 * CarBrand :
                 * CanApply : false
                 * InternalExpiry : 0001-01-01T00:00:00
                 * Form : Normal
                 */

                private int TypeId;
                private int SubTypeId;
                private String CourseNo;
                private String CourseName;
                private int Days;
                private int Fee;
                private String CarBrand;
                private boolean CanApply;
                private String InternalExpiry;
                private String Form;

                public int getTypeId() {
                    return TypeId;
                }

                public void setTypeId(int TypeId) {
                    this.TypeId = TypeId;
                }

                public int getSubTypeId() {
                    return SubTypeId;
                }

                public void setSubTypeId(int SubTypeId) {
                    this.SubTypeId = SubTypeId;
                }

                public String getCourseNo() {
                    return CourseNo;
                }

                public void setCourseNo(String CourseNo) {
                    this.CourseNo = CourseNo;
                }

                public String getCourseName() {
                    return CourseName;
                }

                public void setCourseName(String CourseName) {
                    this.CourseName = CourseName;
                }

                public int getDays() {
                    return Days;
                }

                public void setDays(int Days) {
                    this.Days = Days;
                }

                public int getFee() {
                    return Fee;
                }

                public void setFee(int Fee) {
                    this.Fee = Fee;
                }

                public String getCarBrand() {
                    return CarBrand;
                }

                public void setCarBrand(String CarBrand) {
                    this.CarBrand = CarBrand;
                }

                public boolean isCanApply() {
                    return CanApply;
                }

                public void setCanApply(boolean CanApply) {
                    this.CanApply = CanApply;
                }

                public String getInternalExpiry() {
                    return InternalExpiry;
                }

                public void setInternalExpiry(String InternalExpiry) {
                    this.InternalExpiry = InternalExpiry;
                }

                public String getForm() {
                    return Form;
                }

                public void setForm(String Form) {
                    this.Form = Form;
                }
            }
        }
    }
}
