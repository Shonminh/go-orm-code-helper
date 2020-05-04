package org.shonminh.helper.sql;

import org.junit.Test;

import static org.junit.Assert.*;

public class SqlParserTest {

    @Test
    public void TestExecute() {
        String sql = "CREATE DATABASE IF NOT EXISTS demo_db;\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS `demo`.`test_tab` (\n" +
                "  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  user_id int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  parent_user_id varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL,\n" +
                "  no_id bigint(21) DEFAULT 0 NOT NULL,\n" +
                "  `tiny_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL,\n" +
                "\n" +
                "  `score` decimal(10,3)  DEFAULT '0.00' NOT NULL,\n" +
                "  create_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  update_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  UNIQUE (user_id, pareent_user_id),\n" +
                "  PRIMARY KEY (id)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
        SqlParser sqlParser = new SqlParser();
        String expect = "package model\n" +
                "\n" +
                "type TestTab struct {\n" +
                "\tId           uint64  `gorm:\"type:BIGINT(21) UNSIGNED;PRIMARY_KEY;AUTO_INCREMENT;NOT NULL\"`\n" +
                "\tUserId       uint32  `gorm:\"type:INT(11) UNSIGNED;NOT NULL\"`\n" +
                "\tParentUserId string  `gorm:\"type:VARCHAR(64);NOT NULL\"`\n" +
                "\tNoId         int64   `gorm:\"type:BIGINT(21);NOT NULL\"`\n" +
                "\tTinyName     string  `gorm:\"type:VARCHAR(64);NOT NULL\"`\n" +
                "\tScore        float64 `gorm:\"type:DECIMAL(10, 3);NOT NULL\"`\n" +
                "\tCreateTime   uint32  `gorm:\"type:INT(11) UNSIGNED;NOT NULL\"`\n" +
                "\tUpdateTime   uint32  `gorm:\"type:INT(11) UNSIGNED;NOT NULL\"`\n" +
                "}\n";
        String actual = sqlParser.Execute(sql);
        assertEquals(expect, actual);
    }

    @Test
    public void TestIssue16_Can_not_work_with_goland_windows_version() {
        String sql = "CREATE TABLE group_job (\n" +
                "g_id int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "g_type tinyint(4) NOT NULL DEFAULT '1' COMMENT '1:TYPEA; 2:TYPEB',\n" +
                "g_valid_start timestamp NOT NULL DEFAULT '2001-01-01 00:00:00',\n" +
                "g_message text NOT NULL COMMENT 'Template, json format',\n" +
                "g_job_status tinyint(4) NOT NULL DEFAULT '1' COMMENT '0:invalid,1:valid',\n" +
                "g_a_id varchar(20) NOT NULL DEFAULT '' COMMENT 'account_id',\n" +
                "g_operator_id int(10) unsigned NOT NULL DEFAULT '0',\n" +
                "g_version tinyint(255) unsigned NOT NULL DEFAULT '1',\n" +
                "g_created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "g_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "PRIMARY KEY (g_id) USING BTREE,\n" +
                "KEY g_type (g_type) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        SqlParser sqlParser = new SqlParser();
        String actual = sqlParser.Execute(sql);
        String expect = "package model\n" +
                "\n" +
                "type GroupJob struct {\n" +
                "\tGId         uint32    `gorm:\"type:INT(10) UNSIGNED;PRIMARY_KEY;AUTO_INCREMENT;NOT NULL\"`\n" +
                "\tGType       int8      `gorm:\"type:TINYINT(4);NOT NULL\"`\n" +
                "\tGValidStart time.Time `gorm:\"type:TIMESTAMP;NOT NULL\"`\n" +
                "\tGMessage    string    `gorm:\"type:TEXT;NOT NULL\"`\n" +
                "\tGJobStatus  int8      `gorm:\"type:TINYINT(4);NOT NULL\"`\n" +
                "\tGAId        string    `gorm:\"type:VARCHAR(20);NOT NULL\"`\n" +
                "\tGOperatorId uint32    `gorm:\"type:INT(10) UNSIGNED;NOT NULL\"`\n" +
                "\tGVersion    uint8     `gorm:\"type:TINYINT(255) UNSIGNED;NOT NULL\"`\n" +
                "\tGCreatedAt  time.Time `gorm:\"type:TIMESTAMP;NOT NULL\"`\n" +
                "\tGUpdatedAt  time.Time `gorm:\"type:TIMESTAMP;NOT NULL\"`\n" +
                "}\n";
        assertEquals(expect, actual);
    }
}