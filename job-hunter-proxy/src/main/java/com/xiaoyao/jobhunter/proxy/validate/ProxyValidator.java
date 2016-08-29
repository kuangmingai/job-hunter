package com.xiaoyao.jobhunter.proxy.validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.xiaoyao.jobhunter.proxy.crawl.BaseService;
import com.xiaoyao.jobhunter.proxy.dao.ProxyInfoDao;

public class ProxyValidator extends BaseService {
	static Logger logger = LoggerFactory.getLogger(ProxyValidator.class);

	private static final int SO_TIMEOUT = 10000;
	private static final int CONNECT_TIMEOUT = 5000;
	private static final int MAX_CON_MS = 5000; // 最长链接时间.
	private static final String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
	private static final List<String> valiteWebsites = new ArrayList<String>();

	public static void init() {
////		 valiteWebsites.add("http://www.baidu.com"); // 百度用代理可能不能访问
//		 valiteWebsites.add("http://www.qq.com");
//		 valiteWebsites.add("http://www.163.com");
		 valiteWebsites.add("http://www.sina.com.cn");
//		 valiteWebsites.add("http://www.sohu.com/");
//		 valiteWebsites.add("http://www.taobao.com");
//		 valiteWebsites.add("http://www.58.com");
//		 valiteWebsites.add("http://www.ctrip.com");
//		 valiteWebsites.add("http://www.ifeng.com");
//		valiteWebsites.add("http://www.qqxxbd.com/testip.jsp");
//		valiteWebsites.add("http://www.lagou.com");
	}

	static {
		init();
	}

	/**
	 * 
	 * @param proxyHost
	 * @param proxyPort
	 * @return
	 */
	public static boolean validateIp(String proxyHost, int proxyPort) {
		if (valiteWebsites.size() == 0) {
			logger.error("没有检测网站!");
			return false;
		}
		long start = System.currentTimeMillis();

		// 循环取待检测网站
		String testUrl = valiteWebsites.remove(0);
		valiteWebsites.add(testUrl);

		boolean isOk = false;
		CloseableHttpClient closeableHttpClient = null;
		try {
			// HttpClientBuilder httpClientBuilder =
			// HttpClientBuilder.create().build();
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			closeableHttpClient = HttpClients.custom().setUserAgent(userAgent)
					/**/ .setRoutePlanner(routePlanner) .build();
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();

			HttpGet get = new HttpGet(testUrl);
			get.setConfig(requestConfig);
			HttpResponse resp = closeableHttpClient.execute(get);
			StatusLine statusLine = resp.getStatusLine();
			logger.info("statusLine:" + statusLine);
			isOk = statusLine.getStatusCode() == 200;
//			HttpEntity httpEntity = resp.getEntity();
//			String result = EntityUtils.toString(httpEntity);
//			logger.info("结果:" + result.trim());
		} catch (Exception e) {
			logger.error("#httpGeT error", e);
		} finally {
			try {
				if (closeableHttpClient != null) {
					closeableHttpClient.close();
				}
			} catch (IOException e) {
				logger.warn(" close client error");
			}
		}
		long end = System.currentTimeMillis();
		long spend = end - start;

		logger.info(testUrl + "," + proxyHost + ":" + proxyPort + " spend:" + spend + " ms :" + isOk);
		return isOk;
	}

	protected static ProxyInfoDao proxyInfoDao = new ProxyInfoDao();

	public static void validateAll() {
		List<BasicDBObject> proxyInfos = proxyInfoDao.getAll();
		int allCount =proxyInfos.size() ;
		int okCount =0 ;
		for (int i = 0; i < allCount   ; i++) {
			logger.info("第"+(i+1)+"/"+allCount+"个.."  );
			BasicDBObject proxyInfo = proxyInfos.get(i);
			String proxyHost = proxyInfo.getString("ip");
			boolean isOk = false;
			try {
				int proxyPort = Integer.parseInt(proxyInfo.getString("port"));
				long start = System.currentTimeMillis();
				isOk = validateIp(proxyHost, proxyPort);
				long end = System.currentTimeMillis();
				long spend = end - start;
				double response = spend / 1000.0;
				proxyInfo.put("response", response);
			} catch (Exception e) {
				logger.error(proxyInfo + "-->" + e.getMessage());
			}
			if (!isOk) {
				ObjectId objectId =  proxyInfo.getObjectId("_id");
				proxyInfoDao.deleteById(objectId);
			} else {
				okCount++ ;
				proxyInfo.put("verifyTime", new Date()) ; //16-08-15 12:43
				proxyInfo.put("enable", true);
				proxyInfoDao.updateById(proxyInfo);
			} 
			// logger.info(" "+proxyInfo);
		}
		logger.info("结果比例:"+okCount +"/" +allCount);
	}

	@Test
	public void testAll() {
		validateAll();
	}

	////////////////////////////////////
	@Test
	public void test() {
		// 用户IP:175.43.242.135,type:x-forwarded-for
		System.out.println(System.getProperty("java.io.tmpdir"));
		String ip = "12.202.111.4";
		int port = 8080;
		// ip = "121.193.143.249";
		// port = 80;
		int count = 10;
		for (int i = 0; i < count; i++) {
			ProxyValidator.validateIp(ip, port);
		}
	}

}
