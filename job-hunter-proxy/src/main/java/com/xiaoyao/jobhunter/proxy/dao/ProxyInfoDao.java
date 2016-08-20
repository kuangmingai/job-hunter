package com.xiaoyao.jobhunter.proxy.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.xiaoyao.jobhunter.mongo.dao.BaseDao;

public class ProxyInfoDao extends BaseDao {

	public static final String collectionName = "ProxyInfo";

	public ProxyInfoDao() {
		super(collectionName);
	}

	public static void main(String[] args) {
		ProxyInfoDao proxyInfoDao = new ProxyInfoDao();
		DBObject keys = new BasicDBObject();
		keys.put("ip", 1);
		DBObject options = new BasicDBObject();
		options.put("name", "ip_unique_index");
		options.put("unique", true);
		options.put("dropDups", true);

		proxyInfoDao.addIndex(keys, options);
	}
}
