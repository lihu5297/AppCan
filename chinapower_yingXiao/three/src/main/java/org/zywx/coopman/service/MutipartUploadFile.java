/**
 * $Id: MutipartUploadFile.java 6243 2013-10-09 07:17:39Z chenggang.du $
 * Copyright: 正益无线2013 版权所有
 */

package org.zywx.coopman.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


/** 
 * Spring支持web应用中的分段文件上传。这种支持是由即插即用的MultipartResolver来实现
 *
 * @author Administrator 
 */
@Service
public class MutipartUploadFile {
	
	protected Log logger = LogFactory.getLog(getClass());
	/** 默认的文件名生成器 */
	public static final FileNameGenerator DEFAULT_FILE_NAME_GENERATOR = new CommonFileNameGenerator();
	
	public FileNameGenerator fileNameGenerator = DEFAULT_FILE_NAME_GENERATOR;
	
	/**
	 * 
	 * 上传,返回relativePath+文件名
	 *
	 * @return
	 */
	public String[] upload(HttpServletRequest request, String fileName, String uploadTmpLoc, String relativePath, String acceptTypes, long acceptSize){
		 String[] strArrays = new String[4];
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	    CommonsMultipartFile orginalFile = (CommonsMultipartFile) multipartRequest.getFile(fileName);
	    
        if (orginalFile != null && !orginalFile.isEmpty()) {
        	File dir = new File(uploadTmpLoc + relativePath);
        	if(!dir.exists()){
        		try{
        			dir.mkdirs();
        		}catch(Exception e){
        			logger.error("error when mkdir with path:"+uploadTmpLoc + relativePath, e);
        			return null;
        		}
        	}
        	String suffix = getSuffix(orginalFile.getOriginalFilename());
        	long fileSize = orginalFile.getSize();
        	if(fileSize > acceptSize){
        		strArrays[0] = "1";
        		strArrays[1] = "size not accept";
        		return strArrays;
        	}
        	if(acceptTypes != null && !acceptTypes.isEmpty()){
        		if(!acceptTypes.toLowerCase().contains(suffix)){
        			logger.info("上传的文件类型是:"+suffix+";支持的文件类型是:"+acceptTypes);
        			strArrays[0] = "1";
            		strArrays[1] = "format not accept";
            		return strArrays;
        		}
        	}
        	String targetFileName = fileNameGenerator.generate(suffix);
            String targetFile = uploadTmpLoc + relativePath + targetFileName;
            InputStream inStream = null;
            DataOutputStream outStream = null;
            try {
                outStream = new DataOutputStream(new FileOutputStream(targetFile));
                inStream = orginalFile.getInputStream();
                byte[] buffer = new byte[inStream.available()];
                inStream.read(buffer);
                outStream.write(buffer);
            } catch (Exception e) {
                logger.error("write file error in MutipartUploadFile with uploadPath:"+uploadTmpLoc + relativePath, e);
                return null;
            } finally {
            	if (inStream != null) {
        			try {
        				inStream.close();
        			} catch (IOException ex) {
        				logger.error("error occured in close file:", ex);
        			}
        		}
            	if (outStream != null) {
        			try {
        				outStream.close();
        			} catch (IOException ex) {
        				logger.error("error occured in close file:", ex);
        			}
        		}
            }
            logger.info("MutipartUploadFile success with file:"+relativePath + orginalFile.getOriginalFilename());
            strArrays[0] = "0";
            strArrays[1] = targetFileName;
            strArrays[2] = orginalFile.getOriginalFilename();
            strArrays[3] = String.valueOf(fileSize);
            return strArrays;
        }else{
        	logger.error("orginalFile is null in MutipartUploadFile");
        	return null;
        }
	}
	

	
	/**
	 * 通用文件名生成器
	 * 
	 * 实现  接口，根据序列值和时间生成唯一文件名
	 * 
	 */
	public static class CommonFileNameGenerator implements FileNameGenerator {
		private static final int MAX_SERIAL = 999999;
		private static final AtomicInteger atomic = new AtomicInteger();

		private static int getNextInteger() {
			int value = atomic.incrementAndGet();
			if (value >= MAX_SERIAL)
				atomic.set(0);

			return value;
		}

		
		/**
		 * 根据序列值和时间生成 'XXXXXX_YYYYYYYYYYYYY' 格式的唯一文件名
		 */
		@Override
		public String generate(String suffix) {
			int serial = getNextInteger();
			long millsec = System.currentTimeMillis();
			return String.format("%06d_%013d", serial, millsec)+"."+suffix;
		}
	}
	
	/**
	 * 文件名生成器接口
	 * 
	 * 每次保存一个上传文件前都需要调用该接口的方法生成要保存的文件名
	 * 
	 */
	public static interface FileNameGenerator {
		/**
		 * 文件名生成方法
		 * 
		 * @param item
		 *            : 上传文件对应的对象
		 * @param suffix
		 *            : 上传文件的后缀名
		 * 
		 */
		String generate(String suffix);
	}
	
	
	/**
	 * 获取文件名后缀
	 */
	public static String getSuffix(String orginalFileName){
		
		String suffix = null;
		int stuffPos = orginalFileName.lastIndexOf(".");

		if (stuffPos != -1) {
			suffix = orginalFileName
					.substring(stuffPos+1, orginalFileName.length()).toLowerCase();
		}
		return suffix;
	}
}
