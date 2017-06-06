package com.netease.nim.uikit.session.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.webkit.WebView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.WebViewUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.lang.ref.WeakReference;

public class WebViewActivity extends UI {
    private WebView webView;
    private WeakReference<? extends AppCompatActivity> wr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String url = intent.getStringExtra("item");

        ToolBarOptions options = new ToolBarOptions();
        options.titleString = title;
        setToolBar(R.id.toolbar, options);

        wr = new WeakReference<>(this);
        webView = (WebView) findViewById(R.id.webView);

        WebViewUtil.getInstance(wr).init(url);
    }

    @Override
    public void onBackPressed() {
        if (null != webView && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        WebViewUtil.getInstance(wr).finish();
        super.onDestroy();
    }
}

