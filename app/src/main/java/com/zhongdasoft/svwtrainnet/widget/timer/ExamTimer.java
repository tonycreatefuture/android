package com.zhongdasoft.svwtrainnet.widget.timer;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserPaper;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;

import java.lang.ref.WeakReference;

public class ExamTimer extends CountDownTimer {
    private WeakReference<? extends BaseActivity> wr;
    private TextView tv;
    private String dbName;

    /**
     * @param millisInFuture    表示以秒为单位 倒计时的总数
     *                          <p/>
     *                          例如 millisInFuture=1 表示1秒
     * @param countDownInterval 表示 间隔 多少秒 调用一次 onTick 方法
     *                          <p/>
     *                          例如: countDownInterval =1 ; 表示每1秒调用一次onTick()
     */
    public ExamTimer(long millisInFuture, long countDownInterval, WeakReference<? extends BaseActivity> wr, TextView tv, String dbName) {
        super(1000 * millisInFuture, 1000 * countDownInterval);
        this.wr = wr;
        this.tv = tv;
        this.dbName = dbName;
    }

    @Override
    public void onFinish() {
        UserPaper up = DaoQuery.getInstance().findUserPaperByDB(dbName);
        WebserviceUtil.getInstance().submitExam(wr, up);
        tv.setText("done");
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // SharedPreferences sharedPreferences = (SharedPreferences) map
        // .get("sharedPreferences");
        tv.setText(getFormat24HourTime(millisUntilFinished / 1000));
        // 异步执行保存剩余时间，每10秒保存一次
        //if (millisUntilFinished / 1000 % 10 == 0) {
        Long leftSeconds = millisUntilFinished / 1000;
        UserPaper userPaper = DaoQuery.getInstance().getUserPaperByDB(dbName);
        userPaper.setLeftTime(leftSeconds);
        CRUD.getInstance().UpdateUserPaperAsyn(userPaper);
    }

    private String getFormat24HourTime(long l) {
        long min = l / 60;
        long sec = l - min * 60;
        return (min < 10 ? "0" + min : min + "") + ":"
                + (sec < 10 ? "0" + sec : sec + "");
    }

}
