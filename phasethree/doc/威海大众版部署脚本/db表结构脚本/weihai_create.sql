/*
Navicat MySQL Data Transfer

Source Server         : 10.0.2.2
Source Server Version : 50629
Source Host           : 10.0.2.2:3306
Source Database       : cooperation

Target Server Type    : MYSQL
Target Server Version : 50629
File Encoding         : 65001

Date: 2016-11-17 18:32:58
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for c_project_dynamic
-- ----------------------------
DROP TABLE IF EXISTS `C_PROJECT_DYNAMIC`;
CREATE TABLE `C_PROJECT_DYNAMIC` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `prjid` bigint(20) NOT NULL,
  `prjname` varchar(255) DEFAULT NULL,
  `totaldynamic` int(11) DEFAULT NULL,
  `taskdynamic` int(11) DEFAULT NULL,
  `v_time` datetime DEFAULT NULL,
  `totalmember` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `prjid_time` (`prjid`,`v_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for c_project_dynamic_tmp
-- ----------------------------
DROP TABLE IF EXISTS `C_PROJECT_DYNAMIC_TMP`;
CREATE TABLE `C_PROJECT_DYNAMIC_TMP` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `prjid` bigint(20) NOT NULL,
  `prjname` varchar(255) DEFAULT NULL,
  `taskdynamic` int(11) DEFAULT NULL,
  `v_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `prjid_time` (`prjid`,`v_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for c_team_dynamic
-- ----------------------------
DROP TABLE IF EXISTS `C_TEAM_DYNAMIC`;
CREATE TABLE `C_TEAM_DYNAMIC` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `teamid` bigint(20) NOT NULL,
  `teamname` varchar(255) DEFAULT NULL,
  `totaldynamic` int(11) DEFAULT NULL,
  `taskdynamic` int(11) DEFAULT NULL,
  `v_time` datetime DEFAULT NULL,
  `totalmember` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `teamid_time` (`teamid`,`v_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for c_team_dynamic_tmp
-- ----------------------------
DROP TABLE IF EXISTS `C_TEAM_DYNAMIC_TMP`;
CREATE TABLE `C_TEAM_DYNAMIC_TMP` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `teamid` bigint(20) NOT NULL,
  `teamname` varchar(255) DEFAULT NULL,
  `taskdynamic` int(11) DEFAULT NULL,
  `v_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `teamid_time` (`teamid`,`v_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for c_user_dynamic
-- ----------------------------
DROP TABLE IF EXISTS `C_USER_DYNAMIC`;
CREATE TABLE `C_USER_DYNAMIC` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userid` bigint(20) NOT NULL,
  `account` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `totaldynamic` int(11) DEFAULT NULL,
  `taskdynamic` int(11) DEFAULT NULL,
  `v_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userid_time` (`userid`,`v_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for c_user_dynamic_tmp
-- ----------------------------
DROP TABLE IF EXISTS `C_USER_DYNAMIC_TMP`;
CREATE TABLE `C_USER_DYNAMIC_TMP` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userid` bigint(20) NOT NULL,
  `account` varchar(255) DEFAULT NULL,
  `taskdynamic` int(11) DEFAULT NULL,
  `v_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userid_time` (`userid`,`v_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for invitationcode
-- ----------------------------
DROP TABLE IF EXISTS `INVITATIONCODE`;
CREATE TABLE `INVITATIONCODE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(200) DEFAULT NULL COMMENT '用户名',
  `myCode` varchar(8) DEFAULT NULL,
  `systemCode` varchar(8) DEFAULT NULL COMMENT '登录协同后系统赠送的邀请码',
  `checkStatus` varchar(20) DEFAULT NULL COMMENT '后台审核状态(needCheck 待审核，available审核通过)',
  `enterCoopStatus` varchar(20) DEFAULT NULL COMMENT '是否通过邀请码验证(needCode需要邀请码，available不需要邀请码)',
  `userType` varchar(255) DEFAULT NULL COMMENT 'personal个人,enterprise 企业',
  `createTime` timestamp NULL DEFAULT NULL,
  `origin` varchar(50) DEFAULT NULL,
  `secondDevTag` int(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for t_action
-- ----------------------------
DROP TABLE IF EXISTS `T_ACTION`;
CREATE TABLE `T_ACTION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `authRelatedType` int(11) DEFAULT NULL,
  `method` varchar(6) NOT NULL,
  `name` varchar(50) NOT NULL,
  `pattern` varchar(255) NOT NULL,
  `targetType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_advice
-- ----------------------------
DROP TABLE IF EXISTS `T_ADVICE`;
CREATE TABLE `T_ADVICE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `content` longtext,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_app
-- ----------------------------
DROP TABLE IF EXISTS `T_APP`;
CREATE TABLE `T_APP` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `AppSource` bigint(20) DEFAULT NULL,
  `appType` int(11) DEFAULT NULL,
  `appcanAppId` varchar(255) DEFAULT NULL,
  `appcanAppKey` varchar(255) DEFAULT NULL,
  `detail` longtext,
  `name` varchar(1000) DEFAULT NULL,
  `projectId` bigint(20) DEFAULT NULL,
  `published` int(11) DEFAULT NULL,
  `publishedAppCan` int(11) DEFAULT NULL,
  `publishedTest` int(11) DEFAULT NULL,
  `relativeRepoPath` varchar(255) DEFAULT NULL,
  `repoType` int(11) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  `appCategory` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `codePullStatus` varchar(255) DEFAULT NULL,
  `forbidPub` varchar(255) DEFAULT NULL,
  `sourceGitRepo` varchar(255) DEFAULT NULL,
  `specialAppCanAppId` varchar(255) DEFAULT NULL,
  `specialAppCanAppKey` varchar(255) DEFAULT NULL,
  `pinYinHeadChar` varchar(255) DEFAULT NULL,
  `pinYinName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_app_channel
-- ----------------------------
DROP TABLE IF EXISTS `T_APP_CHANNEL`;
CREATE TABLE `T_APP_CHANNEL` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `appId` bigint(20) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_app_package
-- ----------------------------
DROP TABLE IF EXISTS `T_APP_PACKAGE`;
CREATE TABLE `T_APP_PACKAGE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `appVersionId` bigint(20) NOT NULL,
  `buildJsonSettings` text,
  `buildLogUrl` varchar(255) DEFAULT NULL,
  `buildMessage` varchar(255) DEFAULT NULL,
  `buildStatus` int(11) DEFAULT NULL,
  `buildType` int(11) DEFAULT NULL,
  `channelCode` varchar(255) DEFAULT NULL,
  `downloadUrl` varchar(255) DEFAULT NULL,
  `fileSize` bigint(20) NOT NULL,
  `hardwareAccelerated` int(11) NOT NULL,
  `increUpdateIF` int(11) NOT NULL,
  `osType` int(11) DEFAULT NULL,
  `publised` int(11) DEFAULT NULL,
  `publisedAppCan` int(11) DEFAULT NULL,
  `publisedTest` int(11) DEFAULT NULL,
  `pushIF` int(11) NOT NULL,
  `qrCode` varchar(255) DEFAULT NULL,
  `terminalType` int(11) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  `updateSwith` int(11) NOT NULL,
  `versionDescription` varchar(255) DEFAULT NULL,
  `versionNo` varchar(255) DEFAULT NULL,
  `newAppCanAppId` varchar(255) DEFAULT NULL,
  `newAppCanAppKey` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_app_patch
-- ----------------------------
DROP TABLE IF EXISTS `T_APP_PATCH`;
CREATE TABLE `T_APP_PATCH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `baseAppVersionId` bigint(20) NOT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  `fileSize` bigint(20) NOT NULL,
  `published` int(11) DEFAULT NULL,
  `publishedTest` int(11) DEFAULT NULL,
  `seniorAppVersionId` bigint(20) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  `versionDescription` varchar(255) DEFAULT NULL,
  `versionNo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_app_version
-- ----------------------------
DROP TABLE IF EXISTS `T_APP_VERSION`;
CREATE TABLE `T_APP_VERSION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `appId` bigint(20) DEFAULT NULL,
  `branchName` varchar(255) DEFAULT NULL,
  `branchZipName` varchar(255) DEFAULT NULL,
  `newVersionNo` varchar(255) DEFAULT NULL,
  `oldVersionNo` varchar(255) DEFAULT NULL,
  `patchOrFull` int(11) DEFAULT NULL,
  `patchPublishedProduct` int(11) DEFAULT NULL,
  `patchPublishedTest` int(11) DEFAULT NULL,
  `tagName` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `versionDescription` longtext,
  `versionNo` varchar(255) DEFAULT NULL,
  `widgetName` varchar(255) DEFAULT NULL,
  `patchType` int(11) DEFAULT NULL,
  `widgetPublised` int(11) DEFAULT NULL,
  `widgetPublisedTest` int(11) DEFAULT NULL,
  `branchZipSize` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_app_widget
-- ----------------------------
DROP TABLE IF EXISTS `T_APP_WIDGET`;
CREATE TABLE `T_APP_WIDGET` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `appVersionId` bigint(20) NOT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  `fileSize` bigint(20) NOT NULL,
  `publised` int(11) DEFAULT NULL,
  `publisedTest` int(11) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  `versionDescription` varchar(255) DEFAULT NULL,
  `versionNo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_backup_log
-- ----------------------------
DROP TABLE IF EXISTS `T_BACKUP_LOG`;
CREATE TABLE `T_BACKUP_LOG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `backupDetail` varchar(255) DEFAULT NULL,
  `backupPath` varchar(255) DEFAULT NULL,
  `backupTime` datetime DEFAULT NULL,
  `serverIp` varchar(255) DEFAULT NULL,
  `serverName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_bug
-- ----------------------------
DROP TABLE IF EXISTS `T_BUG`;
CREATE TABLE `T_BUG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(1000) NOT NULL,
  `detail` varchar(1000) NOT NULL,
  `processId` bigint(20) NOT NULL,
  `appId` bigint(20) NOT NULL DEFAULT '-1',
  `status` int(1) NOT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `del` int(1) NOT NULL,
  `moduleId` bigint(20) NOT NULL DEFAULT '-1' COMMENT '模块ID',
  `priority` int(1) NOT NULL COMMENT '优先级',
  `affectVersion` varchar(50) DEFAULT NULL COMMENT '影响版本',
  `resolveVersion` varchar(50) DEFAULT NULL COMMENT '解决版本',
  `resolveUserId` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'bug解决人',
  `solution` int(1) DEFAULT NULL COMMENT '解决方案',
  `resolveAt` datetime DEFAULT NULL COMMENT '解决时间',
  `closeAt` datetime DEFAULT NULL COMMENT '关闭时间',
  `closeUserId` bigint(20) NOT NULL DEFAULT '-1' COMMENT '关闭操作人',
  `lastModifyUserId` bigint(20) NOT NULL COMMENT '最后操作人',
  PRIMARY KEY (`id`),
  KEY `INDEX_PROCESSID` (`processId`),
  KEY `INDEX_CLOSEUSERID_LASTMODIFYUSERID` (`closeUserId`,`lastModifyUserId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_bug_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_BUG_AUTH`;
CREATE TABLE `T_BUG_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime NOT NULL,
  `del` int(1) NOT NULL,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `memberId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_MEMBERID_ROLEID` (`memberId`,`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_bug_mark
-- ----------------------------
DROP TABLE IF EXISTS `T_BUG_MARK`;
CREATE TABLE `T_BUG_MARK` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bugId` bigint(20) NOT NULL,
  `info` varchar(1000) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_BUGID` (`bugId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_bug_member
-- ----------------------------
DROP TABLE IF EXISTS `T_BUG_MEMBER`;
CREATE TABLE `T_BUG_MEMBER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime NOT NULL,
  `del` int(1) NOT NULL,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `bugId` bigint(20) NOT NULL,
  `type` int(1) DEFAULT NULL COMMENT '0,创建者;1,指派人;2,参与人',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  KEY `INDEX_BUGID_USERID` (`bugId`,`userId`),
  KEY `INDEX_USERID` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_bug_module
-- ----------------------------
DROP TABLE IF EXISTS `T_BUG_MODULE`;
CREATE TABLE `T_BUG_MODULE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `projectId` bigint(20) NOT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL,
  `del` int(1) NOT NULL,
  `managerId` bigint(20) NOT NULL COMMENT '负责人',
  `creatorId` bigint(20) NOT NULL COMMENT '创建者',
  `pinYinHeadChar` varchar(255) DEFAULT NULL,
  `pinYinName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_PROJECTID` (`projectId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_bug_status_sort
-- ----------------------------
DROP TABLE IF EXISTS `T_BUG_STATUS_SORT`;
CREATE TABLE `T_BUG_STATUS_SORT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL COMMENT '项目ID',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `status` int(1) NOT NULL,
  `sort` int(1) NOT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userId` (`userId`,`projectId`,`sort`),
  UNIQUE KEY `projectId` (`projectId`,`userId`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_bug_survey
-- ----------------------------
DROP TABLE IF EXISTS `T_BUG_SURVEY`;
CREATE TABLE `T_BUG_SURVEY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL COMMENT '任务ID',
  `managerUserId` bigint(20) NOT NULL COMMENT '用户ID',
  `bugAt` varchar(10) NOT NULL COMMENT '任务日期',
  `stockNum` bigint(20) NOT NULL COMMENT '存量任务数',
  `addNum` bigint(20) NOT NULL COMMENT '新增任务数',
  `completeNum` bigint(20) NOT NULL COMMENT '已完成任务数',
  PRIMARY KEY (`id`),
  KEY `INDEX_TOPTASKID` (`projectId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_document
-- ----------------------------
DROP TABLE IF EXISTS `T_DOCUMENT`;
CREATE TABLE `T_DOCUMENT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `describ` text,
  `name` varchar(255) DEFAULT NULL,
  `projectId` bigint(20) DEFAULT NULL,
  `pub` tinyint(4) DEFAULT NULL,
  `pubUrl` varchar(255) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `pinYinHeadChar` varchar(255) DEFAULT NULL,
  `pinYinName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_document_chapter
-- ----------------------------
DROP TABLE IF EXISTS `T_DOCUMENT_CHAPTER`;
CREATE TABLE `T_DOCUMENT_CHAPTER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `contentHTML` longtext,
  `contentMD` longtext,
  `documentId` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parentId` bigint(20) DEFAULT NULL,
  `pub` tinyint(4) DEFAULT NULL,
  `sort` tinyint(4) DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_document_marker
-- ----------------------------
DROP TABLE IF EXISTS `T_DOCUMENT_MARKER`;
CREATE TABLE `T_DOCUMENT_MARKER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `docCId` bigint(20) DEFAULT NULL,
  `target` varchar(255) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_dynamic
-- ----------------------------
DROP TABLE IF EXISTS `T_DYNAMIC`;
CREATE TABLE `T_DYNAMIC` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `info` text,
  `moduleType` varchar(255) DEFAULT NULL,
  `relationId` bigint(20) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_TYPE_RELATIONID` (`type`,`relationId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_dynamic_dependency
-- ----------------------------
DROP TABLE IF EXISTS `T_DYNAMIC_DEPENDENCY`;
CREATE TABLE `T_DYNAMIC_DEPENDENCY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `dynamicId` bigint(20) DEFAULT NULL,
  `entityId` bigint(20) DEFAULT NULL,
  `entityType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_ENTITYTYPE_ENTITYID` (`entityType`,`entityId`),
  KEY `INDEX_DYNAMICID` (`dynamicId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_dynamic_module
-- ----------------------------
DROP TABLE IF EXISTS `T_DYNAMIC_MODULE`;
CREATE TABLE `T_DYNAMIC_MODULE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `formatStr` varchar(255) DEFAULT NULL,
  `moduleIcon` varchar(255) DEFAULT NULL,
  `moduleType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_scds1j2hvrtxgvqw9pljt1uyy` (`moduleType`),
  KEY `INDEX_MODULETYPE` (`moduleType`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_engine
-- ----------------------------
DROP TABLE IF EXISTS `T_ENGINE`;
CREATE TABLE `T_ENGINE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `downloadUrl` varchar(255) DEFAULT NULL,
  `osType` int(11) DEFAULT NULL,
  `pkgGitRepoUrl` varchar(255) DEFAULT NULL,
  `projectId` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `versionDescription` varchar(255) DEFAULT NULL,
  `versionNo` varchar(255) DEFAULT NULL,
  `uploadStatus` int(11) DEFAULT NULL,
  `package` varchar(100) DEFAULT NULL,
  `packageDescription` varchar(255) DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `kernel` varchar(255) DEFAULT 'system',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_enterprise
-- ----------------------------
DROP TABLE IF EXISTS `T_ENTERPRISE`;
CREATE TABLE `T_ENTERPRISE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `emailOrQQ` varchar(255) DEFAULT NULL,
  `linkMan` varchar(255) DEFAULT NULL,
  `telephone` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_entity_resource_rel
-- ----------------------------
DROP TABLE IF EXISTS `T_ENTITY_RESOURCE_REL`;
CREATE TABLE `T_ENTITY_RESOURCE_REL` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `entityId` bigint(20) NOT NULL,
  `entityType` int(11) DEFAULT NULL,
  `resourceId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_git_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `T_GIT_OPERATION_LOG`;
CREATE TABLE `T_GIT_OPERATION_LOG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `account` varchar(255) DEFAULT NULL,
  `appId` bigint(20) DEFAULT NULL,
  `gitRemoteUrl` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_identity_code
-- ----------------------------
DROP TABLE IF EXISTS `T_IDENTITY_CODE`;
CREATE TABLE `T_IDENTITY_CODE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_admin
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_ADMIN`;
CREATE TABLE `T_MAN_ADMIN` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `account` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `cellphone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `qq` varchar(255) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_backup_log
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_BACKUP_LOG`;
CREATE TABLE `T_MAN_BACKUP_LOG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `backupDetail` varchar(255) DEFAULT NULL,
  `backupFileName` varchar(255) DEFAULT NULL,
  `backupPath` varchar(255) DEFAULT NULL,
  `backupTime` datetime DEFAULT NULL,
  `serverIp` varchar(255) DEFAULT NULL,
  `serverName` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_disk_statistic
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_DISK_STATISTIC`;
CREATE TABLE `T_MAN_DISK_STATISTIC` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `host` varchar(255) DEFAULT NULL,
  `hostName` varchar(255) DEFAULT NULL,
  `unUsedInfo` longtext,
  `usedInfo` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_log_action
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_LOG_ACTION`;
CREATE TABLE `T_MAN_LOG_ACTION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `pattern` varchar(255) NOT NULL,
  `targetType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_manager_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_MANAGER_AUTH`;
CREATE TABLE `T_MAN_MANAGER_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `managerId` bigint(20) NOT NULL,
  `moduleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_module
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_MODULE`;
CREATE TABLE `T_MAN_MODULE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `cnName` varchar(255) DEFAULT NULL,
  `enName` varchar(255) DEFAULT NULL,
  `parentId` bigint(20) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_OPERATION_LOG`;
CREATE TABLE `T_MAN_OPERATION_LOG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `account` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `logActionId` bigint(20) NOT NULL,
  `method` varchar(255) DEFAULT NULL,
  `operationLog` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_platform_log
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_PLATFORM_LOG`;
CREATE TABLE `T_MAN_PLATFORM_LOG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `filename` varchar(255) DEFAULT NULL,
  `hostName` varchar(255) DEFAULT NULL,
  `logDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_process_config
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_PROCESS_CONFIG`;
CREATE TABLE `T_MAN_PROCESS_CONFIG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `creatorRoleStr` varchar(255) DEFAULT NULL,
  `managerRoleStr` varchar(255) DEFAULT NULL,
  `memberRoleStr` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `processTemplateId` bigint(20) NOT NULL,
  `sequence` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_process_template
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_PROCESS_TEMPLATE`;
CREATE TABLE `T_MAN_PROCESS_TEMPLATE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_setting
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_SETTING`;
CREATE TABLE `T_MAN_SETTING` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `EMMAccessUrl` varchar(255) DEFAULT NULL,
  `EMMAndroidPushUrl` varchar(255) DEFAULT NULL,
  `EMMDataReportUrl` varchar(255) DEFAULT NULL,
  `EMMDataStatisticUrl` varchar(255) DEFAULT NULL,
  `EMMPushBindUrl` varchar(255) DEFAULT NULL,
  `EMMTestAccessUrl` varchar(255) DEFAULT NULL,
  `EMMTestAndroidPushUrl` varchar(255) DEFAULT NULL,
  `EMMTestDataReportUrl` varchar(255) DEFAULT NULL,
  `EMMTestDataStatisticUrl` varchar(255) DEFAULT NULL,
  `EMMTestPushBindUrl` varchar(255) DEFAULT NULL,
  `SYSIntegrateTime` datetime DEFAULT NULL,
  `SYSKey` varchar(255) DEFAULT NULL,
  `SYSStatus` int(11) DEFAULT NULL,
  `SYSdoMain` varchar(255) DEFAULT NULL,
  `emailAccount` varchar(255) DEFAULT NULL,
  `emailPassword` varchar(255) DEFAULT NULL,
  `emailServerPort` varchar(255) DEFAULT NULL,
  `emailServerStatus` int(11) DEFAULT NULL,
  `emailServerType` int(11) DEFAULT NULL,
  `emailServerUrl` varchar(255) DEFAULT NULL,
  `platBackupPath` varchar(255) DEFAULT NULL,
  `platExecuteTime` datetime DEFAULT NULL,
  `platInterval` bigint(20) NOT NULL,
  `platLogo` varchar(255) DEFAULT NULL,
  `platName` varchar(255) DEFAULT NULL,
  `authDeadTime` varchar(255) DEFAULT NULL,
  `authStatus` varchar(255) DEFAULT NULL,
  `authorizePath` varchar(255) DEFAULT NULL,
  `webAddr` varchar(255) DEFAULT NULL,
  `EMMContentManageUrl` varchar(255) DEFAULT NULL,
  `EMMDeviceManageUrl` varchar(255) DEFAULT NULL,
  `EMMTestContentManageUrl` varchar(255) DEFAULT NULL,
  `EMMTestDeviceManageUrl` varchar(255) DEFAULT NULL,
  `platExecuteTime_hour` int(11) NOT NULL,
  `platExecuteTime_minutes` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_task_config
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_TASK_CONFIG`;
CREATE TABLE `T_MAN_TASK_CONFIG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_task_config_relate
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_TASK_CONFIG_RELATE`;
CREATE TABLE `T_MAN_TASK_CONFIG_RELATE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `nextTaskId` bigint(20) DEFAULT NULL,
  `taskConfigId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_man_user_active
-- ----------------------------
DROP TABLE IF EXISTS `T_MAN_USER_ACTIVE`;
CREATE TABLE `T_MAN_USER_ACTIVE` (
  `id` varchar(255) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `describ` varchar(255) DEFAULT NULL,
  `entityId` bigint(20) DEFAULT NULL,
  `entityType` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_member_complete
-- ----------------------------
DROP TABLE IF EXISTS `T_MEMBER_COMPLETE`;
CREATE TABLE `T_MEMBER_COMPLETE` (
  `projectId` bigint(20) NOT NULL COMMENT '任务ID',
  `loginUserId` bigint(20) NOT NULL COMMENT '登陆人ID',
  `userId` varchar(100) NOT NULL COMMENT '被选中的成员ID',
  `chartContent` char(1) NOT NULL COMMENT '图表内容',
  PRIMARY KEY (`projectId`,`loginUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_mgr_action
-- ----------------------------
DROP TABLE IF EXISTS `T_MGR_ACTION`;
CREATE TABLE `T_MGR_ACTION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `authRelatedType` int(11) DEFAULT NULL,
  `method` varchar(6) NOT NULL,
  `name` varchar(50) NOT NULL,
  `pattern` varchar(255) NOT NULL,
  `targetType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_notice
-- ----------------------------
DROP TABLE IF EXISTS `T_NOTICE`;
CREATE TABLE `T_NOTICE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `noInfo` varchar(1500) DEFAULT NULL,
  `noModuleType` varchar(255) DEFAULT NULL,
  `noRead` int(11) DEFAULT NULL,
  `recievedId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_recievedId` (`recievedId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_notice_dependency
-- ----------------------------
DROP TABLE IF EXISTS `T_NOTICE_DEPENDENCY`;
CREATE TABLE `T_NOTICE_DEPENDENCY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `entityId` bigint(20) DEFAULT NULL,
  `entityType` varchar(255) DEFAULT NULL,
  `noticeId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_noticeId` (`noticeId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_notice_module
-- ----------------------------
DROP TABLE IF EXISTS `T_NOTICE_MODULE`;
CREATE TABLE `T_NOTICE_MODULE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `noFormatStr` varchar(255) DEFAULT NULL,
  `noModuleType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_NOMODULETYPE` (`noModuleType`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_notice_module_copy
-- ----------------------------
DROP TABLE IF EXISTS `T_NOTICE_MODULE_COPY`;
CREATE TABLE `T_NOTICE_MODULE_COPY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `noFormatStr` varchar(255) DEFAULT NULL,
  `noModuleType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_permission
-- ----------------------------
DROP TABLE IF EXISTS `T_PERMISSION`;
CREATE TABLE `T_PERMISSION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `actionId` bigint(20) NOT NULL,
  `cnName` varchar(255) DEFAULT NULL,
  `enName` varchar(255) DEFAULT NULL,
  `typeId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_permission_type
-- ----------------------------
DROP TABLE IF EXISTS `T_PERMISSION_TYPE`;
CREATE TABLE `T_PERMISSION_TYPE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `cnName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_permission_type_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_PERMISSION_TYPE_AUTH`;
CREATE TABLE `T_PERMISSION_TYPE_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `cnName` varchar(255) DEFAULT NULL,
  `enName` varchar(255) DEFAULT NULL,
  `permissionId` bigint(20) NOT NULL,
  `permissionTypeId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_plugin
-- ----------------------------
DROP TABLE IF EXISTS `T_PLUGIN`;
CREATE TABLE `T_PLUGIN` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `categoryId` bigint(20) NOT NULL,
  `cnName` varchar(255) DEFAULT NULL,
  `detail` longtext,
  `enName` varchar(255) DEFAULT NULL,
  `projectId` bigint(20) NOT NULL,
  `tutorial` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_plugin_category
-- ----------------------------
DROP TABLE IF EXISTS `T_PLUGIN_CATEGORY`;
CREATE TABLE `T_PLUGIN_CATEGORY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_plugin_resource
-- ----------------------------
DROP TABLE IF EXISTS `T_PLUGIN_RESOURCE`;
CREATE TABLE `T_PLUGIN_RESOURCE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `downloadUrl` varchar(255) DEFAULT NULL,
  `pluginVersionId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `absFilePath` varchar(255) DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `uploadStatus` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_plugin_version
-- ----------------------------
DROP TABLE IF EXISTS `T_PLUGIN_VERSION`;
CREATE TABLE `T_PLUGIN_VERSION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `customDownloadUrl` varchar(255) DEFAULT NULL,
  `customResPackageUrl` varchar(255) DEFAULT NULL,
  `downloadUrl` varchar(255) DEFAULT NULL,
  `osType` int(11) DEFAULT NULL,
  `pkgGitRepoUrl` varchar(255) DEFAULT NULL,
  `pluginId` bigint(20) NOT NULL,
  `resPackageUrl` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `versionDescription` varchar(255) DEFAULT NULL,
  `versionNo` varchar(255) DEFAULT NULL,
  `uploadStatus` int(11) DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_process
-- ----------------------------
DROP TABLE IF EXISTS `T_PROCESS`;
CREATE TABLE `T_PROCESS` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `detail` varchar(1000) DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  `name` varchar(1000) NOT NULL,
  `projectId` bigint(20) NOT NULL,
  `startDate` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `weight` int(11) NOT NULL,
  `pinYinHeadChar` varchar(255) DEFAULT NULL,
  `pinYinName` varchar(255) DEFAULT NULL,
  `finishDate` datetime DEFAULT NULL,
  `progress` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_process_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_PROCESS_AUTH`;
CREATE TABLE `T_PROCESS_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `memberId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_MEMBERID_ROLEID` (`memberId`,`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_process_member
-- ----------------------------
DROP TABLE IF EXISTS `T_PROCESS_MEMBER`;
CREATE TABLE `T_PROCESS_MEMBER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `processId` bigint(20) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_PROCESSID_USERID` (`processId`,`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_project
-- ----------------------------
DROP TABLE IF EXISTS `T_PROJECT`;
CREATE TABLE `T_PROJECT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `bizCompanyId` varchar(255) DEFAULT NULL,
  `bizCompanyName` varchar(255) DEFAULT NULL,
  `bizLicense` int(11) DEFAULT NULL,
  `categoryId` bigint(20) NOT NULL,
  `detail` text,
  `name` varchar(1000) NOT NULL,
  `productionEMMUrl` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `teamId` bigint(20) NOT NULL,
  `testingEMMUrl` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `finishDate` date DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `pinYinHeadChar` varchar(255) DEFAULT NULL,
  `pinYinName` varchar(255) DEFAULT NULL,
  `progress` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_project_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_PROJECT_AUTH`;
CREATE TABLE `T_PROJECT_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `memberId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_MEMBERID_ROLEID` (`memberId`,`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_project_category
-- ----------------------------
DROP TABLE IF EXISTS `T_PROJECT_CATEGORY`;
CREATE TABLE `T_PROJECT_CATEGORY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_project_member
-- ----------------------------
DROP TABLE IF EXISTS `T_PROJECT_MEMBER`;
CREATE TABLE `T_PROJECT_MEMBER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `projectId` bigint(20) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_PROJECTID_USERID` (`projectId`,`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_project_sort
-- ----------------------------
DROP TABLE IF EXISTS `T_PROJECT_SORT`;
CREATE TABLE `T_PROJECT_SORT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `projectId` bigint(20) DEFAULT NULL,
  `sort` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_resources
-- ----------------------------
DROP TABLE IF EXISTS `T_RESOURCES`;
CREATE TABLE `T_RESOURCES` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `filePath` varchar(255) NOT NULL,
  `fileSize` bigint(20) DEFAULT NULL,
  `name` varchar(1000) NOT NULL,
  `parentId` bigint(20) NOT NULL,
  `projectId` bigint(20) NOT NULL,
  `type` varchar(255) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `userName` varchar(255) NOT NULL,
  `sourceType` int(11) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `isPublic` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `T_ROLE`;
CREATE TABLE `T_ROLE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `allowdel` int(11) DEFAULT NULL,
  `cnName` varchar(255) DEFAULT NULL,
  `enName` varchar(255) DEFAULT NULL,
  `parentId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_role_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_ROLE_AUTH`;
CREATE TABLE `T_ROLE_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `premissionId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_settings_config
-- ----------------------------
DROP TABLE IF EXISTS `T_SETTINGS_CONFIG`;
CREATE TABLE `T_SETTINGS_CONFIG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `describ` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `T_TAG`;
CREATE TABLE `T_TAG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK`;
CREATE TABLE `T_TASK` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `appId` bigint(20) NOT NULL,
  `deadline` date DEFAULT NULL,
  `detail` longtext,
  `lastStatusUpdateTime` datetime DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `processId` bigint(20) NOT NULL,
  `repeatable` int(11) DEFAULT NULL,
  `groupId` bigint(20) NOT NULL,
  `finishDate` datetime DEFAULT NULL,
  `finishUserId` bigint(20) NOT NULL,
  `oldStatus` int(1) DEFAULT '-1',
  `status` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_AUTH`;
CREATE TABLE `T_TASK_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `memberId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_MEMBERID_ROLEID` (`memberId`,`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_bak
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_BAK`;
CREATE TABLE `T_TASK_BAK` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `appId` bigint(20) NOT NULL,
  `deadline` date DEFAULT NULL,
  `detail` varchar(200) DEFAULT NULL,
  `lastStatusUpdateTime` datetime DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `priority` int(11) DEFAULT NULL,
  `processId` bigint(20) NOT NULL,
  `progress` int(11) NOT NULL,
  `repeatable` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_comment
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_COMMENT`;
CREATE TABLE `T_TASK_COMMENT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `content` text,
  `replyTo` bigint(20) NOT NULL,
  `taskId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_comment_bak
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_COMMENT_BAK`;
CREATE TABLE `T_TASK_COMMENT_BAK` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `content` text,
  `replyTo` bigint(20) NOT NULL,
  `taskId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_group
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_GROUP`;
CREATE TABLE `T_TASK_GROUP` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `del` int(1) NOT NULL,
  `projectId` bigint(20) NOT NULL,
  `sort` int(11) DEFAULT NULL,
  `pinYinHeadChar` varchar(255) DEFAULT NULL,
  `pinYinName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `projectId` (`projectId`,`name`,`del`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_group_sort
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_GROUP_SORT`;
CREATE TABLE `T_TASK_GROUP_SORT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL COMMENT '项目ID',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `groupId` bigint(20) NOT NULL COMMENT '任务分组ID',
  `sort` int(2) NOT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `PROJECTID_USERID` (`projectId`,`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_leaf
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_LEAF`;
CREATE TABLE `T_TASK_LEAF` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `detail` varchar(1000) NOT NULL COMMENT '子任务详细信息',
  `processId` bigint(20) NOT NULL COMMENT '子任务所在流程ID',
  `appId` bigint(20) NOT NULL DEFAULT '-1' COMMENT '子任务所属应用ID',
  `deadline` date NOT NULL,
  `status` int(1) NOT NULL,
  `lastStatusUpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后状态改变时间',
  `createdAt` datetime NOT NULL,
  `updatedAt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `del` int(1) NOT NULL,
  `topTaskId` bigint(20) NOT NULL,
  `finishDate` datetime DEFAULT NULL,
  `finishUserId` bigint(20) NOT NULL DEFAULT '-1',
  `managerUserId` bigint(20) NOT NULL COMMENT '子任务负责人ID',
  PRIMARY KEY (`id`),
  KEY `INDEX_TOPTASKID` (`topTaskId`),
  KEY `INDEX_LEADERUSERID` (`managerUserId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_member
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_MEMBER`;
CREATE TABLE `T_TASK_MEMBER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `taskId` bigint(20) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_TASKID_USERID` (`taskId`,`userId`),
  KEY `INDEX_USERID_TASKID` (`userId`,`taskId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_status_sort
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_STATUS_SORT`;
CREATE TABLE `T_TASK_STATUS_SORT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `sort` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_survey
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_SURVEY`;
CREATE TABLE `T_TASK_SURVEY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL COMMENT '任务ID',
  `managerUserId` bigint(20) NOT NULL COMMENT '用户ID',
  `taskAt` varchar(10) NOT NULL COMMENT '任务日期',
  `stockNum` bigint(20) NOT NULL COMMENT '存量任务数',
  `addNum` bigint(20) NOT NULL COMMENT '新增任务数',
  `completeNum` bigint(20) NOT NULL COMMENT '已完成任务数',
  PRIMARY KEY (`id`),
  KEY `INDEX_TOPTASKID` (`projectId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_task_tag
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_TAG`;
CREATE TABLE `T_TASK_TAG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `tagId` bigint(20) NOT NULL,
  `taskId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_team
-- ----------------------------
DROP TABLE IF EXISTS `T_TEAM`;
CREATE TABLE `T_TEAM` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `detail` varchar(1000) DEFAULT NULL,
  `enterpriseId` varchar(255) DEFAULT NULL,
  `enterpriseName` varchar(255) DEFAULT NULL,
  `name` varchar(1000) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `pinYinHeadChar` varchar(255) DEFAULT NULL,
  `pinYinName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_team_analy
-- ----------------------------
DROP TABLE IF EXISTS `T_TEAM_ANALY`;
CREATE TABLE `T_TEAM_ANALY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `teamId` bigint(20) NOT NULL,
  `projectId` bigint(20) NOT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL,
  `del` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_TEAMID` (`teamId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_team_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_TEAM_AUTH`;
CREATE TABLE `T_TEAM_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `memberId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_MEMBERID_ROLEID` (`memberId`,`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_team_group
-- ----------------------------
DROP TABLE IF EXISTS `T_TEAM_GROUP`;
CREATE TABLE `T_TEAM_GROUP` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `teamId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_team_member
-- ----------------------------
DROP TABLE IF EXISTS `T_TEAM_MEMBER`;
CREATE TABLE `T_TEAM_MEMBER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `groupId` bigint(20) DEFAULT NULL,
  `joinTime` datetime DEFAULT NULL,
  `teamId` bigint(20) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `INDEX_TEAMID_USERID` (`teamId`,`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_timedate
-- ----------------------------
DROP TABLE IF EXISTS `T_TIMEDATE`;
CREATE TABLE `T_TIMEDATE` (
  `timeDate` varchar(10) NOT NULL COMMENT '任务ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_topic
-- ----------------------------
DROP TABLE IF EXISTS `T_TOPIC`;
CREATE TABLE `T_TOPIC` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `detail` longtext,
  `projectId` bigint(20) DEFAULT NULL,
  `title` longtext,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_topic_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_TOPIC_AUTH`;
CREATE TABLE `T_TOPIC_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `memberId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_topic_comment
-- ----------------------------
DROP TABLE IF EXISTS `T_TOPIC_COMMENT`;
CREATE TABLE `T_TOPIC_COMMENT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `detail` text,
  `replyTo` bigint(20) DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_topic_member
-- ----------------------------
DROP TABLE IF EXISTS `T_TOPIC_MEMBER`;
CREATE TABLE `T_TOPIC_MEMBER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `topicId` bigint(20) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_topic_resource
-- ----------------------------
DROP TABLE IF EXISTS `T_TOPIC_RESOURCE`;
CREATE TABLE `T_TOPIC_RESOURCE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `resourceId` bigint(20) DEFAULT NULL,
  `topicCId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `T_USER`;
CREATE TABLE `T_USER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `account` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `cellphone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `joinPlat` int(11) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `qq` varchar(255) DEFAULT NULL,
  `receiveMail` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `userlevel` int(11) DEFAULT NULL,
  `initDemoStatus` int(11) DEFAULT NULL,
  `bindEmail` varchar(255) DEFAULT NULL,
  `pinYinHeadChar` varchar(255) DEFAULT NULL,
  `pinYinName` varchar(255) DEFAULT NULL,
  `nickName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_oavkgrxskmummfreq8eubojwl` (`account`),
  UNIQUE KEY `UK_buk4r0o8evx2b40lql6umufwv` (`email`),
  KEY `INDEX_ACCOUNT` (`account`),
  KEY `INDEX_USERNAME_PINYINHEADCHAR_PINYINNAME` (`userName`,`pinYinHeadChar`,`pinYinName`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user_auth
-- ----------------------------
DROP TABLE IF EXISTS `T_USER_AUTH`;
CREATE TABLE `T_USER_AUTH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `del` tinyint(4) DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  `roleId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_userchoiced
-- ----------------------------
DROP TABLE IF EXISTS `T_USERCHOICED`;
CREATE TABLE `T_USERCHOICED` (
  `projectId` bigint(20) NOT NULL COMMENT '任务ID',
  `loginUserId` bigint(20) NOT NULL COMMENT '登陆人ID',
  `userId` varchar(100) NOT NULL COMMENT '被选中的成员ID',
  PRIMARY KEY (`projectId`,`loginUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
