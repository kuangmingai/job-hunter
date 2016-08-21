package com.xiaoyao.jobhunter.crawl.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;

import com.xiaoyao.jobhunter.commons.constant.X;
import com.xiaoyao.jobhunter.crawl.conf.WebsiteInfoConf;
import com.xiaoyao.jobhunter.crawl.pipeline.BaseJsonPipeline;
import com.xiaoyao.jobhunter.crawl.pipeline.BasePipeline;
import com.xiaoyao.jobhunter.crawl.util.LagouParser;
import com.xiaoyao.jobhunter.model.CompanyInfo;
import com.xiaoyao.jobhunter.model.JobDetail;

import us.codecraft.webmagic.Page;

/**
 * 拉勾网处理. <BR>
 * 封IP :http://forbidden.lagou.com/forbidden/fb.html?ip=112.93.112.146
 * 
 * @author 旷明爱
 * @date 2016年8月15日 上午12:07:13
 * @email mingai.kuang@mljr.com
 */

public class LagouPageProcesser extends BasePageProcesser {

	public static final String id = "lagou";
	public static final int PAGE_SIZE = 15;// 拉钩的分页
	public static final int PAGE_MAX = 30;// 拉钩最大页码 30
	public static final String SORTTYPE = "new";// new:最新排序, default:默认

	public static final String KEY_KEYWORD = "kd=";// 关键词
	public static final String KEY_CITY = "&city=";// 城市key
	public static final String KEY_HANGYE = "&hy=";// 行业key
	public static final String KEY_RONGZI = "&jd=";// 融资分类key
	public static final String KEY_GZJY = "&gj=";// 工作经验key
	public static final String KEY_XUELI = "&xl=";// 学历key
	public static final String KEY_PAGE = "&pn=";// 分页key

	public static final List<String> hangyeList = new ArrayList<>();
	public static final List<String> rongziList = new ArrayList<>();
	public static final List<String> gzjyList = new ArrayList<>();
	public static final List<String> xueliList = new ArrayList<>();
	static {
		hangyeList.add("移动互联网");
		hangyeList.add("电子商务");
		hangyeList.add("金融");
		hangyeList.add("企业服务");
		hangyeList.add("教育");
		hangyeList.add("文化娱乐");
		hangyeList.add("游戏");
		hangyeList.add("O2O");
		hangyeList.add("硬件");
		hangyeList.add("社交网络");
		hangyeList.add("旅游");
		hangyeList.add("医疗健康");
		hangyeList.add("生活服务");
		hangyeList.add("信息安全");
		hangyeList.add("数据服务");
		hangyeList.add("广告营销");
		hangyeList.add("分类信息");
		hangyeList.add("招聘");
		hangyeList.add("其他");

		//
		rongziList.add("未融资");
		rongziList.add("天使轮");
		rongziList.add("A轮");
		rongziList.add("B轮");
		rongziList.add("C轮");
		rongziList.add("D轮");
		rongziList.add("上市公司");
		rongziList.add("不需要融资");
		 
		
		gzjyList.add("应届毕业生") ;
		gzjyList.add("3年及以下") ;
		gzjyList.add("3-5年") ;
		gzjyList.add("5-10年") ;
		gzjyList.add("10年以上") ;
		gzjyList.add("不要求") ;
		
		xueliList.add("大专") ;
		xueliList.add("本科") ;
		xueliList.add("硕士") ;
		xueliList.add("博士") ;
		xueliList.add("不要求") ; 

	}

	public static final String LG_TOTALCOUNT_JSONPATH = "$.content.positionResult.totalCount";

	BasePipeline  basePipeline = new BaseJsonPipeline(); // 解析器
	
	static Set<String> hotCityList = new HashSet<>();
	static Set<String> cityList = new HashSet<>();
	static Set<String> keywordList = new HashSet<>();
	static String hotCityRegex = "<a rel=\"nofollow\" href=\"javascript:;\">([^\\{]+?)</a>";
	static String cityRegex = "\"name\":\"(.+?)\"";
	static String keywordRegex = "<a href=\"//www.lagou.com/zhaopin/.+?/\" data-lg-tj-id.+?class[^>]*?>([^>]+?)</a>";
	static Pattern hotCityPattern = Pattern.compile(hotCityRegex);
	static Pattern cityPattern = Pattern.compile(cityRegex);
	static Pattern keywordPattern = Pattern.compile(keywordRegex);

