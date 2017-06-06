package com.zhongdasoft.svwtrainnet.wizlong.reactModules;

import android.content.Intent;
import android.util.Log;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.wizlong.LiveCourseDetailActivity;

/**
 * Created by ls on 16/12/13.
 */
public class VctModule extends ReactContextBaseJavaModule {

    private static final String MODULE_NAME="VCTManager";

    public VctModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void gotoVctByRectNative(String url){
        Log.e("==========","==========VCTurl:"+url);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(getReactApplicationContext(), LiveCourseDetailActivity.class);
        intent.putExtra(LiveCourseDetailActivity.INTENT_KEY_URL, url);
        getReactApplicationContext().startActivity(intent);
    }

    @ReactMethod
    public void gotoBackNative(){
        TrainNetApp.getInstance().containerActivity.receiveFinishEvent();

    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }
}
