package org.shonminh.helper.sql;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.shonminh.helper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {


    private List<String> statements;
    private String fileName;

    private void generateStatements(String str) {
        String[] splits = str.split("[\n\r]+");
        List<String> list = new ArrayList<>();
        boolean lastHasEndWithSeparator = true;
        for (String split : splits) {
            String string = split.trim();
            // if is empty or comment, pass
            if ("".equals(string) || StringUtil.isSqlComment(string)) {
                continue;
            }
            if (lastHasEndWithSeparator) {
                list.add(string);
            } else {
                String last = list.get(list.size() - 1);
                last = last + " " + string;
                list.set(list.size() - 1, last);
            }
            lastHasEndWithSeparator = split.endsWith(";");
        }
        this.setStatements(list);
    }

    private boolean checkIfCreateStatement(String string) {
        string = string.toLowerCase();
        Pattern pattern = Pattern.compile("create table (.*)\\((.*)\\)(.*)");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }


    private String parseStatements() {

        StringBuilder resultStringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (String statement : statements) {
            // if not create statement then pass
            if (!checkIfCreateStatement(statement)) {
                continue;
            }
            Statements ccjStatements;
            try {
                ccjStatements = CCJSqlParserUtil.parseStatements(statement);

                List<Statement> statementList = ccjStatements.getStatements();
                if (statementList.size() < 1) {
                    continue;
                }

                // get first one, assume that we only have parse one statement
                Statement singleCCJStatement = statementList.get(0);
                String modelName = getModelName(singleCCJStatement);
                Model model = new Model(modelName);
                model.setPrimaryKeyName(singleCCJStatement);
                model.appendColumnsByCCJCreateTable(singleCCJStatement);
                this.setFileName(model.getModelName().replaceFirst("_tab$", "") + ".go");

                // if is first append string then add golang package name
                if (isFirst) {
                    resultStringBuilder.append("package model\n\n");
                    isFirst = false;
                }
                resultStringBuilder.append(model.generateGoStruct());
            } catch (JSQLParserException ignored) {
            }
        }
        return resultStringBuilder.toString();
    }

    private String getModelName(Statement statement) {
        CreateTable createTable = (CreateTable) statement;
        return createTable.getTable().getName();
    }

    public void setStatements(List<String> statements) {
        this.statements = statements;
    }

    public String Execute(String sql) {
        this.generateStatements(sql);
        return parseStatements();
    }


    public static void main(String[] args) {
        String sql = "CREATE DATABASE IF NOT EXISTS demo_db;\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS demo.test_tab (\n" +
                "  id bigint(21) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  user_id int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  parent_user_id varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL,\n" +
                "  no_id bigint(21) DEFAULT 0 NOT NULL,\n" +
                "  tiny_name varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL,\n" +
                "\n" +
                "  score decimal(10, 3)  DEFAULT '0.00' NOT NULL,\n" +
                "  create_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  update_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  UNIQUE KEY (user_id, pareent_user_id),\n" +
                "  PRIMARY KEY (id)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
        SqlParser sqlParser = new SqlParser();
        System.out.println(sqlParser.Execute(sql));
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

