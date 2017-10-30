#!/bin/sh
#

 mysql  -hxt2 -ucoop_user -pcooppass <<EOF 
 #use cooperation_enterprise;
 use cooperation;

delete from T_TASK_SURVEY where DATE_FORMAT(taskAt,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d');
insert into T_TASK_SURVEY(projectId,managerUserId,taskAt,stockNum,addNum,completeNum)
select t3.projectId,t3.managerUserId,DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d'),
sum(stockNum) stockNum,
sum(addNum) addNum,
sum(completeNum) completeNum
from (
select t.projectId,t2.userId managerUserId,sum(case when t1.status=0 then 1 else 0 end) as stockNum,
sum(case when DATE_FORMAT(t1.createdAt,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') then 1 else 0 end) as addNum,
sum(case when DATE_FORMAT(t1.finishDate,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') and t1.status=1 then 1 else 0 end) as completeNum 	
from T_PROCESS t
left join T_TASK t1 on t.id=t1.processId
left join T_TASK_MEMBER t2 on t1.id=t2.taskId
left join T_TASK_AUTH t3 on t2.id=t3.memberId
left join T_ROLE t4 on t4.id=t3.roleId
where t.del=0 and t1.del=0 and t2.del=0 and t3.del=0 and t1.id not in (select distinct topTaskId from T_TASK_LEAF)
 and t4.enName='TASK_MANAGER'
group by t.projectId,t2.userId
union all 
select t.projectId,t2.managerUserId,sum(case when t2.status=0 then 1 else 0 end)  as stockNum,
sum(case when DATE_FORMAT(t2.createdAt,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') then 1 else 0 end) as addNum,
sum(case when DATE_FORMAT(t2.finishDate,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') and t2.status=1 then 1 else 0 end) as completeNum 	
from  T_PROCESS t
left join T_TASK t1 on t.id=t1.processId
inner join T_TASK_LEAF t2 on t1.id=t2.topTaskId
where t.del=0 and t1.del=0 and t2.del=0
group by t.projectId,t2.managerUserId
) t3 
group by t3.projectId,t3.managerUserId;
delete from T_BUG_SURVEY where DATE_FORMAT(bugAt,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d');
insert into T_BUG_SURVEY(projectId,ManagerUserId,bugAt,stockNum,addNum,completeNum)
select t.projectId,t.ManagerUserId,t.bugAt,sum(stockNum),sum(addNum),sum(completeNum)
from (
select t.projectId,t2.userId ManagerUserId,DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') bugAt,
sum(case when t1.status=0 then 1 else 0 end) as stockNum,
sum(case when DATE_FORMAT(t1.createdAt,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') then 1 else 0 end) as addNum,
sum(case when DATE_FORMAT(t1.closeAt,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') and t1.status=2 then 1 else 0 end) as completeNum 	
from T_PROCESS t
left join T_BUG t1 on t.id=t1.processId
left join T_BUG_MEMBER t2 on t1.id=t2.bugId
left join T_BUG_AUTH t3 on t2.id=t3.memberId
left join T_ROLE t4 on t3.roleId=t4.id 
where t.del=0 and t1.del=0  and t2.del=0 and t3.del=0
and t4.enName='BUG_ASSIGNEDPERSON'  and t1.status=0
group by t.projectId,t2.userId 
union all 
select t.projectId,t1.resolveUserId ManagerUserId,DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') bugAt, 
sum(case when t1.status=0 then 1 else 0 end) as stockNum,
sum(case when DATE_FORMAT(t1.createdAt,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') then 1 else 0 end) as addNum,
sum(case when DATE_FORMAT(t1.closeAt,'%Y-%m-%d')=DATE_FORMAT(date_sub(curdate(),interval 1 day),'%Y-%m-%d') and t1.status=2 then 1 else 0 end) as completeNum 	
from T_PROCESS t
left join T_BUG t1 on t.id=t1.processId
where t.del=0 and t1.del=0 and t1.status in (1,2)
and t1.resolveUserId!=0
group by t.projectId,t1.resolveUserId) t
group by t.projectId,t.ManagerUserId,t.bugAt;
quit
EOF
