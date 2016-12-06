package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.input.Experiment;
import cn.j1angvei.castk2.input.Genome;
import cn.j1angvei.castk2.type.OutType;
import cn.j1angvei.castk2.type.PfType;
import cn.j1angvei.castk2.type.SubType;
import cn.j1angvei.castk2.type.SwType;
import cn.j1angvei.castk2.util.ConfUtil;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.SwUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j1angvei on 2016/11/29.
 */
public class SwCmd {
    private static ConfUtil CONF = ConfUtil.getInstance();
    private static final String PARAM = "ILLUMINACLIP:%s:2:30:10 LEADING:3 TRAILING:3 SLIDINGWINDOW:4:15 AVGQUAL:20 HEADCROP:6 MINLEN:%s";

    public static String genomeIndex(Genome genome) {
        return String.format("%s index -p %s %s",
                CONF.getSoftwareExecutable(SwType.BWA),
                CONF.getDirectory(OutType.IDX_GENOME) + genome.getCode(),
                CONF.getDirectory(SubType.INPUT) + genome.getFasta());
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

    public static String trimReads(Experiment experiment) {
        //before trim reads, get phred, minLen and fa file from qc results in zip file
        SwUtil.parseQcZip(experiment);

        String fastq1 = CONF.getDirectory(SubType.INPUT) + experiment.getFastq1();
        String inputPrefix = CONF.getDirectory(OutType.PARSE_ZIP) + experiment.getCode();
        String outputPrefix = CONF.getDirectory(OutType.TRIM) + experiment.getCode();
        String phred = FileUtil.readFile(inputPrefix + ".phred");
        String minLen = FileUtil.readFile(inputPrefix + ".len");
        String faFile = inputPrefix + ".fa";
        if (experiment.getFastq2() == null) {
            //single end
            return String.format("%s -Xmx256m -jar %s SE -threads %d %s %s %s " + PARAM,
                    CONF.getPlatform(PfType.JAVA),
                    CONF.getSoftwareExecutable(SwType.TRIMMOMATIC),
                    SwUtil.THREAD_NUMBER,
                    phred,
                    CONF.getDirectory(SubType.INPUT) + fastq1,
                    outputPrefix + ".fastq",
                    faFile,
                    minLen);
        } else {
            //pair end
            String fastq2 = CONF.getDirectory(SubType.INPUT) + experiment.getFastq2();
            return String.format("%s -Xmx256m -jar %s PE -threads %d %s %s %s %s %s %s %s " + PARAM,
                    CONF.getPlatform(PfType.JAVA),
                    CONF.getSoftwareExecutable(SwType.TRIMMOMATIC),
                    SwUtil.THREAD_NUMBER,
                    phred,
                    CONF.getDirectory(SubType.INPUT) + fastq1,
                    CONF.getDirectory(SubType.INPUT) + fastq2,
                    outputPrefix + "_1.fastq",
                    outputPrefix + "_1_unpaired.fastq",
                    outputPrefix + "_2.fastq",
                    outputPrefix + "_2_unpaired.fastq",
                    faFile,
                    minLen
            );
        }
    }

    public static String[] qcCleanReads(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        cmd.add(String.format("%s -o %s -t %d %s",
                CONF.getSoftwareExecutable(SwType.FASTQC),
                CONF.getDirectory(OutType.TRIM),
                SwUtil.THREAD_NUMBER,
                CONF.getDirectory(OutType.QC_CLEAN) + experiment.getFastq1())
        );
        if (experiment.getFastq2() != null) {
            cmd.add(String.format("%s -o %s -t %d %s",
                    CONF.getSoftwareExecutable(SwType.FASTQC),
                    CONF.getDirectory(OutType.TRIM),
                    SwUtil.THREAD_NUMBER,
                    CONF.getDirectory(OutType.QC_CLEAN) + experiment.getFastq2())
            );
        }
        return FileUtil.listToArray(cmd);
    }

    public static String alignment(Experiment experiment) {
        return experiment.getFastq2() == null ?
                String.format("%s mem %s -t %d %s > %s",
                        CONF.getSoftwareExecutable(SwType.BWA),
                        CONF.getDirectory(OutType.IDX_GENOME) + experiment.getGenomeCode(),
                        SwUtil.THREAD_NUMBER,
                        CONF.getDirectory(OutType.TRIM) + experiment.getCode() + ".fastq",
                        CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + ".sam") :
                String.format("%s mem %s -t %d %s %s > %s",
                        CONF.getSoftwareExecutable(SwType.BWA),
                        CONF.getDirectory(OutType.IDX_GENOME) + experiment.getGenomeCode(),
                        SwUtil.THREAD_NUMBER,
                        CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_1.fastq",
                        CONF.getDirectory(OutType.TRIM) + experiment.getCode() + "_2.fastq",
                        CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + ".sam");
    }

    public static String convertSamToBam(Experiment experiment) {
        return String.format("%s view --threads %d -bS %s -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                SwUtil.THREAD_NUMBER,
                CONF.getDirectory(OutType.ALIGNMENT) + experiment.getCode() + ".sam",
                CONF.getDirectory(OutType.BAM_CONVERTED) + experiment.getCode() + ".bam");
    }

    public static String sortBam(Experiment experiment) {
        return String.format("%s sort %s --threads %d -o %s",
                CONF.getSoftwareExecutable(SwType.SAMTOOLS),
                CONF.getDirectory(OutType.BAM_CONVERTED) + experiment.getCode() + ".bam",
                SwUtil.THREAD_NUMBER,
                CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + ".bam");
    }

    public static String qcBam(Experiment experiment) {
        return String.format("%s bamqc -bam %s -outdir %s",
                CONF.getSoftwareExecutable(SwType.QUALIMAP),
                CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + ".bam",
                CONF.getDirectory(OutType.QC_BAM));
    }

    public static String[] callPeaks(Experiment experiment) {
        List<String> cmd = new ArrayList<>();
        //add python environment variable
        cmd.add(OsCmd.addPythonPath(CONF.getSoftwareFolder(SwType.MACS2)));
        //set genome size
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        long gSize = genome.getSize();
        if (gSize == 0) {
            gSize = FileUtil.countFileSize(CONF.getDirectory(SubType.INPUT) + genome.getFasta());
            genome.setSize(gSize);
        }
        if (experiment.getFastq2() == null) {
            //single "macs2 callpeak -t ChIP.bam -c Control.bam -f BAM -g hs -n test -B -q 0.01"
            cmd.add(String.format("%s callpeak -t %s -f BAM -g %d -n %s -B",
                    CONF.getSoftwareExecutable(SwType.MACS2),
                    CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + ".bam",
                    gSize,
                    CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode())
            );
        } else {
            //pair end
            cmd.add(String.format("%s callpeak -t %s -c %s -f BAM -g %d -n %s -B",
                    CONF.getSoftwareExecutable(SwType.MACS2),
                    CONF.getDirectory(OutType.BAM_SORTED) + experiment.getCode() + ".bam",
                    CONF.getDirectory(OutType.BAM_SORTED) + experiment.getControl() + ".bam",
                    gSize,
                    CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode())
            );
        }
        return FileUtil.listToArray(cmd);
    }

    public static String[] annotatePeaks(Experiment experiment) {
        Genome genome = CONF.getGenome(experiment.getGenomeCode());
        String annoFormat = genome.getGtf().substring(genome.getGtf().lastIndexOf('.') + 1);
        List<String> cmd = new ArrayList<>();
        cmd.add(OsCmd.addPath(CONF.getSoftwareFolder(SwType.HOMER) + "bin"));
        cmd.add(String.format("%s %s %s %s %s > %s",
                CONF.getSoftwareExecutable(SwType.HOMER),
                CONF.getDirectory(OutType.PEAK_CALLING) + experiment.getCode() + "_peaks.bed",
                CONF.getDirectory(SubType.INPUT) + genome.getFasta(),
                annoFormat,
                CONF.getDirectory(SubType.INPUT) + genome.getGtf(),
                CONF.getDirectory(OutType.ANNOTATION))
        );
        return FileUtil.listToArray(cmd);
    }
}
