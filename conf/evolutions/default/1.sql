# --- !Ups
CREATE TABLE `users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
);
INSERT INTO `users` (`name`, `password`) VALUES ('admin', 'admin');
CREATE TABLE `clients` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `hostname` VARCHAR(255) NOT NULL,
  `port` INT NOT NULL,
  `password` VARCHAR(2545),
  `encoding` VARCHAR(63) NOT NULL,
  `delay` INT NOT NULL,
  `nickname` VARCHAR(255) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `realname` VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);
INSERT INTO `clients` (`hostname`, `port`, `password`, `encoding`, `delay`, `nickname`, `username`, `realname`)
VALUES ('localhost', '6667', NULL, 'UTF-8', '2000', 'bot', 'bot', 'mtgto');
CREATE TABLE `channels` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(63) NOT NULL,
  PRIMARY KEY (id)
);
INSERT INTO `channels` (`name`) VALUES ('#test');
INSERT INTO `channels` (`name`) VALUES ('#mtgto');
CREATE TABLE `bots` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(1023) NOT NULL,
  `config` TEXT,
  `enabled` TINYINT NOT NULL,
  PRIMARY KEY (id)
);

# --- !Downs
DROP TABLE `users`;
DROP TABLE `clients`;
DROP TABLE `channels`;
DROP TABLE `bots`;
