package org.shonminh.helper.sql;

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
        StringBuilder sb = new StringBuilder();
        sb.append("type ");
        sb.append(StringUtil.camelString(this.modelName, true));
        sb.append(" struct{\n");
        for (int i = 0; i < this.columns.size(); i++) {
            Column column = this.columns.get(i);

        }



        return sb.toString();
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
