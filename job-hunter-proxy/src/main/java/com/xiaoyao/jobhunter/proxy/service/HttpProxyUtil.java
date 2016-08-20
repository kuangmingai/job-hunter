package com.xiaoyao.jobhunter.proxy.service;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyao.jobhunter.proxy.pojo.ProxyInfo;
import com.xiaoyao.jobhunter.util.FileUtil;
import com.xiaoyao.jobhunter.util.HttpUtil;

/**
 * 代理配置
 * 
 * @author 旷明爱
 * 
 */
public class HttpProxyUtil {

	static Logger logger = LoggerFactory.getLogger(HttpProxyUtil.class);

	public static final String proxyPath = "proxy.conf";
	public static final String proxySep = ";|:";
	static List<ProxyInfo> proxyList = new ArrayList<ProxyInfo>();
	static {
		init();
	}

	/**
	 * 定时更新代理源
	 */
	public static void clockUpdateProxySource() {

	}

	/**
	 * 定时切换,代理IP
	 */
	public static void clockSwith() {

	}

	private static void init() {
		try {
			proxyList.clear();
			String content = FileUtil.readAbsolutlyFile(FileUtil.getPath() + proxyPath);
			String[] lines = content.split("\r\n");
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].contains("#")) {
					continue;
				}
				String[] strs = lines[i].split(proxySep);

				if (strs.length == 2 || strs.length == 4) {
					ProxyInfo proxyInfo = new ProxyInfo();
					proxyInfo.setIp(strs[0].trim());
					proxyInfo.setPort(strs[1].trim());
					if (strs.length == 4) {// 有密码
						proxyInfo.setUsername(strs[2].trim());
						proxyInfo.setPassword(strs[3].trim());
					}
					proxyList.add(proxyInfo);
				}
			}
		} catch (IOException e) {
			logger.error(""+e);
		}
	}

	static int index_ = 0;

	public static int getIndex(int size) {
		// return RandomUtil.getNextInt(size);
		return index_++ % size;
	}

	/**
	 * 随机取一个代理
	 */
	public static synchronized void useProxy() {
		init();
		Properties prop = System.getProperties();
		prop.setProperty("proxySet", "true");
		int size = proxyList.size();
		int index = getIndex(size);
		ProxyInfo proxyInfo = proxyList.get(index);

		final String ip = proxyInfo.getIp();
		final String port = proxyInfo.getPort();
		final String username = proxyInfo.getUsername();
		final String password = proxyInfo.getPassword();

		prop.put("http.proxyHost", ip);
		prop.put("http.proxyPort", port);
		logger.info("使用" + index + "/" + size + " 代理配置:" + ip + ":" + port);
		if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, new String(password).toCharArray());
				}
			});
		}
	}

	public static void testIp() throws IOException {
		for (int i = 0; i < 5; i++) {
			HttpProxyUtil.useProxy();
			HttpUtil httpUtil = new HttpUtil();
			String testUrl = "http://www.qqxxbd.com/testip.jsp";
			long start =System.currentTimeMillis() ;
			String page="";
			try {
				page = httpUtil.getPage(testUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long end =System.currentTimeMillis() ;
			
			logger.info("     耗时:"+(end-start)+" ms "+page) ;
		}
	}

	/**
	 * 关闭代理.
	 */
	public static void closeProxy() {
		Properties prop = System.getProperties();
		prop.setProperty("proxySet", "false");
	}

	public static void main(String[] args) throws IOException {
//		HttpProxy.useProxy();
		testIp();
	}
}
