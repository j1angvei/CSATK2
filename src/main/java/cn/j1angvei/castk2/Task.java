package cn.j1angvei.castk2;


import cn.j1angvei.castk2.cmd.InstallCmd;
import cn.j1angvei.castk2.run.Analysis;
import cn.j1angvei.castk2.run.Executor;
import cn.j1angvei.castk2.type.OutType;
import cn.j1angvei.castk2.type.SubType;
import cn.j1angvei.castk2.type.SwType;
import cn.j1angvei.castk2.util.ConfUtil;
import cn.j1angvei.castk2.util.FileUtil;


/**
 * Created by Wayne on 2016/11/23.
 */
public class Task {
    public static final String PIPELINE = "-p";
    public static final String INSTALL = "-i";
    public static final String RESET = "-r";
    public static final String BACKUP = "-b";
    public static final String FUNCTION = "-f";
    private static ConfUtil CONF = ConfUtil.getInstance();

    public static void pipeline() {
        for (Function function : Function.values()) {
            System.out.println("Start analysis, " + function.name());
            Analysis.runFunction(function);
            System.out.println("Analysis , " + function.name() + " finished!");
        }
    }

    public static void install() {
        for (SwType swType : SwType.values()) {
            String swName = swType.name();
            System.out.println("Installing " + swName + " ...");
            Executor.execute("install_" + swType.name(), InstallCmd.install(swType));
            System.out.println("Install " + swName + " finished.");
        }
    }

    public static void reset() {
        System.out.println("Reset CSATK2 to default status...");
        for (SubType sub : SubType.values()) {
            String dir = CONF.getDirectory(sub);
            FileUtil.makeDirs(dir);
        }
        for (OutType out : OutType.values()) {
            String dir = CONF.getDirectory(out);
            FileUtil.makeDirs(dir);
        }
        //read config file from resource to config dir
        FileUtil.restoreConfig();
        FileUtil.restoreInput();
        FileUtil.restoreAdapter();
        System.out.println("Reset finished!");
    }

    public static void backup() {
        String timestamp = FileUtil.getTimestamp();
        //backup files and dirs
        backupSubDir(timestamp, SubType.CONFIG);
        backupSubDir(timestamp, SubType.LOG);
        backupSubDir(timestamp, SubType.SCRIPT);
        backupSubDir(timestamp, SubType.OUTPUT);
        System.out.println("Backup at: " + CONF.getDirectory(SubType.BACKUP) + timestamp);
        //reset directories
        reset();

    }

    private static void backupSubDir(String timestamp, SubType type) {
        String destPath = CONF.getDirectory(SubType.BACKUP) + timestamp;
        FileUtil.makeDirs(destPath);
        FileUtil.move(CONF.getDirectory(type), destPath);
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
}
