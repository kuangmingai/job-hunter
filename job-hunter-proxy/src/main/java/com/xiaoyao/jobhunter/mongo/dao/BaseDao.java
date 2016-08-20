package com.xiaoyao.jobhunter.mongo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.xiaoyao.jobhunter.mongo.pojo.MyBasicObject;

/**
 * 基类
 * 
 * @author 旷明爱
 * 
 */
public class BaseDao {
	static Logger logger = LoggerFactory.getLogger(BaseDao.class);
	static String dbName = "test";
	protected String _collectionName = "";
	protected DBCollection dbCollection = null;

	public BaseDao(String collectionName) {
		setCollectionName(collectionName);
	}

	public void setCollectionName(String collectionName) {
		this._collectionName = collectionName;
		dbCollection = getDbCollection();
	}

	public DBCollection getDbCollection() {
		if (dbCollection == null) {
			dbCollection = MongoDbUtil.getCollection(_collectionName);
		}
		return dbCollection;
	}

	// //////////////////////////////////
	public void insert(MyBasicObject dbObject) {
		dbCollection.insert(dbObject.toBson());
	}
	public void insert(DBObject... dbObject) {
		dbCollection.insert(dbObject);
	}
 
	public void insert(List<?> list) {
		// dbCollection.insert(list);
		for (int i = 0; i < list.size(); i++) {
			MyBasicObject dbObject = (MyBasicObject) list.get(i);
			dbCollection.insert(dbObject.toBson());
		}
	}

	public void updateById(MyBasicObject object) {
		updateById(object.toBson());
	}

	public void updateById(List<MyBasicObject> objectList) {
		for (MyBasicObject object : objectList) {
			updateById(object);
		}
	}

	/**
	 * OK,
	 */
	public void updateById(DBObject object) {
		DBObject queryObject = new BasicDBObject();
		queryObject.put(MyBasicObject.ID_KEY, object.get(MyBasicObject.ID_KEY));
//		object.put("lineFeature", "<div style='font-weight: bold;'><span class='_FF0000'>❤<span class='_000000'>搭乘天合联盟成员<span class='_0000FF'>美国达美航空公司客机，全程免费托运行李，省去500元行李托运费用</span>；</span></span><span class='_FF0000'>❤</span><br><span class='_FF0000'>❤</span><span class='_000000'>畅游美国东西海岸名城：世界经济中心<span class='_FF0000'>纽约</span>、政治中心<span") ;
		dbCollection.update(queryObject, object ); 
	}

	// ////////////////////////////////////////////////
	public List<BasicDBObject> getAll() {
		DBCursor cur = dbCollection.find();
		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
		while (cur.hasNext()) {
			BasicDBObject dbObject = (BasicDBObject) cur.next();
			dbObjects.add(dbObject);
		}
		return dbObjects;
	}

	public DBObject groupBy(DBObject groupKey, DBObject query, DBObject initial, String reduce) {
		reduce = "function(doc, aggr){" + "    aggr.count += 1;" + "  }";
		//
		groupKey = new BasicDBObject("advisorCity", 1);
		//
		Map<String, Integer> advisorQueryMap = new HashMap<String, Integer>();
		advisorQueryMap.put("$gt", 0);
		query = new BasicDBObject("advisorCount", advisorQueryMap);
		//
		initial = new BasicDBObject("count", 0);

		DBObject result = dbCollection.group(groupKey, query, initial, reduce);
		logger.info(""+result);
		return result;
	}

	/**
	 * 聚集 3.0 驱动
	 * 
	 * @param pipeline
	 * @return
	 */
	public List<BasicDBObject> aggregate(List<DBObject> pipeline) {
		AggregationOutput result = dbCollection.aggregate( pipeline);
		Iterator<DBObject> iterator = result.results().iterator();

		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
		while (iterator.hasNext()) {
			dbObjects.add((BasicDBObject) iterator.next());
		}
		return dbObjects;
	}
	
	
//	/**
//	 * 聚集  
//	 * 
//	 * @param pipeline
//	 * @return
//	 */
//	public List<BasicDBObject> aggregate(DBObject query ,DBObject group,DBObject  project ,DBObject sort   ) {
//		AggregationOutput result = dbCollection.aggregate(query, group ,project);
//		Iterator<DBObject> iterator = result.results().iterator();
//
//		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
//		while (iterator.hasNext()) {
//			dbObjects.add((BasicDBObject) iterator.next());
//		}
//		return dbObjects;
//	}
//	 
	
//	
//	/**
//	 * 聚集 2.9 以下驱动//有问题
//	 * 
//	 * @param pipeline
//	 * @return
//	 */
//	public List<BasicDBObject> aggregate(DBObject fist,DBObject... pipeline) {
//		AggregationOutput result = dbCollection.aggregate(fist, pipeline);
//		Iterator<DBObject> iterator = result.results().iterator();
//
//		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
//		while (iterator.hasNext()) {
//			dbObjects.add((BasicDBObject) iterator.next());
//		}
//		return dbObjects;
//	}

