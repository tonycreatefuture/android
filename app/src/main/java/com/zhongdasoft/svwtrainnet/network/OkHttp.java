package com.zhongdasoft.svwtrainnet.network;

import android.content.Context;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.network.callback.StringCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.ApplyOfflineCourseCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.ConfirmApplyCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.CounselingCourseTypeListCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.CounselingFinishedPlansCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.CounselingNotFinishedPlansCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.EvaluationListCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.EvaluationPaperCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.EvaluationSubmitCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetAppSliderCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetConfirmListCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetCourseCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetCourseListCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetCourseTypeListCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetDateTimeCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetExamListCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetNewsListCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetOfflineCourseResultCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetPendingAuditListCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetProfileCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetScheduleCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetSuitableCourseCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetSvwTvCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetTrainingPlanCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetUnfinishedOnlineCourseCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.GetVersionCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalCancelPlanCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalCancelRegisterTraineeCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalDeleteFileCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalNewPlanCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalPublishPlanCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalQueryPlansCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalQueryTraineesCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalRegisterTraineeCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalSubmitCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalSuitableTraineesCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalSuitableWorktypesCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalSummaryCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalUpdateSummaryCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalUpdateTraineeCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.InternalUploadFileCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.LoginByNameCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.LoginCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.OnlineCourseListCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.OnlineCourseTypesCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.OnlineSelectCourseCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.ScanBarcodeCallback;
import com.zhongdasoft.svwtrainnet.network.iCallback.SubmitGradeCallback;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.NetManager;
import com.zhongdasoft.svwtrainnet.util.PhoneInfo;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;

import java.util.HashMap;

import okhttp3.MediaType;

public class OkHttp {
    private static OkHttp instance;

    private OkHttp() {
    }

    /**
     * 单例模式
     */
    public synchronized static OkHttp getInstance() {
        if (null == instance) {
            instance = new OkHttp();
        }
        return instance;
    }

    public void invoke(Context context, HashMap<String, String> mParams, StringCallback callback) {
        boolean isConnected = NetManager.isNetworkConnected(context);
        if (!isConnected) {
            ToastUtil.show(context, context.getResources().getString(R.string.showByNetError));
            return;
        }
        String serviceUrl = mParams.get("serviceUrl").toString();
        String methodName = mParams.get("methodName").toString();
        mParams.remove("serviceUrl");
        mParams.remove("methodName");
        mParams.remove("recurField");
        String requestXml = buildRequestData(methodName, mParams);
        OkHttpUtil
                .postString()
                .url(serviceUrl)
                .mediaType(MediaType.parse("text/xml; charset=utf-8"))
                .content(requestXml)
                .build()
                .execute(callback);
    }

    private String buildRequestData(String mName, HashMap<String, String> mParams) {
        String nameSpace = MyProperty.loadConfig().getProperty("NAMESPACE");
        StringBuffer soapRequestData = new StringBuffer();
        soapRequestData.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        soapRequestData
                .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                        + " xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapRequestData.append("<soap:Body>");
        soapRequestData.append("<" + mName + " xmlns=\"" + nameSpace
                + "\">");
        for (String name : mParams.keySet()) {
            soapRequestData.append("<" + name + ">" + mParams.get(name)
                    + "</" + name + ">");
        }
        soapRequestData.append("</" + mName + ">");
        soapRequestData.append("</soap:Body>");
        soapRequestData.append("</soap:Envelope>");

        return soapRequestData.toString();
    }

    /**
     * 获取设置的默认参数
     *
     * @author tony
     * created at 2016/11/14 16:17
     */
    private HashMap<String, String> getDefaultParams(Context context, String... params) {
        HashMap<String, String> mParams = new HashMap<>();
        String serviceUrl = MyProperty.getCurrentValue(context.getResources().getString(R.string.Api));
        mParams.put("serviceUrl", serviceUrl);
        for (int i = 0; i < params.length; i += 2) {
            mParams.put(params[i], params[i + 1]);
        }
        if ("Login".equals(mParams.get("methodName")) || "LoginByName".equals(mParams.get("methodName"))) {
            mParams.put("device", PhoneInfo.getInstance().getDeviceXML(context));
        } else {
            String accessToken = MySharedPreferences.getInstance().getAccessToken();
            mParams.put("accessToken", accessToken);
            mParams.put("deviceId", PhoneInfo.getInstance().getDeviceId(context));
        }
        mParams.put("network", PhoneInfo.getInstance().getNetworkXML(context));
        return mParams;
    }

