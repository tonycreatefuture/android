package com.zhongdasoft.svwtrainnet.module.trainonline;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.util.WebViewUtil;

import java.lang.ref.WeakReference;

public class TrainPlanActivity extends BaseActivity {
//    private WebView webView;
    private BaseActivity activity;
    private WeakReference<? extends BaseActivity> wr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        wr = new WeakReference<>(activity);

//        webView = (WebView) findViewById(R.id.webView);

        TextView leftBtn = (TextView) findViewById(R.id.trainnet_button_left);
        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                if (webView.canGoBack()) {
//                    webView.goBack();
//                } else {
                    onBackPressed();
//                }
            }
        });
        String url = "https://download.zhongdasoft.com/e/eclass/teachplanList.aspx";

        WebViewUtil.getInstance(wr).init(url);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_trainplan;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_trainplan;
    }

    @Override
    public void onDestroy() {
        WebViewUtil.getInstance(wr).finish();
        super.onDestroy();
    }
}
