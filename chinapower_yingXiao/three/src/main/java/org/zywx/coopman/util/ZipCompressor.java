	/**  
     * @author jingjian.wu
     * @date 2015年9月24日 下午12:15:39
     */
    
package org.zywx.coopman.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


    /**
 * @author jingjian.wu
 * @date 2015年9月24日 下午12:15:39
 */

public class ZipCompressor {

	private static Log logger = LogFactory.getLog(ZipCompressor.class);
	static final int BUFFER = 8192;

	public static void main(String[] args) {
//		compress("e:\\testlog", "e:\\ttt.zip", "bbb/");
		List<String> list = new ArrayList<String>();
		list.add("d:\\res");
		list.add("d:\\2.sql");
		list.add("d:\\7.sql");
		compress(list, "d:\\tmp\\tmp.zip", "baiduMap/");
	}
	
public static boolean compress(List<String> srcPath1, String desPath, String basedir) {
		
		logger.debug("compress method ----> srcPath: " + srcPath1 
				+ ", desPath: " + desPath + ", basedir: " + basedir);
		
		boolean flag = true;
		try {
			File zipFile = new File(desPath);
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
					new CRC32());
			ZipOutputStream out = new ZipOutputStream(cos);
			for(String srcPath:srcPath1){
				File file = new File(srcPath);
				if (!file.exists()){
					logger.error(srcPath + " is not exist!");
					flag = false;
				} else {
					compress(file, out, basedir);
				}
			}
			out.close();
		}  catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
		logger.debug("compress result :----->"+flag);
		return flag;
	}


public static boolean compress(String srcPath, String desPath, String basedir) {
	
	logger.debug("compress method ----> srcPath: " + srcPath 
			+ ", desPath: " + desPath + ", basedir: " + basedir);
	
	boolean flag = true;
	
	File zipFile = new File(desPath);
	File file = new File(srcPath);
	if (!file.exists()){
		logger.error(srcPath + " is not exist!");
		flag = false;
	} else {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
					new CRC32());
			ZipOutputStream out = new ZipOutputStream(cos);
			//String basedir = "bbb";
			compress(file, out, basedir);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
	}
	logger.debug("compress result :----->"+flag);
	return flag;
}




private static void compress(File file, ZipOutputStream out, String basedir) {
	/* 判断是目录还是文件 */
	if (file.isDirectory()) {
		compressDirectory(file, out, basedir);
	} else {
		compressFile(file, out, basedir);
	}
}

private static void compressDirectory(File dir, ZipOutputStream out, String basedir) {
	if (!dir.exists())
		return;
	
	File[] files = dir.listFiles();
	for (int i = 0; i < files.length; i++) {
		/* 递归 */
		compress(files[i], out,  basedir + dir.getName() + "/");
	}
}

/** 压缩一个文件 */
private static void compressFile(File file, ZipOutputStream out, String basedir) {
	if (!file.exists()) {
		return;
	}
	try {
		BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(file));
		ZipEntry entry = new ZipEntry(basedir + file.getName());
		out.putNextEntry(entry);
		int count;
		byte data[] = new byte[BUFFER];
		while ((count = bis.read(data, 0, BUFFER)) != -1) {
			out.write(data, 0, count);
		}
		bis.close();
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}
}