    /**
     * 验证用户名密码
     *
     * @param context  上下文
     * @param userName 用户账号
     * @param password 用户密码
     */
    public void Login(Context context, String userName, String password) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "Login", "userName", userName, "password", password);
        invoke(context, mParams, new LoginCallback(context));
    }

    /**
     * 根据经销商及姓名登录
     *
     * @param context   上下文
     * @param dealerNo  经销商号
     * @param staffName 用户姓名
     */
    public void LoginByName(Context context, String dealerNo, String staffName) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "LoginByName", "dealerNo", dealerNo, "staffName", staffName);
        invoke(context, mParams, new LoginByNameCallback(context));
    }

    /**
     * 获取用户的考试试卷，得到的是sqlite数据库文件名称
     *
     * @param context 上下文
     */
    public void GetExamList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetExamList");
        invoke(context, mParams, new GetExamListCallback(context));
    }

    /**
     * 提交成绩
     *
     * @param context 上下文
     * @param data    提交数据
     */
    public void SubmitGrade(Context context, String data) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "SubmitGrade", "data", data);
        invoke(context, mParams, new SubmitGradeCallback(context));
    }

    /**
     * 获取服务器系统时间
     *
     * @param context 上下文
     */
    public void GetDateTime(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetDateTime");
        invoke(context, mParams, new GetDateTimeCallback(context));
    }

    /**
     * 获取版本号
     *
     * @param context 上下文
     */
    public void GetVersion(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetVersion", "platform", "Android");
        invoke(context, mParams, new GetVersionCallback(context));
    }

    /**
     * 获取新闻列表
     *
     * @param context 上下文
     */
    public void GetNewsList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetNewsList");
        invoke(context, mParams, new GetNewsListCallback(context));
    }

    /**
     * app新闻
     *
     * @param context 上下文
     */
    public void GetAppSlider(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetAppSlider");
        invoke(context, mParams, new GetAppSliderCallback(context));
    }


    /**
     * 获取课程列表
     *
     * @param context    上下文
     * @param courseType 课程类型
     * @param form       课程形式
     */
    public void GetCourseList(Context context, int courseType, String form) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetCourseList", "courseType", courseType + "", "form", form);
        invoke(context, mParams, new GetCourseListCallback(context));
    }

    /**
     * 获取课程详细信息
     *
     * @param context  上下文
     * @param courseNo 课程编号
     */
    public void GetCourse(Context context, String courseNo) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetCourse", "courseNo", courseNo);
        invoke(context, mParams, new GetCourseCallback(context));
    }

    /**
     * 申请离线课程
     *
     * @param context  上下文
     * @param courseNo 课程编号
     */
    public void ApplyOfflineCourse(Context context, String courseNo) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ApplyOfflineCourse", "courseNo", courseNo);
        invoke(context, mParams, new ApplyOfflineCourseCallback(context));
    }

    /**
     * 确认申请
     *
     * @param context 上下文
     * @param applyId 申请Id
     */
    public void ConfirmApply(Context context, String applyId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ConfirmApply", "applyId", applyId);
        invoke(context, mParams, new ConfirmApplyCallback(context));
    }

    /**
     * 获取确认列表
     *
     * @param context 上下文
     */
    public void GetConfirmList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetConfirmList", "recurField", "Course,ApplyId");
        invoke(context, mParams, new GetConfirmListCallback(context));
    }

    /**
     * 扫描二维码
     *
     * @param context    上下文
     * @param scanResult 扫描结果
     */
    public void ScanBarcode(Context context, String scanResult) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ScanBarcode", "scanResult", scanResult);
        invoke(context, mParams, new ScanBarcodeCallback(context));
    }

    /**
     * 培训计划
     *
     * @param context 上下文
     * @param planId  计划id
     */
    public void GetTrainingPlan(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetTrainingPlan", "planId", planId);
        invoke(context, mParams, new GetTrainingPlanCallback(context));
    }

    /**
     * 获取待审核申请列表
     *
     * @param context 上下文
     */
    public void GetPendingAuditList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetPendingAuditList");
        invoke(context, mParams, new GetPendingAuditListCallback(context));
    }

    /**
     * 获取待评估项目列表
     *
     * @param context 上下文
     */
    public void EvaluationList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "EvaluationList");
        invoke(context, mParams, new EvaluationListCallback(context));
    }

    /**
     * 获取未完成在线课程清单
     *
     * @param context 上下文
     */
    public void GetUnfinishedOnlineCourse(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetUnfinishedOnlineCourse");
        invoke(context, mParams, new GetUnfinishedOnlineCourseCallback(context));
    }

    /**
     * 刷新用户数据，与Login接口返回值类似
     *
     * @param context 上下文
     */
    public void GetProfile(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetProfile");
        invoke(context, mParams, new GetProfileCallback(context));
    }

    /**
     * 获取日程安排，开始时间beginDate,结束时间endDate最好不要超过1个月
     *
     * @param context   上下文
     * @param beginDate 开始日期
     * @param endDate   结束日期
     */
    public void GetSchedule(Context context, String beginDate, String endDate) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetSchedule", "beginDate", beginDate, "endDate", endDate);
        invoke(context, mParams, new GetScheduleCallback(context));
    }

    /**
     * 获取svwtv地址，该地址只能使用一次
     *
     * @param context 上下文
     */
    public void GetSvwTvUrl(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetSvwTvUrl");
        invoke(context, mParams, new GetSvwTvCallback(context));
    }

    /**
     * 获取评估页面
     *
     * @param context    上下文
     * @param activityId 活动ID
     */
    public void EvaluationPaper(Context context, String activityId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "EvaluationPaper", "activityId", activityId);
        invoke(context, mParams, new EvaluationPaperCallback(context));
    }

    /**
     * 提交评估页面
     *
     * @param context    上下文
     * @param activityId 活动ID
     * @param answers    json数据
     */
    public void EvaluationSubmit(Context context, String activityId, String answers) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "EvaluationSubmit", "activityId", activityId, "answers", answers);
        invoke(context, mParams, new EvaluationSubmitCallback(context));
    }

    /**
     * 内部培训取消计划
     *
     * @param context 上下文
     * @param planId  计划ID
     */
    public void InternalCancelPlan(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalCancelPlan", "planId", planId);
        invoke(context, mParams, new InternalCancelPlanCallback(context));
    }

    /**
     * 内部培训取消学员
     *
     * @param context 上下文
     * @param planId  计划ID
     * @param trainee 培训人员
     */
    public void InternalCancelRegisterTrainee(Context context, String planId, String trainee) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalCancelRegisterTrainee", "planId", planId, "trainee", trainee);
        invoke(context, mParams, new InternalCancelRegisterTraineeCallback(context));
    }

    /**
     * 内部培训删除小结附件
     *
     * @param context 上下文
     * @param annexId 附件ID
     */
    public void InternalDeleteFile(Context context, int annexId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalDeleteFile", "annexId", annexId + "");
        invoke(context, mParams, new InternalDeleteFileCallback(context));
    }

    /**
     * 内部培训创建计划
     *
     * @param context 上下文
     * @param plan    计划
     */
    public void InternalNewPlan(Context context, String plan) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalNewPlan", "plan", plan);
        invoke(context, mParams, new InternalNewPlanCallback(context));
    }

    /**
     * 内部培训更新计划
     *
     * @param context 上下文
     * @param plan    计划
     * @return 返回map结果集
     */
    public void InternalUpdatePlan(Context context, String plan) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalUpdatePlan", "plan", plan);
        invoke(context, mParams, new InternalNewPlanCallback(context));
    }

    /**
     * 内部培训查询计划
     *
     * @param context   上下文
     * @param beginDate 开始日期
     * @param endDate   结束日期
     */
    public void InternalQueryPlans(Context context, String beginDate, String endDate) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalQueryPlans", "beginDate", beginDate, "endDate", endDate);
        invoke(context, mParams, new InternalQueryPlansCallback(context));
    }

    /**
     * 内部培训选择学员
     *
     * @param context 上下文
     * @param planId  计划ID
     * @param trainee 培训人员
     */
    public void InternalRegisterTrainee(Context context, String planId, String trainee) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalRegisterTrainee", "planId", planId, "trainee", trainee);
        invoke(context, mParams, new InternalRegisterTraineeCallback(context));
    }

    /**
     * 内部培训更新学员成绩
     *
     * @param context 上下文
     * @param trainee 培训人员
     */
    public void InternalUpdateTrainee(Context context, String trainee) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalUpdateTrainee", "trainee", trainee);
        invoke(context, mParams, new InternalUpdateTraineeCallback(context));
    }

    /**
     * 内部培训发布计划
     *
     * @param context 上下文
     * @param planId  计划ID
     */
    public void InternalPublishPlan(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalPublishPlan", "planId", planId);
        invoke(context, mParams, new InternalPublishPlanCallback(context));
    }

    /**
     * 内部培训提交
     *
     * @param context 上下文
     * @param planId  计划ID
     */
    public void InternalSubmit(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalSubmit", "planId", planId);
        invoke(context, mParams, new InternalSubmitCallback(context));
    }

    /**
     * 内部培训获取可选学员列表
     *
     * @param context 上下文
     * @param planId  计划ID
     */
    public void InternalSuitableTrainees(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalSuitableTrainees", "planId", planId);
        invoke(context, mParams, new InternalSuitableTraineesCallback(context));
    }

    /**
     * 内部培训获取已安排学员列表
     *
     * @param context 上下文
     * @param planId  计划ID
     */
    public void InternalQueryTrainees(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalQueryTrainees", "planId", planId);
        invoke(context, mParams, new InternalQueryTraineesCallback(context));
    }

    /**
     * 内部培训获取可选岗位列表
     *
     * @param context 上下文
     * @param planId  计划ID
     */
    public void InternalSuitableWorktypes(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalSuitableWorktypes", "planId", planId);
        invoke(context, mParams, new InternalSuitableWorktypesCallback(context));
    }

    /**
     * 内部培训小结
     *
     * @param context 上下文
     * @param planId  计划ID
     */
    public void InternalSummary(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalSummary", "planId", planId);
        invoke(context, mParams, new InternalSummaryCallback(context));
    }

    /**
     * 内部培训更新总结
     *
     * @param context 上下文
     * @param planId  计划ID
     * @param summary 总结
     */
    public void InternalUpdateSummary(Context context, String planId, String summary) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalUpdateSummary", "planId", planId, "summary", summary);
        invoke(context, mParams, new InternalUpdateSummaryCallback(context));
    }

    /**
     * 内部培训上传小结附件
     *
     * @param context  上下文
     * @param planId   计划ID
     * @param fileName 文件名
     * @param content  文件内容
     */
    public void InternalUploadFile(Context context, String planId, String fileName, String content) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalUploadFile", "planId", planId, "fileName", fileName, "content", content);
        invoke(context, mParams, new InternalUploadFileCallback(context));
    }

    /**
     * 辅导培训参加过的辅导课程
     *
     * @param context 上下文
     */
    public void CounselingFinishedPlans(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "CounselingFinishedPlans");
        invoke(context, mParams, new CounselingFinishedPlansCallback(context));
    }

    /**
     * 辅导培训未结束辅导课程
     *
     * @param context 上下文
     */
    public void CounselingNotFinishedPlans(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "CounselingNotFinishedPlans");
        invoke(context, mParams, new CounselingNotFinishedPlansCallback(context));
    }

    /**
     * 获取课程类型列表
     *
     * @param context 上下文
     */
    public void GetCourseTypeList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetCourseTypeList", "recurField", "SubTypes,Id");
        invoke(context, mParams, new GetCourseTypeListCallback(context));
    }

    /**
     * 获取可申请课程列表
     *
     * @param context 上下文
     */
    public void GetSuitableCourse(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetSuitableCourse");
        invoke(context, mParams, new GetSuitableCourseCallback(context));
    }

    /**
     * 获取离线课程结果
     *
     * @param context 上下文
     */
    public void GetOfflineCourseResult(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetOfflineCourseResult", "recurField", "Course,ApplyId");
        invoke(context, mParams, new GetOfflineCourseResultCallback(context));
    }

    /**
     * 辅导培训辅导类型（包括大类、小类）
     *
     * @param context 上下文
     */
    public void CounselingCourseTypeList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "CounselingCourseTypeList", "recurField", "SubTypes,Id");
        invoke(context, mParams, new CounselingCourseTypeListCallback(context));
    }

    /**
     * 在线课程类型
     *
     * @param context 上下文
     */
    public void OnlineCourseTypes(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "OnlineCourseTypes", "recurField", "SubTypes,Id");
        invoke(context, mParams, new OnlineCourseTypesCallback(context));
    }

    /**
     * 在线课程列表（目前返回值固定）
     *
     * @param context 上下文
     * @param typeId  返回该类型的所有课程、为空的话返回所有课程；目前暂时返回固定内容
     */
    public void OnlineCourseList(Context context, String typeId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "OnlineCourseList", "typeId", typeId);
        invoke(context, mParams, new OnlineCourseListCallback(context));
    }

    /**
     * 选课（目前返回值固定）
     *
     * @param context  上下文
     * @param courseId 课件Id
     */
    public void OnlineSelectCourse(Context context, String courseId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "OnlineSelectCourse", "courseId", courseId);
        invoke(context, mParams, new OnlineSelectCourseCallback(context));
    }
}
