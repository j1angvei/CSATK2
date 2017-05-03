package cn.j1angvei.castk2.qc;

import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * parse qc result from zip file generated from FastQC,v0.11.5
 * Created by Wayne on 4/8 2017.
 */
public class ParseZip {
    /**
     * FastQC qc detailed report not only displayed in HTML format,
     * but also store in the file "fastqc_data.txt", which located in the zip file
     */
    private static final String QC_CONTENT = "fastqc_data.txt";

    /**
     * flag line in QC_CONTENT text file representing step into or out a module.
     */
    private static final String FLAG_IN_BASIC_STATISTICS = ">>Basic Statistics";
    private static final String FLAG_IN_PER_BASE_SEQUENCE_CONTENT = ">>Per base sequence content";
    private static final String FLAG_IN_OVERREPRESENTED_SEQUENCES = ">>Overrepresented sequences";
    private static final String FLAG_ADAPTER_CONTENT = ">>Adapter Content";
    private static final String FLAG_END_MODULE = ">>END_MODULE";
    /**
     * four boolean value representing 4 modules in QC content text file
     */
    private boolean inBasicStatistics;
    private boolean inPerBaseSequenceContent;
    private boolean inOverrepresentedSequences;
    private boolean inAdapterContent;
    /**
     * if current module flag status is "fail" or "error", we retrieve info from that module and store in QCInfo
     * else we ignore it
     */
    private boolean moduleFail;

    /**
     * read from FastQC report "Per Base Sequence Content".
     * if QC percentage or AT percentage bigger than 10, then they should be removed from the reads
     * which is a param HEADCROP in Trimmomatic
     */
    private QCInfo qcInfo = new QCInfo();

    private ParseZip() {
    }

    public static ParseZip newInstance() {
        return new ParseZip();
    }

