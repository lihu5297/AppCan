package org.zywx.cooldev.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.TOPIC_MEMBER_TYPE;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.query.TopicQuery;
import org.zywx.cooldev.entity.topic.Topic;
import org.zywx.cooldev.entity.topic.TopicComment;
import org.zywx.cooldev.entity.topic.TopicMember;
import org.zywx.cooldev.entity.topic.TopicResource;
import org.zywx.cooldev.service.TopicResourceService;
import org.zywx.cooldev.service.TopicService;

/**
 * 
 * @describe get方法默认是获取list数据 <br>
 * @author jiexiong.liu <br>
 * @date 2015年8月11日 下午5:18:39 <br>
 *
 */
@Controller
@RequestMapping(value = "/topic")
public class TopicController extends BaseController {

	@Autowired
	protected TopicService topicService;

	@Autowired
	protected TopicResourceService topicResourceService;

	/**
	 * 
	 * @describe 创建topic <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月17日 下午2:48:13 <br>
	 * @param request
	 * @param response
	 *            <br>
	 * @returnType void
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "", method = { RequestMethod.POST })
	public ModelAndView merger(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("loginUserId") Long loginUserId, @RequestParam("title") String title,
			@RequestParam("detail") String detail, @RequestParam("projectId") Long projectId,
			@RequestParam("actor") List<Long> actors) {
		Topic topic = null;
		try {
			topic = new Topic();
			topic.setTitle(title);
			topic.setDetail(detail);
			topic.setProjectId(projectId);
			topic.setUserId(loginUserId);
			topic = this.topicService.addTopic(topic, actors);
		} catch (Exception e) {
			return this.getFailedModel("创建失败");
		}

		User user = this.userService.findUserById(loginUserId);
		// 添加动态
		this.dynamicService.addPrjDynamic(user.getId(), DYNAMIC_MODULE_TYPE.TOPIC_CREATE, topic.getProjectId(),
				new Object[] { topic });
		
		actors.remove(loginUserId);
		// 添加通知
		Long[] longIds = actors.toArray(new Long[]{});
		
		this.noticeService.addNotice(user.getId(), longIds, NOTICE_MODULE_TYPE.TOPIC_ADD_MEMBER,
				new Object[] { user, topic });
		//发送邮件
		this.baseService.sendEmail(user.getId(), longIds, NOTICE_MODULE_TYPE.TOPIC_ADD_MEMBER, new Object[]{user,topic});
		
		return this.getSuccessModel(topic);
	}

	/**
	 * 
	 * @describe 查询topic的详细信息 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午5:17:57 <br>
	 * @param request
	 * @param response
	 * @param topicId
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/{topicId}", method = { RequestMethod.GET })
	public ModelAndView getTopicDetail(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long topicId, @RequestHeader(value = "loginUserId", required = true) Long loginUserId) {
		try {
			String required = (ENTITY_TYPE.TOPIC + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
			// 项目成员权限
			Map<Long, List<String>> pMapAsTopicMember = this.topicService.permissionMapAsMemberWith(required,loginUserId);
			
			
			
			Topic topic = this.topicService.getTopicSingle(topicId);
			Map<Long, List<String>> pMapAsProjectMember = this.projectService.permissionMapAsMemberWithAndOnlyByProjectId(required,loginUserId,topic.getProjectId());
			if(null==topic || topic.getDel().equals(DELTYPE.DELETED)){
				return this.getFailedModel("讨论已经删除");
			}
			
			Map<String, Object> job = this.topicService.getTopic(topicId);
			if (null != pMapAsTopicMember && pMapAsTopicMember.containsKey(topicId) || null!=pMapAsProjectMember && pMapAsProjectMember.containsKey(job.get("projectId"))) {
				List<String> pListAsTopicMember = pMapAsTopicMember.get(topicId);
				Map<String, Integer> pMap = new HashMap<>();
				if (pListAsTopicMember != null) {
					for (String p : pListAsTopicMember) {
						pMap.put(p, 1);
					}
				}
				//projectMember中的权限融入topicMember的权限中
				List<String> pListAsProjectMember = pMapAsProjectMember.get(job.get("projectId"));
				if (pListAsProjectMember != null) {
					for (String p : pListAsProjectMember) {
						pMap.put(p, 1);
					}
				}
				
				Map<String, Object> element = new HashMap<>();
				element.put("object", job);
				element.put("permissions", pMap);
				return this.getSuccessModel(element);
			} else
				return this.getFailedModel("您没有查看该讨论的权限");
			
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 更新讨论内容 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午5:17:30 <br>
	 * @param request
	 * @param response
	 * @param topicId
	 * @param topic
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/{topicId}", method = { RequestMethod.PUT })
	public ModelAndView updateTopic(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long topicId, @RequestHeader("loginUserId") Long loginUserId,
			@RequestParam("projectId") Long projectId, @RequestParam("detail") String detail,
			@RequestParam("title") String title) {
		Topic topic1 = null;
		try {
			/*
			 * 注掉此处 在web.xml中配置put请求接收参数 HashModelAndView map =
			 * URLDecoderPutParameter.getPutParameter(request, response);
			 */
			Topic topic = new Topic();
			topic.setTitle(title);
			topic.setDetail(detail);
			topic.setProjectId(projectId);

