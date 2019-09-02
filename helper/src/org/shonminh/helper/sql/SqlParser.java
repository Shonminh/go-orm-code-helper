package org.shonminh.helper.sql;


import org.shonminh.helper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {


    private List<String> statements;

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
            statement = statement.toLowerCase().replaceAll("[ ]+", " ");
            int left = statement.indexOf("(");
            int right = statement.lastIndexOf(")");
            String[] s = statement.substring(0, left).split(" ");
            String modelName = "";
            if (s.length > 1) {
                modelName = s[s.length - 1].trim();
                if (modelName.contains(".")) {
                    modelName = modelName.split("\\.")[1];
                }
            }
            if ("".equals(modelName)) {
                continue;
            }
            Model model = new Model(modelName);
            String rowColumns = statement.substring(left + 1, right).trim();
            String[] split = rowColumns.split(",");
            for (String str : split) {
                str = str.trim();

                // if is key then pass
                if (str.startsWith("key") || str.startsWith("unique")) {
                    continue;
                }
                String[] s1 = str.split(" ");
                if (s1.length <= 1) {
                    continue;
                }
                if (isPrimaryKey(s1)) { // set primary key
                    String primaryKey = s1[s1.length - 1].replace("(", "").replace(")", "");
                    model.setPrimaryKey(primaryKey);
                    continue;
                }
                Column column = new Column();
                column.setRaw(str);
                column.setName(s1[0]);
                column.setType(s1[1]);
                int indexOfDefaultValue = findIndexOfDefaultValue(s1);
                if (indexOfDefaultValue != -1) {
                    column.setDefaultValue(s1[indexOfDefaultValue]);
                }
                if (isNotNull(s1)) {
                    column.setNotNull(true);
                } else {
                    column.setNotNull(false);
                }
                if (isUnsigned(s1)) {
                    column.setUnsigned(true);
                } else {
                    column.setUnsigned(false);
                }
                if (isAutoIncrement(s1)) {
                    column.setAutoIncrement(true);
                } else {
                    column.setAutoIncrement(false);
                }

                model.appendColumn(column);
            }

            // if is first append string then add golang package name
            if (isFirst) {
                resultStringBuilder.append("package model\n\n");
                isFirst = false;
            }
            resultStringBuilder.append(model.generateGoStruct());
        }
        return resultStringBuilder.toString();
    }


    private boolean isPrimaryKey(String[] split) {
        return split[0].equals("primary") && split[1].equals("key");
    }

    private int findIndexOfDefaultValue(String[] split) {
        int index = 0;
        for (String s : split) {
            if ("default".equals(s)) {
                if (index + 1 <= split.length - 1) {
                    return index + 1;
                }
                return -1;
            }
            index++;
        }
        return -1;
    }

    private boolean isNotNull(String[] split) {
        int index = 0;
        for (String s : split) {
            if ("not".equals(s) && index + 1 <= split.length - 1 && "null".equals(split[index + 1])) {
                return true;
            }
            index++;
        }
        return false;
    }

    private boolean isUnsigned(String[] split) {
        for (String s : split) {
            if ("unsigned".equals(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAutoIncrement(String[] split) {
        for (String s : split) {
            if ("auto_increment".equals(s)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getStatements() {
        return statements;
    }

    public void setStatements(List<String> statements) {
        this.statements = statements;
    }

    public String Execute(String sql) {
        this.generateStatements(sql);
        return parseStatements();
    }

}

