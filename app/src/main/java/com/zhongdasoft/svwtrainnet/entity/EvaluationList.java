package com.zhongdasoft.svwtrainnet.entity;

import java.util.List;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/20 10:23
 * 修改人：tony
 * 修改时间：2016/12/20 10:23
 * 修改备注：
 */

public class EvaluationList {

    /**
     * ReturnCode : 0
     * Message : OK
     * Result : {"ApiEvaluation":[{"Name":"销售顾问资质培训教学质量评估调查","EvaluationType":"OfflineCourse","TrainPlan":{"PlanId":"201612A0010001","Teacher":"韩迎胜","Course":{"TypeId":0,"SubTypeId":0,"CourseNo":"A001","CourseName":"常规课程测试","Days":0,"Fee":0,"CarBrand":"","CanApply":false,"InternalExpiry":"0001-01-01T00:00:00","Form":"Normal"},"StartDate":"2016-12-20T08:30:00","EndDate":"2016-12-20T17:00:00","Place":{"Name":"PPG 上海培训中心"}},"SolutionId":"20110805A0001","ActivityId":"20161219A0002"},{"Name":"销售顾问资质培训教学质量评估调查","EvaluationType":"OfflineCourse","TrainPlan":{"PlanId":"201612A0010001","Teacher":"韩迎胜","Course":{"TypeId":0,"SubTypeId":0,"CourseNo":"A001","CourseName":"常规课程测试","Days":0,"Fee":0,"CarBrand":"","CanApply":false,"InternalExpiry":"0001-01-01T00:00:00","Form":"Normal"},"StartDate":"2016-12-20T08:30:00","EndDate":"2016-12-20T17:00:00","Place":{"Name":"PPG 上海培训中心"}},"SolutionId":"20110805A0001","ActivityId":"20161219A0002"}]}
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
        private List<ApiEvaluationBean> ApiEvaluation;

        public List<ApiEvaluationBean> getApiEvaluation() {
            return ApiEvaluation;
        }

        public void setApiEvaluation(List<ApiEvaluationBean> ApiEvaluation) {
            this.ApiEvaluation = ApiEvaluation;
        }

        public static class ApiEvaluationBean {
            /**
             * Name : 销售顾问资质培训教学质量评估调查
             * EvaluationType : OfflineCourse
             * TrainPlan : {"PlanId":"201612A0010001","Teacher":"韩迎胜","Course":{"TypeId":0,"SubTypeId":0,"CourseNo":"A001","CourseName":"常规课程测试","Days":0,"Fee":0,"CarBrand":"","CanApply":false,"InternalExpiry":"0001-01-01T00:00:00","Form":"Normal"},"StartDate":"2016-12-20T08:30:00","EndDate":"2016-12-20T17:00:00","Place":{"Name":"PPG 上海培训中心"}}
             * SolutionId : 20110805A0001
             * ActivityId : 20161219A0002
             */

            private String Name;
            private String EvaluationType;
            private TrainPlanBean TrainPlan;
            private String SolutionId;
            private String ActivityId;

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getEvaluationType() {
                return EvaluationType;
            }

            public void setEvaluationType(String EvaluationType) {
                this.EvaluationType = EvaluationType;
            }

            public TrainPlanBean getTrainPlan() {
                return TrainPlan;
            }

            public void setTrainPlan(TrainPlanBean TrainPlan) {
                this.TrainPlan = TrainPlan;
            }

            public String getSolutionId() {
                return SolutionId;
            }

            public void setSolutionId(String SolutionId) {
                this.SolutionId = SolutionId;
            }

            public String getActivityId() {
                return ActivityId;
            }

            public void setActivityId(String ActivityId) {
                this.ActivityId = ActivityId;
            }

            public static class TrainPlanBean {
                /**
                 * PlanId : 201612A0010001
                 * Teacher : 韩迎胜
                 * Course : {"TypeId":0,"SubTypeId":0,"CourseNo":"A001","CourseName":"常规课程测试","Days":0,"Fee":0,"CarBrand":"","CanApply":false,"InternalExpiry":"0001-01-01T00:00:00","Form":"Normal"}
                 * StartDate : 2016-12-20T08:30:00
                 * EndDate : 2016-12-20T17:00:00
                 * Place : {"Name":"PPG 上海培训中心"}
                 */

                private String PlanId;
                private String Teacher;
                private CourseBean Course;
                private String StartDate;
                private String EndDate;
                private PlaceBean Place;

                public String getPlanId() {
                    return PlanId;
                }

                public void setPlanId(String PlanId) {
                    this.PlanId = PlanId;
                }

                public String getTeacher() {
                    return Teacher;
                }

                public void setTeacher(String Teacher) {
                    this.Teacher = Teacher;
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

                public PlaceBean getPlace() {
                    return Place;
                }

                public void setPlace(PlaceBean Place) {
                    this.Place = Place;
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

                public static class PlaceBean {
                    /**
                     * Name : PPG 上海培训中心
                     */

                    private String Name;

                    public String getName() {
                        return Name;
                    }

                    public void setName(String Name) {
                        this.Name = Name;
                    }
                }
            }
        }
    }
}
