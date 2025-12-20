/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.8.5-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: blood_bank
-- ------------------------------------------------------
-- Server version	11.8.5-MariaDB-2+b2 from Debian

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `blood_requests`
--

DROP TABLE IF EXISTS `blood_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `blood_requests` (
  `request_id` int(11) NOT NULL AUTO_INCREMENT,
  `hospital_name` varchar(100) DEFAULT NULL,
  `blood_group` varchar(5) DEFAULT NULL,
  `units_requested` int(11) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Pending',
  `request_date` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`request_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blood_requests`
--

LOCK TABLES `blood_requests` WRITE;
/*!40000 ALTER TABLE `blood_requests` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `blood_requests` VALUES
(1,'City Hospital','O-',2,'Rejected','2025-12-18 16:03:54'),
(2,'Red Cross','B+',1,'Rejected','2025-12-18 16:03:54'),
(3,'tester','AB+',4,'Approved','2025-12-19 14:35:12'),
(4,'Lumbini Provincial Hospital','O-',3,'Rejected','2025-12-19 15:15:59'),
(5,'Teaching Hospital','A-',2,'Approved','2025-12-19 15:15:59'),
(6,'Norvic Hospital','B+',5,'Approved','2025-12-19 15:15:59'),
(7,'City Care Center','AB-',1,'Approved','2025-12-19 15:15:59'),
(8,'City General Hospital','B-',3,'Pending','2025-12-20 06:12:33'),
(9,'LifeCare Center','O-',5,'Pending','2025-12-20 06:12:33'),
(10,'Red Cross Clinic','A-',2,'Pending','2025-12-20 06:12:33');
/*!40000 ALTER TABLE `blood_requests` ENABLE KEYS */;
UNLOCK TABLES;
commit;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_uca1400_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER update_stock_after_request
AFTER UPDATE ON blood_requests
FOR EACH ROW
BEGIN
    IF NEW.status = 'Approved' AND OLD.status = 'Pending' THEN
        UPDATE blood_stock_summary 
        SET total_units = total_units - NEW.units_requested
        WHERE blood_group = NEW.blood_group;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Temporary table structure for view `blood_stock_summary`
--

DROP TABLE IF EXISTS `blood_stock_summary`;
/*!50001 DROP VIEW IF EXISTS `blood_stock_summary`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `blood_stock_summary` AS SELECT
 1 AS `blood_group`,
  1 AS `total_units` */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `donors`
--

DROP TABLE IF EXISTS `donors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `donors` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `blood_group` varchar(5) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `donation_date` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donors`
--

LOCK TABLES `donors` WRITE;
/*!40000 ALTER TABLE `donors` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `donors` VALUES
(1,'Suman Hamal','O+','9801234567','Butwal','2025-12-20 07:55:25'),
(2,'Anjali Thapa','B+','9811223344','Kathmandu','2025-12-20 07:55:25'),
(3,'Bipul Chettri','A-','9844556677','Pokhara','2025-12-20 07:55:25'),
(4,'Manoj Sharma','B+','9392902222','38433','2025-12-20 14:36:28');
/*!40000 ALTER TABLE `donors` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `fulfilled_donations`
--

DROP TABLE IF EXISTS `fulfilled_donations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `fulfilled_donations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `request_id` int(11) DEFAULT NULL,
  `donor_id` int(11) DEFAULT NULL,
  `units_donated` int(11) DEFAULT NULL,
  `donation_date` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `request_id` (`request_id`),
  KEY `donor_id` (`donor_id`),
  CONSTRAINT `fulfilled_donations_ibfk_1` FOREIGN KEY (`request_id`) REFERENCES `blood_requests` (`request_id`),
  CONSTRAINT `fulfilled_donations_ibfk_2` FOREIGN KEY (`donor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fulfilled_donations`
--

LOCK TABLES `fulfilled_donations` WRITE;
/*!40000 ALTER TABLE `fulfilled_donations` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `fulfilled_donations` VALUES
(2,2,8,2,'2025-12-19 15:17:32');
/*!40000 ALTER TABLE `fulfilled_donations` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `system_alerts`
--

DROP TABLE IF EXISTS `system_alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_alerts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `blood_group` varchar(5) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_alerts`
--

LOCK TABLES `system_alerts` WRITE;
/*!40000 ALTER TABLE `system_alerts` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `system_alerts` VALUES
(1,'O-','Critical Alert: Stock dropped below 5 units. Notify O- donors immediately.','2025-12-19 15:16:08'),
(2,'A-','Warning: A- stock is low (3 units remaining).','2025-12-19 15:16:08'),
(3,'B-','Emergency shortage of B- Negative. Please visit City Hospital.','2025-12-20 06:12:33');
/*!40000 ALTER TABLE `system_alerts` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `blood_group` varchar(5) DEFAULT NULL,
  `password` varchar(64) NOT NULL,
  `role` varchar(20) DEFAULT 'User',
  `points` int(11) DEFAULT 0,
  `last_donation_date` date DEFAULT NULL,
  `donation_count` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `users` VALUES
(1,'System Admin','admin',NULL,NULL,NULL,NULL,'240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','Admin',0,NULL,0),
(6,'Suman Hamal','suman123','suman@email.com','9801234567','Butwal','O+','9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c','User',1500,'2025-11-15',150),
(7,'Anjali Thapa','anjali_t','anjali@email.com','9811223344','Kathmandu','B+','9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c','User',950,'2025-10-20',95),
(8,'Bipul Chettri','bipul_c','bipul@email.com','9844556677','Pokhara','A-','9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c','User',400,'2025-12-05',40),
(10,'david kim','david','david@gmail.com','9867142433',NULL,'B-','9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c','User',30,NULL,3),
(20,'Maniesh Neupane','maniesh','maniesh@gmail.com','',NULL,'B+','9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c','User',0,NULL,0),
(24,'Super Admin','admin_boss',NULL,NULL,NULL,NULL,'3deff660926494dc46d9fb6b56c3fcb766d2e00c6ddf818729c3536606867164','Admin',0,NULL,0),
(25,'Manoj Sharma','Manoj','manoj@gmail.com','9867190211',NULL,'B+','61fe5486bf913ef18c0a592b1a1a895f0d237b910a169dac38faf685c11be2c1','User',10,'2025-12-20',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Final view structure for view `blood_stock_summary`
--

/*!50001 DROP VIEW IF EXISTS `blood_stock_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_uca1400_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `blood_stock_summary` AS select `blood_requests`.`blood_group` AS `blood_group`,sum(`blood_requests`.`units_requested`) AS `total_units` from `blood_requests` where `blood_requests`.`status` = 'Approved' group by `blood_requests`.`blood_group` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2025-12-20 20:35:28