	/**
	 * 
	 * @param query
	 *            查询条件
	 * @param columns
	 *            返回字段
	 * @param orderBy
	 *            排序
	 * @param pageSize
	 *            结果数
	 * @param pageNum
	 *            第几页,1开始
	 * @return
	 */
	public List<BasicDBObject> get(DBObject query, List<String> columns, DBObject orderBy, int pageSize, int pageNum) {

		DBCursor cur = null;
		if (columns == null) {
			cur = dbCollection.find(query);
		} else {
			DBObject projection = new BasicDBObject();
			if (columns != null && columns.size() > 0) {
				// query.put(BasicObject.ID_KEY, "1"); // ID
				for (String column : columns) {
					projection.put(column, 1);
				}
			}
			cur = dbCollection.find(query, projection);
		}
		
		if (orderBy != null) { // 排序
			cur = cur.sort(orderBy);
		}
		if (pageSize > 0) { // 结果数
			cur = cur.limit(pageSize);
		}
		if (pageNum > 0) { // 第几页
			cur = cur.skip(pageSize * (pageNum - 1));
		}

		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
		while (cur.hasNext()) {
			BasicDBObject dbObject = (BasicDBObject) cur.next();
			dbObjects.add(dbObject);
		}
		return dbObjects;
	}

	/**
	 * 
	 * @param query
	 *            查询条件
	 * @param columns
	 *            返回字段
	 * @param orderBy
	 *            排序
	 * @return
	 */
	public List<BasicDBObject> get(DBObject query, List<String> columns, DBObject orderBy) {
		return get(query, columns, orderBy, 0, 1);
	}

	/**
	 * 
	 * @param query
	 *            查询条件 
	 * @return
	 */
	public List<BasicDBObject> get(DBObject query ) {
		return get(query, null, null);
	}
	/**
	 * 
	 * @param query
	 *            查询条件
	 * @param columns
	 *            返回字段
	 * @return
	 */
	public List<BasicDBObject> get(DBObject query, List<String> columns) {
		return get(query, columns, null);
	}

	/**
	 * 返回字段
	 * 
	 * @param columns
	 * @return
	 */
	public List<BasicDBObject> getAll(List<String> columns) {
		DBObject query = new BasicDBObject();
		DBObject projection = new BasicDBObject();
		// query.put(BasicObject.ID_KEY, "1"); // ID
		for (String column : columns) {
			projection.put(column, 1);
		}
		DBCursor cur = dbCollection.find(query, projection);
		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
		while (cur.hasNext()) {
			BasicDBObject dbObject = (BasicDBObject) cur.next();
			dbObjects.add(dbObject);
		}
		return dbObjects;
	}

	Set<String> indexNameSet = new HashSet<String>();

	/**
	 * 获取所有索引名字.可以用命名空间作为前缀
	 * 
	 * @return
	 */
	public Set<String> getAllIndex() {
		if (indexNameSet.size() == 0) {
			List<DBObject> indexList = dbCollection.getIndexInfo();
			for (DBObject dbObject : indexList) {
				indexNameSet.add(dbObject.get("name") + "");
			}
		}
		return indexNameSet;
	}

	/**
	 * 是否存在索引
	 * 
	 * @param indexName
	 * @return
	 */
	public boolean existIndex(String indexName) {
		return getAllIndex().contains(indexName);
	}

	/**
	 * 创建索引,重复建索引会抛异常 <BR>
	 * db.CncnAdvisor.ensureIndex({"advisorId":1,"unique":true}); <BR>
	 * db.CncnAdvisor.ensureIndex({ "advisorId" : 1 }, { "name"
	 * :"AdvisorId_unique", "unique" : true, "dropDups" : true, "background"
	 * :true }); <BR>
	 * db.CncnAdvisor.dropIndex("索引名,name"); <BR>
	 * 
	 * @param keys
	 */
	public void addIndex(DBObject keys, DBObject options) {
		String indexName = options.get("name") + "";
		if (!existIndex(indexName)) {
			dbCollection.createIndex(keys, options);
			logger.info("索引" + _collectionName + ":" + indexName + " 创建成功!");
		} else {
			logger.info("索引" + _collectionName + ":" + indexName + " 已经存在!");
		}
	}

	public long getAllCount() {
		long count = dbCollection.count();
		return count;
	}

	public long getCount(DBObject query) {
		long count = dbCollection.count(query);
		return count;
	}

	public void deleteAll() {
		DBObject queryObject = new BasicDBObject();
		dbCollection.remove(queryObject);
	}

	public void deleteById(ObjectId _id) {
		DBObject queryObject = new BasicDBObject();
		queryObject.put(MyBasicObject.ID_KEY, _id);
		dbCollection.remove(queryObject);
	}

	public void delete(DBObject query) {
		dbCollection.remove(query);
	}

}
