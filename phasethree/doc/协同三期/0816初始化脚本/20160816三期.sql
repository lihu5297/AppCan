-----增加uuid字段，用来存贮公开后的文件名
ALTER TABLE T_RESOURCES
ADD COLUMN uuid  varchar(255) default null AFTER sourceType;
-----给uuid初始化
update T_RESOURCES set uuid=UUID() where uuid is null;

-----增加是否公开标识
ALTER TABLE T_RESOURCES
ADD COLUMN isPublic  tinyint(4) default 0 AFTER uuid;
 
-----资源重命名权限、资源公开权限
INSERT INTO T_PERMISSION (createdAt,del,updatedAt,actionId,enName,cnName,typeId) VALUES(NOW(),0,NOW(),0,'resource_rename','资源重命名',12);
INSERT INTO T_PERMISSION (createdAt,del,updatedAt,actionId,enName,cnName,typeId) VALUES(NOW(),0,NOW(),0,'resource_public','资源公开',12);

-----需要先查询permissionId
SELECT id FROM T_PERMISSION where enName='resource_rename'
SELECT id FROM T_PERMISSION where enName='resource_public'
-----把id放到values里面
INSERT INTO T_PERMISSION_TYPE_AUTH (createdAt, del, updatedAt, cnName, enName, permissionId, permissionTypeId, roleId) VALUES ( NOW(), '0', NOW(), '资源重命名', 'resource_rename', select id from  T_PERMISSION where enName='resource_rename'
, '12', '4');
INSERT INTO T_PERMISSION_TYPE_AUTH (createdAt, del, updatedAt, cnName, enName, permissionId, permissionTypeId, roleId) VALUES (NOW(), '0', NOW(), '资源重命名', 'resource_rename', '159', '12', '5');
INSERT INTO T_PERMISSION_TYPE_AUTH (createdAt, del, updatedAt, cnName, enName, permissionId, permissionTypeId, roleId) VALUES (NOW(), '0', NOW(), '资源重命名', 'resource_rename', '158', '12', '6');
INSERT INTO T_PERMISSION_TYPE_AUTH (createdAt, del, updatedAt, cnName, enName, permissionId, permissionTypeId, roleId) VALUES (NOW(), '0', NOW(), '资源重命名', 'resource_rename', '158', '12', '16');

INSERT INTO T_PERMISSION_TYPE_AUTH (createdAt, del, updatedAt, cnName, enName, permissionId, permissionTypeId, roleId) VALUES ( NOW(), '0', NOW(), '资源公开', 'resource_public', '160', '12', '4');
INSERT INTO T_PERMISSION_TYPE_AUTH (createdAt, del, updatedAt, cnName, enName, permissionId, permissionTypeId, roleId) VALUES (NOW(), '0', NOW(), '资源公开', 'resource_public', '160', '12', '5');
INSERT INTO T_PERMISSION_TYPE_AUTH (createdAt, del, updatedAt, cnName, enName, permissionId, permissionTypeId, roleId) VALUES (NOW(), '0', NOW(), '资源公开', 'resource_public', '160', '12', '6');
INSERT INTO T_PERMISSION_TYPE_AUTH (createdAt, del, updatedAt, cnName, enName, permissionId, permissionTypeId, roleId) VALUES (NOW(), '0', NOW(), '资源公开', 'resource_public', '160', '12', '16');





----T_ENGINE表增加kernel字段
ALTER TABLE T_ENGINE ADD COLUMN kernel varchar(255) default 'system' AFTER filePath;



----增加动态模版
DELETE FROM T_DYNAMIC_MODULE where moduleType in ('TEAM_ASK_UNBIND','TEAM_CANCEL_UNBIND','PROJECT_ASK_BIND','PROJECT_CANCEL_BIND','PROJECT_ASK_UNBIND','PROJECT_CANCEL_UNBIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>申请将团队<span>%s</span>解绑企业%s', '/usr/a.png', 'TEAM_ASK_UNBIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>取消将团队<span>%s</span>解绑企业%s的申请', '/usr/a.png', 'TEAM_CANCEL_UNBIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>申请将项目<span>%s</span>绑定企业%s', '/usr/a.png', 'PROJECT_ASK_BIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>取消将项目<span>%s</span>绑定企业%s的申请', '/usr/a.png', 'PROJECT_CANCEL_BIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>申请将项目<span>%s</span>解绑企业%s', '/usr/a.png', 'PROJECT_ASK_UNBIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>取消将项目<span>%s</span>解绑企业%s的申请', '/usr/a.png', 'PROJECT_CANCEL_UNBIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( now(), '0', now(), '<span>%s</span>将资源<span>%s</span>的名称改为<span>%s</span>', '/usr/a.png', 'PROCESS_RENAME_RESOURCE');
----增加通知模版
DELETE FROM T_NOTICE_MODULE where noModuleType in ('TEAM_AGREE_UNBIND_ENTERPRISE','TEAM_UNAGREE_UNBIND_ENTERPRISE','PROJECT_BIND_ENTERPRISE',
'PROJECT_UNBIND_ENTERPRISE','PROJECT_TRANSFER','PROJECT_AGREE_UNBIND_ENTERPRISE','PROJECT_UNAGREE_UNBIND_ENTERPRISE');
INSERT INTO T_NOTICE_MODULE ( createdAt, del, updatedAt, noFormatStr, noModuleType) VALUES ( NOW(), '0', NOW(), '你的<span>%s</span>团队已成功和<span>%s</span>企业解绑', 'TEAM_AGREE_UNBIND_ENTERPRISE');
INSERT INTO T_NOTICE_MODULE ( createdAt, del, updatedAt, noFormatStr, noModuleType) VALUES ( NOW(), '0', NOW(), '你的<span>%s</span>团队被<span>%s</span>企业拒绝解绑', 'TEAM_UNAGREE_UNBIND_ENTERPRISE');
INSERT INTO T_NOTICE_MODULE ( createdAt, del, updatedAt, noFormatStr, noModuleType) VALUES ( NOW(), '0', NOW(), '你的<span>%s</span>项目已成功和<span>%s</span>企业绑定', 'PROJECT_BIND_ENTERPRISE');
INSERT INTO T_NOTICE_MODULE ( createdAt, del, updatedAt, noFormatStr, noModuleType) VALUES ( NOW(), '0', NOW(), '你的<span>%s</span>项目被<span>%s</span>企业拒绝绑定', 'PROJECT_UNBIND_ENTERPRISE');
INSERT INTO T_NOTICE_MODULE ( createdAt, del, updatedAt, noFormatStr, noModuleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>把<span>%s</span>项目转移给了你', 'PROJECT_TRANSFER');
INSERT INTO T_NOTICE_MODULE ( createdAt, del, updatedAt, noFormatStr, noModuleType) VALUES ( NOW(), '0', NOW(), '你的<span>%s</span>项目已成功和<span>%s</span>企业解绑', 'PROJECT_AGREE_UNBIND_ENTERPRISE');
INSERT INTO T_NOTICE_MODULE ( createdAt, del, updatedAt, noFormatStr, noModuleType) VALUES ( NOW(), '0', NOW(), '你的<span>%s</span>项目被<span>%s</span>企业拒绝解绑', 'PROJECT_UNAGREE_UNBIND_ENTERPRISE');



