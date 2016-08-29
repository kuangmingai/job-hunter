package com.xiaoyao.jobhunter.crawl.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyao.jobhunter.commons.util.FileUtil;

/**
 * 站点配置
 * 
 * @author 旷明爱
 * @date 2016年8月13日 上午12:25:06
 * @email mingai.kuang@mljr.com
 */
public class ConfigureUtil {
static Logger logger =LoggerFactory.getLogger(ConfigureUtil.class) ;
	public static final String FILE_CONF = "jd-conf.xml";
	private static Map<String,WebsiteInfoConf> websiteInfoConfMap=new HashMap<String,WebsiteInfoConf>();
	
	/**
	 * 根据ID获取配置.
	 * @param id
	 * @return
	 * @throws Exception 没有配置说明有问题.
	 */
	public static WebsiteInfoConf getWebsiteInfoConfById(String id) throws Exception{
		if (websiteInfoConfMap.isEmpty()) {
			synchronized (FILE_CONF) {
				if (websiteInfoConfMap.isEmpty()) {
					readConfigure();
				} 
			}
		}
		return websiteInfoConfMap.get(id) ;
	}

	@SuppressWarnings("unchecked")
	public static void readConfigure() throws Exception {
		String text = FileUtil.readFile(FILE_CONF);
		logger.debug("configure file :"+text);
		Document document = DocumentHelper.parseText(text);
		Element jdsRoot = document.getRootElement();
		List<Element> websiteEles = jdsRoot.elements("website");
		for (Element websiteEle : websiteEles) { 
			String id = websiteEle.attributeValue("id");
			String description = websiteEle.elementTextTrim("description");
			String name = websiteEle.elementTextTrim("name");
			String threadStr = websiteEle.elementTextTrim("thread");
			int thread=1 ;
			try {
				thread = Integer.parseInt(threadStr);
			} catch (Exception e) {
//				e.printStackTrace();
				logger.warn("线程配置有误!");
			}
			
			String domain = websiteEle.elementTextTrim("domain");

			List<Node> helpUrlNodeList = websiteEle.element("helpUrls").selectNodes("helpUrl");
			List<String> helpUrls = new ArrayList<String>();
			if (CollectionUtils.isNotEmpty(helpUrlNodeList)) {
				for (Node node : helpUrlNodeList) {
					helpUrls.add(node.getText().trim());
				}
			}

			// 不能为空
			List<Node> entryUrlNodeList = websiteEle.element("entryUrls").selectNodes("entryUrl");
			List<String> entryUrls = new ArrayList<String>();
			for (Node node : entryUrlNodeList) {
				entryUrls.add(node.getText().trim() );
			}

			// 不能为空
			List<ClassifyInfoConf> classifyInfoConfs = new ArrayList<ClassifyInfoConf>();
			List<Node> classifyNodeList = websiteEle.element("classifys").selectNodes("classify");
			int classifySize = classifyNodeList.size() ;
			for (int i = 0; i < classifySize ; i++) {
				Element classifyEle = (Element) classifyNodeList.get(i) ;
				
				String datatype = classifyEle.attributeValue("datatype") ;
				String urlRegex = classifyEle.elementTextTrim("urlRegex") ;
				String className = classifyEle.elementTextTrim("className") ;
				
				List<Node> fieldNodeList = classifyEle.elements("field");
				int fieldSize = fieldNodeList.size() ;
				List<FieldConf> fieldConfs = new ArrayList<FieldConf>();
				for (int j = 0; j < fieldSize  ; j++) {
					Element fieldEle = (Element) fieldNodeList.get(j) ;
					String fieldName =  fieldEle.attributeValue("name") ;
					String replaceRegex =  fieldEle.attributeValue("replaceRegex") ;
					String replacement =  fieldEle.attributeValue("replacement") ;
					String fieldRegex = fieldEle.getTextTrim() ;
					
					FieldConf fieldConf=new FieldConf(fieldName, fieldRegex, replaceRegex, replacement);
					fieldConfs.add(fieldConf);
				}  
				
				ClassifyInfoConf classifyInfoConf = new ClassifyInfoConf();
				classifyInfoConf.setDatatype(datatype);
				classifyInfoConf.setFieldConfs(fieldConfs);
				classifyInfoConf.setUrlRegex(urlRegex);
				classifyInfoConf.setClassName(className);
				classifyInfoConfs.add(classifyInfoConf) ; 
			}  


			WebsiteInfoConf websiteInfoConf = new WebsiteInfoConf();
			websiteInfoConf.setId(id);
			websiteInfoConf.setName(name);
			websiteInfoConf.setThread(thread);
			websiteInfoConf.setDomain(domain);
			websiteInfoConf.setDescription(description);
			websiteInfoConf.setEntryUrls(entryUrls);
			websiteInfoConf.setHelpUrls(helpUrls);
			websiteInfoConf.setClassifyInfoConfs(classifyInfoConfs);
			websiteInfoConfMap.put(id, websiteInfoConf) ;			
		}

	}
	
	public static void main(String[] args) throws Exception {
		String id = "zhilian" ;
		ConfigureUtil.getWebsiteInfoConfById(id) ;
	}

}
