package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.ConfigInitializer;
import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.Function;
import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.conf.Experiment;
import cn.j1angvei.castk2.conf.Genome;
import cn.j1angvei.castk2.conf.Resource;
import cn.j1angvei.castk2.html.HtmlGenerator;
import cn.j1angvei.castk2.panther.PantherAnalysis;
import cn.j1angvei.castk2.qc.ParseZip;
import cn.j1angvei.castk2.stat.StatType;
import cn.j1angvei.castk2.stat.Statistics;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;
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

    private Analysis() {
        initializer = ConfigInitializer.getInstance();
        expThreadNum = initializer.getConfig().getExpThread();
    }

    public static Analysis getInstance() {
        return new Analysis();
    }

    public void runFunction(Function function) {
        switch (function) {
            //function should iterate genomes
            case GENOME_IDX:
            case GENOME_SIZE:
            case GENOME_TSS:
                iterateGenomes(function);
                break;

            //function should start in native shell command
            case QC_RAW:
            case TRIM:
            case QC_CLEAN:
            case ALIGNMENT:
            case CONVERT_SAM:
            case SORT_BAM:
            case RMDUP_BAM:
            case UNIQUE_BAM:
            case QC_BAM:
            case PEAK_CALLING:
            case MOTIF:
            case PEAK_ANNOTATION:
            case FLAGSTAT:
//            case BIGWIG:
            case BW_DT:
            case PEAK_HEATMAP:
            case TSS_PROFILE:
                iterateExperiment(function, true);
                break;

            //function should start in pure Java, implemented in CSATK
            case STATISTIC:
                //before start statistics, create all stat files first
                Statistics.initStatisticsFile();
            case PARSE_RAW:
            case PARSE_CLEAN:
            case GENE_LIST:
            case GO_PATHWAY:
                iterateExperiment(function, false);
                break;

            //function should start only once, without iterate genome or experiment
            case CHIP_QUALITY:
            case CORRELATION:
                runShellFunction(function);
                break;
            case HTML:
                String statDir = ConfigInitializer.getPath(Out.STATISTICS);
                HtmlGenerator.getInstance(statDir).generate();
                break;
            default:
                System.err.println("Illegal function keyword " + function.getKeyword());
                break;
        }
    }

    private void runShellFunction(final Function function) {
        final List<Callable<String>> expCalls = new ArrayList<>();
        final String jobTitle = String.format("Project_%s", function.name());
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.format("Job %s submitted...\n", jobTitle);
                ShellExecutor.execute(jobTitle, SwCmd.getProjectCommands(function));
                return jobTitle;
            }
        };

        expCalls.add(callable);
        pendingCallable(expCalls);
    }

    private void iterateExperiment(final Function function, final boolean isShellJob) {
        final List<Callable<String>> expCalls = new ArrayList<>();
        for (final Experiment experiment : initializer.getExperiments()) {
            //job description for user to identify
            final String jodTitle = String.format("Experiment_%s_%s", experiment.getCode(), function.name());
            final Callable<String> callable = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    System.out.format("Job %s submitted...\n", jodTitle);
                    //shell job should start using ShellExecutor
                    if (isShellJob) {
                        String[] commands = SwCmd.getExperimentCommands(function, experiment);
                        ShellExecutor.execute(jodTitle, commands);
                    } else {
                        runBuiltInFunction(function, experiment);
                    }
                    return jodTitle;
                }
            };
            //add jobs to the list
            expCalls.add(callable);
        }
        //wait all jobs finished
        pendingCallable(expCalls);
    }

    private void runBuiltInFunction(Function function, final Experiment experiment) {

        final String geneList = ConfigInitializer.getPath(Out.GENE_LIST) + experiment.getCode() + Constant.SFX_GENE_LIST;
        //using pure CSATK built-in Java function in CSATK depending on the Function
        switch (function) {
            //parse qc info from raw data's FastQC result
            case PARSE_RAW:
                String rawFastqPfx = experiment.getFastq1().substring(0, experiment.getFastq1().lastIndexOf('.'));
                //before trim, parse info from qc zip file to JSON QCInfo Object
                ParseZip.newInstance().parse(ConfigInitializer.getPath(Out.QC_RAW)
                        + rawFastqPfx + Constant.QC_ZIP_SFX, ConfigInitializer.getPath(Out.PARSE_RAW), experiment.getCode());
                break;
            //parse qc info from filtered data's FastQC result
            case PARSE_CLEAN:
                String zipPath = ConfigInitializer.getPath(Out.QC_CLEAN) + experiment.getCode();
                if (StrUtil.isValid(experiment.getFastq2())) {
                    zipPath += "_1" + Constant.QC_ZIP_SFX;
                } else {
                    zipPath += Constant.QC_ZIP_SFX;
                }
                ParseZip.newInstance().parse(zipPath,
                        ConfigInitializer.getPath(Out.PARSE_CLEAN),
                        experiment.getCode());
                break;
            //parse gene list from peak annotation result
            case GENE_LIST:
                String annotatedPeak = ConfigInitializer.getPath(Out.ANNOTATION) + experiment.getCode() + Constant.SFX_ANNO_BED;
                SwUtil.extractGeneList(annotatedPeak, geneList);
                break;
            //do go & pathway analysis using PANTHER
            case GO_PATHWAY:
                final String outGoFile = ConfigInitializer.getPath(Out.GO_PATHWAY) + experiment.getCode() + Constant.SFX_GO_PATHWAY;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(experiment.getCode() + " start go & pathway in background!");
                        PantherAnalysis.newInstance(experiment.getCode(), experiment.getGenomeCode(), geneList, outGoFile).analysis();
                    }
                }).start();
                break;
            //do statistics
            case STATISTIC:
                String outDir = ConfigInitializer.getPath(Out.STATISTICS);
                for (StatType type : StatType.values()) {
                    String outFilePath = outDir + type.getResFileName();
                    String content = Statistics.start(experiment, type);
                    FileUtil.appendFile(content, outFilePath, false);
                }
                break;
            default:
                System.out.printf("Wrong function keyword \'%s\'for running CSATK built-in function in %s\n"
                        , function.getKeyword(), experiment.getCode());
                break;
        }
    }

    private void iterateGenomes(final Function function) {
        List<Callable<String>> genomeCalls = new ArrayList<>();
        for (final Genome genome : initializer.getGenomes()) {
            final String jobTitle = String.format("Genome_%s_%s", genome.getName(), function.name());
            Callable<String> callable = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    System.out.format("Job %s submitted...\n", jobTitle);
                    String[] commands = SwCmd.getGenomeCommands(function, genome);
                    ShellExecutor.execute(jobTitle, commands);
                    //if running calculating genome size, use new value and replace it
                    if (function == Function.GENOME_SIZE) {
                        updateGenomeSize(genome);
                    }
                    return jobTitle;
                }
            };
            genomeCalls.add(callable);
        }
        pendingCallable(genomeCalls);

        //after calculate genome size, write it back to input.json file
        if (Function.GENOME_SIZE == function) {
            String inputJson = GsonUtil.toJson(initializer.getInput());
            FileUtil.overwriteFile(inputJson, ConfigInitializer.getPath(Directory.Sub.CONFIG) + Resource.INPUT.getFileName());
        }
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
                int size = futures.size();
                if (size == 0) {
                    System.out.println("All jobs complete!");
                    service.shutdownNow();
                    break;
                } else {
                    TimeUnit.SECONDS.sleep(5);
                }
            }

        } catch (Exception e) {
            System.err.println("Unknown error happened to background jobs, shut them all down");
            e.printStackTrace();
            service.shutdownNow();
        }
    }

    private ExecutorService newThreadPoolInstance() {
        if (expThreadNum == 0) {
            expThreadNum = Runtime.getRuntime().availableProcessors();
        }
        return Executors.newFixedThreadPool(expThreadNum);
    }

