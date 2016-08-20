package com.xiaoyao.jobhunter.crawl.annotation;

import java.util.List;

import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl("http://my.oschina.net/flashsword/blog/\\d+")
public class OschinaBlogDTO {

	@ExtractBy("//div[@class='blog-heading']/div[@class='title']/text()")
	private String title;

	@ExtractBy(value = "div.BlogContent", type = ExtractBy.Type.Css)
	private String content;

	@ExtractBy(value = "//div[@class='BlogTags']/a/text()", multi = true)
	private List<String> tags;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "OschinaBlogDTO [title=" + title + ", content=" + content + ", tags=" + tags + "]";
	}
	
	
	
	

}