package com.xiaoyao.jobhunter.commons.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 计时器
 * 
 * @author 旷明爱
 * @date Jun 27, 2012 10:10:22 AM
 */
public class SpendClock {
	long start = 0;
	long stop = 0;

	public SpendClock() {
		start = System.currentTimeMillis();
	}

	private long getSpend() {
		stop = System.currentTimeMillis();
		long spend = stop - start;
		start = System.currentTimeMillis();
		return spend;
	}

	public long stop() {
		return getSpend();
	}

	SimpleDateFormat format = new SimpleDateFormat("HH:MM:ss SSS");

	public void print() {
		Date date = new Date();
		System.out.println(format.format(date) + " thread " + Thread.currentThread().getId() + " 耗时:" + getSpend() + " ms.");

	}

}
