package org.shonminh.helper.sql;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;
import org.shonminh.helper.util.HeaderUtil;
import org.shonminh.helper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SqlParser {


    private List<MySqlCreateTableStatement> statements;
    private String fileName;
    private String errMsg;

    final String beforeComment = "format.before_comment";

    private void generateStatements(String str) {

        List<SQLStatement> statements = SQLUtils.parseStatements(str, JdbcConstants.MYSQL);
        List<MySqlCreateTableStatement> createTableList = new ArrayList<>();
        for (SQLStatement statement : statements) {
            if (!(statement instanceof MySqlCreateTableStatement)) {
                continue;
            }
            MySqlCreateTableStatement createTable = (MySqlCreateTableStatement) statement;
            // if comment then ignore
            if (createTable.getAttribute(beforeComment) != null) {
                continue;
            }

            // if is `create like` statement then ignore
            if (createTable.getLike() != null) {
                continue;
            }
            createTableList.add(createTable);
        }
        this.setStatements(createTableList);
    }


    private String parseStatements() {
        StringBuilder resultStringBuilder = new StringBuilder();
        Header h = new Header();
        for (MySqlCreateTableStatement createTable : statements) {
            try {
                String modelName = getModelName(createTable);
                Model model = new Model(modelName);
                // set all column properties
                model.setAllColumnProperties(createTable);
                this.setFileName(model.getModelName().replaceFirst("_tab$", "") + ".go");
                List<String> packageList = model.getHeader().getDependencyPackageList();
                h.appendDependencyPackageList(packageList);
                resultStringBuilder.append(model.generateGoStruct());
            } catch (Exception e) {
                this.setErrMsg(e.getCause().getMessage());
            }
        }
        List<String> totalPackageList = h.getDependencyPackageList();
        String headerCodes = HeaderUtil.getHeaderCodes(totalPackageList);
        return headerCodes + resultStringBuilder.toString();
    }


    private String getModelName(MySqlCreateTableStatement createTable) {
        SQLExpr sqlExpr = createTable.getTableSource().getExpr();
        String simpleName = "";
        if (sqlExpr instanceof SQLPropertyExpr) {
            simpleName = ((SQLPropertyExpr) sqlExpr).getSimpleName();
        } else if (sqlExpr instanceof SQLIdentifierExpr) {
            simpleName = ((SQLIdentifierExpr) sqlExpr).getSimpleName();
        }
        return StringUtil.filterBackQuote(simpleName);
    }

    public void setStatements(List<MySqlCreateTableStatement> statements) {
        this.statements = statements;
    }

    public String Execute(String sql) {
        this.generateStatements(sql);
        return parseStatements();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}