	public LagouPageProcesser() {
		super(id);
	}

	/**
	 * 解析 拉钩 的数据 JSON
	 * 
	 * @param json
	 * @param url
	 * @param websiteInfoConf
	 */
	private void parseLagouJson(String url, Page page, String json, WebsiteInfoConf websiteInfoConf) {
		if (json.trim().indexOf("<!DOCTYPE") > -1) {
			logger.warn("格式有问题,可能封IP了:" + json.contains("您已被封禁"));
			return;
		}
		// if ... 各类网页的解析规则.
		page.putField("url", url);
		page.putField("websiteId", websiteInfoConf.getId());
		page.putField("datatype", X.DATATYPE_COMPANY_JOBDETAIL);

		List<JobDetail> jobDetails = LagouParser.parseJobDetail(url, websiteInfoConf, page);
		List<CompanyInfo> companyInfos = LagouParser.parseCompanyInfo(url, websiteInfoConf, page);

		page.putField(X.KEY_jobdetail_list, jobDetails);
		page.putField(X.KEY_company_list, companyInfos); 
	}

	@Override
	public void process(Page page) {
		String url = page.getRequest().getUrl();
		logger.info(" 当前网页url ----> " + url);
		String rawText = page.getRawText();

		List<String> links = new ArrayList<String>();
		if (url.equals("http://www.lagou.com/jobs/allCity.html")) { // 热点城市
			Matcher cityMatcher = hotCityPattern.matcher(rawText);
			while (cityMatcher.find()) {
				hotCityList.add(cityMatcher.group(1));
			}
			hotCityList.remove("全国");
			logger.info("热门城市:" + hotCityList);
		} else if ("http://www.lagou.com".equals(url)) { // 解析所有关键词 //<dd></dd>
			Matcher keywordMatcher = keywordPattern.matcher(rawText);
			while (keywordMatcher.find()) {
				keywordList.add(keywordMatcher.group(1));
			}
			logger.info("关键词:" + keywordList);
		} else if ("http://www.lagou.com/lbs/getAllCitySearchLabels.json".equals(url)) { // 一般城市
			Matcher cityMatcher = cityPattern.matcher(rawText);
			while (cityMatcher.find()) {
				cityList.add(cityMatcher.group(1));
			}
			logger.info("一般城市:" + cityList);
		}
		/**
		 * 
		 * <entryUrl>http://www.lagou.com/jobs/allCity.html</entryUrl>
		 * <entryUrl>http://www.lagou.com/lbs/getAllCitySearchLabels.json
		 * </entryUrl> <entryUrl>http://www.lagou.com</entryUrl>
		 * 
		 */
		// 城市和关键词配对
		// http://www.lagou.com/jobs/positionAjax.json?kd=电子商务&px=new&needAddtionalResult=false&pn=100
		if (!isInitOver && CollectionUtils.isNotEmpty(hotCityList) && CollectionUtils.isNotEmpty(cityList)
				&& CollectionUtils.isNotEmpty(keywordList)) {
			isInitOver = true;
			cityList.removeAll(hotCityList);
		}
		if (isInitOver) {
			// 第一次,第一页,分页
			if (!url.contains("positionAjax")) { // 第一次 //网页
				for (String keyword : keywordList) {
					for (String city : hotCityList) { // 热门
						String ajaxUrl = "http://www.lagou.com/jobs/positionAjax.json?kd=" + keyword + "&city=" + city + "&px=" + SORTTYPE
								+ "&needAddtionalResult=false";
						links.add(ajaxUrl);
					}
				}
				for (String city : cityList) {// 一般城市,不匹配关键词
					String ajaxUrl = "http://www.lagou.com/jobs/positionAjax.json?city=" + city + "&px=" + SORTTYPE
							+ "&needAddtionalResult=false";
					links.add(ajaxUrl);
				}
			} else{
				// 算出页码 //关键词,城市,行业,融资
				String totalCountStr = page.getJson().jsonPath(LG_TOTALCOUNT_JSONPATH).toString();
				int totalCount = 1;
				try {
					totalCount = Integer.parseInt(totalCountStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
				int pageCount = (totalCount - 1) / PAGE_SIZE+1; // 页数
				logger.info("totalCount :" + totalCount + ",pageCount:" + pageCount);
				// java 北京 1.8万
				// php 北京 16622 ,移动互联网 9K
				// hy:行业 ;jd:不需要融资 
				if (!url.contains(KEY_PAGE)) { // 第一页 //json 
					if (pageCount > PAGE_MAX && !url.contains(KEY_KEYWORD )) { // 关键词
						for(String keyword:keywordList){
							String ajaxUrl =url+ KEY_KEYWORD  + keyword;
							links.add(ajaxUrl); 
						} 	
					}else  if (pageCount > PAGE_MAX && !url.contains(KEY_CITY )) { // 城市
						for (String city : hotCityList) { // 热门
							String ajaxUrl =url+ KEY_CITY  + city ;
							links.add(ajaxUrl);
						} 
						for (String city : cityList) {// 一般城市,不匹配关键词
							String ajaxUrl =url+ KEY_CITY  + city ;
							links.add(ajaxUrl);
						}						
					}else  if (pageCount > PAGE_MAX && !url.contains(KEY_HANGYE  )) { // 行业
						for(String str:hangyeList){
							String ajaxUrl =url+ KEY_HANGYE  + str;
							links.add(ajaxUrl); 
						} 				
					}else  if (pageCount > PAGE_MAX && !url.contains(KEY_RONGZI )) { // 融资
						for(String str:rongziList){
							String ajaxUrl =url+ KEY_RONGZI  + str;
							links.add(ajaxUrl); 
						} 				
					}else  if (pageCount > PAGE_MAX && !url.contains(KEY_GZJY )) { // 工作经验
						for(String str:gzjyList){
							String ajaxUrl =url+ KEY_GZJY  + str;
							links.add(ajaxUrl); 
						} 				
					}else  if (pageCount > PAGE_MAX && !url.contains(KEY_XUELI )) { // 学历
						for(String str:xueliList){
							String ajaxUrl =url+ KEY_XUELI  + str;
							links.add(ajaxUrl); 
						} 				
					}else{// 分页
						for (int pageNow = 1; pageNow <= pageCount && pageNow <= PAGE_MAX; pageNow++) {
							String ajaxUrl = url + KEY_PAGE + pageNow;
							links.add(ajaxUrl);
						} 
					} 
					// gj=应届毕业生&xl=本科
					parseLagouJson(url, page, rawText, websiteInfoConf); // 解析
				} else { // 后面的页
					parseLagouJson(url, page, rawText, websiteInfoConf); // 解析
				}
			}
		} else { // 后面的页//有暂停的情况
			parseLagouJson(url, page, rawText, websiteInfoConf); // 解析
		}
		if (CollectionUtils.isNotEmpty(testLinsk)) {
			page.addTargetRequests(testLinsk);
		} else {
			page.addTargetRequests(links);
		}
	}

	boolean isInitOver = false;// 初始信息是否完成

	public static void main(String[] args) {
		LagouPageProcesser pageProcesser = new LagouPageProcesser();
		pageProcesser.websiteInfoConf.setUseProxy(true);
		// List<String> testLinsk =new ArrayList<>() ;
		// testLinsk.add("http://www.lagou.com/jobs/positionAjax.json?px=new&city=常州&needAddtionalResult=false")
		// ;
		// // testLinsk.add("http://www.lagou.com/gongsi/749.html") ;
		// pageProcesser.setTestLinks(testLinsk);
		// pageProcesser.setHelpUrls(null);
		pageProcesser.startAsync();
	}
}