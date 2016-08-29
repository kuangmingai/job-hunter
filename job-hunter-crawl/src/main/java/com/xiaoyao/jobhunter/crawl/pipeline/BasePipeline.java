package com.xiaoyao.jobhunter.crawl.pipeline;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.xiaoyao.jobhunter.model.CompanyInfo;
import com.xiaoyao.jobhunter.model.JobDetail;
import com.xiaoyao.jobhunter.mongo.dao.BaseDao;
import com.xiaoyao.jobhunter.mongo.pojo.MyBasicObject;

/**
 *  数据解析 保存基类
 * 
 * @author 旷明爱
 * @date 2016年8月13日 下午1:29:39
 * @email mingai.kuang@mljr.com
 */
public class BasePipeline implements Pipeline {

	static Logger logger = LoggerFactory.getLogger(BasePipeline.class);

	public void process(ResultItems resultItems, Task metaInfo) {
	}

	BaseDao jobDetailDao = new BaseDao("JobDetail");
	BaseDao companyInfoDao = new BaseDao("CompanyInfo");

	/**
	 * 如果存在就更新
	 * @param object
	 */
	public void saveJobDetail(JobDetail object) {
		JSONObject jsonObject = JSONObject.fromObject(object);
		DBObject dbObject = MyBasicObject.fromJson(jsonObject);
		
		DBObject existQueryObj =new BasicDBObject();
		existQueryObj.put("websiteId", object.getWebsiteId()) ;
		existQueryObj.put("priId", object.getPriId() ) ; 

		List<BasicDBObject> results= jobDetailDao.get(existQueryObj) ;
		if (results!=null &&results.size()>0) {
			BasicDBObject existObj=results.get(0);
			dbObject.put("_id",existObj.getObjectId("_id")) ;
//			existObj.putAll(dbObject);
			jobDetailDao.updateById(existObj); 
		}else{
			jobDetailDao.insert(dbObject);
		}
	}

	public void saveJobDetailList(List<JobDetail> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		int size = list.size();
		for (int i = 0; i < size; i++) {
			saveJobDetail(list.get(i));
		}
	}

	/**
	 * 如果存在就更新
	 * @param object
	 */
	public void saveCompanyInfo(CompanyInfo object) {
		JSONObject jsonObject = JSONObject.fromObject(object);
		DBObject dbObject = MyBasicObject.fromJson(jsonObject);
		
		DBObject existQueryObj =new BasicDBObject();
		existQueryObj.put("websiteId", object.getWebsiteId()) ;
		existQueryObj.put("priId", object.getPriId() ) ;
		List<BasicDBObject> results= companyInfoDao.get(existQueryObj) ;
		if (results!=null &&results.size()>0) {
			BasicDBObject existObj=results.get(0);
			dbObject.put("_id",existObj.getObjectId("_id")) ;
//			existObj.putAll(dbObject);
			companyInfoDao.updateById(existObj); 
		}else{
			companyInfoDao.insert(dbObject);
		}
		
	}

	public void saveCompanyInfoList(List<CompanyInfo> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		int size = list.size();
		for (int i = 0; i < size; i++) {
			saveCompanyInfo(list.get(i));
		}
	}

}
