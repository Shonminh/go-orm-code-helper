package org.shonminh.helper.util;

import java.util.List;

public class HeaderUtil {
    public static String getDependencyPackageName(String goType) {
        if ("timestamp".equals(goType)) {
            return "time";
        }
        return null;
    }

    public static String getHeaderCodes(List<String> dependencyPackageList) {
        StringBuilder sb = new StringBuilder();
        sb.append("package model\n\n");
        if (dependencyPackageList == null || dependencyPackageList.size() == 0) {
            return sb.toString();
        }
        sb.append("import (\n");
        for (String dependencyName :
                dependencyPackageList) {
            sb.append("\t");
            sb.append("\"");
            sb.append(dependencyName);
            sb.append("\"");
            sb.append("\n");
        }
        sb.append(")\n\n");
        return sb.toString();
    }
}



