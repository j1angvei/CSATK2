package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.input.Experiment;
import cn.j1angvei.castk2.input.Genome;
import cn.j1angvei.castk2.type.OutType;
import cn.j1angvei.castk2.type.PfType;
import cn.j1angvei.castk2.type.SubType;
import cn.j1angvei.castk2.type.SwType;
import cn.j1angvei.castk2.util.ConfUtil;
import cn.j1angvei.castk2.util.NumUtil;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by j1angvei on 2016/11/29.
 */
public class SwCommand {
    private static ConfUtil CONF = ConfUtil.getInstance();
    private static final String PARAM = "ILLUMINACLIP:%s:2:30:10 LEADING:3 TRAILING:3 SLIDINGWINDOW:4:15 AVGQUAL:20 HEADCROP:6 MINLEN:%s";

    public static String genomeIndex(Genome genome) {
        return String.format("%s index -p %s %s",
                CONF.getSwExe(SwType.BWA),
                CONF.getOutDir(OutType.IDX_GENOME) + genome.getCode(),
                CONF.getSubDir(SubType.INPUT) + genome.getFasta());
    }

    public static String qcRawReads(String rawRead) {
        return String.format("%s -o %s -t %d %s",
                CONF.getSwExe(SwType.FASTQC),
                CONF.getOutDir(OutType.QC_RAW),
                NumUtil.THREAD_NUMBER,
                CONF.getSubDir(SubType.INPUT) + rawRead);
    }

    public static void parseQcZip(Experiment experiment) {
        String fastqFileNamePrefix = experiment.getFastq1().substring(0, experiment.getFastq1().lastIndexOf('.'));
        String outDir = CONF.getOutDir(OutType.PARSE_ZIP);
        String phred = "-phred64";
        try {
            //open zip file
            ZipFile zip = new ZipFile(CONF.getOutDir(OutType.QC_RAW) + fastqFileNamePrefix + "_fastqc.zip");
            ZipEntry entry = zip.getEntry(fastqFileNamePrefix + "_fastqc/fastqc_data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
            boolean inOverrepresentedBlock = false;
            String line;
            while ((line = reader.readLine()) != null) {

                //skip comment line
                if (line.startsWith("#")) {
                    continue;
                }

                //read encoding info
                if (line.startsWith("Encoding")) {
                    if (line.contains("Sanger")) {
                        phred = "-phred33";
                    } else if (line.contains("Illumina")) {
                        String[] segment = line.split("[ \t/]");
                        float value = Float.parseFloat(segment[segment.length - 1]);
                        if (value >= 1.8f) {
                            phred = "-phred33";
                        }
                    }
                    FileUtils.writeStringToFile(new File(outDir + experiment.getCode() + ".phred"), phred, Charset.defaultCharset());
                }

                //read reads length
                if (line.startsWith("Sequence length")) {
                    String len = line.split("\t")[1];
                    if (len.contains("-")) {
                        len = len.split("-")[0];
                    }
                    len = String.valueOf(Integer.parseInt(len) / 3);
                    FileUtils.writeStringToFile(new File(outDir + experiment.getCode() + ".len"), len, Charset.defaultCharset());
                }


                //in overrepresented block
                if (line.startsWith(">>Overrepresented sequences")) {
                    inOverrepresentedBlock = true;
                    continue;
                }
                //exit block
                if (line.startsWith(">>END_MODULE")) {
                    inOverrepresentedBlock = false;
                    continue;
                }

                if (inOverrepresentedBlock) {
                    if (line.startsWith(">>") || line.startsWith("#"))
                        continue;
                    String[] seg = line.split("\t");
                    String forward = seg[0];
                    String name = seg[3];
                    if (forward.contains("N") || forward.contains("n")) {
                        continue;
                    }

                    FileUtils.writeStringToFile(new File(outDir + experiment.getCode() + ".fa"), ">" + name + "\n" + forward, Charset.defaultCharset());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String trimReads(Experiment experiment) {
        String fastq1 = CONF.getSubDir(SubType.INPUT) + experiment.getFastq1();
        String inputPrefix = CONF.getOutDir(OutType.PARSE_ZIP) + experiment.getCode();
        String outputPrefix = CONF.getOutDir(OutType.TRIM) + experiment.getCode();
        String phred = null;
        String minLen = null;
        try {
            phred = FileUtils.readFileToString(new File(inputPrefix + ".phred"), Charset.defaultCharset());
            minLen = FileUtils.readFileToString(new File(inputPrefix + ".len"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String faFile = inputPrefix + ".fa";
        if (experiment.getFastq2() == null) {//single end
            return String.format("%s -Xmx256m -jar %s SE -threads %d %s %s %s " + PARAM,
                    CONF.getPlatform(PfType.JAVA),
                    CONF.getSwExe(SwType.TRIMMOMATIC),
                    NumUtil.THREAD_NUMBER,
                    phred,
                    CONF.getSubDir(SubType.INPUT) + fastq1,
                    outputPrefix + ".fastq",
                    faFile,
                    minLen);
        } else {//pair end
            String fastq2 = CONF.getSubDir(SubType.INPUT) + experiment.getFastq2();
            return String.format("%s -Xmx256m -jar %s PE -threads %d %s %s %s %s %s %s %s " + PARAM,
                    CONF.getPlatform(PfType.JAVA),
                    CONF.getSwExe(SwType.TRIMMOMATIC),
                    NumUtil.THREAD_NUMBER,
                    phred,
                    CONF.getSubDir(SubType.INPUT) + fastq1,
                    CONF.getSubDir(SubType.INPUT) + fastq2,
                    outputPrefix + "_1.fastq",
                    outputPrefix + "_1_unpaired.fastq",
                    outputPrefix + "_2.fastq",
                    outputPrefix + "_2_unpaired.fastq",
                    faFile,
                    minLen
            );
        }

    }

    public static String qcCleanReads(String rawRead) {
        return String.format("%s -o %s -t %d %s", CONF.getSwExe(SwType.FASTQC), CONF.getOutDir(OutType.QC_CLEAN), NumUtil.THREAD_NUMBER, CONF.getOutDir(OutType.QC_CLEAN) + rawRead);
    }
}
