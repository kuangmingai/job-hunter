package com.xiaoyao.jobhunter.crawl.pipeline;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObject;
import com.xiaoyao.jobhunter.commons.constant.X;
import com.xiaoyao.jobhunter.model.CompanyInfo;
import com.xiaoyao.jobhunter.model.JobDetail;
import com.xiaoyao.jobhunter.mongo.dao.BaseDao;
import com.xiaoyao.jobhunter.mongo.pojo.MyBasicObject;

import net.sf.json.JSONObject;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Json 数据解析
 * 
 * @author 旷明爱
 * @date 2016年8月13日 下午1:29:39
 * @email mingai.kuang@mljr.com
 */
public class BaseJsonPipeline extends  BasePipeline   {

	static Logger logger = LoggerFactory.getLogger(BaseJsonPipeline.class);

	@Override
	public void process(ResultItems resultItems, Task metaInfo) {
		Map<String, Object> fieldsMap = (Map<String, Object>) resultItems.getAll();
		String url = resultItems.getRequest().getUrl();
		// String site = metaInfo.getSite().getDomain() ;
		logger.info("解析" +url+":" +  fieldsMap);
		String datatype = (String) fieldsMap.get(X.KEY_datatype);

		if (X.DATATYPE_COMPANY.equals(datatype)) {
		} else if (X.DATATYPE_JOBDETAIL.equals(datatype)) {
		} else if (X.DATATYPE_COMPANY_JOBDETAIL.equals(datatype)) {
			List<JobDetail> jobDetails = (List<JobDetail>) fieldsMap.get(X.KEY_jobdetail_list);
			List<CompanyInfo> companyInfos = (List<CompanyInfo>) fieldsMap.get(X.KEY_company_list);
			logger.info("jobDetails:" + jobDetails);
			logger.info("companyInfos:" + companyInfos);
			saveCompanyInfoList(companyInfos);
			saveJobDetailList(jobDetails);
		}
	}
 
}
