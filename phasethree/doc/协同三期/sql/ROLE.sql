/**��������Ȩ��**/
delete from T_PERMISSION where enName like 'task_%';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','��������','task_create',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','����������','task_child_create',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','�༭��������','task_edit',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','�༭����������','task_child_edit',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ɾ������','task_remove',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ɾ��������','task_child_remove',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','�鿴����','task_retrieve',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','������������','task_manager_add',id from T_PERMISSION_TYPE where cnName='����Ȩ��';

INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','�������������','task_member_add',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ɾ�����������','task_member_remove',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','��������','task_copy',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','��������������','task_child_manager_add',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ת��������Ϊ����','task_child_to_parent',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','�������״̬','task_status_change',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','���������״̬','task_child_status_change',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','������񸽼�','task_resource_add',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ɾ�����񸽼�','task_resource_remove',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','����������','task_group_add',id from T_PERMISSION_TYPE where cnName='����Ȩ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ɾ���������','task_group_remove',id from T_PERMISSION_TYPE where cnName='����Ȩ��';


/**�����񴴽��ߡ������ˡ���Ա���Ȩ��**/
update T_PERMISSION_TYPE_AUTH set del=1 where enName like 'task_%';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_create' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_create' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_create' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭��������','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_edit' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭��������','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_edit' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭��������','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_edit' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭����������','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_edit' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭����������','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_edit' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭����������','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_edit' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ������','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_remove' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ������','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_remove' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ������','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_remove' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��������','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_remove' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��������','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_remove' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��������','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_remove' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴����','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_retrieve' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴����','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_retrieve' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴����','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_retrieve' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_manager_add' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_manager_add' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_manager_add' and r.enName='TASK_MEMBER';





INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_add' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_add' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_add' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_remove' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_remove' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_remove' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_copy' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_copy' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_copy' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������������','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_manager_add' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������������','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_manager_add' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������������','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_manager_add' and r.enName='TASK_MEMBER';





INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ת��������Ϊ����','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_to_parent' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ת��������Ϊ����','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_to_parent' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ת��������Ϊ����','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_to_parent' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������״̬','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_status_change' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������״̬','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_status_change' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������״̬','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_status_change' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������״̬','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_status_change' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������״̬','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_status_change' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������״̬','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_status_change' and r.enName='TASK_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������񸽼�','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_add' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������񸽼�','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_add' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������񸽼�','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_add' and r.enName='TASK_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����񸽼�','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_remove' and r.enName='TASK_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����񸽼�','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_remove' and r.enName='TASK_MANAGER';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����񸽼�','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_remove' and r.enName='TASK_MEMBER';



/**����Ŀ�����ߡ�����Ա�������˴���Ȩ��**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_create' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_create' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_create' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_create' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_create' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_child_create',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_create' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭��������','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_edit' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭��������','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_edit' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭��������','task_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_edit' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭����������','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_edit' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭����������','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_edit' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭����������','task_child_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_edit' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ������','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ������','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ������','task_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��������','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��������','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ��������','task_child_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴����','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_retrieve' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴����','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_retrieve' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴����','task_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_retrieve' and r.enName='PROJECT_MEMBER';

INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_manager_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_manager_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','task_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_manager_add' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','task_member_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_add' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','task_member_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_member_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','PROJECT_ADMINISTRATOR_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_ADMINISTRATOR_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','PROJECT_ADMINISTRATOR_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_ADMINISTRATOR_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������������','PROJECT_ADMINISTRATOR_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_ADMINISTRATOR_add' and r.enName='PROJECT_MEMBER';






INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','PROJECT_MEMBER_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_MEMBER_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','PROJECT_MEMBER_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_MEMBER_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������������','PROJECT_MEMBER_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_MEMBER_add' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','PROJECT_MEMBER_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_MEMBER_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','PROJECT_MEMBER_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_MEMBER_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����������','PROJECT_MEMBER_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='PROJECT_MEMBER_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_copy' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_copy' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������','task_copy',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_copy' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������������','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_manager_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������������','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_manager_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��������������','task_child_manager_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_manager_add' and r.enName='PROJECT_MEMBER';





INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ת��������Ϊ����','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_to_parent' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ת��������Ϊ����','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_to_parent' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ת��������Ϊ����','task_child_to_parent',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_to_parent' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������״̬','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_status_change' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������״̬','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_status_change' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�������״̬','task_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_status_change' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������״̬','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_status_change' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������״̬','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_status_change' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������״̬','task_child_status_change',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_child_status_change' and r.enName='PROJECT_MEMBER';



INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������񸽼�','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������񸽼�','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','������񸽼�','task_resource_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_add' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����񸽼�','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����񸽼�','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ�����񸽼�','task_resource_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_resource_remove' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_group_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_group_add' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_group_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_group_add' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','����������','task_group_add',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_group_add' and r.enName='PROJECT_MEMBER';


INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ���������','task_group_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_group_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ���������','task_group_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_group_remove' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ���������','task_group_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='task_group_remove' and r.enName='PROJECT_MEMBER';


/**����Ȩ�����Ӵ�Web IDE**/
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','��Web IDE','web_ide_open',id from T_PERMISSION_TYPE where cnName='����Ȩ��';

