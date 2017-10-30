/**增加任务权限**/
delete from T_PERMISSION where enName like 'task_%';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','创建任务','task_create',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','创建子任务','task_child_create',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','编辑任务内容','task_edit',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','编辑子任务内容','task_child_edit',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','删除任务','task_remove',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','删除子任务','task_child_remove',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','查看任务','task_retrieve',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','邀请任务负责人','task_manager_add',id from T_PERMISSION_TYPE where cnName='任务权限';

INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','邀请任务参与人','task_member_add',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','删除任务参与人','task_member_remove',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','复制任务','task_copy',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','邀请子任务负责人','task_child_manager_add',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','转化子任务为任务','task_child_to_parent',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','变更任务状态','task_status_change',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','变更子任务状态','task_child_status_change',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','添加任务附件','task_resource_add',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','删除任务附件','task_resource_remove',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','添加任务分组','task_group_add',id from T_PERMISSION_TYPE where cnName='任务权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','删除任务分组','task_group_remove',id from T_PERMISSION_TYPE where cnName='任务权限';


/**给任务创建者、负责人、成员添加权限**/
update T_PERMISSION_TYPE_AUTH set del=1 where enName like 'task_%';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建子任务','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_create' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建子任务','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_create' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建子任务','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_create' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑任务内容','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_edit' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑任务内容','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_edit' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑任务内容','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_edit' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑子任务内容','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_edit' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑子任务内容','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_edit' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑子任务内容','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_edit' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_remove' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_remove' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_remove' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除子任务','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_remove' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除子任务','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_remove' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除子任务','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_remove' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看任务','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_retrieve' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看任务','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_retrieve' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看任务','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_retrieve' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_manager_add' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_manager_add' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_manager_add' and r.enName='TASK_MEMBER';





INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_add' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_add' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_add' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_remove' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_remove' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_remove' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制任务','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_copy' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制任务','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_copy' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制任务','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_copy' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请子任务负责人','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_manager_add' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请子任务负责人','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_manager_add' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请子任务负责人','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_manager_add' and r.enName='TASK_MEMBER';





INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','转化子任务为任务','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_to_parent' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','转化子任务为任务','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_to_parent' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','转化子任务为任务','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_to_parent' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更任务状态','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_status_change' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更任务状态','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_status_change' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更任务状态','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_status_change' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更子任务状态','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_status_change' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更子任务状态','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_status_change' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更子任务状态','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_status_change' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务附件','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_add' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务附件','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_add' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务附件','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_add' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务附件','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_remove' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务附件','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_remove' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务附件','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_remove' and r.enName='TASK_MEMBER';



/**给项目创建者、管理员、参与人创建权限**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建任务','task_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_create' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建任务','task_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_create' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建任务','task_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_create' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建子任务','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_create' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建子任务','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_create' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','创建子任务','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_create' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑任务内容','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_edit' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑任务内容','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_edit' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑任务内容','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_edit' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑子任务内容','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_edit' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑子任务内容','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_edit' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑子任务内容','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_edit' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除子任务','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除子任务','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除子任务','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看任务','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_retrieve' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看任务','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_retrieve' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看任务','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_retrieve' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_manager_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_manager_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_manager_add' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_add' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_member_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','PROJECT_ADMINISTRATOR_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_ADMINISTRATOR_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','PROJECT_ADMINISTRATOR_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_ADMINISTRATOR_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务负责人','PROJECT_ADMINISTRATOR_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_ADMINISTRATOR_add' and r.enName='PROJECT_MEMBER';






INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','PROJECT_MEMBER_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_MEMBER_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','PROJECT_MEMBER_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_MEMBER_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请任务参与人','PROJECT_MEMBER_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_MEMBER_add' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','PROJECT_MEMBER_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_MEMBER_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','PROJECT_MEMBER_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_MEMBER_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务参与人','PROJECT_MEMBER_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='PROJECT_MEMBER_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制任务','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_copy' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制任务','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_copy' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','复制任务','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_copy' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请子任务负责人','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_manager_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请子任务负责人','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_manager_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','邀请子任务负责人','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_manager_add' and r.enName='PROJECT_MEMBER';





INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','转化子任务为任务','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_to_parent' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','转化子任务为任务','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_to_parent' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','转化子任务为任务','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_to_parent' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更任务状态','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_status_change' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更任务状态','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_status_change' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更任务状态','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_status_change' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更子任务状态','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_status_change' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更子任务状态','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_status_change' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','变更子任务状态','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_child_status_change' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务附件','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务附件','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务附件','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_add' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务附件','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务附件','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务附件','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_resource_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务分组','task_group_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_group_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务分组','task_group_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_group_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','添加任务分组','task_group_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_group_add' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务分组','task_group_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_group_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务分组','task_group_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_group_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除任务分组','task_group_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='任务权限' and p.enName='task_group_remove' and r.enName='PROJECT_MEMBER';


/**代码权限增加打开Web IDE**/
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','打开Web IDE','web_ide_open',id from T_PERMISSION_TYPE where cnName='代码权限';

/**项目成员增加web ide**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','打开Web IDE','web_ide_open',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='代码权限' and p.enName='web_ide_open' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','打开Web IDE','web_ide_open',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='代码权限' and p.enName='web_ide_open' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','打开Web IDE','web_ide_open',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='代码权限' and p.enName='web_ide_open' and r.enName='PROJECT_MEMBER';
/**项目版本权限增加下载配置文件**/
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','下载配置文件','config_file_download',id from T_PERMISSION_TYPE where cnName='项目版本权限';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','下载配置文件','config_file_download',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='项目版本权限' and p.enName='config_file_download' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','下载配置文件','config_file_download',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='项目版本权限' and p.enName='config_file_download' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','下载配置文件','config_file_download',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='项目版本权限' and p.enName='config_file_download' and r.enName='PROJECT_MEMBER';
/**增加团队项目权限类型**/
INSERT INTO T_PERMISSION_TYPE(del,cnName) VALUES('0','团队项目权限');
/**增加团队项目下有哪些权限**/
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','编辑项目信息','team_project_info_edit',id from T_PERMISSION_TYPE where cnName='团队项目权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','转让项目','team_project_transfer',id from T_PERMISSION_TYPE where cnName='团队项目权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','删除项目','team_project_remove',id from T_PERMISSION_TYPE where cnName='团队项目权限';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','查看项目','team_project_retrieve',id from T_PERMISSION_TYPE where cnName='团队项目权限';
/**团队创建者赋予团队项目权限**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑项目信息','team_project_info_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_info_edit' and r.enName='TEAM_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','转让项目','team_project_transfer',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_transfer' and r.enName='TEAM_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除项目','team_project_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_remove' and r.enName='TEAM_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看项目','team_project_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_retrieve' and r.enName='TEAM_CREATOR';
/**团队管理员赋予团队项目权限**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除项目','team_project_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_remove' and r.enName='TEAM_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看项目','team_project_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_retrieve' and r.enName='TEAM_ADMINISTRATOR';
/**项目创建者赋予团队项目权限**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','编辑项目信息','team_project_info_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_info_edit' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','转让项目','team_project_transfer',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_transfer' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','删除项目','team_project_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','查看项目','team_project_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='团队项目权限' and p.enName='team_project_retrieve' and r.enName='PROJECT_CREATOR';