package com.zhongdasoft.svwtrainnet.network.iCallback;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.entity.ConfirmList;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.module.home.LoginAfterActivity;
import com.zhongdasoft.svwtrainnet.module.home.MainActivity;
import com.zhongdasoft.svwtrainnet.network.callback.StringCallback;
import com.zhongdasoft.svwtrainnet.util.JsonUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;

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

public class GetConfirmListCallback extends StringCallback {
    private Context context;
    private Gson gson;

    private BaseActivity activity;

    public GetConfirmListCallback(Context context) {
        this.context = context;
        this.activity = (BaseActivity) context;
        this.gson = new Gson();
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.show(context, "待确认接口错误！原因：" + e.getMessage());
    }

    @Override
    public void onResponse(String response, int id) {
        JsonObject jo = JsonUtil.getJsonObject(context, response, "GetConfirmList", "ApiApply");
        if (null != jo) {
            ConfirmList confirmList = gson.fromJson(jo, new TypeToken<ConfirmList>() {
            }.getType());
            //do something
            int count = confirmList.getResult().getApiApply().size();
            String processCount;
            processCount = count > 99 ? "99+" : count + "";
            TrainNetApp.getCache().put(CacheKey.HomeConfirmCount, processCount);
            TrainNetApp.getCache().put(CacheKey.ConfirmRefresh, TrainNetApp.getGson().toJson(confirmList));

            String applyId = null;
            String startDate = null;
            String courseNo;
            String currentTime = MySharedPreferences.getInstance().getCurrentTime();
            boolean needRegistered = false;
            for (int i = 0; i < count; i++) {
                if (!needRegistered) {
                    startDate = confirmList.getResult().getApiApply().get(i).getStartDate();
                    applyId = confirmList.getResult().getApiApply().get(i).getApplyId();
                    courseNo = confirmList.getResult().getApiApply().get(i).getCourse().getCourseNo();
                    needRegistered = WebserviceUtil.getInstance().prepareRegisterRoute(courseNo, startDate, currentTime);
                    if (needRegistered) {
                        break;
                    }
                }
            }
            if (needRegistered) {
                Bundle bundle = new Bundle();
                bundle.putString("ApplyId", applyId);
                MySharedPreferences.getInstance().setStoreString(CacheKey.RouterRegister, applyId);
                activity.readyGoThenKill(LoginAfterActivity.class, bundle);
            } else {
                activity.readyGoThenKill(MainActivity.class);
            }
        }
    }
}
