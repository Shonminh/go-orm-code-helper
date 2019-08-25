package org.shonminh.helper.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoTypeUtil {



    public static String Translate2GoType(String string) {

        Pattern compile = Pattern.compile("([a-zA-Z]+)(\\()?(.+)(\\))?");
        Matcher matcher = compile.matcher(string);
        String value = "";
        String type = "";
        if (matcher.matches()) {
            type = matcher.group(1);
            value = matcher.group(3);
        }
        System.out.println(value);
        System.out.println(type);
        return "";
    }

    public static void main(String[] args) {
        Translate2GoType("vachar(11)");
    }
}
