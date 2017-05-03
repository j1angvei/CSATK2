package cn.j1angvei.castk2;


import cn.j1angvei.castk2.gui.MainApp;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

/**
 * Entry of the program
 * Created by Wayne on 2016/11/23.
 */
public class CSATK {
    public static void main(String[] args) {
        if (args.length == 0) {
            if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8) && SystemUtils.IS_OS_WINDOWS) {
                MainApp.main(args);
            } else {
                usage();
            }
            return;
        }
        Task task = Task.fromKeyword(args[0]);
        String[] param = new String[args.length - 1];
        System.arraycopy(args, 1, param, 0, param.length);
        Task.run(task, param);

    }

    public static void usage() {
        System.out.printf("Program: CSATK(ChIP-Seq Analysis Toolkit)\n" +
                        "Version: 2.0-170503 by j1angvei\n" +
                        "Project: https://github.com/j1angvei/CSATK2\n" +
                        "\n" +
                        "Tasks:\nCMD:\tjava -jar CSATK.jar <task>\n" +
                        Task.getTaskUsage() +
                        "\n" +
                        "Functions:\nCMD:\tjava -jar CSATK.jar -f <function1,function2,...>\n" +
                        Function.getFunctionUsage() +
                        "\n" +
                        "Sole function:\nCMD:\tjava -jar CSATK.jar -s <function keyword> [arg1] [arg2] ...\n" +
                        "\t1) GO & Pathway analysis:\n\t%s [species code] [gene list] [output]\n"
                ,//solely function
                Function.GO_PATHWAY
        );
    }
}
