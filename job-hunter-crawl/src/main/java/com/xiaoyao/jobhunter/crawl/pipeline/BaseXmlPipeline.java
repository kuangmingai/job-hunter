package com.xiaoyao.jobhunter.crawl.pipeline;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyao.jobhunter.commons.constant.X;
import com.xiaoyao.jobhunter.crawl.util.Map2Object;
import com.xiaoyao.jobhunter.model.CompanyInfo;
import com.xiaoyao.jobhunter.model.JobDetail;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
/**
 * 数据解析
 * 
 * @author 旷明爱
 * @date 2016年8月13日 下午1:29:39
 * @email mingai.kuang@mljr.com
 */
public class BaseXmlPipeline    extends  BasePipeline   {

	static Logger logger = LoggerFactory.getLogger(BaseXmlPipeline.class);

	@Override
	public void process(ResultItems resultItems, Task metaInfo) {
		Map<String, Object > fieldsMap = (Map<String, Object>) resultItems.getAll() ;
		String url =resultItems.getRequest().getUrl();
//		String site = metaInfo.getSite().getDomain() ;
		logger.info(url+"解析:"+fieldsMap );
		String datatype = (String) fieldsMap.get("datatype")  ;
		
		if (X.DATATYPE_COMPANY.equals(datatype)) {
			CompanyInfo companyInfo = Map2Object.convertCompanyInfo(fieldsMap);
			logger.info("companyInfo:"+companyInfo);
			saveCompanyInfo(companyInfo);
		}else if (X.DATATYPE_JOBDETAIL.equals(datatype)) {
			JobDetail  jobDetail =Map2Object.convertJobDetail(fieldsMap);
			logger.info("jobdetail:"+jobDetail);
			saveJobDetail(jobDetail);
		}  
	}

}
