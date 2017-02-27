package cn.j1angvei.castk2.cmd;

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
import com.sun.javafx.applet.ExperimentalExtensions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j1angvei on 2016/11/29.
 */
public class SwCmd {
    private static final String PARAM = "ILLUMINACLIP:%s:2:30:10 LEADING:3 TRAILING:3 SLIDINGWINDOW:4:15 AVGQUAL:20 HEADCROP:6 MINLEN:%s";
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
        String phred = FileUtil.readFile(inputPrefix + ".phred");
        String minLen = FileUtil.readFile(inputPrefix + ".len");
        String faFile = inputPrefix + ".fa";
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
                    minLen);
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
                    minLen
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
                String.format("%s mem %s -t %d %s > %s",
                        CONF.getSoftwareExecutable(SwType.BWA),
                        CONF.getDirectory(OutType.IDX_GENOME) + experiment.getGenomeCode(),
                        SwUtil.THREAD_NUMBER,
                        CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "." + StrUtil.getSuffix(experiment.getFastq1()),
                        CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + ".sam") :
                String.format("%s mem %s -t %d %s %s > %s",
                        CONF.getSoftwareExecutable(SwType.BWA),
                        CONF.getDirectory(OutType.IDX_GENOME) + experiment.getGenomeCode(),
                        SwUtil.THREAD_NUMBER,
                        CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_1." + StrUtil.getSuffix(experiment.getFastq1()),
                        CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_2." + StrUtil.getSuffix(experiment.getFastq2()),
                        CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + ".sam");
        return FileUtil.wrapString(cmd);
    }

    public static String[] convertSamToBam(Experiment experiment) {
        String cmd = String.format("%s view --threads %d -bS %s -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                SwUtil.THREAD_NUMBER,
                CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + ".sam",
                CONF.getDirectory(OutType.BAM_CONVERTED) + experiment.getCode() + ".sam.bam");
        return FileUtil.wrapString(cmd);
    }

    public static String[] sortBam(Experiment experiment) {
        String cmd = String.format("%s sort %s --threads %d -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                CONF.getDirectory(OutType.BAM_CONVERTED) + experiment.getCode() + ".sam.bam",
                SwUtil.THREAD_NUMBER,
                CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + ".sorted.bam");
        return FileUtil.wrapString(cmd);
    }

    public static String[] qcBam(Experiment experiment) {
        String cmd = String.format("%s bamqc -bam %s -outdir %s -outformat PDF -outfile %s.pdf",
                CONF.getSoftwareExecutable(SwType.QUALIMAP),
                CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + ".sorted.bam",
                CONF.getDirectory(OutType.QC_BAM),
                experiment.getCode());
        return FileUtil.wrapString(cmd);
    }

    public static String[] rmdupBam(Experiment experiment) {
        String cmd = String.format("%s rmdup %s %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + ".sorted.bam",
                CONF.getDirectory(OutType.BAM_RMDUP) + experiment.getCode() + ".rmdup.bam"
        );
        return FileUtil.wrapString(cmd);
    }

    public static String[] uniqueBam(Experiment experiment) {
        String cmd = String.format("%s view -b %s -q 30 -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                CONF.getDirectory(OutType.BAM_RMDUP) + experiment.getCode() + ".rmdup.bam",
                CONF.getDirectory(OutType.BAM_UNIQUE) + experiment.getCode() + ".unique.bam"
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
            gSize = FileUtil.countFileSize(CONF.getDirectory(SubType.GENOME) + genome.getFasta());
            genome.setSize(gSize);
        }
        //has no control experiment
        if (experiment.getControl() == null) {
            //single "macs2 callpeak -t ChIP.bam -c Control.bam -f BAM -g hs -n test -B -q 0.01"
            cmd.add(String.format("%s callpeak -t %s -f BAM -g %d -n %s -B",
                    CONF.getSoftwareExecutable(SwType.MACS2),
                    CONF.getDirectory(OutType.BAM_UNIQUE) + experiment.getCode() + ".unique.bam",
                    gSize,
                    CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode())
            );
        } else {
            //has control experiment
            cmd.add(String.format("%s callpeak -t %s -c %s -f BAM -g %d -n %s -B",
                    CONF.getSoftwareExecutable(SwType.MACS2),
                    CONF.getDirectory(OutType.BAM_UNIQUE) + experiment.getCode() + ".unique.bam",
                    CONF.getDirectory(OutType.BAM_UNIQUE) + experiment.getControl() + ".unique.bam",
                    gSize,
                    CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode())
            );
        }
        return FileUtil.listToArray(cmd);
    }

    public static String[] annotatePeaks(Experiment experiment) {
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        String annoFileName = genome.getAnnotation();
        String annoFormat = annoFileName.substring(annoFileName.lastIndexOf('.') + 1);
        List<String> cmd = new ArrayList<>();
        cmd.add(OsCmd.addPath(CONF.getSoftwareFolder(SwType.HOMER) + "bin"));
        cmd.add(String.format("%s %s %s -%s %s > %s",
                CONF.getSoftwareExecutable(SwType.HOMER),
                CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode() + "_peaks.narrowPeak",
                CONF.getDirectory(SubType.GENOME) + genome.getFasta(),
                annoFormat,
                CONF.getDirectory(SubType.GENOME) + genome.getAnnotation(),
                CONF.getDirectory(OutType.ANNOTATION) + experiment.getCode() + "_annotation.bed")
        );
        return FileUtil.listToArray(cmd);
    }
}
