select * from T_PERMISSION where enName = 'resource_create_file';
INSERT INTO T_PERMISSION_TYPE_AUTH (createdAt, del, updatedAt, cnName, enName, permissionId, permissionTypeId, roleId) VALUES ( NOW(), '0', NOW(), '上传资源文件', 'resource_create_file', select id from  T_PERMISSION where enName='resource_create_file'
, '12', '16');