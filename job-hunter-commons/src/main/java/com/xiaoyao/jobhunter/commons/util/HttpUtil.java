package com.xiaoyao.jobhunter.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * HTTP 操作类. <BR>
 * 可以加上传参 编码的接口.<BR>
 * cookie 使用上一次返回的cookie<BR>
 * 
 * @author 旷明爱
 * @date Aug 28, 2012 2:25:19 PM
 */
public class HttpUtil {
	private static Logger logger = Logger.getLogger(HttpUtil.class);
	// 超时时间 30 s
	private int timeout = 30 * 1000;
	private static HttpUtil httpUtil = new HttpUtil();

	private String charset = "GBK";
	private String cookie = "";
	private int useCookie = 1; // -1 不使用, 0 使用新的 , 1 所有的cookie

	/**
	 * 默认编码
	 * 
	 * @return
	 */
	public static HttpUtil getDefault() {
		return httpUtil;
	}

	public HttpUtil setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public HttpUtil setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	private HttpUtil() {
	}

	/**
	 * 可以指定编码
	 * 
	 * @param charset
	 */
	public HttpUtil(String charset) {
		this.charset = charset;
	}

	/**
	 * 设置是否发送 cookie,构造后就要设置.
	 */
	public HttpUtil setUseCookie(int useCookie) {
		this.useCookie = useCookie;
		return this;
	}

	/**
	 * 手动添加cookie
	 * 
	 * @param cookie
	 */
	public void addCookie(String key, String value) {
		cookieMap.put(key, value);
		this.cookie = this.cookie + "; " + key + value;
	}

	/**
	 * 获取cookie串.
	 * 
	 * @param cookie
	 */
	public String getCookie() {
		return cookie;
	}

	/**
	 * 根据cookie名获取cookie
	 * 
	 * @param cookie
	 */
	public String getCookie(String cookieName) {
		return cookieMap.get(cookieName);
	}

	// *****************************************************
	static {
		try {
			trustAllHttpsCertificates();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Https 初始化失败!");
			throw new RuntimeException(e);
		}
	}

	/**
	 * GET HttpURLConnection <BR>
	 * 限制访问间隔.
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public HttpURLConnection getGetConnection(String urlStr, Map<String, String> headMap) throws IOException {
		URL url = new URL(urlStr);// 
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("User-Agent",
		// IE
				// "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1;
				// Trident/4.0; InfoPath.2; .NET CLR 2.0.50727; .NET CLR
				// 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C; .NET4.0E)");
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");

		con.setConnectTimeout(timeout);
		con.setReadTimeout(timeout);
		con.setInstanceFollowRedirects(false);// TODO 设置为false 即为不自动跳转.
		// 如果使用 cookie
		if (useCookie != -1) {
			con.setRequestProperty("Cookie", cookie);
			logger.debug("------本次发送cookie:" + cookie);
		}
		if (headMap == null) {
			return con;
		}
		for (Map.Entry<String, String> entry : headMap.entrySet()) {
			con.setRequestProperty(entry.getKey(), entry.getValue());
		}
		return con;
	}

	/**
	 * POST HttpURLConnection
	 * 
	 * @param url
	 * @param param
	 * @param headMap
	 * @return
	 * @throws IOException
	 */
	public HttpURLConnection getPostConnection(String urlStr, String param, Map<String, String> headMap) throws IOException {
		HttpURLConnection con = getGetConnection(urlStr, headMap);
		con.setRequestMethod("POST");// 
		con.setDoOutput(true);
		return con;
	}

	/**
	 * 从 URLConnection 中提取 cookie.
	 * 
	 * @param connection
	 * @return
	 */
	Map<String, String> cookieMap = new HashMap<String, String>();//

	public synchronized String getCookie(URLConnection connection) {
		if (useCookie == 0) {
			cookieMap.clear();// 使用全新的cookie
		}

		Map<String, List<String>> map = connection.getHeaderFields();
		// logger.info("HeaderFields :" + map);
		List<String> list = map.get("Set-Cookie");
		if (list == null) {
			list = map.get("Cookie");
		}
		String newCookie = "";
		if (list == null || list.size() == 0) {
			return cookie;
		}
		for (int i = 0; i < list.size(); i++) {
			String line = list.get(i) + " ";
			int pos = line.indexOf("=");
			int pos2 = line.indexOf(";");
			if (pos2 < pos) {
				pos2 = line.length();
			}
			cookieMap.put(line.substring(0, pos), line.substring(pos, pos2));
		}
		Set<String> keySet = cookieMap.keySet();
		list = new ArrayList<String>(keySet);
		// Collections.sort(list); //可能需要排序
		// Collections.reverse(list);//反序
		for (String key : list) {
			newCookie += (key + cookieMap.get(key) + "; ");
		}
		cookie = newCookie.substring(0, newCookie.length() - 2);
		logger.debug("newCookie :" + cookie);
		return newCookie;
	}

