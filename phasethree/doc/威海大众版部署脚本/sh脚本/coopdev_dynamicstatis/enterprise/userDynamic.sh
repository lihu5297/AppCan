#!/bin/sh
#


 mysql  -hxt2 -ucoop_user -pcooppass <<EOF 
 use cooperation;

INSERT INTO  C_USER_DYNAMIC( userid,account,username,totaldynamic,v_time)

SELECT dy.userId,u.account,u.username,COUNT(1) totaldynamic ,DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00') v_time FROM T_DYNAMIC dy LEFT JOIN T_USER u ON dy.userId = u.id WHERE u.del=0 and  dy.createdAt >= DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00')   AND dy.createdAt<DATE_FORMAT(NOW(),'%Y-%m-%d %H:00:00')  GROUP BY dy.userId;

INSERT INTO  C_USER_DYNAMIC_TMP( userid,account,taskdynamic,v_time)

SELECT dy.userId,u.account,COUNT(1) taskdynamic ,DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00') v_time FROM T_DYNAMIC dy LEFT JOIN T_USER u ON dy.userId = u.id  LEFT JOIN T_DYNAMIC_DEPENDENCY dep ON dy.id=dep.dynamicId WHERE u.del=0 and  dy.createdAt >= DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) ,'%Y-%m-%d %H:00:00')   AND dy.createdAt<DATE_FORMAT(NOW(),'%Y-%m-%d %H:00:00') AND  dep.entityType='Task'  GROUP BY dy.userId;


UPDATE C_USER_DYNAMIC main INNER JOIN C_USER_DYNAMIC_TMP tmp ON main.userid = tmp.userid AND main.v_time = tmp.v_time AND  main.v_time = DATE_FORMAT( DATE_SUB(NOW(), INTERVAL 1 HOUR) , '%Y-%m-%d %H:00:00')  SET main.taskdynamic=tmp.taskdynamic;



quit
EOF
