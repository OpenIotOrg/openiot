-- phpMyAdmin SQL Dump
-- version 3.5.8.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 14, 2013 at 07:18 PM
-- Server version: 5.5.31-0ubuntu0.13.04.1
-- PHP Version: 5.4.9-4ubuntu2.1

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

--
-- Table structure for table `permission`
--

CREATE TABLE IF NOT EXISTS `permission` (
  `id` char(36) COLLATE latin1_general_cs NOT NULL,
  `clazz` varchar(500) COLLATE latin1_general_cs NOT NULL,
  `name` varchar(200) COLLATE latin1_general_cs DEFAULT NULL,
  `actions` varchar(200) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

-- --------------------------------------------------------

--
-- Table structure for table `principal`
--

CREATE TABLE IF NOT EXISTS `principal` (
  `id` char(36) COLLATE latin1_general_cs NOT NULL,
  `name` varchar(200) COLLATE latin1_general_cs NOT NULL,
  `clazz` varchar(500) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table `principal`
--

INSERT INTO `principal` (`id`, `name`, `clazz`) VALUES
('278ccfc5-d516-11e2-afc0-f0def19cca4a', 'sysadmin', 'eu.openiot.auth.SysAdminPrincipal');

-- --------------------------------------------------------

--
-- Table structure for table `principal_permission`
--

CREATE TABLE IF NOT EXISTS `principal_permission` (
  `principal_id` char(36) COLLATE latin1_general_cs NOT NULL,
  `permission_id` char(36) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`principal_id`,`permission_id`),
  KEY `permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` char(36) COLLATE latin1_general_cs NOT NULL,
  `username` varchar(50) COLLATE latin1_general_cs NOT NULL,
  `password` char(32) COLLATE latin1_general_cs NOT NULL,
  `first_name` varchar(150) COLLATE latin1_general_cs NOT NULL,
  `last_name` varchar(150) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `iid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `first_name`, `last_name`) VALUES
('cda58adc-2db6-469d-a2c8-ffbf9c9ccf67', 'riahi', 'cc03e747a6afbbcbf8be7668acfebee5', 'Mehdi', 'Riahi');

-- --------------------------------------------------------

--
-- Table structure for table `user_principal`
--

CREATE TABLE IF NOT EXISTS `user_principal` (
  `user_id` char(36) COLLATE latin1_general_cs NOT NULL,
  `principal_id` char(36) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`user_id`,`principal_id`),
  KEY `user_id` (`user_id`),
  KEY `user_id_2` (`user_id`),
  KEY `principal_id` (`principal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table `user_principal`
--

INSERT INTO `user_principal` (`user_id`, `principal_id`) VALUES
('cda58adc-2db6-469d-a2c8-ffbf9c9ccf67', '278ccfc5-d516-11e2-afc0-f0def19cca4a');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `principal_permission`
--
ALTER TABLE `principal_permission`
  ADD CONSTRAINT `principal_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `principal_permission_ibfk_1` FOREIGN KEY (`principal_id`) REFERENCES `principal` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `user_principal`
--
ALTER TABLE `user_principal`
  ADD CONSTRAINT `user_principal_ibfk_2` FOREIGN KEY (`principal_id`) REFERENCES `principal` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `user_principal_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
