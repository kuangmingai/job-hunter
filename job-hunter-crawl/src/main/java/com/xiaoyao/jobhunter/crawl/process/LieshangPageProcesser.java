package com.xiaoyao.jobhunter.crawl.process;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import us.codecraft.webmagic.Page;

public class LieshangPageProcesser extends BasePageProcesser {

	public static final String id = "lieshang";

	public LieshangPageProcesser() { 
		super(id);
	}

	private static final int SO_TIMEOUT = 120000;
	private static final int CONNECT_TIMEOUT = 120000;  
	

	/**
	 * 转换响应数据
	 * @param resp
	 * @return
	 */
	private static String convertResponseBytes2String(HttpResponse resp) {
		String result = "";
		InputStream instream  =null;
		try {
			instream  = resp.getEntity().getContent();
			byte[] respBytes = IOUtils.toByteArray(instream );
			result = new String(respBytes, Charset.forName("UTF-8"));
		} catch (Exception e) {
			logger.error("# error", e);
		} finally{
			try {
				if(instream != null)
					instream .close();
			} catch (IOException e) {
				logger.error("# close Io error", e);
			}
		}
		return result;
	}
	/**
	 * 登录
	 */
	public void login(){
		
		String url="http://www.hunteron.com/auth/login.json?st="+System.currentTimeMillis();
		CloseableHttpClient closeableHttpClient = null;
		String result =null;
		try {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("imageVeriCode=" ) ;
			stringBuffer.append("&isRememberLogin=true" ) ;
			stringBuffer.append("&loginName=13828804427" ) ;
			stringBuffer.append("&password=74833024addf819bb0580908e5c98656" ) ;
			HttpEntity entity = new StringEntity(stringBuffer.toString()  , "UTF-8");
			closeableHttpClient = httpClientBuilder.build();
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).
						setConnectTimeout(CONNECT_TIMEOUT).build();//设置请求和传输超时时间
			HttpPost post = new HttpPost(url);
			post.setConfig(requestConfig);
			post.setEntity(entity);
			
			post.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
			HttpResponse resp = closeableHttpClient.execute(post);
			Header[] headers =	resp.getAllHeaders() ;
			
			for (int i = 0; i < headers.length; i++) {
				logger.info("  --> " +headers[i]);

				String name =headers[i].getName() ;
				String value =headers[i].getValue()  ;
				getSite().addCookie(name, value);
			}
			
			 result = convertResponseBytes2String(resp);
			 logger.info("登录结果:"+result);
		} catch (Exception e) {
			logger.error("#httpPOST error", e);
		} finally{
			try {
				if(closeableHttpClient!=null)
					closeableHttpClient.close();
			} catch (IOException e) {
				logger.info("# close client error" + result);
			}
		}
		
		
	}
	
	public void isLogin(){
		
	}

	static String startRegex="(&start=\\d+)" ;
	static Pattern startPattern =Pattern.compile(startRegex);
	
	@Override
	public void process(Page page) {
		// 处理掉 无效的分页.每个城市重新开始翻页.
		String content =page.getRawText() ;
		String url =page.getUrl().toString();
		Matcher startMatcher =startPattern.matcher(url) ;
		if (startMatcher.find()) {
			String startUri =startMatcher.group(1);
			int len1 =content.length() ;
			content=content.replaceAll(startUri,"");
			int len2 =content.length() ;
//			logger.info(content);
			logger.info(" -->"+startUri+","+len1+"->"+len2);
		}
		content=content.replaceAll("&start=0","");
		page.setRawText(content) ;//一定要替换原来的网页源码
		
		super.process(page);
	}

	public static void main(String[] args) {
		LieshangPageProcesser pageProcesser = new LieshangPageProcesser();
//		List<String> testLinsk =new ArrayList<>() ;
////		testLinsk.add("http://www.hunteron.com/position/detail/64371.htm") ;
//		testLinsk.add("http://www.hunteron.com/position/list.htm?cityId=30101&start=435") ;
//		pageProcesser.setTestLinks(testLinsk);
		pageProcesser.startAsync();
	}
}