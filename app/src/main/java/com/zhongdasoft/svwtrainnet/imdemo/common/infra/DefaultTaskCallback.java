package com.zhongdasoft.svwtrainnet.imdemo.common.infra;

public interface DefaultTaskCallback {
    public void onFinish(String key, int result, Object attachment);
}