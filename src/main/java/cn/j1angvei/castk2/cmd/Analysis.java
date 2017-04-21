package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.Function;
import cn.j1angvei.castk2.conf.Experiment;
import cn.j1angvei.castk2.conf.Genome;
import cn.j1angvei.castk2.panther.PantherAnalysis;
import cn.j1angvei.castk2.qc.ParseZip;
import cn.j1angvei.castk2.ConfigInitializer;
import cn.j1angvei.castk2.util.SwUtil;

import java.util.ArrayList;
import java.util.List;

import static cn.j1angvei.castk2.conf.Directory.Out;

/**
 * Created by j1angvei on 2016/12/7.
 */
public class Analysis {
    private static ConfigInitializer CONF = ConfigInitializer.getInstance();

    public static void runFunction(Function function) {
        switch (function) {
            //genome analysis
            case GENOME_IDX:
                traverseGenomes(function);
                break;
            //experiment analysis
            case QC_RAW:
            case TRIM:
            case QC_CLEAN:
            case ALIGNMENT:
            case CONVERT_SAM:
            case SORT_BAM:
            case QC_BAM:
            case RMDUP_BAM:
            case UNIQUE_BAM:
            case PEAK_CALLING:
            case MOTIF:
            case PEAK_ANNOTATION:
            case GENE_LIST:
            case GO_PATHWAY:
                traverseExperiment(function);
                break;
            //illegal args
            default:
                System.err.println("Illegal argument!");
                break;
        }
    }

    private static void traverseExperiment(final Function function) {
        List<Thread> experimentThreads = new ArrayList<>();
        for (final Experiment experiment : CONF.getExperiments()) {
            final String scriptNamePrefix = "Experiment_" + experiment.getCode() + "_" + function.name();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Executor.execute(scriptNamePrefix, getCommands(experiment, function));
                }
            });
            t.setName(scriptNamePrefix);
            experimentThreads.add(t);
            t.start();
        }
        checkStatus(experimentThreads);
    }

    private static void traverseGenomes(final Function function) {
        List<Thread> genomeThreads = new ArrayList<>();
        for (final Genome genome : CONF.getGenomes()) {
            final String scriptNamePrefix = "Genome_" + genome.getCode() + "_" + function.name();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Executor.execute(scriptNamePrefix, getCommands(genome, function));
                }
            });
            t.setName(scriptNamePrefix);
            genomeThreads.add(t);
            t.start();
        }
        checkStatus(genomeThreads);
    }

    private static void checkStatus(List<Thread> threads) {
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread " + t.getName() + " finished!");
        }
    }

    private static String[] getCommands(Genome genome, Function function) {
        switch (function) {
            case GENOME_IDX:
                return SwCmd.genomeIndex(genome);
            default:
                throw new IllegalArgumentException("Illegal Function args in genome analysis!");
        }
    }

    private static String[] getCommands(final Experiment exp, Function function) {
        final String geneList = ConfigInitializer.getPath(Out.GENE_LIST) + exp.getCode() + Constant.SUFFIX_GENE_LIST;
        switch (function) {
            case QC_RAW:
                return SwCmd.qcRawReads(exp);
            case TRIM:
                String rawFastqPfx = exp.getFastq1().substring(0, exp.getFastq1().lastIndexOf('.'));
                //before trim, parse info from qc zip file to JSON QCInfo Object
                ParseZip.newInstance().parse(ConfigInitializer.getPath(Out.QC_RAW) + rawFastqPfx + Constant.QC_ZIP_SFX,
                        ConfigInitializer.getPath(Out.PARSE_ZIP), exp.getCode());
                return SwCmd.trimReads(exp);
            case QC_CLEAN:
                return SwCmd.qcCleanReads(exp);
            case ALIGNMENT:
                return SwCmd.alignment(exp);
            case CONVERT_SAM:
                return SwCmd.convertSamToBam(exp);
            case SORT_BAM:
                return SwCmd.sortBam(exp);
            case RMDUP_BAM:
                return SwCmd.rmdupBam(exp);
            case UNIQUE_BAM:
                return SwCmd.uniqueBam(exp);
            case QC_BAM:
                return SwCmd.qcBam(exp);
            case PEAK_CALLING:
                return SwCmd.callPeaks(exp);
            case MOTIF:
                return SwCmd.findMotifs(exp);
            case PEAK_ANNOTATION:
                return SwCmd.annotatePeaks(exp);
            case GENE_LIST:
                String annotatedPeak = ConfigInitializer.getPath(Out.ANNOTATION) + exp.getCode() + Constant.SUFFIX_ANNO_BED;
                SwUtil.extractGeneList(annotatedPeak, geneList);
                return new String[]{
                        "echo\"GENE_LIST are located at: " + annotatedPeak + "\" "
                };
            case GO_PATHWAY:
                final String outGoFile = ConfigInitializer.getPath(Out.GO_PATHWAY) + exp.getCode() + Constant.SUFFIX_GO_PATHWAY;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("run go & pathway in background!");
                        PantherAnalysis.newInstance(exp.getCode(), exp.getGenomeCode(), geneList, outGoFile).analysis();
                    }
                }).start();
                return new String[]{
                        "echo \"GO_PATHWAY results are located at: " + outGoFile + "\"",
                        "echo \"genome code is: " + exp.getGenomeCode() + "\"",
                        "echo \"gene list file at: " + geneList + "\""};
            default:
                throw new IllegalArgumentException("Illegal Function args in experiment analysis!");
        }
    }
}
