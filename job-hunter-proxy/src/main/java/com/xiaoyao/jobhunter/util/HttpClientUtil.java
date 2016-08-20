package com.xiaoyao.jobhunter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP 操作类. <BR>
 * 可以加上传参 编码的接口.<BR>
 * cookie 使用上一次返回的cookie<BR>
 * 
 * @author 旷明爱
 * @date Aug 28, 2012 2:25:19 PM
 */
public class HttpClientUtil  extends HttpUtil{
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	// 超时时间 30 s
	private int TIMEOUT = 30 * 1000;
	private static HttpClientUtil httpUtil = new HttpClientUtil("UTF-8");

	private  String charset = "UTF-8";

	public void setTIMEOUT(int timeout) {
		TIMEOUT = timeout;
	}

	/**
	 * 默认编码
	 * 
	 * @return
	 */
	public static HttpClientUtil getDefault() {
		return httpUtil;
	}

	public HttpClientUtil() { 
	}

	/**
	 * 可以指定编码
	 * 
	 * @param charset
	 */
	public HttpClientUtil(String charset) {
		this.charset = charset;
	}

	// *****************************************************
//	static {
//		try {
//			trustAllHttpsCertificates();
//		} catch (KeyManagementException e) {
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Https 初始化失败!");
//			throw new RuntimeException(e);
//		}
//	}

	/**
	 * 从 URLConnection 中提取 cookie.
	 * 
	 * @param connection
	 * @return
	 */
	Map<String, String> cookieMap = new HashMap<String, String>();//

	/**
	 * Get 方式获取页面
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public String getPage(String urlStr) throws IOException {
		return getPage(urlStr, null);
	}

	/**
	 * Get 方式获取页面,可以设置头部
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public String getPage(String urlStr, Map<String, String> headMap) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(urlStr);
		if (headMap != null) {
			for (Entry<String, String> entry : headMap.entrySet()) {
				httpGet.addHeader(entry.getKey(), entry.getValue());
			}
		}
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity entity = httpResponse.getEntity();
		String page = "";
		if (entity != null) {
			InputStream instreams = entity.getContent();
			page = convertStreamToString(instreams,charset);
		}
		return page;
	}

	public static String convertStreamToString(InputStream is,String charset) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,charset));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Post 方式获取页面,可以设置头部
	 * 
	 * @param urlStr
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String postPage(String urlStr, String param, Map<String, String> headMap) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(urlStr);
		if (headMap != null) {
			for (Entry<String, String> entry : headMap.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
		}
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String strs[] = params[i].split("=");
			nvps.add(new BasicNameValuePair(strs[0], strs[1]));
		}
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));  
		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity entity = httpResponse.getEntity();
		String page = "";
		if (entity != null) {
			InputStream instreams = entity.getContent();
			page = convertStreamToString(instreams,charset);
		}
		return page;
	}

	/**
	 * Post 方式获取页面
	 * 
	 * @param urlStr
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String postPage(String urlStr, String param) throws IOException {
		return postPage(urlStr, param, null);
	}

	/**
	 * HTTPS 证书
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static void trustAllHttpsCertificates() throws NoSuchAlgorithmException, KeyManagementException {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("TLS"); // SSL
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

}
