package com.xiaoyao.jobhunter.proxy.crawl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyao.jobhunter.proxy.pojo.ProxyInfo;
import com.xiaoyao.jobhunter.util.FileUtil;
import com.xiaoyao.jobhunter.util.HttpUtil;

/**
 * @deprecated 网站可能关闭
 * http://proxy.com.ru/list_2.html
 * 
 * http://proxy.com.ru/gaoni/list_2.html http://proxy.com.ru/niming/
 * http://proxy.com.ru/touming/
 * 
 * 俄罗斯 免费代理.每5分钟自动更新一次
 * 
 * @author 旷明爱
 * 
 */
public class ProxyRuFree extends BaseProxyCrawl {
	static Logger logger = LoggerFactory.getLogger(ProxyRuFree.class);

	static final int MAX_PAGE = 1;// 最大翻页.
	static final int MAX_DELAY = 3;// 延时.秒

	HttpUtil httpUtil = new HttpUtil("GBK");

	static final Map<String, String> regionTypeMap = new HashMap<String, String>();
	static {
		regionTypeMap.put("gaoni", "高匿");
		regionTypeMap.put("touming", "透明");
		regionTypeMap.put("niming", "匿名");
	}

	// 1.按分类.
	// 2.分页
	public void crawl() {
		logger.info("获取俄罗斯代理.");
		Set<String> keySet = regionTypeMap.keySet();
		for (String regionTypeId : keySet) {
			for (int pageIndex = 1; pageIndex <= MAX_PAGE; pageIndex++) {
				String urlStr = "http://proxy.com.ru/" + regionTypeId + "/list_" + pageIndex + ".html";
				String localPath = FileUtil.PROXY_PAGE + urlStr.replaceAll("[\\?:/\\\\]", "-");
				String page = readFileOrHttp(localPath, httpUtil, urlStr, true);
				List<ProxyInfo> proxyInfos = parse(page); 
				insertAfterDelete(proxyInfos);//根据IP删除再插入
			}
		}
	}

	protected List<ProxyInfo> parse(String page) {
		List<ProxyInfo> proxyInfos = new ArrayList<ProxyInfo>();
		String regex = "<tr><b><td>.+?</td>\\s*<td>(?<ip>.*?)</td>\\s*<td>(?<port>.*?)</td>" +
				"\\s*<td>(?<anonymityType>.*?)</td>\\s*<td>(?<location>.*?)</td></b></tr>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(page);
		while (matcher.find()) {
			String ip = matcher.group("ip");
			String port = matcher.group("port");
			String location = matcher.group("location");
			String anonymityType = matcher.group("anonymityType");// 普通,匿名,高匿名,透明
			anonymityType = getProxyType(anonymityType);
			// 没有以下三个数据
			String verifyTime = "";// 最后验证时间. //可能没有
			double response = 0;// 响应速度,秒//可能没有
			String protocol = "HTTP";// HTTP,HTTPS

			ProxyInfo proxyInfo = new ProxyInfo();
			proxyInfo.setIp(ip);
			proxyInfo.setPort(port);
			proxyInfo.setVerifyTime(verifyTime);
			proxyInfo.setResponse(response);
			proxyInfo.setProtocol(protocol);
			proxyInfo.setLocation(location);
			proxyInfo.setAnonymityType(anonymityType);

			proxyInfos.add(proxyInfo);
		}
		return proxyInfos;
	}

	public static void main(String[] args) {
		ProxyRuFree proxyRuFree = new ProxyRuFree();
		proxyRuFree.crawl();
	}
}
