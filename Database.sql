show databases;

create database msig_test_candidate;

use msig_test_candidate;

CREATE TABLE `users`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `username`         varchar(255) DEFAULT NULL,
    `name`             varchar(100) DEFAULT NULL,
    `password`         varchar(255) DEFAULT NULL,
    `token`            varchar(255) DEFAULT NULL,
    `token_expired_at` bigint       DEFAULT NULL,
    `created_at`       datetime(6)  DEFAULT NULL,
    `updated_at`       datetime(6)  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `contacts`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `user_id`    bigint       DEFAULT NULL,
    `first_name` varchar(100) DEFAULT NULL,
    `last_name`  varchar(100) DEFAULT NULL,
    `email`      varchar(150) DEFAULT NULL,
    `phone`      varchar(12)  DEFAULT NULL,
    `created_at` datetime(6)  DEFAULT NULL,
    `updated_at` datetime(6)  DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_users_userid` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB;

CREATE TABLE `addresses`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `contact_id`  bigint       DEFAULT NULL,
    `street`      varchar(255) DEFAULT NULL,
    `city`        varchar(50)  DEFAULT NULL,
    `province`    varchar(50)  DEFAULT NULL,
    `country`     varchar(50)  DEFAULT NULL,
    `postal_code` varchar(6)   DEFAULT NULL,
    `created_at`  datetime(6)  DEFAULT NULL,
    `updated_at`  datetime(6)  DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_contacts_contactid` FOREIGN KEY (`contact_id`) REFERENCES `contacts` (`id`)
) ENGINE = InnoDB;

show tables;
show create table users;
show create table contacts;
show create table addresses;