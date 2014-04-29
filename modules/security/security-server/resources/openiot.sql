
SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `openiot`
--

-- --------------------------------------------------------
CREATE DATABASE `openiot` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;
USE `openiot`;

-- --------------------------------------------------------

--
-- Table structure for table `locks`
--

CREATE TABLE IF NOT EXISTS `locks` (
  `application_id` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `expiration_date` datetime DEFAULT NULL,
  `unique_id` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table `locks`
--

INSERT INTO `locks` (`application_id`, `expiration_date`, `unique_id`) VALUES
('cas-ticket-registry-cleaner', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `PERMISSIONS`
--

CREATE TABLE IF NOT EXISTS `PERMISSIONS` (
  `name` varchar(30) COLLATE latin1_general_cs NOT NULL,
  `description` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table `PERMISSIONS`
--

INSERT INTO `PERMISSIONS` (`name`, `description`) VALUES
('*', 'All permissions'),
('admin:create_user', 'Create new users'),
('admin:delete_stream:s1', 'Delete stream s1'),
('admin:delete_stream:s2,s3', 'Delete streams s2 and s3'),
('admin:delete_user', 'Delete existing users'),
('stream:query:s1', 'Query stream s1'),
('stream:query:s2', 'Query stream s2'),
('stream:view:s1', 'View stream s1'),
('stream:view:s2', 'View stream s2');

-- --------------------------------------------------------

--
-- Table structure for table `RegisteredServiceImpl`
--

CREATE TABLE IF NOT EXISTS `RegisteredServiceImpl` (
  `expression_type` varchar(15) COLLATE latin1_general_cs NOT NULL DEFAULT 'ant',
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `allowedToProxy` tinyint(1) NOT NULL,
  `anonymousAccess` tinyint(1) NOT NULL,
  `description` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  `evaluation_order` int(11) NOT NULL,
  `ignoreAttributes` tinyint(1) NOT NULL,
  `name` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `serviceId` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `ssoEnabled` tinyint(1) NOT NULL,
  `theme` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `username_attr` varchar(256) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs AUTO_INCREMENT=5 ;

--
-- Dumping data for table `RegisteredServiceImpl`
--

INSERT INTO `RegisteredServiceImpl` (`expression_type`, `id`, `allowedToProxy`, `anonymousAccess`, `description`, `enabled`, `evaluation_order`, `ignoreAttributes`, `name`, `serviceId`, `ssoEnabled`, `theme`, `username_attr`) VALUES
('ant', 1, 1, 0, 'Service Manager', 1, 0, 0, 'Service Manager', 'https://localhost:8443/openiot-cas/services/j_acegi_cas_security_check', 0, NULL, NULL),
('ant', 2, 1, 0, 'testsecret1', 1, 0, 1, 'testkey1', 'http://localhost:9080/callback?client_name=CasOAuthWrapperClient', 1, 'Service1', NULL),
('ant', 3, 1, 0, 'oauth wrapper callback url', 1, 0, 0, 'HTTP', 'https://localhost:8443/openiot-cas/oauth2.0/callbackAuthorize', 1, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `ROLES`
--

CREATE TABLE IF NOT EXISTS `ROLES` (
  `name` varchar(20) COLLATE latin1_general_cs NOT NULL,
  `description` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table `ROLES`
--

INSERT INTO `ROLES` (`name`, `description`) VALUES
('admin', 'Administrator role'),
('darklord', 'Darklord role'),
('goodguy', 'Goodguy role'),
('president', 'President role'),
('schwartz', 'Schwartz role');

-- --------------------------------------------------------

--
-- Table structure for table `ROLES_PERMISSIONS`
--

CREATE TABLE IF NOT EXISTS `ROLES_PERMISSIONS` (
  `role_name` varchar(20) COLLATE latin1_general_cs NOT NULL,
  `permission_name` varchar(30) COLLATE latin1_general_cs NOT NULL,
  `service_id` int(11) NOT NULL,
  PRIMARY KEY (`role_name`,`permission_name`,`service_id`),
  KEY `RP_1` (`role_name`),
  KEY `RP_2` (`permission_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table `ROLES_PERMISSIONS`
--

INSERT INTO `ROLES_PERMISSIONS` (`role_name`, `permission_name`, `service_id`) VALUES
('admin', '*', 2),
('goodguy', 'admin:create_user', 2),
('goodguy', 'stream:query:s2', 2),
('president', 'admin:delete_stream:s2,s3', 2),
('president', 'stream:query:s1', 2);

-- --------------------------------------------------------

--
-- Table structure for table `rs_attributes`
--

CREATE TABLE IF NOT EXISTS `rs_attributes` (
  `RegisteredServiceImpl_id` bigint(20) NOT NULL,
  `a_name` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `a_id` int(11) NOT NULL,
  PRIMARY KEY (`RegisteredServiceImpl_id`,`a_id`),
  KEY `FK4322E153C595E1F` (`RegisteredServiceImpl_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

-- --------------------------------------------------------

--
-- Table structure for table `SERVICETICKET`
--

CREATE TABLE IF NOT EXISTS `SERVICETICKET` (
  `ID` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `NUMBER_OF_TIMES_USED` int(11) DEFAULT NULL,
  `CREATION_TIME` bigint(20) DEFAULT NULL,
  `EXPIRATION_POLICY` longblob NOT NULL,
  `LAST_TIME_USED` bigint(20) DEFAULT NULL,
  `PREVIOUS_LAST_TIME_USED` bigint(20) DEFAULT NULL,
  `FROM_NEW_LOGIN` tinyint(1) NOT NULL,
  `TICKET_ALREADY_GRANTED` tinyint(1) NOT NULL,
  `SERVICE` longblob NOT NULL,
  `ticketGrantingTicket_ID` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK7645ADE132A2C0E5` (`ticketGrantingTicket_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

-- --------------------------------------------------------

--
-- Table structure for table `TICKETGRANTINGTICKET`
--

CREATE TABLE IF NOT EXISTS `TICKETGRANTINGTICKET` (
  `ID` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `NUMBER_OF_TIMES_USED` int(11) DEFAULT NULL,
  `CREATION_TIME` bigint(20) DEFAULT NULL,
  `EXPIRATION_POLICY` longblob NOT NULL,
  `LAST_TIME_USED` bigint(20) DEFAULT NULL,
  `PREVIOUS_LAST_TIME_USED` bigint(20) DEFAULT NULL,
  `AUTHENTICATION` longblob NOT NULL,
  `EXPIRED` tinyint(1) NOT NULL,
  `SERVICES_GRANTED_ACCESS_TO` longblob NOT NULL,
  `ticketGrantingTicket_ID` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKB4C4CDDE32A2C0E5` (`ticketGrantingTicket_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

-- --------------------------------------------------------

--
-- Table structure for table `USERS`
--

CREATE TABLE IF NOT EXISTS `USERS` (
  `username` varchar(15) COLLATE latin1_general_cs NOT NULL,
  `email` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  `name` varchar(65) COLLATE latin1_general_cs DEFAULT NULL,
  `password` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table `USERS`
--

INSERT INTO `USERS` (`username`, `email`, `name`, `password`) VALUES
('admin', 'admin@example.com', 'Administrator', '5ebe2294ecd0e0f08eab7690d2a6ee69'),
('darkhelmet', 'u2@example.com', 'User P2', 'd9aaefa96ffeabb3a3bac5fdeadde3fa'),
('lonestarr', 'u3@example.com', 'User P3', '960c8c80adfcc7eee97eb6ebad135642'),
('presidentskroob', 'u1@example.com', 'User P1', '827ccb0eea8a706c4c34a16891f84e7b');

-- --------------------------------------------------------

--
-- Table structure for table `USERS_ROLES`
--

CREATE TABLE IF NOT EXISTS `USERS_ROLES` (
  `username` varchar(15) COLLATE latin1_general_cs NOT NULL,
  `role_name` varchar(20) COLLATE latin1_general_cs NOT NULL,
  KEY `UR_1` (`username`),
  KEY `UR_2` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table `USERS_ROLES`
--

INSERT INTO `USERS_ROLES` (`username`, `role_name`) VALUES
('admin', 'admin'),
('presidentskroob', 'president'),
('darkhelmet', 'darklord'),
('darkhelmet', 'schwartz'),
('lonestarr', 'goodguy'),
('lonestarr', 'schwartz'),
('lonestarr', 'president');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `ROLES_PERMISSIONS`
--
ALTER TABLE `ROLES_PERMISSIONS`
  ADD CONSTRAINT `RP_1` FOREIGN KEY (`role_name`) REFERENCES `ROLES` (`name`),
  ADD CONSTRAINT `RP_2` FOREIGN KEY (`permission_name`) REFERENCES `PERMISSIONS` (`name`);

--
-- Constraints for table `rs_attributes`
--
ALTER TABLE `rs_attributes`
  ADD CONSTRAINT `FK4322E153C595E1F` FOREIGN KEY (`RegisteredServiceImpl_id`) REFERENCES `RegisteredServiceImpl` (`id`);

--
-- Constraints for table `SERVICETICKET`
--
ALTER TABLE `SERVICETICKET`
  ADD CONSTRAINT `FK7645ADE132A2C0E5` FOREIGN KEY (`ticketGrantingTicket_ID`) REFERENCES `TICKETGRANTINGTICKET` (`ID`);

--
-- Constraints for table `TICKETGRANTINGTICKET`
--
ALTER TABLE `TICKETGRANTINGTICKET`
  ADD CONSTRAINT `FKB4C4CDDE32A2C0E5` FOREIGN KEY (`ticketGrantingTicket_ID`) REFERENCES `TICKETGRANTINGTICKET` (`ID`);

--
-- Constraints for table `USERS_ROLES`
--
ALTER TABLE `USERS_ROLES`
  ADD CONSTRAINT `UR_1` FOREIGN KEY (`username`) REFERENCES `USERS` (`username`),
  ADD CONSTRAINT `UR_2` FOREIGN KEY (`role_name`) REFERENCES `ROLES` (`name`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
