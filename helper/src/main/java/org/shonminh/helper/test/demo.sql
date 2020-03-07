CREATE DATABASE IF NOT EXISTS demo_db;

CREATE TABLE IF NOT EXISTS demo.test_demo_tab (
  id bigint(21) unsigned NOT NULL AUTO_INCREMENT,
  user_id int(11) unsigned DEFAULT 0 NOT NULL,
  parent_user_id varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL,
  no_id bigint(21) DEFAULT 0 NOT NULL,
  tinyname varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL,

  score decimal(10, 3)  DEFAULT '0.00' NOT NULL,
  create_time int(11) unsigned DEFAULT 0 NOT NULL,
  update_time int(11) unsigned DEFAULT 0 NOT NULL,
  UNIQUE KEY (user_id, pareent_user_id),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;