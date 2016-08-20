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

	public static JobDetail convertJobDetail(Map<String, Object> map ) {
		JobDetail object = new JobDetail();
		object.setWebsiteId( map.get("websiteId")+"" );
		object.setUrl( map.get("url")+"" );
		object.setCompany(map.get("company")+"");
		object.setTitle( map.get("title")+"" );
		return object;
	}

	public static CompanyInfo convertCompanyInfo(Map<String, Object> map) {
		CompanyInfo object = new CompanyInfo();
		object.setWebsiteId( map.get("websiteId")+"" );
		object.setUrl( map.get("url")+"" );
		object.setCompanyName( map.get("companyName")+"" );
		object.setDescription( map.get("description")+"" ); 
		return object;
	}
}
