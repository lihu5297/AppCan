/**��Ŀ�۲�Ա�������Ȩ��**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_create' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_create' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭��������','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_edit' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭����������','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_edit' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ������','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��������','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴����','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_retrieve' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_manager_add' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_add' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','PROJECT_ADMINISTRATOR_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_ADMINISTRATOR_add' and r.enName='PROJECT_OBSERVER';







INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','PROJECT_MEMBER_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_MEMBER_add' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','PROJECT_MEMBER_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_MEMBER_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_copy' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������������','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_manager_add' and r.enName='PROJECT_OBSERVER';






INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ת��������Ϊ����','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_to_parent' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������״̬','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_status_change' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������״̬','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_status_change' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������񸽼�','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_add' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����񸽼�','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_remove' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_group_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_group_add' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ���������','task_group_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_group_remove' and r.enName='PROJECT_OBSERVER';

/**����Ŀ�۲�Ա���bugȨ��**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_create' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭bug����','bug_update',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_update' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bug������','bug_assign_assignedperson',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_assign_assignedperson' and r.enName='PROJECT_OBSERVER';




INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴bug','bug_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_retrieve' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug','bug_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_copy' and r.enName='PROJECT_OBSERVER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����bug������','bug_invite_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_invite_member' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug������','bug_del_member',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_member' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bug����','bug_add_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_add_resource' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bug����','bug_del_resource',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_del_resource' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��bugģ��','bug_module_del',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_del' and r.enName='PROJECT_OBSERVER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���bugģ��','bug_module_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_add' and r.enName='PROJECT_OBSERVER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ָ��bugģ�鸺����','bug_module_add_manager',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='bugȨ��' and p.enName='bug_module_add_manager' and r.enName='PROJECT_OBSERVER';

