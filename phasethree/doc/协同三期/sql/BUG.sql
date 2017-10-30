DROP TABLE IF EXISTS `T_BUG_MODULE`;
CREATE TABLE `T_BUG_MODULE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
	`projectId` bigint(20) NOT NULL,
  `createdAt` datetime NOT NULL,
	`updatedAt` datetime NOT NULL,  
	`del` int(1) NOT NULL,
	`managerId` bigint(20) NOT NULL COMMENT '������',
	`creatorId` bigint(20) NOT NULL COMMENT '������',
  PRIMARY KEY (`id`),
  UNIQUE KEY(`projectId`,`name`),
  KEY `INDEX_PROJECTID` (`projectId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `T_BUG`;
CREATE TABLE `T_BUG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(1000) NOT NULL,
	`detail` VARCHAR(1000) NOT NULL,
	`processId` bigint(20) NOT NULL,
	`appId` bigint(20) NOT NULL DEFAULT -1,
	`status` int(1) NOT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`del` int(1) NOT NULL,
  `moduleId` bigint(20) NOT NULL DEFAULT -1 COMMENT 'ģ��ID',  
  `priority` int(1) NOT NULL COMMENT '���ȼ�',
  `affectVersion` varchar(50) DEFAULT NULL COMMENT 'Ӱ��汾',
  `resolveVersion` varchar(50) DEFAULT NULL COMMENT '����汾',
  `resolveUserId` bigint(20) NOT NULL DEFAULT -1  COMMENT 'bug�����',
  `solution` int(1) DEFAULT NULL COMMENT '�������',
  `resolveAt` datetime DEFAULT NULL COMMENT '���ʱ��',
  `closeAt` datetime DEFAULT NULL COMMENT '�ر�ʱ��',
  `closeUserId` bigint(20) NOT NULL DEFAULT -1  COMMENT '�رղ�����',
  `lastModifyUserId` bigint(20) NOT NULL COMMENT '��������',
  PRIMARY KEY (`id`),
  KEY `INDEX_PROCESSID` (`processId`),
  KEY `INDEX_CLOSEUSERID_LASTMODIFYUSERID` (`closeUserId`,`lastModifyUserId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `T_BUG_MARK`;

CREATE TABLE `T_BUG_MARK` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bugId` bigint(20)  NOT NULL,
	`info` VARCHAR(500) NOT NULL,
	`userId` bigint(20) NOT NULL,
  `createdAt` datetime NOT NULL,
	`updatedAt` timestamp NOT NULL default CURRENT_TIMESTAMP,  
	`del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_BUGID` (bugId)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `T_BUG_MEMBER`;
CREATE TABLE `T_BUG_MEMBER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime NOT NULL,
  `del` int(1) NOT NULL,
  `updatedAt` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `bugId` bigint(20) NOT NULL,
  `type` int(1) DEFAULT NULL COMMENT '0,������;1,ָ����;2,������',
  `userId` bigint(20) NOT NULL COMMENT '�û�ID',
  PRIMARY KEY (`id`),
  KEY `INDEX_BUGID_USERID` (`bugId`,`userId`),
  KEY `INDEX_USERID` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `T_BUG_AUTH`;
CREATE TABLE `T_BUG_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime NOT NULL,
  `del` int(1) NOT NULL,
  `updatedAt` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `memberId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_MEMBERID_ROLEID` (`memberId`,`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `T_BUG_STATUS_SORT`;
