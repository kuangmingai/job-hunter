package com.xiaoyao.jobhunter.mongo.dao;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDbUtil {

	public static final String DBNAME = "test";
	public static final String SERVERIP = "localhost";
	public static final int SERVERPORT = 27017;
 
	static Mongo mongo = null;
	static {
		try {
			mongo = new Mongo(SERVERIP, SERVERPORT);
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static DB db = mongo.getDB(DBNAME);

	public static DB getDb() {
		return db;
	}

	public static DBCollection getCollection(String collectionName) {
		return db.getCollection(collectionName);
	}
}
