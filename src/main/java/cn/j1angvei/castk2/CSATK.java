package cn.j1angvei.castk2;

import cn.j1angvei.castk2.util.ConfUtil;

/**
 * Entry of the program
 * Created by Wayne on 2016/11/23.
 */
public class CSATK {
    public static void main(String[] args) {
        test();
        if (args.length == 0 || args.length > 2) {
            usage();
            return;
        }
        String task = args[0];
        switch (task) {
            case Task.PIPELINE:
                Task.pipeline();
                break;
            case Task.INSTALL:
                Task.install();
                break;
            case Task.RESET:
                Task.reset();
                break;
            case Task.BACKUP:
                Task.backup();
                break;
            case Task.FUNCTION:
                if (args.length == 2) {
                    Task.function(args[1]);
                } else {
                    functionArgError();
                }
                break;
            default:
                taskArgError();
        }


    }

    private static void usage() {
        System.out.printf("Program: CSATK(ChIP-Seq Analysis Toolkit)\n" +
                        "Version: 2.0\n" +
                        "\n" +
                        "Usage:\tjava -jar CSATK.jar <task> [function1,function2,...]\n " +
                        "\n" +
                        "Tasks:\n" +
                        "\t%s,\tChIP-Seq analysis pipeline\n" +
                        "\t%s,\trun function(s) in order\n" +
                        "\t%s,\t(re)install all software\n" +
                        "\t%s,\treset cn.j1angvei.castk2.CSATK to original state\n" +
                        "\t%s,\tbackup all file of last analysis, ready for next analysis\n" +
                        "\n" +
                        "Functions:\n" +
                        "\t%s,\tgenerate genome index\n" +
                        "\t%s,\tquality control of raw reads\n" +
                        "\t%s,\ttrim and filter raw reads\n" +
                        "\t%s,\tquality control of clean(filtered) reads\n" +
                        "\t%s,\talignment of clean reads\n" +
                        "\t%s,\tconvert sam to bam files\n" +
                        "\t%s,\tsort bam files\n" +
                        "\t%s,\tquality of bam files\n" +
                        "\t%s,\tpeak calling\n" +
                        "\t%s,\tpeak calling\n" +
                        "\n"
                ,
                Task.PIPELINE, Task.FUNCTION, Task.INSTALL, Task.RESET, Task.BACKUP,
                Function.GENOME_IDX, Function.QC_RAW, Function.TRIM, Function.QC_CLEAN,
                Function.ALIGNMENT, Function.CONVERT_SAM, Function.SORT_BAM, Function.QC_BAM,
                Function.PEAK_CALLING, Function.PEAK_ANNOTATION
        );
    }

    private static void taskArgError() {
        System.out.println("Illegal task arguments!");
    }

    private static void functionArgError() {
        System.out.println("Illegal function arguments");
    }

    private static void analysis(String arg) {
        switch (arg) {
            case Task.BACKUP:

        }
    }

    private static void test() {
        ConfUtil confUtil = ConfUtil.getInstance();
        System.out.println(confUtil.getConfig());
        System.out.println(confUtil.getInput());
    }

}
