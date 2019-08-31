package org.shonminh.helper.util;

import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static void WriteFile(String filePath, String in) throws IOException {

        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(in);
        fileWriter.flush();
        fileWriter.close();

    }
}
