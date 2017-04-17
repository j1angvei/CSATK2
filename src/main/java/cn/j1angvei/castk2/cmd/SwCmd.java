package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.input.Experiment;
import cn.j1angvei.castk2.input.Genome;
import cn.j1angvei.castk2.qc.QCInfo;
import cn.j1angvei.castk2.type.OutType;
import cn.j1angvei.castk2.type.PfType;
import cn.j1angvei.castk2.type.SubType;
import cn.j1angvei.castk2.type.SwType;
import cn.j1angvei.castk2.util.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * generate all commands for corresponding function
 * Created by j1angvei on 2016/11/29.
 */
public class SwCmd {
    private static final String PARAM = "ILLUMINACLIP:%s:2:30:10 LEADING:3 TRAILING:3 SLIDINGWINDOW:4:15 AVGQUAL:20 MINLEN:%d";
    private static final int THREAD_NUMBER = 10;
    private static ConfUtil CONF = ConfUtil.getInstance();

    public static String[] genomeIndex(Genome genome) {
        String cmd = String.format("%s index -p %s %s",
                CONF.getSoftwareExecutable(SwType.BWA),
                CONF.getDirectory(OutType.IDX_GENOME) + genome.getCode(),
                CONF.getDirectory(SubType.GENOME) + genome.getFasta());
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcRawReads(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        cmd.add(String.format("%s -o %s -t %d %s",
                CONF.getSoftwareExecutable(SwType.FASTQC),
                CONF.getDirectory(OutType.QC_RAW),
                THREAD_NUMBER,
                CONF.getDirectory(SubType.INPUT) + experiment.getFastq1())
        );
        if (experiment.getFastq2() != null) {
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSoftwareExecutable(SwType.FASTQC),
                    CONF.getDirectory(OutType.QC_RAW),
                    THREAD_NUMBER,
                    CONF.getDirectory(SubType.INPUT) + experiment.getFastq2())
            );
        }
        return FileUtil.listToArray(cmd);
    }

    private static QCInfo getQCInfo(Experiment experiment) {
        File qcFile = new File(CONF.getDirectory(OutType.PARSE_ZIP) + experiment.getCode() + Constant.JSON_SFX);
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
        String fastq1 = CONF.getDirectory(SubType.INPUT) + experiment.getFastq1();
        String outputPrefix = CONF.getDirectory(OutType.TRIM) + experiment.getCode();

        QCInfo qcInfo = getQCInfo(experiment);
        if (experiment.getFastq2() == null) {
            //single end
            cmd = String.format("%s -Xmx256m -jar %s SE -threads %d %s %s %s " + PARAM,
                    CONF.getPlatform(PfType.JAVA),
                    CONF.getSoftwareExecutable(SwType.TRIMMOMATIC),
                    THREAD_NUMBER,
                    qcInfo.getPhred(),
                    fastq1,
                    outputPrefix + "." + StrUtil.getSuffix(experiment.getFastq1()),
                    qcInfo.getFaFilePath(),
                    qcInfo.getLength() / 3);
        } else {
            //pair end
            String fastq2 = CONF.getDirectory(SubType.INPUT) + experiment.getFastq2();
            cmd = String.format("%s -Xmx256m -jar %s PE -threads %d %s %s %s %s %s %s %s " + PARAM,
                    CONF.getPlatform(PfType.JAVA),
                    CONF.getSoftwareExecutable(SwType.TRIMMOMATIC),
                    THREAD_NUMBER,
                    qcInfo.getPhred(),
                    fastq1,
                    fastq2,
                    outputPrefix + "_1." + StrUtil.getSuffix(experiment.getFastq1()),
                    outputPrefix + "_1_unpaired." + StrUtil.getSuffix(experiment.getFastq1()),
                    outputPrefix + "_2." + StrUtil.getSuffix(experiment.getFastq2()),
                    outputPrefix + "_2_unpaired." + StrUtil.getSuffix(experiment.getFastq2()),
                    qcInfo.getFaFilePath(),
                    qcInfo.getLength() / 3
            );
        }
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcCleanReads(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        cmd.add(String.format("%s -o %s -t %d %s",
                CONF.getSoftwareExecutable(SwType.FASTQC),
                CONF.getDirectory(OutType.QC_CLEAN),
                THREAD_NUMBER,
                CONF.getDirectory(OutType.TRIM) + experiment.getCode()) + "_1." + StrUtil.getSuffix(experiment.getFastq1())
        );
        if (experiment.getFastq2() != null) {
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSoftwareExecutable(SwType.FASTQC),
                    CONF.getDirectory(OutType.QC_CLEAN),
                    THREAD_NUMBER,
                    CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_2." + StrUtil.getSuffix(experiment.getFastq2())
            ));
        }
        return FileUtil.listToArray(cmd);
    }

    public static String[] alignment(Experiment experiment) {
        QCInfo qcInfo = getQCInfo(experiment);
        //BWA executable absolute path
        String exe = CONF.getSoftwareExecutable(SwType.BWA);
        //reference genome index prefix with absolute path
        String ref = CONF.getDirectory(OutType.IDX_GENOME) + experiment.getGenomeCode();

        //two FastQ file,fastq2 is null if is SINGLE-END data
        String fastq1, fastq2 = null;
        //if sequence length short than 70bp,using BWA-ALN algorithm will generate SAI temporary index file
        String sai1, sai2 = null;
        if (experiment.getFastq2() == null) {
            fastq1 = CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "." + StrUtil.getSuffix(experiment.getFastq1());
            sai1 = CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + Constant.SAI_SFX;
        } else {
            fastq1 = CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_1." + StrUtil.getSuffix(experiment.getFastq1());
            fastq2 = CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_2." + StrUtil.getSuffix(experiment.getFastq2());
            sai1 = CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + "_1" + Constant.SAI_SFX;
            sai2 = CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + "_2" + Constant.SAI_SFX;
        }
        //alignment result SAM file's absolute path
        String sam = CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + Constant.SAM_SFX;
        int length = qcInfo.getLength();
        //do BWA-ALN algorithm
        if (length <= 70) {
            String[] commands;
            //single end
            if (experiment.getFastq2() == null) {
                commands = new String[2];
                //bwa aln ref.fa short_read.fq > aln_sa.sai
                commands[0] = String.format("%s aln %s %s > %s", exe, ref, fastq1, sai1);
                //bwa samse ref.fa aln_sa.sai short_read.fq > aln-se.sam
                commands[1] = String.format("%s samse %s %s > %s", exe, ref, sai1, sam);
            }
            //pair end
            else {
                commands = new String[3];
                //bwa aln ref.fa short_read.fq > aln_sa.sai
                commands[0] = String.format("%s aln %s %s > %s", exe, ref, fastq1, sai1);
                commands[1] = String.format("%s aln %s %s > %s", exe, ref, fastq2, sai2);
                //bwa sampe ref.fa aln_sa1.sai aln_sa2.sai read1.fq read2.fq > aln-pe.sam
                commands[2] = String.format("%s sampe %s %s %s %s %s > %s", exe, ref, sai1, sai2, fastq1, fastq2, sam);
            }
            return commands;
        }
        //do BWA-MEM algorithm
        else {
            String cmd = experiment.getFastq2() == null ?
                    //single end
                    String.format("%s mem -M %s -t %d %s > %s", exe, ref, THREAD_NUMBER, fastq1, sam) :
                    //pair end
                    String.format("%s mem -M %s -t %d %s %s > %s", exe, ref, THREAD_NUMBER, fastq1, fastq2, sam);
            return FileUtil.wrapString(cmd);
        }

    }

    public static String[] convertSamToBam(Experiment experiment) {
        String cmd = String.format("%s view --threads %d -bS %s -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                THREAD_NUMBER,
                CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + Constant.SAM_SFX,
                CONF.getDirectory(OutType.BAM_CONVERTED) + experiment.getCode() + Constant.SUFFIX_CONVERTED_BAM);
        return FileUtil.wrapString(cmd);
    }

    public static String[] sortBam(Experiment experiment) {
        String cmd = String.format("%s sort %s --threads %d -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                CONF.getDirectory(OutType.BAM_CONVERTED) + experiment.getCode() + Constant.SUFFIX_CONVERTED_BAM,
                THREAD_NUMBER,
                CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + Constant.SUFFIX_SORTED_BAM);
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcBam(Experiment experiment) {
        String sortBamFile = CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + Constant.SUFFIX_SORTED_BAM;
        long sortBamSize = FileUtil.getFileSize(sortBamFile, FileUtil.Unit.MB);
        String cmd = String.format("%s bamqc -bam %s -outdir %s --java-mem-size=%dM",
                CONF.getSoftwareExecutable(SwType.QUALIMAP),
                sortBamFile,
                CONF.getDirectory(OutType.QC_BAM) + experiment.getCode(),
                sortBamSize / 2);
        return FileUtil.wrapString(cmd);
    }

    public static String[] rmdupBam(Experiment experiment) {
        String cmd = String.format("%s rmdup %s %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + Constant.SUFFIX_SORTED_BAM,
                CONF.getDirectory(OutType.BAM_RMDUP) + experiment.getCode() + Constant.SUFFIX_RMDUP_BAM
        );
        return FileUtil.wrapString(cmd);
    }

    public static String[] uniqueBam(Experiment experiment) {
        String cmd = String.format("%s view -b %s -q 30 -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                CONF.getDirectory(OutType.BAM_RMDUP) + experiment.getCode() + Constant.SUFFIX_RMDUP_BAM,
                CONF.getDirectory(OutType.BAM_UNIQUE) + experiment.getCode() + Constant.SUFFIX_UNIQUE_BAM
        );
        return FileUtil.wrapString(cmd);
    }

    public static String[] callPeaks(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        //add python environment variable
        cmd.add(OsCmd.addPythonPath(CONF.getSoftwareFolder(SwType.MACS2)));
        //set genome size
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        long gSize = genome.getSize();
        if (gSize == 0) {
            System.err.println("Genome size not specified! calculate genome FASTA file size instead!");
            gSize = FileUtil.countFileContentSize(CONF.getDirectory(SubType.GENOME) + genome.getFasta());
            genome.setSize(gSize);
            System.err.println("Calculated genome size is:" + gSize);
        }
        //has no control experiment
        //single "macs2 callpeak -t ChIP.bam -c Control.bam -f BAM -g hs -n test -B -q 0.01"
        String callPeakCmd = String.format("%s callpeak -t %s -f BAM -g %d -n %s -B",
                CONF.getSoftwareExecutable(SwType.MACS2),
                CONF.getDirectory(OutType.BAM_UNIQUE) + experiment.getCode() + Constant.SUFFIX_UNIQUE_BAM,
                gSize,
                CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode());
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
        cmd.add(OsCmd.addPath(SwUtil.getPath(SwType.HOMER)));
        cmd.add(String.format("%s %s %s -%s %s > %s",
                CONF.getSoftwareExecutable(SwType.HOMER) + Constant.EXE_HOMER_ANNOTATE_PEAK,
                CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode() + sfxPeakFile,
                CONF.getDirectory(SubType.GENOME) + genome.getFasta(),
                annoFormat,
                CONF.getDirectory(SubType.GENOME) + genome.getAnnotation(),
                CONF.getDirectory(OutType.ANNOTATION) + experiment.getCode() + Constant.SUFFIX_ANNO_BED)
        );
        return FileUtil.listToArray(cmd);
    }

    public static String[] findMotifs(Experiment experiment) {
        String sfxPeakFile = experiment.isBroadPeak() ? Constant.SFX_BROAD_PEAKS : Constant.SFX_NARROW_PEAKS;
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        List<String> cmd = new ArrayList<>();
        //add homer to environment path
        cmd.add(OsCmd.addPath(SwUtil.getPath(SwType.HOMER)));
        //add weblogo to environment path
        cmd.add(OsCmd.addPath(SwUtil.getPath(SwType.WEBLOGO)));
        //Usage: findMotifsGenome.pl <pos file> <genome> <output directory> [additional options]
        cmd.add(String.format("%s %s %s %s -size 200 -len 8",
                CONF.getSoftwareExecutable(SwType.HOMER) + Constant.EXE_HOMER_FIND_MOTIF,
                CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode() + sfxPeakFile,
                CONF.getDirectory(SubType.GENOME) + genome.getFasta(),
                CONF.getDirectory(OutType.MOTIF) + File.separator + experiment.getCode()
        ));
        return FileUtil.listToArray(cmd);
    }

    public static String[] flagStat(Experiment experiment) {
        //for sorted bam, rmdup bam, q>30 bam
        String[] commands = new String[3];
        String exe = CONF.getSoftwareExecutable(SwType.SAMTOOLS);
        //sorted bam file
        String sortedBam = CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + Constant.SUFFIX_SORTED_BAM;
        commands[0] = String.format("%s flagstat %s > %s", exe, sortedBam, sortedBam + Constant.FLAGSTAT_SFX);
        //rmdup bam file
        String rmdupBam = CONF.getDirectory(OutType.BAM_RMDUP) + experiment.getCode() + Constant.SUFFIX_RMDUP_BAM;
        commands[1] = String.format("%s flagstat %s > %s", exe, rmdupBam, rmdupBam + Constant.FLAGSTAT_SFX);
        //q>30 bam file
        String q30Bam = CONF.getDirectory(OutType.BAM_RMDUP) + experiment.getCode() + Constant.SUFFIX_RMDUP_BAM;
        commands[1] = String.format("%s flagstat %s > %s", exe, q30Bam, q30Bam + Constant.FLAGSTAT_SFX);
        return commands;
    }
}