CREATE TABLE `T_BUG_STATUS_SORT` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
	`projectId` bigint(20) NOT NULL COMMENT '��ĿID',
	`userId` bigint(20) NOT NULL  COMMENT '�û�ID',
	`status` int(1) NOT NULL,
	`sort` int(1) NOT NULL,
    `createdAt` datetime NOT NULL,
	`updatedAt` timestamp NOT NULL default CURRENT_TIMESTAMP,  
	`del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY(`userId`,`projectId`,`sort`),
  UNIQUE KEY(`projectId`,`userId`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


/**���Ӷ�̬ģ��**/
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>������Bug<span>%s</span>',  'BUG_CREATE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>�ر���Bug<span>%s</span>',  'BUG_CLOSE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>�����Bug<span>%s</span>�����������<span>%s</span>',  'BUG_SOLVE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>������Bug<span>%s</span>',  'BUG_ACTIVE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>�����Bugģ��<span>%s</span>',  'BUG_MODULE_CREATE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>ɾ����Bugģ��<span>%s</span>',  'BUG_MODULE_DELETE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>ָ����<span>%s</span>ΪBugģ��<span>%s</span>�ĸ�����',  'BUG_MODULE_ADD_MANAGER');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>��Bugģ�� <span>%s</span>�ĸ����˸�Ϊ<span>%s</span>',  'BUG_MODULE_UPDATE_MANAGER');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>�޸���Bug<span>%s</span>',  'BUG_UPDATE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>�޸���Bug<span>%s</span>���⣺<span>%s<span>�ĳ�<span>%s<span>',  'BUG_UPDATE_TITLE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>�޸���Bug<span>%s</span>������<span>%s<span>�ĳ�<span>%s<span>',  'BUG_UPDATE_DETAIL');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>��Bug<span>%s</span>�ĸ������޸�Ϊ<span>%s</span>',  'BUG_CHANGE_ASSIGNEDPERSON');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>��Bug<span>%s</span>���Ƴ��˳�Ա<span>%s</span>',  'BUG_REMOVE_MEMBER');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>ΪBug<span>%s</span>����˳�Ա<span>%s</span>',  'BUG_ADD_MEMBER');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>������Bug<span>%s</span>',  'BUG_COPY');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>��Bug<span>%s</span>���ϴ��˸���<span>%s</span>',  'BUG_ADD_RESOURCE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>��Bug<span>%s</span>������˱�ע',  'BUG_ADD_MARK');

/**֪ͨģ��**/
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>����ָ����Bug:��<span>%s</span>������֪����', 'BUG_ADD_MANAGER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>��������Bug:��<span>%s</span>������֪����', 'BUG_ADD_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','��������Bug����<span class="noticeClick">%s</span>���ѱ�<span>%s</span>�رգ���֪����', 'BUG_CLOSE_TO_CREATOR');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>����֤��ر���Bug:��<span>%s</span>������֪��', 'BUG_CLOSE_TO_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','�������Bug����<span class="noticeClick">%s</span>���ѱ�<span>%s</span>��֤��رգ���֪����', 'BUG_CLOSE_TO_ASSIGNEDPERSON');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>������Bug����<span>%s</span>������֪����', 'BUG_ACTIVE_TO_CREATOR');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>������Bug����<span>%s</span>������֪����', 'BUG_ACTIVE_TO_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>������Bug����<span>%s</span>������֪����', 'BUG_ACTIVE_TO_ASSIGNEDPERSON');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>�����Bug��<span class="noticeClick">%s</span>�����������Ϊ��<span>%s</span>������Ҫ���ո�Bug��', 'BUG_SOLVE_TO_CREATOR');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>�����Bug��<span class="noticeClick">%s</span>�����������Ϊ��<span>%s</span>������Bug�ѽ����', 'BUG_SOLVE_TO_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>�����Bug��<span class="noticeClick">%s</span>�����������Ϊ��<span>%s</span>������Bug�ѽ����', 'BUG_SOLVE_TO_ASSIGNEDPERSON');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>�Ѿ�������Bug��<span>%s</span>�����Ƴ���', 'BUG_REMOVE_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>��Bug��<span>%s</span>��ָ�ɸ����㣬��֪����', 'BUG_UPDATE_ASSIGNEDPERSON');
/**�޸�֪ͨ�ı�����**/
update T_NOTICE_MODULE set noFormatStr=replace(noFormatStr,':','��');
update T_NOTICE_MODULE set noFormatStr=replace(noFormatStr,',','��');
update T_NOTICE_MODULE set noFormatStr=replace(noFormatStr,'.','��');
/**����bug��ɫ**/
INSERT INTO T_ROLE(del,allowdel,cnName,enName,parentId) VALUES('0','0','Bug������','BUG_CREATOR','-1');
INSERT INTO T_ROLE(del,allowdel,cnName,enName,parentId) VALUES('0','0','Bugָ����','BUG_ASSIGNEDPERSON','-1');
INSERT INTO T_ROLE(del,allowdel,cnName,enName,parentId) VALUES('0','0','Bug������','BUG_MEMBER','-1');
/**����bugȨ������**/
INSERT INTO T_PERMISSION_TYPE(del,cnName) VALUES('0','bugȨ��');
/**����bugȨ��**/
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','����bug','bug_create',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','�༭bug����','bug_update',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ָ��bug������','bug_assign_assignedperson',id from T_PERMISSION_TYPE where cnName='bugȨ��';

INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','����bug','bug_copy',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','�鿴bug','bug_retrieve',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','����bug������','bug_invite_member',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ɾ��bug������','bug_del_member',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','���bug����','bug_add_resource',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ɾ��bug����','bug_del_resource',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','���bugģ��','bug_module_add',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ɾ��bugģ��','bug_module_del',id from T_PERMISSION_TYPE where cnName='bugȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ָ��bugģ�鸺����','bug_module_add_manager',id from T_PERMISSION_TYPE where cnName='bugȨ��';
/**��bug��ɫ����Ȩ��**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭bug����','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_update' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭bug����','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_update' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭bug����','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_update' and r.enName='BUG_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bug������','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_assign_assignedperson' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bug������','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_assign_assignedperson' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bug������','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_assign_assignedperson' and r.enName='BUG_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_retrieve' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_retrieve' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_retrieve' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_copy' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_copy' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_copy' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug������','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_invite_member' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug������','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_invite_member' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug������','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_invite_member' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug������','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_member' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug������','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_member' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug������','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_member' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bug����','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_add_resource' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bug����','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_add_resource' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bug����','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_add_resource' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug����','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_resource' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug����','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_resource' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug����','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_resource' and r.enName='BUG_MEMBER';


/**����Ŀ��ɫ����Ȩ��**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_create' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_create' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_create' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭bug����','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_update' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭bug����','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_update' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭bug����','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_update' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bug������','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_assign_assignedperson' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bug������','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_assign_assignedperson' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bug������','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_assign_assignedperson' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_retrieve' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_retrieve' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_retrieve' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_copy' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_copy' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_copy' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug������','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_invite_member' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug������','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_invite_member' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug������','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_invite_member' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug������','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_member' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug������','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_member' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug������','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_member' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bug����','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_add_resource' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bug����','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_add_resource' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bug����','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_add_resource' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug����','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_resource' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug����','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_resource' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug����','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_resource' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bugģ��','bug_module_del',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_del' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bugģ��','bug_module_del',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_del' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bugģ��','bug_module_del',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_del' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bugģ��','bug_module_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bugģ��','bug_module_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bugģ��','bug_module_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_add' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bugģ�鸺����','bug_module_add_manager',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_add_manager' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bugģ�鸺����','bug_module_add_manager',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_add_manager' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bugģ�鸺����','bug_module_add_manager',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_add_manager' and r.enName='PROJECT_MEMBER';



/**�������̽�ֹʱ��**/
alter table T_PROCESS add finishDate datetime;


