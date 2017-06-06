package com.zhongdasoft.svwtrainnet.module.traininner;

import android.support.v4.app.Fragment;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.widget.imageload.AbsSingleFragmentActivity;
import com.zhongdasoft.svwtrainnet.widget.imageload.ListImgFragment;

public class UploadImageFileActivity extends AbsSingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ListImgFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_single_fragment;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }
}
