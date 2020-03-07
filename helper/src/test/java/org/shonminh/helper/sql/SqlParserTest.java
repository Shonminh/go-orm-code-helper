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
                "  `score` decimal(10, 3)  DEFAULT '0.00' NOT NULL,\n" +
                "  create_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  update_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  UNIQUE KEY (user_id, pareent_user_id),\n" +
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
                "\tScore        float64 `gorm:\"type:DECIMAL(10,3);NOT NULL\"`\n" +
                "\tCreateTime   uint32  `gorm:\"type:INT(11) UNSIGNED;NOT NULL\"`\n" +
                "\tUpdateTime   uint32  `gorm:\"type:INT(11) UNSIGNED;NOT NULL\"`\n" +
                "}\n";
        assertEquals(expect, sqlParser.Execute(sql));
    }
}