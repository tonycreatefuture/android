package com.zhongdasoft.svwtrainnet.module.home;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.EvaluateFragmentPagerAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;

import java.lang.ref.WeakReference;

public class AwaitEvaluateActivity extends BaseActivity {
    private EvaluateFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WeakReference<? extends BaseActivity> wr = new WeakReference<>(this);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new EvaluateFragmentPagerAdapter(getSupportFragmentManager(), wr);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_await_evaluatetabs;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_awaitevaluate;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getCache().put(CacheKey.HomeRefresh, "101");
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isChanged = getIntent().getBooleanExtra("isChanged", false);
        if (isChanged) {
            pagerAdapter.setUpdateFlag();
            pagerAdapter.notifyDataSetChanged();
        }
    }
}