    /**
     * @param zipFilePath where qc zip file located
     * @param outDir      where JSON result and FA file will store in
     */
    public void parse(String zipFilePath, String outDir, String expCode) {
        //add File.separator to outdir if absent
        if (!outDir.endsWith(File.separator)) {
            outDir += File.separator;
        }

        //the inner file folder in qc zip file, the same as the short zip file name without ".zip" suffix and absolute path.
        String innerFolder = zipFilePath.substring(
                zipFilePath.lastIndexOf(File.separator) + 1, zipFilePath.lastIndexOf('.'));

        //start read zip file
        try {
            //parse the qc zip file
            ZipFile zipFile = new ZipFile(zipFilePath);
            ZipEntry zipEntry = zipFile.getEntry(innerFolder + "/" + QC_CONTENT);//MUST NOT use File.separator here

            //read the text in zip file line by line
            BufferedReader txtReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
            String line;
            while ((line = txtReader.readLine()) != null) {
                //skip comment line
                if (line.startsWith("#")) {
                    continue;
                }

                //update module
                if (line.startsWith(">>")) {
                    updateModuleFlag(line);
                    continue;
                }

                //get "encoding", "total", "length", "gc" information in Basic Statistics Module
                if (inBasicStatistics) {
                    String encodingHeader = "Encoding\t";
                    String totalReadsHeader = "Total Sequences\t";
                    String lengthHeader = "Sequence length\t";
                    String percentGCHeader = "%GC\t";

                    //get "phred" info
                    if (line.startsWith(encodingHeader)) {
                        String encoding = line.replace(encodingHeader, "");
                        qcInfo.setPhred(encodingToPhred(encoding));
                    }

                    //get "total sequence" number
                    else if (line.startsWith(totalReadsHeader)) {
                        String totalReads = line.replace(totalReadsHeader, "");
                        qcInfo.setTotalReads(Long.parseLong(totalReads));
                    }

                    //get "sequence length" number
                    else if (line.startsWith(lengthHeader)) {
                        String len = line.replace(lengthHeader, "");
                        if (len.contains("-")) {
                            len = len.split("-")[1];
                        }
                        qcInfo.setLength(Integer.parseInt(len));
                    }

                    //get "GC percentage" number
                    else if (line.startsWith(percentGCHeader)) {
                        String gc = line.replace(percentGCHeader, "");
                        qcInfo.setPercentGC(Integer.parseInt(gc));
                    }
                }
                // get "HEADCROP"(aka cut base from start to end in a sequence) information in Per base sequence content Module
                else if (moduleFail && inPerBaseSequenceContent) {
                    String[] percentageLine = line.split("\t");
                    //default value
                    int pos;
                    if (percentageLine[0].contains("-")) {
                        String[] tmp = percentageLine[0].split("-");
                        pos = Integer.parseInt(tmp[0] + tmp[1]) / 2;
                    } else {
                        pos = Integer.parseInt(percentageLine[0]);
                    }
                    //if current position bigger than one  third of reads length, just skip it
                    if (pos >= qcInfo.getLength() / 3) {
                        continue;
                    }
                    double gPercentage = Double.parseDouble(percentageLine[1]);
                    double aPercentage = Double.parseDouble(percentageLine[2]);
                    double tPercentage = Double.parseDouble(percentageLine[3]);
                    double cPercentage = Double.parseDouble(percentageLine[4]);
                    if (Math.abs(aPercentage - tPercentage) >= 10d || Math.abs(gPercentage - cPercentage) >= 10) {
                        qcInfo.setHeadCrop(pos);
                    }
                }

                //get "fa file content" information in Overrepresented Sequence Module
                //#Sequence	Count	Percentage	Possible Source
                //GTACATGGAAGCAGTGGTATCAACGCAGAGTACATGGAAGCAGTGGTATC	163858	1.4454892351816577	No Hit
                else if (moduleFail && inOverrepresentedSequences) {
                    String[] item = line.split("\t");
                    //skip no hit sequence, because some overrepresented sequences are needed in RNA-Seq
                    if (item[3].equals("No Hit")) {
                        continue;
                    }
                    qcInfo.getOverrepresentedSeq().put(item[0], item[3]);
                }
                // get "adapter" information in Adapter Content Module
                else if (moduleFail && inAdapterContent) {
                    String[] scores = line.split("\t");
                    //first column is base position in sequence, should ignored
                    for (int i = 1; i < scores.length; i++) {//as v0.11.5 has 5 adapter in it, but v0.11.3 has only 4 adapters
                        double score = 0d;
                        try {
                            score = Double.parseDouble(scores[i]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            System.err.printf("Adapter Content: %s\tBasic Statistics: %s\t Overrepresented Sequences: %s\t Per Base Sequence Content: %s\n",
                                    inAdapterContent, inBasicStatistics, inOverrepresentedSequences, inPerBaseSequenceContent);
                            System.err.println("zip file:\t" + zipFilePath);
                            System.err.println("line:\t" + line);
                        }
                        if (score > 0.05d) {
                            qcInfo.getAdapter().add(Adapter.values()[i - 1]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: QC ZIP File " + zipFilePath + " Not Found!!!");
            e.printStackTrace();
        }

        //get all overrepresented sequence and adapters, store it as .fa file
        String faFilePath = outDir + expCode + Constant.FA_SFX;
        StringBuilder faContent = new StringBuilder();
        //adapters
        for (Adapter adapter : qcInfo.getAdapter()) {
            faContent.append(">").append(adapter.getName()).append("\n")
                    .append(adapter.getSequence()).append("\n");

        }
        //overrepresented sequences
        for (Map.Entry<String, String> entry : qcInfo.getOverrepresentedSeq().entrySet()) {
            //forward sequence
            String seq = entry.getKey();
            String name = entry.getValue();
            faContent.append(">").append(name).append("\n")
                    .append(seq).append("\n");
            //reverse sequence
            String revSeq = "";
            String revName = name + "_rv";
            faContent.append(">").append(revName).append("\n")
                    .append(revSeq).append("\n");
        }
        try {
            FileUtils.writeStringToFile(new File(faFilePath), faContent.toString(), Charset.defaultCharset(), false);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: " + faFilePath + " CAN NOT be written");
        }
        //add information in QCInfo
        FileUtil.createFileIfNotExist(faFilePath);
        qcInfo.setFaFilePath(faFilePath);

        //convert QCInfo Object to String and store it as .json file
        String qcContent = GsonUtil.toJson(qcInfo);
        String qcFilePath = outDir + expCode + Constant.JSON_SFX;
        try {
            FileUtils.writeStringToFile(new File(qcFilePath), qcContent, Charset.defaultCharset(), false);
        } catch (IOException e) {
            System.err.println("ERROR: " + qcFilePath + " CAN NOT be written");
            e.printStackTrace();
        }
    }

    /**
     * according to the flag to update boolean value which representing what module are currently in
     *
     * @param flagLine in qc text file, start with ">>"
     */
    private void updateModuleFlag(String flagLine) {
        String[] info = flagLine.split("\t");
        moduleFail = info.length == 2 && !info[1].equals("pass");
        String flag = info[0];
        switch (flag) {
            case FLAG_IN_BASIC_STATISTICS:
                inBasicStatistics = true;
                break;
            case FLAG_IN_PER_BASE_SEQUENCE_CONTENT:
                inPerBaseSequenceContent = true;
                break;
            case FLAG_IN_OVERREPRESENTED_SEQUENCES:
                inOverrepresentedSequences = true;
                break;
            case FLAG_ADAPTER_CONTENT:
                inAdapterContent = true;
                break;
            case FLAG_END_MODULE:
                inBasicStatistics = false;
                inPerBaseSequenceContent = false;
                inOverrepresentedSequences = false;
                inAdapterContent = false;
                break;
            default:
                break;
        }
    }

    /**
     * if encoding contains "Sanger", phred should be -phred33
     * if encoding contains "Illumina", and Illumina version equal or bigger than 1.8, phred should be -phred33
     * else phred should be -phred64
     *
     * @param encoding retrieved from FastQC report such as Sanger /Illumina 1.8
     * @return phred score, phred33 or phred64
     */
    private String encodingToPhred(String encoding) {
        String phred = "-phred64";
        if (encoding.contains("Sanger")) {
            phred = "-phred33";
        } else if (encoding.contains("Illumina")) {
            String[] segment = encoding.trim().split("[ \t]");
            float value = Float.parseFloat(segment[segment.length - 1]);
            if (value >= 1.8f) {
                phred = "-phred33";
            }
        }
        return phred;
    }

}
