package com.xiaoyao.jobhunter.crawl.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyao.jobhunter.commons.constant.X;
import com.xiaoyao.jobhunter.crawl.conf.ClassifyInfoConf;
import com.xiaoyao.jobhunter.crawl.conf.ConfigureUtil;
import com.xiaoyao.jobhunter.crawl.conf.FieldConf;
import com.xiaoyao.jobhunter.crawl.conf.WebsiteInfoConf;
import com.xiaoyao.jobhunter.crawl.download.MyCacheDownload;
import com.xiaoyao.jobhunter.crawl.download.MySite;
import com.xiaoyao.jobhunter.crawl.pipeline.BaseJsonPipeline;
import com.xiaoyao.jobhunter.crawl.pipeline.BasePipeline;
import com.xiaoyao.jobhunter.crawl.proxy.JobProxyManager;
import com.xiaoyao.jobhunter.crawl.spider.MyCacheSpider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

public class BasePageProcesser implements PageProcessor {
	static Logger logger = LoggerFactory.getLogger(BasePageProcesser.class);
	private String id = "";
	WebsiteInfoConf websiteInfoConf;
	// help url
	List<String> helpUrls = null;
	// parse
	List<String> entryUrls = null;
	List<ClassifyInfoConf> classifyInfoConfs = null;

	String regexUrl = null;
	protected Site site;
	/////////////////////////////
	/////////////////////////////
	protected BasePipeline  basePipeline = new BaseJsonPipeline(); // 解析器
	protected MyCacheDownload downloader = new MyCacheDownload();// 下载器

	protected Scheduler scheduler =new FileCacheQueueScheduler(X.URL_CACHE_PATH) ;// URL管理
//	protected Scheduler scheduler =new RedisScheduler("127.0.0.1") ;// URL管理,分布式抓取
	
