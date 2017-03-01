package cn.j1angvei.castk2.util;

import cn.j1angvei.castk2.CSATK;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mjian on 2016/11/29.
 */
public class FileUtil {
    private static String WORK_DIR = System.getProperty("user.dir") + File.separator;

    public enum Unit {
        BYTES, KB, MB, GB
    }

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
            assert file != null;
            FileUtils.writeStringToFile(file, content, Charset.defaultCharset(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendFile(String content, String fileName) {
        File file = createFileIfNotExist(fileName);
        try {
            assert file != null;
            FileUtils.write(file, content, Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readLineIntoList(String fileName, boolean skipComment) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String eachLine;
            while ((eachLine = reader.readLine()) != null) {
                //if line start with "#", and set to skip comment, skip this line
                if (eachLine.startsWith("#") && skipComment) continue;
                lines.add(eachLine);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("read lines total count: " + lines.size());
        return lines;
    }

    public static File makeDirs(String filePath) {
        File file = new File(filePath);
        boolean success = false;
        if (!file.exists()) {
            success = file.mkdirs();
        }
        return success ? file : null;
    }

    public static File createFileIfNotExist(String fileName) {
        File file = new File(fileName);
        boolean success = false;
        if (!file.exists()) {
            try {
                success = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success ? file : null;
    }

    public static void move(String srcPath, String destPath) {
        File src = new File(srcPath);
        File dest = new File(destPath);
        try {
            if (dest.isDirectory()) {
                if (src.isDirectory()) {
                    //move dir to dir
                    FileUtils.moveDirectoryToDirectory(src, dest, true);
                } else {
                    //move file to dir
                    FileUtils.moveFileToDirectory(src, dest, true);
                }
            } else {
                if (src.isDirectory()) {
                    //copy dir to file,WRONG!
                    throw new IllegalArgumentException("Can not move Directory " + srcPath + " to File " + destPath);
                } else {
                    //copy file to file
                    FileUtils.moveFile(src, dest);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(String srcPath, String destPath) {
        File src = new File(srcPath);
        File dest = new File(destPath);
        try {
            if (dest.isDirectory()) {
                if (src.isDirectory()) {
                    //copy dir to dir
                    FileUtils.copyDirectory(src, dest, true);
                } else {
                    //copy file to dir
                    FileUtils.copyFileToDirectory(src, dest, true);
                }
            } else {
                if (src.isDirectory()) {
                    //copy dir to file,WRONG!
                    throw new IllegalArgumentException("Can not copy Directory " + srcPath + " to File " + destPath);
                } else {
                    //copy file to file
                    FileUtils.copyFile(src, dest);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readConfig() {
        return readFile(WORK_DIR + "config" + File.separator + SwUtil.CONFIG_JSON);
    }

    public static String readInput() {
        return readFile(WORK_DIR + "config" + File.separator + SwUtil.INPUT_JSON);
    }

    public static String readAdapter() {
        return readFile(WORK_DIR + "config" + File.separator + SwUtil.ADAPTERS_TXT);
    }

    public static void restoreConfig() {
        String content = readResourceFile(SwUtil.CONFIG_JSON);
        overwriteFile(content, WORK_DIR + "config" + File.separator + SwUtil.CONFIG_JSON);
    }

    public static void restoreInput() {
        String content = readResourceFile(SwUtil.INPUT_JSON);
        overwriteFile(content, WORK_DIR + "config" + File.separator + SwUtil.INPUT_JSON);
    }

    public static void restoreAdapter() {
        String content = readResourceFile(SwUtil.ADAPTERS_TXT);
        overwriteFile(content, WORK_DIR + "config" + File.separator + SwUtil.ADAPTERS_TXT);
    }

    private static String readResourceFile(String name) {
        String content = "";
        try {
            content = IOUtils.toString(CSATK.class.getClassLoader().getResourceAsStream(name), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static long countFileContentSize(String fileName) {
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
        return count;
    }

    public static long getFileSize(String fileName, Unit unit) {
        File file = new File(fileName);
        long lenInBytes = file.length();
        switch (unit) {
            case BYTES:
                return lenInBytes;
            case KB:
                return lenInBytes / 1024;
            case MB:
                return lenInBytes / 1024 / 1024;
            case GB:
                return lenInBytes / 1024 / 1024 / 1024;
            default:
                return 0;
        }
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
