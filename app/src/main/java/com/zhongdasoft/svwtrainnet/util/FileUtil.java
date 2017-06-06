package com.zhongdasoft.svwtrainnet.util;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.module.traininner.InnerPlanSummaryActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/14 15:04
 * 修改人：Administrator
 * 修改时间：2016/7/14 15:04
 * 修改备注：
 */
public class FileUtil {
    private static FileUtil instance;

    private FileUtil() {
    }

    /**
     * 单例模式
     */
    public synchronized static FileUtil getInstance() {
        if (null == instance) {
            instance = new FileUtil();
        }
        return instance;
    }

    public InputStream getInputStream(String httpUrl, int[] length) {
        URL url;
        InputStream is;
        try {
            url = new URL(httpUrl);
            // 创建连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000 * 60 * 3);
            conn.connect();
            // 获取文件大小
            if (null != length && length.length > 0) {
                length[0] = conn.getContentLength();
            }
            // 创建输入流
            is = conn.getInputStream();
            return is;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveFileFromHttp(String httpUrl, String fileName, String filePath) {
        boolean result = true;
        try {
            InputStream is = getInputStream(httpUrl, null);
            if (null == is) {
                return false;
            }
            FileOutputStream fos;
            File file = new File(filePath);
            // 判断文件目录是否存在
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(filePath, fileName);
            fos = new FileOutputStream(file);
            // 缓存
            byte buf[] = new byte[1024];
            int numRead;
            while ((numRead = is.read(buf)) > 0) {
                // 写入文件
                fos.write(buf, 0, numRead);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    //修改apk权限
    public static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getContentFromFile(String filePath) {
        File file = new File(filePath);
        if (file.canRead()) {
            try {
                InputStream in = new FileInputStream(file);
                //创建合适文件大小的数组
                byte b[] = new byte[(int) file.length()];
                //读取文件中的内容到b[]数组
                in.read(b);
                in.close();
                return b;
            } catch (IOException e) {
                e.printStackTrace();
                return new byte[1];
            }
        } else {
            return new byte[1];
        }
    }

    /**
     * 读取修改时间的方法
     */
    public String getModifiedTime(long time) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        cal.setTimeInMillis(time);
        return formatter.format(cal.getTime());
    }

//    public boolean deleteDBFile(String dbName) {
//        String DeviceDBPath = MyProperty.loadConfig().getProperty("DeviceDBPath");
//        File file = new File(DeviceDBPath + "/" + dbName);
//        if (file.isFile()) {
//            file.setExecutable(true, false);
//            file.setReadable(true, false);
//            file.setWritable(true, false);
//            file.delete();
//        }
//        file.setExecutable(true, false);
//        file.setReadable(true, false);
//        file.setWritable(true, false);
//        file.delete();
//        return !file.exists();
//    }

    public String saveFile(String filePath, String fileShowName, String targetDir, BaseActivity activity) {
        FileOutputStream fos = null;
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(targetDir, fileShowName);
        Bitmap bitmap = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                return file.getPath();
            }
            bitmap = Glide.with(activity).load(filePath).asBitmap().centerCrop().into(500, 500).get();
            if (null != bitmap) {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                return targetDir + fileShowName;
            } else {
                boolean result = FileUtil.getInstance().saveFileFromHttp(filePath, fileShowName, targetDir);
                if (result) {
                    return targetDir + fileShowName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != bitmap) {
                bitmap.recycle();
            }
        }
        return null;
    }
}
