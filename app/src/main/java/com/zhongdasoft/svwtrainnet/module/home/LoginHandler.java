package com.zhongdasoft.svwtrainnet.module.home;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.NetManager;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.widget.timer.CurrentTimer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler extends Handler {
    private final BaseActivity activity;
    private String userName;
    private String password;
    private boolean isLogin = false;
    private CurrentTimer mc;
    private Map<String, Object> timeMap = null;
    //    private AbortableFuture<LoginInfo> loginRequest;
    private View loginView;
    private AbortableFuture<LoginInfo> loginRequest;
    private HashMap<String, Object> nimAccountMap;

    public LoginHandler(WeakReference<? extends BaseActivity> myActivity, View loginView) {
        this.activity = myActivity.get();
        this.userName = "";
        this.password = "";
        this.isLogin = true;
        this.loginView = loginView;

        Waiting.show(activity, activity.getResources().getString(R.string.LoadingLogin));
        new Thread(new LoginThread()).start();
    }

    public LoginHandler(WeakReference<? extends BaseActivity> myActivity, String userName, String password) {
        this.activity = myActivity.get();
        this.userName = userName;
        this.password = password;
        this.isLogin = false;

        Waiting.show(activity, activity.getResources().getString(R.string.LoadingLogin));
        new Thread(new LoginThread()).start();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 0:
                Waiting.dismiss();
                if (null != loginView) {
                    loginView.setVisibility(View.VISIBLE);
                }
                ToastUtil.show(activity, activity.getResources().getString(R.string.net_broken));
                break;
            case 1:
                int ReturnCode = Integer.parseInt(timeMap.get(activity.getResources().getString(R.string.ReturnCode))
                        .toString());
                // 设置999为自动登录
                if (ReturnCode == 999) {
                    MySharedPreferences.getInstance().setStoreString("currentTime", timeMap.get("GetDateTimeResult").toString());
                    initUserMenu();
                    setUserMenu();
                    setCounter();
                } else {
                    MySharedPreferences.getInstance().setStoreString("AccountLogout", "0");
                    String Message = timeMap.get(activity.getResources().getString(R.string.Message)).toString();
                    if (ReturnCode == 0) {
                        // 登录验证成功
                        // 创建Intent对象，传入源Activity和目的Activity的类对象
                        String value;
                        try {
                            for (String property : timeMap.keySet()) {
                                if (activity.getResources().getString(R.string.ReturnCode).equals(property)
                                        || activity.getResources().getString(R.string.Message).equals(property)
                                        || "Id".equals(property)) {
                                    continue;
                                }
                                value = timeMap.get(property).toString();
                                if ("GetDateTimeResult".equals(property)) {
                                    property = "currentTime";
                                    value = value.substring(0, 19).replace("T", " ");
                                }
                                MySharedPreferences.getInstance().setStoreString(property, value);
                            }
                            MySharedPreferences.getInstance().setStoreString("userName", userName);
                            MySharedPreferences.getInstance().setStoreString("password", password);
                            initUserMenu();
                            setUserMenu();
                            setCounter();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Waiting.dismiss();
                        // 登录信息错误，通过Toast显示提示信息
                        ToastUtil.show(activity, Message);
                    }
                }
                break;
        }
    }

    private void initUserMenu() {
        String isUpdate = MySharedPreferences.getInstance().getString("isUpdate");
        //菜单表有数据且当前没有升级app时，不操作数据表
        if (DaoQuery.getInstance().getUserMenuCount() > 0 && StringUtil.isNullOrEmpty(isUpdate)) {
            return;
        }
        CRUD.getInstance().InitUserMenu(activity.getResources());
        MySharedPreferences.getInstance().removeString("isUpdate");
    }

    private void setUserMenu() {
        //设置是否显示菜单
        String post = MySharedPreferences.getInstance().getPost();
        UserMenu userMenu = DaoQuery.getInstance().getUserMenuByResName(activity.getResources().getString(R.string.TrainInner));
        userMenu.setIsValid(1);
        if (!StringUtil.isNullOrEmpty(post)) {
            if (!post.contains("内训师")) {
                userMenu.setIsValid(0);
            }
        } else {
            userMenu.setIsValid(0);
        }
        CRUD.getInstance().UpdateUserMenu(userMenu);

        String teachPlan = MySharedPreferences.getInstance().getString("TeachPlan");
        userMenu = DaoQuery.getInstance().getUserMenuByResName(activity.getResources().getString(R.string.TrainPlan));
        if (1 == userMenu.getIsValid()) {
            if (!StringUtil.isNullOrEmpty(teachPlan)) {
                userMenu.setIsValid(1);
            } else {
                userMenu.setIsValid(0);
            }
            CRUD.getInstance().UpdateUserMenu(userMenu);
        }

        if (!DaoQuery.getInstance().existUserFavorite(userName)) {
            CRUD.getInstance().InitUserFavorite(userName, ActivityKey.Add);
        }
    }

    private void setCounter() {
        if (mc != null) {
            mc.cancel();
        }
        MySharedPreferences.getInstance().setStoreString("countTimer", "0");
        mc = new CurrentTimer(MyProperty.MaxValue, 1, activity);
        mc.start();

        Waiting.dismiss();
        activity.readyGoThenKill(MainActivity.class);
    }

    public class LoginThread implements Runnable {

        public LoginThread() {
        }

        @Override
        public void run() {
            if (!NetManager.isNetworkConnected(activity)) {
                sendEmptyMessage(0);
                return;
            }
            ArrayList<HashMap<String, Object>> login = new ArrayList<>();
            timeMap = TrainNetWebService.getInstance().GetDateTime(activity);
            if (!timeMap.containsKey("GetDateTimeResult")) {
                sendEmptyMessage(0);
                return;
            }
            nimAccountMap = TrainNetWebService.getInstance().ProfileNimAccount(activity);
            if (!isLogin) {
                if (StringUtil.isNullOrEmpty(userName)) {
                    HashMap<String, Object> localMap = new HashMap<>();
                    localMap.put(activity.getResources().getString(R.string.ReturnCode), activity.getResources().getString(R.string.LoginCode));
                    localMap.put(activity.getResources().getString(R.string.Message), activity.getResources().getString(R.string.LoginMessage));
                    login.add(localMap);
                } else {
                    login = TrainNetWebService.getInstance().Login(activity, userName, password);
                }
            } else {
                timeMap.put(activity.getResources().getString(R.string.ReturnCode), "999");
                timeMap.put(activity.getResources().getString(R.string.Message), "自动登录");
            }
            String parentNode = activity.getResources().getString(R.string.ParentNode);
            if (login != null) {
                for (HashMap<String, Object> localMap : login) {
                    for (String key : localMap.keySet()) {
                        if (null == localMap.get(key) || "".equals(localMap.get(key).toString())) {
                            continue;
                        }
                        if ("LoginResult".equals(localMap.get(parentNode).toString())
                                || "Result".equals(localMap.get(parentNode).toString())
                                || "Dealer".equals(localMap.get(parentNode).toString())) {
                            timeMap.put(key, localMap.get(key));
                        } else if ("BindedDealerStaffUser".equals(localMap.get(parentNode).toString())) {
                            MySharedPreferences.getInstance().setStoreString("TeachPlan", "1");
                        } else if ("ApiIdValuePairOfInt32String".equals(localMap.get(parentNode).toString())) {
                            if ("Value".equalsIgnoreCase(key)) {
                                if (timeMap.containsKey("post")) {
                                    timeMap.put("post", timeMap.get("post").toString() + "," + localMap.get(key));
                                } else {
                                    timeMap.put("post", localMap.get("Value"));
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            sendEmptyMessage(1);
        }
    }
}
