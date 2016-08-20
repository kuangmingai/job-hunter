package com.xiaoyao.jobhunter.crawl.pipeline;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class Job51Pipeline implements Pipeline {

	static Logger logger = LoggerFactory.getLogger(Job51Pipeline.class);

	@Override
	public void process(ResultItems resultItems, Task metaInfo) {
		Map<String, Object > fieldsMap = (Map<String, Object>) resultItems.getAll() ;
		String url =resultItems.getRequest().getUrl();
		String site = metaInfo.getSite().getDomain() ;
		logger.info("站点地址:"+site+","+url );
		logger.info("解析结果:"+fieldsMap );
	}

}
