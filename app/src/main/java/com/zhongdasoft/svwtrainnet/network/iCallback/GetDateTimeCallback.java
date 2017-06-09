package com.zhongdasoft.svwtrainnet.network.iCallback;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhongdasoft.svwtrainnet.network.callback.StringCallback;
import com.zhongdasoft.svwtrainnet.util.JsonUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;

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

public class GetDateTimeCallback extends StringCallback {
    private Context context;
    private Gson gson;

    public GetDateTimeCallback(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.show(context, "系统时间接口错误！原因：" + e.getMessage());
    }

    @Override
    public void onResponse(String response, int id) {
        JsonObject jo = JsonUtil.getJsonObject(context, response, "GetDateTime", null);
        String timeStr = jo.getAsString();
        MySharedPreferences.getInstance().setStoreString("currentTime", timeStr.substring(0, 19).replace("T", " "));
    }
}
