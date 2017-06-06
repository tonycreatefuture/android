package com.zhongdasoft.svwtrainnet.module.home.fragment;

import android.os.Bundle;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.base.BaseLazyFragment;

import java.lang.ref.WeakReference;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/28 16:21
 * 修改人：Administrator
 * 修改时间：2016/11/28 16:21
 * 修改备注：
 */

public class ScanFragment extends BaseLazyFragment {
    private WeakReference<? extends BaseActivity> wr;

    public static ScanFragment newInstance(WeakReference<? extends BaseActivity> wr) {
        ScanFragment fragment = new ScanFragment();
        fragment.wr = wr;
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_scan;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }

//        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
    }

    @Override
    protected void initRecyclerView() {
    }
}
