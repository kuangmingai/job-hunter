package com.xiaoyao.jobhunter.crawl.util;

import java.util.Map;

import com.xiaoyao.jobhunter.model.CompanyInfo;
import com.xiaoyao.jobhunter.model.JobDetail;

/**
 * 将Xpath 解析出来的数据 映射到相应的对象里面
 * 
 * @author 旷明爱
 * @date 2016年8月13日 上午1:27:28
 * @email mingai.kuang@mljr.com
 */
public class Map2Object {
 
	
	public static String parseDefaultString(Object obj){
		String str="";
		if(obj!=null){
			str=obj+"";
		} 
		return str;
	}
	
	public static JobDetail convertJobDetail(Map<String, Object> map ) {
		/**
		 * 
	private String priId;// 站点主键ID
	private String title; // 标题
	private String company; // 公司名
	private String city;// 城市
	private String salary;// 薪资
	private String publishTime;// 发布时间
	private String address;// 工作地点
	private String workYears;// 工作经验
	private String education;// 最低学历
	private String peoples;// 招聘人数
	private String jobType;// 职位类别
	private String jobDescribe;// 职位描述
	private String jobRequire;;// 职位要求//暂时不需要
	private String tags; // 标签
	private String keywords; // 关键词
		 */
		JobDetail object = new JobDetail();
		object.setWebsiteId(parseDefaultString( map.get("websiteId") ) );
		object.setUrl(parseDefaultString( map.get("url") ) );
		object.setCompany(parseDefaultString(map.get("company") ));
		object.setTitle(parseDefaultString( map.get("title") ) ); 		
		
		object.setPriId(parseDefaultString( map.get("priId") ) ); 		
		object.setCity(parseDefaultString( map.get("city") ) ); 		
		object.setSalary(parseDefaultString( map.get("salary") ) ); 		
		object.setPublishTime(parseDefaultString( map.get("publishTime") ) ); 		
		object.setAddress(parseDefaultString( map.get("address") ) ); 		
		object.setWorkYears(parseDefaultString( map.get("workYears") ) ); 		
		object.setEducation(parseDefaultString( map.get("education") ) ); 		
		object.setPeoples(parseDefaultString( map.get("peoples") ) ); 		
		object.setJobType(parseDefaultString( map.get("jobType") ) ); 		
		object.setJobDescribe(parseDefaultString( map.get("jobDescribe") ) ); 		
		object.setJobRequire(parseDefaultString( map.get("jobRequire") ) ); 		
		object.setTags(parseDefaultString( map.get("tags") ) ); 		
		object.setKeywords(parseDefaultString( map.get("keywords") ) ); 		
		
		return object;
	}

	public static CompanyInfo convertCompanyInfo(Map<String, Object> map) {
		/**
		 * 
	private String url;// 链接//json类型的可能不准确.
	private String priId;// 站点主键ID

	private String companyName; // 公司名
	private String description; // 介绍 //有的可能不单独抓
	private String companyNature; // 性质
	private String companySize; // 规模
	private String website; // 网站
	private String industry; // 行业
	private String address; // 地址
		 */
		CompanyInfo object = new CompanyInfo();
		object.setWebsiteId( parseDefaultString(map.get("websiteId")) );
		object.setUrl(parseDefaultString( map.get("url")) );
		object.setCompanyName(parseDefaultString( map.get("companyName")) );
		object.setDescription(parseDefaultString( map.get("description")) ); 
		object.setAddress(parseDefaultString( map.get("address") ) ); 		
		 
		object.setCompanyNature(parseDefaultString( map.get("companyNature") ) ); 		
		object.setCompanySize(parseDefaultString( map.get("companySize") ) ); 		
		object.setWebsite(parseDefaultString( map.get("website") ) ); 		
		object.setIndustry(parseDefaultString( map.get("industry") ) ); 		
		 
		return object;
	}
}
