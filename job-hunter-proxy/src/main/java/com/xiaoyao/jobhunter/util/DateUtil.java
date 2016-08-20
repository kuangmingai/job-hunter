package com.xiaoyao.jobhunter.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期类的辅助类
 * 
 * @author 旷明爱
 * @date Aug 3, 2012 11:33:26 AM
 */
public class DateUtil {
	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// 1
	private static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");// 2
	private static SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// 3
	private static SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 4
	public static long DAY = 24 * 3600 * 1000;
	
	public static final int TYPE_YYYYMMDD=1 ;
	public static final int TYPE_YYYY_MM_DD=2 ;
	public static final int TYPE_YYYYMMDDHHMMSS_CHINESE=3 ;
	public static final int TYPE_YYYY_MM_DD_HH_MM_SS=4 ;
	

	/**
	 * 日期传参的格式 <BR>
	 * yyyy-MM-dd ,yyyyMMdd 格式
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateStr(Date date) {
		return simpleDateFormat2.format(date);
		// System.out.println(String.format("%1$,02d",2) );
	}

	public static String getDateStr(Date date,int type) { 
		SimpleDateFormat dateFormat = simpleDateFormat;
		if (type == 2) {
			dateFormat = simpleDateFormat2;
		} else if (type == 3) {
			dateFormat = simpleDateFormat3;
		} else if (type == 4) {
			dateFormat = simpleDateTimeFormat;
		}
		return dateFormat.format(date);
		// System.out.println(String.format("%1$,02d",2) );
	}

	public static String getDateTimeStr(Date date) {
		return simpleDateFormat3.format(date);
	}

	public static String getNowDateTimeStr() {
		Date date = new Date();
		return simpleDateFormat3.format(date);
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
		return new Date(date.getTime() + days * DAY);
	}

	public static Timestamp addHour(Date date, int hour) {
		return new Timestamp(date.getTime() + hour * Constant.HOUR);
	}

	/**
	 * 在今天加几天
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date addDate(int days) {
		return addDate(getNowTime(), days);
	}

	public static void main(String[] args) {
		Date date = new Date();
		System.out.println(getDateStr(date));
		System.out.println(getDateStr(addDate(date, 40)));
	}

	@SuppressWarnings("deprecation")
	public static Timestamp parseDate(String dateStr, int type) {
		try {
			SimpleDateFormat dateFormat = simpleDateFormat;
			if (type == 2) {
				dateFormat = simpleDateFormat2;
			} else if (type == 3) {
				dateFormat = simpleDateFormat3;
			} else if (type == 4) {
				dateFormat = simpleDateTimeFormat;
			}
			Date dd = dateFormat.parse(dateStr);
			// dd.setDate(dd.getDate()-1900);
			Timestamp stamp = new Timestamp(dd.getTime());
			return stamp;
		} catch (ParseException e) {
			logger.error("转换日期错误!" + e.getMessage());
		}
		return new Timestamp(new Date().getTime());
	}

	/**
	 * 日期字符串转换成日期 20120802 八位.出错返回现在的时间.
	 * 
	 * @param dateStr
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Timestamp parseDate(String dateStr) {
		try {
			dateStr = dateStr.replaceAll("-", "");
			Date dd = simpleDateFormat.parse(dateStr);
			// dd.setDate(dd.getDate()-1900);
			Timestamp stamp = new Timestamp(dd.getTime());
			return stamp;
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

	/**
	 * 获取现在的时间 YYYY-MM-dd
	 * 
	 * @return
	 */
	public static String getNowDate() {
		return getDateStr(getNowTime());
	}

}
