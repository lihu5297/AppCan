1.//增加项目拼音字段
alter table T_PROJECT add pinYinHeadChar varchar(255);
alter table T_PROJECT add pinYinName varchar(255);
//增加项目拼音接口
localhost:8080/cooldev/project/addPinyin
//增加，编辑,搜索名称已写


2.//增加流程拼音字段
alter table T_PROCESS add pinYinHeadChar varchar(255);
alter table T_PROCESS add pinYinName varchar(255);
//增加流程拼音接口
localhost:8080/cooldev/process/addPinyin
//增加，编辑,搜索名称已写

3.//增加应用拼音字段
alter table T_APP add pinYinHeadChar varchar(255);
alter table T_APP add pinYinName varchar(255);
//增加应用拼音接口
localhost:8080/cooldev/app/addPinyin
//增加，编辑,搜索名称已写

4.//增加任务分组拼音字段
alter table T_TASK_GROUP add pinYinHeadChar varchar(255);
alter table T_TASK_GROUP add pinYinName varchar(255);
//增加任务分组拼音接口
localhost:8080/cooldev/task/addTaskGroupPinyin
//增加，编辑,搜索名称已写

5.//增加bug模块拼音字段
alter table T_BUG_MODULE add pinYinHeadChar varchar(255);
alter table T_BUG_MODULE add pinYinName varchar(255);
//增加bug模块拼音接口
localhost:8080/cooldev/bugModule/addPinyin
//增加，编辑,搜索名称已写

alter table T_TASK drop COLUMN progress;
alter table T_PROJECT add progress int default 0;
alter table T_PROCESS add progress int default 0;
//增加流程进度,需要初始化
/process/initProgress
//项目进度,需要初始化
/project/initProgress





=======================20160721企业版上线===========================
curl http://localhost:8081/cooldev/project/addPinyin
curl http://localhost:8081/cooldev/process/addPinyin
curl http://localhost:8081/cooldev/app/addPinyin
curl http://localhost:8081/cooldev/task/addTaskGroupPinyin
curl http://localhost:8081/cooldev/bugModule/addPinyin
curl http://localhost:8081/cooldev/process/initProgress
curl http://localhost:8081/cooldev/project/initProgress





======================拼音搜索补充==================================

1.增加团队拼音字段
alter table T_TEAM add pinYinHeadChar varchar(255);
alter table T_TEAM add pinYinName varchar(255);
 创建，编辑，查询已改
初始化拼音字段
localhost:8080/cooldev/team/addPinYin
2.增加文档拼音字段
alter table T_DOCUMENT add pinYinHeadChar varchar(255);
alter table T_DOCUMENT add pinYinName varchar(255);
 创建，编辑，查询已改
 初始化拼音字段
localhost:8080/cooldev/document/addPinYin






