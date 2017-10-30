DROP TABLE IF EXISTS `T_TASK_SURVEY`;
CREATE TABLE `T_TASK_SURVEY` (
  	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`projectId` bigint(20) NOT NULL COMMENT '任务ID',
	`managerUserId` bigint(20) NOT NULL COMMENT '用户ID',	
	`taskAt`  varchar(10) NOT NULL COMMENT '任务日期',
	`stockNum` bigint(20) NOT NULL COMMENT '存量任务数',
	`addNum` bigint(20) NOT NULL COMMENT '新增任务数',
	`completeNum` bigint(20) NOT NULL COMMENT '已完成任务数',
  PRIMARY KEY (`id`),
  KEY `INDEX_TOPTASKID` (projectId)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `T_BUG_SURVEY`;
CREATE TABLE `T_BUG_SURVEY` (
  	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`projectId` bigint(20) NOT NULL COMMENT '任务ID',
	`managerUserId` bigint(20) NOT NULL COMMENT '用户ID',	
	`bugAt`  varchar(10) NOT NULL COMMENT '任务日期',
	`stockNum` bigint(20) NOT NULL COMMENT '存量任务数',
	`addNum` bigint(20) NOT NULL COMMENT '新增任务数',
	`completeNum` bigint(20) NOT NULL COMMENT '已完成任务数',
  PRIMARY KEY (`id`),
  KEY `INDEX_TOPTASKID` (projectId)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS `T_TIMEDATE`;
CREATE TABLE `T_TIMEDATE` (
`timeDate`  varchar(10) NOT NULL COMMENT '任务ID'
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into T_TIMEDATE(timedate)
select * from (
SELECT
	adddate(
		(
			DATE_FORMAT("2016-01-01", '%Y-%m-%d')
		),
		numlist.id
	) AS `date`
FROM
	(
		SELECT
			n1.i + n10.i * 10 + n100.i * 100 + n1000.i * 1000 AS id
		FROM
			 (select 0 as i union  all select 1 union  all select 2 union  all select 3 union  all select 4 union  all select 5 union  all select 6 union  all select 7 union  all select 8 union  all select 9) AS n1
		CROSS JOIN (select 0 as i union  all select 1 union  all select 2 union  all select 3 union  all select 4 union  all select 5 union  all select 6 union  all select 7 union  all select 8 union  all select 9) AS n10
		CROSS JOIN (select 0 as i union  all select 1 union  all select 2 union  all select 3 union  all select 4 union  all select 5 union  all select 6 union  all select 7 union  all select 8 union  all select 9) AS n100
		CROSS JOIN (select 0 as i union  all select 1 union  all select 2 union  all select 3 union  all select 4 union  all select 5 union  all select 6 union  all select 7 union  all select 8 union  all select 9) AS n1000
	) AS numlist
) dateList;

DROP TABLE IF EXISTS `T_USERCHOICED`;
CREATE TABLE `T_USERCHOICED` (
`projectId` bigint(20) NOT NULL COMMENT '任务ID',
`loginUserId`   bigint(20) NOT NULL COMMENT '登陆人ID',
`userId`  varchar(100) NOT NULL COMMENT '被选中的成员ID',
 PRIMARY KEY (`projectId`,`loginUserId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `T_MEMBER_COMPLETE`;
CREATE TABLE `T_MEMBER_COMPLETE` (
`projectId` bigint(20) NOT NULL COMMENT '任务ID',
`loginUserId`   bigint(20) NOT NULL COMMENT '登陆人ID',
`userId`  varchar(100) NOT NULL COMMENT '被选中的成员ID',
`chartContent`   char(1) NOT NULL COMMENT '图表内容',
 PRIMARY KEY (`projectId`,`loginUserId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

