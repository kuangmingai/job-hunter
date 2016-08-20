package com.xiaoyao.jobhunter.commons.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

/**
 * 文件操作类 <BR>
 * 都是以 classpath 为目录.
 * 
 * @author 旷明爱
 * @date Aug 1, 2012 11:55:02 AM
 */
public class FileUtil {
	// 源文件保存目录
	public static final String SOURCE_DIR = "config/";

	private static Logger logger = Logger.getLogger(FileUtil.class);
	private static String PATH = "";
	static {
		try {
			PATH = FileUtil.class.getClassLoader().getResource("").toURI().getPath();

			File sourceDir = new File(PATH + SOURCE_DIR);
			if (!sourceDir.exists()) {
				sourceDir.mkdirs();
			}

		} catch (URISyntaxException e) {
			logger.error("文件目录获取初始化失败!" + e.getMessage());
			throw new RuntimeException(e);
		}
		logger.info("路径PATH:" + PATH);
	}

	public static void main(String[] args) throws Exception {
		// saveFile("aa.txt", "aa;", true) ;
		// saveFile("aa.txt", "aa;", true) ;

		String fileName = "C:\\Users\\kuangmingai\\Desktop\\";
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
	 * 保存图片 携程图片为 jpg 格式
	 * 
	 * @param bytes
	 * @throws Exception
	 */
	public static void savePic(byte[] bytes, String picPath) {
		try {
			if (bytes == null) {
				logger.error("保存图片失败!没有下载到");
			} else {
				FileOutputStream fos = new FileOutputStream(picPath);
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
		try {
			FileOutputStream fos = new FileOutputStream(PATH + fileName);
			fos.write(content.getBytes());
			fos.close();
		} catch (IOException e) {
			logger.error("保存文件错误!" + e.getMessage());
		}
	}

	/**
	 * 保存文件 绝对路径
	 * 
	 * @param fileName
	 * @param content
	 * @throws Exception
	 */
	public static void saveAbsolutlyFile(String fileName, String content) {
		if (ClassUtil.isEmpty(content)) {
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
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
	 * @param fileName
	 * @throws Exception
	 */
	public static String readAbsolutlyFile(String fileName) throws IOException {
		StringBuffer buffer = new StringBuffer(10000);
		FileInputStream fis = null;
		BufferedReader reader = null;
		try {
			fis = new FileInputStream(fileName);
			reader = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = reader.readLine()) != null) {
//				logger.info(""+line);
				buffer.append(line);
			}
		} catch (IOException e) {
			logger.error(e);
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
