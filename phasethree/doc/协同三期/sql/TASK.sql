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
	`detail` VARCHAR(1000) NOT NULL COMMENT '��������ϸ��Ϣ',
	`processId` bigint(20) NOT NULL COMMENT '��������������ID',
	`appId` bigint(20) NOT NULL  DEFAULT -1 COMMENT '����������Ӧ��ID',
	`deadline` date NOT NULL,
	`status` int(1) NOT NULL,
	`lastStatusUpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '���״̬�ı�ʱ��',
    `createdAt` datetime NOT NULL,
	`updatedAt` timestamp NOT NULL,  
	`del` int(1) NOT NULL,
  `topTaskId` bigint(20) NOT NULL,  
  `finishDate` datetime DEFAULT NULL,
  `finishUserId` bigint(20) NOT NULL DEFAULT -1,
  `managerUserId` bigint(20) NOT NULL COMMENT '����������ID',
  PRIMARY KEY (`id`),
  KEY `INDEX_TOPTASKID` (TOPTASKID),
  KEY `INDEX_LEADERUSERID` (managerUserId)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



update T_TASK set finishDate = closeDate  where closeDate is not NULL;
/**������һ���ֶ�oldStatus��ԭ�ȵ�status״̬��������,��һ�Ժ�����**/
ALTER TABLE T_TASK ADD oldStatus int(1) default -1;
update T_TASK set oldStatus=status;
ALTER TABLE T_TASK CHANGE status groupId bigint(20) NOT NULL;
ALTER TABLE T_TASK ADD status int(1) NOT NULL;

/**��ԭ�ȷǹرյ����񶼸�Ϊδ���״̬,�رյ������Ϊ�����״̬.(֮��״ֻ̬��δ���,���������)**/
update T_TASK set status =0 where groupId!=5;  /**(ԭ��5���ѹر�,���Խ����ѹرյĶ���Ϊ���ڵ�0δ���)**/

update T_TASK set status=1 where groupId=5; /**���ѹرյ�״̬��Ϊ�����.**/

/**ȥ�����������ֶ�**/
update T_TASK set detail = CONCAT(name ,',',detail) ;
ALTER TABLE T_TASK DROP name;


DROP TABLE IF EXISTS `T_TASK_GROUP_SORT`;
CREATE TABLE `T_TASK_GROUP_SORT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
	`projectId` bigint(20) NOT NULL COMMENT '��ĿID',
	`userId` bigint(20) NOT NULL  COMMENT '�û�ID',
	`groupId` bigint(20) NOT NULL COMMENT '�������ID',
	`sort` int(2) NOT NULL,
    `createdAt` datetime NOT NULL,
	`updatedAt` timestamp NOT NULL default CURRENT_TIMESTAMP,  
	`del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
	 KEY `PROJECTID_USERID` (`projectId`,`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



/**ALTER TABLE T_TASK_STATUS_SORT CHANGE `status` `group` BIGINT(20) NOT NULL COMMENT '�������ID';**/


/**ÿ���˵������������
SELECT g.* FROM t_task_group  g
	left join 
 	t_task_group_sort s on  g.id=s.`groupId` 
 where g.del=0 and g.projectId=1 
 	and s.userId=178 and s.projectId=178 
 	order by s.sort
**/
 	
/**ɾ����  T_MAN_TASK_CONFIG   t_task_sort**/

 	
 	ALTER TABLE T_TASK drop lastModifyUserId;
 	ALTER TABLE T_TASK drop lastModifyDate;
 	ALTER TABLE T_TASK drop closeUserId;
 	ALTER TABLE T_TASK drop closeDate;
 	
 	
/**��̬**/
INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>��<span>%s</span>�������������','TASK_LEAF_ADD');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>��������<span>%s</span>�ĸ������޸�Ϊ<span>%s</span>','TASK_LEAF_CHANGE_MANAGER');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>ɾ����������<span>%s</span>','TASK_LEAF_REMOVE');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>��������<span>%s</span>ת��Ϊ����','TASK_LEAF_UPGRADE');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>������������<span>%s</span>','TASK_GROUP_ADD');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>ɾ�����������<span>%s</span>','TASK_GROUP_REMOVE');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>ɾ�����������<span>%s</span>,�ҽ������µ�����ת����<span>%s</span>��','TASK_GROUP_REMOVE_AND_TRANSFER');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>��<span>%s</span>���޸���������','TASK_LEAF_UPDATE');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>�����������','TASK_LEAF_FINISHED');

INSERT INTO `T_DYNAMIC_MODULE`(del,formatStr,moduleType) VALUES (0,'<span>%s</span>������������','TASK_LEAF_UNFINISHED');

update T_DYNAMIC_MODULE set formatStr='<span>%s</span>���´�������<span>%s</span>' where moduleType = 'TASK_REJECT';

/**֪ͨ,�ʼ�**/
insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>�������������<span class="noticeClick">%s</span>��,�����ֹʱ��Ϊ:<span>%s</span>,��֪����','TASK_ADD_TO_LEADER');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>�����������񣺡�<span class="noticeClick">%s</span>��,�����ֹʱ��Ϊ:<span>%s</span>,��֪����','TASK_ADD_TO_MEMBER');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>���������������񣺡�<span class="noticeClick">%s</span>��,�����ֹʱ��Ϊ:<span>%s</span>,��֪����','TASK_LEAF_ADD_TO_LEADER');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>�Ѿ������ӡ�<span>%s</span>�����Ƴ������Ѳ���Ҫ��ע������','TASK_REMOVE_TO_MEMBER');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>����ˡ�<span>%s</span>������Ҫȷ��������������','TASK_LEAF_FINISHED_TO_CREATOR');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>����ˡ�<span>%s</span>���������񼴽�����,����Ҫȷ��������������','TASK_LEAF_FINISHED_TO_CREATOR_WARNING');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>����ˡ�<span>%s</span>����������������,����Ҫȷ������������','TASK_LEAF_FINISHED_TO_CREATOR_OVERDUE');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'<span>%s</span>�޸��ˡ�<span>%s</span>������״̬������Ҫ������ɴ�����','TASK_UNFINISHED');



/**��������**/

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'�������ġ�<span>%s</span>���������ڣ���֪����','TASK_WARNING_TO_CREATOR');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'�������ġ�<span>%s</span>�������ڣ���֪����','TASK_OVERDUE_TO_CREATOR');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'��������<span>%s</span>����������,����Ҫ������ɴ�����','TASK_WARNING_TO_PARTICIPATOR');

insert into T_NOTICE_MODULE(del,noFormatStr,noModuleType)
values (0,'��������<span>%s</span>�������ڣ�����Ҫ������ɴ�����','TASK_OVERDUE_TO_PARTICIPATOR');

