#!/bin/sh
#


 mysql  -hxt2 -ucoop_user -pcooppass <<EOF 
  use cooperation;

INSERT INTO  C_TEAM_DYNAMIC( teamid,teamname,totaldynamic,v_time,totalmember)

SELECT t1.teamid,t1.teamname,t1.totaldynamic,t1.v_time,ss.memcount FROM (
  SELECT t.teamid,t.teamname,COUNT(1) totaldynamic,DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00') v_time FROM (
    SELECT team.id teamid,team.name teamname,dy.createdAt FROM T_DYNAMIC dy LEFT JOIN T_TEAM  team ON dy.relationId = team.id WHERE team.del=0 and  dy.createdAt >= DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00')   AND dy.createdAt<DATE_FORMAT(NOW(),'%Y-%m-%d %H:00:00')    AND dy.type=1 

    UNION ALL
  
    SELECT team.id teamid,team.name teamname,dy.createdAt FROM T_DYNAMIC dy LEFT JOIN T_PROJECT prj ON dy.relationId=prj.id LEFT JOIN T_TEAM team ON prj.teamId = team.id  WHERE prj.del=0 and team.del=0 and  dy.createdAt >= DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00')   AND dy.createdAt<  DATE_FORMAT(NOW(),'%Y-%m-%d %H:00:00')    AND dy.TYPE=0 AND prj.type=1 
  
  
  ) t  GROUP BY t.teamId  

) t1
  LEFT JOIN 
  
  (
  
    SELECT t.id,t.name,COUNT(DISTINCT t.userid) memcount FROM (
    SELECT team.id,team.name,tm.userid FROM T_TEAM  team LEFT JOIN T_TEAM_MEMBER tm ON team.id=tm.teamId   WHERE team.del=0 AND tm.del=0 AND tm.type IN (0,1)
    UNION ALL 
    SELECT team.id,team.name,  pm.userid   FROM T_TEAM team LEFT JOIN T_PROJECT prj ON team.id=prj.teamId LEFT JOIN T_PROJECT_MEMBER pm ON prj.id = pm.projectId WHERE team.del=0 AND pm.del=0 AND prj.del=0  AND pm.type IN (0,1)
    ) t GROUP BY t.id 
  
  ) ss
  
  ON t1.teamid = ss.id;


  INSERT INTO  C_TEAM_DYNAMIC_TMP( teamid,teamname,taskdynamic,v_time)
	
  SELECT t1.teamid,t1.teamname,COUNT(1) taskdynamic,DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00') v_time FROM (
      SELECT team.id teamid,team.name teamname,dy.createdAt FROM T_DYNAMIC dy LEFT JOIN T_TEAM  team ON dy.relationId = team.id LEFT JOIN T_DYNAMIC_DEPENDENCY  dep ON dy.id = dep.dynamicId     WHERE  team.del=0 and  dy.createdAt >= DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00')   AND dy.createdAt<DATE_FORMAT(NOW(),'%Y-%m-%d %H:00:00')    AND dy.type=1 AND dep.entityType='Task'
      UNION ALL
      SELECT team.id teamid,team.name teamname,dy.createdAt FROM T_DYNAMIC dy LEFT JOIN T_PROJECT prj ON dy.relationId=prj.id LEFT JOIN T_TEAM team ON prj.teamId = team.id LEFT JOIN T_DYNAMIC_DEPENDENCY  dep ON dy.id = dep.dynamicId       WHERE  prj.del=0 and team.del=0 and dy.createdAt >= DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00')   AND  dy.createdAt<DATE_FORMAT(NOW(),'%Y-%m-%d %H:00:00')    AND dy.TYPE=0 AND prj.type=1  AND dep.entityType='Task'
  )t1  GROUP BY t1.teamId;




UPDATE C_TEAM_DYNAMIC main INNER JOIN C_TEAM_DYNAMIC_TMP tmp on main.teamid = tmp.teamid and main.v_time = tmp.v_time and  main.v_time = DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) , '%Y-%m-%d %H:00:00')  SET main.taskdynamic=tmp.taskdynamic;

quit
EOF
