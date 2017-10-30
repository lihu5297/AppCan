DROP TABLE IF EXISTS `T_BUG_MODULE`;
CREATE TABLE `T_BUG_MODULE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
	`projectId` bigint(20) NOT NULL,
  `createdAt` datetime NOT NULL,
	`updatedAt` datetime NOT NULL,  
	`del` int(1) NOT NULL,
	`managerId` bigint(20) NOT NULL COMMENT '负责人',
	`creatorId` bigint(20) NOT NULL COMMENT '创建者',
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
  `moduleId` bigint(20) NOT NULL DEFAULT -1 COMMENT '模块ID',  
  `priority` int(1) NOT NULL COMMENT '优先级',
  `affectVersion` varchar(50) DEFAULT NULL COMMENT '影响版本',
  `resolveVersion` varchar(50) DEFAULT NULL COMMENT '解决版本',
  `resolveUserId` bigint(20) NOT NULL DEFAULT -1  COMMENT 'bug解决人',
  `solution` int(1) DEFAULT NULL COMMENT '解决方案',
  `resolveAt` datetime DEFAULT NULL COMMENT '解决时间',
  `closeAt` datetime DEFAULT NULL COMMENT '关闭时间',
  `closeUserId` bigint(20) NOT NULL DEFAULT -1  COMMENT '关闭操作人',
  `lastModifyUserId` bigint(20) NOT NULL COMMENT '最后操作人',
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
  `type` int(1) DEFAULT NULL COMMENT '0,创建者;1,指派人;2,参与人',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
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
	`projectId` bigint(20) NOT NULL COMMENT '项目ID',
	`userId` bigint(20) NOT NULL  COMMENT '用户ID',
	`status` int(1) NOT NULL,
	`sort` int(1) NOT NULL,
    `createdAt` datetime NOT NULL,
	`updatedAt` timestamp NOT NULL default CURRENT_TIMESTAMP,  
	`del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY(`userId`,`projectId`,`sort`),
  UNIQUE KEY(`projectId`,`userId`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


/**增加动态模板**/
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>创建了Bug<span>%s</span>',  'BUG_CREATE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>关闭了Bug<span>%s</span>',  'BUG_CLOSE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>解决了Bug<span>%s</span>，解决方案：<span>%s</span>',  'BUG_SOLVE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>激活了Bug<span>%s</span>',  'BUG_ACTIVE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>添加了Bug模块<span>%s</span>',  'BUG_MODULE_CREATE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>删除了Bug模块<span>%s</span>',  'BUG_MODULE_DELETE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>指派了<span>%s</span>为Bug模块<span>%s</span>的负责人',  'BUG_MODULE_ADD_MANAGER');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>将Bug模块 <span>%s</span>的负责人改为<span>%s</span>',  'BUG_MODULE_UPDATE_MANAGER');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>修改了Bug<span>%s</span>',  'BUG_UPDATE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>修改了Bug<span>%s</span>标题：<span>%s<span>改成<span>%s<span>',  'BUG_UPDATE_TITLE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>修改了Bug<span>%s</span>描述：<span>%s<span>改成<span>%s<span>',  'BUG_UPDATE_DETAIL');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>将Bug<span>%s</span>的负责人修改为<span>%s</span>',  'BUG_CHANGE_ASSIGNEDPERSON');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>在Bug<span>%s</span>中移除了成员<span>%s</span>',  'BUG_REMOVE_MEMBER');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>为Bug<span>%s</span>添加了成员<span>%s</span>',  'BUG_ADD_MEMBER');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>复制了Bug<span>%s</span>',  'BUG_COPY');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>在Bug<span>%s</span>中上传了附件<span>%s</span>',  'BUG_ADD_RESOURCE');
INSERT INTO T_DYNAMIC_MODULE  (del,formatStr,moduleType) VALUES ('0','<span>%s</span>在Bug<span>%s</span>中添加了备注',  'BUG_ADD_MARK');

