package com.xiaoyao.jobhunter.util;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

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
public class HttpUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	// 超时时间 30 s
	private int TIMEOUT = 30 * 1000;
	private static HttpUtil httpUtil = new HttpUtil("UTF-8");

	private String charset = "UTF-8";

	public void setTIMEOUT(int timeout) {
		TIMEOUT = timeout;
	}

	/**
	 * 默认编码
	 * 
	 * @return
	 */
	public static HttpUtil getDefault() {
		return httpUtil;
	}

	public HttpUtil() {
	}

	/**
	 * 可以指定编码
	 * 
	 * @param charset
	 */
	public HttpUtil(String charset) {
		this.charset = charset;
	}

	// *****************************************************
	// static {
	// try {
	// trustAllHttpsCertificates();
	// } catch (KeyManagementException e) {
	// e.printStackTrace();
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error("Https 初始化失败!");
	// throw new RuntimeException(e);
	// }
	// }

	private String userAgent ="" ;
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	/**
	 * GET HttpURLConnection <BR>
	 * 限制访问间隔.
	 * 
	 * @param urlNode
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public HttpURLConnection getGetConnection(String urlStr, Map<String, String> headMap) throws IOException {
		URL url = new URL(urlStr);// ///
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Accept-Encoding", "gzip,deflate");  // GZIP
		if ( !ClassUtil.isEmpty( userAgent)) {
			con.setRequestProperty("User-Agent",userAgent);
		}else { 
			con.setRequestProperty("User-Agent",
			// IE
					// "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1;
					// Trident/4.0; InfoPath.2; .NET CLR 2.0.50727; .NET CLR
					// 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C; .NET4.0E)");
//					 "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0");
					"Baiduspider");
		}

		con.setReadTimeout(TIMEOUT);
		con.setConnectTimeout(TIMEOUT);
		con.setInstanceFollowRedirects(true);//  设置为false 即为不自动跳转.
		if (headMap == null) {
			return con;
		}
		for (Map.Entry<String, String> entry : headMap.entrySet()) {
			con.setRequestProperty(entry.getKey(), entry.getValue());
		}
		return con;
	}

	/**
	 * 从 URLConnection 中提取 cookie.
	 * 
	 * @param connection
	 * @return
	 */
	Map<String, String> cookieMap = new HashMap<String, String>();//

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
		if (!ClassUtil.isEmpty(param)) {
			con.getOutputStream().write(param.getBytes(charset)); // 要指定编码
		}
		// ************************************************
		// 跳转
		int code = ((HttpURLConnection) con).getResponseCode();
		if (code == 403|| code ==503) { // 封禁
			logger.info("code:" + code+",封禁");
			throw new IOException("error:"+code);
		}
		// String location = con.getHeaderField("Location");
		// if (!ClassUtil.isEmpty(location)) {
		// logger.info("跳转到:" + location);
		// HttpURLConnection cc = getGetConnection(location, null);
		// return getConData(cc, null);
		// }
		// **********************************************
		String line = "";
		StringBuffer buffer = new StringBuffer(100000);
		BufferedReader reader = null;
		InputStream is = null;
		try {
			
			String ContentEncoding =con.getHeaderField("Content-Encoding")+"";
			if (ContentEncoding.contains("gzip")) {
				is = new GZIPInputStream( con.getInputStream()); //GZIP 封装
			}else {
				is =   con.getInputStream() ;
			}
			
			
//			int leng = is.available();
//			logger.info("长度:"+leng) ;
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
	 * 检测最新修改时间,目录页面
	 * 
	 * @param urlStr
	 * @param book
	 * @return
	 * @throws IOException
	 */
	public String getPageByLastDate(String urlStr, Book book) throws IOException {
		HttpURLConnection con = getGetConnection(urlStr, null);
		con.connect();

		String html = "";
		StringBuffer buffer = new StringBuffer(100000);

		String line = "";
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
		} finally {
			if (reader != null) {
				reader.close();
				is.close();
			}
			con.disconnect();
		}
		html = buffer.toString();
		return html;
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
	 * POST HttpURLConnection
	 * 
	 * @param urlNode
	 * @param param
	 * @param headMap
	 * @return
	 * @throws IOException
	 */
	public HttpURLConnection getPostConnection(String urlStr, String param, Map<String, String> headMap)
			throws IOException {
		HttpURLConnection con = getGetConnection(urlStr, headMap);
		con.setRequestMethod("POST");// 
		con.setDoOutput(true);
		return con;
	}

	// //////////////////////////////////////////

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
			} else {
				
			}
			con.connect();
			// POST 方式
			if (!ClassUtil.isEmpty(param)) {
				con.getOutputStream().write(param.getBytes(charset)); // 要指定编码
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
