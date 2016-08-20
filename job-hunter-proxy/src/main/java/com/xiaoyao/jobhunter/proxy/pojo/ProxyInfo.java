package com.xiaoyao.jobhunter.proxy.pojo;

import java.util.Date;

import com.xiaoyao.jobhunter.mongo.pojo.MyBasicObject;

/**
 * 代理IP信息.
 * 
 * @author 旷明爱
 * 
 */
public class ProxyInfo extends MyBasicObject {

	private Long id;
	private String method = "";
	private String netOp = "";
	private String website = "";

	private String ip;
	private String port;
	private String verifyTime = "";// 最后验证时间. //可能没有
	private double response = 0;// 响应速度,秒//可能没有

	private String protocol = "HTTP";// HTTP,HTTPS
	private String location = "";
	private String anonymityType = "普通";// 普通,匿名,高匿名,透明

	private boolean enable = true;
	private String username;
	private String password;

	private Date createTime = new Date();

	public String getIp() {
		return ip;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getNetOp() {
		return netOp;
	}

	public void setNetOp(String netOp) {
		this.netOp = netOp;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(String verifyTime) {
		this.verifyTime = verifyTime;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAnonymityType() {
		return anonymityType;
	}

	public void setAnonymityType(String anonymityType) {
		this.anonymityType = anonymityType;
	}

	public double getResponse() {
		return response;
	}

	public void setResponse(double response) {
		this.response = response;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
