package com.zhongdasoft.svwtrainnet.module.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.SoftVersion;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

public class AppUpdate {
    private BaseActivity activity;
    private WeakReference<? extends BaseActivity> myActivity;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    private String updateUrl = "";
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private int updateType = 0;

    public AppUpdate(WeakReference<? extends BaseActivity> myActivity) {
        this.activity = myActivity.get();
        this.myActivity = myActivity;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    MySharedPreferences.getInstance().setStoreString("isUpdate", "1");
                    showNoticeDialog();
                    break;
                case 1:
                    if ("0".equals(MyProperty.getValueByKey("PublishMode", "0"))) {
                        TextView tvVersion = (TextView) activity.findViewById(R.id.space1);
                        tvVersion.setVisibility(View.VISIBLE);
                        tvVersion.setText("测试版");
                    }
                    Button loginBtn = (Button) activity.findViewById(R.id.loginBtn);
                    // 得到用户名和密码的编辑框
                    final EditText userName = (EditText) activity.findViewById(R.id.username);
                    final EditText password = (EditText) activity.findViewById(R.id.password);
                    loginBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            String userNameStr = userName.getText().toString();
                            String passwordStr = password.getText().toString();
                            new LoginHandler(myActivity, userNameStr, passwordStr);
                        }
                    });

                    password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                String userNameStr = userName.getText().toString();
                                String passwordStr = password.getText().toString();
                                new LoginHandler(myActivity, userNameStr, passwordStr);
                            }
                            return false;
                        }
                    });
                    String tmpUserName = MySharedPreferences.getInstance().getUserName();
                    if (!StringUtil.isNullOrEmpty(tmpUserName)) {
                        userName.setText(tmpUserName);
                    }

                    LinearLayout ll_login = (LinearLayout) activity.findViewById(R.id.ll_login);
                    // 判断是否已经登录
                    if (!StringUtil.isNullOrEmpty(tmpUserName)
                            && userName.getText().toString().equalsIgnoreCase(tmpUserName)) {
                        ll_login.setVisibility(View.INVISIBLE);
                        new LoginHandler(myActivity, ll_login);
                    } else {
                        Waiting.dismiss();
                        ll_login.setVisibility(View.VISIBLE);
                        userName.requestFocus();
                    }
                    break;
            }
        }
    };

    /**
     * 检测软件更新
     */
    public void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateType = isUpdate();
                    if (updateType > 0) {
                        Log.d("消息", "有新版本");
                        // 显示提示对话框
                        mHandler.sendEmptyMessage(0);
                    } else {
                        mHandler.sendEmptyMessage(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Waiting.dismiss();
                }

            }
        }).start();
    }

    /**
     * 检查软件是否有更新版本
     *
     * @return
     * @throws IOException
     */
    private int isUpdate() {
        // 获取当前软件版本
        String versionCode = SoftVersion.getVersionCode(activity);
        // 获取服务器版本信息
        Map<String, Object> mapResult = TrainNetWebService.getInstance().GetVersion(activity);
        // 版本判断
        if (!mapResult.containsKey("AtLeastVersion")) {
            return 0;
        }
        String atLeastVersion = mapResult.get("AtLeastVersion").toString();
        String latestVersion = mapResult.get("LatestVersion").toString();
        updateUrl = mapResult.get("UpdateUrl").toString();
        if (StringUtil.myCompareTo(versionCode, atLeastVersion) < 0) {
            // 强制更新
            return 1;
        } else if (StringUtil.myCompareTo(versionCode, latestVersion) < 0) {
            // 提示更新
            return 2;
        } else {
            // 不更新
            return 0;
        }
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog() {
        Waiting.dismiss();
        if (updateType == 1) {
            showDownloadDialog();
        } else {
            // 构造对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.soft_update_info);
            // 更新
            builder.setPositiveButton(R.string.soft_update_updateBtn,
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 显示下载对话框
                            showDownloadDialog();
                        }
                    });
            // 稍后更新
            builder.setNegativeButton(R.string.soft_update_later,
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            Dialog noticeDialog = builder.create();
            noticeDialog.show();
        }
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.trainnet_autoupdate, null);
        mProgress = (ProgressBar) v.findViewById(R.id.autoupdate_progress1);
        builder.setView(v);
        if (updateType != 1) {
            // 取消更新
            builder.setNegativeButton(R.string.soft_update_cancel,
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 设置取消状态
                            cancelUpdate = true;
                        }
                    });
        }
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // 下载文件
        new AppDownLoadHandler(myActivity, mProgress, mDownloadDialog, cancelUpdate,
                updateUrl);
    }

}
