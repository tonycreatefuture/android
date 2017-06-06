package com.zhongdasoft.svwtrainnet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.module.trainextra.ExtraPlanFragment;

import java.lang.ref.WeakReference;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/30 11:42
 * 修改人：Administrator
 * 修改时间：2016/11/30 11:42
 * 修改备注：
 */

public class ExtraFragmentPagerAdapter extends FragmentPagerAdapter {
    private WeakReference<? extends BaseActivity> wr;
    private String[] tabTitles = new String[]{"未完成", "已完成"};
    private boolean[] fragmentsUpdateFlag = new boolean[]{false, false};
    FragmentManager fm;

    public ExtraFragmentPagerAdapter(FragmentManager fm, WeakReference<? extends BaseActivity> wr) {
        super(fm);
        this.fm = fm;
        this.wr = wr;
    }

    @Override
    public Fragment getItem(int position) {
        return ExtraPlanFragment.newInstance(position, wr);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //得到缓存的fragment
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        //得到tag，这点很重要
        String fragmentTag = fragment.getTag();

        if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {
            //如果这个fragment需要更新
            FragmentTransaction ft = fm.beginTransaction();
            //移除旧的fragment
            ft.remove(fragment);
            //换成新的fragment
            fragment = ExtraPlanFragment.newInstance(position, wr);
            //添加新fragment时必须用前面获得的tag，这点很重要
            ft.add(container.getId(), fragment, fragmentTag);
            ft.attach(fragment);
            ft.commit();
            //复位更新标志
            fragmentsUpdateFlag[position % fragmentsUpdateFlag.length] = false;
        }
        return fragment;
    }

    public void setUpdateFlag() {
        for (int i = 0; i < fragmentsUpdateFlag.length; i++) {
            fragmentsUpdateFlag[i] = true;
        }
    }
}
