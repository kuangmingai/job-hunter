package com.xiaoyao.jobhunter.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常用类的辅助类
 * 
 * @author 旷明爱
 * @date Aug 3, 2012 11:33:26 AM
 */
public class ClassUtil {
	private static Logger logger = LoggerFactory.getLogger("ClassUtil");
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 火车站日期传参的格式
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateStr(Date date) {
		return simpleDateFormat.format(date);
		// System.out.println(String.format("%1$,02d",2) );
	}

	/**
	 * 在日期上加几天
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date addDate(Date date, int days) {
		return new Date(date.getYear(), date.getMonth(), date.getDate() + days);
	}

	public static void main(String[] args) {
		Date date = new Date();
		System.out.println(getDateStr(date));
		System.out.println(getDateStr(addDate(date, 40)));
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isEmpty(String data) {
		return data == null || "".equals(data.trim());
	}

	/**
	 * 判断集合是否为空
	 * 
	 * @param collection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isEmpty(Collection collection) {
		return collection == null || collection.size() == 0;
	}

	/**
	 * 日期字符串转换成日期 20120802 八位.
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Timestamp parseDate(String dateStr) {
		try {
			dateStr = dateStr.replaceAll("-", "");
			Date dd = simpleDateFormat.parse(dateStr);
			Timestamp stamp = new Timestamp(dd.getTime());
			return stamp;
			// return new Timestamp(simpleDateFormat.parse(dateStr).getTime());
		} catch (ParseException e) {
			logger.error("转换日期错误!" + e.getMessage());
		}
		return new Timestamp(new Date().getTime());
	}

	/**
	 * 获取现在的时间
	 * 
	 * @return
	 */
	public static Timestamp getNowTime() {
		return new Timestamp(new Date().getTime());
	}

}
