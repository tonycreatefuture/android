package com.netease.nim.uikit.common.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;

import java.lang.ref.WeakReference;

public class WebViewUtil {
    private WebView webView;

    private WeakReference<? extends AppCompatActivity> wr;

    private WebViewUtil(WeakReference<? extends AppCompatActivity> wr) {
        this.wr = wr;
    }

    public static WebViewUtil getInstance(WeakReference<? extends AppCompatActivity> wr) {
        return new WebViewUtil(wr);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    public void init(String url) {
        try {
            boolean isConnected = NetworkUtil.isNetworkConnected(wr.get());
            if (!isConnected) {
                Toast.makeText(wr.get(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                return;
            }
            if (url.contains("?")) {
                url = String.format("%s&token=%s", url, NimUIKit.getToken());
            } else {
                url = String.format("%s?token=%s", url, NimUIKit.getToken());
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

//            webView.addJavascriptInterface(wr.get(), "SvwTrainNetJSBridge");
            //设置下载监听
//            webView.setDownloadListener(new DownLoadListener(wr.get()));
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
                    progressBar.setVisibility(View.VISIBLE);
                }

                // 网页加载完成时调用，隐藏加载提示旋转进度条
                @Override
                public void onPageFinished(WebView view, String url) {
                    // TODO Auto-generated method stub
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                }

                // 网页加载失败时调用，隐藏加载提示旋转进度条
                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    // TODO Auto-generated method stub
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

//                    handler.cancel(); //默认的处理方式，WebView变成空白页
                    handler.proceed();//接受证书

                  //handleMessage(Message msg); 其他处理
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
