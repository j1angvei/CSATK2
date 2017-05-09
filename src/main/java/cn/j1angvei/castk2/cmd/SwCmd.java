package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.ConfigInitializer;
import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.Function;
import cn.j1angvei.castk2.conf.Experiment;
import cn.j1angvei.castk2.conf.Genome;
import cn.j1angvei.castk2.conf.Software;
import cn.j1angvei.castk2.qc.QCInfo;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;
import cn.j1angvei.castk2.util.StrUtil;
import cn.j1angvei.castk2.util.SwUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static cn.j1angvei.castk2.conf.Directory.Out;
import static cn.j1angvei.castk2.conf.Directory.Sub;

/**
 * generate all commands for corresponding function
 * Created by j1angvei on 2016/11/29.
 */
public class SwCmd {
    private static final String TRIM_PARAM = "ILLUMINACLIP:%s:2:30:10 LEADING:3 TRAILING:3 SLIDINGWINDOW:4:15 AVGQUAL:20 MINLEN:%d HEADCROP:%d";
    private static ConfigInitializer CONF = ConfigInitializer.getInstance();
    private static final int THREAD_NUMBER = CONF.getConfig().getThread();

    public static String[] getExperimentCommands(Function function, Experiment exp) {
        switch (function) {
            case QC_RAW:
                return SwCmd.qcRawReads(exp);
            case TRIM:
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
            case FLAGSTAT:
                return SwCmd.bamIndexStat(exp);
            case BIGWIG:
                return SwCmd.bigwig(exp);
            case TSS_PROFILE:
                return SwCmd.tssProfile(exp);
            case BW_DT:
                return SwCmd.bigwigDt(exp);
            case PEAK_HEATMAP:
                return SwCmd.peakHeatmap(exp);
            default:
                System.err.println("Wrong function  keyword in experiment analysis, return empty command");
                return SwCmd.emptyCmd(function);
        }
    }

    public static String[] getProjectCommands(Function function) {
        switch (function) {
            case CORRELATION:
                return correlation();
            case CHIP_QUALITY:
                return chipQuality();
            default:
                return new String[0];
        }

    }

    public static String[] getGenomeCommands(Function function, Genome genome) {
        switch (function) {
            case GENOME_IDX:
                return SwCmd.genomeIndex(genome);
            case GENOME_SIZE:
                return SwCmd.faidx(genome);
            case GENOME_TSS:
                return SwCmd.parseTSS(genome);
            default:
                System.err.println("Wrong function  keyword in genome analysis, return empty command");
                return SwCmd.emptyCmd(function);
        }
    }

