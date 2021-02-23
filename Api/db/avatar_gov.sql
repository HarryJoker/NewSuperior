/*
 Navicat Premium Data Transfer

 Source Server         : Local
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost
 Source Database       : avatar_gov

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : utf-8

 Date: 10/24/2019 22:58:14 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `tbl_admin`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_admin`;
CREATE TABLE `tbl_admin` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) NOT NULL DEFAULT '',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_advice`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_advice`;
CREATE TABLE `tbl_advice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL DEFAULT '0',
  `content` text NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_attachment`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_attachment`;
CREATE TABLE `tbl_attachment` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `filepath` text NOT NULL,
  `atttype` varchar(50) NOT NULL DEFAULT '',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `createtime` (`createtime`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_banner`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_banner`;
CREATE TABLE `tbl_banner` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL DEFAULT '',
  `image` varchar(255) NOT NULL DEFAULT '',
  `link` varchar(255) NOT NULL DEFAULT '',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '1：task，2：outLink，3：图片',
  `availably` int(11) NOT NULL DEFAULT '1' COMMENT '0：无效， 1：有效',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_childTask`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_childTask`;
CREATE TABLE `tbl_childTask` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `taskId` int(11) DEFAULT '0',
  `name` text,
  `content` text,
  `unitId` int(11) DEFAULT '0',
  `unitName` varchar(255) DEFAULT NULL,
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTIme` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `progress` int(11) DEFAULT '0',
  `status` int(11) DEFAULT NULL COMMENT '进度状态：较快，正常，缓慢，退回，完成',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tbl_grade`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_grade`;
CREATE TABLE `tbl_grade` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT '',
  `grade` double DEFAULT '0',
  `category` int(11) DEFAULT '0' COMMENT '1：',
  `categoryName` varchar(255) DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_maintask`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_maintask`;
CREATE TABLE `tbl_maintask` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '任务标题',
  `plan` text NOT NULL COMMENT '任务描述',
  `planDetail` text NOT NULL COMMENT '任务详细信息',
  `category` tinyint(4) NOT NULL DEFAULT '0' COMMENT '(1,政府工作报告，2：市委市政府重大决策， 3：建议提案， 4：会议议定事项， 5：',
  `reportType` int(11) NOT NULL DEFAULT '0' COMMENT '1：一月一次，2：具体时间，3：每周一次',
  `reportDate` varchar(50) DEFAULT '' COMMENT '报送时间',
  `attachmentids` varchar(255) DEFAULT '' COMMENT '附件',
  `createtime` varchar(255) NOT NULL DEFAULT '' COMMENT '创建时间',
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `reportTime` varchar(100) DEFAULT '',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0:新发布，1：调度完成，2：失效',
  `type` int(11) DEFAULT '0' COMMENT '子分类 \r\n\r\n市委市政府重大决策（排名分类）\r\n建议提案（1：人大，2：政协）\r\n会议议定事项（常务会议，',
  `typeName` varchar(255) DEFAULT '' COMMENT '子分类名称',
  `taskLabelXXXXX` varchar(255) DEFAULT '' COMMENT '会议议定事项（会议分类标签）',
  `sequenceXXXXX` int(11) NOT NULL DEFAULT '0' COMMENT '市委市政府重大决策排名顺序',
  `childTypeXXXXX` int(11) NOT NULL DEFAULT '0' COMMENT '建议提案（1：人大，2：政协）',
  `leaderUnitId` int(11) NOT NULL COMMENT '分管领导部门Id',
  `leaderUnitName` varchar(255) NOT NULL COMMENT '分管领导部门名称',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `createtime` (`createtime`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=1070 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_message`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_message`;
CREATE TABLE `tbl_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unitid` bigint(20) unsigned NOT NULL DEFAULT '0',
  `title` varchar(255) NOT NULL DEFAULT '',
  `content` text NOT NULL,
  `read` tinyint(4) NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1：图片，2：文本，3，link',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `createtime` (`createtime`) USING BTREE,
  KEY `unitid` (`unitid`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=314 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_report`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_report`;
CREATE TABLE `tbl_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `content` text COLLATE utf8_bin,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_reportadmin`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_reportadmin`;
CREATE TABLE `tbl_reportadmin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `password` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_task`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_task`;
CREATE TABLE `tbl_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `groupId` varchar(255) DEFAULT '' COMMENT '相同主任务唯一键',
  `name` varchar(50) DEFAULT '',
  `plan` text NOT NULL,
  `level` tinyint(4) NOT NULL DEFAULT '0',
  `category` tinyint(4) NOT NULL DEFAULT '0',
  `accept` int(11) DEFAULT '0',
  `priority` int(11) DEFAULT '0',
  `progress` int(11) NOT NULL DEFAULT '3' COMMENT '1：完成，2：快速，3：正常，4：缓慢',
  `unitId` int(11) NOT NULL DEFAULT '0',
  `reportType` int(11) NOT NULL DEFAULT '0' COMMENT '1：一月一次，2：具体时间，3：每周一次',
  `reportDate` varchar(50) DEFAULT '',
  `attachmentids` varchar(255) DEFAULT '',
  `createtime` varchar(255) NOT NULL DEFAULT '',
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `planDetail` varchar(255) NOT NULL DEFAULT '',
  `reportTime` varchar(100) DEFAULT '',
  `valid` int(11) DEFAULT '1' COMMENT '是否有效',
  `type` int(11) DEFAULT '0' COMMENT '子分类 \r\n\r\n市委市政府重大决策（排名分类）\r\n建议提案（1：人大，2：政协）\r\n会议议定事项（常务会议，',
  `typeName` varchar(255) DEFAULT '' COMMENT '子分类名称',
  `taskLabel` varchar(255) DEFAULT '' COMMENT '会议议定事项（会议分类标签）',
  `sequence` int(11) NOT NULL DEFAULT '0' COMMENT '市委市政府重大决策排名顺序',
  `childType` int(11) NOT NULL DEFAULT '0' COMMENT '建议提案（1：人大，2：政协）',
  `year` int(4) NOT NULL,
  `month` int(2) DEFAULT '0',
  `day` int(2) DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `createtime` (`createtime`) USING BTREE,
  KEY `name` (`name`) USING BTREE,
  KEY `unitid` (`unitId`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=7595 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_taskuserrelation`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_taskuserrelation`;
CREATE TABLE `tbl_taskuserrelation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT '0',
  `taskId` int(11) DEFAULT '0',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `createtime` (`createtime`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=3701 DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED;

-- ----------------------------
--  Table structure for `tbl_trace`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_trace`;
CREATE TABLE `tbl_trace` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `taskId` bigint(20) NOT NULL DEFAULT '0',
  `userId` bigint(20) NOT NULL DEFAULT '0',
  `content` text NOT NULL,
  `attachment` varchar(255) DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：进行中(新提报），1：已审核，2：退回，3：完成，4：过期未提报',
  `type` int(11) DEFAULT '0' COMMENT '0：正常的上报工作，1：系统自动生成督促，2：督查主动催报，3,任务领取，4：领导批示',
  `category` int(11) NOT NULL DEFAULT '0',
  `location` varchar(255) NOT NULL DEFAULT '',
  `address` varchar(255) NOT NULL DEFAULT '',
  `unitName` varchar(255) NOT NULL DEFAULT '',
  `unitId` int(11) NOT NULL DEFAULT '0',
  `remark` text NOT NULL,
  `cutScore` double NOT NULL DEFAULT '0',
  `cutScoreDetail` varchar(255) DEFAULT '',
  `verifyContent` varchar(255) NOT NULL DEFAULT '',
  `veracity` int(20) NOT NULL DEFAULT '0',
  `timeliness` int(20) NOT NULL DEFAULT '0',
  `completeness` int(20) NOT NULL DEFAULT '0',
  `feasibility` int(20) NOT NULL DEFAULT '0',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `progress` int(11) NOT NULL DEFAULT '0',
  `valid` int(11) DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `taskid` (`taskId`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=22831 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_unit`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_unit`;
CREATE TABLE `tbl_unit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parentid` bigint(20) unsigned NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL DEFAULT '',
  `logo` varchar(255) NOT NULL DEFAULT '',
  `role` int(11) NOT NULL DEFAULT '0' COMMENT '0：默认，1：县长，2：副县长，3：督查，4：单位',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `createtime` (`createtime`) USING BTREE,
  KEY `name` (`name`) USING BTREE,
  KEY `parentid` (`parentid`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=132 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_unitrelation`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_unitrelation`;
CREATE TABLE `tbl_unitrelation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unitId` int(11) NOT NULL,
  `parentId` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=140 DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED;

-- ----------------------------
--  Table structure for `tbl_unittask`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_unittask`;
CREATE TABLE `tbl_unittask` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mainTaskId` int(11) NOT NULL DEFAULT '0' COMMENT '主任务Id',
  `unitTask` varchar(255) NOT NULL DEFAULT '' COMMENT '部门任务',
  `unitPlan` text NOT NULL COMMENT '部门任务描述',
  `unitPlanDetail` varchar(255) NOT NULL DEFAULT '' COMMENT '部门任务详细描述',
  `unitTaskStatus` int(11) NOT NULL DEFAULT '0' COMMENT '0：未接收，1，正常，2快速，3，缓慢，4，完成',
  `unitId` int(11) NOT NULL DEFAULT '0' COMMENT '部门id',
  `unitName` varchar(30) NOT NULL DEFAULT '1' COMMENT '部门名称',
  `attachments` varchar(255) DEFAULT '' COMMENT '附件',
  `createtime` varchar(255) NOT NULL DEFAULT '',
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reportTime` varchar(100) DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `createtime` (`createtime`) USING BTREE,
  KEY `name` (`unitTask`) USING BTREE,
  KEY `unitid` (`unitId`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=4402 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_user`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user`;
CREATE TABLE `tbl_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(255) DEFAULT '',
  `logo` varchar(255) DEFAULT '',
  `name` varchar(255) DEFAULT '',
  `nick` varchar(255) DEFAULT '',
  `sex` varchar(255) DEFAULT '',
  `card` varchar(255) DEFAULT '',
  `duty` varchar(255) DEFAULT '',
  `jobDetail` varchar(255) DEFAULT '',
  `verify` int(11) DEFAULT '0' COMMENT '0：未审核，1：已审核，2：收回',
  `unitId` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `name` (`name`) USING BTREE,
  KEY `phone` (`phone`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=236 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tbl_version`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_version`;
CREATE TABLE `tbl_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `DUPLICATE KEY` (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED;

SET FOREIGN_KEY_CHECKS = 1;
