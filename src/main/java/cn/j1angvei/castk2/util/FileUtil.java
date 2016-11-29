package cn.j1angvei.castk2.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by mjian on 2016/11/29.
 */
public class FileUtil {
    private static String WORK_DIR = System.getProperty("user.dir") + File.separator;

    public static String getWorkDir() {
        return WORK_DIR;
    }

    public static String readFile(String fileName) {
        String content = null;
        try {
            content = FileUtils.readFileToString(new File(fileName), Charset.defaultCharset());
        } catch (IOException e) {
            System.err.println(fileName + " not found!");
        }
        return content;
    }

    public static String readConfig() {
        return readFile(WORK_DIR + "config" + File.separator + "config.json");
    }

    public static String readInput() {
        return readFile(WORK_DIR + "config" + File.separator + "input.json");
    }
}
