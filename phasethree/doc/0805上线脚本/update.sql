update T_TEAM set type=2 where enterpriseId is not null and enterpriseName is not null and type=0;

update T_PROJECT set bizLicense=2 where teamId in (select id from T_TEAM where type=2)


alter table T_TASK_MEMBER  ADD INDEX INDEX_USERID_TASKID (`userId`,`taskId`);