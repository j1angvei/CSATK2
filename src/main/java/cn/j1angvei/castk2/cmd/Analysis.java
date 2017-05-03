package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.ConfigInitializer;
import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.Function;
import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.conf.Experiment;
import cn.j1angvei.castk2.conf.Genome;
import cn.j1angvei.castk2.html.HtmlGenerator;
import cn.j1angvei.castk2.panther.PantherAnalysis;
import cn.j1angvei.castk2.qc.ParseZip;
import cn.j1angvei.castk2.stat.StatType;
import cn.j1angvei.castk2.stat.Statistics;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.StrUtil;
import cn.j1angvei.castk2.util.SwUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

import static cn.j1angvei.castk2.conf.Directory.Out;

/**
 * Created by j1angvei on 2016/12/7.
 */
public class Analysis {
    private ConfigInitializer initializer;
    private int expThreadNum;

    public static Analysis getInstance() {
        return new Analysis();
    }

    private Analysis() {
        initializer = ConfigInitializer.getInstance();
        expThreadNum = initializer.getConfig().getExpThread();
    }

    public void runFunction(Function function) {
        switch (function) {
            case GENOME_IDX:
            case GENOME_SIZE:
                iterateGenomes(function);
                break;
            case HTML:
                String statDir = ConfigInitializer.getPath(Out.STATISTICS);
                HtmlGenerator.getInstance(statDir).generate();
                break;
            case STATISTIC:
                Statistics.initStatisticsFile();
            default:
                iterateExperiment(function);
        }
    }

    private void iterateExperiment(final Function function) {
        List<Callable<String>> expCallable = new ArrayList<>();
        for (final Experiment experiment : initializer.getExperiments()) {
            final String description = String.format("Experiment_%s_%s", experiment.getCode(), function.name());
            expCallable.add(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    System.out.format("Job %s submitted\n", description);
                    ShellExecutor.execute(description, getCommands(experiment, function));
                    return description;
                }
            });
        }
        pendingCallable(expCallable);
    }

    private void iterateGenomes(final Function function) {
        List<Callable<String>> genomeCallable = new ArrayList<>();
        for (final Genome genome : initializer.getGenomes()) {
            final String description = String.format("Genome_%s_%s", genome.getName(), function.name());
            genomeCallable.add(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    ShellExecutor.execute(description, getCommands(genome, function));
                    //if running calculating genome size, use new value and replace it
                    if (function.equals(Function.GENOME_SIZE)) {
                        updateGenomeSize(genome);
                    }
                    return description;
                }
            });
        }
        pendingCallable(genomeCallable);
    }

    private void updateGenomeSize(Genome genome) {
        long calculatedSize = SwUtil.calculateGenomeSize(
                ConfigInitializer.getPath(Directory.Sub.GENOME)
                        + genome.getFasta() + Constant.SFX_GENOME_SIZES);
        long originalSize;
        try {
            originalSize = Long.parseLong(genome.getSize());
        } catch (NumberFormatException e) {
            originalSize = calculatedSize;
        }
        if (originalSize == 0) {
            originalSize = calculatedSize;
        }
        genome.setSize(String.valueOf(originalSize));
    }

    private void pendingCallable(List<Callable<String>> callable) {
        List<Future<String>> futures = new ArrayList<>();
        ExecutorService service = newThreadPoolInstance();
        for (Callable<String> call : callable) {
            futures.add(service.submit(call));
        }
        try {
            //iterate all callable until they all finished and removed from list
            while (!futures.isEmpty()) {
                //iterate future, remove the finished one
                for (Iterator<Future<String>> iterator = futures.iterator(); iterator.hasNext(); ) {
                    Future<String> future = iterator.next();
                    if (future.isDone()) {
                        iterator.remove();
                        String description = future.get();
                        System.out.printf("Job %s complete!\n", description);
                    }
                }
                if (futures.size() == 0) {
                    System.out.println("All jobs complete!");
                    service.shutdownNow();
                    break;
                }
                TimeUnit.SECONDS.sleep(60);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private ExecutorService newThreadPoolInstance() {
        if (expThreadNum == 0) {
            expThreadNum = Runtime.getRuntime().availableProcessors();
        }
        return Executors.newFixedThreadPool(expThreadNum);
    }

    private String[] getCommands(Genome genome, Function function) {
        switch (function) {
            case GENOME_IDX:
                return SwCmd.genomeIndex(genome);
            case GENOME_SIZE:
                return SwCmd.faidx(genome);
            default:
                throw new IllegalArgumentException("Illegal Function args in genome analysis!");
        }
    }

    private String[] getCommands(final Experiment exp, Function function) {
        final String geneList = ConfigInitializer.getPath(Out.GENE_LIST) + exp.getCode() + Constant.SFX_GENE_LIST;
        switch (function) {
            case QC_RAW:
                return SwCmd.qcRawReads(exp);
            case PARSE_RAW:
                String rawFastqPfx = exp.getFastq1().substring(0, exp.getFastq1().lastIndexOf('.'));
                //before trim, parse info from qc zip file to JSON QCInfo Object
                ParseZip.newInstance().parse(ConfigInitializer.getPath(Out.QC_RAW) + rawFastqPfx + Constant.QC_ZIP_SFX,
                        ConfigInitializer.getPath(Out.PARSE_RAW), exp.getCode());
                return SwCmd.emptyCmd(function);
            case TRIM:
                return SwCmd.trimReads(exp);
            case QC_CLEAN:
                return SwCmd.qcCleanReads(exp);
            case PARSE_CLEAN:
                String zipPath = ConfigInitializer.getPath(Out.QC_CLEAN) + exp.getCode();
                if (StrUtil.isValid(exp.getFastq2())) {
                    zipPath += "_1" + Constant.QC_ZIP_SFX;
                } else {
                    zipPath += Constant.QC_ZIP_SFX;
                }
                ParseZip.newInstance().parse(zipPath,
                        ConfigInitializer.getPath(Out.PARSE_CLEAN),
                        exp.getCode());
                return SwCmd.emptyCmd(function);
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
                String annotatedPeak = ConfigInitializer.getPath(Out.ANNOTATION) + exp.getCode() + Constant.SFX_ANNO_BED;
                SwUtil.extractGeneList(annotatedPeak, geneList);
                return SwCmd.emptyCmd(function);
            case GO_PATHWAY:
                final String outGoFile = ConfigInitializer.getPath(Out.GO_PATHWAY) + exp.getCode() + Constant.SFX_GO_PATHWAY;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(exp.getCode() + " run go & pathway in background!");
                        PantherAnalysis.newInstance(exp.getCode(), exp.getGenomeCode(), geneList, outGoFile).analysis();
                    }
                }).start();
                return SwCmd.emptyCmd(function);
            case FLAGSTAT:
                return SwCmd.flagStat(exp);
            case STATISTIC:
                String outDir = ConfigInitializer.getPath(Out.STATISTICS);
                for (StatType type : StatType.values()) {
                    String outFilePath = outDir + type.getResFileName();
                    String content = Statistics.start(exp, type);
                    FileUtil.appendFile(content, outFilePath, false);
                }
                return SwCmd.emptyCmd(function);
            case HTML:
                return SwCmd.emptyCmd(function);
            case BIGWIG:
                return SwCmd.bigwig(exp);
            default:
                throw new IllegalArgumentException("Illegal Function args in experiment analysis!");
        }
    }
}
