package com.xiaoyao.jobhunter.commons.exception;

/**
 * 初始化异常
 * 
 * @author 旷明爱
 * @date 2016年8月13日 上午12:31:16
 * @email mingai.kuang@mljr.com
 */
class InitException extends RuntimeException {
	private static final long serialVersionUID = -7946561993303405912L;

	public InitException(String msg) {
		super(msg);
	}

	public InitException(Exception exception) {
		super(exception);
	}
}
