package com.zhongdasoft.svwtrainnet.module.more;

import android.content.Intent;
import android.os.Bundle;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.util.CameraUtil;
import com.zhongdasoft.svwtrainnet.util.ListViewUtil;

import java.lang.ref.WeakReference;

public class MoreSettingsActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);
        ListViewUtil.setProfileListView(wr, false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_more_settings;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_settings;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CameraUtil.getInstance().activityResult(requestCode, resultCode, data, this);
    }
}
