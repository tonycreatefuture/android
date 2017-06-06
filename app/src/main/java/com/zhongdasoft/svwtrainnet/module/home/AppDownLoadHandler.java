package com.zhongdasoft.svwtrainnet.module.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.FileUtil;
import com.zhongdasoft.svwtrainnet.util.PermissionUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class AppDownLoadHandler extends Handler {
    /* 下载中 */
    public static final int DOWNLOAD = 1;
    /* 下载结束 */
    public static final int DOWNLOAD_FINISH = 2;
    /* 下载DB文件 */
    public static final int DOWNLOAD_DBFILES = 3;
    /* 下载DB文件完成 */
    public static final int DOWNLOAD_DBFILES_FINISH = 4;
    /* 下载DB文件出错 */
    public static final int DOWNLOAD_FAILURE = 5;
    /* 下载保存路径 */
    private static String mSavePath;
    /* 当前活动 */
    private Activity activity;
    /* 更新进度条 */
    private ProgressBar mProgress;
    /* 记录进度条数量 */
    private int progress;

    private Dialog mDownloadDialog;

    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    /* 更新路径 */
    private String updateUrl = "";
    /* 错误信息 */
    private String errMsg = "";

    public AppDownLoadHandler(WeakReference<? extends Activity> myActivity, ProgressBar mProgress,
                              Dialog mDownloadDialog, boolean cancelUpdate, String updateUrl) {
        this.activity = myActivity.get();
        this.mProgress = mProgress;
        this.mDownloadDialog = mDownloadDialog;
        this.cancelUpdate = cancelUpdate;
        this.updateUrl = updateUrl;
        new Thread(new DownLoadThread()).start();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            // 正在下载
            case DOWNLOAD:
                // 设置进度条位置
                mProgress.setProgress(progress);
                break;
            case DOWNLOAD_DBFILES:
                break;
            case DOWNLOAD_FINISH:
                if (null != mDownloadDialog) {
                    mDownloadDialog.dismiss();
                }
                // 安装文件
                installApk();
                break;
            case DOWNLOAD_DBFILES_FINISH:
                break;
            case DOWNLOAD_FAILURE:
                ToastUtil.show(activity, errMsg);
                break;
            default:
                break;
        }
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, "svwtrainnet.apk");
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("chat_file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }

    public class DownLoadThread implements Runnable {
        public DownLoadThread() {
        }

        @Override
        public void run() {
            // 判断SD卡是否存在，并且是否具有读写权限
            final String fileName = "svwtrainnet.apk";
            PermissionUtil.getInstance().applyPermission(activity, new PermissionUtil.PermissionHandler() {
                @Override
                public void handlePermission(boolean granted) {
                    if (!granted) {
                        errMsg = activity.getResources().getString(R.string.sdCardTips);
                        sendEmptyMessage(AppDownLoadHandler.DOWNLOAD_FAILURE);
                        return;
                    }
                    try {
                        // 获得存储卡的路径
                        String sdPath = Environment.getExternalStorageDirectory()
                                + "/";
                        mSavePath = sdPath + "download";
                        // 获取文件大小
                        int[] length = new int[1];
                        // 创建输入流
                        InputStream is = FileUtil.getInstance().getInputStream(updateUrl, length); //conn.getInputStream();

                        File file = new File(mSavePath);
                        // 判断文件目录是否存在
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        File apkFile = new File(mSavePath, fileName);
                        FileOutputStream fos = new FileOutputStream(apkFile);
                        int count = 0;
                        // 缓存
                        byte buf[] = new byte[1024];
                        // 写入到文件中
                        do {
                            int numRead = is.read(buf);
                            count += numRead;
                            // 计算进度条位置
                            progress = (int) (((float) count / length[0]) * 100);
                            // 更新进度
                            sendEmptyMessage(AppDownLoadHandler.DOWNLOAD);
                            if (numRead <= 0) {
                                // 下载完成
                                sendEmptyMessage(AppDownLoadHandler.DOWNLOAD_FINISH);
                                break;
                            }
                            // 写入文件
                            fos.write(buf, 0, numRead);
                        } while (!cancelUpdate);// 点击取消就停止下载.
                        fos.close();
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        errMsg = e.getMessage();
                        sendEmptyMessage(AppDownLoadHandler.DOWNLOAD_FAILURE);
                    }
                }
            });
        }
    }
}
