package com.zhongdasoft.svwtrainnet.wizlong;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;

/**
 * 视动课堂详情页面
 * @author Xiang Yong
 * 
 */
public class LiveCourseDetailActivity extends BaseActivity implements
		OnClickListener {

	public static String INTENT_KEY_URL = "url";
	private TextView mTextViewBack;
	private WebView mWebViewDetail;

	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live_courses_detail_activity);

		try {
			url = getIntent().getStringExtra(INTENT_KEY_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initViews();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CookieManager.getInstance().removeAllCookie();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initViews() {
		mWebViewDetail = (WebView) findViewById(R.id.webview_live_course_detail);
		mTextViewBack = (TextView) findViewById(R.id.textview_back);

		mTextViewBack.setOnClickListener(this);
		mWebViewDetail.getSettings().setJavaScriptEnabled(true);//设置使用够执行JS脚本
		mWebViewDetail.getSettings().setBuiltInZoomControls(false);//设置使支持缩放

		mWebViewDetail.loadUrl(url);
		mWebViewDetail.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);// 使用当前WebView处理跳转
                return true;//true表示此事件在此处被处理，不需要再广播  
            }  
        });  
	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.textview_back: // 返回
                {
                    finish();
                    TrainNetApp.getInstance().containerActivity.receiveFinishEvent();
                }
				
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
