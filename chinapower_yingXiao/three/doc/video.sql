DROP TABLE IF EXISTS `T_VIDEO`;
CREATE TABLE `T_VIDEO` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `downloadUrl` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `status` int(1) DEFAULT '0',
  `title` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `tuijian` int(1) DEFAULT '0',
  `sort` bigint(20) DEFAULT NULL,
  `type` int(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8;

insert into T_MAN_MODULE(del,cnName,parentId,type,url) values(0,"视频管理",-1,0,"video");
insert into T_MAN_MODULE(del,cnName,parentId,type,url) select 0,"视频维护",id,1,"video/list"  from T_MAN_MODULE where cnName='视频管理';
