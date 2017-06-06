package com.zhongdasoft.svwtrainnet.imdemo.jsbridge;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * 项目名称： TrainNet
 * 类描述：
 * 创建人：tony
 * 创建时间：2016/12/29 16:38
 * 修改人：tony
 * 修改时间：2016/12/29 16:38
 * 修改备注：
 */

public class WebViewConfig {
    public WebViewConfig() {
    }

    public static final void setWebSettings(Context context, WebSettings settings, String cachePath) {
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setAppCacheMaxSize(8388608L);
        if(!TextUtils.isEmpty(cachePath)) {
            settings.setAppCachePath(cachePath);
        }

        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setSupportZoom(true);
        setDisplayZoomControls(settings);
        settings.setGeolocationEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        if(Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(0);
        }

    }

    private static final void setDisplayZoomControls(WebSettings webSettings) {
        if(Build.VERSION.SDK_INT >= 11) {
            webSettings.setDisplayZoomControls(false);
        } else {
            webSettings.setBuiltInZoomControls(true);
        }

    }

    public static final void setAcceptThirdPartyCookies(WebView webView) {
        if(Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

    }

    public static final void setWebViewAllowDebug(boolean allowDebug) {
        if(allowDebug && Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

    }

    public static final void removeJavascriptInterfaces(WebView webView) {
        if(Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 17) {
            removeJavascriptInterfaces11(webView);
        }

    }

    @TargetApi(11)
    private static final void removeJavascriptInterfaces11(WebView webView) {
        try {
            webView.removeJavascriptInterface("searchBoxJavaBridge_");
            webView.removeJavascriptInterface("accessibility");
            webView.removeJavascriptInterface("accessibilityTraversal");
        } catch (Throwable var2) {
            var2.printStackTrace();
        }

    }
}

