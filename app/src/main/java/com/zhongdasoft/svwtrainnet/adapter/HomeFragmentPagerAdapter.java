package com.zhongdasoft.svwtrainnet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.module.home.fragment.FavoriteFragment;
import com.zhongdasoft.svwtrainnet.module.home.fragment.HomeFragment;
import com.zhongdasoft.svwtrainnet.module.home.fragment.MoreFragment;
import com.zhongdasoft.svwtrainnet.module.home.fragment.ScanFragment;
import com.zhongdasoft.svwtrainnet.module.home.fragment.TrainFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/30 11:42
 * 修改人：Administrator
 * 修改时间：2016/11/30 11:42
 * 修改备注：
 */

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentList;
    private WeakReference<? extends BaseActivity> wr;
    int textIds[] = {R.string.menu_home, R.string.menu_function,
            R.string.menu_scan, R.string.menu_train,
            R.string.menu_moreservice};
    private boolean[] fragmentsUpdateFlag = new boolean[]{false, false, false, false, false};
    FragmentManager fm;

    public HomeFragmentPagerAdapter(FragmentManager fm, WeakReference<? extends BaseActivity> wr) {
        super(fm);
        this.fm = fm;
        this.wr = wr;
        fragmentList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return getFragmentInstance(position);
    }

    @Override
    public int getCount() {
        return textIds.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return wr.get().getResources().getString(textIds[position]);
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
            fragment = getFragmentInstance(position);
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

    private Fragment getFragmentInstance(int position) {
        Fragment fragment;
        if (fragmentList.size() <= position) {
            switch (position) {
                case 0:
                    fragment = HomeFragment.newInstance(wr);
                    break;
                case 1:
                    fragment = FavoriteFragment.newInstance(wr);
                    break;
                case 2:
                    fragment = ScanFragment.newInstance(wr);
                    break;
                case 3:
                    fragment = TrainFragment.newInstance(wr);
                    break;
                case 4:
                    fragment = MoreFragment.newInstance(wr);
                    break;
                default:
                    fragment = HomeFragment.newInstance(wr);
                    break;
            }
            fragmentList.add(fragment);
        } else {
            fragment = fragmentList.get(position);
        }
        return fragment;
    }

}
