package cn.j1angvei.castk2;


import cn.j1angvei.castk2.cmd.Analysis;
import cn.j1angvei.castk2.cmd.Executor;
import cn.j1angvei.castk2.cmd.InstallCmd;
import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.conf.Resource;
import cn.j1angvei.castk2.conf.Software;
import cn.j1angvei.castk2.panther.PantherAnalysis;
import cn.j1angvei.castk2.util.FileUtil;

import static cn.j1angvei.castk2.conf.Directory.Sub;

/**
 * run task list in introduction
 * Created by Wayne on 2016/11/23.
 */
public enum Task {
    PIPELINE("-p", "ChIP-Seq analysis pipeline"),
    FUNCTION("-i", "run function(s) in order"),
    SOLELY("-r", "run solely function with arguments"),
    INSTALL("-b", "(re)install all software"),
    RESET("-f", "reset project to original state"),
    BACKUP("-s", "backup all file of last analysis");
    private String keyword;
    private String description;

    Task(String keyword, String description) {
        this.keyword = keyword;
        this.description = description;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("\t%s,\t%s", this.keyword, this.description);
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

    public static void pipeline() {
        for (Function function : Function.values()) {
            System.out.println("Start analysis, " + function.name());
            Analysis.runFunction(function);
            System.out.println("Analysis , " + function.name() + " finished!");
        }
    }

    public static void install() {
        for (Software software : Software.values()) {
            String swName = software.getSwName();
            System.out.println("Installing " + swName + " ...");
            Executor.execute("install_" + software.name(), InstallCmd.install(software));
            System.out.println("Install " + swName + " finished.");
        }
    }

    public static void reset() {
        System.out.println("Reset CSATK2 to default status...");
        //read conf file from resource to conf dir
        for (Resource type : Resource.values()) {
            FileUtil.restoreConfig(type);
            System.out.println("File " + type.getFileName() + " has been reset!");
        }
        for (Directory.Sub sub : Directory.Sub.values()) {
            String dir = ConfigInitializer.getPath(sub);
            boolean success = FileUtil.makeDirs(dir);
            System.out.println(success ? "Create directory " + dir + " success!" : "Skip creating " + dir + ", already exists!");
        }
        for (Directory.Out out : Directory.Out.values()) {
            String dir = ConfigInitializer.getPath(out);
            boolean success = FileUtil.makeDirs(dir);
            System.out.println(success ? "Create directory " + dir + " success!" : "Skip creating " + dir + ", already exists!");
        }
        System.out.println("Reset finished!");
    }

    public static void backup() {
        String timestamp = FileUtil.getTimestamp();
        //backup files and dirs
        backupSubDir(timestamp, Sub.CONFIG);
        backupSubDir(timestamp, Sub.LOG);
        backupSubDir(timestamp, Sub.SCRIPT);
        backupSubDir(timestamp, Sub.OUTPUT);
        System.out.println("Backup at: " + ConfigInitializer.getPath(Sub.BACKUP) + timestamp);
        //reset directories
        reset();

    }

    private static void backupSubDir(String timestamp, Sub sub) {
        String destPath = ConfigInitializer.getPath(Sub.BACKUP) + timestamp;
        FileUtil.makeDirs(destPath);
        FileUtil.move(ConfigInitializer.getPath(sub), destPath);
    }

    public static void function(String keywords) {
        String[] functions = keywords.split(",");
        for (String keyword : functions) {
            Function function = Function.fromKeyword(keyword);
            System.out.println("Start analysis, " + function.name());
            Analysis.runFunction(function);
            System.out.println("Analysis " + function.name() + " finished!");
        }
    }

    public static void solely(String functionKeyword, String[] paramArgs) {
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
