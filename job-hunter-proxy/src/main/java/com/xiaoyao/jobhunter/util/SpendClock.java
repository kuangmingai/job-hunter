package com.xiaoyao.jobhunter.util;

public class SpendClock {
	private static long start = 0;
	private static long end = 0;
	public SpendClock() {  
		start = System.currentTimeMillis();
		end = System.currentTimeMillis();
	}

	public static void start() {
		start = System.currentTimeMillis();
		end = System.currentTimeMillis();
	}

	public static void end() {
		end = System.currentTimeMillis();
	}

	public static void print() {
		end = System.currentTimeMillis();
		System.out.println("耗时:" + (end - start) + "ms");
	}
	public static long getSpend() {
		return (end - start);
	}
}
