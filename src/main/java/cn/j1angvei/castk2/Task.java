package cn.j1angvei.castk2;


import cn.j1angvei.castk2.cmd.Analysis;
import cn.j1angvei.castk2.cmd.ShellExecutor;
import cn.j1angvei.castk2.cmd.InstallCmd;
import cn.j1angvei.castk2.conf.BroadPeak;
import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.conf.Resource;
import cn.j1angvei.castk2.conf.Software;
import cn.j1angvei.castk2.panther.PantherAnalysis;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static cn.j1angvei.castk2.conf.Directory.Sub;

/**
 * run task list in introduction
 * Created by Wayne on 2016/11/23.
 */
public enum Task {
    HELP("-h", "print help information and usage"),
    PIPELINE("-p", "ChIP-Seq analysis pipeline"),
    FUNCTION("-f", "run function(s) in order"),
    SOLELY("-s", "run solely function with arguments"),
    INSTALL("-i", "(re)install all software"),
    RESET("-r", "reset project to original state"),
    BACKUP("-b", "backup all file of last analysis"),
    PEAK_TYPE("-t", "print broad,narrow,mix peak type information"),
    SPECIES("-c", "print species code information");
    private String keyword;
    private String description;

    Task(String keyword, String description) {
        this.keyword = keyword;
        this.description = description;
    }

    public static void run(Task task, String[] args) {
        switch (task) {
            case PIPELINE:
                pipeline();
                break;
            case INSTALL:
                install();
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
                if (args.length == 1) {
                    function(args[0]);
                } else {
                    System.err.println("Function keywords are not in <function1,function2,function...> format!");
                }
                break;
            case SOLELY:
                if (args.length > 1) {
                    String functionKeyword = args[1];
                    String[] paramArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, paramArgs, 0, paramArgs.length);
                    solely(functionKeyword, paramArgs);
                } else {
                    System.err.println("Lack of solely function arguments, -s <function keyword> [arg0] [arg0] ...");
                }
                break;

            default:
                throw new IllegalArgumentException("\nTask " + args[0] + " not found!");
        }
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("\t%s,\t%s", this.getKeyword(), this.getDescription());
    }

    public static Task fromKeyword(String keyword) {
        for (Task task : Task.values()) {
            if (task.getKeyword().toLowerCase().equals(keyword)) {
                return task;
            }
        }
        throw new IllegalArgumentException("keyword " + keyword + " not found in " + Task.class);
    }

    public static String getTaskUsage() {
        StringBuilder builder = new StringBuilder();
        for (Task task : Task.values()) {
            builder.append(task.toString())
                    .append("\n");
        }
        return builder.toString();
    }

    public static String getSpeciesInfo() {
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

    public static String getPeakTypeInfo() {
        String peakInfo = FileUtil.readResourceAsString(Resource.BROAD_PEAKS.getFileName());
        BroadPeak broadPeak = GsonUtil.convertBroadPeak(peakInfo);
        return broadPeak.toString();
    }

    private static void pipeline() {
        function(Function.assemblePipelineKeywords());
//        for (Function function : Function.values()) {
//            function(function.getKeyword());
//            System.out.println("Start analysis, " + function.name() + " ...");
//            Analysis.getInstance().runFunction(function);
//            System.out.println("Analysis , " + function.name() + " finished!");
//        }
    }

    private static void install() {
        for (Software software : Software.values()) {
            String swName = software.getSwName();
            System.out.println("Installing " + swName + " ...");
            ShellExecutor.execute("install_" + software.name().toUpperCase(), InstallCmd.install(software));
            System.out.println("Install " + swName + " finished.");
        }
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
        backupSubDir(timestamp, Sub.CONFIG);
        backupSubDir(timestamp, Sub.LOG);
        backupSubDir(timestamp, Sub.SCRIPT);
        backupSubDir(timestamp, Sub.OUTPUT);
        System.out.println("Backup at: " + ConfigInitializer.getPath(Sub.BACKUP) + timestamp);
        System.out.println("Reset start after backup succeed!");
        //after backup complete, do reset
        reset();
    }

    private static void backupSubDir(String timestamp, Sub sub) {
        String destPath = ConfigInitializer.getPath(Sub.BACKUP) + timestamp;
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

    private static void solely(String functionKeyword, String[] paramArgs) {
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
}
