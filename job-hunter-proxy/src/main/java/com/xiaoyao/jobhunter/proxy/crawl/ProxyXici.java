package com.xiaoyao.jobhunter.proxy.crawl;

import java.io.IOException;
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
 * http://www.xici.net.co/nt/ <BR/>
 * http://www.xici.net.co/nt/2 <BR/>
 * 
 * http://www.xici.net.co/nn/ <BR/>
 * http://www.xici.net.co/wn/ <BR/>
 * http://www.xici.net.co/wt/ <BR/>
 * 西刺免费代理
 * @author 旷明爱
 * 
 */
public class ProxyXici extends BaseProxyCrawl {

	static Logger logger = LoggerFactory.getLogger(ProxyXici.class);

	static final int MAX_PAGE = 1;// 最大翻页.
	static final int MAX_DELAY = 3;// 延时.秒

	HttpUtil httpUtil = new HttpUtil("UTF-8");

	static final Map<String, String> regionTypeMap = new HashMap<String, String>();
	static {
		regionTypeMap.put("nt", "国内高匿");
		regionTypeMap.put("nn", "国内普通");
//		regionTypeMap.put("wn", "国外高匿");
//		regionTypeMap.put("wt", "国外普通");
	}
	 public ProxyXici() {
		 httpUtil.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0");
	 }
	// 1.按分类.
	// 2.分页
	public void crawl() {
		logger.info("获取西刺代理.");
		Set<String> keySet = regionTypeMap.keySet();
		for (String regionTypeId : keySet) {
			for (int pageIndex = 1; pageIndex <= MAX_PAGE; pageIndex++) {
				String urlStr = "http://www.xici.net.co/" + regionTypeId + "/" ;//+ pageIndex;
				String localPath = FileUtil.PROXY_PAGE + urlStr.replaceAll("[\\?:/\\\\]", "-");
				
				Map<String, String> headMap=new HashMap<String, String>() ;
				headMap.put("Host", "www.xici.net.co");
				headMap.put("Referer", "http://www.xici.net.co/nt/");
				
				String page="";
				try {
					page = httpUtil.getPage(urlStr, headMap);
				} catch (IOException e) {
					logger.error(""+e);
				}
				List<ProxyInfo> proxyInfos = parse(page);
				logger.info(regionTypeId+"-"+pageIndex + " 数量:"+proxyInfos.size());
				insertAfterDelete(proxyInfos);//根据IP删除再插入
			}
		}
	}

	protected List<ProxyInfo> parse(String page) {
		List<ProxyInfo> proxyInfos = new ArrayList<ProxyInfo>();
		String regex = "<tr class.+?>\\s*<td class=\"country\">.*?</td>"
				+ "\\s*<td>(?<ip>.*?)</td>\\s*<td>(?<port>.*?)</td>"
				+ "\\s*<td>(?<location>.*?)</td>" 
				+ "\\s*<td class=\"country\">(?<anonymityType>.*?)</td>"
				+ "\\s*<td>(?<protocol>.*?)</td>"
				+ "\\s*<td class=\"country\">\\s*<div title=\"(?<response>.*?)秒\".+?</td>" 
				+ "\\s*<td class=\"country\">\\s*<div title=\".*?秒\".+?</td>"  
				+ "\\s*<td>.*?</td>" 
				+ "\\s*<td>(?<verifyTime>.*?)</td>\\s*</tr>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(page);
		while (matcher.find()) {

			String ip = matcher.group("ip");
			String port = matcher.group("port");
			String verifyTime = matcher.group("verifyTime");// 最后验证时间. //可能没有
			double response = Double.parseDouble(matcher.group("response"));// 响应速度,秒//可能没有

			String protocol = matcher.group("protocol");// HTTP,HTTPS
			protocol = getProtocol(protocol);
			String location = matcher.group("location").replaceAll("<.+?>", " ").trim();
			String anonymityType = matcher.group("anonymityType");// 普通,匿名,高匿名,透明
			anonymityType = getProxyType(anonymityType);

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
		ProxyXici proxyXici = new ProxyXici();
		proxyXici.crawl();
	}

}
