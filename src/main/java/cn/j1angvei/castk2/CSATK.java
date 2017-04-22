package cn.j1angvei.castk2;


/**
 * Entry of the program
 * Created by Wayne on 2016/11/23.
 */
public class CSATK {
    public static void main(String[] args) {
//        new HtmlGenerator("19_statistic"+ File.separator).generate();
        if (args.length == 0) {
            usage();
            return;
        }
        Task task = Task.fromKeyword(args[0]);
        switch (task) {
            case PIPELINE:
                Task.pipeline();
                break;
            case INSTALL:
                Task.install();
                break;
            case RESET:
                Task.reset();
                break;
            case BACKUP:
                Task.backup();
                break;
            case FUNCTION:
                if (args.length == 2) {
                    Task.function(args[1]);
                } else {
                    System.err.println("Function keywords are not in function1,function2,function... format!");
                }
                break;
            case SOLELY:
                if (args.length > 2) {
                    String functionKeyword = args[1];
                    String[] paramArgs = new String[args.length - 2];
                    System.arraycopy(args, 2, paramArgs, 0, paramArgs.length);
                    Task.solely(functionKeyword, paramArgs);
                } else {
                    System.err.println("Lack of solely function arguments, java -jar CSATK.jar -s [function keyword] [arg0] [arg0] ...");
                }
                break;
            default:
                throw new IllegalArgumentException("\nTask " + args[0] + " not found!");
        }
    }

    private static void usage() {
        System.out.printf("Program: CSATK(ChIP-Seq Analysis Toolkit)\n" +
                        "Version: 2.0-170419 by j1angvei\n" +
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
