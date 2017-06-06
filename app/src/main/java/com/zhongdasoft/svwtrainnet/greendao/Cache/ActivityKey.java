package com.zhongdasoft.svwtrainnet.greendao.Cache;

import com.zhongdasoft.svwtrainnet.module.exam.TestCenterActivity;
import com.zhongdasoft.svwtrainnet.module.favorite.FuncAddActivity;
import com.zhongdasoft.svwtrainnet.module.home.LoginActivity;
import com.zhongdasoft.svwtrainnet.module.more.MoreAboutUsActivity;
import com.zhongdasoft.svwtrainnet.module.more.MoreSettingsActivity;
import com.zhongdasoft.svwtrainnet.module.more.TvContentActivity;
import com.zhongdasoft.svwtrainnet.module.trainextra.TrainExtraActivity;
import com.zhongdasoft.svwtrainnet.module.traininner.TrainInnerActivity;
import com.zhongdasoft.svwtrainnet.module.trainnormal.NormalApplyActivity;
import com.zhongdasoft.svwtrainnet.module.trainnormal.NormalCallBackActivity;
import com.zhongdasoft.svwtrainnet.module.trainnormal.NormalCourseListActivity;
import com.zhongdasoft.svwtrainnet.module.trainnormal.NormalScoreActivity;
import com.zhongdasoft.svwtrainnet.module.trainnormal.TrainNormalActivity;
import com.zhongdasoft.svwtrainnet.module.trainonline.MeetingActivity;
import com.zhongdasoft.svwtrainnet.module.trainonline.TrainOnLineActivity;
import com.zhongdasoft.svwtrainnet.module.trainonline.TrainPlanActivity;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/30 13:40
 * 修改人：Administrator
 * 修改时间：2016/11/30 13:40
 * 修改备注：
 */

public class ActivityKey {
    public static final String Login = LoginActivity.class.getName();
    public static final String MainHome = "MainHome";
    public static final String MainScan = "MainScan";

    public static final String MainTrain = "MainTrain";
    public static final String Normal = TrainNormalActivity.class.getName();
    public static final String CourseList = NormalCourseListActivity.class.getName();
    public static final String Apply = NormalApplyActivity.class.getName();
    public static final String CallBack = NormalCallBackActivity.class.getName();
    public static final String Score = NormalScoreActivity.class.getName();

    public static final String Inner = TrainInnerActivity.class.getName();
    public static final String Extra = TrainExtraActivity.class.getName();
    public static final String Exam = TestCenterActivity.class.getName();
    public static final String OnLineTV = TvContentActivity.class.getName();
    public static final String Meeting = MeetingActivity.class.getName();

    public static final String OnLine = TrainOnLineActivity.class.getName();
    public static final String Plan = TrainPlanActivity.class.getName();

    public static final String MainMore = "MainMore";
    public static final String Setting = MoreSettingsActivity.class.getName();
    public static final String AboutUs = MoreAboutUsActivity.class.getName();

    public static final String MainFavorite = "MainFavorite";
    public static final String Add = FuncAddActivity.class.getName();
}
