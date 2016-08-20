package com.xiaoyao.jobhunter.crawl.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZhilianPageProcesser extends BasePageProcesser {
	static Logger logger = LoggerFactory.getLogger(ZhilianPageProcesser.class);
	public static final String id = "zhilian";

	public ZhilianPageProcesser() {
		super(id);
	}

	public static void main(String[] args) {
		ZhilianPageProcesser zhilianPageProcesser =new ZhilianPageProcesser() ;
		zhilianPageProcesser.startAsync();
	}
}