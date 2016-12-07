package cn.j1angvei.castk2.util;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    public static void overwriteFile(String content, String fileName) {
        File file = createFileIfNotExist(fileName);
        try {
            FileUtils.writeStringToFile(file, content, Charset.defaultCharset(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendFile(String content, String fileName) {
        File file = createFileIfNotExist(fileName);
        try {
            FileUtils.write(file, content, Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createFileIfNotExist(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String readConfig() {
        return readFile(WORK_DIR + "config" + File.separator + "config.json");
    }

    public static String readInput() {
        return readFile(WORK_DIR + "config" + File.separator + "input.json");
    }


    public static long countFileSize(String fileName) {
        long count = 0;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                count += line.length();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("count genome size " + fileName + " " + count);
        return count;
    }

    public static String[] listToArray(List<String> input) {
        if (input != null) {
            return input.toArray(new String[input.size()]);
        }
        throw new NullPointerException("String array is null");
    }

    public static String[] wrapString(String raw) {
        return new String[]{raw};
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
    }
}
