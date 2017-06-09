package com.zhongdasoft.svwtrainnet.util;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserPaper;
import com.zhongdasoft.svwtrainnet.module.exam.TestCenterActivity;
import com.zhongdasoft.svwtrainnet.module.home.LoginAfterActivity;
import com.zhongdasoft.svwtrainnet.module.more.TvContentActivity;
import com.zhongdasoft.svwtrainnet.network.OkHttp;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.widget.timer.CurrentTimer;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/8/23 16:19
 * 修改人：Administrator
 * 修改时间：2016/8/23 16:19
 * 修改备注：
 */
public class WebserviceUtil {
    private static WebserviceUtil instance;

    private WebserviceUtil() {
    }

    /**
     * 单例模式
     */
    public synchronized static WebserviceUtil getInstance() {
        if (null == instance) {
            instance = new WebserviceUtil();
        }
        return instance;
    }

    public void submitEvaluation(final WeakReference<? extends BaseActivity> wr, final String activityId, String jsonData) {
        final BaseActivity activity = wr.get();
        String evaluationID = MySharedPreferences.getInstance().getString(activity.getResources().getString(R.string.evaluationID));
        if (!NetManager.isNetworkConnected(activity)) {
            MySharedPreferences.getInstance().setStoreString(activity.getResources().getString(R.string.evaluationID), evaluationID + activityId + ",");
            MySharedPreferences.getInstance().setStoreString(activityId, jsonData);
            ToastUtil.show(activity, activity.getResources().getString(R.string.submitRating));
            return;
        }
        if (evaluationID.contains(activityId + ",")) {
            evaluationID = evaluationID.replace(activityId + ",", "");
            MySharedPreferences.getInstance().removeString(activityId);
            MySharedPreferences.getInstance().setStoreString(activity.getResources().getString(R.string.evaluationID), evaluationID);
        }

        final String myData = PhoneInfo.getInstance().getEvaluationAnswersXML(jsonData);
        Waiting.show(activity, activity.getResources().getString(R.string.LoadingEvaluate));
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> myMap = TrainNetWebService.getInstance().EvaluationSubmit(activity, activityId, myData);
                final String returnCode = myMap.get(activity.getResources().getString(R.string.ReturnCode)).toString();
                final String message = myMap.get(activity.getResources().getString(R.string.Message)).toString();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        DialogUtil.getInstance().showDialog(wr, activity.getResources().getString(R.string.tips), message, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog alertDialog = (AlertDialog) v.getTag();
                                alertDialog.dismiss();
                                if ("0".equals(returnCode)) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("isChanged", true);
                                    activity.onBackPressed();
                                }
                            }
                        }, null);
                    }
                });
            }
        }).start();
    }

    public void submitExam(final WeakReference<? extends BaseActivity> wr, final UserPaper up) {
        final BaseActivity activity = wr.get();
        Waiting.show(activity, activity.getResources().getString(R.string.LoadingUpdateScore));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String scoreInfo = DaoQuery.getInstance().getUserQuestionScoreInfo(up.getDbName());
                Float score = DaoQuery.getInstance().getUserQuestionScore(up.getDbName());
                String userAnswer = DaoQuery.getInstance().getUserQuestionAnswer(up.getDbName());
                up.setScore(score);
                up.setScoreInfo(scoreInfo);
                up.setUserAnswer(userAnswer);
                CRUD.getInstance().UpdateUserPaper(up);

                String currentTime = MySharedPreferences.getInstance().getCurrentTime();
                currentTime = currentTime.replace(" ", "T");
                String examXml = PhoneInfo.getInstance().getExamXML(up.getExamineeId(), up.getPaperId(), up.getRealBeginTime().replace(" ", "T"), currentTime, score + "", userAnswer);
                final HashMap<String, Object> mapSubmitPaper = TrainNetWebService.getInstance().SubmitGrade(activity, examXml);
                int ReturnCode = Integer.parseInt(mapSubmitPaper.get(activity.getResources().getString(R.string.ReturnCode)).toString());
                String Message = mapSubmitPaper.get(activity.getResources().getString(R.string.Message)).toString();
                if (0 == ReturnCode) {
                    up.setStatus(3);
                    if (StringUtil.isNullOrEmpty(up.getRealEndTime())) {
                        up.setRealEndTime(currentTime);
                    }
                    Message = "您的成绩已提交";
                } else if (1 == ReturnCode) {
                    up.setStatus(3);
                } else {
                    up.setStatus(2);
                    up.setRealEndTime(currentTime);
                }
                CRUD.getInstance().UpdateUserPaper(up);
                final String errMsg = Message;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        int status = DaoQuery.getInstance().getUserPaperStatus(up.getDbName());
                        if (status >= 2) {
                            if (!up.getShowScore()) {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("isChanged", true);
                                activity.readyGoThenKill(TestCenterActivity.class, bundle);
                            } else {
                                String name = MySharedPreferences.getInstance().getName();
                                DialogUtil.getInstance().showScoreDialog(wr, name + "(" + up.getUserName() + ")", up.getDbName());
                            }
                        } else {
                            ToastUtil.show(activity, errMsg);
                        }
                    }
                });
            }
        }).start();
    }

    public boolean prepareRegisterRoute(String courseNo, String startDate, String currentTime) {
        if (startDate.substring(0, 10).compareTo(currentTime.substring(0, 10)) > 0 && ("0132".equals(courseNo) || "0679".equals(courseNo))) {
            return true;
        }
        return false;
    }

    public void registerRoute(final WeakReference<? extends BaseActivity> wr, String applyId) {
        BaseActivity activity = wr.get();
        String url = activity.getResources().getString(R.string.RegisterUrl, applyId);
        Bundle bundle = new Bundle();
        bundle.putString("item", url);
        bundle.putString("route", activity.getResources().getString(R.string.course_register));
        activity.readyGoThenKill(TvContentActivity.class, bundle);
    }

    public void Login(BaseActivity activity) {
        //初始化用户菜单
        String userName = MySharedPreferences.getInstance().getString("userName");
        String isUpdate = MySharedPreferences.getInstance().getString("isUpdate");
        //菜单表有数据且当前没有升级app时，不操作数据表
        if (DaoQuery.getInstance().getUserMenuCount() <= 0 || !StringUtil.isNullOrEmpty(isUpdate)) {
            CRUD.getInstance().InitUserMenu(activity.getResources());
            MySharedPreferences.getInstance().removeString("isUpdate");
        }
        //设置是否显示菜单
        String post = MySharedPreferences.getInstance().getPost();
        UserMenu userMenu = DaoQuery.getInstance().getUserMenuByResName(activity.getResources().getString(R.string.TrainInner));
        userMenu.setIsValid(1);
        if (!StringUtil.isNullOrEmpty(post)) {
            if ("0".equals(MyProperty.getValueByKey("PublishMode", "0"))) {
                if (!post.contains("培训经理")) {
                    userMenu.setIsValid(0);
                }
            } else {
                if (!post.contains("内训师")) {
                    userMenu.setIsValid(0);
                }
            }
        } else {
            userMenu.setIsValid(0);
        }
        CRUD.getInstance().UpdateUserMenu(userMenu);
        if (!DaoQuery.getInstance().existUserFavorite(userName)) {
            CRUD.getInstance().InitUserFavorite(userName, ActivityKey.Add);
        }
        //设置计数器
        CurrentTimer mc = null;
        if (mc != null) {
            mc.cancel();
        }
        MySharedPreferences.getInstance().setStoreString("countTimer", "0");
        mc = new CurrentTimer(MyProperty.MaxValue, 1, activity);
        mc.start();
        //设置行程登记
        String RouterRegister = TrainNetApp.getCache().getAsString(CacheKey.RouterRegister);
        if (StringUtil.isNullOrEmpty(RouterRegister)) {
            OkHttp.getInstance().GetConfirmList(activity);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("ApplyId", RouterRegister);
            activity.readyGoThenKill(LoginAfterActivity.class, bundle);
        }
    }

    public ArrayList<HashMap<String, Object>> onlineCourseList(BaseActivity activity, ArrayList<HashMap<String, Object>> courseList, Boolean unfinished) {
        HashMap<String, Object> map = null;
        HashMap<String, Object> coursewareMap = null;
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        for (HashMap<String, Object> course : courseList) {
            if ("ApiTrainOnlineCourse".equals(course.get(activity.getResources().getString(R.string.ParentNode)))) {
                boolean selected = Boolean.parseBoolean(course.get("Selected").toString());
                map = new HashMap<>();
                map.put("required", course.get("Required").toString());
                map.put("isPackageCourse", course.get("IsPackageCourse").toString());
                map.put("courseId", course.get("CourseId").toString());
                map.put("instruction", null == course.get("Instruction") ? "" : course.get("Instruction").toString());
                map.put("title", course.get("CourseName").toString());
                map.put("courseNo", course.get("CourseNo").toString());
                map.put("courseTypeId", course.get("CourseTypeId").toString());
                if (selected) {
                    map.put("progress_event", activity.getResources().getString(R.string.studyProgress));
                } else {
                    map.put("select_event", activity.getResources().getString(R.string.selectCourse));
                }
            } else if ("ApiTrainOnlineCourseware".equals(course.get(activity.getResources().getString(R.string.ParentNode)))) {
                if (map.containsKey("coursewareId")) {
                    coursewareMap = new HashMap<>();
                    coursewareMap.putAll(map);
                } else {
                    coursewareMap = map;
                }
                coursewareMap.put("screen", course.get("Screen").toString());
                coursewareMap.put("period", course.get("Period").toString());
                coursewareMap.put("playType", course.get("PlayType").toString());
                coursewareMap.put("creditHour", course.get("CreditHour").toString());
                coursewareMap.put("finishTime", course.get("FinishTime").toString());
                coursewareMap.put("firstTime", course.get("FirstTime").toString());
                coursewareMap.put("lastTime", course.get("LastTime").toString());
                coursewareMap.put("finishNum", course.get("FinishNum").toString());
                coursewareMap.put("chapterNum", course.get("ChapterNum").toString());
                coursewareMap.put("coursewareId", course.get("CoursewareId").toString());
                coursewareMap.put("info1", "开始学习时间：" + course.get("FirstTime").toString().replace("T", " ").substring(0, 16));
                coursewareMap.put("info2", "最后学习时间：" + course.get("LastTime").toString().replace("T", " ").substring(0, 16));
                int finishNum = Integer.parseInt(course.get("FinishNum").toString());
                int chapterNum = Integer.parseInt(course.get("ChapterNum").toString());
                map.put("info3Label", "学习进度：");
                if (!unfinished) {
                    if (0 == chapterNum) {
                        coursewareMap.put("info3", 0);
                        coursewareMap.put("info3Percent", "0%");
                    } else {
                        if (finishNum * 100 / chapterNum < 100) {
                            coursewareMap.put("info3", finishNum * 100 / chapterNum + "");
                            coursewareMap.put("info3Percent", finishNum * 100 / chapterNum + "%");
                        } else {
                            coursewareMap.put("info3", 100 + "");
                            coursewareMap.put("info3Percent", "100%");
                        }
                    }
                    listItem.add(coursewareMap);
                } else {
                    if (coursewareMap.containsKey("progress_event") && chapterNum > 0 && finishNum * 100 / chapterNum < 100) {
                        coursewareMap.put("info3", finishNum * 100 / chapterNum + "");
                        coursewareMap.put("info3Percent", finishNum * 100 / chapterNum + "%");
                        listItem.add(coursewareMap);
                    }
                }
            } else {

            }
        }
        return listItem;
    }

    public void profileCellNumber(final BaseActivity activity, final String cellNumber) {
        Waiting.show(activity, activity.getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> resultMap = TrainNetWebService.getInstance().ProfileUpdatePhoneNumber(activity, cellNumber);
                profileRefresh(activity);
                final String msg = resultMap.get(activity.getResources().getString(R.string.Message)).toString();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(activity, msg);
                        Waiting.dismiss();
                    }
                });
            }
        }).start();
    }

    public void profileEmail(final BaseActivity activity, final String email) {
        Waiting.show(activity, activity.getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> resultMap = TrainNetWebService.getInstance().ProfileUpdateEmail(activity, email);
                profileRefresh(activity);
                final String msg = resultMap.get(activity.getResources().getString(R.string.Message)).toString();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(activity, msg);
                        Waiting.dismiss();
                    }
                });
            }
        }).start();
    }

    public void profileRefresh(final BaseActivity activity) {
        HashMap<String, Object> profileMap = new HashMap<>();
        ArrayList<HashMap<String, Object>> profileList = TrainNetWebService.getInstance().GetProfile(activity);
        StringBuilder sbPost = new StringBuilder();
        for (HashMap<String, Object> map : profileList) {
            if (!"ApiIdValuePairOfInt32String".equals(map.get(activity.getResources().getString(R.string.ParentNode)))) {
                for (String key : map.keySet()) {
                    if (!activity.getResources().getString(R.string.ParentNode).equals(key)) {
                        profileMap.put(key, StringUtil.objectToStr(map.get(key)));
                    }
                }
            } else {
                if (null != map.get("Value") && !StringUtil.isNullOrEmpty(map.get("Value").toString())) {
                    if (sbPost.length() > 0) {
                        sbPost.append(",");
                    }
                    sbPost.append(map.get("Value").toString());
                }
            }
        }
        profileMap.put("post", sbPost.toString());
        TrainNetApp.getCache().put(CacheKey.ProfileRefresh, TrainNetApp.getGson().toJson(profileMap));
    }

    public void profilePhotoRefresh(final BaseActivity activity) {
        HashMap<String, Object> profilePhotoMap = TrainNetWebService.getInstance().ProfileGetPhoto(activity);
        Object oFilePath;
        String cameraFilePath = CameraUtil.getInstance().getCameraFilePath();
        String cameraFileName = CameraUtil.getInstance().getCameraFileName();
        if (null != (oFilePath = profilePhotoMap.get(activity.getResources().getString(R.string.Result)))) {
            FileUtil.getInstance().saveFileFromHttp(oFilePath.toString(), cameraFileName, cameraFilePath);
        } else {
            File f = new File(cameraFilePath + cameraFileName);
            f.delete();
        }
    }
}
