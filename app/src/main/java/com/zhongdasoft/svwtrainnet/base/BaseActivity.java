package com.zhongdasoft.svwtrainnet.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        int mTitle = getMTitle();
        if (mTitle > 0) {
            TextView tvTitle = (TextView) findViewById(R.id.trainnet_title);
            tvTitle.setText(mTitle);
        }

        TextView leftBtn = (TextView) findViewById(R.id.trainnet_button_left);
        if (null != leftBtn) {
            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    onBackPressed();
                }
            });
        }
    }

    protected abstract int getLayoutId();

    protected abstract int getMTitle();

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    @Override
    protected void onDestroy() {
//        Waiting.dismiss();
        super.onDestroy();
    }

    /**
     * 使应用中的字体不受系统设置影响
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * startActivity
     *
     * @param clazz
     */
    public void readyGo(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    public void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     * @param bundleName
     */
    public void readyGo(Class<?> clazz, Bundle bundle, String bundleName) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtra(bundleName, bundle);
        }
        startActivity(intent);
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    /**
     * startActivity then finish
     *
     * @param clazz
     */
    public void readyGoThenKill(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    /**
     * startActivity with bundle then finish
     *
     * @param clazz
     * @param bundle
     */
    public void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (this.getClass().getName().equals(ActivityKey.Login)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
        }
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    public void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    public void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    public void continuePreview() {
    }

    public com.zhongdasoft.svwtrainnet.widget.zxingview.ViewfinderView getViewfinderView() {
        return null;
    }

    public void handleDecode(com.google.zxing.Result result, Bitmap barcode) {

    }

    public android.os.Handler getHandler() {
        return null;
    }
}
