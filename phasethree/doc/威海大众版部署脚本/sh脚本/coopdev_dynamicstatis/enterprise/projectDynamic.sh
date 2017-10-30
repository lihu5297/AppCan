#!/bin/sh
#


 mysql  -hxt2 -ucoop_user -pcooppass <<EOF 
 use cooperation;

INSERT INTO  C_PROJECT_DYNAMIC( prjid,prjname,totaldynamic,v_time,totalmember)

SELECT t1.prjid,t1.prjname,t1.totaldynamic,t1.v_time,ss.memcount FROM (
  SELECT t.prjid,t.prjname,COUNT(1) totaldynamic,DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00') v_time FROM (
    SELECT prj.id prjid,prj.name prjname,dy.createdAt FROM T_DYNAMIC dy LEFT JOIN T_PROJECT  prj ON dy.relationId = prj.id WHERE prj.del=0 and  dy.createdAt >= DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00')   AND dy.createdAt<DATE_FORMAT(NOW(),'%Y-%m-%d %H:00:00')    AND dy.type=0 

  ) t  GROUP BY t.prjid  

) t1
  LEFT JOIN 
  
  (
  
    SELECT p.id,p.name,COUNT(DISTINCT p.userid) memcount FROM (
  SELECT prj.id,prj.name,pm.userid FROM T_PROJECT prj LEFT JOIN T_PROJECT_MEMBER pm ON prj.id = pm.projectId WHERE prj.del=0 AND pm.del=0 AND pm.type IN (0,1)
    ) p GROUP BY p.id 
  
  ) ss
  
  ON t1.prjid = ss.id;


  INSERT INTO  C_PROJECT_DYNAMIC_TMP(  prjid,prjname,taskdynamic,v_time)
  
  SELECT t1.prjid,t1.prjname,COUNT(1) taskdynamic,DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00') v_time FROM (
      
      SELECT prj.id prjid,prj.name prjname,dy.createdAt FROM T_DYNAMIC dy LEFT JOIN T_PROJECT prj ON dy.relationId=prj.id  LEFT JOIN T_DYNAMIC_DEPENDENCY  dep ON dy.id = dep.dynamicId       WHERE prj.del=0 and  dy.createdAt >= DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00')   AND  dy.createdAt<DATE_FORMAT(NOW(),'%Y-%m-%d %H:00:00')    AND dy.TYPE=0 AND  dep.entityType='Task'
  )t1  GROUP BY t1.prjId;




UPDATE C_PROJECT_DYNAMIC main INNER JOIN C_PROJECT_DYNAMIC_TMP tmp ON main.prjid = tmp.prjid AND main.v_time = tmp.v_time AND  main.v_time = DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) , '%Y-%m-%d %H:00:00')  SET main.taskdynamic=tmp.taskdynamic;


quit
EOF
