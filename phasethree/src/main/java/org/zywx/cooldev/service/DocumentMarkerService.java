package org.zywx.cooldev.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.document.DocumentMarker;

@Service
public class DocumentMarkerService extends AuthService{

	@Override
	public List<Permission> getPermissionList(long loginUserId, long entityId) {
		return null;
	}

	/**
	 * 
	 * @describe 添加文档标记	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年8月26日 下午6:06:18	<br>
	 * @param loginUserId
	 * @param docM  <br>
	 * @returnType void
	 *
	 */
	public DocumentMarker addMarker(Long loginUserId, DocumentMarker docM) {
		User user= this.userDao.findOne(loginUserId);
		docM.setUserName(user.getUserName());
		docM = this.documentMarkerDao.save(docM);
		return docM;
	}

	/**
	 * 
	 * @describe 获取文档批注列表	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年8月28日 下午6:28:13	<br>
	 * @param docC
	 * @param loginUserId
	 * @param target 
	 * @return  <br>
	 * @returnType List<DocumentMarker>
	 *
	 */
	public List<DocumentMarker> getMarkerByDocCIdAndUserId(Long docCId, Long loginUserId, String target) {
		List<DocumentMarker> list = this.documentMarkerDao.findByDocCIdAndUserIdAndTargetAndDelOrderByCreatedAtDesc(docCId,loginUserId,target,DELTYPE.NORMAL);
		return list;
	}

	/**
	 * 
	 * @describe 删除文档批注	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年8月28日 下午6:28:00	<br>
	 * @param docMId  <br>
	 * @returnType void
	 *
	 */
	public void deleteDocumentMarker(Long docMId) {
		this.documentMarkerDao.delete(docMId);
	}

	public int getCountByDocCId(Long docCId, Long loginUserId, String target) {
		String sql = "select count(*) from T_DOCUMENT_MARKER tdm where tdm.userId =? and tdm.docCId =? and tdm.target = ? and tdm.del =? ";
		@SuppressWarnings("deprecation")
		int count = this.jdbcTpl.queryForInt(sql, new Object[]{loginUserId,docCId,target,DELTYPE.NORMAL});
		return count;
		
	}

	public DocumentMarker findOne(Long docMId) {
		DocumentMarker docM = this.documentMarkerDao.findOne(docMId);
		return docM;
	}

}
