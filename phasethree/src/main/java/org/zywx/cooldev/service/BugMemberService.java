package org.zywx.cooldev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.BUG_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_MEMBER_TYPE;
import org.zywx.cooldev.dao.bug.BugAuthDao;
import org.zywx.cooldev.dao.bug.BugMemberDao;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.bug.BugAuth;
import org.zywx.cooldev.entity.bug.BugMember;
import org.zywx.cooldev.entity.task.TaskAuth;
import org.zywx.cooldev.entity.task.TaskMember;
import org.zywx.cooldev.system.Cache;

/**
 * bugMember service
 * 
 * @author yongwen.wang
 * 
 */
@Service
public class BugMemberService extends BaseService {
	@Autowired
	private BugMemberDao bugMemberDao;
	@Autowired
	private BugAuthDao bugAuthDao;

	public BugMember getBugAssignedPerson(long bugId) {
		String roleName = ENTITY_TYPE.BUG + "_" + ROLE_TYPE.ASSIGNEDPERSON;
		Role role = Cache.getRole(roleName);
		Long roleId = role.getId();
		BugMember member = this.bugMemberDao.findByBugIdAndRoleIdAndDel(bugId,
				roleId, DELTYPE.NORMAL);
		return member;
	}

	public synchronized int editBugAssignedPerson(long bugId, long assignedPersonUserId) {
		User user=userDao.findByIdAndDel(assignedPersonUserId,DELTYPE.NORMAL);
		if(user==null){
			log.info("assignedPersonUserId===>"+assignedPersonUserId);
			throw new RuntimeException("未解决或解决异常，无解决人，不能激活");
		}
		String roleName = ENTITY_TYPE.BUG + "_" + ROLE_TYPE.ASSIGNEDPERSON;
		Role role = Cache.getRole(roleName);
		List<BugMember> members = bugMemberDao.findByBugIdAndDel(bugId,
				DELTYPE.NORMAL);
		for (BugMember member : members) {
			List<BugAuth> authes = bugAuthDao.findByMemberIdAndDel(
					member.getId(), DELTYPE.NORMAL);
			for (BugAuth auth : authes) {
				if (auth.getRoleId() == role.getId()) {
					//删除原来的指派人
					List<BugAuth> oldAssignedAuth = bugAuthDao.findByMemberIdAndDel(auth.getMemberId(), DELTYPE.NORMAL);
					//如果没有其他角色了，需要删除member表的记录
					if(oldAssignedAuth.size()==1){
						bugMemberDao.delete(member);
					}
				    auth.setDel(DELTYPE.DELETED);
					bugAuthDao.save(auth);
					//新的负责人以前是否在此任务下的成员中
					BugMember targetMember = bugMemberDao
							.findByBugIdAndUserIdAndDel(bugId,
									assignedPersonUserId, DELTYPE.NORMAL);
					//新的指派者不在bug成员中
					if (null == targetMember) {
						BugMember bugAssignedPerson = new BugMember();
						bugAssignedPerson.setBugId(member.getBugId());
						bugAssignedPerson.setType(BUG_MEMBER_TYPE.PARTICIPATOR);
						bugAssignedPerson.setUserId(assignedPersonUserId);
						bugMemberDao.save(bugAssignedPerson);
						BugAuth taManager = new BugAuth();
						taManager.setMemberId(bugAssignedPerson.getId());
						taManager.setRoleId(role.getId());
						bugAuthDao.save(taManager);
					} else {
						BugAuth taManager = new BugAuth();
						taManager.setMemberId(targetMember.getId());
						taManager.setRoleId(role.getId());
						bugAuthDao.save(taManager);
					}
				}
			}
		}
		return 1;
	}

	public void removeBugMember(long memberId, long loginUserId) {
		BugMember bugMember = this.bugMemberDao.findOne(memberId);
		List<BugAuth> listBugAuth = this.bugAuthDao.findByMemberIdAndDel(
				bugMember.getId(), DELTYPE.NORMAL);
		if (null != listBugAuth && listBugAuth.size() > 0) {
			String roleName = ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER;
			Role role = Cache.getRole(roleName);
			if (listBugAuth.size() == 1) {
				// 只有一个角色的情况下:如果此角色还是普通成员,则需要连成员taskMember记录也删除
				if (listBugAuth.get(0).getRoleId() == role.getId()) {
					bugMember.setDel(DELTYPE.DELETED);
					bugMemberDao.save(bugMember);
				}
			}
			for (BugAuth bugAuth : listBugAuth) {
				if (bugAuth.getRoleId() == role.getId()) {
					bugAuth.setDel(DELTYPE.DELETED);
					this.bugAuthDao.save(bugAuth);
				}
			}
		}

	}

	public BugMember addBugMember(BugMember member, Long loginUserId) {

		User user = userDao.findOne(member.getUserId());
		if (user != null) {
			member.setUserIcon(user.getIcon());
			member.setUserName(user.getUserName());
		}
		List<BugMember> list = bugMemberDao.findByBugIdAndDel(
				member.getBugId(), DELTYPE.NORMAL);
		int i = 0;
		if (null != list && list.size() > 0) {
			for (BugMember bugMember : list) {
				if (bugMember.getUserId() == member.getUserId()) {
					// 这个人是创建者,或者指派人
					BugAuth bugAuth = new BugAuth();
					bugAuth.setMemberId(bugMember.getId());
					String roleName = ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER;
					Role role = Cache.getRole(roleName);
					bugAuth.setRoleId(role.getId());
					bugAuthDao.save(bugAuth);
					break;
				}
				i++;
			}
		}

		if (null == list || i == list.size()) {
			// 新添加的人,以前不是该bug的相关人员
			bugMemberDao.save(member);
			BugAuth ta = new BugAuth();
			ta.setMemberId(member.getId());
			String roleName = ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER;
			Role role = Cache.getRole(roleName);
			ta.setRoleId(role.getId());
			bugAuthDao.save(ta);
		}

		return member;
	}

}
