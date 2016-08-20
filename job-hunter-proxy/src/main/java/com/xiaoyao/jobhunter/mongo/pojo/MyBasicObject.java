package com.xiaoyao.jobhunter.mongo.pojo;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import net.sf.json.JSONObject;

/**
 * 对象类基类
 * 
 * @author 旷明爱
 * 
 */
public class MyBasicObject{ 
	public static final String ID_KEY = "_id";
	public ObjectId _id = null;// 自定义_ID

	/**
	 * 转换成 mongoDb DBObject 对象.不包含集合类型,复杂类型,只包含本表字段
	 * 
	 * @return
	 */
	public DBObject toBson() {
		JSONObject jsonObject = JSONObject.fromObject(this);//ObjectId 类型需要转换
		DBObject dbObject = new BasicDBObject(jsonObject);
		dbObject.put(ID_KEY, _id ) ; 
		return dbObject; 
	}
	public static DBObject fromJson(JSONObject jsonObject){
		DBObject dbObject = new BasicDBObject(jsonObject);
//		dbObject.put(ID_KEY, "" ) ; 
		return dbObject; 
	}
	
	public void set_id(String _id) {
		this._id = new ObjectId(_id);
	}

	public Object get_id() {
		return _id;
	}
}
