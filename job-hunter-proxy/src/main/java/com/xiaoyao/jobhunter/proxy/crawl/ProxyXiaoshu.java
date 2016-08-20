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
 * 小舒代理.
 * 
 * http://www.71https.com/?stype=1 <BR/>
 * http://www.71https.com/?stype=1&page=2 <BR/>
 * 
 * http://www.71https.com/?stype=2 <BR/>
 * http://www.71https.com/?stype=3 <BR/>
 * http://www.71https.com/?stype=4 <BR/>
 * 
 * 
 * @author 旷明爱
 * 
 */
public class ProxyXiaoshu extends BaseProxyCrawl {
	static Logger logger = LoggerFactory.getLogger(ProxyXiaoshu.class) ;

	static final int MAX_PAGE = 1;// 最大翻页.
	static final int MAX_DELAY = 3;// 延时.秒

	HttpUtil httpUtil = new HttpUtil("UTF-8");

	static final Map<String, String> regionTypeMap = new HashMap<String, String>();
	static {
		regionTypeMap.put("1", "国内高匿");
		regionTypeMap.put("2", "国内普通");
		regionTypeMap.put("3", "国外高匿");
		regionTypeMap.put("4", "国外普通");
	}

	// 1.按分类.
	// 2.分页
	public void crawl() {
		logger.info("获取小舒代理.") ;
		Set<String> keySet = regionTypeMap.keySet();
		for (String regionTypeId : keySet) {
			for (int pageIndex = 1; pageIndex <= MAX_PAGE; pageIndex++) {
				//http://www.xsdaili.com/index.php?s=/index/mfdl/p/2.html
				//http://www.xsdaili.com/index.php?s=/index/mfdl/type/2/p/4.html
				String urlStr = "http://www.xsdaili.com/index.php?s=/index/mfdl/" + regionTypeId + "/p/" + pageIndex+".html";
				logger.info("rawl:"+urlStr);
				String localPath = FileUtil.PROXY_PAGE + urlStr.replaceAll("[\\?:/\\\\]", "-");
				String page = readFileOrHttp(localPath, httpUtil, urlStr, true);
				List<ProxyInfo> proxyInfos = parse(page);
				insertAfterDelete(proxyInfos);//根据IP删除再插入
			}
		}
	}

	protected List<ProxyInfo> parse(String page) {
//		page = page.replaceFirst(".+?<div id=\"list\"", "");
//		page = page.replaceFirst("</table>.+?", "");
		page = page.replaceAll("\"", "'");
		logger.info("page:"+page);
		
		List<ProxyInfo> proxyInfos = new ArrayList<ProxyInfo>();
		//IP	PORT	匿名度	类型	get/post支持	位置	运营商	响应速度	最后验证时间
		String regex = "<td>(?<ip>.*?)</td>\\s*<td>(?<port>.*?)</td>\\s*<td>(?<anonymityType>.*?)</td>"
				+ "\\s*<td>(?<protocol>.*?)</td>"
				+ "\\s*<td>(?<method>.*?)</td>" //
				+ "\\s*<td>(?<location>.*?)</td>"
				+ "\\s*<td>(?<netOp>.*?)</td>" // 
				+ "\\s*<td>(?<response>.*?)秒</td>"
				+ "\\s*<td>(?<verifyTime>.*?)</td>\\s*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(page);
		while (matcher.find()) {
			String ip = matcher.group("ip");
			String port = matcher.group("port");
			String verifyTime = matcher.group("verifyTime");// 最后验证时间. //可能没有
			double response = Double.parseDouble(matcher.group("response"));// 响应速度,秒//可能没有

			String protocol = matcher.group("protocol");// HTTP,HTTPS
			protocol = getProtocol(protocol);
			String location = matcher.group("location").replaceAll("<.+?>", "").replaceAll("(&nbsp;)|(\\\\t)"," ").replaceAll("\\s+"," ").trim();
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
		ProxyXiaoshu proxyXiaoshu =new ProxyXiaoshu() ;
		proxyXiaoshu.crawl();
		
		
	}

}
