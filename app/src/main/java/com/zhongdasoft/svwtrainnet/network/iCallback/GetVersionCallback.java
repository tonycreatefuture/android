package com.zhongdasoft.svwtrainnet.network.iCallback;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.entity.Version;
import com.zhongdasoft.svwtrainnet.network.OkHttp;
import com.zhongdasoft.svwtrainnet.network.OkHttpUtil;
import com.zhongdasoft.svwtrainnet.network.callback.FileCallBack;
import com.zhongdasoft.svwtrainnet.network.callback.StringCallback;
import com.zhongdasoft.svwtrainnet.util.JsonUtil;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.PermissionUtil;
import com.zhongdasoft.svwtrainnet.util.SoftVersion;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;

import java.io.File;

import okhttp3.Call;

/**
 * 项目名称： TrainNet
 * 累描述：
 * 创建人：tony
 * 创建时间：2016/12/15 11:28
 * 修改人：tony
 * 修改时间：2016/12/15 11:28
 * 修改备注：
 */

public class GetVersionCallback extends StringCallback {
    private Context context;
    private Gson gson;
    /* 更新类型 */
    private int updateType = 0;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    /* 更新进度条 */
    private ProgressBar mProgressBar;
    /* APK路径 */
    private String updateUrl = "";
    /* 下载对话框 */
    private Dialog mDownloadDialog;

    private BaseActivity activity;

    public GetVersionCallback(Context context) {
        this.context = context;
        this.activity = (BaseActivity) context;
        this.gson = new Gson();
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.show(context, "版本接口错误！原因：" + e.getMessage());
    }

    @Override
    public void onResponse(String response, int id) {
        JsonObject jo = JsonUtil.getJsonObject(context, response, "GetVersion", null);
        if (null != jo) {
            Version version = gson.fromJson(jo, new TypeToken<Version>() {
            }.getType());
            //do something
            String versionCode = SoftVersion.getVersionCode(context);
            if (StringUtil.myCompareTo(versionCode, version.getResult().getAtLeastVersion()) < 0) {
                // 强制更新
                updateType = 1;
            } else if (StringUtil.myCompareTo(versionCode, version.getResult().getLatestVersion()) < 0) {
                // 提示更新
                updateType = 2;
            } else {
                // 不更新
                updateType = 0;
            }
            if (updateType > 0) {
                MySharedPreferences.getInstance().setStoreString("isUpdate", "1");
                showNoticeDialog();
            } else {
                if ("0".equals(MyProperty.getValueByKey("PublishMode", "0"))) {
                    TextView tvVersion = (TextView) activity.findViewById(R.id.space1);
                    tvVersion.setVisibility(View.VISIBLE);
                    tvVersion.setText("测试版");
                }
                // 得到用户名和密码的编辑框
                final EditText userName = (EditText) activity.findViewById(R.id.username);
                final EditText password = (EditText) activity.findViewById(R.id.password);

                password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            String userNameStr = userName.getText().toString();
                            String passwordStr = password.getText().toString();
                            login(userNameStr, passwordStr);
                        }
                        return false;
                    }
                });
                //登录操作
                Button loginBtn = (Button) activity.findViewById(R.id.loginBtn);
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String userNameStr = userName.getText().toString();
                        String passwordStr = password.getText().toString();
                        login(userNameStr, passwordStr);
                    }
                });
                String tmpUserName = MySharedPreferences.getInstance().getUserName();
                String tmpPassword = MySharedPreferences.getInstance().getUserName();
                LinearLayout ll_login = (LinearLayout) activity.findViewById(R.id.ll_login);
                // 判断是否已经登录
                if (!StringUtil.isNullOrEmpty(tmpUserName)) {
                    ll_login.setVisibility(View.INVISIBLE);
                    login(tmpUserName, tmpPassword);
                } else {
                    ll_login.setVisibility(View.VISIBLE);
                    userName.requestFocus();
                }
            }
        }
    }

    /**
     * 登录
     *
     * @author tony
     * @time 2016/12/16 11:07
     **/
    private void login(String userName, String password) {
        if (StringUtil.isNullOrEmpty(userName)) {
            ToastUtil.show(context, activity.getResources().getString(R.string.LoginMessage));
            return;
        }
        String timer = MySharedPreferences.getInstance().getString("countTimer");
        if (StringUtil.isNullOrEmpty(timer)) {
            OkHttp.getInstance().GetDateTime(context);
        }
        String tmpUserName = MySharedPreferences.getInstance().getUserName();
        if(!StringUtil.isNullOrEmpty(tmpUserName)){
            WebserviceUtil.getInstance().Login(activity);
        }else {
            OkHttp.getInstance().Login(context, userName, password);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.soft_update_info);
            // 更新
            builder.setPositiveButton(R.string.soft_update_updateBtn,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 显示下载对话框
                            showDownloadDialog();
                        }
                    });
            // 稍后更新
            builder.setNegativeButton(R.string.soft_update_later,
                    new DialogInterface.OnClickListener() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.trainnet_autoupdate, null);
        mProgressBar = (ProgressBar) v.findViewById(R.id.autoupdate_progress1);
        builder.setView(v);
        if (updateType != 1) {
            // 取消更新
            builder.setNegativeButton(R.string.soft_update_cancel,
                    new DialogInterface.OnClickListener() {
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
        PermissionUtil.getInstance().applyPermission(activity, new PermissionUtil.PermissionHandler() {
            @Override
            public void handlePermission(boolean granted) {
                if (!granted) {
                    String errMsg = context.getResources().getString(R.string.sdCardTips);
                    ToastUtil.show(context, errMsg);
                    return;
                }
                OkHttpUtil//
                        .get()//
                        .url(updateUrl)//
                        .build()//
                        .execute(new FileCallBack(Environment.getExternalStorageDirectory() + "/download", "svwtrainnet.apk")//
                        {
                            @Override
                            public void inProgress(float progress, long total, int id) {
                                mProgressBar.setProgress((int) (100 * progress));
                            }

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.show(context, "下载APK文件错误！原因：" + e.getMessage());
                            }

                            @Override
                            public void onResponse(File file, int id) {
                                installApk(file);
                            }
                        });
            }
        });
    }

    /**
     * 安装APK文件
     */
    private void installApk(File apkfile) {
        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("chat_file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
