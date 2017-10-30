package org.zywx.cooldev.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.vo.Match4Project;

/**
 * 项目报表
 * @author haijun.cheng
 * @date 2016-05-26
 *
 */
@Controller
@RequestMapping(value = "/projectreport")
public class ProjectReportController extends BaseController{

	/**
	 *  任务概况
	 *  @param taskProjectId
	 *  @return
	 */
	@ResponseBody
	@RequestMapping(value="getTaskSituation", method=RequestMethod.GET)
	public Map<String, Object> getTaskSituation(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId) {
		try {
			log.info("get getTaskSituation Detail-->projectId:"+projectId+", loginUserId:"+loginUserId);
			List<Map<String, String>> map = projectReportService.getTaskSituation(projectId);
			return this.getSuccessMap(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("查询任务概况失败");
		}
	}
	/**
	 * 根据项目id查询任务优先级
	 * @param taskProjectId
	 * @return Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value="getTaskPriority",method=RequestMethod.GET)
	public Map<String,Object> getTaskPriority(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId){
		try{
 			log.info("get getTaskPriority Detail-->projectId:"+projectId+", loginUserId:"+loginUserId);
			List<Map<String,Object>> list = projectReportService.getTaskPriority(projectId);
			return this.getSuccessMap(list);
		}catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("查询任务优先级分布失败");
		}
	}
	/**
	 * 根据项目id查询每天的任务情况
	 * @param taskProjectId
	 * @param startdate
	 * @param enddate
	 * @return List<Map<String,Object>>
	 */
	@ResponseBody
	@RequestMapping(value="getTaskCircumstances",method=RequestMethod.GET)
	public Map<String,Object> getTaskCircumstances(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId,
			@RequestParam(required=true) String startdate,
			@RequestParam(required=true) String enddate){
		try{
			log.info("get getTaskCircumstances Detail-->projectId:"+projectId+", loginUserId:"+loginUserId+", startdate:"+startdate+", enddate:"+enddate);
			List<Map<String,Object>> list = projectReportService.getTaskCircumstances(projectId,startdate,enddate);
			return  this.getSuccessMap(list);
		}catch (Exception e) {
			e.printStackTrace();
			return  this.getFailedMap("查询每天的任务情况失败");
		}
	}
	/**
	 * 根据项目id成员完成工作情况
	 *  @param taskProjectId
	 */
	@ResponseBody
	@RequestMapping(value="membersCompleteSituation ",method=RequestMethod.GET)
	public Map<String,Object> membersCompleteSituation(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId){
		try{
			log.info("get membersCompleteSituation Detail-->projectId:"+projectId+", loginUserId:"+loginUserId);
			List<Map<String,Object>> list = projectReportService.membersCompleteSituation(projectId);
			return  this.getSuccessMap(list);
		}catch (Exception e) {
			e.printStackTrace();
			return  this.getFailedMap("查询成员完成工作情况失败");
		}
	}
	/**
	 * 成员未完成任务量和总任务量对比图
	 *  @param projectId
	 *  @param userId
	 */
	@ResponseBody
	@RequestMapping(value="taskCompleteSituation ",method=RequestMethod.GET)
	public Map<String,Object> taskCompleteSituation(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId,
			@RequestParam(required=false) String userId){
		try{
			log.info("get taskCompleteSituation Detail-->projectId:"+projectId+", loginUserId:"+loginUserId+",userId:"+userId);
			List<Map<String,Object>> list = projectReportService.taskCompleteSituation(loginUserId,projectId,userId);
			return  this.getSuccessMap(list);
		}catch (Exception e) {
			e.printStackTrace();
			return  this.getFailedMap("查询成员未完成任务量和总任务量对比图失败");
		}
	}
	/**
	 * 成员详情
	 * @param projectId
	 */
	@RequestMapping(value="/memberDetails")
	public Map<String, Object> memberDetails(HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) long projectId,
			@RequestParam(required=false) String detail,
			@RequestParam(required=false) String sequence
			){
		try {
			log.info("get memberDetails Detail-->projectId:"+projectId+", loginUserId:"+loginUserId);
			String sPageNo      = request.getParameter("pageNo");
			String sPageSize    = request.getParameter("pageSize");

			int pageNo       = 0;
			int pageSize     = 10;
			
			try {
				if(sPageNo != null) {
					int paramsPageNo =Integer.parseInt(sPageNo); 
					if(paramsPageNo>0){
						pageNo		= paramsPageNo-1;
					}
				}
				if(sPageSize != null) {
					pageSize	= Integer.parseInt(sPageSize);
				}
				
			} catch (NumberFormatException nfe) {				
				return this.getFailedMap( nfe.getMessage() );
			}
			
			Map<String,Object> map = projectReportService.memberDetails(pageNo, pageSize, projectId,detail,sequence);
			
			return this.getSuccessMap(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * BUG概况
	 * @param projectId
	 */
	@ResponseBody
	@RequestMapping(value="getBugSituation", method=RequestMethod.GET)
	public Map<String,Object> getBugSituation(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId){
		try {
			log.info("get getBugSituation Detail-->projectId:"+projectId+", loginUserId:"+loginUserId);
			List<Map<String, String>> map = projectReportService.getBugSituation(projectId);
			return this.getSuccessMap(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("查询Bug概况失败");
		}
		
	}
	/**
	 * BUG优先级分布
	 * @param projectId
	 */
	@ResponseBody
	@RequestMapping(value="getBugPriority",method=RequestMethod.GET)
	public Map<String,Object> getBugPriority(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId	){
		try{
			log.info("get getBugPriority Detail --> projectId:"+projectId+", loginUserId:"+loginUserId);
			List<Map<String,String>> map = projectReportService.getBugPriority(projectId);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询BUG优先级分布失败");
		}
	}
	/**
	 * BUG驻留情况
	 * @param projectId
	 */
	@ResponseBody
	@RequestMapping(value="getBugReside",method=RequestMethod.GET)
	public Map<String,Object> getBugReside(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId){
		try{
			log.info("get getBugReside Detail --> projectId:"+projectId+", loginUserId:"+loginUserId);
			List<Map<String,String>> map = projectReportService.getBugRside(projectId);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询BUG驻留情况失败");
		}
	}
	/**
	 * 每天的BUG情况
	 * @param projectId
	 */
	@ResponseBody
	@RequestMapping(value="getBugCircumstances",method=RequestMethod.GET)
	public Map<String,Object> getBugCircumstances(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId,
			@RequestParam(required=true) String startdate,
			@RequestParam(required=true) String enddate){
		try{
			log.info("get getBugCircumstances Detail --> projectId:"+projectId+", loginUserId:"+loginUserId+", startdate:"+startdate+", enddate:"+enddate);
			List<Map<String,String>> map = projectReportService.getBugCircumstances(projectId,startdate,enddate);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询每天的BUG情况失败");
		}
	}
	/**
	 * 成员未完成BUG量和总BUG量
	 * @param projectId
	 * @param userId
	 */
	@ResponseBody
	@RequestMapping(value="bugCompleteSituation",method=RequestMethod.GET)
	public Map<String,Object> bugCompleteSituation(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId,
			@RequestParam(required=true) String userId){
		try{
			log.info("get bugCompleteSituation Detail --> projectId:"+projectId+", loginUserId:"+loginUserId+", userId:"+userId);
			List<Map<String,String>> map = projectReportService.bugCompleteSituation(loginUserId,projectId,userId);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询成员未完成BUG量和总BUG量失败");
		}
	}
	/**
	 * 成员每天完成任务情况
	 * @param projectId
	 * @param userId
	 * @param startdate
	 * @param enddate 
	 */
	@ResponseBody
	@RequestMapping(value="memberTaskCompleteSituation",method=RequestMethod.GET)
	public Map<String,Object> memberTaskCompleteSituation(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId,
			@RequestParam(required=true) String userId,
			@RequestParam(required=true) String startdate,
			@RequestParam(required=true) String enddate){
		try{
			log.info("get memberTaskCompleteSituation Detail --> projectId:"+projectId+",userId:"+userId+", startdate:"+startdate+", enddate:"+enddate);
			List<Object> map = projectReportService.memberTaskCompleteSituation(projectId,userId,startdate,enddate,loginUserId);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询成员完成BUG情况失败");
		}
	}
	/**
	 * 成员每天完成BUG情况
	 * @param projectId
	 * @param userId
	 * @param startdate
	 * @param enddate 
	 */
	@ResponseBody
	@RequestMapping(value="memberBugCompleteSituation",method=RequestMethod.GET)
	public Map<String,Object> memberBugCompleteSituation(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId,
			@RequestParam(required=true) String userId,
			@RequestParam(required=true) String startdate,
			@RequestParam(required=true) String enddate){
		try{
			log.info("get memberBugCompleteSituation Detail --> projectId:"+projectId+", userId"+userId+", startdate:"+startdate+", enddate:"+enddate);
			List<Object> map = projectReportService.memberBugCompleteSituation(projectId,userId,startdate,enddate);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询成员完成BUG情况失败");
		}
	}
	/**
	 * 查询项目建立日期
	 * @param projectId
	 */
	@ResponseBody
	@RequestMapping(value="projectCreatedAt",method=RequestMethod.GET)
	public Map<String,Object> projectCreatedAt(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId){
		try{
			log.info("get projectCreatedAt Detail --> projectId:"+projectId);
			Map<String,String> map = projectReportService.projectCreatedAt(projectId);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询项目开始时间失败");
		}
	}
	/**
	 * 查询该项目下已选则的成员
	 * @param projectId
	 */
	@ResponseBody
	@RequestMapping(value="selectMemberChoiced",method=RequestMethod.GET)
	public Map<String,Object> selectMemberChoiced(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId){
		try{
			log.info("get selectMemberChoiced Detail --> projectId:"+projectId);
			List<Map<String,String>> map = projectReportService.selectMemberChoiced(loginUserId,projectId);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询项目下已选则的成员失败");
		}
	}
	/**
	 * 成员每天完成工作情况查询已选则的成员和图表内容
	 * @param projectId
	 */
	@ResponseBody
	@RequestMapping(value="memberCompleteChoiced",method=RequestMethod.GET)
	public Map<String,Object> memberCompleteChoiced(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId){
		try{
			log.info("get memberCompleteChoiced Detail --> projectId:"+projectId);
			Map<String, Object> map = projectReportService.memberCompleteChoiced(loginUserId,projectId);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("成员每天完成工作情况查询已选则的成员和图表内容失败");
		}
	}
	/**
	 * 流程阶段情况
	 * @param projectId
	 */
	@RequestMapping(value="/workplat")
	public Map<String, Object> getWorkPlatList(Match4Project match, HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) long loginUserId){
		try {
			log.info("get workplat Detail --> loginUserId:"+loginUserId);
			Map<String,Object> map = this.projectReportService.getWorkPlatList(match, loginUserId);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 成员每天完成工作情况保存已选则的成员和图表内容
	 * @param projectId
	 * @param chartContent
	 */
	@ResponseBody
	@RequestMapping(value="putChoiced",method=RequestMethod.PUT)
	public Map<String,Object> putChoiced(
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true) Long projectId,
			@RequestParam(required=false) String chartContent,
			@RequestParam(required=true) String userId){
		try{
			log.info("get putChoiced Detail --> projectId:"+projectId+", chartContent:"+chartContent+", userId:"+userId);
			int map = projectReportService.putChoiced(loginUserId,projectId,chartContent,userId);
			return this.getSuccessMap(map);
 		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("保存已选则的成员和图表内容失败");
		}
	}
}
