package com.zhongdasoft.svwtrainnet.module.home;

import android.content.Intent;
import android.os.Bundle;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;

public class LoginActivity extends BaseActivity {

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getCache().clear();
//        Log.e("---login---", "login clear");
        // 注销清除本地缓存记录
        Intent intent = getIntent();
        String LoginOut = intent.getStringExtra("item");
        if (LoginOut != null) {
            Waiting.dismiss();
            String reason = intent.getStringExtra("reason");
            ToastUtil.show(this, reason);
            clear();
        }
//        throw new NullPointerException();
        Waiting.show(this, getResources().getString(R.string.LoadingUpdate));
        new AppUpdate(new WeakReference<>(this)).checkUpdate();
//        OkHttp.getInstance().GetVersion(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_login;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private synchronized void clear() {
        // 清除本地记录的登录信息
        NIMClient.getService(AuthService.class).logout();
        MySharedPreferences.getInstance().removeAll(this);
        getCache().clear();
        Waiting.dismiss();
    }

    @Override
    public void onBackPressed() {
        ToastUtil.show(this, getResources().getString(R.string.pleaseInputUserPass));
//        super.onBackPressed();
    }
}
