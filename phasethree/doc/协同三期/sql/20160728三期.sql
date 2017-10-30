
INSERT INTO T_PERMISSION (`del`,`actionId`,`enName`,`cnName`,`typeId`) VALUES(0,0,'resource_rename','资源重命名',12);
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) SELECT '0', '资源重命名', 'resource_rename', p.id, pt.id, r.id FROM T_PERMISSION p JOIN T_PERMISSION_TYPE pt ON p.typeId = pt.id JOIN T_ROLE r WHERE pt.cnName = '资源权限' AND p.enName = 'resource_rename' AND r.enName = 'PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) SELECT '0', '资源重命名', 'resource_rename', p.id, pt.id, r.id FROM T_PERMISSION p JOIN T_PERMISSION_TYPE pt ON p.typeId = pt.id JOIN T_ROLE r WHERE pt.cnName = '资源权限' AND p.enName = 'resource_rename' AND r.enName = 'PROJECT_ADMINISTRATOR';


update T_DYNAMIC_MODULE set formatStr='<span>%s</span>完成了子任务<span>%s</span>' where moduleType = 'TASK_LEAF_FINISHED';
update T_DYNAMIC_MODULE set formatStr='<span>%s</span>驳回了子任务<span>%s</span>' where moduleType = 'TASK_LEAF_UNFINISHED';