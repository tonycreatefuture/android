package com.zhongdasoft.svwtrainnet.module.home;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class LoginAfterActivity extends BaseActivity {
    private int count1 = 6;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private TextView tv;
    private Handler handler1;
    private WeakReference<? extends BaseActivity> wr;
    private String applyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyId = this.getIntent().getStringExtra("ApplyId");
        wr = new WeakReference<>(this);

        RelativeLayout rvl = (RelativeLayout) findViewById(R.id.rl1);
        rvl.setClickable(true);
        rvl.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    //当前有可用网络
                    WebserviceUtil.getInstance().registerRoute(wr, applyId);
                } else {
                    //当前无可用网络
                    readyGoThenKill(MainActivity.class);
                }
            }
        });


        tv = (TextView) findViewById(R.id.time1);
        tv.setClickable(false);
        tv.setText(Html.fromHtml("<font color=\"red\">&nbsp;" + (count1 - 1) + "</font>" + "<font color=\"white\">&nbsp;秒</font>"));
        tv.setTextSize(24);
        tv.getBackground().setAlpha(100);

        handler1 = new Handler() {
            public void handleMessage(Message msg) {
                tv.setText(Html.fromHtml("<font color=\"red\">&nbsp;" + msg.what + "</font>" + "<font color=\"white\">&nbsp;秒</font>"));
                if (msg.what == 0) {
                    mTimer.cancel();
                    tv.setText(Html.fromHtml("<u>关&nbsp;闭</u>"));
                    tv.setClickable(true);

                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            readyGoThenKill(MainActivity.class);//修改一下
                        }
                    });
                }
            }
        };

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                count1--;
                Message msg = handler1.obtainMessage();
                msg.what = count1;
                handler1.sendMessage(msg);
            }
        };

        //开始一个定时任务
        mTimer.schedule(mTimerTask, 100, 1000);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_after;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        WebserviceUtil.getInstance().registerRoute(wr, applyId);
    }
}
