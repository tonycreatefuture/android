package com.zhongdasoft.svwtrainnet.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtil {
    /**
     * imageview or imagebutton used
     *
     * @param oriBmp 原始图片
     * @return bitmap
     */
    public static Bitmap getMsgBitmap(Bitmap oriBmp, int maxRadius) {
        // 初始化画布
        Bitmap bmp = oriBmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bmp);

        int width = oriBmp.getWidth();
        int height = oriBmp.getHeight();

        int radius = 10;

        // 拷贝图片
        Paint paint = new Paint();
        paint.setDither(true);
        // 防抖动
        paint.setFilterBitmap(true);
        // 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
        Rect src = new Rect(0, 0, width, height);
        Rect dst = new Rect(0, 0, width, height);
        canvas.drawBitmap(bmp, src, dst, paint);

        Paint resPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        resPaint.setColor(Color.RED);
        canvas.drawCircle(maxRadius - 2 * radius, 2 * radius, radius, resPaint);

        return bmp;
    }

    /**
     * imageview or imagebutton used
     *
     * @param oriBmp 原始图片
     * @param count  显示数目，大于10显示…
     * @return bitmap
     */
    public static Bitmap getMsgCountBitmap(Bitmap oriBmp, int maxRadius,
                                           int count) {
        if (count == 0) {
            return oriBmp;
        }
        // 初始化画布
        Bitmap bmp = oriBmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bmp);

        int width = oriBmp.getWidth();
        int height = oriBmp.getHeight();

        int radius = (int) (maxRadius * 0.1f);
        int textSize = radius + 3;

        // 拷贝图片
        Paint paint = new Paint();
        paint.setDither(true);
        // 防抖动
        paint.setFilterBitmap(true);
        // 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
        Rect src = new Rect(0, 0, width, height);
        Rect dst = new Rect(0, 0, width, height);
        canvas.drawBitmap(bmp, src, dst, paint);

        Paint resPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        resPaint.setColor(Color.RED);
        canvas.drawCircle(maxRadius - 2 * radius, 2 * radius, radius, resPaint);

        // 启用抗锯齿和使用设备的文本字距
        Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        countPaint.setColor(Color.WHITE);
        countPaint.setTextSize(18f);
        countPaint.setTypeface(Typeface.DEFAULT_BOLD);
        String countStr = String.valueOf(count);
        if (count >= 10) {
            countStr = "…";
        }
        canvas.drawText(countStr, maxRadius - 2 * textSize, 2 * textSize,
                countPaint);

        return bmp;
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static Bitmap fromRes(Resources res, int id) {
        Bitmap bmp = BitmapFactory.decodeResource(res, id);
        return bmp;
    }

    public static Bitmap getResBitmap(Context context, String name, String path) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        Resources res = context.getResources();
        int resID = res.getIdentifier(name, path, appInfo.packageName);
        return BitmapFactory.decodeResource(res, resID);
    }

    public static Drawable getResDrawable(Context context, String name, String path) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        Resources res = context.getResources();
        int resID = res.getIdentifier(name, path, appInfo.packageName);
        Bitmap bitmap = BitmapFactory.decodeResource(res, resID);
        return bitmap2Drawable(bitmap);
    }

    @SuppressWarnings("deprecation")
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static void setMsgCount(View v, Resources res, int count) {
        Bitmap oriBmp = BitmapUtil.drawable2Bitmap(v.getBackground());
        int max = Math.max(v.getBackground().getIntrinsicWidth(), v
                .getBackground().getIntrinsicHeight());
        Bitmap bmp = BitmapUtil.getMsgCountBitmap(oriBmp, max, count);
        Drawable drawable = BitmapUtil.bitmap2Drawable(bmp);
        v.setBackground(drawable);
    }

    public static Bitmap getBitmapByCompress(String filePath, int width, int height) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if (bm == null) {
            return null;
        }
        int degree = readPictureDegree(filePath);
        bm = rotateBitmap(bm, degree);
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;

    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static long getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();

    }
}
