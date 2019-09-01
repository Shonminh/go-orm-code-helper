package org.shonminh.helper.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoTypeUtil {


    public static String Translate2GoType(String string, boolean isUnsigned) {

        Pattern compile = Pattern.compile("([a-zA-Z]+)(\\((.+)\\)|(.*))");
        Matcher matcher = compile.matcher(string);
        String type = null;
        if (matcher.matches()) {
            type = matcher.group(1);
        }

        if (type == null) {
            return null;
        }

        switch (type) {
            case "varchar":
            case "text":
            case "mediumtext":
            case "longtext":
            case "tinytext":
            case "char":
                return "string";
            case "tinyint":
                return isUnsigned ? "uint8" : "int8";
            case "smallint":
                return isUnsigned ? "uint16" : "int16";
            case "int":
                return isUnsigned ? "uint32" : "int32";
            case "bigint":
                return isUnsigned ? "uint64" : "int64";
            case "float":
            case "double":
            case "decimal":
                return "float64";
            case "datetime":
            case "timestamp":
                return "time.Time";
        }
        return null;
    }
}