	/**
	 * 从连接中读取数据
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public String getConData(HttpURLConnection con, String param) throws IOException {
		// logger.debug("马上连接......");
		con.connect();
		// POST 方式
		if (!"null".equals(param + "")) {
			con.getOutputStream().write(param.getBytes(charset)); // 要指定编码
		}
		if (useCookie != -1) {
			getCookie(con);
		}

		// ************************************************
		// 跳转
		// int code = ((HttpURLConnection) con).getResponseCode();
		// System.out.println("code:" + code);
		// Map<String, List<String>> map = con.getHeaderFields();
		String location = con.getHeaderField("Location");
		if (!"null".equals(location + "")) {
			if (!location.startsWith("http:")) {
				String url = con.getURL().toString();
				location = url.replaceAll("com/.+", "com") + location;
			}
			logger.info("跳转到:" + location);
			HttpURLConnection cc = getGetConnection(location, null);
			if (useCookie != -1) {
				getCookie(cc);
			}
			String data = getConData(cc, null);
			return data;
		}
		// **********************************************
		String line = "";
		StringBuffer buffer = new StringBuffer(10000);
		BufferedReader reader = null;
		InputStream is = null;
		try {
			is = con.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, charset));
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			logger.error("获取页面失败!" + e.getMessage());
			throw e;
		} finally {
			if (reader != null) {
				reader.close();
				is.close();
			}
			con.disconnect();
		}
		return buffer.toString() + "";
	}

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
		HttpURLConnection con = getGetConnection(urlStr, headMap);
		return getConData(con, null);
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
		HttpURLConnection con = getPostConnection(urlStr, param, headMap);
		return getConData(con, param);
	}

	/**
	 * 下载文件.
	 * 
	 * @param urlStr
	 * @param param
	 * @param method
	 * @param headMap
	 * @return
	 */
	public byte[] downLoad(String urlStr, String param, String method, Map<String, String> headMap) {
		HttpURLConnection con = null;
		try {
			if (method.equalsIgnoreCase("GET")) {
				con = getGetConnection(urlStr, headMap);
			} else if (method.equalsIgnoreCase("POST")) {
				con = getPostConnection(urlStr, param, headMap);
			}
			con.connect();
			// POST 方式
			if (!"null".equals(param + "")) {
				con.getOutputStream().write(param.getBytes(charset)); // 要指定编码
			}
			if (useCookie != -1) {
				getCookie(con);
			}

			InputStream is = con.getInputStream();
			byte[] cbuf = new byte[5100000];// 最大约5M ,
			int read = 0;
			int leng = 0;
			int perByte = 1000;
			while ((read = is.read(cbuf, leng, perByte)) > 0) {
				leng += read;
			}
			byte[] realByte = new byte[leng];
			System.arraycopy(cbuf, 0, realByte, 0, leng);
			return realByte;
		} catch (IOException e) {
			logger.error("下载失败!" + e);
		}

		return null;
	}

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://www.baidu.com/ss/search");
		System.out.println(url.toString());
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
	 * 下载图片
	 * 
	 * @param picStr
	 * @return byte[] 下载失败返回null
	 */
	public byte[] getPic(String picStr) {
		try {
			HttpURLConnection con = getGetConnection(picStr, null);
			InputStream is = con.getInputStream();

			byte[] cbuf = new byte[1024000];// 最大约1M ,
			int read = 0;
			int leng = 0;
			int perByte = 1000;
			while ((read = is.read(cbuf, leng, perByte)) > 0) {
				leng += read;
			}
			byte[] realByte = new byte[leng];
			System.arraycopy(cbuf, 0, realByte, 0, leng);
			return realByte;
		} catch (Exception e) {
			logger.error("下载图片失败!" + e.getMessage());
		}
		return null;
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

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
			return;
		}
	}

}
