package com.zhongdasoft.svwtrainnet.module.home;

import android.os.Bundle;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.ListViewUtil;

import java.lang.ref.WeakReference;

public class AwaitConfirmActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        ListViewUtil.setCallBackListView(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_await_confirm;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_awaitconfirm;
    }
}
