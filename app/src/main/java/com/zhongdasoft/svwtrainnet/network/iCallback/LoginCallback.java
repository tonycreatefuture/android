package com.zhongdasoft.svwtrainnet.network.iCallback;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.entity.Login;
import com.zhongdasoft.svwtrainnet.network.callback.StringCallback;
import com.zhongdasoft.svwtrainnet.util.JsonUtil;
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

public class LoginCallback extends StringCallback {
    private Context context;
    private BaseActivity activity;
    private Gson gson;

    public LoginCallback(Context context) {
        this.context = context;
        this.activity = (BaseActivity) context;
        this.gson = new Gson();
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.show(context, "登录接口错误！原因：" + e.getMessage());
    }

    @Override
    public void onResponse(String response, int id) {
        JsonObject jo = JsonUtil.getJsonObject(context, response, "Login", null);
        if (null != jo) {
            Login login = gson.fromJson(jo, new TypeToken<Login>() {
            }.getType());
            //do something
            if (0 == login.getReturnCode()) {
                WebserviceUtil.getInstance().Login(activity);
            } else {
                ToastUtil.show(context, login.getMessage());
            }
        }
    }
}
