SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `OpenCapture` ;
SHOW WARNINGS;
USE `OpenCapture`;

-- -----------------------------------------------------
-- Table `OpenCapture`.`QUEUES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`QUEUES` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`QUEUES` (
  `QUEUE_ID` BIGINT NOT NULL AUTO_INCREMENT ,
  `QUEUE_NAME` VARCHAR(25) NOT NULL ,
  `DESC` VARCHAR(45) NOT NULL ,
  `PLUGIN` VARCHAR(128) NULL ,
  PRIMARY KEY (`QUEUE_ID`) )
ENGINE = InnoDB
COMMENT = 'Queues table has a list of queues a batch can process through.';

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`BATCHES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`BATCHES` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`BATCHES` (
  `BATCH_ID` BIGINT NOT NULL AUTO_INCREMENT ,
  `BATCH_NAME` VARCHAR(50) NOT NULL ,
  `BATCH_CLASS_ID` BIGINT NOT NULL ,
  `SCAN_DATETIME` DATETIME NOT NULL ,
  `SITE_ID` INT NOT NULL ,
  `BATCH_STATE` INT NOT NULL ,
  `ERROR_NO` INT NULL DEFAULT 0 ,
  `ERROR_MSG` TEXT NULL ,
  `BATCH_DESC` VARCHAR(45) NULL ,
  `PRIORITY` TINYINT NULL DEFAULT 5 ,
  `QUEUE_ID` BIGINT NOT NULL ,
  PRIMARY KEY (`BATCH_ID`) ,
  CONSTRAINT `FK_QUEUE_ID`
    FOREIGN KEY (`QUEUE_ID` )
    REFERENCES `OpenCapture`.`QUEUES` (`QUEUE_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Batch DB is used to tracked scanned/imported batches.';

SHOW WARNINGS;
CREATE INDEX SCAN_DATETIME_NDX ON `OpenCapture`.`BATCHES` (`SCAN_DATETIME` ASC) ;

SHOW WARNINGS;
CREATE INDEX BATCH_NAME_NDX ON `OpenCapture`.`BATCHES` (`BATCH_NAME` ASC) ;

SHOW WARNINGS;
CREATE INDEX FK_QUEUE_ID ON `OpenCapture`.`BATCHES` (`QUEUE_ID` ASC) ;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`LKP_DOCCLASS_INDEXDATA`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`LKP_DOCCLASS_INDEXDATA` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`LKP_DOCCLASS_INDEXDATA` (
  `DOC_CLASS_ID` BIGINT NOT NULL ,
  `INDEX_DATA_ID` BIGINT NOT NULL ,
  PRIMARY KEY (`DOC_CLASS_ID`, `INDEX_DATA_ID`) )
ENGINE = InnoDB
COMMENT = 'Lookup table for doc class and index data.';

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`DOCUMENT_CLASS`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`DOCUMENT_CLASS` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`DOCUMENT_CLASS` (
  `DOC_CLASS_ID` BIGINT NOT NULL AUTO_INCREMENT ,
  `DOC_CLASS_NAME` VARCHAR(50) NOT NULL ,
  `DESC` VARCHAR(45) NULL ,
  PRIMARY KEY (`DOC_CLASS_ID`) ,
  CONSTRAINT `FK_DOC_CLASS_ID`
    FOREIGN KEY (`DOC_CLASS_ID` )
    REFERENCES `OpenCapture`.`LKP_DOCCLASS_INDEXDATA` (`DOC_CLASS_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Document class table is used to track document classes.';

SHOW WARNINGS;
CREATE INDEX FK_DOC_CLASS_ID ON `OpenCapture`.`DOCUMENT_CLASS` (`DOC_CLASS_ID` ASC) ;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`BATCH_CLASS`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`BATCH_CLASS` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`BATCH_CLASS` (
  `BATCH_CLASS_ID` BIGINT NOT NULL AUTO_INCREMENT ,
  `BATCH_CLASS_NAME` VARCHAR(50) NOT NULL ,
  `DESC` VARCHAR(45) NULL ,
  `IMAGE_PATH` VARCHAR(512) NOT NULL ,
  PRIMARY KEY (`BATCH_CLASS_ID`) ,
  CONSTRAINT `FK_BATCH_CLASS_ID`
    FOREIGN KEY (`BATCH_CLASS_ID` )
    REFERENCES `OpenCapture`.`BATCHES` (`BATCH_CLASS_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Batch class table is used to track batch classes.';

SHOW WARNINGS;
CREATE INDEX FK_BATCH_CLASS_ID ON `OpenCapture`.`BATCH_CLASS` (`BATCH_CLASS_ID` ASC) ;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`LKP_BATCHCLASS_DOCCLASS`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`LKP_BATCHCLASS_DOCCLASS` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`LKP_BATCHCLASS_DOCCLASS` (
  `BATCH_CLASS_ID` BIGINT NOT NULL ,
  `DOC_CLASS_ID` BIGINT NOT NULL ,
  PRIMARY KEY (`BATCH_CLASS_ID`, `DOC_CLASS_ID`) ,
  CONSTRAINT `FK_BATCH_CLASS`
    FOREIGN KEY (`BATCH_CLASS_ID` )
    REFERENCES `OpenCapture`.`BATCH_CLASS` (`BATCH_CLASS_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_DOC_CLASS`
    FOREIGN KEY (`DOC_CLASS_ID` )
    REFERENCES `OpenCapture`.`DOCUMENT_CLASS` (`DOC_CLASS_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Lookup table for linking batch class to document class.';

SHOW WARNINGS;
CREATE INDEX FK_BATCH_CLASS ON `OpenCapture`.`LKP_BATCHCLASS_DOCCLASS` (`BATCH_CLASS_ID` ASC) ;

SHOW WARNINGS;
CREATE INDEX FK_DOC_CLASS ON `OpenCapture`.`LKP_BATCHCLASS_DOCCLASS` (`DOC_CLASS_ID` ASC) ;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`DOC_TYPES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`DOC_TYPES` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`DOC_TYPES` (
  `DOC_TYPE_ID` BIGINT NOT NULL AUTO_INCREMENT ,
  `DOC_TYPE_NAME` VARCHAR(25) NOT NULL ,
  `DESC` VARCHAR(50) NOT NULL ,
  `DOC_CLASS_ID` BIGINT NOT NULL ,
  PRIMARY KEY (`DOC_TYPE_ID`) )
ENGINE = InnoDB
COMMENT = 'Document type table is used to track different document or form types.';

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`BATCH_DATA`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`BATCH_DATA` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`BATCH_DATA` (
  `BATCH_DATA_ID` BIGINT NOT NULL AUTO_INCREMENT ,
  `BATCH_DATA_NAME` VARCHAR(45) NOT NULL ,
  `DISPLAY_NAME` VARCHAR(45) NOT NULL ,
  `FIELD_TYPE_ID` BIGINT NOT NULL ,
  PRIMARY KEY (`BATCH_DATA_ID`) )
ENGINE = InnoDB
COMMENT = 'Batch data table holds data that should be available for entire batch.';

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`FIELD_TYPES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`FIELD_TYPES` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`FIELD_TYPES` (
  `FIELD_TYPE_ID` BIGINT NOT NULL AUTO_INCREMENT ,
  `FIELD_TYPE_NAME` VARCHAR(45) NOT NULL ,
  `DESC` VARCHAR(45) NULL ,
  `DATA_TYPE` VARCHAR(1) NOT NULL DEFAULT S ,
  `LENGTH` INT NOT NULL DEFAULT 10 ,
  `DEC_PLACES` INT NULL DEFAULT 0 ,
  PRIMARY KEY (`FIELD_TYPE_ID`) )
ENGINE = InnoDB
COMMENT = 'Field types table holds field type definitions.';

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`INDEX_DATA`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`INDEX_DATA` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`INDEX_DATA` (
  `INDEX_DATA_ID` BIGINT NOT NULL AUTO_INCREMENT ,
  `INDEX_DATA_NAME` VARCHAR(45) NOT NULL ,
  `DISPLAY_NAME` VARCHAR(45) NOT NULL ,
  `FIELD_TYPE_ID` BIGINT NOT NULL ,
  PRIMARY KEY (`INDEX_DATA_ID`) ,
  CONSTRAINT `FK_INDEX_DATA_ID`
    FOREIGN KEY (`INDEX_DATA_ID` )
    REFERENCES `OpenCapture`.`LKP_DOCCLASS_INDEXDATA` (`INDEX_DATA_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_FIELD_TYPE_ID`
    FOREIGN KEY (`FIELD_TYPE_ID` )
    REFERENCES `OpenCapture`.`FIELD_TYPES` (`FIELD_TYPE_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Index data table holds field names for document type indexes.  The fields in this table makeup the index information to track.';

SHOW WARNINGS;
CREATE INDEX FK_INDEX_DATA_ID ON `OpenCapture`.`INDEX_DATA` (`INDEX_DATA_ID` ASC) ;

SHOW WARNINGS;
CREATE INDEX FK_FIELD_TYPE_ID ON `OpenCapture`.`INDEX_DATA` (`FIELD_TYPE_ID` ASC) ;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`SAMPLES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`SAMPLES` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`SAMPLES` (
  `SAMPLE_ID` BIGINT NOT NULL AUTO_INCREMENT ,
  `PATH` VARCHAR(512) NOT NULL ,
  `DOC_TYPE_ID` BIGINT NOT NULL ,
  PRIMARY KEY (`SAMPLE_ID`) )
ENGINE = InnoDB
COMMENT = 'Samples holds path information for sample pages.';

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `OpenCapture`.`LKP_BATCHCLASS_QUEUES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `OpenCapture`.`LKP_BATCHCLASS_QUEUES` ;

SHOW WARNINGS;
CREATE  TABLE IF NOT EXISTS `OpenCapture`.`LKP_BATCHCLASS_QUEUES` (
  `BATCH_CLASS_ID` BIGINT NOT NULL ,
  `QUEUE_ID` BIGINT NOT NULL ,
  PRIMARY KEY (`BATCH_CLASS_ID`, `QUEUE_ID`) ,
  CONSTRAINT `FK_BATCH_CLASS_ID`
    FOREIGN KEY (`BATCH_CLASS_ID` )
    REFERENCES `OpenCapture`.`BATCH_CLASS` (`BATCH_CLASS_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_QUEUE_ID`
    FOREIGN KEY (`QUEUE_ID` )
    REFERENCES `OpenCapture`.`QUEUES` (`QUEUE_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE INDEX FK_BATCH_CLASS_ID ON `OpenCapture`.`LKP_BATCHCLASS_QUEUES` (`BATCH_CLASS_ID` ASC) ;

SHOW WARNINGS;
CREATE INDEX FK_QUEUE_ID ON `OpenCapture`.`LKP_BATCHCLASS_QUEUES` (`QUEUE_ID` ASC) ;

SHOW WARNINGS;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