//    private String[] getCommands(Genome genome, Function function) {
//        switch (function) {
//            case GENOME_IDX:
//                return SwCmd.genomeIndex(genome);
//            case GENOME_SIZE:
//                return SwCmd.faidx(genome);
//            default:
//                throw new IllegalArgumentException("Illegal Function args in genome analysis!");
//        }
//    }
//
//    private String[] getCommands(final Experiment exp, Function function) {
//        final String geneList = ConfigInitializer.getPath(Out.GENE_LIST) + exp.getCode() + Constant.SFX_GENE_LIST;
//        switch (function) {
//            case QC_RAW:
//                return SwCmd.qcRawReads(exp);
//            case PARSE_RAW:
//                String rawFastqPfx = exp.getFastq1().substring(0, exp.getFastq1().lastIndexOf('.'));
//                //before trim, parse info from qc zip file to JSON QCInfo Object
//                ParseZip.newInstance().parse(ConfigInitializer.getPath(Out.QC_RAW) + rawFastqPfx + Constant.QC_ZIP_SFX,
//                        ConfigInitializer.getPath(Out.PARSE_RAW), exp.getCode());
//                return SwCmd.emptyCmd(function);
//            case TRIM:
//                return SwCmd.trimReads(exp);
//            case QC_CLEAN:
//                return SwCmd.qcCleanReads(exp);
//            case PARSE_CLEAN:
//                String zipPath = ConfigInitializer.getPath(Out.QC_CLEAN) + exp.getCode();
//                if (StrUtil.isValid(exp.getFastq2())) {
//                    zipPath += "_1" + Constant.QC_ZIP_SFX;
//                } else {
//                    zipPath += Constant.QC_ZIP_SFX;
//                }
//                ParseZip.newInstance().parse(zipPath,
//                        ConfigInitializer.getPath(Out.PARSE_CLEAN),
//                        exp.getCode());
//                return SwCmd.emptyCmd(function);
//            case ALIGNMENT:
//                return SwCmd.alignment(exp);
//            case CONVERT_SAM:
//                return SwCmd.convertSamToBam(exp);
//            case SORT_BAM:
//                return SwCmd.sortBam(exp);
//            case RMDUP_BAM:
//                return SwCmd.rmdupBam(exp);
//            case UNIQUE_BAM:
//                return SwCmd.uniqueBam(exp);
//            case QC_BAM:
//                return SwCmd.qcBam(exp);
//            case PEAK_CALLING:
//                return SwCmd.callPeaks(exp);
//            case MOTIF:
//                return SwCmd.findMotifs(exp);
//            case PEAK_ANNOTATION:
//                return SwCmd.annotatePeaks(exp);
//            case GENE_LIST:
//                String annotatedPeak = ConfigInitializer.getPath(Out.ANNOTATION) + exp.getCode() + Constant.SFX_ANNO_BED;
//                SwUtil.extractGeneList(annotatedPeak, geneList);
//                return SwCmd.emptyCmd(function);
//            case GO_PATHWAY:
//                final String outGoFile = ConfigInitializer.getPath(Out.GO_PATHWAY) + exp.getCode() + Constant.SFX_GO_PATHWAY;
//                new Thread(new Runnable() {
//                    @Override
//                    public void start() {
//                        System.out.println(exp.getCode() + " start go & pathway in background!");
//                        PantherAnalysis.newInstance(exp.getCode(), exp.getGenomeCode(), geneList, outGoFile).analysis();
//                    }
//                }).start();
//                return SwCmd.emptyCmd(function);
//            case FLAGSTAT:
//                return SwCmd.bamIndexStat(exp);
//            case STATISTIC:
//                String outDir = ConfigInitializer.getPath(Out.STATISTICS);
//                for (StatType type : StatType.values()) {
//                    String outFilePath = outDir + type.getResFileName();
//                    String content = Statistics.start(exp, type);
//                    FileUtil.appendFile(content, outFilePath, false);
//                }
//                return SwCmd.emptyCmd(function);
//            case HTML:
//                return SwCmd.emptyCmd(function);
//            case BIGWIG:
//                return SwCmd.bigwig(exp);
//            default:
//                throw new IllegalArgumentException("Illegal Function args in experiment analysis!");
//        }
//    }
}
