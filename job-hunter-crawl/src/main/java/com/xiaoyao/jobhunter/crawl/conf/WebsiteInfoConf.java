package com.xiaoyao.jobhunter.crawl.conf;

import java.util.ArrayList;
import java.util.List;

import com.xiaoyao.jobhunter.commons.constant.X;

/**
 * 站点配置信息
 * 
 * @author 旷明爱
 * @date 2016年8月12日 下午11:30:18
 * @email mingai.kuang@mljr.com
 */
public class WebsiteInfoConf {

	private String id;
	private String name;
	private boolean useProxy = false || X.ISDEBUG; // 是否使用代理,默认false
	private int retryTimes = 3; // 重试次数
	private int intervalSleepMs = 100;// 抓取间隔

	private String parseType; // xml , json
	private int thread;
	private String domain;
	private String description;
	private List<String> helpUrls;
	private List<String> entryUrls;
	private List<ClassifyInfoConf> classifyInfoConfs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public int getIntervalSleepMs() {
		return intervalSleepMs;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setIntervalSleepMs(int intervalSleepMs) {
		this.intervalSleepMs = intervalSleepMs;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public int getThread() {
		return thread;
	}

	public String getParseType() {
		return parseType;
	}

	public void setParseType(String parseType) {
		this.parseType = parseType;
	}

	public void setThread(int thread) {
		this.thread = thread;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getHelpUrls() {
		if (helpUrls == null) {
			helpUrls = new ArrayList<String>();
		}
		return helpUrls;
	}

	public void setHelpUrls(List<String> helpUrls) {
		this.helpUrls = helpUrls;
	}

	public List<String> getEntryUrls() {
		if (entryUrls == null) {
			entryUrls = new ArrayList<String>();
		}
		return entryUrls;
	}

	public void setEntryUrls(List<String> entryUrls) {
		this.entryUrls = entryUrls;
	}

	public List<ClassifyInfoConf> getClassifyInfoConfs() {
		return classifyInfoConfs;
	}

	public void setClassifyInfoConfs(List<ClassifyInfoConf> classifyInfoConfs) {
		this.classifyInfoConfs = classifyInfoConfs;
	}

}
