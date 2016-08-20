package com.xiaoyao.jobhunter.model;

import java.util.List;

public class OschinaBlog {
	private String title;
	private String content;
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
		return "OschinaBlog [title=" + title + ", content=" + content + "]";
	}

}