package com.xiaoyao.jobhunter.crawl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyao.jobhunter.commons.constant.X;
import com.xiaoyao.jobhunter.crawl.conf.ClassifyInfoConf;
import com.xiaoyao.jobhunter.crawl.conf.WebsiteInfoConf;
import com.xiaoyao.jobhunter.model.CompanyInfo;
import com.xiaoyao.jobhunter.model.JobDetail;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Json;

public class LagouParser {
	static Logger logger = LoggerFactory.getLogger(LagouParser.class);

	public static String JP_RESULTS = "$.content.positionResult.result";
	public static String JP_RESULT_SIZE = "$.content.positionResult.resultSize";
	// job
	public static String JP_JOB_TITLE = "positionName";
	public static String JP_JOB_COMPANY = "companyFullName";
	

	public static String JP_JOB_ID = "positionId";
	public static String JP_JOB_CITY = "city";
	public static String JP_JOB_TAGS = "positionAdvantage";
	public static String JP_JOB_PUBLISHTIME = "createTime";
	public static String JP_JOB_SALARY = "salary";
	public static String JP_JOB_ADDRESS = "city";
	public static String JP_JOB_WORKYEAR = "workYear";
	public static String JP_JOB_EDUCATION = "education";  
	 
	
	// company
	public static String JP_COMPANY_NAME = "companyFullName";
	public static String JP_COMPANY_DESCRIPTION = "positionAdvantage";
	public static String JP_COMPANY_ID = "companyId";
	public static String JP_COMPANY_SIZE = "companySize";
	public static String JP_COMPANY_INDUSTRY   = "industryField";
	public static String JP_COMPANY_CITY = "city"; 
	public static String JP_COMPANY_NATURE = "financeStage";

	//// 一些缓存数据
	static ClassifyInfoConf jobDetailConf = null;
	static ClassifyInfoConf companyConf = null;
	static Map<Integer, String> jobIndexJsonPathMap=new HashMap<>() ;
	static Map<Integer, String> companyIndexJsonPathMap=new HashMap<>() ;
	
	

	public static List<JobDetail> parseJobDetail(String url, WebsiteInfoConf websiteInfoConf, Page page) {
		if (jobDetailConf == null) {
			List<ClassifyInfoConf> classifyInfoConfs = websiteInfoConf.getClassifyInfoConfs();
			for (ClassifyInfoConf classifyInfoConf : classifyInfoConfs) {
				if (X.DATATYPE_JOBDETAIL.equals(classifyInfoConf.getDatatype())) {
					jobDetailConf = classifyInfoConf;
					break;
				}
			}
		}

		Json json = page.getJson();
		int resultSize = Integer.parseInt(json.jsonPath(JP_RESULT_SIZE).toString());
		List<JobDetail> jobDetails = new ArrayList<>();
		for (int i = 0; i < resultSize; i++) {
			String title = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_JOB_TITLE).toString();
			String company = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_JOB_COMPANY).toString();

			String priId = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_JOB_ID  ).toString();
			String city = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_JOB_CITY).toString();
			String salary = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_JOB_SALARY).toString();
			String publishTime = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_JOB_PUBLISHTIME).toString();
			String address = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_JOB_ADDRESS).toString();
			String workYears = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_JOB_WORKYEAR).toString();
			String education = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_JOB_EDUCATION).toString();
			
//			String peoples;// 招聘人数
//			 String jobType;// 职位类别
//			 String jobDescribe;// 职位描述
//			 String jobRequire;;// 职位要求 
 
			JobDetail jobDetail = new JobDetail();
			jobDetail.setUrl(url);
			jobDetail.setWebsiteId(websiteInfoConf.getId());
			jobDetail.setTitle(title);
			jobDetail.setCompany(company);
			jobDetail.setPriId(priId);  
			jobDetail.setCity(city);  
			jobDetail.setSalary(salary);  
			jobDetail.setPublishTime(publishTime);  
			jobDetail.setAddress(address);   
			jobDetail.setWorkYears(workYears);
			jobDetail.setEducation(education);
			
			// logger.info("lagou:"+jobDetail);
			jobDetails.add(jobDetail);
		}
		return jobDetails;
	}

	public static List<CompanyInfo> parseCompanyInfo(String url, WebsiteInfoConf websiteInfoConf, Page page) {
		if (companyConf == null) {
			List<ClassifyInfoConf> classifyInfoConfs = websiteInfoConf.getClassifyInfoConfs();
			for (ClassifyInfoConf classifyInfoConf : classifyInfoConfs) {
				if (X.DATATYPE_COMPANY  .equals(classifyInfoConf.getDatatype())) {
					companyConf = classifyInfoConf;
					break;
				}
			}
		}  
		List<CompanyInfo> companyInfos = new ArrayList<>();

		Json json = page.getJson();
		int resultSize = Integer.parseInt(json.jsonPath(JP_RESULT_SIZE).toString());
		for (int i = 0; i < resultSize; i++) {
			String companyName = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_COMPANY_NAME).toString();
			String description = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_COMPANY_DESCRIPTION).toString();
			String priId = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_COMPANY_ID).toString();
			String companyNature = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_COMPANY_NATURE).toString();
			String industry = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_COMPANY_INDUSTRY).toString();
			String city = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_COMPANY_CITY  ).toString();
			String companySize = page.getJson().jsonPath(JP_RESULTS + "[" + i + "]." + JP_COMPANY_SIZE).toString();
			    
			CompanyInfo jobDetail = new CompanyInfo();
			jobDetail.setPriId(priId);
			jobDetail.setUrl(url);
			jobDetail.setWebsiteId(websiteInfoConf.getId());
			jobDetail.setCompanyName(companyName);
			jobDetail.setDescription(description);
			jobDetail.setPriId(priId);
			jobDetail.setCompanyNature(companyNature);
			jobDetail.setCompanySize(companySize);
			jobDetail.setAddress(city);
			jobDetail.setIndustry(industry);
			
			// logger.info("lagou:"+jobDetail);
			companyInfos.add(jobDetail);
		}
		return companyInfos;
	}
}