	public BasePageProcesser(String id) {
		this.id = id;
		try {
			websiteInfoConf = ConfigureUtil.getWebsiteInfoConfById(id);
			classifyInfoConfs = websiteInfoConf.getClassifyInfoConfs();

			site = new MySite().setDomain(websiteInfoConf.getDomain())
					.setRetryTimes(websiteInfoConf.getRetryTimes()).setSleepTime(websiteInfoConf.getIntervalSleepMs());
			
			helpUrls = websiteInfoConf.getHelpUrls();
//			downloader.setThread(websiteInfoConf.getThread());
			downloader.setHelpUrls(helpUrls);

			entryUrls = websiteInfoConf.getEntryUrls();

			StringBuffer buffer = new StringBuffer();
			buffer.append("(") ;
			for (String helpUrl : helpUrls) {
				buffer.append("(").append(helpUrl).append(")|");
			}
			List<ClassifyInfoConf> classifyInfoConfs =	websiteInfoConf.getClassifyInfoConfs();
			for(ClassifyInfoConf classifyInfoConf :classifyInfoConfs){
				buffer.append("(").append(classifyInfoConf.getUrlRegex()).append(")|");
			}
			if (buffer.length()>0) {
				buffer = buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append(")") ;
			regexUrl = buffer.toString();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("初始化配置异常");
			// throw new InitException(e);
		} 
	}

	// http://sou.zhaopin.com/
	// http://sou.zhaopin.com/jobs/searchresult.ashx?jl=765&bj=2060000&sj=399
	// http://jobs.zhaopin.com/131567772250010.htm?ssidkey=y&ss=201&ff=03
	// http://jobs.zhaopin.com/sj083/ju_fbfzr/
	// http://company.zhaopin.com/CC278833588.htm
	// http://company.zhaopin.com/常州市诺众商贸有限公司_CC507459123.htm

	List<String> testLinsk =null; //单独测试某些URL
	public void setTestLinks(List<String> testLinsk){
		this.testLinsk =testLinsk ;
		scheduler=new QueueScheduler();//测试的是好用内存保存
	}
	public List<String> getHelpUrls() {
		if (helpUrls == null) {
			helpUrls =new ArrayList<String>() ;
		}
		return helpUrls;
	}
	public void setHelpUrls(List<String> helpUrls) {
		this.helpUrls = helpUrls;
		StringBuffer buffer = new StringBuffer();
		buffer.append("(") ;
		for (String helpUrl : getHelpUrls()) {
			buffer.append("(").append(helpUrl).append(")|");
		}
		if (buffer.length()>0) {
			buffer = buffer.deleteCharAt(buffer.length() - 1);
		} 
		buffer.append(")") ;
		regexUrl = buffer.toString();
	}
	public void process(Page page) {
		String url = page.getRequest().getUrl();
		logger.info( " 当前网页url ----> " + url);
//		logger.info("过滤前链接:" + page.getHtml().links().all());
		List<String> links = page.getHtml().links().regex(regexUrl).all();
//		 logger.info("过滤后链接:" + links);
//		for(String link:links){ 
//			if (link!=null&& ( link.contains("list.htm_cityId=32704&start=75")  || link.contains("start=435"))) {
//				logger.info("link:"+link);
////				try {
////					Thread.sleep(1000000);
////				} catch (Exception e) {
////					e.printStackTrace();
////				}
//			}
//		}
//		
		if (CollectionUtils.isNotEmpty(testLinsk)) {
			page.addTargetRequests(testLinsk); 
		}else{
			// #锚点重复URL处理
			Set<String> linkSet =new HashSet<String>();
			for (String link:links) {
				if (link!=null) {
					linkSet.add(link.replaceFirst("#.*", ""));
				}
			}
			links= new ArrayList<String>(  linkSet );
			page.addTargetRequests(links);
		}
//		logger.info("URL匹配规则");
		// if ... 各类网页的解析规则.
		for (ClassifyInfoConf classifyInfoConf : classifyInfoConfs) {
			boolean isMatch = url.matches(classifyInfoConf.getUrlRegex());
			logger.debug(classifyInfoConf.getUrlRegex() + ":" + isMatch);
			if (isMatch) {
				String datatype = classifyInfoConf.getDatatype();
				logger.info(" <" + websiteInfoConf.getName() + "> 匹配类型:" + datatype);
				page.putField("websiteId", websiteInfoConf.getId());
				page.putField("datatype", datatype);
				page.putField("url", url) ;
				///////////////////////////////////////////
				List<FieldConf> fieldConfs  = classifyInfoConf.getFieldConfs() ;
				for (FieldConf fieldConf:fieldConfs ) {
					String replaceRegex=fieldConf.getReplaceRegex() ;
					String replacement =fieldConf.getReplacement() ;
					String value =page.getHtml().xpath(fieldConf.getFieldRegex()).toString();
					if (StringUtils.isNotBlank( replaceRegex)) {
						value = value.replaceAll(replaceRegex, replacement);
					} 
					page.putField(fieldConf.getName()  ,value );
				}
				break;
			}
		} 
	}

	public Site getSite() {
		return site;
	}

	public String getId() {
		return id;
	}
	/**
	 * 刷新代理
	 */
	public void refreshProxy(){
		if (websiteInfoConf.isUseProxy()) {
			//代理保存地址:C:\Users\kuangmingai\AppData\Local\Temp\webmagic
			getSite().setHttpProxyPool(JobProxyManager.getProxyPool());
		}
	}
	 
	public void startAsync() {
		refreshProxy();
		Spider spider = MyCacheSpider.create(this);
		for (String entryUrl : websiteInfoConf.getEntryUrls()) {
			spider.addUrl(entryUrl);
		}
		spider.thread(websiteInfoConf.getThread()) ;
		spider.setDownloader(downloader);
		spider.setScheduler(scheduler) ;
		spider.addPipeline(basePipeline).runAsync();
	}

	public static void main(String[] args) {
		String id = "zhilian";
		new BasePageProcesser(id).startAsync();
	}
}