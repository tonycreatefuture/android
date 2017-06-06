package com.zhongdasoft.svwtrainnet.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.DownLoadListener;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;

import java.lang.ref.WeakReference;

public class WebViewUtil {
    private WebView webView;

    private WeakReference<? extends BaseActivity> wr;

    private WebViewUtil(WeakReference<? extends BaseActivity> wr) {
        this.wr = wr;
    }

    public static WebViewUtil getInstance(WeakReference<? extends BaseActivity> wr) {
        return new WebViewUtil(wr);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    public void init(String url) {
        try {
            boolean isConnected = NetManager.isNetworkConnected(wr.get());
            if (!isConnected) {
                ToastUtil.show(wr.get(), wr.get().getResources().getString(R.string.showByNetError));
                return;
            }
            webView = (WebView) wr.get().findViewById(R.id.webView);
            webView.setWebChromeClient(new WebChromeClient());
            final ProgressBar progressBar = (ProgressBar) wr.get()
                    .findViewById(R.id.progressBar);
            // 打开页面时， 自适应屏幕
            WebSettings webSettings = webView.getSettings();
            // js
            webSettings.setAppCacheEnabled(true);// 设置启动缓
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

            webView.addJavascriptInterface(wr.get(), "SvwTrainNetJSBridge");
            //设置下载监听
            webView.setDownloadListener(new DownLoadListener(wr.get()));
            //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
            webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                            //表示按返回键时的操作
                            webView.goBack();   //后退
                            //webview.goForward();//前进
                            return true;    //已处理
                        }
                    }
                    return false;
                }
            });
            // WebView加载web资源
            webView.loadUrl(url);
            //回退key
            webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView.canGoBack()) {
                        webView.goBack();
                        return true;//return true表示这个事件已经被消费，不会再向上传播
                    }
                    return false;
                }
            });
            // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }

                // 网页加载开始时调用，显示加载提示旋转进度条
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    // TODO Auto-generated method stub
                    super.onPageStarted(view, url, favicon);
                    progressBar.setVisibility(android.view.View.VISIBLE);
                }

                // 网页加载完成时调用，隐藏加载提示旋转进度条
                @Override
                public void onPageFinished(WebView view, String url) {
                    // TODO Auto-generated method stub
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(android.view.View.GONE);
                }

                // 网页加载失败时调用，隐藏加载提示旋转进度条
                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    // TODO Auto-generated method stub
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    progressBar.setVisibility(android.view.View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finish() {
        WebView webView = (WebView) wr.get().findViewById(R.id.webView);
        if (webView != null) {
            webView.destroy();
        }
    }
}
