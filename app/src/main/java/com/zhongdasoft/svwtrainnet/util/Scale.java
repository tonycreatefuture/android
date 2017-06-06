package com.zhongdasoft.svwtrainnet.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;

import java.lang.ref.WeakReference;

public class Scale {

    public static Screen getScreen(WeakReference<? extends Activity> activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.get().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 屏幕宽度（像素）
        int height = metric.heightPixels; // 屏幕高度（像素）
        float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
        // 应用区域的获取
        Rect outRectApplication = new Rect();
        activity.get().getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(outRectApplication);
        // view绘制区域获取
        Rect outRectDraw = new Rect();
        activity.get().getWindow().findViewById(Window.ID_ANDROID_CONTENT)
                .getDrawingRect(outRectDraw);

        height -= (outRectDraw.top - outRectApplication.top);

        Screen screen = new Screen();
        screen.setPxWidth(width);
        screen.setPxHeight(height);
        screen.setDipWidth(Px2Dp(activity.get(), width));
        screen.setDipHeight(Px2Dp(activity.get(), height));
        screen.setDensity(density);
        screen.setDensityDpi(densityDpi);
        return screen;
    }

    public static int getRectangle(BaseActivity activity) {
        int[] screenCenter = getScreenCenterPosition(activity);
        int pQuery = screenCenter[0] * 50 / 360;
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            pQuery = screenCenter[1] * 50 / 360;
        }
        if (pQuery >= 75) {
            pQuery = 75;
        }
        return pQuery;
    }

    public static float getTextSize(BaseActivity activity) {
        int[] screenCenter = getScreenCenterPosition(activity);
        float textSize = screenCenter[0] * 16.0f / 360.0f;
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            textSize = screenCenter[1] * 16.0f / 360.0f;
        }
        if (textSize >= 20.0f) {
            textSize = 20.0f;
        }
        return textSize;
    }

    public static int[] getScreenCenterPosition(BaseActivity activity) {
        int[] location = {0, 0};

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        location[0] = width / 2;
        location[1] = height / 2;

        return location;
    }

    public static int getStatusHeight(BaseActivity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        return statusBarHeight;
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static void setCenterByScale(WeakReference<? extends BaseActivity> activity,
                                        LinearLayout layout, EditText left, EditText right) {
        LinearLayout.LayoutParams lp = null;
        int[] location = Scale.getScreenCenterPosition(activity.get());
        int wDp = Scale.Px2Dp(activity.get(), location[0] * 2);
        int hDp = Scale.Px2Dp(activity.get(), location[1] * 2);

        float h_lr = 0.2f;
        float h_m = 0.6f;
        float v_lr = 0.2f;
        float v_m = 0.6f;

        int width = 400;
        int height = 400;

        float step = 0.1f;
        // 宽度
        if (wDp < width) {
            v_lr = 0.0f;
            v_m = 1.0f;
        } else {
            if (wDp * v_m < width) {
                while (wDp * v_m < width && v_m <= 1) {
                    v_lr = v_lr - step / 2;
                    v_m = v_m + step;
                }
            } else {
                while (wDp * v_m > width && v_m >= 0) {
                    v_lr = v_lr + step / 2;
                    v_m = v_m - step;
                }
            }
        }
        // 高度
        if (hDp < height) {
            v_lr = 0.0f;
            v_m = 1.0f;
        } else {
            if (hDp * h_m < height) {
                while (hDp * h_m < height && h_m <= 1) {
                    h_lr = h_lr - step / 2;
                    h_m = h_m + step;
                }
            } else {
                while (hDp * h_m > height && h_m >= 0) {
                    h_lr = h_lr + step / 2;
                    h_m = h_m - step;
                }
            }
        }
        if (activity.get().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, h_m);
            layout.setLayoutParams(lp);
            lp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, h_lr);
            left.setLayoutParams(lp);
            lp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, h_lr);
            right.setLayoutParams(lp);
        } else {
            lp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, v_m);
            layout.setLayoutParams(lp);
            lp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, v_lr);
            left.setLayoutParams(lp);
            lp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, v_lr);
            right.setLayoutParams(lp);
        }
    }

    public static int getControlWidth(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        int width = v.getMeasuredWidth();
        return width;
    }

    public static int getControlHeight(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        int height = v.getMeasuredHeight();
        return height;
    }

    public static int getControlChildHeight(LinearLayout ll) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = 0;
        for (int i = 0; i < ll.getChildCount(); i++) {
            View v = ll.getChildAt(i);
            v.measure(w, h);
            height += v.getMeasuredHeight();
        }
        return height;
    }
}
