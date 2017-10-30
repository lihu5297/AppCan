alter table T_PROJECT modify  name varchar(1000) not null;
alter table T_TEAM modify  name varchar(1000);
alter table T_TEAM modify  detail varchar(1000);
alter table T_PROCESS modify  name varchar(1000) not null;
alter table T_PROCESS modify  detail varchar(1000);
alter table T_RESOURCES modify  name varchar(1000) not null;
alter table T_APP modify name varchar(1000);
alter table T_NOTICE modify noInfo varchar(1500);