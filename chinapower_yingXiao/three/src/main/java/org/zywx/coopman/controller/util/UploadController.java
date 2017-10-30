/**
 * $Id: UploadController.java 6243 2013-10-09 07:17:39Z chenggang.du $
 * Copyright: 正益无线2011 版权所有
 */

package org.zywx.coopman.controller.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.resource.ResourceFileInfo;
import org.zywx.coopman.entity.resource.TempletInfo;
import org.zywx.coopman.service.ManagerService;
import org.zywx.coopman.service.MutipartUploadFile;
import org.zywx.coopman.service.ResourceService;



/**
 * 上传文件或图片的控制器类
 * 
 * 
 */
@Controller
@RequestMapping(value = "/upload")
public class UploadController {

	private Log Logger = LogFactory.getLog(getClass());

	@Value("${rootpath}")
	private String fileRootPath;

	@Value("${accessUrl}")
	private String accessUrl;
	
	 @Autowired
	 protected  MutipartUploadFile mutipartUploadFile;
	@Autowired
	private ResourceService resourceService;
	@Autowired
	protected ManagerService managerService;
		

	/**
	 * 上传内容附件
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/contentFile")
	public void notificationFile(HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter pw = getPrintWriter(response);
		JSONObject jsonObject = new JSONObject();
		if (pw == null) {
			Logger.error("when upload notificationFile,get printWriter is error!");
			return;
		}
		if (request instanceof MultipartHttpServletRequest) {
			//String uploadTmpLoc = "E:/";
			String uploadTmpLoc = fileRootPath;
			String relativePath = "/contentMgr/contentFile/";
			String uploadFileName = "file";
			String acceptTypes = "doc,docx,xls,xlsx,txt,pdf,zip,rar,jpg,png,gif,bmp,jpe,jpeg,ico";
			long acceptSize = 10 * 1048576; // 文件限制大小10M

			String[] fileInfo = mutipartUploadFile.upload(request,
					uploadFileName, uploadTmpLoc, relativePath, acceptTypes,
					acceptSize);

			if (fileInfo != null) {
				if (fileInfo[0].equals("1") && fileInfo[1].equals("size not accept")) {
					jsonObject.put("retCode", "1");
					jsonObject.put("retInfo", "上传文件超过最大限制10MB！");
				} else if (fileInfo[0].equals("1") && fileInfo[1].equals("format not accept")) {
					jsonObject.put("retCode", "1");
					jsonObject.put("retInfo", "文件格式超出限制!");
				} else if(fileInfo[0].equals("0")) {
					ResourceFileInfo rf = new ResourceFileInfo();
					rf.setFileName(fileInfo[1]);
					rf.setOriginalName(fileInfo[2]);
					rf.setFilePath(relativePath+fileInfo[1]);
					rf.setFileSize(Long.parseLong(fileInfo[3]));
					rf = resourceService.saveUpdateFileInfo(rf);
					
					jsonObject.put("fileId", rf.getId());
					jsonObject.put("fileUrl", accessUrl+ relativePath+fileInfo[1]);
					jsonObject.put("fileOriginalName", fileInfo[2]);
					jsonObject.put("retCode", "0");
					jsonObject.put("retInfo", "上传附件成功!");
				}
			} else {
				jsonObject.put("retCode", "1");
				jsonObject.put("retInfo", "上传附件失败！");
			}
		}
		pw.print(jsonObject);
		closePrintWriter(pw);
		return;
	}


	/**
	 * 
	 * 上传模板
	 * 
	 * @return
	 */
	@RequestMapping(value = "/templetFile/{id}")
	public void templetFile(HttpServletRequest request, HttpServletResponse response,@PathVariable("id")Long id) {

		PrintWriter pw = getPrintWriter(response);
		JSONObject jsonObject = new JSONObject();
		if (pw == null) {
			Logger.error("when upload newsImg,get printWriter is error!");
			return;
		}

		if (request instanceof MultipartHttpServletRequest) {

			String uploadTmpLoc = fileRootPath;
			String relativePath = "/templetMgr/templetFile/";

			String uploadFileName = "file";

			String acceptTypes = "doc,docx,xls,xlsx,txt,pdf,zip,rar,jpg,png,gif,bmp,jpe,jpeg,ico";

			long acceptSize = 10 * 1048576; // 文件限制大小10M

			String[] fileInfo = mutipartUploadFile.upload(request,
					uploadFileName, uploadTmpLoc, relativePath, acceptTypes,
					acceptSize);

			if (fileInfo != null) {
				if (fileInfo[0].equals("1") && fileInfo[1].equals("size not accept")) {
					jsonObject.put("retCode", "1");
					jsonObject.put("retInfo", "上传文件超过最大限制10MB！");
				} else if (fileInfo[0].equals("1") && fileInfo[1].equals("format not accept")) {
					jsonObject.put("retCode", "1");
					jsonObject.put("retInfo", "文件格式超出限制!");
				} else if(fileInfo[0].equals("0")) {
					TempletInfo ti = resourceService.findTempletById(id);
					long adminId = (Long)request.getSession().getAttribute("userId");
					ti.setCreator(adminId);
					ti.setFilePath(relativePath+fileInfo[1]);
					ti = resourceService.saveEditTempletInfo(ti);
					Manager m = managerService.getMnager(adminId);
					jsonObject.put("fileUrl", accessUrl+ relativePath+fileInfo[1]);
					jsonObject.put("creatorName", m != null ? m.getAccount() : "");
					jsonObject.put("updatedAt",m.getUpdatedAt());
					jsonObject.put("retCode", "0");
					jsonObject.put("retInfo", "上传附件成功!");
				}
			} else {
				jsonObject.put("retCode", "1");
				jsonObject.put("retInfo", "上传附件失败！");
			}
		}
		pw.print(jsonObject);
		closePrintWriter(pw);
		return;
	}


	private PrintWriter getPrintWriter(HttpServletResponse response) {

		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
		} catch (IOException e) {
			Logger.error("get printWriter is error!", e);
			return null;
		}
		return pw;
	}

	private void closePrintWriter(PrintWriter pw) {
		if (pw != null) {
			pw.flush();
			pw.close();
		}
	}

	private void iniFileDir(String fileDir) {
		FileUploadingUtil.FILEDIR = fileRootPath + fileDir;
		if (FileUploadingUtil.FILEDIR == null) {
			FileUploadingUtil.FILEDIR = "/opt/emm/uploads/" + fileDir;
		}
	}

}
