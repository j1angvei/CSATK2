package cn.j1angvei.castk2.util;

import cn.j1angvei.castk2.CSATK;
import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.conf.Resource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
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

    public static void appendFile(String content, String fileName, boolean appendNewLine) {
        if (appendNewLine) {
            content += "\n";
        }
        File file = createFileIfNotExist(fileName);
        try {
            FileUtils.write(file, content, Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readLineIntoList(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String eachLine;
            while ((eachLine = reader.readLine()) != null) {
                //if line start with "#", and set to skip comment, skip this line
                if (eachLine.startsWith("#")) continue;
                lines.add(eachLine);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static boolean makeDirs(String filePath) {
        boolean success = false;
        File file = new File(filePath);
        if (!file.exists()) {
            success = file.mkdirs();
        }
        return success;
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
                    throw new IllegalArgumentException("Can not move  " + srcPath + " to File " + destPath);
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
                    throw new IllegalArgumentException("Can not copy  " + srcPath + " to File " + destPath);
                } else {
                    //copy file to file
                    FileUtils.copyFile(src, dest);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static URL readResource(String resName) {
        return CSATK.class.getClassLoader().getResource(resName);
    }

    public static String readResourceAsString(String resName) {
        try {
            return IOUtils.toString(readResource(resName), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(resName + " resource not found");
            return null;
        }
    }

    public static String readFromConfigFolder(Resource type) {
        return readFile(WORK_DIR + Directory.Sub.CONFIG.getDirName() + File.separator + type.getFileName());
    }

    public static String readFromResourceFolder(Resource type) {
        String content = "";
        try {
            content = IOUtils.toString(CSATK.class.getClassLoader().getResourceAsStream(type.getFileName()), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void restoreConfig(Resource type) {
        String content = readFromResourceFolder(type);
        overwriteFile(content, WORK_DIR + "config" + File.separator + type.getFileName());
    }

    public static byte[] readToByteArray(String filePath) {
        try {
            return FileUtils.readFileToByteArray(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static String encodeToBase64(String filePath) {
        return Base64.encodeBase64String(readToByteArray(filePath));
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

    public static long countLines(String filePath) {
        List<String> lines = readLines(filePath);
        return lines.size();

    }

    public static List<String> readLines(String filePath) {
        try {
            return FileUtils.readLines(new File(filePath), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
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
        return new SimpleDateFormat("yyyyMMdd-HH:mm:ss").format(new Date());
    }

    public enum Unit {
        BYTES, KB, MB, GB
    }
}
