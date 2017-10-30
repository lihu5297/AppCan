/**-------------修改引擎的filePath--------------**/

update T_ENGINE set filePath = CONCAT(

'd_engine_',id,'_', 
case type when 0 then 'PUBLIC' when 1 then 'PRIVATE' when 2 then 'PROJECT' END ,

'_',
case osType when 0 then 'IOS' when 1 then 'ANDROID'  END ,'_',versionNo
) 
 where filePath is NULL
 
 
 
/**---------修改插件filePath-----------**/
 update T_PLUGIN_VERSION pluginVersion inner JOIN 
(select id,

CONCAT(

'd_pluginVersion_',id,'_', 
case type when 0 then 'PUBLIC' when 1 then 'PRIVATE' when 2 then 'PROJECT' END ,

'_',
case osType when 0 then 'IOS' when 1 then 'ANDROID'  END ,'_',versionNo,'_',enName
) A
 from (
select v.*,p.type,p.enName from T_PLUGIN_VERSION v left join T_PLUGIN p on v.pluginId=p.id 
) t ) tmp on pluginVersion.id = tmp.id set pluginVersion.filePath= tmp.A