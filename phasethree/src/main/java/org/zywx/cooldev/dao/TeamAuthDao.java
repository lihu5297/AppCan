package org.zywx.cooldev.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.TeamAuth;

public interface TeamAuthDao extends PagingAndSortingRepository<TeamAuth, Long> {

	public TeamAuth findByMemberIdAndDel(Long memberId,DELTYPE delType);
	
	public TeamAuth findByMemberIdAndRoleIdAndDel(Long memberId,long roleId,DELTYPE delType);
}
