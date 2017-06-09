package com.zhongdasoft.svwtrainnet.widget.timer;

import android.content.Context;
import android.os.CountDownTimer;

import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.StringUtil;

public class CurrentTimer extends CountDownTimer {
    private Context context;

    /**
     * @param millisInFuture    表示以秒为单位 倒计时的总数
     *                          <p>
     *                          例如 millisInFuture=1 表示1秒
     * @param countDownInterval 表示 间隔 多少秒 调用一次 onTick 方法
     *                          <p>
     *                          例如: countDownInterval =1 ; 表示每1秒调用一次onTick()
     */
    public CurrentTimer(long millisInFuture, long countDownInterval, Context context) {
        super(1000 * millisInFuture, 1000 * countDownInterval);
        this.context = context;
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onTick(long millisUntilFinished) {
        String timer = MySharedPreferences.getInstance().getString("countTimer");
        if (!StringUtil.isNullOrEmpty(timer)) {
            timer = (Integer.parseInt(timer) + 1) + "";
            MySharedPreferences.getInstance().setStoreString("countTimer", timer);
        }
//        Log.d("---tick---", "second:" + currentTime);
    }
}