/**��Ŀ��Ա����web ide**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��Web IDE','web_ide_open',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='web_ide_open' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��Web IDE','web_ide_open',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='web_ide_open' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','��Web IDE','web_ide_open',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='����Ȩ��' and p.enName='web_ide_open' and r.enName='PROJECT_MEMBER';
/**��Ŀ�汾Ȩ���������������ļ�**/
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','���������ļ�','config_file_download',id from T_PERMISSION_TYPE where cnName='��Ŀ�汾Ȩ��';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������ļ�','config_file_download',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='��Ŀ�汾Ȩ��' and p.enName='config_file_download' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������ļ�','config_file_download',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='��Ŀ�汾Ȩ��' and p.enName='config_file_download' and r.enName='PROJECT_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','���������ļ�','config_file_download',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='��Ŀ�汾Ȩ��' and p.enName='config_file_download' and r.enName='PROJECT_MEMBER';
/**�����Ŷ���ĿȨ������**/
INSERT INTO T_PERMISSION_TYPE(del,cnName) VALUES('0','�Ŷ���ĿȨ��');
/**�����Ŷ���Ŀ������ЩȨ��**/
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','�༭��Ŀ��Ϣ','team_project_info_edit',id from T_PERMISSION_TYPE where cnName='�Ŷ���ĿȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ת����Ŀ','team_project_transfer',id from T_PERMISSION_TYPE where cnName='�Ŷ���ĿȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','ɾ����Ŀ','team_project_remove',id from T_PERMISSION_TYPE where cnName='�Ŷ���ĿȨ��';
INSERT INTO T_PERMISSION(del,actionId,cnName,enName,typeId) select '0','0','�鿴��Ŀ','team_project_retrieve',id from T_PERMISSION_TYPE where cnName='�Ŷ���ĿȨ��';
/**�ŶӴ����߸����Ŷ���ĿȨ��**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭��Ŀ��Ϣ','team_project_info_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_info_edit' and r.enName='TEAM_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ת����Ŀ','team_project_transfer',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_transfer' and r.enName='TEAM_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ����Ŀ','team_project_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_remove' and r.enName='TEAM_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴��Ŀ','team_project_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_retrieve' and r.enName='TEAM_CREATOR';
/**�Ŷӹ���Ա�����Ŷ���ĿȨ��**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ����Ŀ','team_project_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_remove' and r.enName='TEAM_ADMINISTRATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴��Ŀ','team_project_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_retrieve' and r.enName='TEAM_ADMINISTRATOR';
/**��Ŀ�����߸����Ŷ���ĿȨ��**/
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�༭��Ŀ��Ϣ','team_project_info_edit',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_info_edit' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ת����Ŀ','team_project_transfer',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_transfer' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','ɾ����Ŀ','team_project_remove',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_remove' and r.enName='PROJECT_CREATOR';
INSERT INTO T_PERMISSION_TYPE_AUTH(del,cnName,enName,permissionId,permissionTypeId,roleId) select '0','�鿴��Ŀ','team_project_retrieve',p.id,pt.id,r.id from T_PERMISSION p join T_PERMISSION_TYPE pt on p.typeId= pt.id join T_ROLE r where pt.cnName='�Ŷ���ĿȨ��' and p.enName='team_project_retrieve' and r.enName='PROJECT_CREATOR';