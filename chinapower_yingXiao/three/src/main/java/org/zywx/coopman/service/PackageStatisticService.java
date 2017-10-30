package org.zywx.coopman.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.zywx.coopman.commons.Enums.AppPackageBuildStatus;
import org.zywx.coopman.entity.QueryEntity;

@Service
public class PackageStatisticService extends BaseService {

	public List<Map<String, Object>> getpackageBuildInfo(QueryEntity queryEntity) {
		Pageable page = new PageRequest(queryEntity.getPageNo() - 1, queryEntity.getPageSize(), Direction.DESC, "id");

		//返回结果：date、failed、all;
		String sqlWithOutTime = "select da.* from (select date(createdAt) 'dateTime',(select count(*) from T_APP_PACKAGE ta where date(tap.createdAt) = date(ta.createdAt)) 'allCount',count(*) 'failedCount' from T_APP_PACKAGE tap where tap.buildStatus= %d group by date(tap.createdAt) order by tap.createdAt desc) da limit %d,%d";
		String sqlWithTime = "select da.* from (select date(createdAt) 'dateTime',count(*) 'failedCount',(select count(*) from T_APP_PACKAGE ta where date(tap.createdAt) = date(ta.createdAt)) 'allCount'  from T_APP_PACKAGE tap where tap.buildStatus= %d AND tap.createdAt > '%s' AND tap.createdAt < '%s' group by date(tap.createdAt) order by tap.createdAt desc) da limit %d,%d";
		List<Map<String, Object>> list ;
		if (queryEntity.getEndTime() == null) {
			list = this.jdbcTpl.queryForList(String.format(sqlWithOutTime, AppPackageBuildStatus.FAILED.ordinal(),queryEntity.getStartNum(),queryEntity.getPageSize()));
		} else {
			list = this.jdbcTpl.queryForList(String.format(sqlWithTime, AppPackageBuildStatus.FAILED.ordinal(),
					queryEntity.getStartTime(), queryEntity.getEndTime(),queryEntity.getStartNum(),queryEntity.getPageSize()));
		}
		return list;
	}

	public List<Map<String, Object>> getpackageBuildInfo(String date) {
		String sqlWithDate = "select date_format(tap.createdAt,'%Y-%m-%d %H:%i:%s') 'dateTime',tap.buildLogUrl,userId,tu.account from T_APP_PACKAGE tap left join T_USER tu on tap.userId = tu.id where tap.buildStatus= "+AppPackageBuildStatus.FAILED.ordinal()+" and date(tap.createdAt) = '"+date+"' order by tap.createdAt desc";
		
		List<Map<String, Object>> list ;
		if (date != null) {
			list = this.jdbcTpl.queryForList(sqlWithDate);
		}else
			list = null;
		
		return list;
	}

	@SuppressWarnings("deprecation")
	public Long getCountPackageBuildInfo(QueryEntity queryEntity) {
		String sqlWithOutTime = "select count(*) from (select date(createdAt) 'dateTime',(select count(*) from T_APP_PACKAGE ta where date(tap.createdAt) = date(ta.createdAt)) 'allCount',count(*) 'failedCount' from T_APP_PACKAGE tap where tap.buildStatus= %d group by date(tap.createdAt) order by tap.createdAt desc) tt";
		String sqlWithTime = "select count(*) from (select date(createdAt) 'dateTime',count(*) 'failedCount',(select count(*) from T_APP_PACKAGE ta where date(tap.createdAt) = date(ta.createdAt)) 'allCount'  from T_APP_PACKAGE tap where tap.buildStatus= %d AND tap.createdAt > '%s' AND tap.createdAt < '%s' group by date(tap.createdAt) order by tap.createdAt desc) tt";
		Long list ;
		if (queryEntity.getEndTime() == null) {
			list = this.jdbcTpl.queryForLong(String.format(sqlWithOutTime, AppPackageBuildStatus.FAILED.ordinal()));
		} else {
			list = this.jdbcTpl.queryForLong(String.format(sqlWithTime, AppPackageBuildStatus.FAILED.ordinal(),
					queryEntity.getStartTime(), queryEntity.getEndTime()));
		}
		return list;
	}

}
