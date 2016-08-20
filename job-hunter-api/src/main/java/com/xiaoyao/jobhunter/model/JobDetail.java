package com.xiaoyao.jobhunter.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class JobDetail implements Serializable {

	private static final long serialVersionUID = 3919723756045583357L;

	private long id;
	private String url;// 链接
	private String websiteId;// 站点类型
	private String priId;// 站点主键ID
	private String title; // 标题
	private String company; // 公司名
	private String city;// 城市
	private String salary;// 薪资
	private String publishTime;// 发布时间
	private String address;// 工作地点
	private String workYears;// 工作经验
	private String education;// 最低学历
	private String peoples;// 招聘人数
	private String jobType;// 职位类别
	private String jobDescribe;// 职位描述
	private String jobRequire;;// 职位要求//暂时不需要
	private String tags; // 标签
	private String keywords; // 关键词

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

	public void setId(long id) {
		this.id = id;
	}

	public String getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(String websiteId) {
		this.websiteId = websiteId;
	}

	public String getUrl() {
		return url;
	}

	public String getPriId() {
		return priId;
	}

	public void setPriId(String priId) {
		this.priId = priId;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWorkYears() {
		return workYears;
	}

	public void setWorkYears(String workYears) {
		this.workYears = workYears;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getPeoples() {
		return peoples;
	}

	public void setPeoples(String peoples) {
		this.peoples = peoples;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobDescribe() {
		return jobDescribe;
	}

	public void setJobDescribe(String jobDescribe) {
		this.jobDescribe = jobDescribe;
	}

	public String getJobRequire() {
		return jobRequire;
	}

	public void setJobRequire(String jobRequire) {
		this.jobRequire = jobRequire;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "JobDetail [ websiteId=" + websiteId + ", title=" + title + ", company=" + company + "]";
	}

}
