package cn.j1angvei.castk2.qc;

import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;
import cn.j1angvei.castk2.util.StrUtil;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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

    public static ParseZip getInstance() {
        return INSTANCE;
    }

    private static final ParseZip INSTANCE = new ParseZip();

    private ParseZip() {
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
        System.out.println(innerFolder);

        //Object to store all retrieved info
        QCInfo qcInfo = new QCInfo();

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
                    String[] item = line.split("\t");
                    String flag = item[0];
                    moduleFail = item.length == 2 && item[1].equals("fail");
                    updateModuleFlag(flag);
                    continue;
                }

                //get "encoding", "total", "length", "gc" information in Basic Statistics Module
                if (inBasicStatistics) {
                    String encodingHeader = "Encoding\t";
                    String totalReadsHeader = "Total Sequences\t";
                    String lengthHeader = "Sequence length\t";
                    String percentGCHeader = "%GC\t";

                    //get "encoding" info
                    if (line.startsWith(encodingHeader)) {
                        String encode = line.replace(encodingHeader, "");
                        qcInfo.setEncoding(encode);
                    }

                    //get "total sequence" number
                    else if (line.startsWith(totalReadsHeader)) {
                        String totalReads = line.replace(totalReadsHeader, "");
                        qcInfo.setTotalReads(Long.parseLong(totalReads));
                    }

                    //get "sequence length" number
                    else if (line.startsWith(lengthHeader)) {
                        String length = line.replace(lengthHeader, "");
                        if (length.contains("-")) {
                            length = length.split("-")[1];
                        }
                        qcInfo.setLength(Integer.parseInt(length));
                    }

                    //get "GC percentage" number
                    else if (line.startsWith(percentGCHeader)) {
                        String gc = line.replace(percentGCHeader, "");
                        qcInfo.setPercentGC(Integer.parseInt(gc));
                    }
                }
                // get "HEADCROP"(aka cut base from start to end in a sequence) information in Per base sequence content Module
                else if (moduleFail && inPerBaseSequenceContent) {
                    //TO BE MODIFIED LATER
                    qcInfo.setHeadCrop(0);
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
                    Pair<String, String> pair = new Pair<>(item[3], item[0]);
                    qcInfo.getOverrepresentedSeq().add(pair);
                }
                // get "adapter" information in Adapter Content Module
                else if (moduleFail && inAdapterContent) {
                    String[] scores = line.split("\t");

                    //first column is base position in sequence, should ignored
                    for (int i = 1; i < scores.length; i++) {//as v0.11.5 has 5 adapter in it, but v0.11.3 has only 4 adapters
                        double score = Double.parseDouble(scores[i]);
                        if (score > 0.05d) {
                            qcInfo.getAdapter().add(Adapter.values()[i - 1]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: QC ZIP File " + zipFilePath + "Not Found!!!");
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
        for (Pair<String, String> pair : qcInfo.getOverrepresentedSeq()) {
            faContent.append(">").append(pair.getKey()).append("\n")
                    .append(pair.getValue()).append("\n");
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
        qcInfo.setPhred(StrUtil.encodingToPhred(qcInfo.getEncoding()));

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
     * @param flag in qc text file, start with ">>"
     */
    private void updateModuleFlag(String flag) {
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
     * four boolean value representing 4 modules in QC content text file
     */
    private boolean inBasicStatistics = false;
    private boolean inPerBaseSequenceContent = false;
    private boolean inOverrepresentedSequences = false;
    private boolean inAdapterContent = false;
    /**
     * if current module flag status is "fail", we retrieve info from that module and store in QCInfo
     * else we ignore it
     */
    private boolean moduleFail = false;
}
