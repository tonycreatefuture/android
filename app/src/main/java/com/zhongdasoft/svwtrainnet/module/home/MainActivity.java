package com.zhongdasoft.svwtrainnet.module.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.adapter.HomeFragmentPagerAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.imdemo.DemoCache;
import com.zhongdasoft.svwtrainnet.imdemo.main.activity.ChatMainActivity;
import com.zhongdasoft.svwtrainnet.module.more.ScanActivity;
import com.zhongdasoft.svwtrainnet.util.CameraUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.widget.NoScrollViewPaper;
import com.zhongdasoft.svwtrainnet.widget.badge.BadgeFactory;
import com.zhongdasoft.svwtrainnet.widget.badge.BadgeView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by tony on 16-12-2.
 */

public class MainActivity extends BaseActivity {
    int textIds[] = {R.string.menu_home, R.string.menu_function,
            R.string.menu_scan, R.string.menu_train,
            R.string.menu_moreservice};
    int drawableIds[] = {R.mipmap.tab_home_unselect,
            R.mipmap.tab_favorite_unselect, R.mipmap.tab_scan_unselect,
            R.mipmap.tab_train_unselect, R.mipmap.tab_more_unselect};
    int selDrawableIds[] = {R.mipmap.tab_home_select,
            R.mipmap.tab_favorite_select, R.mipmap.tab_scan_select,
            R.mipmap.tab_train_select, R.mipmap.tab_more_select};
    /**
     * 用户状态变化
     */
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                Bundle bundle = new Bundle();
                bundle.putString("item", "1");
                bundle.putString("reason", getResources().getString(R.string.loginByOtherDevice));
                readyGoThenKill(LoginActivity.class, bundle);
            } else {
            }
        }
    };
    private WeakReference<? extends BaseActivity> wr;
    private HomeFragmentPagerAdapter pagerAdapter;
    private NoScrollViewPaper viewPager;
    private TabLayout tabLayout;
    private TextView title;
    private TextView subTitle;
    private ImageButton msg;
    private ImageButton profile;
    private BadgeView badgeView;
    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            unreadNumChanged(true);
        }
    };
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);
        title = (TextView) findViewById(R.id.trainnet_title);
        subTitle = (TextView) findViewById(R.id.trainnet_subTitle);
        msg = (ImageButton) findViewById(R.id.trainnet_msg);
        badgeView = BadgeFactory.create(this);
        badgeView
                .setTextColor(Color.WHITE)
                .setWidthAndHeight(15, 15)
                .setSpace(5, 5)
                .setBadgeBackground(Color.RED)
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setShape(BadgeView.SHAPE_CIRCLE)
                .bind(msg);
        badgeView.setVisibility(View.GONE);

        profile = (ImageButton) findViewById(R.id.trainnet_button_right);
        viewPager = (NoScrollViewPaper) findViewById(R.id.pager);
        pagerAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), wr);
        viewPager.setNoScroll(true);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(textIds.length - 1);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        setCustomerView();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setTabUnSelected(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        registerObservers(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_main;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }

    public void setCustomerView() {
        if (tabLayout.getTabCount() != drawableIds.length) {
            return;
        }
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.trainnet_item_imagetext, null);
            tabLayout.getTabAt(i).setTag(i);
            tabLayout.getTabAt(i).setCustomView(view);
        }
        for (int i = 0; i < drawableIds.length; i++) {
            setImageText(i, false);
        }
        setImageText(0, true);
    }

    private void setImageText(int pos, boolean isSelected) {
        ImageView iv = (ImageView) tabLayout.getTabAt(pos).getCustomView().findViewById(R.id.im_image);
        TextView tv = (TextView) tabLayout.getTabAt(pos).getCustomView().findViewById(R.id.tv_menu);
        tv.setText(textIds[pos]);
        if (0 == pos) {
            title.setText(R.string.company_cn);
            subTitle.setVisibility(View.VISIBLE);
            profile.setVisibility(View.VISIBLE);
            msg.setVisibility(View.VISIBLE);
            unreadNumChanged(false);
        } else {
            title.setText(textIds[pos]);
            subTitle.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);
            profile.setVisibility(View.INVISIBLE);
            badgeView.setVisibility(View.GONE);
        }
        if (isSelected) {
            iv.setImageResource(selDrawableIds[pos]);
            tv.setTextColor(getResources().getColor(R.color.app_blue));
        } else {
            iv.setImageResource(drawableIds[pos]);
            tv.setTextColor(getResources().getColor(R.color.black));
        }
    }

    public void setTabSelected(TabLayout.Tab tab) {
        int position = Integer.parseInt(tab.getTag().toString());
        if (2 == position) {
            int lastPos = viewPager.getCurrentItem();
            TrainNetApp.getCache().put(CacheKey.HomeRefresh, lastPos + "");
            wr.get().readyGo(ScanActivity.class);
        } else {
            setImageText(position, true);
            viewPager.setCurrentItem(position, false);
        }
    }

    public void setTabUnSelected(TabLayout.Tab tab) {
        int position = Integer.parseInt(tab.getTag().toString());
        setImageText(position, false);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.show(getApplicationContext(), "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void gotoChat(View view) {
        readyGo(ChatMainActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String pos = TrainNetApp.getCache().getAsString(CacheKey.HomeRefresh);
        if (!StringUtil.isNullOrEmpty(pos)) {
            TrainNetApp.getCache().remove(CacheKey.HomeRefresh);
            int iPos = Integer.parseInt(pos);
            if (iPos > 100) {
                pagerAdapter.setUpdateFlag();
                iPos = 0;
            }
            setImageText(iPos, true);
            viewPager.setCurrentItem(iPos, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        badgeView.unbind();
        super.onDestroy();
        registerObservers(false);
    }

    public void unreadNumChanged(boolean isObserver) {
        if (null == DemoCache.getAccount()) {
            return;
        }
        int unreadNum;
        if (isObserver) {
            unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();
            TrainNetApp.getCache().put(CacheKey.NimUnreadNum, unreadNum + "");
        } else {
            String strUnreadNum = TrainNetApp.getCache().getAsString(CacheKey.NimUnreadNum);
            if (StringUtil.isNullOrEmpty(strUnreadNum)) {
                unreadNum = 0;
            } else {
                unreadNum = Integer.parseInt(strUnreadNum);
            }
        }
        if (unreadNum > 0) {
            if (msg.getVisibility() == View.VISIBLE) {
                badgeView.setVisibility(View.VISIBLE);
                if (unreadNum < 10) {
                    badgeView.setTextSize(10);
                } else if (unreadNum >= 10 && unreadNum < 100) {
                    badgeView.setTextSize(8);
                } else {
                    badgeView.setTextSize(6);
                }
                badgeView.setBadgeCount(unreadNum);
            } else {
                badgeView.setVisibility(View.GONE);
            }
        } else {
            badgeView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CameraUtil.getInstance().activityResult(requestCode, resultCode, data, this);
    }

    /**
     * ********************** 收消息，处理状态变化 ************************
     */
    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeRecentContact(messageObserver, register);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
    }
}
