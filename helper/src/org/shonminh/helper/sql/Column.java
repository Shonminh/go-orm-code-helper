package org.shonminh.helper.sql;

import org.shonminh.helper.util.GoTypeUtil;
import org.shonminh.helper.util.StringUtil;

public class Column {

    private String name;
    private String type;
    private String defaultValue;
    private boolean isUnsigned;
    private boolean isNotNull;
    private String raw;
    private String formatStr;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isUnsigned() {
        return isUnsigned;
    }

    public void setUnsigned(boolean unsigned) {
        isUnsigned = unsigned;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }

    public String getFormatStr() {
        return formatStr;
    }

    public void setFormatStr(String formatStr) {
        this.formatStr = formatStr;
    }

    public String generateColumnStruc(boolean isPrimaryKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t");
        sb.append(StringUtil.camelString(this.name));
        sb.append(this.formatStr);
        sb.append(GoTypeUtil.Translate2GoType(this.type, this.isUnsigned));
        sb.append("`gorm:\"type:").append(this.type.toUpperCase());
        if (this.isUnsigned) {
            sb.append(" UNSIGNED");
        }
        sb.append(";");
        if (this.isNotNull) {
            sb.append("NOT NULL");
        }
        sb.append("\"`");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", isUnsigned=" + isUnsigned +
                ", isNotNull=" + isNotNull +
                ", raw='" + raw + '\'' +
                ", formatStr='" + formatStr + '\'' +
                '}';
    }
}
