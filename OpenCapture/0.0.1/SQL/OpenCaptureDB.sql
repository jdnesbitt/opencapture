CREATE DATABASE `opencapture` /*!40100 DEFAULT CHARACTER SET latin1 */;

DROP TABLE IF EXISTS `opencapture`.`batch_class`;
CREATE TABLE  `opencapture`.`batch_class` (
  `BATCH_CLASS_ID` bigint(20) unsigned NOT NULL auto_increment,
  `BATCH_CLASS_NAME` varchar(255) NOT NULL,
  `DESCR` varchar(255) default NULL,
  `IMAGE_PATH` varchar(255) NOT NULL,
  PRIMARY KEY  (`BATCH_CLASS_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `opencapture`.`batches`;
CREATE TABLE  `opencapture`.`batches` (
  `BATCH_ID` bigint(20) NOT NULL auto_increment,
  `BATCH_STATE` int(11) NOT NULL,
  `ERROR_NO` int(11) default NULL,
  `BATCH_CLASS_ID` bigint(20) NOT NULL,
  `ERROR_MSG` text,
  `SITE_ID` int(11) NOT NULL,
  `BATCH_DESC` varchar(255) default NULL,
  `SCAN_DATETIME` datetime NOT NULL,
  `PRIORITY` smallint(6) unsigned zerofill default '000005',
  `BATCH_NAME` varchar(255) NOT NULL,
  `QUEUE_ID` bigint(20) NOT NULL,
  PRIMARY KEY  (`BATCH_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `opencapture`.`lkp_batchclass_queues`;
CREATE TABLE  `opencapture`.`lkp_batchclass_queues` (
  `QUEUE_ID` bigint(20) NOT NULL,
  `BATCH_CLASS_ID` bigint(20) NOT NULL,
  `SEQUENCE_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`QUEUE_ID`,`BATCH_CLASS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `opencapture`.`queues`;
CREATE TABLE  `opencapture`.`queues` (
  `QUEUE_ID` bigint(20) unsigned NOT NULL auto_increment,
  `QUEUE_NAME` varchar(45) NOT NULL,
  `QUEUE_DESC` varchar(100) default NULL,
  `PLUGIN` varchar(128) default NULL,
  PRIMARY KEY  (`QUEUE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

