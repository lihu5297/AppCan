package org.zywx.cooldev.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.entity.TeamAnaly;
import org.zywx.cooldev.util.ExportExcel;

/**
 * 团队统计
 * 
 * @author yongewen.wang
 * 
 */
@Controller
@RequestMapping(value = "/teamAnaly")
public class TeamAnalyController extends BaseController {
	@Value("${downExcel.path}")
	private String downExcelPath;
	/**
	 * 1.团队下项目总数统计
	 * 
	 * @param teamId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/projectTotal", method = RequestMethod.GET)
	public Map<String, Object> projectAnaly(
			@RequestParam(value = "teamId") long teamId,
			@RequestHeader(value = "loginUserId") long loginUserId) {
		try {
			Map<String, Object> map = this.teamAnalyService.projectAnaly(
					teamId, loginUserId);
			return this.getSuccessMap(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	/**
	 * 2.团队下项目详情统计
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/projectsDetail", method = RequestMethod.GET)
	public Map<String, Object> projectDetail(
			@RequestHeader(value = "loginUserId") long loginUserId,
			@RequestParam(value = "teamId") long teamId) {
		try {
			Map<String, Object> map = this.teamAnalyService.getProjectsDetail(
					loginUserId, teamId);
			return this.getSuccessMap(map);
		} catch (Exception e) {
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 3.增加项目统计
	 */
	@ResponseBody
	@RequestMapping(value = "/addProject", method = RequestMethod.POST)
	public Map<String, Object> addProject(
			@RequestHeader(value = "loginUserId") long loginUserId,
			TeamAnaly teamAnaly) {
		try {
			Map<String, Object> map = this.teamAnalyService.addProject(
					loginUserId, teamAnaly);
			if(map.get("failed")!=null){
				return this.getFailedMap(map.get("failed"));
			}else{
				return this.getSuccessMap(map);
			}
		} catch (Exception e) {
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 4.判断此人是不是团队的管理员、创建者
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/judgeTeamManager", method = RequestMethod.GET)
	public Map<String, Object> judgeTeamManager(
			@RequestHeader(value = "loginUserId") long loginUserId,
			@RequestParam(value = "teamId") long teamId) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			String yesOrNo = this.teamAnalyService.judgeTeamManager(
					loginUserId, teamId);
			map.put("yesOrNo", yesOrNo);
			return this.getSuccessMap(map);
		} catch (Exception e) {
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 5.删除项目统计
	 */
	@ResponseBody
	@RequestMapping(value = "/removeProject", method = RequestMethod.DELETE)
	public Map<String, Object> removeProject(
			@RequestHeader(value = "loginUserId") long loginUserId,
			TeamAnaly teamAnaly) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			String yesOrNo = this.teamAnalyService.judgeTeamManager(
					loginUserId, teamAnaly.getTeamId());
			if (yesOrNo.equals("YES")) {
				int affect = this.teamAnalyService.removeProject(
						teamAnaly.getProjectId(), teamAnaly.getTeamId());
				map.put("affect", affect);
				return this.getSuccessMap(map);
			} else {
				map.put("failed", "无权限，您不是该团队的创建者或管理员");
				return this.getSuccessMap(map);
			}
		} catch (Exception e) {
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 6.统计团队成员
	 */
	@ResponseBody
	@RequestMapping(value = "/teamMember", method = RequestMethod.GET)
	public Map<String, Object> teamMember(
			@RequestHeader(value = "loginUserId",required=false) long loginUserId,
			@RequestParam(value = "teamId") long teamId,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value="pageNo",required=false) String pageNo,
			@RequestParam(value="pageSize",required=false) String pageSize) {
		try {
			Integer pageNoS=1;
			Integer pageSizeS=10;
			if(pageNo!=null){
				pageNoS=Integer.parseInt(pageNo);
			}
			if(pageSize!=null){
				pageSizeS=Integer.parseInt(pageSize);
			}
			Map<String, Object> map = this.teamAnalyService.teamMember(teamId,pageNoS,pageSizeS);
			if (type!=null&&type.equals("downloadExcel")) {
				String[] title = new String[] { "成员|userName",
						"参与项目|projectTotal", "任务总量|taskTotal",
						"未完成任务|taskNoFinishTotal", "Bug总量|bugTotal",
						"未解决Bug|bugNoFixTotal" };
				String fileName = "团队成员统计";
				@SuppressWarnings("unchecked")
				HSSFWorkbook wb = ExportExcel.exportExcel(title, fileName,
						(ArrayList<Object>) (map.get("data")));
				String putFileName = new Date().getTime() + ".xls";
				File file = new File(downExcelPath, putFileName);
				FileOutputStream fo = new FileOutputStream(file);
				wb.write(fo);
				fo.flush();
				fo.close();
				Map<String,Object> pathMap=new HashMap<String,Object>();
				pathMap.put("path", downExcelPath + "/" + putFileName);
				return this.getSuccessMap(pathMap);
			} else {
				return this.getSuccessMap(map);
			}
		} catch (Exception e) {
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 7.统计多个成员
	 */
	@ResponseBody
	@RequestMapping(value="/selectTeamMemberTJ",method=RequestMethod.GET)
	public Map<String,Object> selectTeamMemberTJ(@RequestHeader(value="loginUserId",required=false) long loginUserId,
			@RequestParam(value="teamId") long teamId,@RequestParam(value="userIdList") ArrayList<Long> userIdList){
		try{
		    ArrayList<Object> list=this.teamAnalyService.selectTeamMemberTj(teamId,userIdList);
		    return this.getSuccessMap(list);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 查看团队下的成员
	 */
	@ResponseBody
	@RequestMapping(value="/selectTeamMemberList",method=RequestMethod.GET)
	public Map<String,Object> selectTeamMemberList(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(value="teamId") long teamId,@RequestParam(value="userNameSearch") String userNameSearch,
			@RequestParam(value="existUserIdList",required=false) String existUserIdList){
		try{
			ArrayList<Object> list=this.teamAnalyService.selectTeamMemberList(teamId,userNameSearch,loginUserId,existUserIdList);
			if(list.size()==0){
				return this.getSuccessMap(list);
			}else{
				return this.getSuccessMap(list.get(0));
			}
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

}
