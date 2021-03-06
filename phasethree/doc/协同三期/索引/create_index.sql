--【通知】
alter table t_notice add index index_recievedId(recievedId)

alter table t_notice_dependency add index index_noticeId(noticeId)

ALTER TABLE T_NOTICE_MODULE ADD INDEX INDEX_NOMODULETYPE(NOMODULETYPE)


--【团队】
alter table T_TEAM_MEMBER ADD INDEX INDEX_TEAMID_USERID (TEAMID,USERID)

ALTER TABLE T_TEAM_AUTH ADD INDEX INDEX_MEMBERID_ROLEID (MEMBERID,ROLEID)

--【项目】
alter table T_PROJECT_MEMBER ADD INDEX INDEX_PROJECTID_USERID (PROJECTID,USERID)

ALTER TABLE T_PROJECT_AUTH ADD INDEX INDEX_MEMBERID_ROLEID (MEMBERID,ROLEID)

--【流程】
alter table T_PROCESS_MEMBER ADD INDEX INDEX_PROCESSID_USERID (PROCESSID,USERID)

ALTER TABLE T_PROCESS_AUTH ADD INDEX INDEX_MEMBERID_ROLEID (MEMBERID,ROLEID)

--【任务】
alter table T_TASK_MEMBER ADD INDEX INDEX_TASKID_USERID (TASKID,USERID)

ALTER TABLE T_TASK_AUTH ADD INDEX INDEX_MEMBERID_ROLEID (MEMBERID,ROLEID)

--【用户表】

ALTER TABLE T_USER ADD INDEX INDEX_ACCOUNT (ACCOUNT);

ALTER TABLE T_USER ADD INDEX INDEX_USERNAME_PINYINHEADCHAR_PINYINNAME (USERNAME,PINYINHEADCHAR,PINYINNAME);


--【动态】

ALTER TABLE T_DYNAMIC_MODULE ADD INDEX INDEX_MODULETYPE(MODULETYPE);

ALTER TABLE T_DYNAMIC ADD INDEX INDEX_TYPE_RELATIONID(TYPE,RELATIONID);

ALTER TABLE T_DYNAMIC_DEPENDENCY ADD INDEX INDEX_ENTITYTYPE_ENTITYID(ENTITYTYPE,ENTITYID);

ALTER TABLE T_DYNAMIC_DEPENDENCY ADD INDEX INDEX_DYNAMICID(DYNAMICID);
