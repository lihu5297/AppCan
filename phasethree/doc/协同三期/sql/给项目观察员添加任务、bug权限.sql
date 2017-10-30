/**项目观察员添加任务权限**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建任务','task_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_create' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建子任务','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_create' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑任务内容','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_edit' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑子任务内容','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_edit' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除子任务','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看任务','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_retrieve' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_manager_add' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_add' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','PROJECT_ADMINISTRATOR_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_ADMINISTRATOR_add' and r.enName='PROJECT_OBSERVER';







INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','PROJECT_MEMBER_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_MEMBER_add' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','PROJECT_MEMBER_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_MEMBER_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制任务','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_copy' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请子任务负责人','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_manager_add' and r.enName='PROJECT_OBSERVER';






INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','转化子任务为任务','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_to_parent' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更任务状态','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_status_change' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更子任务状态','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_status_change' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务附件','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_add' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务附件','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务分组','task_group_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_group_add' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务分组','task_group_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_group_remove' and r.enName='PROJECT_OBSERVER';

/**给项目观察员添加bug权限**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建bug','bug_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_create' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑bug内容','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_update' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug负责人','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_assign_assignedperson' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_retrieve' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_copy' and r.enName='PROJECT_OBSERVER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请bug参与人','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_invite_member' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug参与人','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_member' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug附件','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_add_resource' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug附件','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_del_resource' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除bug模块','bug_module_del',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_del' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加bug模块','bug_module_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_add' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','指派bug模块负责人','bug_module_add_manager',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bug权限' and p.enName='bug_module_add_manager' and r.enName='PROJECT_OBSERVER';

