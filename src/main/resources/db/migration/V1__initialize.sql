/*
 Navicat Premium Data Transfer

 Source Server         : mysqlTestService192,168.1.254
 Source Server Type    : MySQL
 Source Server Version : 50641
 Source Host           : 192.168.1.254:3306
 Source Schema         : monitor

 Target Server Type    : MySQL
 Target Server Version : 50641
 File Encoding         : 65001

 Date: 20/06/2019 15:34:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `in_time` datetime(0) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  CONSTRAINT `admin_user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES (1, 'admin', '$2a$10$0F6RXnrQDF8SsOudYk7uhuWlqq3kjPuPm4UGeDCj0gvO8xj2pbZ4y', '2019-06-20 11:11:11', 1);

-- ----------------------------
-- Table structure for code
-- ----------------------------
DROP TABLE IF EXISTS `code`;
CREATE TABLE `code`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `in_time` datetime(0) NOT NULL,
  `expire_time` datetime(0) NOT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `mobile` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL  ,
  `used` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `code_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for flyway_schema_history
-- ----------------------------
DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE `flyway_schema_history`  (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `script` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `checksum` int(11) NULL DEFAULT NULL,
  `installed_by` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `installed_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`) USING BTREE,
  INDEX `flyway_schema_history_s_idx`(`success`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `pid` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE,
  UNIQUE INDEX `value`(`value`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

INSERT INTO `permission` (`id`, `name`, `value`, `pid`)
VALUES
	(1, '首页', 'index', 0),
	(6, '验证码', 'code', 0),
	(8, '权限', 'permission', 0),
	(9, '系统', 'system', 0),
	(10, '后台用户', 'admin_user', 0),
	(11, '仪表盘', 'index:index', 1),
	(25, '验证码列表', 'code:list', 6),
	(30, '权限列表', 'permission:list', 8),
	(31, '权限编辑', 'permission:edit', 8),
	(32, '权限删除', 'permission:delete', 8),
	(33, '角色', 'role', 0),
	(34, '日志', 'log', 0),
	(35, '角色列表', 'role:list', 33),
	(36, '角色编辑', 'role:edit', 33),
	(37, '角色删除', 'role:delete', 33),
	(38, '系统设置', 'system:edit', 9),
	(39, '后台用户列表', 'admin_user:list', 10),
	(40, '后台用户编辑', 'admin_user:edit', 10),
	(41, '后台用户创建', 'admin_user:add', 10),
	(42, '日志列表', 'log:list', 34),
	(44, '权限添加', 'permission:add', 8);

/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;

INSERT INTO `role` (`id`, `name`)
VALUES
	(2,'审核员'),
	(1,'超级管理员');

/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table role_permission
# ------------------------------------------------------------

DROP TABLE IF EXISTS `role_permission`;

CREATE TABLE `role_permission` (
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  KEY `role_id` (`role_id`),
  KEY `permission_id` (`permission_id`),
  CONSTRAINT `role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;

INSERT INTO `role_permission` (`role_id`, `permission_id`)
VALUES
	(2, 11),
	(1, 11),
	(1, 45),
	(1, 46),
	(1, 48),
	(1, 49),
	(1, 43),
	(1, 25),
	(1, 30),
	(1, 31),
	(1, 32),
	(1, 44),
	(1, 38),
	(1, 39),
	(1, 40),
	(1, 41),
	(1, 35),
	(1, 36),
	(1, 37),
	(1, 42);

/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  `description` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `pid` int(11) NOT NULL DEFAULT 0,
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `option` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `reboot` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

LOCK TABLES `system_config` WRITE;
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
INSERT INTO `system_config` VALUES (1, 'admin_remember_me_max_age', '30', '登录后台记住我功能记住时间，单位：天', 23, 'number', NULL, 1);
INSERT INTO `system_config` VALUES (2, 'base_url', 'http://localhost:8080', '网站部署后访问的域名，注意这个后面没有 \"/\"', 23, 'url', NULL, 0);
INSERT INTO `system_config` VALUES (4, 'cookie_domain', 'localhost', '存cookie时用到的域名，要与网站部署后访问的域名一致', 23, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (5, 'cookie_max_age', '604800', 'cookie有效期，单位秒，默认1周', 23, 'number', NULL, 0);
INSERT INTO `system_config` VALUES (6, 'cookie_name', 'user_token', '存cookie时用到的名称', 23, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (11, 'intro', '<h5>基于Java写的服务器监控</h5><p>在这里，您可以配置需要监控的服务，以及用于通知的方式，接收服务异常通知的人员！</p>', '项目介绍', 23, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (12, 'mail_host', 'smtp.qq.com', '邮箱的smtp服务器地址', 24, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (13, 'mail_password', '', '发送邮件的邮箱密码', 24, 'password', NULL, 0);
INSERT INTO `system_config` VALUES (14, 'mail_username', 'xxoo@qq.com', '发送邮件的邮箱地址', 24, 'email', NULL, 0);
INSERT INTO `system_config` VALUES (15, 'name', 'monitor控制台', '站点名称', 23, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (16, 'page_size', '20', '分页每页条数', 23, 'number', NULL, 0);
INSERT INTO `system_config` VALUES (18, 'static_url', 'http://localhost:8080/static/upload/', '静态文件访问地址，主要用于上传图片的访问，注意最后有个\"/\"', 25, 'url', NULL, 0);
INSERT INTO `system_config` VALUES (20, 'upload_avatar_size_limit', '2', '上传头像文件大小，单位MB，默认2MB', 25, 'number', NULL, 0);
INSERT INTO `system_config` VALUES (21, 'upload_path', '/opt/monitor/static/upload/', '上传文件的路径，注意最后有个\"/\"', 25, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (23, NULL, NULL, '基础配置', 0, NULL, NULL, 0);
INSERT INTO `system_config` VALUES (24, NULL, NULL, '邮箱配置', 0, NULL, NULL, 0);
INSERT INTO `system_config` VALUES (25, NULL, NULL, '上传配置', 0, NULL, NULL, 0);
INSERT INTO `system_config` VALUES (27, NULL, NULL, 'Redis配置', 0, NULL, NULL, 0);
INSERT INTO `system_config` VALUES (29, 'redis_host', '', 'redis服务host地址', 27, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (30, 'redis_port', '', 'redis服务端口（默认: 6379）', 27, 'number', NULL, 0);
INSERT INTO `system_config` VALUES (31, 'redis_password', '', 'redis服务密码', 27, 'password', NULL, 0);
INSERT INTO `system_config` VALUES (32, 'redis_timeout', '2000', '网站连接redis服务超时时间，单位毫秒', 27, 'number', NULL, 0);
INSERT INTO `system_config` VALUES (33, 'redis_database', '0', '网站连接redis服务的哪个数据库，默认0号数据库，取值范围0-15', 27, 'number', NULL, 0);
INSERT INTO `system_config` VALUES (35, NULL, NULL, 'Elasticsearch配置', 0, NULL, NULL, 0);
INSERT INTO `system_config` VALUES (36, 'elasticsearch_host', '', 'elasticsearch服务的地址', 35, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (37, 'elasticsearch_port', '', 'elasticsearch服务的http端口', 35, 'number', NULL, 0);
INSERT INTO `system_config` VALUES (38, 'elasticsearch_index', '', '索引的名字', 35, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (39, 'search', '0', '是否开启搜索功能（如果开启，需要额外启动一个ES服务，并填好ES相关的配置）', 35, 'radio', NULL, 0);
INSERT INTO `system_config` VALUES (40, NULL, NULL, 'Github配置', 0, '', NULL, 0);
INSERT INTO `system_config` VALUES (41, 'oauth_github_client_id', '', 'Github登录配置项ClientId', 40, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (42, 'oauth_github_client_secret', '', 'Github登录配置项ClientSecret', 40, 'password', NULL, 0);
INSERT INTO `system_config` VALUES (43, 'oauth_github_callback_url', '', 'Github登录配置项回调地址', 40, 'url', NULL, 0);
INSERT INTO `system_config` VALUES (48, 'theme', 'default', '系统主题', 23, 'select', 'default', 0);
INSERT INTO `system_config` VALUES (59, 'sms_tencent_app_id', ' ', '腾讯云短信appId', 61, 'number', NULL, 0);
INSERT INTO `system_config` VALUES (60, 'sms_tencent_app_key', ' ', '腾讯云短信appKey', 61, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (61, NULL, NULL, '腾讯云短信配置', 0, NULL, NULL, 0);
INSERT INTO `system_config` VALUES (62, NULL, '', '企业微信配置', 0, NULL, NULL, 0);
INSERT INTO `system_config` VALUES (63, 'appId', '', '企业微信appId', 62, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (64, 'appSecret', '', '企业微信appSecret', 62, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (65, 'token', '', '企业微信token', 62, 'text', NULL, 0);
INSERT INTO `system_config` VALUES (66, 'agentId', '', '企业微信应用id（agentId）', 62, 'text', NULL, 0);

/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;

SET FOREIGN_KEY_CHECKS = 1;
