package com.xiaoyao.jobhunter.proxy.crawl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.xiaoyao.jobhunter.proxy.dao.ProxyInfoDao;
import com.xiaoyao.jobhunter.proxy.pojo.ProxyInfo;

/** 
 * 爬虫代理基类
 * 
 * @author 旷明爱
 * 
 */
public class BaseProxyCrawl extends BaseService {
	protected ProxyInfoDao proxyInfoDao = new ProxyInfoDao();
	protected int MAX_DELAY = 3;// 延时.秒

	public String getProtocol(String str) {
		String protocol = "HTTP";
		if (str.contains("HTTPS")) {
			protocol = "HTTPS";
		}
		return protocol;

	}

	static Map<String, String> proxyTypeMap = new HashMap<String, String>();
	static Set<String> proxyTypeSet = new HashSet<String>();
	static {
		proxyTypeMap.put("普通", "普通");
		proxyTypeMap.put("透明", "透明");
		proxyTypeMap.put("高匿", "高匿");
		proxyTypeMap.put("匿名", "普通");
		proxyTypeSet = proxyTypeMap.keySet();
	}

	public String getProxyType(String str) {
		String proxyType = "普通";
		for (String key : proxyTypeSet) {
			if (str.contains(key)) {
				proxyType = proxyTypeMap.get(key);
				break;
			}
		}
		return proxyType;
	}

	public void crawl() {
	}

	protected List<ProxyInfo> parse(String page) {
		return null;
	}

	/**
	 * 根据IP删除,再插入
	 * 
	 * @param proxyInfos
	 */
	protected void insertAfterDelete(List<ProxyInfo> proxyInfos) {
		for (ProxyInfo proxyInfo : proxyInfos) {
			DBObject dbObject = new BasicDBObject("ip", proxyInfo.getIp());
			proxyInfoDao.delete(dbObject);
			if (proxyInfo.getResponse() < MAX_DELAY) {
				proxyInfoDao.insert(proxyInfo);
			}
		}
	}
}
