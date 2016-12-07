package cn.j1angvei.castk2;


import cn.j1angvei.castk2.cmd.InstallCmd;
import cn.j1angvei.castk2.run.Analysis;
import cn.j1angvei.castk2.run.Executor;
import cn.j1angvei.castk2.type.SwType;

/**
 * Created by Wayne on 2016/11/23.
 */
public class Task {
    public static final String PIPELINE = "-p";
    public static final String INSTALL = "-i";
    public static final String RESET = "-r";
    public static final String BACKUP = "-b";
    public static final String FUNCTION = "-f";

    public static void pipeline() {
        for (Function function : Function.values()) {
            System.out.println("Start analysis, " + function.name());
            Analysis.runFunction(function);
            System.out.println("Analysis , " + function.name() + "finished!");
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

    }

    public static void backup() {

    }

    public static void function(String keywords) {
        String[] functions = keywords.split(",");
        for (String keyword : functions) {
            Function function = Function.fromKeyword(keyword);
            System.out.println("Start analysis, " + function.name());
            Analysis.runFunction(function);
            System.out.println("Analysis , " + function.name() + "finished!");
        }
    }
}
