package org.shonminh.helper.sql;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.shonminh.helper.util.GoTypeUtil;
import org.shonminh.helper.util.HeaderUtil;
import org.shonminh.helper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private String modelName;
    private String primaryKey;
    private List<Column> columns;
    private Header header;
    private String structModelName;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Model(String modelName) {
        this.modelName = modelName;
        this.header = new Header();
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

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public void appendColumn(Column column) {
        if (null == this.columns) {
            this.columns = new ArrayList<>();
        }
        this.columns.add(column);
    }

    public String generateGoStruct() {
        if (this.getColumns() == null || this.getColumns().size() == 0) {
            return "";
        }
        this.setStructModelName(StringUtil.camelString(this.getModelName()));

        // calculate formalize blank string
        calculateFormalizeString();

        StringBuilder sb = new StringBuilder();
        sb.append("type ");
        sb.append(this.getStructModelName());
        sb.append(" struct {\n");

        for (Column column : this.getColumns()) {
            sb.append(column.generateColumnStruc(this.getPrimaryKey().equals(column.getName())));
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
            column.setColumn(columnDefinition.getColumnName());


            // if primary key is defined in the current column
            List<SQLColumnConstraint> constraints = columnDefinition.getConstraints();
            if (constraints != null && constraints.size() > 0) {
                for (SQLColumnConstraint constraint : constraints) {
                    if (constraint instanceof SQLColumnPrimaryKey && this.primaryKey == null) {
                        this.setPrimaryKey(column.getName());
                        break;
                    }
                }
            }

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

            // set model header
            String dependencyPackageName = HeaderUtil.getDependencyPackageName(column.getType());
            if (dependencyPackageName != null) {
                Header h = this.getHeader();
                h.appendDependencyPackage(dependencyPackageName);
                this.setHeader(h);
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


    protected String generateGoCreateFunction() {
        StringBuilder sb = new StringBuilder();
        String s = String.format("func Create%s(db *gorm.db, dao %s, tableName string) (err error) {\n", this.getStructModelName(), this.getStructModelName());
        sb.append(s);
        sb.append("\tif err = db.Table(tableName).Create(&dao).Error; err != nil {\n");
        sb.append("\t\treturn err\n");
        sb.append("\t}\n");
        sb.append("\treturn nil\n");

        sb.append("}\n");
        return sb.toString();
    }

    protected String generateGoUpdateFunction() {

        return String.format("func Update%s(db *gorm.DB, dao %s, tableName string, query interface{}, queryArgs []interface{}, updateArgs map[string]interface{}) (affectRows int64, err error) {\n" +
                "\td := db.Table(tableName).Where(query, queryArgs...).Updates(updateArgs)\n" +
                "\treturn d.RowsAffected, d.Error\n" +
                "}\n", this.getStructModelName(), this.getStructModelName());
    }


    public String getStructModelName() {
        return structModelName;
    }

    public void setStructModelName(String structModelName) {
        this.structModelName = structModelName;
    }

    @Override
    public String toString() {
        return "Model{" +
                "modelName='" + modelName + '\'' +
                ", primaryKey='" + primaryKey + '\'' +
                ", columns=" + columns +
                ", header=" + header +
                ", structModelName='" + structModelName + '\'' +
                '}';
    }
}
