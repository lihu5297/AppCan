DROP TABLE IF EXISTS `T_TASK_GROUP`;
CREATE TABLE `T_TASK_GROUP` (
  	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL,
    `createdAt` datetime NOT NULL,
	`updatedAt` timestamp NOT NULL default CURRENT_TIMESTAMP,  
	`del` int(1) NOT NULL,
	`projectId` bigint(20) NOT NULL,
	`sort` int(11) default null,
  PRIMARY KEY (`id`),
  UNIQUE KEY(`projectId`,`name`,`del`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `T_TASK_LEAF`;
CREATE TABLE `T_TASK_LEAF` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
	`detail` VARCHAR(1000) NOT NULL COMMENT '子任务详细信息',
	`processId` bigint(20) NOT NULL COMMENT '子任务所在流程ID',
	`appId` bigint(20) NOT NULL  DEFAULT -1 COMMENT '子任务所属应用ID',
	`deadline` date NOT NULL,
	`status` int(1) NOT NULL,
	`lastStatusUpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后状态改变时间',
    `createdAt` datetime NOT NULL,
	`updatedAt` timestamp NOT NULL,  
	`del` int(1) NOT NULL,
  `topTaskId` bigint(20) NOT NULL,  
  `finishDate` datetime DEFAULT NULL,
  `finishUserId` bigint(20) NOT NULL DEFAULT -1,
  `managerUserId` bigint(20) NOT NULL COMMENT '子任务负责人ID',
  PRIMARY KEY (`id`),
  KEY `INDEX_TOPTASKID` (TOPTASKID),
  KEY `INDEX_LEADERUSERID` (managerUserId)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



update T_TASK set finishDate = closeDate  where closeDate is not NULL;
/**增加了一个字段oldStatus将原先的status状态保存起来,万一以后有用**/
ALTER TABLE T_TASK ADD oldStatus int(1) default -1;
update T_TASK set oldStatus=status;
ALTER TABLE T_TASK CHANGE status groupId bigint(20) NOT NULL;
ALTER TABLE T_TASK ADD status int(1) NOT NULL;

/**将原先非关闭的任务都改为未完成状态,关闭的任务改为已完成状态.(之后状态只有未完成,已完成两种)**/
update T_TASK set status =0 where groupId!=5;  /**(原先5是已关闭,所以讲非已关闭的都改为现在的0未完成)**/

update T_TASK set status=1 where groupId=5; /**将已关闭的状态改为已完成.**/

/**去掉任务名称字段**/
update T_TASK set detail = CONCAT(name ,',',detail) ;
ALTER TABLE T_TASK DROP name;


DROP TABLE IF EXISTS `T_TASK_GROUP_SORT`;
CREATE TABLE `T_TASK_GROUP_SORT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
	`projectId` bigint(20) NOT NULL COMMENT '项目ID',
	`userId` bigint(20) NOT NULL  COMMENT '用户ID',
	`groupId` bigint(20) NOT NULL COMMENT '任务分组ID',
	`sort` int(2) NOT NULL,
    `createdAt` datetime NOT NULL,
	`updatedAt` timestamp NOT NULL default CURRENT_TIMESTAMP,  
	`del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
	 KEY `PROJECTID_USERID` (`projectId`,`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



/**ALTER TABLE T_TASK_STATUS_SORT CHANGE `status` `group` BIGINT(20) NOT NULL COMMENT '任务分组ID';**/


/**每个人的任务分组排序
SELECT g.* FROM t_task_group  g
	left join 
 	t_task_group_sort s on  g.id=s.`groupId` 
 where g.del=0 and g.projectId=1 
 	and s.userId=178 and s.projectId=178 
 	order by s.sort
**/
 	
/**删除表  T_MAN_TASK_CONFIG   t_task_sort**/

 	
 	ALTER TABLE T_TASK drop lastModifyUserId;
 	ALTER TABLE T_TASK drop lastModifyDate;
 	ALTER TABLE T_TASK drop closeUserId;
 	ALTER TABLE T_TASK drop closeDate;
 	
 	
/**动态**/
INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>在<span>%s</span>中添加了子任务','TASK_LEAF_ADD');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>将子任务<span>%s</span>的负责人修改为<span>%s</span>','TASK_LEAF_CHANGE_MANAGER');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>删除了子任务<span>%s</span>','TASK_LEAF_REMOVE');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>将子任务<span>%s</span>转化为任务','TASK_LEAF_UPGRADE');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>添加了任务分组<span>%s</span>','TASK_GROUP_ADD');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>删除了任务分组<span>%s</span>','TASK_GROUP_REMOVE');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>删除了任务分组<span>%s</span>,且将该组下的任务转移至<span>%s</span>中','TASK_GROUP_REMOVE_AND_TRANSFER');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>在<span>%s</span>中修改了子任务','TASK_LEAF_UPDATE');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>完成了子任务','TASK_LEAF_FINISHED');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>驳回了子任务','TASK_LEAF_UNFINISHED');

update T_DYNAMIC_MODULE set formatStr='<span>%s</span>重新打开了任务<span>%s</span>' where moduleType = 'TASK_REJECT';

/**通知,邮件**/
insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>给你分配了任务【<span class="noticeClick">%s</span>】,任务截止时间为:<span>%s</span>,请知晓。','TASK_ADD_TO_LEADER');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>邀您参与任务：【<span class="noticeClick">%s</span>】,任务截止时间为:<span>%s</span>,请知晓。','TASK_ADD_TO_MEMBER');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>给您分配了子任务：【<span class="noticeClick">%s</span>】,任务截止时间为:<span>%s</span>,请知晓。','TASK_LEAF_ADD_TO_LEADER');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>已经将您从【<span>%s</span>】中移除，您已不需要关注此任务。','TASK_REMOVE_TO_MEMBER');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>完成了【<span>%s</span>】您需要确认任务完成情况。','TASK_LEAF_FINISHED_TO_CREATOR');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>完成了【<span>%s</span>】，现任务即将延期,您需要确认任务完成情况。','TASK_LEAF_FINISHED_TO_CREATOR_WARNING');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>完成了【<span>%s</span>】，现任务已延期,您需要确认任务完成情况','TASK_LEAF_FINISHED_TO_CREATOR_OVERDUE');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>修改了【<span>%s</span>】任务状态，您需要重新完成此任务。','TASK_UNFINISHED');



/**延期提醒**/

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'您创建的【<span>%s</span>】即将延期，请知晓。','TASK_WARNING_TO_CREATOR');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'您创建的【<span>%s</span>】已延期，请知晓。','TASK_OVERDUE_TO_CREATOR');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'您的任务【<span>%s</span>】即将延期,您需要尽快完成此任务。','TASK_WARNING_TO_PARTICIPATOR');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'您的任务【<span>%s</span>】已延期，您需要尽快完成此任务。','TASK_OVERDUE_TO_PARTICIPATOR');

