package com.xiaoyao.jobhunter.crawl.download;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyao.jobhunter.commons.constant.X;
import com.xiaoyao.jobhunter.commons.util.FileUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;

/**
 * 下载器.每个站点不能共用配置
 * 
 * @author 旷明爱
 * @date 2016年8月13日 上午10:11:45
 * @email mingai.kuang@mljr.com
 */
public class MyCacheDownload extends HttpClientDownloader {

	static Logger logger = LoggerFactory.getLogger(MyCacheDownload.class);

	static boolean idDebug =false;
	// help url
	List<String> helpUrls = null;
	String helpUrlRegex = null;
	long cachedMs = 365*24*3600*1000;// 缓存时间 ,小于0为永久
	
	int pageMinLength =1*1024;
	String filterRegex=null;//"您已被封禁，您的IP是";
	
	public void setHelpUrls(List<String> helpUrls) {
		this.helpUrls = helpUrls;
		if (CollectionUtils.isNotEmpty(helpUrls)) {
			StringBuffer buffer = new StringBuffer();
			for (String helpUrl : helpUrls) {
				buffer.append("(").append(helpUrl).append(")|");
			}
			buffer = buffer.deleteCharAt(buffer.length() - 1);
			helpUrlRegex = buffer.toString();
		}
	}

	public List<String> getHelpUrls() {
		if (helpUrls == null) {
			helpUrls = new ArrayList<String>();
		}
		return helpUrls;
	}

	protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
//		logger.info("444 getHtmlCharset");
		return super.getHtmlCharset(httpResponse, contentBytes);
	}

	@Override
	protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
//		logger.info("333 getContent");
		return super.getContent(charset, httpResponse);
	}

	@Override
	public Html download(String url, String charset) {
//		logger.info("11 download:" + url + ">" + charset);
		return super.download(url, charset);
	}

	///////////////////////
	///////////////////////

	public static final int URL_MAX_LENGTH=200 ;
	public String parseLocalPath(String url) {
		String localPath = url.replaceFirst("https?://", "");
		localPath = localPath.replaceAll("[\\?&:\\*]", "_");// 文件名特殊字符
		if (localPath.endsWith("/")) {
			localPath+="index";
		}
		localPath = X.HTML_SOURCE_DIR + localPath;
		if (localPath.length()>URL_MAX_LENGTH) {
			localPath=localPath.substring(0,URL_MAX_LENGTH);
		}
		if (!localPath.endsWith(X.SOURCE_FILE_SUFFIX)) {
			localPath=localPath +".html"; ;
		}
		return localPath;
	}

	public Page getLocalPage(String localPath, Request request) throws IOException {
		Page page = new Page();
		String rawText = FileUtil.readAbsolutlyFile(localPath);
		if (rawText.length()<pageMinLength ) {
			if ( StringUtils.isNotBlank(filterRegex) && rawText.matches(filterRegex) ) {
				return null;
			}
		}
		 
		
		page.setRawText(rawText);
		page.setRequest(request);
		page.setUrl(new PlainText(request.getUrl()));
		return page;
	}

	public void saveLocalPage(Page page , String localPath) {
		if (page==null  ) {
			return;
		}
		String rawText =page.getRawText();
		if (StringUtils.isEmpty(rawText)){
			return;
		}
		if (rawText.length()<pageMinLength ){
			if(StringUtils.isNotBlank(filterRegex) && rawText.matches(filterRegex) ) {
				return;
			}
		}
		
		File file = new File(localPath);
		File parentDir = file.getParentFile();
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		FileUtil.saveAbsolutlyFile(localPath, page.getRawText());
	}

	///////////////////////
	///////////////////////
	@Override
	public Page download(Request request, Task task) {
//		logger.info("11 download:" + request);
		String url = request.getUrl();
		String localPath = parseLocalPath(url);
		Page page = null;
		File file = new File(localPath);

		// 是否需要更新 帮助页面
		boolean needUpdate = !X.ISDEBUG && url.matches(helpUrlRegex);

		// 判断本地文件
		if (!needUpdate && !file.exists() || System.currentTimeMillis() - file.lastModified() > cachedMs) {
			logger.info("文件不存在或者过期exists:"+file.exists());
			needUpdate = true;
		}
		
		if (!needUpdate) { // 直接取缓存页
			logger.info("本地页面:" + url + "," + localPath);
			try {
				page = getLocalPage(localPath, request);
				if (page==null) {
					logger.info("本地没取到!页面内容太少,或者被封");
					needUpdate=true;
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("取本地数据失败:" + e);
			}
		}

		if (needUpdate) { // 需要更新辅助页面
//			logger.info("更新页面:" + url);
			page = super.download(request, task);
			saveLocalPage(page, localPath);// 保存页面
		} 
		// logger.info("page:"+page);
		return page;
	}

	@Override
	public Html download(String url) {
//		logger.info("111download:" + url); 
		return super.download(url);
	}

	@Override
	protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
//		logger.info("222 handleResponse");
		return super.handleResponse(request, charset, httpResponse, task);
	}

	public void setPageMinLength(int pageMinLength) {
		this.pageMinLength = pageMinLength;
	}
	public void setFilterRegex(String filterRegex) {
		this.filterRegex = filterRegex;
	}
}
