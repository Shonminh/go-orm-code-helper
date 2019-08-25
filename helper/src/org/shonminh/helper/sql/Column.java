package org.shonminh.helper.sql;

import org.shonminh.helper.util.StringUtil;

public class Column {

    private String name;
    private String type;
    private String defaultValue;
    private boolean isUnsigned;
    private boolean isNotNull;
    private String raw;

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


//    type CompareStockDetailTab struct {
//	ID           uint64 `gorm:"type:BIGINT(21) UNSIGNED;PRIMARY_KEY;AUTO_INCREMENT;NOT NULL;"`
//	VersionDate  string `gorm:"type:VARCHAR(64);NOT NULL"`
//	NodeID       string `gorm:"type:VARCHAR(64);NOT NULL"`
//	Operator     string `gorm:"type:VARCHAR(64);NOT NULL"`
//	SheetType    uint16 `gorm:"type:SMALLINT(5) UNSIGNED;NOT NULL"`
//	SheetID      string `gorm:"type:VARCHAR(64);NOT NULL"`
//	IscChangeQty uint32 `gorm:"type:INT(11) UNSIGNED;NOT NULL"`
//	WmsChangeQty uint32 `gorm:"type:INT(11) UNSIGNED;NOT NULL"`
//	ChangeQty    uint32 `gorm:"type:INT(11) UNSIGNED;NOT NULL"`
//
//	// 是否有差异
//	IsDifferent int8 `gorm:"type:TINYINT(4);NOT NULL"`
//	// 状态
//	Status int8 `gorm:"type:TINYINT(4);NOT NULL"`
//	CreateTime uint32 `gorm:"type:INT(11) UNSIGNED;NOT NULL"`
//	UpdateTime uint32 `gorm:"type:INT(11) UNSIGNED;NOT NULL"`
//}

    public String generateColumnStruc(boolean isPrimaryKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t");
        sb.append(StringUtil.camelString(this.name, true));
        sb.append("\t\t");
        //TODO implement me


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
                '}';
    }
}
