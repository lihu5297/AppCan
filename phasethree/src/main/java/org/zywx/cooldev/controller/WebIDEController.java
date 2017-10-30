package org.zywx.cooldev.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.appdo.facade.mam.entity.application.MdmAplctiongrp;
import org.zywx.appdo.facade.mam.service.appGroup.MdmAplctiongrpFacade;
import org.zywx.cooldev.entity.Advice;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.service.AdviceService;
import org.zywx.cooldev.service.WebIDEService;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.emm.TokenUtilProduct;

import net.sf.json.JSONObject;


/**
 * 意见反馈相关处理控制器
 * @author yang.li
 * @date 2015-09-18
 *
 */
@Controller
@RequestMapping(value = "/webide")
public class WebIDEController extends BaseController {

	@Autowired
	private WebIDEService webIDEService;

	/**
	 * 浏览代码
	 * @param appId
	 * @param relativePath
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/browse", method=RequestMethod.GET)
	public Map<String, Object> browse(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="relativePath") String relativePath,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			Map<String, Object> ret = webIDEService.browseRepo(appId, loginUserId, relativePath);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());
		
		}
	}
	
	//***********************************
	//*		File Actions                *
	//***********************************
	/**
	 * 获取文件
	 * @param appId
	 * @param relativePath
	 * @param response
	 * @param loginUserId
	 */
	@RequestMapping(value="/file", method=RequestMethod.GET)
	public void file(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="relativePath") String relativePath,
			HttpServletResponse response,
			@RequestParam(value="loginUserId") long loginUserId) {

		try {
			File file = webIDEService.fileRepo(appId, loginUserId, relativePath);
			response.setContentType("text/plain");
			
	        response.reset(); // 清除response中的缓存信息
	        ServletOutputStream out = response.getOutputStream();
	        
	        if(file.length() == 0) {
	        	// 空文件
	        	;
	        } else {
				FileInputStream      is = new FileInputStream(file);
		        
				while(is.available() > 0) {
				
			        byte[] content = new byte[is.available()];
			        
			        is.read(content);
			        out.write(content, 0, content.length);
				}
		        
				is.close();
	        }

	        out.flush();
	        out.close();

			
		} catch(Exception e) {
			log.error("webide -> file -> " +e.getMessage());
		}
	}

	/**
	 * 保存文件
	 * @param appId
	 * @param relativePath
	 * @param content
	 * @param loginUserId
	 * @return
	 */
	@RequestMapping(value="/storeFile", method=RequestMethod.PUT)
	public Map<String, Object> storeFile(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="relativePath") String relativePath,
			@RequestParam(value="content") String content,
			@RequestParam(value="loginUserId") long loginUserId) {
		
		
		try {
			log.info("--------->come into storeFile method");
			boolean ret = webIDEService.storeFile(appId, loginUserId, relativePath, content);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
		
	}
	
	/**
	 * 创建文件夹
	 * @param appId
	 * @param relativePath
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/folder", method=RequestMethod.POST)
	public Map<String, Object> createFolder(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="relativePath") String relativePath,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			log.info("--------->创建文件夹 appId:"+appId+",relativePath:"+relativePath);
			boolean ret = webIDEService.makeDirectory(appId, loginUserId, relativePath);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	
	/**
	 * 重命名文件夹
	 * @param appId
	 * @param relativePath
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/folder", method=RequestMethod.PUT)
	public Map<String, Object> renameFolder(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="relativePath") String relativePath,
			@RequestParam(value="newName") String newName,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			boolean ret = webIDEService.renameDirectory(appId, loginUserId, relativePath, newName);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}

	/**
	 * 删除文件夹
	 * @param appId
	 * @param relativePath
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/folder", method=RequestMethod.DELETE)
	public Map<String, Object> removeFolder(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="relativePath") String relativePath,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			boolean ret = webIDEService.removeDirectory(appId, loginUserId, relativePath);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}	

	/**
	 * 创建文件
	 * @param appId
	 * @param relativePath
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/file", method=RequestMethod.POST)
	public Map<String, Object> createFile(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="relativePath") String relativePath,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			boolean ret = webIDEService.makeFile(appId, loginUserId, relativePath);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}	

	/**
	 * 重命名文件
	 * @param appId
	 * @param relativePath
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/file", method=RequestMethod.PUT)
	public Map<String, Object> renameFile(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="relativePath") String relativePath,
			@RequestParam(value="newName") String newName,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			boolean ret = webIDEService.renameFile(appId, loginUserId, relativePath, newName);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}

	/**
	 * 删除文件
	 * @param appId
	 * @param relativePath
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/file", method=RequestMethod.DELETE)
	public Map<String, Object> removeFile(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="relativePath") String relativePath,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			boolean ret = webIDEService.removeFile(appId, loginUserId, relativePath);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}	

	/**
	 * 拉取代码
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/pull", method=RequestMethod.GET)
	public Map<String, Object> pull(
			@RequestParam(value="appId") long appId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			String ret = webIDEService.gitPull(appId, loginUserId);
			return this.getWarningMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	
	/**
	 * 提交代码
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/commit", method=RequestMethod.GET)
	public Map<String, Object> commit(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="message") String message,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			String ret = webIDEService.gitCommit(appId, loginUserId, message);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	
	/**
	 * 推送代码
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/push", method=RequestMethod.GET)
	public Map<String, Object> push(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="branch") String branch,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			String ret = webIDEService.gitPush(appId, loginUserId, branch);
			return this.getWarningMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	
	/**
	 * 提交与推送代码
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/commitThenPush", method=RequestMethod.GET)
	public Map<String, Object> commitThenPush(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="message") String message,
			@RequestParam(value="branch") String branch,
			@RequestParam(value="relativePathes") List<String> relativePathes,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			String ret = webIDEService.gitCommitThenPush(appId, loginUserId, message, branch, relativePathes);
			return this.getWarningMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	@ResponseBody
	@RequestMapping(value="/commitFiles", method=RequestMethod.GET)
	public Map<String, Object> commitFiles(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="message") String message,
			@RequestParam(value="relativePathes") List<String> relativePathes,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			boolean ret = webIDEService.gitCommitFiles(appId, loginUserId, message, relativePathes);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
		
	}
	
	/**
	 * 本地分支列表
	 * @param appId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/branch", method=RequestMethod.GET)
	public Map<String, Object> branch(
			@RequestParam(value="appId") long appId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			List<Map<String, Object>> ret = webIDEService.getLocalBranchList(appId, loginUserId);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	
	/**
	 * 远程分支列表
	 * @param appId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/branchRemote", method=RequestMethod.GET)
	public Map<String, Object> branchRemote(
			@RequestParam(value="appId") long appId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			List<Map<String, Object>> ret = webIDEService.getRemoteBranchList(appId, loginUserId);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	
	/**
	 * 检出分支
	 * @param appId
	 * @param branch
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/checkout", method=RequestMethod.GET)
	public Map<String, Object> checkout(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="branch") String branch,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			String ret = webIDEService.checkoutBranch(appId, loginUserId, branch);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	
	/**
	 * 检出远程分支
	 * @param appId
	 * @param branch
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/checkoutRemote", method=RequestMethod.GET)
	public Map<String, Object> checkoutR(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="branch") String branch,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			log.info("----->checkoutRemote>appId:"+appId+",branch:"+branch+",loginUserId:"+loginUserId);
			String ret = webIDEService.checkoutBranchR(appId, loginUserId, branch);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	
	/**
	 * 删除分支
	 * @param appId
	 * @param branch
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/branchDelete", method=RequestMethod.GET)
	public Map<String, Object> branchDelete(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="branch") String branch,
			@RequestHeader(value="loginUserId") long loginUserId) {
	
	try {
		String ret = webIDEService.deleteBranch(appId, loginUserId, branch);
		return this.getSuccessMap(ret);

	} catch(Exception e) {
		return this.getFailedMap(e.getMessage());

	}
}

	/**
	 * 创建分支
	 * @param appId
	 * @param branch
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/branch", method=RequestMethod.POST)
	public Map<String, Object> addBranch(
			@RequestParam(value="appId") long appId,
			@RequestParam(value="branch") String branch,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			String ret = webIDEService.createBranch(appId, loginUserId, branch);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}
	
	/**
	 * 获取文件状态
	 * @param appId
	 * @param branch
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/status", method=RequestMethod.GET)
	public Map<String, Object> getStatus(
			@RequestParam(value="appId") long appId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			Map<String, Object> ret = webIDEService.gitStatus(appId, loginUserId);
			return this.getSuccessMap(ret);

		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());

		}
	}

	@ResponseBody
	@RequestMapping(value="/diff/file", method=RequestMethod.GET)
	public Map<String, Object> modifiedDiff(
		@RequestParam(value="appId") long appId,
		@RequestParam(value="relativePath") String relativePath,
		@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			String ret = webIDEService.gitDiffFile(appId, loginUserId, relativePath);
					
			return this.getSuccessMap(ret);
	
		} catch(Exception e) {
			return this.getFailedMap(e.getMessage());
	
		}
	}

	@RequestMapping(value="/code/preview", method=RequestMethod.GET)
	public void codePreview(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		String source = request.getParameter("source");
		if(!source.startsWith("/")) {
			source = "/" + source;
		}
		
		response.setHeader("X-Accel-Redirect", "/personalRepo/" + loginUserId + source);
		
	}
}
