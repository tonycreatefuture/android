package com.zhongdasoft.svwtrainnet.network.iCallback;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.entity.Login;
import com.zhongdasoft.svwtrainnet.network.callback.StringCallback;
import com.zhongdasoft.svwtrainnet.util.JsonUtil;
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

public class EvaluationListCallback extends StringCallback {
    private Context context;
    private Gson gson;

    public EvaluationListCallback(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.show(context, "登录接口错误！原因："+e.getMessage());
    }

    @Override
    public void onResponse(String response, int id) {
        String json = JsonUtil.xml2JSON(response);
        JsonParser parse = new JsonParser();
        JsonObject jo = parse.parse(json).getAsJsonObject();
        JsonObject joResult = null;
        if(jo.has("Result")){
            joResult = jo.get("Result").getAsJsonObject();
        }
        if(null!=joResult){
            Login login = gson.fromJson(joResult, new TypeToken<Login>() {
            }.getType());
            //do something

        }
    }
}
