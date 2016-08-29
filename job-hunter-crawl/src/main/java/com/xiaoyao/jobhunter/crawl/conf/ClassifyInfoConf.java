package com.xiaoyao.jobhunter.crawl.conf;

import java.util.ArrayList;
import java.util.List;

/**
 * 每种信息类型的配置.
 * 
 * @author 旷明爱
 * @date 2016年8月12日 下午11:32:20
 * @email mingai.kuang@mljr.com
 */
public class ClassifyInfoConf {

	private String datatype;
	private String urlRegex;
	private String className;

	private List<FieldConf> fieldConfs;

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getUrlRegex() {
		return urlRegex;
	}

	public void setUrlRegex(String urlRegex) {
		this.urlRegex = urlRegex;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<FieldConf> getFieldConfs() {
		if (fieldConfs == null) {
			fieldConfs = new ArrayList<FieldConf>();
		}
		return fieldConfs;
	}

	public void setFieldConfs(List<FieldConf> fieldConfs) {
		this.fieldConfs = fieldConfs;
	}

}
