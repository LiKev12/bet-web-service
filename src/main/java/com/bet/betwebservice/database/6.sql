CREATE DATABASE IF NOT EXISTS `betappproductiondb` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `betappproductiondb`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `pod` (
  `id` BINARY(16),
  `timestamp_unix` INT NOT NULL,
  `id__user_create` BINARY(16),
  `name` varchar(50),
  `description` varchar(1000),
  `is_public` BOOLEAN DEFAULT FALSE,
  `id__image_key` BINARY(16),
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `stamp` (
  `id` BINARY(16),
  `timestamp_unix` INT NOT NULL,
  `id__user_create` BINARY(16),
  `name` varchar(50),
  `description` varchar(1000),
  `id__image_key` BINARY(16),
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `task` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__user_create` BINARY(16) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(1000),
  `id__image_key` BINARY(16),
  `number_of_points` INT NOT NULL,
  `timestamp_update` INT,
  `datetime_target` varchar(1000),
  `id__pod` BINARY(16),
  `is_archived` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `user` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `name` varchar(50) NOT NULL,
  `username` varchar(30) NOT NULL,
  `password` varchar(1000) NOT NULL,
  `email` varchar(255) NOT NULL,
  `bio` varchar(1000),
  `id__image_key` BINARY(16),
  `time_zone` varchar(1000),
  `is_public` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `task_user_task_complete` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__task` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT FK_task_user_task_complete__task_id FOREIGN KEY (id__task) REFERENCES task(id),
  CONSTRAINT FK_task_user_task_complete__user_id FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `task_user_task_star` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__task` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT FK_task_user_task_star__task_id FOREIGN KEY (id__task) REFERENCES task(id),
  CONSTRAINT FK_task_user_task_star__user_id FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `task_user_task_pin` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__task` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT FK_task_user_task_pin__task_id FOREIGN KEY (id__task) REFERENCES task(id),
  CONSTRAINT FK_task_user_task_pin__user_id FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `task_user_task_note` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__task` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  `note_text` varchar(1000),
  `timestamp_note_text` INT,
  `id__note_image_key` BINARY(16),
  `timestamp_note_image` INT,
  PRIMARY KEY (`id`),
  CONSTRAINT FK_task_user_task_note__task_id FOREIGN KEY (id__task) REFERENCES task(id),
  CONSTRAINT FK_task_user_task_note__user_id FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `pod_user_pod_has_user` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__pod` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  `is_member` BOOLEAN DEFAULT FALSE,
  `timestamp_become_member` INT,
  `is_moderator` BOOLEAN DEFAULT FALSE,
  `timestamp_become_moderator` INT,
  `is_join_pod_invite_sent` BOOLEAN DEFAULT FALSE,
  `is_join_pod_invite_accepted` BOOLEAN DEFAULT FALSE,
  `id__user_join_pod_invite_sender` BINARY(16),
  `timestamp_join_pod_invite_sent` INT,
  `is_become_pod_moderator_request_sent` BOOLEAN DEFAULT FALSE,
  `is_become_pod_moderator_request_approved` BOOLEAN DEFAULT FALSE,
  `id__user_become_pod_moderator_request_approver` BINARY(16),
  `timestamp_become_pod_moderator_request_sent` INT,
  PRIMARY KEY (`id`),
  CONSTRAINT FK_pod_user_pod_has_user__pod_id FOREIGN KEY (id__pod) REFERENCES pod(id),
  CONSTRAINT FK_pod_user_pod_has_user__user_id FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `stamp_task_stamp_has_task` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__stamp` BINARY(16) NOT NULL,
  `id__task` BINARY(16) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT FK__stamp_task_stamp_has_task__id__stamp FOREIGN KEY (id__stamp) REFERENCES stamp(id),
  CONSTRAINT FK__stamp_task_stamp_has_task__id__task FOREIGN KEY (id__task) REFERENCES task(id)
);

CREATE TABLE IF NOT EXISTS `stamp_user_user_collect_stamp` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__stamp` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT FK__stamp_user_user_collect_stamp__id__stamp FOREIGN KEY (id__stamp) REFERENCES stamp(id),
  CONSTRAINT FK__stamp_user_user_collect_stamp__id__task FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `user_user_user_1_follow_user_2` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__user_1` BINARY(16) NOT NULL,
  `id__user_2` BINARY(16) NOT NULL,
  `is_following` BOOLEAN DEFAULT FALSE,
  `timestamp_of_following` INT,
  `is_request_sent` BOOLEAN DEFAULT FALSE,
  `timestamp_request_sent` INT,
  `is_request_accepted` BOOLEAN DEFAULT FALSE,
  `timestamp_request_accepted` INT,
  PRIMARY KEY (`id`),
  CONSTRAINT FK__user_user_user_1_follow_user_2__id__user_1 FOREIGN KEY (id__user_1) REFERENCES user(id),
  CONSTRAINT FK__user_user_user_1_follow_user_2__id__user_2 FOREIGN KEY (id__user_2) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `task_user_task_comment` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__task` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  `is_text` BOOLEAN DEFAULT FALSE,
  `comment_text` varchar(1000),
  `is_image` BOOLEAN DEFAULT FALSE,
  `id__comment_image_key` BINARY(16),
  PRIMARY KEY (`id`),
  CONSTRAINT FK__task_user_task_comment__id__task FOREIGN KEY (id__task) REFERENCES task(id),
  CONSTRAINT FK__task_user_task_comment__id__user FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `task_user_task_comment_reply` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__task_comment` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  `comment_reply_text` varchar(1000),
  `is_text` BOOLEAN DEFAULT FALSE,
  `id__comment_reply_image_key` BINARY(16),
  `is_image` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`id`),
  CONSTRAINT FK__task_user_task_comment_reply__id__task_comment FOREIGN KEY (id__task_comment) REFERENCES task_user_task_comment(id),
  CONSTRAINT FK__task_user_task_comment_reply__id__user FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `task_user_task_reaction` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__task` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  `reaction_type` varchar(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT FK__task_user_task_reaction__id__task FOREIGN KEY (id__task) REFERENCES task(id),
  CONSTRAINT FK__task_user_task_reaction__id__user FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `task_user_task_comment_reaction` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__task_comment` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  `reaction_type` varchar(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT FK__task_user_task_comment_reaction__id__task_comment FOREIGN KEY (id__task_comment) REFERENCES task_user_task_comment(id),
  CONSTRAINT FK__task_user_task_comment_reaction__id__user FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `task_user_task_comment_reply_reaction` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__task_comment_reply` BINARY(16) NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  `reaction_type` varchar(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT FK__task_comment_reply_reaction__id__task_comment_reply FOREIGN KEY (id__task_comment_reply) REFERENCES task_user_task_comment_reply(id),
  CONSTRAINT FK__task_comment_reply_reaction__id__user FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `notification` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  `notification_type` varchar(1000),
  `notification_message` varchar(1000),
  `link_page_type` varchar(1000),
  `id__link_page` BINARY(16),
  `is_seen` BOOLEAN DEFAULT FALSE,
  `is_dismissed` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`id`),
  CONSTRAINT FK__notifications__id__user FOREIGN KEY (id__user) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS `forgot_password_code` (
  `id` BINARY(16) NOT NULL,
  `timestamp_unix` INT NOT NULL,
  `id__user` BINARY(16) NOT NULL,
  `secret_code` varchar(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT FK__forgot_password_code__id__user FOREIGN KEY (id__user) REFERENCES user(id)
);
/*!40101 SET character_set_client = @saved_cs_client */;

UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

/* https://www.javatpoint.com/mysql-uuid */
