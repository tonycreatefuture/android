package com.zhongdasoft.svwtrainnet.module.more;

import android.os.Bundle;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;

public class MoreSystemInfoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_more_systeminfo;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_systeminfo;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
