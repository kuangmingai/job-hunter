package com.xiaoyao.jobhunter.crawl.download;

import java.util.List;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.proxy.ProxyPool;

public class MySite  extends Site{

    private ProxyPool httpProxyPool;
	@Override
	public Site setHttpProxyPool(List<String[]> httpProxyList) {
        this.httpProxyPool=new ProxyPool(httpProxyList,false);
        return this;
	}
	
	
}
