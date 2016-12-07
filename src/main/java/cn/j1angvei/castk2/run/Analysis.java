package cn.j1angvei.castk2.run;

import cn.j1angvei.castk2.Function;
import cn.j1angvei.castk2.cmd.SwCmd;
import cn.j1angvei.castk2.input.Experiment;
import cn.j1angvei.castk2.input.Genome;
import cn.j1angvei.castk2.util.ConfUtil;

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
            case PEAK_CALLING:
            case PEAK_ANNOTATION:
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

    private static String[] getCommands(Experiment experiment, Function function) {
        switch (function) {
            case QC_RAW:
                return SwCmd.qcRawReads(experiment);
            case TRIM:
                return SwCmd.trimReads(experiment);
            case QC_CLEAN:
                return SwCmd.qcCleanReads(experiment);
            case ALIGNMENT:
                return SwCmd.alignment(experiment);
            case CONVERT_SAM:
                return SwCmd.convertSamToBam(experiment);
            case SORT_BAM:
                return SwCmd.sortBam(experiment);
            case QC_BAM:
                return SwCmd.qcBam(experiment);
            case PEAK_CALLING:
                return SwCmd.callPeaks(experiment);
            case PEAK_ANNOTATION:
                return SwCmd.annotatePeaks(experiment);
            default:
                throw new IllegalArgumentException("Illegal Function args in experiment analysis!");
        }
    }
}
