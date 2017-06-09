package com.zhongdasoft.svwtrainnet.module.more;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.module.home.MainActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.NetManager;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.util.WebViewUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class TvContentActivity extends BaseActivity {
    private WebView webView;
    private BaseActivity activity;
    private WeakReference<? extends BaseActivity> wr;
    private boolean noBack = false;
    private float textSize;

    @JavascriptInterface
    public void call(String activityName) {
        //此处应该定义常量对应，同时提供给web页面编写者
        if (TextUtils.equals(activityName, "closeWindow")) {
            readyGoThenKill(MainActivity.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        TextView tvTitle = (TextView) findViewById(R.id.trainnet_title);
        wr = new WeakReference<>(activity);

        webView = (WebView) findViewById(R.id.webView);

        TextView leftBtn = (TextView) findViewById(R.id.trainnet_button_left);
        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    onBackPressed();
                }
            }
        });
        Intent intent = getIntent();
        String url = intent.getStringExtra("item");

        boolean isTv = true;
        String title = intent.getStringExtra("qa");
        if (!StringUtil.isNullOrEmpty(title)) {
            isTv = false;
            tvTitle.setText(title);
        }
        String scan = intent.getStringExtra("scan");
        if (!StringUtil.isNullOrEmpty(scan)) {
            isTv = false;
            tvTitle.setText(scan);
        }
        String slide = intent.getStringExtra("slide");
        if (!StringUtil.isNullOrEmpty(slide)) {
            isTv = false;
            tvTitle.setText(slide);
        }
        String news = intent.getStringExtra("news");
        if (!StringUtil.isNullOrEmpty(news)) {
            isTv = false;
            tvTitle.setText(news);
            String id = intent.getStringExtra("item");
            String accessToken = MySharedPreferences.getInstance().getAccessToken();
            url = MyProperty.getCurrentValue(getResources().getString(R.string.NewsUrl, accessToken, id));
        }

        String payment = intent.getStringExtra("pay");
        if (!StringUtil.isNullOrEmpty(payment)) {
            isTv = false;
            tvTitle.setText(payment);
        }

        String registerRoute = intent.getStringExtra("route");
        if (!StringUtil.isNullOrEmpty(registerRoute)) {
            isTv = false;
            LinearLayout ll = (LinearLayout) findViewById(R.id.include_title);
            ll.setVisibility(View.GONE);
            noBack = true;
//            tvTitle.setText(registerRoute);
        }

        int type = NetManager.getConnectedType(this);
        if (isTv && ConnectivityManager.TYPE_MOBILE == type) {
            ToastUtil.show(this, getResources().getString(R.string.notWifiTv));
        }

        if (isTv) {
            Waiting.show(this, getResources().getString(R.string.Loading));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final HashMap<String, Object> svwTvUrlList = TrainNetWebService.getInstance().GetSvwTvUrl(activity);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Waiting.dismiss();
                            String url = "";
                            if (svwTvUrlList.containsKey("Result")) {
                                url = svwTvUrlList.get("Result").toString();
                            }
                            WebViewUtil.getInstance(wr).init(url);
                        }
                    });
                }
            }).start();
        } else {
            WebViewUtil.getInstance(wr).init(url);
        }

//        final TextView trainnet_title = (TextView) findViewById(R.id.trainnet_title);
//        textSize = 22f;
//        ViewTreeObserver vto = trainnet_title.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onGlobalLayout() {
//                trainnet_title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                trainnet_title.getHeight();
//                double w0 = 0, w1 = 1;
//                while (w1 >= w0 && textSize > 13f) {
//                    w0 = trainnet_title.getWidth();//控件宽度
//                    w1 = trainnet_title.getPaint().measureText(trainnet_title.getText().toString());//文本宽度
//                    //需要换行就显示该控件
//                    textSize = textSize - 1;
//                    trainnet_title.setTextSize(textSize - 1);
//                }
//
//            }
//        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_tv_content;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_tvcontent;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (noBack && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            ToastUtil.show(this, "请点击提交，系统自动返回！");
            return true;//消费掉后退键
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        WebViewUtil.getInstance(wr).finish();
        super.onDestroy();
    }
}
