package com.zhongdasoft.svwtrainnet.module.trainextra;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.ExtraFragmentPagerAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;

import java.lang.ref.WeakReference;

public class TrainExtraActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
    private ExtraFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ExtraFragmentPagerAdapter(getSupportFragmentManager(), wr);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_extra_plan;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_trainextra;
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isChanged = getIntent().getBooleanExtra("isChanged", false);
        if (isChanged) {
            pagerAdapter.notifyDataSetChanged();
        }
    }
}
