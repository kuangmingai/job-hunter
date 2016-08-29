package com.xiaoyao.jobhunter.crawl.proxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;

import com.mongodb.BasicDBObject;
import com.xiaoyao.jobhunter.proxy.dao.ProxyInfoDao;

import us.codecraft.webmagic.Site;

/**
 * IP 代理管理
 * 
 * @author 旷明爱
 * @date 2016年8月15日 上午12:37:25
 * @email mingai.kuang@mljr.com
 */
public class JobProxyManager {

	public static final String proxyFilePath = "";

	protected static ProxyInfoDao proxyInfoDao = new ProxyInfoDao();

	public static List<HttpHost> readtHttpProxy() {
		List<HttpHost> hosts = new ArrayList<HttpHost>();
		HttpHost httpProxy = null;

		List<BasicDBObject> proxyInfos = proxyInfoDao.getAll();
		for (int i = 0; i < proxyInfos.size(); i++) {
			BasicDBObject proxyInfo = proxyInfos.get(i);
			String proxyHost = proxyInfo.getString("ip");
			int proxyPort = Integer.parseInt(proxyInfo.getString("port"));
			httpProxy = new HttpHost(proxyHost, proxyPort);
			hosts.add(httpProxy);
		}
		return hosts;
	}

	public static List<String[]> getProxyPool() {
		List<HttpHost> hosts = readtHttpProxy();
		List<String[]> proxyHostPool = new ArrayList<String[]>();

		for (HttpHost httpProxy : hosts) {
			String[] proxyArr = new String[] { httpProxy.getHostName(), httpProxy.getPort() + "" };
			proxyHostPool.add(proxyArr);
		}
		return proxyHostPool;
	}
}
