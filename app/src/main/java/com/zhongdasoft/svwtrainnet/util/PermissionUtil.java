package com.zhongdasoft.svwtrainnet.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.zhongdasoft.svwtrainnet.R;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/8 14:48
 * 修改人：Administrator
 * 修改时间：2016/7/8 14:48
 * 修改备注：
 */
public class PermissionUtil implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int SDCARD_REQUEST_CODE_ASK_PERMISSION = 100;
    private PermissionHandler permissionHandler;
    private static PermissionUtil instance;

    private PermissionUtil() {
    }

    public synchronized static PermissionUtil getInstance() {
        if (null == instance) {
            instance = new PermissionUtil();
        }
        return instance;
    }

    public void applyPermission(final Activity activity, PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    try {
                        new AlertDialog.Builder(activity).setMessage(activity.getResources().getString(R.string.AllowSdcard)).setPositiveButton(activity.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SDCARD_REQUEST_CODE_ASK_PERMISSION);
                            }
                        }).create().show();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(activity, activity.getResources().getString(R.string.storeManual));
                            }
                        });
                    }
                    return;
                }
                try {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SDCARD_REQUEST_CODE_ASK_PERMISSION);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(activity, activity.getResources().getString(R.string.storeManual));
                        }
                    });
                }
                return;
            }
            permissionHandler.handlePermission(true);
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show(activity, activity.getResources().getString(R.string.Sdcard));
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SDCARD_REQUEST_CODE_ASK_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //grant
//                    Log.d("---Permission---", "grant");
                    permissionHandler.handlePermission(true);
                } else {
                    //denied
//                    Log.d("---Permission---", "denied");
                    permissionHandler.handlePermission(false);
                }
                break;
            default:
                //super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                break;
        }
    }

    public interface PermissionHandler {
        void handlePermission(boolean granted);
    }
}
