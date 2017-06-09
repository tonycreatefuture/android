package com.zhongdasoft.svwtrainnet.network;

import android.content.Context;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.NetManager;
import com.zhongdasoft.svwtrainnet.util.PhoneInfo;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainNetWebService {
    private final static int NetCode = 1;
    private final static int ServerCode = 2;
    private static TrainNetWebService instance;

    private TrainNetWebService() {
    }

    /**
     * 单例模式
     */
    public synchronized static TrainNetWebService getInstance() {
        if (null == instance) {
            instance = new TrainNetWebService();
        }
        return instance;
    }

//    /**
//     * 转到登录界面
//     *
//     * @param context 上下文对象
//     * @param reason  原因
//     */
//    private synchronized void gotoLogin(Context context, String reason) {
//        String userName = MySharedPreferences.getInstance().getUserName(context);
//        if (!StringUtil.isNullOrEmpty(userName)) {
//            MySharedPreferences.getInstance().removeString("userName", context);
//            Bundle bundle = new Bundle();
//            bundle.putString("item", "1");
//            bundle.putString("reason", reason);
//            ((BaseActivity) context).readyGoThenKill(LoginActivity.class, bundle);
//        }
//    }

    /**
     * 异常错误信息
     *
     * @author tony
     * created at 2016/11/14 16:20
     */
    private ArrayList<HashMap<String, Object>> netErrorMapList(Context context, int type) {
        ArrayList<HashMap<String, Object>> listResult = new ArrayList<>();
        HashMap<String, Object> mapResult = new HashMap<>();
        if (type == NetCode) {
            mapResult.put(context.getResources().getString(R.string.ReturnCode), context.getResources().getString(R.string.NetCode));
            mapResult.put(context.getResources().getString(R.string.Message), context.getResources().getString(R.string.NetMessage));
        } else {
            mapResult.put(context.getResources().getString(R.string.ReturnCode), context.getResources().getString(R.string.ServerCode));
            mapResult.put(context.getResources().getString(R.string.Message), context.getResources().getString(R.string.ServerMessage));
        }
        listResult.add(mapResult);
        final String msg = mapResult.get(context.getResources().getString(R.string.Message)).toString();
        final BaseActivity activity = ((BaseActivity) context);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(activity, msg);
            }
        });
        return listResult;
    }

    /**
     * 异常错误信息
     *
     * @author tony
     * created at 2016/11/14 16:20
     */
    private HashMap<String, Object> netErrorMap(Context context, int type) {
        HashMap<String, Object> mapResult = new HashMap<>();
        if (type == NetCode) {
            mapResult.put(context.getResources().getString(R.string.ReturnCode), context.getResources().getString(R.string.NetCode));
            mapResult.put(context.getResources().getString(R.string.Message), context.getResources().getString(R.string.NetMessage));
        } else {
            mapResult.put(context.getResources().getString(R.string.ReturnCode), context.getResources().getString(R.string.ServerCode));
            mapResult.put(context.getResources().getString(R.string.Message), context.getResources().getString(R.string.ServerMessage));
        }
        final String msg = mapResult.get(context.getResources().getString(R.string.Message)).toString();
        final BaseActivity activity = ((BaseActivity) context);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(activity, msg);
            }
        });
        return mapResult;
    }

    /**
     * 处理map列表信息
     *
     * @author tony
     * created at 2016/11/14 16:19
     */
    private ArrayList<HashMap<String, Object>> handleEventList(Context context, HashMap<String, String> mParams) {
        boolean isConnected = NetManager.isNetworkConnected(context);
        if (!isConnected) {
//            gotoLogin(context, context.getResources().getString(R.string.showByNetError));
//            ((BaseActivity) context).gotoLogin();
            return netErrorMapList(context, NetCode);
        }
        ArrayList<HashMap<String, Object>> listResult;
        String methodName = mParams.get("methodName");
        String listKey = null == mParams.get("listKey") ? "" : mParams.get("listKey").toString();
        try {
            listResult = ServiceObject.getInstance().invoke(mParams);
            if (null == listResult) {
                return netErrorMapList(context, ServerCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return netErrorMapList(context, ServerCode);
        }
        String key = context.getResources().getString(R.string.ReturnCode);
        HashMap<String, Object> mapResult = filterSimpleMap(context, listResult, methodName + "Result");
        if (mapResult.containsKey(key) && "-1".equals(mapResult.get(key))) {
//            gotoLogin(context, context.getResources().getString(R.string.loginByOtherDevice));
            MySharedPreferences.getInstance().setStoreString("AccountLogout", "1");
//            ((BaseActivity) context).gotoLogin();
        }
        if (StringUtil.isNullOrEmpty(listKey)) {
            return listResult;
        } else {
            if ("GetSchedule".equals(methodName)) {
                //特殊处理，辅导类培训暂时除去
                listResult = filterList(context, listResult, listKey);
                ArrayList<HashMap<String, Object>> newListResult = new ArrayList<>();
                for (HashMap<String, Object> map : listResult) {
                    if ("CounselingTraining".equalsIgnoreCase(map.get("ScheduleType").toString())) {
                        continue;
                    }
                    newListResult.add(map);
                }
                return newListResult;
            }
            if ("GetSuitableCourse".equals(methodName)) {
                listResult = filterList(context, listResult, listKey);
                BaseActivity activity = (BaseActivity) context;
                StringBuilder sbCourseNo = new StringBuilder();
                ArrayList<HashMap<String, Object>> NewListResult = new ArrayList<>();
                for (HashMap<String, Object> map : listResult) {
                    sbCourseNo.append(map.get("CourseNo").toString()).append(",,");
                    NewListResult.add(map);
                }
                TrainNetApp.getCache().put(CacheKey.SuitMeCourse, sbCourseNo.toString());
                return NewListResult;
            }
            return filterList(context, listResult, listKey);
        }
    }

    /**
     * 处理map信息
     *
     * @author tony
     * created at 2016/11/14 16:19
     */
    private HashMap<String, Object> handleEventMap(Context context, HashMap<String, String> mParams) {
        boolean isConnected = NetManager.isNetworkConnected(context);
        if (!isConnected) {
//            gotoLogin(context, context.getResources().getString(R.string.showByNetError));
//            ((BaseActivity) context).gotoLogin();
            return netErrorMap(context, NetCode);
        }
        ArrayList<HashMap<String, Object>> listResult;
        String methodName = mParams.get("methodName");
        try {
            listResult = ServiceObject.getInstance().invoke(mParams);
            if (null == listResult) {
                return netErrorMap(context, ServerCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return netErrorMap(context, ServerCode);
        }
        String key = context.getResources().getString(R.string.ReturnCode);
        HashMap<String, Object> mapResult = filterSimpleMap(context, listResult, methodName + "Result");
        if (null != mapResult && mapResult.containsKey(key) && "-1".equals(mapResult.get(key))) {
//            gotoLogin(context, context.getResources().getString(R.string.loginByOtherDevice));
            MySharedPreferences.getInstance().setStoreString("AccountLogout", "1");
//            ((BaseActivity) context).gotoLogin();
        }
        return filterMap(context, listResult);
    }

    /**
     * 过滤map信息
     *
     * @author tony
     * created at 2016/11/14 16:19
     */
    private HashMap<String, Object> filterMap(Context context, ArrayList<HashMap<String, Object>> listResult) {
        HashMap<String, Object> mapResult = new HashMap<>();
        StringBuilder value = new StringBuilder();
        for (HashMap<String, Object> map : listResult) {
            for (String key : map.keySet()) {
                if (key.equals(context.getResources().getString(R.string.ParentNode))) {
                    continue;
                }
                if (mapResult.containsKey(key) && !map.get(key).equals(mapResult.get(key))) {
                    value.setLength(0);
                    value.append(mapResult.get(key)).append(",").append(map.get(key));
                    mapResult.put(key, value.toString());
                } else {
                    mapResult.put(key, map.get(key));
                }
            }
        }
        return mapResult;
    }

    /**
     * 过滤返回值信息
     *
     * @author tony
     * created at 2016/11/14 16:18
     */
    private HashMap<String, Object> filterSimpleMap(Context context, ArrayList<HashMap<String, Object>> listResult, String mName) {
        for (HashMap<String, Object> map : listResult) {
            if (mName.equals(map.get(context.getResources().getString(R.string.ParentNode)))) {
                return map;
            }
        }
        return null;
    }

    /**
     * 过滤数据列表
     *
     * @author tony
     * created at 2016/11/14 16:18
     */
    private ArrayList<HashMap<String, Object>> filterList(Context context, ArrayList<HashMap<String, Object>> listResult, String listKey) {
        int pos = -1;
        for (int i = 0; i < listResult.size(); i++) {
            if (listKey.equals(listResult.get(i).get(context.getResources().getString(R.string.ParentNode)))) {
                pos = i;
                break;
            }
        }
        for (int i = 0; i < pos; i++) {
            listResult.remove(0);
        }
        if (-1 == pos) {
            listResult.clear();
        }
        return listResult;
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
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> Login(Context context, String userName, String password) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "Login", "userName", userName, "password", password);
        return handleEventList(context, mParams);
    }

    /**
     * 根据经销商及姓名登录
     *
     * @param context   上下文
     * @param dealerNo  经销商号
     * @param staffName 用户姓名
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> LoginByName(Context context, String dealerNo, String staffName) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "LoginByName", "dealerNo", dealerNo, "staffName", staffName);
        return handleEventList(context, mParams);
    }

    /**
     * 获取用户的考试试卷，得到的是sqlite数据库文件名称
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetExamList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetExamList", "listKey", "ApiExamPlan");
        return handleEventList(context, mParams);
    }

    /**
     * 提交成绩
     *
     * @param context 上下文
     * @param data    提交数据
     * @return 返回map结果集
     */
    public HashMap<String, Object> SubmitGrade(Context context, String data) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "SubmitGrade", "data", data);
        return handleEventMap(context, mParams);
    }

    /**
     * 获取服务器系统时间
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public HashMap<String, Object> GetDateTime(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetDateTime");
        return handleEventMap(context, mParams);
    }

    /**
     * 获取版本号
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public HashMap<String, Object> GetVersion(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetVersion", "platform", "Android");
        return handleEventMap(context, mParams);
    }

    /**
     * 获取新闻列表
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetNewsList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetNewsList", "listKey", "ApiNews");
        return handleEventList(context, mParams);
    }

    /**
     * app新闻
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetAppSlider(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetAppSlider", "listKey", "ApiSlider");
        return handleEventList(context, mParams);
    }


    /**
     * 获取课程列表
     *
     * @param context    上下文
     * @param courseType 课程类型
     * @param form       课程形式
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetCourseList(Context context, int courseType, String form) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetCourseList", "courseType", courseType + "", "form", form, "listKey", "ApiCourse");
        return handleEventList(context, mParams);
    }

    /**
     * 获取课程详细信息
     *
     * @param context  上下文
     * @param courseNo 课程编号
     * @return 返回map结果集
     */
    public HashMap<String, Object> GetCourse(Context context, String courseNo) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetCourse", "courseNo", courseNo);
        return handleEventMap(context, mParams);
    }

    /**
     * 申请离线课程
     *
     * @param context  上下文
     * @param courseNo 课程编号
     * @return 返回map结果集
     */
    public HashMap<String, Object> ApplyOfflineCourse(Context context, String courseNo) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ApplyOfflineCourse", "courseNo", courseNo);
        return handleEventMap(context, mParams);
    }

    /**
     * 确认申请
     *
     * @param context 上下文
     * @param applyId 申请Id
     * @return 返回map结果集
     */
    public HashMap<String, Object> ConfirmApply(Context context, String applyId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ConfirmApply", "applyId", applyId);
        return handleEventMap(context, mParams);
    }

    /**
     * 获取确认列表
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetConfirmList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetConfirmList", "recurField", "Course,ApplyId", "listKey", "ApiApply");
        return handleEventList(context, mParams);
    }

    /**
     * 扫描二维码
     *
     * @param context    上下文
     * @param scanResult 扫描结果
     * @return 返回map结果集
     */
    public HashMap<String, Object> ScanBarcode(Context context, String scanResult) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ScanBarcode", "scanResult", scanResult);
        return handleEventMap(context, mParams);
    }

    /**
     * 培训计划
     *
     * @param context 上下文
     * @param planId  计划id
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetTrainingPlan(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetTrainingPlan", "planId", planId);
        return handleEventList(context, mParams);
    }

    /**
     * 获取待审核申请列表
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetPendingAuditList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetPendingAuditList", "listKey", "ApiPendingAudit");
        return handleEventList(context, mParams);
    }

    /**
     * 获取待评估项目列表
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> EvaluationList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "EvaluationList", "listKey", "ApiEvaluation");
        return handleEventList(context, mParams);
    }

    /**
     * 获取未完成在线课程清单
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetUnfinishedOnlineCourse(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetUnfinishedOnlineCourse", "listKey", "ApiOnlineCourse");
        return handleEventList(context, mParams);
    }

    /**
     * 刷新用户数据，与Login接口返回值类似
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetProfile(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetProfile");
        return handleEventList(context, mParams);
    }

    /**
     * 获取日程安排，开始时间beginDate,结束时间endDate最好不要超过1个月
     *
     * @param context   上下文
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetSchedule(Context context, String beginDate, String endDate) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetSchedule", "beginDate", beginDate, "endDate", endDate, "listKey", "ApiSchedule");
        return handleEventList(context, mParams);
    }

    /**
     * 获取svwtv地址，该地址只能使用一次
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public HashMap<String, Object> GetSvwTvUrl(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetSvwTvUrl");
        return handleEventMap(context, mParams);
    }

    /**
     * 获取评估页面
     *
     * @param context    上下文
     * @param activityId 活动ID
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> EvaluationPaper(Context context, String activityId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "EvaluationPaper", "activityId", activityId);
        return handleEventList(context, mParams);
    }

    /**
     * 提交评估页面
     *
     * @param context    上下文
     * @param activityId 活动ID
     * @param answers    json数据
     * @return 返回map结果集
     */
    public HashMap<String, Object> EvaluationSubmit(Context context, String activityId, String answers) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "EvaluationSubmit", "activityId", activityId, "answers", answers);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训取消计划
     *
     * @param context 上下文
     * @param planId  计划ID
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalCancelPlan(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalCancelPlan", "planId", planId);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训取消学员
     *
     * @param context 上下文
     * @param planId  计划ID
     * @param trainee 培训人员
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalCancelRegisterTrainee(Context context, String planId, String trainee) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalCancelRegisterTrainee", "planId", planId, "trainee", trainee);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训删除小结附件
     *
     * @param context 上下文
     * @param annexId 附件ID
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalDeleteFile(Context context, int annexId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalDeleteFile", "annexId", annexId + "");
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训创建计划
     *
     * @param context 上下文
     * @param plan    计划
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalNewPlan(Context context, String plan) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalNewPlan", "plan", plan);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训更新计划
     *
     * @param context 上下文
     * @param plan    计划
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalUpdatePlan(Context context, String plan) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalUpdatePlan", "plan", plan);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训查询计划
     *
     * @param context   上下文
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> InternalQueryPlans(Context context, String beginDate, String endDate) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalQueryPlans", "beginDate", beginDate, "endDate", endDate, "listKey", "ApiInternalPlan");
        return handleEventList(context, mParams);
    }

    /**
     * 内部培训选择学员
     *
     * @param context 上下文
     * @param planId  计划ID
     * @param trainee 培训人员
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalRegisterTrainee(Context context, String planId, String trainee) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalRegisterTrainee", "planId", planId, "trainee", trainee);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训更新学员成绩
     *
     * @param context 上下文
     * @param trainee 培训人员
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalUpdateTrainee(Context context, String trainee) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalUpdateTrainee", "trainee", trainee);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训发布计划
     *
     * @param context 上下文
     * @param planId  计划ID
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalPublishPlan(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalPublishPlan", "planId", planId);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训提交
     *
     * @param context 上下文
     * @param planId  计划ID
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalSubmit(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalSubmit", "planId", planId);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训获取可选学员列表
     *
     * @param context 上下文
     * @param planId  计划ID
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> InternalSuitableTrainees(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalSuitableTrainees", "planId", planId, "listKey", "ApiInternalTrainee");
        return handleEventList(context, mParams);
    }

    /**
     * 内部培训获取已安排学员列表
     *
     * @param context 上下文
     * @param planId  计划ID
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> InternalQueryTrainees(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalQueryTrainees", "planId", planId, "listKey", "ApiInternalTrainee");
        return handleEventList(context, mParams);
    }

    /**
     * 内部培训获取可选岗位列表
     *
     * @param context 上下文
     * @param planId  计划ID
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> InternalSuitableWorktypes(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalSuitableWorktypes", "planId", planId, "listKey", "ApiWorktype");
        return handleEventList(context, mParams);
    }

    /**
     * 内部培训小结
     *
     * @param context 上下文
     * @param planId  计划ID
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> InternalSummary(Context context, String planId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalSummary", "planId", planId);
        return handleEventList(context, mParams);
    }

    /**
     * 内部培训更新总结
     *
     * @param context 上下文
     * @param planId  计划ID
     * @param summary 总结
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalUpdateSummary(Context context, String planId, String summary) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalUpdateSummary", "planId", planId, "summary", summary);
        return handleEventMap(context, mParams);
    }

    /**
     * 内部培训上传小结附件
     *
     * @param context  上下文
     * @param planId   计划ID
     * @param fileName 文件名
     * @param content  文件内容
     * @return 返回map结果集
     */
    public HashMap<String, Object> InternalUploadFile(Context context, String planId, String fileName, String content) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "InternalUploadFile", "planId", planId, "fileName", fileName, "content", content);
        return handleEventMap(context, mParams);
    }

    /**
     * 辅导培训参加过的辅导课程
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> CounselingFinishedPlans(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "CounselingFinishedPlans", "listKey", "ApiCounselingPlan");
        return handleEventList(context, mParams);
    }

    /**
     * 辅导培训未结束辅导课程
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> CounselingNotFinishedPlans(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "CounselingNotFinishedPlans", "listKey", "ApiCounselingPlan");
        return handleEventList(context, mParams);
    }

    /**
     * 获取课程类型列表
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetCourseTypeList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetCourseTypeList", "recurField", "SubTypes,Id", "listKey", "ApiCourseType");
        return handleEventList(context, mParams);
    }

    /**
     * 获取可申请课程列表
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetSuitableCourse(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetSuitableCourse", "listKey", "ApiCourse");
        return handleEventList(context, mParams);
    }

    /**
     * 获取离线课程结果
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> GetOfflineCourseResult(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "GetOfflineCourseResult", "recurField", "Course,ApplyId", "listKey", "ApiApply");
        return handleEventList(context, mParams);
    }

    /**
     * 辅导培训辅导类型（包括大类、小类）
     *
     * @param context 上下文
     * @return 返回map结果集
     */
    public ArrayList<HashMap<String, Object>> CounselingCourseTypeList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "CounselingCourseTypeList", "recurField", "SubTypes,Id", "listKey", "ApiCourseType");
        return handleEventList(context, mParams);
    }

    /**
     * 在线课程类型
     *
     * @param context 上下文
     * @return 返回map结果集
     * @author tony
     * created at 2016/12/1 16:11
     */
    public ArrayList<HashMap<String, Object>> OnlineCourseTypes(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "OnlineCourseTypes", "recurField", "SubTypes,Id", "listKey", "ApiTrainOnlineCourseType");
        return handleEventList(context, mParams);
    }

    /**
     * 在线课程列表（目前返回值固定）
     *
     * @param context 上下文
     * @param typeId  返回该类型的所有课程、为空的话返回所有课程；目前暂时返回固定内容
     * @return 返回map结果集
     * @author tony
     * created at 2016/12/1 16:13
     */
    public ArrayList<HashMap<String, Object>> OnlineCourseList(Context context, String typeId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "OnlineCourseList", "typeId", typeId, "listKey", "ApiTrainOnlineCourse");
        return handleEventList(context, mParams);
    }

    /**
     * 选课（目前返回值固定）
     *
     * @param context  上下文
     * @param courseId 课件Id
     * @author tony
     * created at 2016/12/1 16:18
     */
    public HashMap<String, Object> OnlineSelectCourse(Context context, String courseId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "OnlineSelectCourse", "courseId", courseId);
        return handleEventMap(context, mParams);
    }

    /**
     * 课程令牌
     *
     * @author tony
     * @time 2017/3/1 21:55
     **/
    public HashMap<String, Object> OnlineCourseToken(Context context, String courseId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "OnlineCourseToken", "courseId", courseId);
        return handleEventMap(context, mParams);
    }

    /**
     * 直播列表
     *
     * @author tony
     * @time 2017/3/1 21:58
     **/
    public ArrayList<HashMap<String, Object>> MeetList(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "MeetList");
        return handleEventList(context, mParams);
    }

    /**
     * 直播令牌
     *
     * @author tony
     * @time 2017/3/1 21:59
     **/
    public HashMap<String, Object> MeetToken(Context context, String applyId) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "MeetToken", "applyId", applyId);
        return handleEventMap(context, mParams);
    }

    /**
     * 慧龙参数
     *
     * @param context 上下文
     * @author tony
     * @time 2017/1/18 17:39
     **/
    public HashMap<String, Object> WizlongParameter(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "WizlongParameter");
        return handleEventMap(context, mParams);
    }

    /**
     * 更新云信账号
     *
     * @author tony
     * @time 2017/4/28 13:15
     **/
    public HashMap<String, Object> ProfileNimAccount(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ProfileNimAccount");
        return handleEventMap(context, mParams);
    }

    /**
     * 更新邮件
     *
     * @author tony
     * @time 2017/4/28 13:15
     **/
    public HashMap<String, Object> ProfileUpdateEmail(Context context, String email) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ProfileUpdateEmail", "email", email);
        return handleEventMap(context, mParams);
    }

    /**
     * 更新电话号码
     *
     * @author tony
     * @time 2017/4/28 13:16
     **/
    public HashMap<String, Object> ProfileUpdatePhoneNumber(Context context, String phoneNum) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ProfileUpdatePhoneNumber", "phoneNum", phoneNum);
        return handleEventMap(context, mParams);
    }

    /**
     * 上传照片
     *
     * @author tony
     * @time 2017/4/28 13:16
     **/
    public HashMap<String, Object> ProfileUploadPhoto(Context context, String content) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ProfileUploadPhoto", "content", content);
        return handleEventMap(context, mParams);
    }

    /**
     * 加载照片
     *
     * @author tony
     * @time 2017/5/18 11:16
     **/
    public HashMap<String, Object> ProfileGetPhoto(Context context) {
        HashMap<String, String> mParams = getDefaultParams(context, context.getResources().getString(R.string.MethodName), "ProfileGetPhoto");
        return handleEventMap(context, mParams);
    }
}
