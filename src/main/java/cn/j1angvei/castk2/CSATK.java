package cn.j1angvei.castk2;


import cn.j1angvei.castk2.cmd.Analysis;
import cn.j1angvei.castk2.cmd.InstallCmd;
import cn.j1angvei.castk2.cmd.ShellExecutor;
import cn.j1angvei.castk2.conf.BroadPeak;
import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.conf.Resource;
import cn.j1angvei.castk2.conf.Software;
import cn.j1angvei.castk2.gui.MainApp;
import cn.j1angvei.castk2.panther.PantherAnalysis;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

import java.awt.*;
import java.util.*;

/**
 * Entry of the program
 * Created by Wayne on 2016/11/23.
 */
public class CSATK {
    //CSATK GUI using JavaFX, which required jdk 1.8 and upper.
    static {
        if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
            //if jdk is under 1.8, set headless mode to be true, hence GUI will be disabled
            System.setProperty("java.awt.headless", "true");
        }
    }

    public static void main(String[] args) {
        //open GUI or usage
        if (args.length == 0) {
            if (GraphicsEnvironment.isHeadless()) {
                usage();
            } else {
                MainApp.main(args);
            }
            return;
        }
        Task task = Task.fromKeyword(args[0]);
        String[] param = new String[args.length - 1];
        System.arraycopy(args, 1, param, 0, param.length);
        start(task, param);
    }

    public static void start(Task task, String[] param) {
        switch (task) {
            case PIPELINE:
                pipeline();
                break;
            case INSTALL:
                install(param);
                break;
            case RESET:
                reset();
                break;
            case BACKUP:
                backup();
                break;
            case HELP:
                CSATK.usage();
                break;
            case SPECIES:
                System.out.println(getSpeciesInfo());
                break;
            case PEAK_TYPE:
                System.out.println(getPeakTypeInfo());
                break;
            case FUNCTION:
                if (param.length == 1) {
                    function(param[0]);
                } else {
                    System.err.println("Function keywords are not in <function1,function2,function...> format!");
                }
                break;
            case STANDALONE:
                if (param.length > 1) {
                    String functionKeyword = param[1];
                    String[] paramArgs = new String[param.length - 1];
                    System.arraycopy(param, 1, paramArgs, 0, paramArgs.length);
                    standalone(functionKeyword, paramArgs);
                } else {
                    System.err.println("Lack of solely function arguments, -s <function keyword> [arg0] [arg0] ...");
                }
                break;
            default:
                throw new IllegalArgumentException("\nTask " + param[0] + " not found!");
        }
    }

    private static void usage() {
        System.out.printf("Program: CSATK(ChIP-Seq Analysis Toolkit)\n" +
                        "Version: 2.0-170504 by j1angvei\n" +
                        "Project: https://github.com/j1angvei/CSATK2\n" +
                        "\n" +
                        "Tasks:\nCMD:\tjava -jar CSATK.jar <task>\n" +
                        Task.getTaskUsage() +
                        "\n" +
                        "Functions:\nCMD:\tjava -jar CSATK.jar -f <function1,function2,...>\n" +
                        Function.getFunctionUsage() +
                        "\n" +
                        "Standalone function:\nCMD:\tjava -jar CSATK.jar -s <function keyword> [arg1] [arg2] ...\n" +
                        "\t1) GO & Pathway analysis:\n\t%s, [species code] [gene list] [output]\n"
                ,//solely function
                Function.GO_PATHWAY.getKeyword()
        );
    }

    private static void pipeline() {
        function(Function.assemblePipelineKeywords());
    }

    private static void install(String[] param) {
        if (param.length == 0) {
            System.out.println("No software specified, install them all");
            for (Software software : Software.values()) {
                install(software);
            }
        } else {
            System.out.println("Install software as following :" + Arrays.toString(param));
            for (String sw : param) {
                Software software = Software.valueOf(sw.toUpperCase());
                install(software);
            }
        }
    }

    private static void install(Software software) {
        String swName = software.getSwName();
        System.out.println("Installing " + swName + " ...");
        String jobTile = "install_" + software.name().toUpperCase();
        ShellExecutor.execute(jobTile, InstallCmd.install(software));
        System.out.println("Install " + swName + " finished!");
    }

    private static void reset() {
        System.out.println("Reset CSATK2 to default status...");
        //reset sub directories first
        for (Directory.Sub sub : Directory.Sub.values()) {
            String dir = ConfigInitializer.getPath(sub);
            boolean success = FileUtil.makeDirs(dir);
            System.out.println(success ? "Create directory " + dir + " success!" : "Skip creating " + dir + ", already exists!");
        }
        //restore configuration file from JAR resources to config sub directory
        for (Resource type : new Resource[]{Resource.CONFIG, Resource.INPUT, Resource.ADAPTER}) {
            FileUtil.restoreConfig(type);
            System.out.println("File " + type.getFileName() + " has been reset!");
        }
        //restore all directories under output
        for (Directory.Out out : Directory.Out.values()) {
            String dir = ConfigInitializer.getPath(out);
            boolean success = FileUtil.makeDirs(dir);
            System.out.println(success ? "Create directory " + dir + " success!" : "Skip creating " + dir + ", already exists!");
        }
        System.out.println("Reset finished!");
    }

    private static void backup() {
        String timestamp = FileUtil.getTimestamp();
        //backup files and dirs
        backupSubDir(timestamp, Directory.Sub.CONFIG);
        backupSubDir(timestamp, Directory.Sub.LOG);
        backupSubDir(timestamp, Directory.Sub.SCRIPT);
        backupSubDir(timestamp, Directory.Sub.OUTPUT);
        System.out.println("Backup at: " + ConfigInitializer.getPath(Directory.Sub.BACKUP) + timestamp);
        System.out.println("Reset start after backup succeed!");
        //after backup complete, do reset
        reset();
    }

    private static void backupSubDir(String timestamp, Directory.Sub sub) {
        String destPath = ConfigInitializer.getPath(Directory.Sub.BACKUP) + timestamp;
        FileUtil.makeDirs(destPath);
        FileUtil.move(ConfigInitializer.getPath(sub), destPath);
    }

    private static void function(String keywords) {
        String[] functions = keywords.split(",");
        for (String keyword : functions) {
            Function function = Function.fromKeyword(keyword);
            System.out.println("Start analysis, " + function.name() + " ...");
            Analysis.getInstance().runFunction(function);
            System.out.println("Analysis " + function.name() + " finished!");
        }
    }

    private static void standalone(String functionKeyword, String[] paramArgs) {
        Function function = Function.fromKeyword(functionKeyword);
        switch (function) {
            case GO_PATHWAY:
                int genomeCode = Integer.parseInt(paramArgs[0]);
                String geneList = paramArgs[1];
                String outFile = paramArgs[2];
                PantherAnalysis.newInstance(null, genomeCode, geneList, outFile).analysis();
                break;
            default:
                System.err.println("Function under construction...");
                break;
        }
    }

    private static String getSpeciesInfo() {
        Properties properties = FileUtil.readProperties(Resource.SPECIES);
        Enumeration enuKeys = properties.keys();
        TreeMap<String, String> speciesMap = new TreeMap<>();
        while (enuKeys.hasMoreElements()) {
            //in the properties, key is species code, value is species name
            String key = (String) enuKeys.nextElement();
            String value = properties.getProperty(key);
            //we reverse key and value in the treeMap, so the output can sort by species name
            speciesMap.put(value, key);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Species code\tSpecies name\n");
        for (Map.Entry<String, String> entry : speciesMap.entrySet()) {
            builder.append(entry.getValue())
                    .append("\t")
                    .append(entry.getKey())
                    .append("\n");
        }
        builder.append("ATTENTION: if no need to do GO & Pathway Analysis, any number can be choose as a genome code,as long as it is bigger than 0.\n");
        return builder.toString();
    }

    private static String getPeakTypeInfo() {
        String peakInfo = FileUtil.readResourceAsString(Resource.BROAD_PEAKS.getFileName());
        BroadPeak broadPeak = GsonUtil.convertBroadPeak(peakInfo);
        return broadPeak.toString();
    }
}
