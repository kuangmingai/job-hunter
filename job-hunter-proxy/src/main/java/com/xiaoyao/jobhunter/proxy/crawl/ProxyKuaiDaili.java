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
import com.xiaoyao.jobhunter.util.HttpUtil;

/**
 * http://www.kuaidaili.com/free/inha/ <BR/>
 * http://www.kuaidaili.com/free/inha/2/  <BR/>
 * http://www.kuaidaili.com/free/intr/ <BR/>
 * 
 * http://www.kuaidaili.com/free/outha/ <BR/>
 * http://www.kuaidaili.com/free/outtr/ <BR/>
 * 快代理  GZIP　压缩．
 *  
 * @author 旷明爱
 *
 */
public class ProxyKuaiDaili extends BaseProxyCrawl{

	static Logger logger = LoggerFactory.getLogger(ProxyKuaiDaili.class) ;

	static final int MAX_PAGE = 1;// 最大翻页.
	static final int MAX_DELAY = 3;// 延时.秒

//	HttpUtil httpUtil = new HttpClientUtil("UTF-8");
	HttpUtil httpUtil = new HttpUtil("UTF-8");

	static final Map<String, String> regionTypeMap = new HashMap<String, String>();
	static {
		regionTypeMap.put("inha", "国内高匿");
		regionTypeMap.put("intr", "国内普通");
		regionTypeMap.put("outha", "国外高匿");
		regionTypeMap.put("outtr", "国外普通");
	}

	// 1.按分类.
	// 2.分页
	public void crawl() {
		logger.info("获取快代理.") ;
		Set<String> keySet = regionTypeMap.keySet();
		for (String regionTypeId : keySet) {
			for (int pageIndex = 1; pageIndex <= MAX_PAGE; pageIndex++) {
				String urlStr = "http://www.kuaidaili.com/free/" + regionTypeId + "/" ;//+ pageIndex;
//				String localPath = FileUtil.PROXY_PAGE + urlStr.replaceAll("[\\?:/\\\\]", "-");
//				String page = readFileOrHttp(localPath, httpUtil, urlStr, false);
				
				Map<String, String> headMap =new HashMap<String, String>();
//				headMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") ;
//				headMap.put("Accept-Encoding", "gzip, deflate") ;
//				headMap.put("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3") ;
//				headMap.put("Connection", "keep-alive") ;
//				headMap.put("Host", "www.kuaidaili.com") ;
				headMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0") ;
				
				String page ="" ;
				try {
					page=httpUtil.getPage(urlStr, headMap)				;
				} catch (IOException e) {
					logger.error(""+e);
				}
				
				List<ProxyInfo> proxyInfos = parse(page);
				insertAfterDelete(proxyInfos);//根据IP删除再插入
			}
		}
	}

	protected List<ProxyInfo> parse(String page) {
		page = page.replaceFirst(".+?<div id=\"list\"", "");
		page = page.replaceFirst("</table>.+?", "");
		page = page.replaceAll("\"", "'");
		logger.info(""+page);

		List<ProxyInfo> proxyInfos = new ArrayList<ProxyInfo>();
		String regex = "<td data-title='IP'>(?<ip>.*?)</td>\\s*<td data-title='PORT'>(?<port>.*?)</td>"
				+ "\\s*<td data-title='匿名度'>(?<anonymityType>.*?)</td>"
				+ "\\s*<td data-title='类型'>(?<protocol>.*?)</td>\\s*<td data-title='位置'>(?<location>.*?)</td>"
				+ "\\s*<td data-title='响应速度'>(?<response>.*?)秒</td>\\s*<td data-title='最后验证时间'>(?<verifyTime>.*?)</td>\\s*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(page);
		while (matcher.find()) { 
			String ip = matcher.group("ip");
			String port = matcher.group("port");
			String verifyTime = matcher.group("verifyTime");// 最后验证时间. //可能没有
			double response = Double.parseDouble(matcher.group("response"));// 响应速度,秒//可能没有

			String protocol = matcher.group("protocol");// HTTP,HTTPS
			protocol = getProtocol(protocol);
			String location = matcher.group("location");
			location=location.replaceAll("<.+?>", "").trim();
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
		ProxyKuaiDaili proxyKuaiDaili = new ProxyKuaiDaili() ;
		proxyKuaiDaili.crawl() ;
	}
}
