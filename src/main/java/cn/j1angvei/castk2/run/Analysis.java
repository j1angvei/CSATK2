package cn.j1angvei.castk2.run;

import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.Function;
import cn.j1angvei.castk2.cmd.SwCmd;
import cn.j1angvei.castk2.input.Experiment;
import cn.j1angvei.castk2.input.Genome;
import cn.j1angvei.castk2.panther.PantherAnalysis;
import cn.j1angvei.castk2.qc.ParseZip;
import cn.j1angvei.castk2.type.OutType;
import cn.j1angvei.castk2.type.SubType;
import cn.j1angvei.castk2.util.ConfUtil;
import cn.j1angvei.castk2.util.SwUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j1angvei on 2016/12/7.
 */
public class Analysis {
    private static ConfUtil CONF = ConfUtil.getInstance();

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

    private static String[] getCommands(final Experiment experiment, Function function) {
        final String geneList = CONF.getDirectory(OutType.GENE_LIST) + experiment.getCode() + Constant.SUFFIX_GENE_LIST;
        switch (function) {
            case QC_RAW:
                return SwCmd.qcRawReads(experiment);
            case TRIM:
                //before trim, parse info from qc zip file to JSON QCInfo Object
                ParseZip.getInstance().parse(CONF.getDirectory(SubType.INPUT) + experiment.getFastq1() + Constant.QC_ZIP_SFX,
                        CONF.getDirectory(OutType.PARSE_ZIP), experiment.getCode());
                return SwCmd.trimReads(experiment);
            case QC_CLEAN:
                return SwCmd.qcCleanReads(experiment);
            case ALIGNMENT:
                return SwCmd.alignment(experiment);
            case CONVERT_SAM:
                return SwCmd.convertSamToBam(experiment);
            case SORT_BAM:
                return SwCmd.sortBam(experiment);
            case RMDUP_BAM:
                return SwCmd.rmdupBam(experiment);
            case UNIQUE_BAM:
                return SwCmd.uniqueBam(experiment);
            case QC_BAM:
                return SwCmd.qcBam(experiment);
            case PEAK_CALLING:
                return SwCmd.callPeaks(experiment);
            case MOTIF:
                return SwCmd.findMotifs(experiment);
            case PEAK_ANNOTATION:
                return SwCmd.annotatePeaks(experiment);
            case GENE_LIST:
                String annotatedPeak = CONF.getDirectory(OutType.ANNOTATION) + experiment.getCode() + Constant.SUFFIX_ANNO_BED;
                SwUtil.extractGeneList(annotatedPeak, geneList);
                return new String[]{
                        "echo\"GENE_LIST are located at: " + annotatedPeak + "\" "
                };
            case GO_PATHWAY:
                final String outGoFile = CONF.getDirectory(OutType.GO_PATHWAY) + experiment.getCode() + Constant.SUFFIX_GO_PATHWAY;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("run go & pathway in background!");
                        PantherAnalysis.newInstance(experiment.getCode(), experiment.getGenomeCode(), geneList, outGoFile).analysis();
                    }
                }).start();
                return new String[]{
                        "echo \"GO_PATHWAY results are located at: " + outGoFile + "\"",
                        "echo \"genome code is: " + experiment.getGenomeCode() + "\"",
                        "echo \"gene list file at: " + geneList + "\""};
            default:
                throw new IllegalArgumentException("Illegal Function args in experiment analysis!");
        }
    }
}