/**通知模板**/
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>给您指派了Bug:【<span>%s</span>】，请知晓。', 'BUG_ADD_MANAGER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>邀您参与Bug:【<span>%s</span>】，请知晓。', 'BUG_ADD_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','您创建的Bug：【<span class="noticeClick">%s</span>】已被<span>%s</span>关闭，请知晓。', 'BUG_CLOSE_TO_CREATOR');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>已验证后关闭了Bug:【<span>%s</span>】，请知晓', 'BUG_CLOSE_TO_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','您解决的Bug：【<span class="noticeClick">%s</span>】已被<span>%s</span>验证后关闭，请知晓。', 'BUG_CLOSE_TO_ASSIGNEDPERSON');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>激活了Bug：【<span>%s</span>】，请知晓。', 'BUG_ACTIVE_TO_CREATOR');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>激活了Bug：【<span>%s</span>】，请知晓。', 'BUG_ACTIVE_TO_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>激活了Bug：【<span>%s</span>】，请知晓。', 'BUG_ACTIVE_TO_ASSIGNEDPERSON');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>解决了Bug【<span class="noticeClick">%s</span>】，解决方案为：<span>%s</span>，您需要验收该Bug。', 'BUG_SOLVE_TO_CREATOR');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>解决了Bug【<span class="noticeClick">%s</span>】，解决方案为：<span>%s</span>，您的Bug已解决。', 'BUG_SOLVE_TO_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>解决了Bug【<span class="noticeClick">%s</span>】，解决方案为：<span>%s</span>，您的Bug已解决。', 'BUG_SOLVE_TO_ASSIGNEDPERSON');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>已经将您从Bug【<span>%s</span>】中移除。', 'BUG_REMOVE_MEMBER');
INSERT INTO T_NOTICE_MODULE (del,noFormatStr,noModuleType) VALUES ('0','<span>%s</span>将Bug【<span>%s</span>】指派给了你，请知晓。', 'BUG_UPDATE_ASSIGNEDPERSON');
/**修改通知的标点符号**/
update T_NOTICE_MODULE set noFormatStr=replace(noFormatStr,':','：');
update T_NOTICE_MODULE set noFormatStr=replace(noFormatStr,',','，');
update T_NOTICE_MODULE set noFormatStr=replace(noFormatStr,'.','。');
/**增加bug角色**/
INSERT INTO T_ROLE(del,allowdel,cnName,enName,parentId) VALUES('0','0','Bug创建者','BUG_CREATOR','-1');
INSERT INTO T_ROLE(del,allowdel,cnName,enName,parentId) VALUES('0','0','Bug指派人','BUG_ASSIGNEDPERSON','-1');
INSERT INTO T_ROLE(del,allowdel,cnName,enName,parentId) VALUES('0','0','Bug参与人','BUG_MEMBER','-1');
/**增加bug权限类型**/
INSERT INTO T_PERMISSION_TYPE(del,cnName) VALUES('0','bug权限');
/**增加bug权限**/
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','创建bug','bug_create',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','编辑bug内容','bug_update',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','指派bug负责人','bug_assign_assignedperson',id from T_PERMISSION_TYPE where cnName='bug权限';

INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','复制bug','bug_copy',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','查看bug','bug_retrieve',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','邀请bug参与人','bug_invite_member',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','删除bug参与人','bug_del_member',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','添加bug附件','bug_add_resource',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','删除bug附件','bug_del_resource',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','添加bug模块','bug_module_add',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','删除bug模块','bug_module_del',id from T_PERMISSION_TYPE where cnName='bug权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','指派bug模块负责人','bug_module_add_manager',id from T_PERMISSION_TYPE where cnName='bug权限';
/**给bug角色赋予权限**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑bug内容','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_update' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑bug内容','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_update' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑bug内容','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_update' and r.enName='BUG_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug负责人','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_assign_assignedperson' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug负责人','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_assign_assignedperson' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug负责人','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_assign_assignedperson' and r.enName='BUG_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_retrieve' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_retrieve' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_retrieve' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_copy' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_copy' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_copy' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请bug参与人','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_invite_member' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请bug参与人','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_invite_member' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请bug参与人','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_invite_member' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug参与人','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_member' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug参与人','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_member' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug参与人','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_member' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug附件','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_add_resource' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug附件','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_add_resource' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug附件','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_add_resource' and r.enName='BUG_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug附件','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_resource' and r.enName='BUG_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug附件','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_resource' and r.enName='BUG_ASSIGNEDPERSON';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug附件','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_resource' and r.enName='BUG_MEMBER';


/**给项目角色赋予权限**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建bug','bug_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_create' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建bug','bug_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_create' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建bug','bug_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_create' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑bug内容','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_update' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑bug内容','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_update' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑bug内容','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_update' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug负责人','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_assign_assignedperson' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug负责人','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_assign_assignedperson' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug负责人','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_assign_assignedperson' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_retrieve' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_retrieve' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_retrieve' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_copy' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_copy' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_copy' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请bug参与人','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_invite_member' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请bug参与人','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_invite_member' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请bug参与人','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_invite_member' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug参与人','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_member' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug参与人','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_member' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug参与人','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_member' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug附件','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_add_resource' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug附件','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_add_resource' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug附件','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_add_resource' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug附件','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_resource' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug附件','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_resource' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug附件','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_resource' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug模块','bug_module_del',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_del' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug模块','bug_module_del',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_del' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug模块','bug_module_del',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_del' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug模块','bug_module_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug模块','bug_module_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug模块','bug_module_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_add' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug模块负责人','bug_module_add_manager',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_add_manager' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug模块负责人','bug_module_add_manager',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_add_manager' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug模块负责人','bug_module_add_manager',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_add_manager' and r.enName='PROJECT_MEMBER';



/**增加流程截止时间**/
alter table T_PROCESS add finishDate datetime;


