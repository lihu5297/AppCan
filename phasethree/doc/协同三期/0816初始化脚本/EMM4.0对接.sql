----T_PROJECT表增加uuid字段
ALTER TABLE T_PROJECT
ADD COLUMN uuid  varchar(255) default null AFTER finishDate;

----初始化uuid字段
update T_PROJECT set uuid=UUID() where uuid is null;

----增加动态模版
DELETE FROM T_DYNAMIC_MODULE where moduleType in ('TEAM_ASK_UNBIND','TEAM_CANCEL_UNBIND','PROJECT_ASK_BIND','PROJECT_CANCEL_BIND','PROJECT_ASK_UNBIND','PROJECT_CANCEL_UNBIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>申请将团队<span>%s</span>解绑企业%s', '/usr/a.png', 'TEAM_ASK_UNBIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>取消将团队<span>%s</span>解绑企业%s的申请', '/usr/a.png', 'TEAM_CANCEL_UNBIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>申请将项目<span>%s</span>绑定企业%s', '/usr/a.png', 'PROJECT_ASK_BIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>取消将项目<span>%s</span>绑定企业%s的申请', '/usr/a.png', 'PROJECT_CANCEL_BIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>申请将项目<span>%s</span>解绑企业%s', '/usr/a.png', 'PROJECT_ASK_UNBIND');
INSERT INTO T_DYNAMIC_MODULE ( createdAt, del, updatedAt, formatStr, moduleIcon, moduleType) VALUES ( NOW(), '0', NOW(), '<span>%s</span>取消将项目<span>%s</span>解绑企业%s的申请', '/usr/a.png', 'PROJECT_CANCEL_UNBIND');

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
