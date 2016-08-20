package com.xiaoyao.jobhunter.proxy.crawl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyao.jobhunter.util.Constant;
import com.xiaoyao.jobhunter.util.FileUtil;
import com.xiaoyao.jobhunter.util.HttpUtil;

public class BaseService {
	private static Logger logger = LoggerFactory.getLogger(BaseService.class);

	private static final long sleep = 100L;// 网络抓取休眠时间.ms.
	private static final long expire = 30L * Constant.DAY;// 过期天数.ms

	/**
	 * 读取网络文件,可以指定缓存.可以根据时间刷新.
	 * 
	 * @param localPath
	 *            本地文件
	 * @param httpUtil
	 *            读取Http操作类
	 * @param urlStr
	 *            网络地址
	 * @param readCache
	 *            是否读缓存
	 * @return
	 */
	public String readFileOrHttp(String localPath, HttpUtil httpUtil, String urlStr, boolean readCache) {
		String page = "";
		// 读取缓存文件
		File localFile = new File(localPath);
		long now = System.currentTimeMillis();
		if (readCache && localFile.exists()) {
			long lastModify = localFile.lastModified();
			if (now - lastModify < expire) {
				try {
					logger.debug( "本地缓存:" + urlStr);
					page = FileUtil.readAbsolutlyFile(localPath); 
				} catch (Exception e) {
					logger.error("读取本地文件错误:" + e + "," + localPath);
				}
			}
		}
		// 在线获取
		if (StringUtils.isEmpty(page)) {
			try {
				Thread.sleep(sleep);
				logger.info("抓取页面:" + urlStr);
				page = httpUtil.getPage(urlStr);
				FileUtil.saveAbsolutlyFile(localPath, page);
			} catch (Exception e) {
				logger.error(""+e);
			}
		}
		return page;
	}

	/**
	 * 读取网络文件,可以指定缓存.
	 * 
	 * @param localPath
	 *            本地文件
	 * @param httpUtil
	 *            读取Http操作类
	 * @param urlStr
	 *            网络地址
	 * @param readCache
	 *            是否读缓存
	 * @return
	 */
	public String readFileOrHttpPost(String localPath, HttpUtil httpUtil, String urlStr, String param, boolean readCache) {
		String page = "";
		// 读取缓存文件
		File localFile = new File(localPath);
		if (readCache && localFile.exists()) {
			try {
				logger.info("本地缓存:" + urlStr+param);
				page = FileUtil.readAbsolutlyFile(localPath);
			} catch (IOException e) {
				logger.error("读取本地文件错误:" + e + "," + localPath);
			}
		}
		// 在线获取
		if (StringUtils.isEmpty(page)) {
			try {
				logger.info("抓取页面:" + urlStr + "?" + param);
				page = httpUtil.postPage(urlStr, param);
				FileUtil.saveAbsolutlyFile(localPath, page);
			} catch (Exception e) {
				logger.error(""+e);
			}
		}
		return page;
	}
}
