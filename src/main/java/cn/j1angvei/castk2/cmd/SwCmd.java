package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.input.Experiment;
import cn.j1angvei.castk2.input.Genome;
import cn.j1angvei.castk2.type.OutType;
import cn.j1angvei.castk2.type.PfType;
import cn.j1angvei.castk2.type.SubType;
import cn.j1angvei.castk2.type.SwType;
import cn.j1angvei.castk2.util.ConfUtil;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.StrUtil;
import cn.j1angvei.castk2.util.SwUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * generate all commands for corresponding function
 * Created by j1angvei on 2016/11/29.
 */
public class SwCmd {
    private static final String PARAM = "ILLUMINACLIP:%s:2:30:10 LEADING:3 TRAILING:3 SLIDINGWINDOW:4:15 AVGQUAL:20 MINLEN:%d";
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
                SwUtil.THREAD_NUMBER,
                CONF.getDirectory(SubType.INPUT) + experiment.getFastq1())
        );
        if (experiment.getFastq2() != null) {
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSoftwareExecutable(SwType.FASTQC),
                    CONF.getDirectory(OutType.QC_RAW),
                    SwUtil.THREAD_NUMBER,
                    CONF.getDirectory(SubType.INPUT) + experiment.getFastq2())
            );
        }
        return FileUtil.listToArray(cmd);
    }

    public static String[] trimReads(Experiment experiment) {
        //before trim reads, get phred, minLen and fa file from qc results in zip file
        SwUtil.parseQcZip(experiment);
        String cmd;
        String fastq1 = CONF.getDirectory(SubType.INPUT) + experiment.getFastq1();
        String inputPrefix = CONF.getDirectory(OutType.PARSE_ZIP) + experiment.getCode();
        String outputRawReadsPrefix = CONF.getDirectory(OutType.TRIM) + experiment.getCode();
        String phred = FileUtil.readFile(inputPrefix + Constant.SUFFIX_PHRED);
        String minLen = FileUtil.readFile(inputPrefix + Constant.SUFFIX_LEN);
        String faFile = inputPrefix + Constant.SUFFIX_FA;
        int len = Integer.parseInt(minLen) / 3;
        if (experiment.getFastq2() == null) {
            //single end
            cmd = String.format("%s -Xmx256m -jar %s SE -threads %d %s %s %s " + PARAM,
                    CONF.getPlatform(PfType.JAVA),
                    CONF.getSoftwareExecutable(SwType.TRIMMOMATIC),
                    SwUtil.THREAD_NUMBER,
                    phred,
                    fastq1,
                    outputRawReadsPrefix + "." + StrUtil.getSuffix(experiment.getFastq1()),
                    faFile,
                    len);
        } else {
            //pair end
            String fastq2 = CONF.getDirectory(SubType.INPUT) + experiment.getFastq2();
            cmd = String.format("%s -Xmx256m -jar %s PE -threads %d %s %s %s %s %s %s %s " + PARAM,
                    CONF.getPlatform(PfType.JAVA),
                    CONF.getSoftwareExecutable(SwType.TRIMMOMATIC),
                    SwUtil.THREAD_NUMBER,
                    phred,
                    fastq1,
                    fastq2,
                    outputRawReadsPrefix + "_1." + StrUtil.getSuffix(experiment.getFastq1()),
                    outputRawReadsPrefix + "_1_unpaired." + StrUtil.getSuffix(experiment.getFastq1()),
                    outputRawReadsPrefix + "_2." + StrUtil.getSuffix(experiment.getFastq2()),
                    outputRawReadsPrefix + "_2_unpaired." + StrUtil.getSuffix(experiment.getFastq2()),
                    faFile,
                    len
            );
        }
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcCleanReads(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        cmd.add(String.format("%s -o %s -t %d %s",
                CONF.getSoftwareExecutable(SwType.FASTQC),
                CONF.getDirectory(OutType.QC_CLEAN),
                SwUtil.THREAD_NUMBER,
                CONF.getDirectory(OutType.TRIM) + experiment.getCode()) + "_1." + StrUtil.getSuffix(experiment.getFastq1())
        );
        if (experiment.getFastq2() != null) {
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSoftwareExecutable(SwType.FASTQC),
                    CONF.getDirectory(OutType.QC_CLEAN),
                    SwUtil.THREAD_NUMBER,
                    CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_2." + StrUtil.getSuffix(experiment.getFastq2())
            ));
        }
        return FileUtil.listToArray(cmd);
    }

    public static String[] alignment(Experiment experiment) {
        String cmd = experiment.getFastq2() == null ?
                String.format("%s mem -M %s -t %d %s > %s",
                        CONF.getSoftwareExecutable(SwType.BWA),
                        CONF.getDirectory(OutType.IDX_GENOME) + experiment.getGenomeCode(),
                        SwUtil.THREAD_NUMBER,
                        CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "." + StrUtil.getSuffix(experiment.getFastq1()),
                        CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + Constant.SUFFIX_ALIGNMENT_SAM) :
                String.format("%s mem %s -t %d %s %s > %s",
                        CONF.getSoftwareExecutable(SwType.BWA),
                        CONF.getDirectory(OutType.IDX_GENOME) + experiment.getGenomeCode(),
                        SwUtil.THREAD_NUMBER,
                        CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_1." + StrUtil.getSuffix(experiment.getFastq1()),
                        CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_2." + StrUtil.getSuffix(experiment.getFastq2()),
                        CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + Constant.SUFFIX_ALIGNMENT_SAM);
        return FileUtil.wrapString(cmd);
    }

    public static String[] convertSamToBam(Experiment experiment) {
        String cmd = String.format("%s view --threads %d -bS %s -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                SwUtil.THREAD_NUMBER,
                CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + Constant.SUFFIX_ALIGNMENT_SAM,
                CONF.getDirectory(OutType.BAM_CONVERTED) + experiment.getCode() + Constant.SUFFIX_CONVERTED_BAM);
        return FileUtil.wrapString(cmd);
    }

    public static String[] sortBam(Experiment experiment) {
        String cmd = String.format("%s sort %s --threads %d -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                CONF.getDirectory(OutType.BAM_CONVERTED) + experiment.getCode() + Constant.SUFFIX_CONVERTED_BAM,
                SwUtil.THREAD_NUMBER,
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
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        String annoFileName = genome.getAnnotation();
        String annoFormat = StrUtil.getSuffix(annoFileName);
        List<String> cmd = new ArrayList<>();
        cmd.add(OsCmd.addPath(SwUtil.getPath(SwType.HOMER)));
        cmd.add(String.format("%s %s %s -%s %s > %s",
                CONF.getSoftwareExecutable(SwType.HOMER) + Constant.EXE_HOMER_ANNOTATE_PEAK,
                CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode() + Constant.SUFFIX_PEAKS,
                CONF.getDirectory(SubType.GENOME) + genome.getFasta(),
                annoFormat,
                CONF.getDirectory(SubType.GENOME) + genome.getAnnotation(),
                CONF.getDirectory(OutType.ANNOTATION) + experiment.getCode() + Constant.SUFFIX_ANNO_BED)
        );
        return FileUtil.listToArray(cmd);
    }

    public static String[] findMotifs(Experiment experiment) {
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        List<String> cmd = new ArrayList<>();
        //add homer to environment path
        cmd.add(OsCmd.addPath(SwUtil.getPath(SwType.HOMER)));
        //add weblogo to environment path
        cmd.add(OsCmd.addPath(SwUtil.getPath(SwType.WEBLOGO)));
        //Usage: findMotifsGenome.pl <pos file> <genome> <output directory> [additional options]
        cmd.add(String.format("%s %s %s %s -size 200 -len 8",
                CONF.getSoftwareExecutable(SwType.HOMER) + Constant.EXE_HOMER_FIND_MOTIF,
                CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode() + Constant.SUFFIX_PEAKS,
                CONF.getDirectory(SubType.GENOME) + genome.getFasta(),
                CONF.getDirectory(OutType.MOTIF) + File.separator + experiment.getCode()
        ));
        return FileUtil.listToArray(cmd);
    }

    public static String[] flagStat(Experiment experiment) {
        return null;
    }

    public static String[] goAndPathway(Experiment experiment) {
        return new String[]{""};
    }
}
