package com.xiaoyao.jobhunter.model;

import java.io.Serializable;
import java.util.Date;

public class CompanyInfo implements Serializable {

	private static final long serialVersionUID = 3919723756045583357L;

	private long id;
	private String websiteId;// 站点类型
	private String url;// 链接//json类型的可能不准确.
	private String priId;// 站点主键ID

	private String companyName; // 公司名
	private String description; // 介绍 //有的可能不单独抓
	private String companyNature; // 性质
	private String companySize; // 规模
	private String website; // 网站
	private String industry; // 行业
	private String address; // 地址

	private Date createTime = new Date();

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public long getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPriId() {
		return priId;
	}

	public void setPriId(String priId) {
		this.priId = priId;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(String websiteId) {
		this.websiteId = websiteId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCompanySize() {
		return companySize;
	}

	public void setCompanySize(String companySize) {
		this.companySize = companySize;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCompanyNature() {
		return companyNature;
	}

	public void setCompanyNature(String companyNature) {
		this.companyNature = companyNature;
	}

	@Override
	public String toString() {
		return "CompanyInfo [websiteId=" + websiteId + ", url=" + url + ", priId=" + priId + ", companyName=" + companyName
				+ ", description=" + description + ", companyNature=" + companyNature + ", companySize=" + companySize + ", website="
				+ website + ", industry=" + industry + ", address=" + address + ", createTime=" + createTime + "]";
	}

}
