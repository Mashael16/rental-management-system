-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: car_rental
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cars`
--

DROP TABLE IF EXISTS `cars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cars` (
  `car_id` int NOT NULL AUTO_INCREMENT,
  `factory` varchar(255) DEFAULT NULL,
  `model` varchar(255) NOT NULL,
  `year` int NOT NULL,
  `type` varchar(50) NOT NULL,
  `price_per_day` decimal(10,2) NOT NULL,
  `available` tinyint(1) DEFAULT '1',
  `stock` int DEFAULT '0',
  `availability_status` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`car_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cars`
--

LOCK TABLES `cars` WRITE;
/*!40000 ALTER TABLE `cars` DISABLE KEYS */;
INSERT INTO `cars` VALUES (2,'Honda','Civic',2019,'Sedan',500.00,1,1,0),(3,'Ford','Escape',2021,'SUV',690.00,1,3,1),(4,'Chevrolet','Malibu',2018,'Sedan',480.00,1,2,0),(5,'Jeep','Wrangler',2020,'SUV',580.00,1,2,1),(6,'Tesla','Model 3',2021,'Electric',1600.00,1,2,1),(7,'BMW','X5',2019,'SUV',1540.00,1,1,1),(8,'Hyundai','Elantra',2020,'Sedan',450.00,1,-2,0),(9,'Ford','',2020,'Sedan',950.00,1,3,1),(10,'Nissan','Altima',2021,'Sedan',470.00,1,1,0),(11,'Audi','A4',2019,'Sedan',800.00,1,0,0),(12,'Mercedes-Benz','C-Class',2020,'Sedan',590.00,1,0,0),(13,'Volkswagen','Tiguan',2021,'SUV',600.00,1,1,1),(14,'Subaru','Outback',2020,'SUV',520.00,1,2,1),(15,'Porsche','Cayenne',2019,'SUV',600.00,1,3,1),(16,'','CX-5',2020,'',570.00,1,1,1),(17,'Kia','Optima',2019,'Sedan',460.00,1,2,1),(18,'Lexus','RX 350',2021,'SUV',600.00,1,4,1),(19,'Chrysler','Pacifica',2020,'Minivan',510.00,1,2,1),(20,'Dodge','Charger',2019,'Sedan',600.00,1,2,1),(21,'Toyota','datson 240z',1973,'Sports Car',1300.00,1,2,1),(22,'Ford','GT40',1964,'Sports Car',9300.00,1,1,1),(23,'Bugatti','Chiron',2025,'Sports Car',36000.00,1,2,1);
/*!40000 ALTER TABLE `cars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rent`
--

DROP TABLE IF EXISTS `rent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rent` (
  `rent_id` int NOT NULL AUTO_INCREMENT,
  `user_email` varchar(255) NOT NULL,
  `car_id` int NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `status` enum('active','returned','canceled','pending') NOT NULL,
  `fee` double DEFAULT '0',
  `overdue_days` int DEFAULT '0',
  PRIMARY KEY (`rent_id`),
  KEY `user_email` (`user_email`),
  KEY `car_id` (`car_id`),
  CONSTRAINT `rent_ibfk_1` FOREIGN KEY (`user_email`) REFERENCES `users` (`email`),
  CONSTRAINT `rent_ibfk_2` FOREIGN KEY (`car_id`) REFERENCES `cars` (`car_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rent`
--

LOCK TABLES `rent` WRITE;
/*!40000 ALTER TABLE `rent` DISABLE KEYS */;
INSERT INTO `rent` VALUES (43,'mashael@gmail.com',5,'2024-12-13','2024-12-14',950.00,'returned',0,0),(44,'mashael@gmail.com',15,'2024-12-13','2024-12-14',600.00,'returned',0,0),(45,'ramah@gmail.com',4,'2024-12-12','2024-12-13',480.00,'returned',0,0),(46,'ramah@gmail.com',2,'2024-12-12','2024-12-13',500.00,'active',0,0),(47,'ramah@gmail.com',17,'2024-12-12','2024-12-13',460.00,'active',0,0);
/*!40000 ALTER TABLE `rent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'ramah@gmail.com','12341234'),(2,'hoor@gmail.com','12345678'),(3,'rawan@gmail.com','34213421'),(4,'nora@gmail.com','12345678'),(5,'Mashael@gmail.com','12312312');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-15 19:24:33
