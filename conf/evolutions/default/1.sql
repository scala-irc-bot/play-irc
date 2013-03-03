# --- !Ups
CREATE TABLE `users` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE `clients` (
  `id` CHAR(36) NOT NULL,
  `hostname` VARCHAR(255) NOT NULL,
  `port` INT NOT NULL,
  `password` VARCHAR(2545),
  `encoding` VARCHAR(63) NOT NULL,
  `message_delay` INT NOT NULL,
  `timer_delay` INT NOT NULL,
  `nickname` VARCHAR(255) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `realname` VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);
INSERT INTO `clients` (`id`, `hostname`, `port`, `password`, `encoding`, `message_delay`, `timer_delay`, `nickname`, `username`, `realname`)
VALUES ('97ce3550-0501-4a89-89fc-50b8c279c02b', 'localhost', '6667', NULL, 'UTF-8', '2000', '60000', 'bot', 'bot', 'mtgto');
CREATE TABLE `channels` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(63) NOT NULL,
  PRIMARY KEY (id)
);
INSERT INTO `channels` (`id`, `name`) VALUES ('78835103-25ed-4df5-a497-ce1b8c8d707c', '#test');
INSERT INTO `channels` (`id`, `name`) VALUES ('a92fda68-07fb-4ade-b0d1-9a325c700cbb', '#mtgto');
CREATE TABLE `bots` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(1023) NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `config` TEXT,
  `enabled` TINYINT NOT NULL,
  PRIMARY KEY (id)
);
# --- !Downs
DROP TABLE `users`;
DROP TABLE `clients`;
DROP TABLE `channels`;
DROP TABLE `bots`;
