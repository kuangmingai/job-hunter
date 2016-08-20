package com.xiaoyao.jobhunter.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

/**
 * 文件操作类 统一 UTF-8编码<BR>
 * 都是以 classpath 为目录.
 * 
 * @author 旷明爱
 * @date Aug 1, 2012 11:55:02 AM
 */
public class FileUtil {
	// 源文件保存目录
	public static final String SOURCE_DIR = "D:/source/";
	// 图片保存目录
	public static final String PIC_DIR = "pic/";
	// 错误记录目录
	public static final String ERROR_DIR = "error/";

	public static final String BAICHENG_VISA_CITY = SOURCE_DIR + "baicheng/city/";
	public static final String BAICHENG_VISA_LIST = SOURCE_DIR + "baicheng/visalist/";
	public static final String BAICHENG_VISA_DETAIL = SOURCE_DIR + "baicheng/visadetail/";
	public static final String CNCN_GUWEN_LIST = SOURCE_DIR + "cncn/guwen/";
	public static final String CNCN_GUWEN_ANSWER = SOURCE_DIR + "cncn/answer/";
	public static final String CNCN_LINE_LIST = SOURCE_DIR + "cncn/linelist/";
	public static final String CNCN_LINE_DETAIL = SOURCE_DIR + "cncn/linedetail/";
	public static final String CNCN_LINE_CITY = SOURCE_DIR + "cncn/city/";
	public static final String PROXY_PAGE = SOURCE_DIR + "proxy/";
	public static final String TUNIU_LINE_CITY = SOURCE_DIR + "tuniu/city/";
	public static final String TUNIU_TOUR_LIST = SOURCE_DIR + "tuniu/tourlist/";
	public static final String TUNIU_TOUR_DETAIL = SOURCE_DIR + "tuniu/tourdetail/";
	public static final String TUNIU_TOUR_COMMENT = SOURCE_DIR + "tuniu/tourcomment/";
	public static final String TUNIU_TOUR_QUESTION = SOURCE_DIR + "tuniu/tourquestion/";
	
	
	
	private static String[] dirs = { SOURCE_DIR,TUNIU_TOUR_QUESTION,TUNIU_TOUR_COMMENT,   
		
		BAICHENG_VISA_CITY,  BAICHENG_VISA_LIST  ,BAICHENG_VISA_DETAIL,
		TUNIU_TOUR_LIST,TUNIU_TOUR_DETAIL ,    TUNIU_LINE_CITY,PROXY_PAGE,CNCN_LINE_DETAIL, CNCN_GUWEN_LIST, CNCN_GUWEN_ANSWER,CNCN_LINE_LIST, CNCN_LINE_CITY ,};
	private static String[] pathDirs = { ERROR_DIR }; // classpath
	private static String[] webPathDirs = { PIC_DIR };

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	private static String PATH = "";
	private static String WEBROOT = "";
	private static String CHARSET = "UTF-8";
	static {
		try {

			ClassLoader classLoader = FileUtil.class.getClassLoader();
			URL url = classLoader.getResource("/");// main 中获取的是 null
			if (url == null) {
				url = classLoader.getResource("");// web 中获取的是 tomcat/lib
			}
			PATH = url.toURI().getPath();

			File webFile = new File(PATH);
			WEBROOT = webFile.getParentFile().getParentFile().getAbsolutePath() + "/";

			for (int i = 0; i < dirs.length; i++) {
				File dir = new File(dirs[i]);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
			for (int i = 0; i < pathDirs.length; i++) {
				File dir = new File(PATH + pathDirs[i]);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
			for (int i = 0; i < webPathDirs.length; i++) {
				File dir = new File(WEBROOT + webPathDirs[i]);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
		} catch (URISyntaxException e) {
			logger.error("文件目录获取初始化失败!" + e.getMessage());
			throw new RuntimeException(e);
		}
		logger.info("路径:" + PATH);
		logger.info("WEB路径:" + WEBROOT);
	}

	public static void main(String[] args) throws Exception {
		// saveFile("aa.txt", "aa;", true) ;
		// saveFile("aa.txt", "aa;", true) ;

		String fileName = "C:\\Documents and Settings\\user";
		fileName = PATH;
		File file = new File(fileName);
		File[] files = file.listFiles();
		System.out.println(file.getAbsolutePath());
		System.out.println(files.length);
	}

	/**
	 * 获取根目录
	 * 
	 * @return
	 */
	public static String getPath() {
		return PATH;
	}

	/**
	 * 获取WEB目录
	 * 
	 * @return
	 */
	public static String getWebRoot() {
		return WEBROOT;
	}

	/**
	 * 保存图片 为 jpg 格式
	 * 
	 * @param bytes
	 * @throws Exception
	 */
	public static void savePic(byte[] bytes, String picName) {
		try {
			if (bytes == null) {
				logger.error("保存图片失败!没有下载到");
			} else {
				FileOutputStream fos = new FileOutputStream(FileUtil.WEBROOT + PIC_DIR + picName);
				fos.write(bytes);
				fos.close();
			}
		} catch (IOException e) {
			logger.error("保存图片失败!" + e.getMessage());
		}
	}

	/**
	 * 保存文件
	 * 
	 * @param fileName
	 * @param content
	 * @throws Exception
	 */
	public static void saveFile(String fileName, String content) {
		saveAbsolutlyFile(PATH + fileName, content);
	}

	/**
	 * 保存文件 绝对路径
	 * 
	 * @param filePath
	 * @param content
	 * @throws Exception
	 */
	public static void saveAbsolutlyFile(String filePath, String content) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(content.getBytes());
			fos.close();
		} catch (IOException e) {
			logger.error("保存文件错误!" + e.getMessage());
		}
	}

	/**
	 * 追加文件
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void appendFile(String fileName, String content) {
		try {
			FileWriter fileWriter = new FileWriter(PATH + fileName, true);
			fileWriter.append(content);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			logger.error(" 追加文件文件错误!" + e.getMessage());
		}
	}

	/**
	 * 读取文件 绝对路径
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public static String readAbsolutlyFile(String filePath) throws IOException {
		StringBuffer buffer = new StringBuffer(10000);
		FileInputStream fis = null;
		BufferedReader reader = null;
		try {
			fis = new FileInputStream(filePath);
			reader = new BufferedReader(new InputStreamReader(fis, CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line).append("\r\n");
			}
		} catch (IOException e) {
			logger.error(""+e);
		} finally {
			reader.close();
			fis.close();
		}
		String result = buffer.toString();
		return result;
	}

	/**
	 * 读取文件
	 * 
	 * @param fileName
	 * @throws Exception
	 */
	public static String readFile(String fileName) throws IOException {
		return readAbsolutlyFile(PATH + fileName);
	}
}
