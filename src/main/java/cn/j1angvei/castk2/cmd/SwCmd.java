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
    private static final int THREAD_NUMBER = 10;
    private static ConfigInitializer CONF = ConfigInitializer.getInstance();

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
        String outputPrefix = ConfigInitializer.getPath(Out.TRIM) + experiment.getCode();

        QCInfo qcInfo = getQCInfo(experiment);
        if (isPairEnd(experiment)) {
            //pair end
            String fastq2 = ConfigInitializer.getPath(Sub.INPUT) + experiment.getFastq2();
            cmd = String.format("%s -Xmx256m -jar %s PE -threads %d %s %s %s %s %s %s %s " + TRIM_PARAM,
                    CONF.getJava(),
                    CONF.getSwExecutable(Software.TRIMMOMATIC),
                    THREAD_NUMBER,
                    qcInfo.getPhred(),
                    fastq1,
                    fastq2,
                    outputPrefix + "_1." + StrUtil.getSuffix(experiment.getFastq1()),
                    outputPrefix + "_1_unpaired." + StrUtil.getSuffix(experiment.getFastq1()),
                    outputPrefix + "_2." + StrUtil.getSuffix(experiment.getFastq2()),
                    outputPrefix + "_2_unpaired." + StrUtil.getSuffix(experiment.getFastq2()),
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
                    outputPrefix + "." + StrUtil.getSuffix(experiment.getFastq1()),
                    qcInfo.getFaFilePath(),
                    qcInfo.getLength() / 3,
                    qcInfo.getHeadCrop());
        }
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcCleanReads(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        cmd.add(String.format("%s -o %s -t %d %s",
                CONF.getSwExecutable(Software.FASTQC),
                ConfigInitializer.getPath(Out.QC_CLEAN),
                THREAD_NUMBER,
                ConfigInitializer.getPath(Out.TRIM) + experiment.getCode()) + "_1." + StrUtil.getSuffix(experiment.getFastq1())
        );
        if (isPairEnd(experiment)) {
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSwExecutable(Software.FASTQC),
                    ConfigInitializer.getPath(Out.QC_CLEAN),
                    THREAD_NUMBER,
                    ConfigInitializer.getPath(Out.TRIM) + experiment.getCode() + "_2." + StrUtil.getSuffix(experiment.getFastq2())
            ));
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
            fastq1 = ConfigInitializer.getPath(Out.TRIM) + experiment.getCode() + "_1." + StrUtil.getSuffix(experiment.getFastq1());
            fastq2 = ConfigInitializer.getPath(Out.TRIM) + experiment.getCode() + "_2." + StrUtil.getSuffix(experiment.getFastq2());
            sai1 = ConfigInitializer.getPath(Out.ALIGNMENT) + experiment.getCode() + "_1" + Constant.SAI_SFX;
            sai2 = ConfigInitializer.getPath(Out.ALIGNMENT) + experiment.getCode() + "_2" + Constant.SAI_SFX;

        } else {
            //single end data
            fastq1 = ConfigInitializer.getPath(Out.TRIM) + experiment.getCode() + "." + StrUtil.getSuffix(experiment.getFastq1());
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
                ConfigInitializer.getPath(Out.BAM_CONVERTED) + experiment.getCode() + Constant.SUFFIX_CONVERTED_BAM);
        return FileUtil.wrapString(cmd);
    }

    public static String[] sortBam(Experiment experiment) {
        String cmd = String.format("%s sort %s --threads %d -o %s",
                CONF.getSwExecutable(Software.SAMTOOLS),
                ConfigInitializer.getPath(Out.BAM_CONVERTED) + experiment.getCode() + Constant.SUFFIX_CONVERTED_BAM,
                THREAD_NUMBER,
                ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode() + Constant.SUFFIX_SORTED_BAM);
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcBam(Experiment experiment) {
        String sortBamFile = ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode() + Constant.SUFFIX_SORTED_BAM;
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
                ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode() + Constant.SUFFIX_SORTED_BAM,
                ConfigInitializer.getPath(Out.BAM_RMDUP) + experiment.getCode() + Constant.SUFFIX_RMDUP_BAM
        );
        return FileUtil.wrapString(cmd);
    }

    public static String[] uniqueBam(Experiment experiment) {
        String cmd = String.format("%s view -b %s -q 30 -o %s",
                CONF.getSwExecutable(Software.SAMTOOLS),
                ConfigInitializer.getPath(Out.BAM_RMDUP) + experiment.getCode() + Constant.SUFFIX_RMDUP_BAM,
                ConfigInitializer.getPath(Out.BAM_UNIQUE) + experiment.getCode() + Constant.SUFFIX_UNIQUE_BAM
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

        //has no control experiment
        //single "macs2 callpeak -t ChIP.bam -c Control.bam -f BAM -g hs -n test -B -q 0.01"
        String callPeakCmd = String.format("%s callpeak -t %s -f BAM -g %s -n %s -B",
                CONF.getSwExecutable(Software.MACS2),
                ConfigInitializer.getPath(Out.BAM_UNIQUE) + experiment.getCode() + Constant.SUFFIX_UNIQUE_BAM,
                gSize,
                ConfigInitializer.getPath(Out.PEAK_CALLING) + experiment.getCode());
        //broad peaks need to set '--broad' parameter
        if (experiment.isBroadPeak()) {
            callPeakCmd += " --broad";
        }
        cmd.add(callPeakCmd);
        return FileUtil.listToArray(cmd);
    }

    public static String[] annotatePeaks(Experiment experiment) {
        String sfxPeakFile = experiment.isBroadPeak() ? Constant.SFX_BROAD_PEAKS : Constant.SFX_NARROW_PEAKS;
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        String annoFileName = genome.getAnnotation();
        String annoFormat = StrUtil.getSuffix(annoFileName);
        List<String> cmd = new ArrayList<>();
        cmd.add(OsCmd.addPath(SwUtil.getPath(Software.HOMER)));
        cmd.add(String.format("%s %s %s -%s %s > %s",
                CONF.getSwExecutable(Software.HOMER) + Constant.EXE_HOMER_ANNOTATE_PEAK,
                ConfigInitializer.getPath(Out.PEAK_CALLING) + experiment.getCode() + sfxPeakFile,
                ConfigInitializer.getPath(Sub.GENOME) + genome.getFasta(),
                annoFormat,
                ConfigInitializer.getPath(Sub.GENOME) + genome.getAnnotation(),
                ConfigInitializer.getPath(Out.ANNOTATION) + experiment.getCode() + Constant.SUFFIX_ANNO_BED)
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

    public static String[] flagStat(Experiment experiment) {
        //for sorted bam, rmdup bam, q>30 bam
        String[] commands = new String[3];
        String exe = CONF.getSwExecutable(Software.SAMTOOLS);
        //sorted bam file
        String sortedBam = ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode() + Constant.SUFFIX_SORTED_BAM;
        String sortedStat = ConfigInitializer.getPath(Out.BAM_SORTED) + experiment.getCode() + Constant.FLAGSTAT_SFX;
        commands[0] = String.format("%s flagstat %s > %s", exe, sortedBam, sortedStat);
        //rmdup bam file
        String rmdupBam = ConfigInitializer.getPath(Out.BAM_RMDUP) + experiment.getCode() + Constant.SUFFIX_RMDUP_BAM;
        String rmdupStat = ConfigInitializer.getPath(Out.BAM_RMDUP) + experiment.getCode() + Constant.FLAGSTAT_SFX;
        commands[1] = String.format("%s flagstat %s > %s", exe, rmdupBam, rmdupStat);
        //q>30 bam file
        String q30Bam = ConfigInitializer.getPath(Out.BAM_UNIQUE) + experiment.getCode() + Constant.SUFFIX_UNIQUE_BAM;
        String q30Stat = ConfigInitializer.getPath(Out.BAM_UNIQUE) + experiment.getCode() + Constant.FLAGSTAT_SFX;
        commands[2] = String.format("%s flagstat %s > %s", exe, q30Bam, q30Stat);
        return commands;
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
