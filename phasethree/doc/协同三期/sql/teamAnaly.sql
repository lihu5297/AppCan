DROP TABLE IF EXISTS `T_TEAM_ANALY`;
CREATE TABLE `T_TEAM_ANALY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `teamId` bigint(20) NOT NULL,
	`projectId` bigint(20) NOT NULL,
  `createdAt` datetime NOT NULL,
	`updatedAt` datetime NOT NULL,  
	`del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_TEAMID` (`teamId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


/**初始化项目完成时间**/
update T_PROJECT pj,T_PROCESS pc set pj.finishDate=(select max(endDate) from T_PROCESS where T_PROCESS.projectId=pj.id and T_PROCESS.del=0) where pj.id=pc.projectId and pj.status=0 and pj.del=0