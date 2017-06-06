package com.zhongdasoft.svwtrainnet.entity;

import java.util.List;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/20 10:26
 * 修改人：tony
 * 修改时间：2016/12/20 10:26
 * 修改备注：
 */

public class NewsList {

    /**
     * ReturnCode : 0
     * Message : OK
     * Result : {"ApiNews":[{"Id":2118,"Title":"全新途观L上市标杆培训课程选择","AddDate":"2016-12-14T13:32:26.19"},{"Id":2117,"Title":"辉昂展厅故事情景教学片于SVW TV发布","AddDate":"2016-12-13T17:58:38.023"},{"Id":2116,"Title":"iCrEAM与ASMP市场计划网络广告布码与评估模块使用培训 V-Live培训通知","AddDate":"2016-12-13T17:54:18.98"},{"Id":2115,"Title":"全新途观L上市标杆店培训相关事宜","AddDate":"2016-12-13T16:36:17.443"},{"Id":2113,"Title":"全新途观L上市标杆店培训报名通知","AddDate":"2016-12-13T09:05:26.823"},{"Id":2114,"Title":"11月未完成必修课程学员清单","AddDate":"2016-12-12T16:49:09.79"},{"Id":2112,"Title":"《如何提升售后产值》补报名通知","AddDate":"2016-12-12T14:43:08.587"},{"Id":2111,"Title":"2016年9月（124批）网发基础V-star奖励到账及网上确认提醒","AddDate":"2016-12-12T11:52:05.657"},{"Id":2109,"Title":"《更上一层楼全新途安L如何能跨界争雄》内训课件","AddDate":"2016-12-07T14:03:31.047"},{"Id":2108,"Title":"12月下旬售后非技术培训时间调整","AddDate":"2016-12-05T16:22:22.317"},{"Id":2107,"Title":"销售顾问&amp;服务顾问初级资质培训课前测试不合格名单通报","AddDate":"2016-11-30T17:01:20.31"},{"Id":2106,"Title":"参加途观L海口上市集中培训的学员请注意！","AddDate":"2016-11-29T09:38:33.17"},{"Id":2104,"Title":"ＳＶＷ　ＴＶ更新维护公告","AddDate":"2016-11-24T12:03:41.37"},{"Id":2103,"Title":"东北地区服务顾问资质培训(初级）报名紧急通知","AddDate":"2016-11-22T13:46:47.653"},{"Id":2102,"Title":"原定11月27-30日的《附件经理资质培训(初级）》时间变为28-30日","AddDate":"2016-11-22T11:39:32.79"},{"Id":2095,"Title":"服务顾问资质培训（初级）及服务顾问资质培训（高级）报名通知","AddDate":"2016-11-21T13:15:41.78"}]}
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
        private List<ApiNewsBean> ApiNews;

        public List<ApiNewsBean> getApiNews() {
            return ApiNews;
        }

        public void setApiNews(List<ApiNewsBean> ApiNews) {
            this.ApiNews = ApiNews;
        }

        public static class ApiNewsBean {
            /**
             * Id : 2118
             * Title : 全新途观L上市标杆培训课程选择
             * AddDate : 2016-12-14T13:32:26.19
             */

            private int Id;
            private String Title;
            private String AddDate;

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public String getTitle() {
                return Title;
            }

            public void setTitle(String Title) {
                this.Title = Title;
            }

            public String getAddDate() {
                return AddDate;
            }

            public void setAddDate(String AddDate) {
                this.AddDate = AddDate;
            }
        }
    }
}
