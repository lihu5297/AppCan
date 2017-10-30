package org.zywx.cooldev.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.TeamAnaly;

public interface TeamAnalyDao extends PagingAndSortingRepository<TeamAnaly, Long> {

	TeamAnaly findByTeamIdAndProjectIdAndDel(long teamId, long projectId,
			DELTYPE normal);

	List<TeamAnaly> findByTeamIdAndDel(long teamId, DELTYPE normal);



}
