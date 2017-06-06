package com.zhongdasoft.svwtrainnet.imdemo.login;

import android.app.Activity;
import android.content.Intent;
import android.view.animation.Animation;

import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.ui.drop.DropManager;
import com.zhongdasoft.svwtrainnet.imdemo.DemoCache;
import com.zhongdasoft.svwtrainnet.imdemo.chatroom.helper.ChatRoomHelper;
import com.zhongdasoft.svwtrainnet.module.home.LoginActivity;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;

/**
 * 注销帮助类
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        NimUIKit.clearCache();
        ChatRoomHelper.logout();
        DemoCache.clear();
        LoginSyncDataStatusObserver.getInstance().reset();
        DropManager.getInstance().destroy();
        MySharedPreferences.getInstance().setStoreString("AccountLogout", "1", NimUIKit.getContext());
    }
}
