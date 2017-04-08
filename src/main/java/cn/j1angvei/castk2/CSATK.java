package cn.j1angvei.castk2;

/**
 * Entry of the program
 * Created by Wayne on 2016/11/23.
 */
public class CSATK {
    public static void main(String[] args) {
        if (args.length == 0) {
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
                    System.err.println("Function keywords are not in function1,function2,function... format!");
                }
                break;
            case Task.SOLELY:
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
                        "Version: 2.0-170407 by j1angvei\n" +
                        "Project: https://github.com/j1angvei/CSATK2\n" +
                        "\n" +
                        "Tasks:\nCMD:\tjava -jar CSATK.jar <task keyword>\n" +
                        "\t%s,\tChIP-Seq analysis pipeline\n" +
                        "\t%s,\trun function(s) in order\n" +
                        "\t%s,\trun solely function with arguments\n" +
                        "\t%s,\t(re)install all software\n" +
                        "\t%s,\treset project to original state\n" +
                        "\t%s,\tbackup all file of last analysis\n" +
                        "\n" +
                        "Functions:\nCMD:\tjava -jar CSATK.jar -f <function1,function2,...>\n" +
                        "\t%s,\tgenerate genome index\n" +
                        "\t%s,\tQC of raw reads\n" +
                        "\t%s,\ttrim and filter raw reads\n" +
                        "\t%s,\tQC of clean(filtered) reads\n" +
                        "\t%s,\talignment of clean reads\n" +
                        "\t%s,\tconvert sam to bam files\n" +
                        "\t%s,\tsort bam files\n" +
                        "\t%s,\tQC of bam files using Qualimap\n" +
                        "\t%s,\tremove duplicate reads in BAM file\n" +
                        "\t%s,\tkeep unique mapped reads\n" +
                        "\t%s,\tpeak calling using MACS2\n" +
                        "\t%s,\tfind motifs using Homer\n" +
                        "\t%s,\tpeak annotation using Homer\n" +
                        "\t%s,\tget gene list  from annotation result\n" +
                        "\t%s,\tgo & pathway analysis using panther\n" +
                        "\t%s,\tgenerate analysis summary in HTML format\n" +
                        "\n" +
                        "Sole function:\nCMD:\tjava -jar CSATK.jar -s <function keyword> [arg1] [arg2] ...\n" +
                        "\tGO & Pathway analysis:\t%s [species code] [gene list] [output]\n" +
                        "\n"
                ,
                //task
                Task.PIPELINE, Task.FUNCTION, Task.SOLELY, Task.INSTALL, Task.RESET, Task.BACKUP,
                //function
                Function.GENOME_IDX, Function.QC_RAW, Function.TRIM, Function.QC_CLEAN,
                Function.ALIGNMENT, Function.CONVERT_SAM, Function.SORT_BAM, Function.QC_BAM, Function.RMDUP_BAM, Function.UNIQUE_BAM,
                Function.PEAK_CALLING, Function.MOTIF,
                Function.PEAK_ANNOTATION, Function.GENE_LIST, Function.GO_PATHWAY, Function.SUMMARY,
                //solely function
                Function.GO_PATHWAY
        );
    }
}
