package com.zhongdasoft.svwtrainnet.module.home.fragment;

import android.os.Bundle;
import android.view.View;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.base.BaseLazyFragment;
import com.zhongdasoft.svwtrainnet.module.home.HomeHandler;
import com.zhongdasoft.svwtrainnet.module.home.MainActivity;
import com.zhongdasoft.svwtrainnet.util.Scale;
import com.zhongdasoft.svwtrainnet.util.Screen;
import com.zhongdasoft.svwtrainnet.widget.ImageCycleView;

import java.lang.ref.WeakReference;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/28 16:20
 * 修改人：Administrator
 * 修改时间：2016/11/28 16:20
 * 修改备注：
 */

public class HomeFragment extends BaseLazyFragment {
    WeakReference<? extends BaseActivity> wr;
    private ImageCycleView mAdView;
//    private MainActivity activity;

    public static HomeFragment newInstance(WeakReference<? extends BaseActivity> wr) {
        HomeFragment fragment = new HomeFragment();
        fragment.wr = wr;
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
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
//        activity = (MainActivity) getActivity();
        View v_body1 = wr.get().findViewById(R.id.home_body1);
        Screen screen = Scale.getScreen(wr);
        v_body1.getLayoutParams().height = (int) (224 / 640f * screen.getPxWidth());
        mAdView = (ImageCycleView) wr.get().findViewById(R.id.ad_view);
        new HomeHandler(wr, (MainActivity) getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mAdView) {
            mAdView.startImageCycle();
        }
//        enableMsgNotification(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mAdView) {
            mAdView.pushImageCycle();
        }
//        enableMsgNotification(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mAdView) {
            mAdView.pushImageCycle();
        }
    }
}
