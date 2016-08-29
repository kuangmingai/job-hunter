package com.xiaoyao.jobhunter.crawl.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.xiaoyao.jobhunter.crawl.conf.ClassifyInfoConf;
import com.xiaoyao.jobhunter.crawl.conf.FieldConf;
import com.xiaoyao.jobhunter.crawl.pipeline.BasePipeline;
import com.xiaoyao.jobhunter.crawl.pipeline.Job51Pipeline;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.Xpath2Selector;
/**
 * 
 * 
 * @author 旷明爱
 * @date 2016年8月17日 下午11:12:18
 * @email mingai.kuang@mljr.com
 */
public class Job51PageProcesser extends BasePageProcesser {
/**
 
http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea=010000%2C00&district=000000&funtype=0000&industrytype=01&issuedate=9&providesalary=99&keywordtype=2&curr_page=3&lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&lonlat=0%2C0&radius=-1&ord_field=0&list_type=0&fromType=14&dibiaoid=0&confirmdate=9
 
http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea=040000&funtype=0000&industrytype=01&keywordtype=2&lang=c&stype=2&postchannel=0000&fromType=1&confirmdate=9

城市+行业
jobarea=040000
industrytype=01  
curr_page=3

view-source:http://search.51job.com/jobsearch/advance_search.php?stype=2
id="kuai3"  城市
  <a href="http://search.51job.com/list/010000,000000,0000,00,1,99,%2B,0,1.html?lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=18&dibiaoid=0&address=&line=&specialarea=00&from=&welfare=" >北京</a>
 <a href="http://search.51job.com/list/020000,000000,0000,00,1,99,%2B,0,1.html?lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=18&dibiaoid=0&address=&line=&specialarea=00&from=&welfare=" >上海</a>

id="kuai2"  行业
  <td><a href="javascript:void(0);" onclick="zzSearch.getIndtypeUrl('01')">计算机软件</a></td>



 */
	public static final String id = "51job";
	public static Set<String> cityList = new HashSet<String>();
	public static Set<String> industryList = new HashSet<String>();
	public static Set<String> itList = new HashSet<String>();
	
	static{
		itList.add("01");
		itList.add("37");
		itList.add("38");
		itList.add("31");
		itList.add("39");
		itList.add("32");
		itList.add("02");
		itList.add("40");
		itList.add("35");
	}
	
	static String cityRegex = "<a href=\"http://search.51job.com/list/(\\d{6}),000000,0000,00,1,99,%2B,0,1.html\\?lang=";
	static String industryRegex = "<td><a href=\"javascript:void\\(0\\);\" onclick=\"zzSearch.getIndtypeUrl\\('(\\d{2,4})'\\)\">.{1,20}</a></td>";
	static Pattern cityPattern = Pattern.compile(cityRegex);
	static Pattern industryPattern = Pattern.compile(industryRegex);

	static String pageUrl="http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea=%s&industrytype=%s"
	//		+ "%%2C00&district=000000&funtype=0000&issuedate=9&providesalary=99&keywordtype=2"
	//		+ "&lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99"
	//		+ "&lonlat=0%%2C0&radius=-1&ord_field=0&list_type=0&fromType=14&dibiaoid=0&confirmdate=9"
			;	
	
	static String KEY_CITY="&jobarea=" ;
	static String KEY_INDUSTRY="&industrytype=" ;
	static String KEY_PAGE="&curr_page=" ;
		
	public void process(Page page) {
		String url = page.getRequest().getUrl();
//		logger.info(" 当前网页url ----> " + url);
		String rawText = page.getRawText();
//		System.out.println(rawText);

		List<String> links = new ArrayList<String>();
		if (url.equals("http://search.51job.com/jobsearch/advance_search.php?stype=2")) { //  城市,行业
			Matcher cityMatcher = cityPattern.matcher(rawText);
			while (cityMatcher.find()) {
				cityList.add(cityMatcher.group(1));
			}
			logger.info("城市:" + cityList);

			Matcher industryMatcher = industryPattern  .matcher(rawText);
			while (industryMatcher.find()) {
				industryList.add(industryMatcher.group(1));
			}
			industryList=itList; /// IT,互联网
			
			logger.info("行业:" + cityList);
			for(String city:cityList){
				for(String industry:industryList){
					String urlStr = String.format(pageUrl, city  ,industry,1);
					links.add(urlStr) ;
				}
			}
			
		}else if(url.contains("http://search.51job.com/jobsearch/search_result.php") && !url.contains(KEY_PAGE)){// 分页
			// 解析页码
			int pageCount=1;//</span><span class="dw_c_orange">1</span> / 163<a
			String pageCountRegex="共(\\d+)页";
			Pattern pageCountPattern =Pattern.compile(pageCountRegex);
			Matcher pageCountMatcher =pageCountPattern.matcher(rawText);
			if (pageCountMatcher.find()) {
				pageCount = Integer.parseInt(pageCountMatcher.group(1)) ;
			}
			for (int i = 2; i <= pageCount ; i++) {
				links.add(url+KEY_PAGE+i);
			}
			//解析列表
			links.addAll(  page.getHtml().links().regex(regexUrl).all() );
		}else if (url.contains("http://search.51job.com/jobsearch/search_result.php") ) {
			//解析列表
			links.addAll(  page.getHtml().links().regex(regexUrl).all() );
		}else{// 很可能是详情页面
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
						String replaceRegex = fieldConf.getReplaceRegex();
						String replacement = fieldConf.getReplacement();
						Xpath2Selector xpath2Selector=new Xpath2Selector(fieldConf.getFieldRegex());
 						Selectable selectable = page.getHtml().select(xpath2Selector);//.xpath(fieldConf.getFieldRegex());
//						logger.info(fieldConf.getFieldRegex());
						if (selectable != null) {
							String value = selectable.toString();
//							logger.info(value);
							if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(replaceRegex)) {
								value = value.replaceAll(replaceRegex, replacement);
							}
							page.putField(fieldConf.getName(), value);
						}
					}
					break;
				}
			}  
		}
		 
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
	}

	BasePipeline  basePipeline = new Job51Pipeline(); // 解析器
	public Job51PageProcesser() { 
		super(id);
	}

	public static void main(String[] args) {
		Job51PageProcesser job51PageProcesser = new Job51PageProcesser();
		job51PageProcesser.websiteInfoConf.setUseProxy( true);
//		List<String> testLinsk =new ArrayList<>() ;
//		testLinsk.add("http://jobs.51job.com/chongqing-spbq/78723441.html") ;
//		testLinsk.add("http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea=200500&industrytype=13") ;
//		job51PageProcesser.setTestLinks(testLinsk);
//		job51PageProcesser.setHelpUrls(job51PageProcesser.getHelpUrls());
		job51PageProcesser.startAsync();
	}
}