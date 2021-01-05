/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80019
Source Host           : localhost:3306
Source Database       : databasedesign_project

Target Server Type    : MYSQL
Target Server Version : 80019
File Encoding         : 65001

Date: 2021-01-05 22:13:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for area
-- ----------------------------
DROP TABLE IF EXISTS `area`;
CREATE TABLE `area` (
  `area_level` int NOT NULL,
  `actual_patient_num` int NOT NULL,
  `ward_num` int NOT NULL,
  `bed_num` int NOT NULL,
  `nurse_num` int NOT NULL,
  PRIMARY KEY (`area_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of area
-- ----------------------------
INSERT INTO `area` VALUES ('0', '0', '0', '0', '0');
INSERT INTO `area` VALUES ('1', '0', '2', '8', '4');
INSERT INTO `area` VALUES ('2', '0', '3', '6', '4');
INSERT INTO `area` VALUES ('3', '0', '4', '4', '4');
INSERT INTO `area` VALUES ('4', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `area` int NOT NULL,
  `ward` int NOT NULL,
  `bed_index` int NOT NULL,
  `patient_ID` int DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of bed
-- ----------------------------
INSERT INTO `bed` VALUES ('1', '1', '101', '1', '1');
INSERT INTO `bed` VALUES ('2', '1', '101', '2', '2');
INSERT INTO `bed` VALUES ('3', '1', '101', '3', '3');
INSERT INTO `bed` VALUES ('4', '1', '101', '4', '4');
INSERT INTO `bed` VALUES ('5', '1', '102', '1', '5');
INSERT INTO `bed` VALUES ('6', '1', '102', '2', '6');
INSERT INTO `bed` VALUES ('7', '1', '102', '3', '7');
INSERT INTO `bed` VALUES ('8', '1', '102', '4', '8');
INSERT INTO `bed` VALUES ('9', '2', '201', '1', '11');
INSERT INTO `bed` VALUES ('10', '2', '201', '2', '12');
INSERT INTO `bed` VALUES ('11', '2', '202', '1', '13');
INSERT INTO `bed` VALUES ('12', '2', '202', '2', '14');
INSERT INTO `bed` VALUES ('13', '2', '203', '1', '15');
INSERT INTO `bed` VALUES ('14', '2', '203', '2', '0');
INSERT INTO `bed` VALUES ('15', '3', '301', '1', '16');
INSERT INTO `bed` VALUES ('16', '3', '302', '1', '17');
INSERT INTO `bed` VALUES ('17', '3', '303', '1', '18');
INSERT INTO `bed` VALUES ('18', '3', '304', '1', '19');

-- ----------------------------
-- Table structure for chief_nurse
-- ----------------------------
DROP TABLE IF EXISTS `chief_nurse`;
CREATE TABLE `chief_nurse` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `area` int NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of chief_nurse
-- ----------------------------
INSERT INTO `chief_nurse` VALUES ('1', 'Fay', '123456', '1');
INSERT INTO `chief_nurse` VALUES ('2', 'Grace', '123456', '2');
INSERT INTO `chief_nurse` VALUES ('3', 'Jean', '123456', '3');

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `area` int NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES ('1', 'Aaron', '123456', '1');
INSERT INTO `doctor` VALUES ('2', 'Carl', '123456', '2');
INSERT INTO `doctor` VALUES ('3', 'Dora', '123456', '3');

-- ----------------------------
-- Table structure for emergency_nurse
-- ----------------------------
DROP TABLE IF EXISTS `emergency_nurse`;
CREATE TABLE `emergency_nurse` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of emergency_nurse
-- ----------------------------
INSERT INTO `emergency_nurse` VALUES ('1', 'Karen', '123456');
INSERT INTO `emergency_nurse` VALUES ('2', 'Lisa', '123456');
INSERT INTO `emergency_nurse` VALUES ('3', 'Milly', '123456');
INSERT INTO `emergency_nurse` VALUES ('4', 'Nancy', '123456');

-- ----------------------------
-- Table structure for patient
-- ----------------------------
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `level` int NOT NULL,
  `area` int NOT NULL,
  `bed_ID` int DEFAULT NULL,
  `tempreature` varchar(255) DEFAULT NULL,
  `normal_tempreature_num` int(10) unsigned zerofill DEFAULT NULL,
  `normal_test_num` int(10) unsigned zerofill DEFAULT NULL,
  `nurse_ID` int DEFAULT NULL,
  `state` int NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of patient
-- ----------------------------
INSERT INTO `patient` VALUES ('1', 'Adam', '1', '1', '1', '36.6,36.7', '0000000002', '0000000001', '1', '0');
INSERT INTO `patient` VALUES ('2', 'Alex', '1', '1', '2', null, '0000000000', '0000000000', '1', '0');
INSERT INTO `patient` VALUES ('3', 'Ben', '1', '1', '3', null, '0000000000', '0000000000', '2', '0');
INSERT INTO `patient` VALUES ('4', 'Bob', '1', '1', '4', null, '0000000000', '0000000000', '2', '0');
INSERT INTO `patient` VALUES ('5', 'Cody', '1', '1', '5', null, '0000000000', '0000000000', '3', '0');
INSERT INTO `patient` VALUES ('6', 'Dylan', '1', '1', '6', null, '0000000000', '0000000000', '3', '0');
INSERT INTO `patient` VALUES ('7', 'Eric', '1', '1', '7', null, '0000000000', '0000000000', '3', '0');
INSERT INTO `patient` VALUES ('8', 'Evan', '1', '1', '8', null, '0000000000', '0000000000', '4', '0');
INSERT INTO `patient` VALUES ('9', 'George', '1', '4', '0', null, '0000000000', '0000000000', '0', '0');
INSERT INTO `patient` VALUES ('10', 'Hank', '1', '4', '0', null, '0000000000', '0000000000', '0', '0');
INSERT INTO `patient` VALUES ('11', 'Jeffery', '2', '2', '9', null, '0000000000', '0000000000', '5', '0');
INSERT INTO `patient` VALUES ('12', 'Jordan', '2', '2', '10', null, '0000000000', '0000000000', '5', '0');
INSERT INTO `patient` VALUES ('13', 'Jose', '2', '2', '11', null, '0000000000', '0000000000', '6', '0');
INSERT INTO `patient` VALUES ('14', 'Justin', '2', '2', '12', null, '0000000000', '0000000000', '7', '0');
INSERT INTO `patient` VALUES ('15', 'Lance', '2', '2', '13', null, '0000000000', '0000000000', '7', '0');
INSERT INTO `patient` VALUES ('16', 'Luke', '3', '3', '15', null, '0000000000', '0000000000', '9', '0');
INSERT INTO `patient` VALUES ('17', 'Max', '3', '3', '16', null, '0000000000', '0000000000', '10', '0');
INSERT INTO `patient` VALUES ('18', 'Mike', '3', '3', '17', null, '0000000000', '0000000000', '11', '0');
INSERT INTO `patient` VALUES ('19', 'Neil', '3', '3', '18', null, '0000000000', '0000000000', '12', '0');
INSERT INTO `patient` VALUES ('20', 'Norman', '3', '4', '0', null, '0000000000', '0000000000', '0', '0');

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `patient_ID` int NOT NULL,
  `result` varchar(255) NOT NULL,
  `date` datetime NOT NULL,
  `current_level` int NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of test
-- ----------------------------
INSERT INTO `test` VALUES ('1', '1', 'good', '2021-01-04 21:59:23', '1');

-- ----------------------------
-- Table structure for ward_nurse
-- ----------------------------
DROP TABLE IF EXISTS `ward_nurse`;
CREATE TABLE `ward_nurse` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `area` int NOT NULL,
  `max_patient_num` int NOT NULL,
  `actual_patient_num` int DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of ward_nurse
-- ----------------------------
INSERT INTO `ward_nurse` VALUES ('1', 'Paula', '123456', '1', '3', '2');
INSERT INTO `ward_nurse` VALUES ('2', 'Sally', '123456', '1', '3', '2');
INSERT INTO `ward_nurse` VALUES ('3', 'Tina', '123456', '1', '3', '3');
INSERT INTO `ward_nurse` VALUES ('4', 'Vita', '123456', '1', '3', '1');
INSERT INTO `ward_nurse` VALUES ('5', 'Zara', '123456', '2', '2', '2');
INSERT INTO `ward_nurse` VALUES ('6', 'Abby', '123456', '2', '2', '1');
INSERT INTO `ward_nurse` VALUES ('7', 'Betty', '123456', '2', '2', '2');
INSERT INTO `ward_nurse` VALUES ('8', 'Candy', '123456', '2', '2', '0');
INSERT INTO `ward_nurse` VALUES ('9', 'Dave', '123456', '3', '1', '1');
INSERT INTO `ward_nurse` VALUES ('10', 'Emma', '123456', '3', '1', '1');
INSERT INTO `ward_nurse` VALUES ('11', 'Fiona', '123456', '3', '1', '1');
INSERT INTO `ward_nurse` VALUES ('12', 'Gina', '123456', '3', '1', '1');
INSERT INTO `ward_nurse` VALUES ('13', 'Haley', '123456', '0', '0', '0');
INSERT INTO `ward_nurse` VALUES ('14', 'Irene', '123456', '0', '0', '0');
INSERT INTO `ward_nurse` VALUES ('15', 'Jenny', '123456', '0', '0', '0');
INSERT INTO `ward_nurse` VALUES ('16', 'Kay', '123456', '0', '0', '0');
INSERT INTO `ward_nurse` VALUES ('17', 'Lucy', '123456', '0', '0', '0');
INSERT INTO `ward_nurse` VALUES ('18', 'May', '123456', '0', '0', '0');
INSERT INTO `ward_nurse` VALUES ('19', 'Nina', '123456', '0', '0', '0');
INSERT INTO `ward_nurse` VALUES ('20', 'Polly', '123456', '0', '0', '0');