			topic1 = this.topicService.getTopicSingle(topicId);

			topic1.setTitle(topic.getTitle());
			topic1.setDetail(topic.getDetail());
			topic1.setUpdatedAt(new Timestamp(new Date().getTime()));
			topic1 = this.topicService.updateTopic(topic1);

		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TOPIC_UPDATE, projectId,
				new Object[] { topic1 });
		return this.getSuccessModel(topic1);
	}

	/**
	 * 
	 * @describe 删除讨论根据主键 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午5:16:19 <br>
	 * @param request
	 * @param response
	 * @param topicId
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/{topicId}", method = { RequestMethod.DELETE })
	public ModelAndView deleteTopic(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long topicId, @RequestHeader("loginUserId") Long loginUserId,
			@RequestParam("projectId") Long projectId) {
		try {
			this.topicService.deleteTopic(topicId);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		Topic topic = this.topicService.getTopicSingle(topicId);
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TOPIC_DELETE, projectId,
				new Object[] { topic });
		return this.getAffectModel();
	}

	/**
	 * 
	 * @describe 获取讨论列表 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午5:18:31 <br>
	 * @param request
	 * @param response
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("projectId") Long projectId, @RequestHeader("loginUserId") Long loginUserId,
			@RequestParam(value="type",required=false)List<TOPIC_MEMBER_TYPE> type,TopicQuery query) {
		try {
			List<Map<String, Object>> message = new ArrayList<>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			// 页码
			String pageNo = request.getParameter("pageNo");
			// 页尺寸
			String pageSize = request.getParameter("pageSize");
			// 我创建的 或者 我参与的
			if(null==type || type.size()<1){
				type = new ArrayList<TOPIC_MEMBER_TYPE>();
				type.add(TOPIC_MEMBER_TYPE.ACTOR);
				type.add(TOPIC_MEMBER_TYPE.SPONSOR);
				type.add(TOPIC_MEMBER_TYPE.OTHER);
			}

			int ipageNo = 0;
			int ipageSize = 20;
			if (null != pageNo && null != pageSize) {
				try {
					ipageNo = Integer.parseInt(pageNo)-1;
					ipageSize = Integer.parseInt(pageSize);
				} catch (NumberFormatException nfe) {
					return this.getFailedModel("pageNo or pageSize is illegal");
				}
			}
			
			// 项目id和用户id为空 则返回失败
			if (null == projectId || null == loginUserId || "".equals(projectId) || "".equals(loginUserId)) {
				return this.getFailedModel("projectId or loginUserId is null");
			}
			
			String required = (ENTITY_TYPE.TOPIC + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
			// 讨论成员权限
			Map<Long, List<String>> pMapAsTopicMember = this.topicService.permissionMapAsMemberWith(required,loginUserId);
			
			List<Permission> pListAsProjectMember = this.projectService.getPermissionList(loginUserId, projectId);	

			Pageable pageable = new PageRequest(ipageNo, ipageSize, Direction.DESC, "createdAt");
			map = this.topicService.getTopicList(pageable, projectId, loginUserId, type,query);
			@SuppressWarnings("unchecked")
			List<Topic> list = (List<Topic>) map.get("list");
			if(null!=list){
				for (Topic topic : list) {
					List<String> pListAsTopicMember = pMapAsTopicMember.get(topic.getId());
					Map<String, Integer> pMap = new HashMap<>();
					if (pListAsTopicMember != null) {
						for (String p : pListAsTopicMember) {
							pMap.put(p, 1);
						}
					}
					
					//projectMember中的权限融入topicMember的权限中
					if (pListAsProjectMember != null) {
						for (Permission p : pListAsProjectMember) {
							pMap.put(p.getEnName(), 1);
						}
					}
					
					Map<String, Object> element = new HashMap<>();
					element.put("object", topic);
					element.put("permissions", pMap);
					message.add(element);
				}
			}
						
			HashMap<String,Long> map1 = new HashMap<>();
			if(null!=pListAsProjectMember){
				for(Permission per : pListAsProjectMember){
					map1.put(per.getEnName(), 1L);
				}
			}
			map.put("permission", map1);
			
			map.put("list", message);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}


	/**
	 * 
	 * @describe 创建topic评论 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午5:18:09 <br>
	 * @param request
	 * @param response
	 * @param topicId
	 * @param detail
	 * @param replyTo
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/comment/{topicId}", method = { RequestMethod.POST })
	public ModelAndView postComment(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("loginUserId") Long loginUserId, TopicComment topicC,
			@RequestParam("projectId") Long projectId,
			@RequestParam(value = "resourceIds", required = false) List<Long> resourceIds) {
		try {
			topicC.setUserId(loginUserId);
			topicC = this.topicService.addComment(topicC, resourceIds);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		// 获取回复人信息
		User user = this.userService.findUserById(loginUserId);
		Topic topic = this.topicService.getTopicSingle(topicC.getTopicId());
		if (null != topicC.getReplyTo() && -1L != topicC.getReplyTo() && 0 != topicC.getReplyTo()) {
			User user1 = this.userService.findUserById(topicC.getReplyTo());
			// 添加通知
			this.noticeService.addNotice(loginUserId, new Long[] { topicC.getReplyTo() },
					NOTICE_MODULE_TYPE.TOPIC_REPLY_COMMENT, new Object[] { user, topic });
			//发送邮件
			this.baseService.sendEmail(loginUserId, new Long[] { topicC.getReplyTo() },
					NOTICE_MODULE_TYPE.TOPIC_REPLY_COMMENT, new Object[] { user, topic });
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TOPIC_COMMENT_REPLY, projectId,
					new Object[] { topic, user1 });
		} else
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TOPIC_COMMENT_CREATE, projectId,
					new Object[] { topic,topicC});
		return this.getSuccessModel(topicC);

	}

	/**
	 * 
	 * @describe 删除评论 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午5:20:54 <br>
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/comment/{topicCId}", method = { RequestMethod.DELETE })
	public ModelAndView deleteTopicComment(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("loginUserId") Long loginUserId, @PathVariable Long topicCId,
			@RequestParam("projectId") Long projectId) {
		try {
			this.topicService.deleteTopicComment(topicCId);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		// 添加动态
		TopicComment topicC = this.topicService.getTopicCommentSingle(topicCId);
		Topic topic = this.topicService.getTopicSingle(topicC.getTopicId());
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TOPIC_COMMENT_DELETE, projectId,
				new Object[] { topic,topicC });
		return this.getAffectModel();
	}

	/**
	 * 
	 * @describe 获取评论列表 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午6:27:43 <br>
	 * @param request
	 * @param response
	 * @param topicId
	 * @param pageNo
	 * @param pageSize
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/comment/list/{topicId}", method = { RequestMethod.GET })
	public ModelAndView getComment(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long topicId) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String topicCId = request.getParameter("topicCId");
			String pageNo = request.getParameter("pageNo");
			String pageSize = request.getParameter("pageSize");
			String loginUserId = request.getHeader("loginUserId");
//			if (!this.topicService.isExistUserInMember(topicId, Long.parseLong(loginUserId))) {
//				return this.getFailedModel("this loginUserId have no rights");
//			}
			int ipageNo = 1;
			int ipageSize = 15;
			Long itopicCId = (long) 0;
			if (null != pageNo && null != pageSize) {
				try {
					ipageNo = Integer.parseInt(pageNo);
					ipageSize = Integer.parseInt(pageSize);
				} catch (NumberFormatException nfe) {
					return this.getFailedModel("pageNo or pageSize is illegal");
				}
			}
			if (null != topicCId) {
				try {
					itopicCId = Long.parseLong(topicCId);
				} catch (NumberFormatException nfe) {
					return this.getFailedModel("topicCId is illegal");
				}
			}

			map = this.topicService.getTopicComment(topicId, itopicCId, ipageNo, ipageSize);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}

	}


	/**
	 * 
	 * @describe 查询topic所有参与者 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午5:17:57 <br>
	 * @param request
	 * @param response
	 * @param topicId
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/member/{topicId}", method = { RequestMethod.GET })
	public ModelAndView getTopicMember(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long topicId) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();

			map = this.topicService.getTopicMember(topicId);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}

	}

	/**
	 * 
	 * @describe 添加讨论参与者 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月22日 下午2:48:18 <br>
	 * @param request
	 * @param response
	 * @param topicId
	 * @param userId
	 * @return <br>
	 * @returnType Map<?,?>
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/member/{topicId}", method = { RequestMethod.PUT })
	public ModelAndView addMember(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @PathVariable(value = "topicId") Long topicId,
			@RequestParam(value = "userId") Long userId, @RequestParam("projectId") Long projectId) {
		TopicMember member = null;
		try {
			member = this.topicService.addMember(topicId, userId);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}

		User user = this.userService.findUserById(loginUserId);

		// 获取回复人信息
		User user1 = this.userService.findUserById(userId);
		Topic topic = this.topicService.getTopicSingle(topicId);
		this.noticeService.addNotice(loginUserId, new Long[] { user1.getId() }, NOTICE_MODULE_TYPE.TOPIC_ADD_MEMBER,
				new Object[] { user, topic });
		//发送邮件
		this.baseService.sendEmail(loginUserId, new Long[] { user1.getId() }, NOTICE_MODULE_TYPE.TOPIC_ADD_MEMBER,
				new Object[] { user, topic });
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TOPIC_MEMBER_ADD, projectId,
				new Object[] { user1, topic });

		return this.getSuccessModel(member);

	}

	/**
	 * 
	 * @describe 移除讨论参与者 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月21日 下午7:22:28 <br>
	 * @return <br>
	 * @returnType Map<?,?>
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/member/{topicId}", method = { RequestMethod.DELETE })
	public ModelAndView removeMember(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @RequestParam("projectId") Long projectId,
			@PathVariable(value = "topicId") Long topicId, @RequestParam(value = "userId") Long userId) {
		int a = 0;
		try {
			a = this.topicService.deleteMember(topicId, userId);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		User user = this.userService.findUserById(userId);
		User user1 = this.userService.findUserById(loginUserId);
		Topic topic = this.topicService.getTopicSingle(topicId);
		this.noticeService.addNotice(loginUserId, new Long[] { user.getId() }, NOTICE_MODULE_TYPE.TOPIC_REMOVE_MEMBER,
				new Object[] { user1, topic });
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TOPIC_MEMBER_DELETE, projectId,
				new Object[] { topic, user1 });
		if(a==1){
			return this.getAffectModel();
		}else{
			return this.getFailedModel(0);
		}

	}


	/**
	 * 
	 * @describe 添加topic资源 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月31日 下午6:42:10 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param projectId
	 * @param topicId
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "/resource/{topicCId}", method = { RequestMethod.POST })
	public ModelAndView addTopicResource(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @RequestParam("projectId") Long projectId,
			@PathVariable(value = "topicCId") Long topicCId, TopicResource topicResource) {
		try {
			topicResource.setTopicCId(topicCId);
			topicResource = this.topicResourceService.addResource(topicResource);
			return this.getSuccessModel(topicResource);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 删除topic资源 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月31日 下午6:52:33 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param projectId
	 * @param resourceId
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "/resource/{resourceId}", method = { RequestMethod.DELETE })
	public ModelAndView deleteTopicResource(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @RequestParam("projectId") Long projectId,
			@PathVariable(value = "resourceId") Long resourceId) {
		try {
			this.topicResourceService.deleteTopicResource(resourceId);
			return this.getAffectModel();
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

}
