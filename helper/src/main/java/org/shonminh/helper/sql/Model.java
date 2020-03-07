package org.shonminh.helper.sql;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
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
        sb.append(" struct {\n");

        for (Column column : this.columns) {
            sb.append(column.generateColumnStruc(this.primaryKey.equals(column.getName())));
            sb.append("\n");
        }
        sb.append("}\n");
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


    public void setAllColumnProperties(MySqlCreateTableStatement createTable) {
        List<SQLTableElement> tableElementList = createTable.getTableElementList();
        if (tableElementList == null || tableElementList.size() == 0) {
            return;
        }
        for (SQLTableElement element : tableElementList) {
            // set primary key
            if (element instanceof MySqlPrimaryKey) {
                String primaryKeyName = ((MySqlPrimaryKey) element).getColumns().get(0).getExpr().toString();
                this.setPrimaryKey(primaryKeyName);
                continue;
            }

            // if not a column definition
            if (!(element instanceof SQLColumnDefinition)) {
                continue;
            }
            SQLColumnDefinition columnDefinition = (SQLColumnDefinition) element;
            Column column = new Column();
            column.setName(StringUtil.filterBackQuote(columnDefinition.getName().getSimpleName()));
            column.setType(columnDefinition.getDataType().toString());

            // set unsigned
            if (((SQLDataTypeImpl) columnDefinition.getDataType()).isUnsigned()) {
                column.setUnsigned(true);
            }

            // set default value
            if (columnDefinition.getDefaultExpr() != null) {
                column.setDefaultValue(columnDefinition.getDefaultExpr().toString());
            }

            if (this.isNotNull(columnDefinition)) {
                column.setNotNull(true);
            }

            // set auto increment
            if (columnDefinition.isAutoIncrement()) {
                column.setAutoIncrement(true);
            }
            this.appendColumn(column);
        }
    }


    private boolean isNotNull(SQLColumnDefinition definition) {
        if (definition.getConstraints() != null && definition.getConstraints().size() > 0) {
            for (SQLColumnConstraint constraint :
                    definition.getConstraints()) {
                if (constraint instanceof SQLNotNullConstraint) {
                    return true;
                }
            }
        }
        return false;
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
