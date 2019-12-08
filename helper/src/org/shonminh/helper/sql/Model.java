package org.shonminh.helper.sql;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import org.shonminh.helper.util.GoTypeUtil;
import org.shonminh.helper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private String modelName;
    private String primaryKey;
    private List<Column> columns;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Model(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void appendColumn(Column column) {
        if (null == this.columns) {
            this.columns = new ArrayList<>();
        }
        this.columns.add(column);
    }

    public String generateGoStruct() {
        if (this.columns == null || this.columns.size() == 0) {
            return "";
        }
        // calculate formalize blank string
        calculateFormalizeString();

        StringBuilder sb = new StringBuilder();
        sb.append("type ");
        sb.append(StringUtil.camelString(this.modelName));
        sb.append(" struct{\n");

        for (Column column : this.columns) {
            sb.append(column.generateColumnStruc(this.primaryKey.equals(column.getName())));
            sb.append("\n");
        }
        sb.append("}\n\n");
        return sb.toString();
    }

    public void calculateFormalizeString() {
        final int[] maxSize = {0};
        final int[] maxTypeSize = {0};
        this.columns.forEach(column -> {
            String camelStr = StringUtil.camelString(column.getName());
            String goType = GoTypeUtil.Translate2GoType(column.getType(), column.isUnsigned());
            int typeSize = 1;
            if (null != goType) {
                typeSize += goType.length();
            }
            int size = camelStr.length() + 1;
            if (size > maxSize[0]) {
                maxSize[0] = size;
            }
            if (typeSize > maxTypeSize[0]) {
                maxTypeSize[0] = typeSize;
            }
        });
        this.columns.forEach(column -> {
            String camelStr = StringUtil.camelString(column.getName());
            int size = maxSize[0] - camelStr.length();
            String goType = GoTypeUtil.Translate2GoType(column.getType(), column.isUnsigned());
            int typeSize = maxTypeSize[0];
            if (null != goType) {
                typeSize -= goType.length();
            }
            for (int i = 0; i < size; i++) {
                if (i == 0) {
                    column.setFormatStr(" ");
                } else {
                    column.setFormatStr(column.getFormatStr() + " ");
                }
            }
            for (int i = 0; i < typeSize; i++) {
                if (i == 0) {
                    column.setTypeFormatStr(" ");
                } else {
                    column.setTypeFormatStr(column.getTypeFormatStr() + " ");
                }
            }
        });
    }

    public void appendColumnsByCCJCreateTable(Statement statement) {
        CreateTable createTable = (CreateTable) statement;
        List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
        for (ColumnDefinition co :
                columnDefinitions) {
            Column column = new Column();
            column.setName(co.getColumnName());
            column.setType(co.getColDataType().toString());
            List<String> columnSpecStrings = co.getColumnSpecStrings();
            if (columnSpecStrings.size() > 0) {
                for (int i = 0; i < columnSpecStrings.size(); i++) {
                    String spec = columnSpecStrings.get(i);
                    if ("UNSIGNED".equals(spec.toUpperCase())) {
                        column.setUnsigned(true);
                    }
                    if ("DEFAULT".equals(spec.toUpperCase())) {
                        if (i + 1 < columnSpecStrings.size()) {
                            column.setDefaultValue(columnSpecStrings.get(i + 1));
                        }
                    }
                    if ("NOT".equals(spec.toUpperCase()) && (i + 1) < columnSpecStrings.size() &&
                            "NULL".equals(columnSpecStrings.get(i + 1).toUpperCase())) {
                        column.setNotNull(true);
                    }
                    if ("AUTO_INCREMENT".equals(spec.toUpperCase())) {
                        column.setAutoIncrement(true);
                    }
                }
            }
            this.appendColumn(column);
        }
    }

    public void setPrimaryKeyName(Statement statement) {
        CreateTable createTable = (CreateTable) statement;
        List<Index> indexes = createTable.getIndexes();
        for (Index index :
                indexes) {
            String type = index.getType();
            if ("PRIMARY KEY".equals(type.replaceAll("\\s+", " ").toUpperCase())) {
                List<String> columnsNames = index.getColumnsNames();
                if (columnsNames.size() == 0) {
                    return;
                }
                this.setPrimaryKey(columnsNames.get(0));
            }
        }
    }

    @Override
    public String toString() {
        return "Model{" +
                "modelName='" + modelName + '\'' +
                ", primaryKey='" + primaryKey + '\'' +
                ", columns=" + columns +
                '}';
    }
}
