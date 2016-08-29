package com.xiaoyao.jobhunter.crawl.spider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class MyCacheSpider extends Spider {

	public MyCacheSpider(PageProcessor pageProcessor) {
		super(pageProcessor);
	}
 
    protected void processRequest(Request request) {
        Page page = downloader.download(request, this);
        if (page == null) {
            sleep(site.getRetrySleepTime());
            onError(request);
            return;
        }
        // for cycle retry
        if (page.isNeedCycleRetry()) {
            extractAndAddRequests(page, true);
            sleep(site.getRetrySleepTime());
            return;
        }
        pageProcessor.process(page);
        extractAndAddRequests(page, spawnUrl);
        if (!page.getResultItems().isSkip()) {
            for (Pipeline pipeline : pipelines) {
                pipeline.process(page.getResultItems(), this);
            }
        }
        //for proxy status management
        request.putExtra(Request.STATUS_CODE, page.getStatusCode());
        sleep(site.getSleepTime());
    }

}
