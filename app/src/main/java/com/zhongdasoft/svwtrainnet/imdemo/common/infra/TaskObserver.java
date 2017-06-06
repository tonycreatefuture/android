package com.zhongdasoft.svwtrainnet.imdemo.common.infra;

public interface TaskObserver {
	/**
	 * on task result
	 * @param task
	 * @param results
	 */
	public void onTaskResult(com.zhongdasoft.svwtrainnet.imdemo.common.infra.Task task, Object[] results);

	/**
	 * on task progress
	 * @param task
	 * @param params
	 */
	public void onTaskProgress(com.zhongdasoft.svwtrainnet.imdemo.common.infra.Task task, Object[] params);
}
