package com.xiaoyao.jobhunter.crawl.conf;

/**
 * 字段配置
 * 
 * @author 旷明爱
 * @date 2016年8月13日 下午1:16:15
 * @email mingai.kuang@mljr.com
 */
public class FieldConf {

	private String name;
	private String fieldRegex;
	private String replaceRegex;
	private String replacement;

	
	
	public FieldConf(String name, String fieldRegex, String replaceRegex, String replacement) {
		super();
		this.name = name;
		this.fieldRegex = fieldRegex;
		this.replaceRegex = replaceRegex;
		this.replacement = replacement;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFieldRegex() {
		return fieldRegex;
	}

	public void setFieldRegex(String fieldRegex) {
		this.fieldRegex = fieldRegex;
	}

	public String getReplaceRegex() {
		return replaceRegex;
	}

	public void setReplaceRegex(String replaceRegex) {
		this.replaceRegex = replaceRegex;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

}