    public static String[] genomeIndex(Genome genome) {
        String cmd = String.format("%s index -p %s %s",
                CONF.getSwExecutable(Software.BWA),
                ConfigInitializer.getPath(Out.IDX_GENOME) + genome.getCode(),
                ConfigInitializer.getPath(Sub.GENOME) + genome.getFasta());
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcRawReads(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        cmd.add(String.format("%s -o %s -t %d %s",
                CONF.getSwExecutable(Software.FASTQC),
                ConfigInitializer.getPath(Out.QC_RAW),
                THREAD_NUMBER,
                ConfigInitializer.getPath(Sub.INPUT) + experiment.getFastq1())
        );
        if (isPairEnd(experiment)) {
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSwExecutable(Software.FASTQC),
                    ConfigInitializer.getPath(Out.QC_RAW),
                    THREAD_NUMBER,
                    ConfigInitializer.getPath(Sub.INPUT) + experiment.getFastq2())
            );
        }
        return FileUtil.listToArray(cmd);
    }

    private static QCInfo getQCInfo(Experiment experiment) {
        File qcFile = new File(ConfigInitializer.getPath(Out.PARSE_RAW) + experiment.getCode() + Constant.JSON_SFX);
        String qcContent = null;
        try {
            qcContent = FileUtils.readFileToString(qcFile, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return GsonUtil.fromJson(qcContent);
    }

    public static String[] trimReads(Experiment experiment) {
        //before trim reads, get phred, minLen and fa file from qc results in zip file
        String cmd;
        String fastq1 = ConfigInitializer.getPath(Sub.INPUT) + experiment.getFastq1();
        String fastq1Suffix = StrUtil.getSuffixWithDot(experiment.getFastq1());
        String outputPrefix = ConfigInitializer.getPath(Out.TRIM) + experiment.getCode();

        QCInfo qcInfo = getQCInfo(experiment);
        if (isPairEnd(experiment)) {
            //pair end
            String fastq2 = ConfigInitializer.getPath(Sub.INPUT) + experiment.getFastq2();
            String fastq2Suffix = StrUtil.getSuffixWithDot(experiment.getFastq2());
            cmd = String.format("%s -Xmx256m -jar %s PE -threads %d %s %s %s %s %s %s %s " + TRIM_PARAM,
                    CONF.getJava(),
                    CONF.getSwExecutable(Software.TRIMMOMATIC),
                    THREAD_NUMBER,
                    qcInfo.getPhred(),
                    fastq1,
                    fastq2,
                    outputPrefix + "_1" + fastq1Suffix,
                    outputPrefix + "_1_unpaired" + fastq1Suffix,
                    outputPrefix + "_2" + fastq2Suffix,
                    outputPrefix + "_2_unpaired" + fastq2Suffix,
                    qcInfo.getFaFilePath(),
                    qcInfo.getLength() / 3,
                    qcInfo.getHeadCrop());

        } else {
            //single end
            cmd = String.format("%s -Xmx256m -jar %s SE -threads %d %s %s %s " + TRIM_PARAM,
                    CONF.getJava(),
                    CONF.getSwExecutable(Software.TRIMMOMATIC),
                    THREAD_NUMBER,
                    qcInfo.getPhred(),
                    fastq1,
                    outputPrefix + fastq1Suffix,
                    qcInfo.getFaFilePath(),
                    qcInfo.getLength() / 3,
                    qcInfo.getHeadCrop());
        }
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcCleanReads(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        String fastq1Suffix = StrUtil.getSuffixWithDot(experiment.getFastq1());
        if (isPairEnd(experiment)) {
            String fastq2Suffix = StrUtil.getSuffixWithDot(experiment.getFastq2());
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSwExecutable(Software.FASTQC),
                    ConfigInitializer.getPath(Out.QC_CLEAN),
                    THREAD_NUMBER,
                    ConfigInitializer.getPath(Out.TRIM) + experiment.getCode()) + "_1" + fastq1Suffix
            );
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSwExecutable(Software.FASTQC),
                    ConfigInitializer.getPath(Out.QC_CLEAN),
                    THREAD_NUMBER,
                    ConfigInitializer.getPath(Out.TRIM) + experiment.getCode() + "_2" + fastq2Suffix
            ));
        } else {
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSwExecutable(Software.FASTQC),
                    ConfigInitializer.getPath(Out.QC_CLEAN),
                    THREAD_NUMBER,
                    ConfigInitializer.getPath(Out.TRIM) + experiment.getCode()) + fastq1Suffix
            );
        }
        return FileUtil.listToArray(cmd);
    }

    public static String[] alignment(Experiment experiment) {
        QCInfo qcInfo = getQCInfo(experiment);
        //BWA executable absolute path
        String exe = CONF.getSwExecutable(Software.BWA);
        //reference genome index prefix with absolute path
        String ref = ConfigInitializer.getPath(Out.IDX_GENOME) + experiment.getGenomeCode();

        //two FastQ file,fastq2 is null if is SINGLE-END data
        String fastq1, fastq2 = null;
        //if sequence length short than 70bp,using BWA-ALN algorithm will generate SAI temporary index file
        String sai1, sai2 = null;
        if (isPairEnd(experiment)) {
            //pair end data
            fastq1 = ConfigInitializer.getPath(Out.TRIM) + experiment.getCode() + "_1" + StrUtil.getSuffixWithDot(experiment.getFastq1());
            fastq2 = ConfigInitializer.getPath(Out.TRIM) + experiment.getCode() + "_2" + StrUtil.getSuffixWithDot(experiment.getFastq2());
            sai1 = ConfigInitializer.getPath(Out.ALIGNMENT) + experiment.getCode() + "_1" + Constant.SAI_SFX;
            sai2 = ConfigInitializer.getPath(Out.ALIGNMENT) + experiment.getCode() + "_2" + Constant.SAI_SFX;

        } else {
            //single end data
            fastq1 = ConfigInitializer.getPath(Out.TRIM) + experiment.getCode() + StrUtil.getSuffixWithDot(experiment.getFastq1());
            sai1 = ConfigInitializer.getPath(Out.ALIGNMENT) + experiment.getCode() + Constant.SAI_SFX;
        }
        //alignment result SAM file's absolute path
        String sam = ConfigInitializer.getPath(Out.ALIGNMENT) + experiment.getCode() + Constant.SAM_SFX;
        int length = qcInfo.getLength();
        //do BWA-ALN algorithm
        if (length <= 70) {
            String[] commands;
            if (isPairEnd(experiment)) {
                //pair end
                commands = new String[3];
                //bwa aln ref.fa short_read.fq > aln_sa.sai
                commands[0] = String.format("%s aln %s %s > %s", exe, ref, fastq1, sai1);
                commands[1] = String.format("%s aln %s %s > %s", exe, ref, fastq2, sai2);
                //bwa sampe ref.fa aln_sa1.sai aln_sa2.sai read1.fq read2.fq > aln-pe.sam
                commands[2] = String.format("%s sampe %s %s %s %s %s > %s", exe, ref, sai1, sai2, fastq1, fastq2, sam);
            } else {
                //single end
                commands = new String[2];
                //bwa aln ref.fa short_read.fq > aln_sa.sai
                commands[0] = String.format("%s aln %s %s > %s", exe, ref, fastq1, sai1);
                //bwa samse ref.fa aln_sa.sai short_read.fq > aln-se.sam
                commands[1] = String.format("%s samse %s %s %s > %s", exe, ref, sai1, fastq1, sam);

            }
            return commands;
        }
        //do BWA-MEM algorithm
        else {
            String cmd = isPairEnd(experiment) ?
                    //pair end
                    String.format("%s mem -M %s -t %d %s %s > %s", exe, ref, THREAD_NUMBER, fastq1, fastq2, sam) :
                    //single end
                    String.format("%s mem -M %s -t %d %s > %s", exe, ref, THREAD_NUMBER, fastq1, sam);
            return FileUtil.wrapString(cmd);
        }

    }

    public static String[] convertSamToBam(Experiment experiment) {
        String cmd = String.format("%s view --threads %d -bS %s -o %s",
                CONF.getSwExecutable(Software.SAMTOOLS),
                THREAD_NUMBER,
                ConfigInitializer.getPath(Out.ALIGNMENT) + experiment.getCode() + Constant.SAM_SFX,
                ConfigInitializer.getPath(Out.BAM_CONVERTED) + experiment.getCode() + Constant.SFX_CONVERTED_BAM);
        return FileUtil.wrapString(cmd);
    }

    public static String[] sortBam(Experiment experiment) {
        String cmd = String.format("%s sort %s --threads %d -o %s",
                CONF.getSwExecutable(Software.SAMTOOLS),
                ConfigInitializer.getPath(Out.BAM_CONVERTED) + experiment.getCode() + Constant.SFX_CONVERTED_BAM,
                THREAD_NUMBER,
                ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode() + Constant.SFX_SORTED_BAM);
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcBam(Experiment experiment) {
        String sortBamFile = ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode() + Constant.SFX_SORTED_BAM;
        long sortBamSize = FileUtil.getFileSize(sortBamFile, FileUtil.Unit.MB);
        String cmd = String.format("%s bamqc -bam %s -outdir %s --java-mem-size=%dM",
                CONF.getSwExecutable(Software.QUALIMAP),
                sortBamFile,
                ConfigInitializer.getPath(Out.QC_BAM) + experiment.getCode(),
                sortBamSize / 2);
        return FileUtil.wrapString(cmd);
    }

    public static String[] rmdupBam(Experiment experiment) {
        String cmd = String.format("%s rmdup %s %s",
                CONF.getSwExecutable(Software.SAMTOOLS),
                ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode() + Constant.SFX_SORTED_BAM,
                ConfigInitializer.getPath(Out.BAM_RMDUP) + experiment.getCode() + Constant.SFX_RMDUP_BAM
        );
        return FileUtil.wrapString(cmd);
    }

    public static String[] uniqueBam(Experiment experiment) {
        String cmd = String.format("%s view -b %s -q 30 -o %s",
                CONF.getSwExecutable(Software.SAMTOOLS),
                ConfigInitializer.getPath(Out.BAM_RMDUP) + experiment.getCode() + Constant.SFX_RMDUP_BAM,
                ConfigInitializer.getPath(Out.BAM_UNIQUE) + experiment.getCode() + Constant.SFX_UNIQUE_BAM
        );
        return FileUtil.wrapString(cmd);
    }

    public static String[] callPeaks(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        //add python environment variable
        cmd.add(OsCmd.addPythonPath(CONF.getSwDestFolder(Software.MACS2)));
        //set genome size
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        double gSize = Double.parseDouble(genome.getSize());

        String controlHolder = "CONTROL_HOLDER";
        //example "macs2 callpeak -t ChIP.bam -c Control.bam -f BAM -g hs -n test -B -q 0.01"
        String callPeakCmd = String.format("%s callpeak -t %s " + controlHolder + " -f BAM -g %s -n %s -B",
                CONF.getSwExecutable(Software.MACS2),
                ConfigInitializer.getPath(Out.BAM_UNIQUE) + experiment.getCode() + Constant.SFX_UNIQUE_BAM,
                gSize,
                ConfigInitializer.getPath(Out.PEAK_CALLING) + experiment.getCode());
        //set with control
        String ctrlCode = experiment.getControl();
        if (CONF.getConfig().isCallWithControl() &&//user set to call peak with control
                StrUtil.isValid(ctrlCode) &&//this experiment has control
                !experiment.getCode().equals(ctrlCode)) {//this experiment is not control itself
            String control = "-c " + ConfigInitializer.getPath(Out.BAM_UNIQUE) +
                    ctrlCode + Constant.SFX_UNIQUE_BAM;
            callPeakCmd = callPeakCmd.replace(controlHolder, control);
        } else {
            callPeakCmd = callPeakCmd.replace(controlHolder, "");

        }

        //do broad peak calling
        if (experiment.isBroadPeak()) {
            cmd.add(callPeakCmd + " --broad -q 0.05");
        }
        //do default peak calling
        else {
            cmd.add(callPeakCmd);
        }
        return FileUtil.listToArray(cmd);
    }

    public static String[] annotatePeaks(Experiment experiment) {
        String sfxPeakFile = experiment.isBroadPeak() ? Constant.SFX_BROAD_PEAKS : Constant.SFX_NARROW_PEAKS;
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        String annoFileName = genome.getAnnotation();
        String annoFormat = StrUtil.getSuffixWithoutDot(annoFileName);
        List<String> cmd = new ArrayList<>();
        cmd.add(OsCmd.addPath(SwUtil.getPath(Software.HOMER)));
        cmd.add(String.format("%s %s %s -%s %s > %s",
                CONF.getSwExecutable(Software.HOMER) + Constant.EXE_HOMER_ANNOTATE_PEAK,
                ConfigInitializer.getPath(Out.PEAK_CALLING) + experiment.getCode() + sfxPeakFile,
                ConfigInitializer.getPath(Sub.GENOME) + genome.getFasta(),
                annoFormat,
                ConfigInitializer.getPath(Sub.GENOME) + genome.getAnnotation(),
                ConfigInitializer.getPath(Out.ANNOTATION) + experiment.getCode() + Constant.SFX_ANNO_BED)
        );
        return FileUtil.listToArray(cmd);
    }

    public static String[] findMotifs(Experiment experiment) {
        String sfxPeakFile = experiment.isBroadPeak() ? Constant.SFX_BROAD_PEAKS : Constant.SFX_NARROW_PEAKS;
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        List<String> cmd = new ArrayList<>();
        //add homer to environment path
        cmd.add(OsCmd.addPath(SwUtil.getPath(Software.HOMER)));
        //add weblogo to environment path
        cmd.add(OsCmd.addPath(SwUtil.getPath(Software.WEBLOGO)));
        //Usage: findMotifsGenome.pl <pos file> <genome> <output directory> [additional options]
        cmd.add(String.format("%s %s %s %s -size 200 -len 8",
                CONF.getSwExecutable(Software.HOMER) + Constant.EXE_HOMER_FIND_MOTIF,
                ConfigInitializer.getPath(Out.PEAK_CALLING) + experiment.getCode() + sfxPeakFile,
                ConfigInitializer.getPath(Sub.GENOME) + genome.getFasta(),
                ConfigInitializer.getPath(Out.MOTIF) + File.separator + experiment.getCode()
        ));
        return FileUtil.listToArray(cmd);
    }

    public static String[] bamIndexStat(Experiment experiment) {
        //for sorted bam, rmdup bam, q>30 bam
        List<String> commands = new ArrayList<>();
        String exeSamtools = CONF.getSwExecutable(Software.SAMTOOLS);
        //sorted bam file
        String sortBamPrefix = ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode();
        String sortedBam = sortBamPrefix + Constant.SFX_SORTED_BAM;
        String sortedStat = sortBamPrefix + Constant.FLAGSTAT_SFX;
        //build bam index
        commands.add(String.format("%s index -b %s", exeSamtools, sortedBam));
        commands.add(String.format("%s flagstat %s > %s", exeSamtools, sortedBam, sortedStat));

        //rmdup bam file
        String rmdupBamPrefix = ConfigInitializer.getPath(Out.BAM_RMDUP) + experiment.getCode();
        String rmdupBam = rmdupBamPrefix + Constant.SFX_RMDUP_BAM;
        String rmdupStat = rmdupBamPrefix + Constant.FLAGSTAT_SFX;
        commands.add(String.format("%s index -b %s", exeSamtools, rmdupBam));
        commands.add(String.format("%s flagstat %s > %s", exeSamtools, rmdupBam, rmdupStat));

        //q>30 bam file
        String uniqueBamPrefix = ConfigInitializer.getPath(Out.BAM_UNIQUE) + experiment.getCode();
        String uniqueBam = uniqueBamPrefix + Constant.SFX_UNIQUE_BAM;
        String uniqueStat = uniqueBamPrefix + Constant.FLAGSTAT_SFX;
        commands.add(String.format("%s index -b %s", exeSamtools, uniqueBam));
        commands.add(String.format("%s flagstat %s > %s", exeSamtools, uniqueBam, uniqueStat));
        return FileUtil.listToArray(commands);
    }

    public static String[] faidx(Genome genome) {
        String exeSamtools = CONF.getSwExecutable(Software.SAMTOOLS);
        String fasta = ConfigInitializer.getPath(Sub.GENOME) + genome.getFasta();
        List<String> cmd = new ArrayList<>();
        //samtool faidx command: samtools faidx <file.fa|file.fa.gz> [<reg> [...]]
        cmd.add(String.format("%s faidx %s", exeSamtools, fasta));
        //retrieve first 2 columns to assemble genome sizes
        //cut usage: cut -f 1,2 in.file > out.file
        cmd.add(String.format("cut -f 1,2 %s > %s", fasta + Constant.SFX_GENOME_FAIDX, fasta + Constant.SFX_GENOME_SIZES));
        return FileUtil.listToArray(cmd);
    }

    public static String[] bigwig(Experiment experiment) {
        List<String> commands = new ArrayList<>();
        String code = experiment.getCode();
        String bedGraph = ConfigInitializer.getPath(Out.PEAK_CALLING) + code + Constant.SFX_BEDGRAPH;
        String bigWig = ConfigInitializer.getPath(Out.BIGWIG) + code + Constant.SFX_UCSC_BIG_WIG;
        String wig = ConfigInitializer.getPath(Out.BIGWIG) + code + Constant.SFX_UCSC_WIG;

        String chromeSize = ConfigInitializer.getPath(Sub.GENOME) +
                CONF.getGenome(experiment.getGenomeCode()).getFasta() + Constant.SFX_GENOME_SIZES;
        //sort bedGraph in place
        String exeBedSort = CONF.getSwExecutable(Software.UCSC) + Constant.EXE_UCSC_BED_SORT;
        String exeBedGraphToBigWig = CONF.getSwExecutable(Software.UCSC) + Constant.EXE_UCSC_BEDGRAPH_2_BIGWIG;
        String exeBigWigToWig = CONF.getSwExecutable(Software.UCSC) + Constant.EXE_UCSC_BIGWIG_2_WIG;
        //bed sort usage: bedSort in.bed out.bed (in.bed and out.bed may be the same.)
        commands.add(String.format("%s %s %s", exeBedSort, bedGraph, bedGraph));
        //bedGraphToBigWig usage:  bedGraphToBigWig in.bedGraph chrom.sizes out.bw
        commands.add(String.format("%s %s %s %s", exeBedGraphToBigWig, bedGraph, chromeSize, bigWig));
        //bigWigToWig usage: bigWigToWig in.bigWig out.wig
        commands.add(String.format("%s %s %s", exeBigWigToWig, bigWig, wig));
        return FileUtil.listToArray(commands);
    }

    public static String[] bigwigDt(Experiment experiment) {
        boolean hasControl = hasControl(experiment);
        List<String> commands = new ArrayList<>();
//        commands.add(OsCmd.addPythonPath(CONF.getSwDestFolder(Software.DEEPTOOLS)));
        String exePrefix = getTmpDeeptools();
        String outPrefix = ConfigInitializer.getPath(Out.BIGWIG_DT) + experiment.getCode();

        //convert bam to bigwig using bamCompare and bamCoverage
        String exeBamCoverage = exePrefix + Constant.EXE_DT_BAMCOVERAGE;
        String exeBamCompare = exePrefix + Constant.EXE_DT_BAMCOMPARE;
        String treatBam = ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode() + Constant.SFX_SORTED_BAM;
        String outBw = outPrefix + Constant.SFX_DT_BIG_WIG;

        if (hasControl) {
            //has control experiment
            String controlBam = ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getControl() + Constant.SFX_SORTED_BAM;
            //bamCompare usage:bamCompare -b1 treatment.bam -b2 control.bam -o log2ratio.bw
            commands.add(String.format("%s -b1 %s -b2 %s -o %s -of bigwig", exeBamCompare, treatBam, controlBam, outBw));
        } else {
            //no control experiment
            //bamCoverage usage: bamCoverage -b reads.bam -o coverage.bw
            commands.add(String.format("%s -b %s -o %s -of bigwig", exeBamCoverage, treatBam, outBw));
        }
        return FileUtil.listToArray(commands);
    }

    private static boolean hasControl(Experiment experiment) {
        return StrUtil.isValid(experiment.getControl());
    }

//    public static String[] computeMatrix(Experiment experiment) {
//        List<String> commands = new ArrayList<>();
//        String exePrefix = getTmpDeeptools();
//        String exeComputeMatrix = exePrefix + Constant.EXE_DT_COMPUTEMATRIX;
//        //compute matrix, scale-regions or reference-point mode, we choose first one
//        //usage:  computeMatrix scale-regions -S <bigwig file> -R <bed file> -b 1000
//        //using bigwig generated by deeptools
//        String inTss = ConfigInitializer.getPath(Sub.GENOME) + CONF.getGenome(experiment.getGenomeCode()).getAnnotation() + Constant.SFX_GENOME_TSS;
//        String inPrefix = ConfigInitializer.getPath(Out.BIGWIG_DT) + experiment.getCode();
//        String inBw = inPrefix + Constant.SFX_DT_BIG_WIG;
//
//        String outPrefix = ConfigInitializer.getPath(Out.MATRIX) + experiment.getCode();
//        String outTssMatrix = outPrefix + Constant.SFX_DT_TSS_MATRIX;
//
//        commands.add(String.format("%s scale-regions -S %s -R %s -b 1000 -a 1000 -out %s",
//                exeComputeMatrix, inBw, inTss, outTssMatrix));
//
//        return FileUtil.listToArray(commands);
//
//    }

    public static String[] peakHeatmap(Experiment experiment) {
        //peak matrix
        //calculate matrix
        List<String> commands = new ArrayList<>();
        String exePrefix = getTmpDeeptools();
        String exeComputeMatrix = exePrefix + Constant.EXE_DT_COMPUTEMATRIX;
        String exePlotHeatmap = exePrefix + Constant.EXE_DT_PLOTHEATMAP;

        String inPeak = ConfigInitializer.getPath(Out.PEAK_CALLING) + experiment.getCode()
                + (experiment.isBroadPeak() ? Constant.SFX_BROAD_PEAKS : Constant.SFX_NARROW_PEAKS);
        String inBw = ConfigInitializer.getPath(Out.BIGWIG_DT) + experiment.getCode() + Constant.SFX_DT_BIG_WIG;

        String matrix = ConfigInitializer.getPath(Out.MATRIX) + experiment.getCode() + Constant.SFX_DT_PEAK_MATRIX;

        commands.add(String.format("%s scale-regions -S %s -R %s -b 1000 -a 1000 -out %s",
                exeComputeMatrix, inBw, inPeak, matrix));
        String outHeatmap = ConfigInitializer.getPath(Out.PEAK_HEATMAP) + experiment.getCode() + Constant.PNG_DT_HEATMAP;

        commands.add(String.format("%s -m %s -out %s --samplesLabel %s",
                exePlotHeatmap, matrix, outHeatmap, experiment.getCode()));
        return FileUtil.listToArray(commands);
    }

    public static String[] tssProfile(Experiment experiment) {

        List<String> commands = new ArrayList<>();
//        commands.add(OsCmd.addPythonPath(CONF.getSwDestFolder(Software.DEEPTOOLS)));
        //calculate matrix
        String exePrefix = getTmpDeeptools();
        String exeComputeMatrix = exePrefix + Constant.EXE_DT_COMPUTEMATRIX;
        //compute matrix, scale-regions or reference-point mode, we choose first one
        //usage:  computeMatrix scale-regions -S <bigwig file> -R <bed file> -b 1000
        //using bigwig generated by deeptools
        String inTss = ConfigInitializer.getPath(Sub.GENOME) + CONF.getGenome(experiment.getGenomeCode()).getAnnotation() + Constant.SFX_GENOME_TSS;
        String inBw = ConfigInitializer.getPath(Out.BIGWIG_DT) + experiment.getCode() + Constant.SFX_DT_BIG_WIG;

        String outTssMatrix = ConfigInitializer.getPath(Out.MATRIX) + experiment.getCode() + Constant.SFX_DT_TSS_MATRIX;

        commands.add(String.format("%s scale-regions -S %s -R %s -b 1000 -a 1000 -out %s",
                exeComputeMatrix, inBw, inTss, outTssMatrix));

        //plot profile usage: plotProfile -m matrix -out outfile
        String exePlotProfile = exePrefix + Constant.EXE_DT_PLOTPROFILE;
        String outPngProfile = ConfigInitializer.getPath(Out.TSS_PROFILE) + experiment.getCode() + Constant.PNG_DT_PROFILE;
        commands.add(String.format("%s -m %s -out %s --samplesLabel %s",
                exePlotProfile, outTssMatrix, outPngProfile, experiment.getCode()));

        return FileUtil.listToArray(commands);
    }


    /**
     * using deeptools's plotFingerprint commands
     *
     * @return plotFingerprint command
     */
    public static String[] chipQuality() {
        List<String> commands = new ArrayList<>();
//        commands.add(OsCmd.addPythonPath(CONF.getSwDestFolder(Software.DEEPTOOLS)));
        String exePrefix = getTmpDeeptools();
        String inPrefix = ConfigInitializer.getPath(Out.BAM_SORTED);
        String outPrefix = ConfigInitializer.getPath(Out.CHIP_QUALITY);

        StringBuilder bamList = new StringBuilder();
        StringBuilder labelList = new StringBuilder();
        for (Experiment e : CONF.getExperiments()) {
            bamList.append(inPrefix).append(e.getCode()).append(Constant.SFX_SORTED_BAM).append(" ");
            labelList.append(e.getCode()).append(" ");
        }

        String exeFingerprint = exePrefix + Constant.EXE_DT_PLOT_FINGERPRINT;
        String outPng = outPrefix + Constant.PNG_DT_FINGER_PRINT;
        String title = "\"Fingerprints of all experiments\"";
        //usage: An example usage is: plotFingerprint -b treatment.bam control.bam -plot fingerprint.png
        commands.add(String.format("%s -b %s -T %s -l %s -plot %s",
                exeFingerprint, bamList.toString(), title, labelList.toString(), outPng));

        return FileUtil.listToArray(commands);
    }

    private static String getTmpDeeptools() {
        return "";
    }

    public static String[] correlation() {
        List<String> commands = new ArrayList<>();
//        commands.add(OsCmd.addPythonPath(CONF.getSwDestFolder(Software.DEEPTOOLS)));
        //multi bigwig summary
//        String exePrefix = CONF.getSwExecutable(Software.DEEPTOOLS);
        String exePrefix = getTmpDeeptools();
        String inPrefix = ConfigInitializer.getPath(Out.BIGWIG_DT);
        String dirPrefix = ConfigInitializer.getPath(Out.CORRELATION);

        String exeMultiBigwigSummary = exePrefix + Constant.EXE_DT_MULTI_BIGWIG_SUMMARY;
        StringBuilder bigwigList = new StringBuilder();
        StringBuilder labelList = new StringBuilder();
        for (Experiment experiment : CONF.getExperiments()) {
            bigwigList.append(inPrefix).append(experiment.getCode()).append(Constant.SFX_DT_BIG_WIG).append(" ");
            labelList.append(experiment.getCode()).append(" ");
        }
        String outNpz = dirPrefix + Constant.SFX_DT_NPZ;
        commands.add(String.format("%s bins -b %s -out %s",
                exeMultiBigwigSummary, bigwigList.toString(), outNpz));

        //correlation
        String exeCorrelation = exePrefix + Constant.EXE_DT_PLOT_CORRELATION;
        String outCorrelation = dirPrefix + Constant.SFX_DT_CORRELATION;
        String plotTitle = " \"Spearman Correlation of Read Counts\"";
        commands.add(String.format("%s -in %s -c spearman --skipZeros  --plotTitle %s -p heatmap --colorMap RdYlBu -o %s -l %s -plotNumbers",
                exeCorrelation, outNpz, plotTitle, outCorrelation, labelList));
        return FileUtil.listToArray(commands);
    }

    public static String[] parseTSS(Genome genome) {
        List<String> commands = new ArrayList<>();
        //add path
        commands.add(OsCmd.addPath(SwUtil.getPath(Software.HOMER)));

        String exeParseGtf = CONF.getSwExecutable(Software.HOMER) + Constant.EXE_HOMER_PARSE_GTF;
        String annoFile = ConfigInitializer.getPath(Sub.GENOME) + genome.getAnnotation();
        String annoFormat = StrUtil.getSuffixWithoutDot(genome.getAnnotation()).toLowerCase();
        String outTss = annoFile + Constant.SFX_GENOME_TSS;

        //usage: parseGTF.pl <GTF format file> <mode> [options]; mode : tss, tts

        // options -gff -gff3, which is determined by annotation format
        String homerOption = "";
        if (annoFormat.equals("gff")) {
            homerOption = "-gff";
        } else if (annoFormat.equals("gff3")) {
            homerOption = "-gff3";
        }
        //mode: tss, tts, ann , etc. we only need tss
        String homerMode = "tss";
        commands.add(String.format("%s %s %s %s | awk '{OFS=\"\t\"} {print $2,$3,$4,$6,$1,$5}' > %s",
                exeParseGtf, annoFile, homerMode, homerOption, outTss));
        return FileUtil.listToArray(commands);
    }

    public static String[] emptyCmd(Function function) {
        return new String[]{
                "echo \"No native commands needed in this step:\"" + function.name()
        };
    }

    private static boolean isPairEnd(Experiment experiment) {
        return StrUtil.isValid(experiment.getFastq2());
    }


}
