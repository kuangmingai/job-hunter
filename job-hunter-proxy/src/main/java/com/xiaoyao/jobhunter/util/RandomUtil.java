package com.xiaoyao.jobhunter.util;

import java.util.Random;

public class RandomUtil {

	static Random random = new Random();

	public static int getNextInt(int max) {
		return random.nextInt(max);
	}

	public static long getNextLong(long max) {
//		long result = random.nextInt((int)max) ;
//		System.out.println("随机数:"+result);
//		return result;  
		return (random.nextLong()%max+max)%max;
//		return  random.nextInt((int)max) ;
	}

	public static void main(String[] args) {
//		System.out.println(random.nextInt(5));
		System.out.println(Long.MAX_VALUE) ;
		System.out.println(Integer.MAX_VALUE) ;
		long x=75807L;
		System.out.println( x) ;
		System.out.println(getNextLong(x)) ;
	}
}
