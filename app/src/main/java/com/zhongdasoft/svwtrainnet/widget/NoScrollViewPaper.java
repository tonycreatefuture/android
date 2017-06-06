package com.zhongdasoft.svwtrainnet.widget;

import android.content.Context;

import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/6 11:19
 * 修改人：tony
 * 修改时间：2016/12/6 11:19
 * 修改备注：
 */

public class NoScrollViewPaper extends ViewPager {
    private boolean noScroll = false;
    public NoScrollViewPaper(Context context) {
        super(context);
    }

    public NoScrollViewPaper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}
