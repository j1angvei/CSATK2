package cn.j1angvei.castk2.util;

import cn.j1angvei.castk2.input.Experiment;
import cn.j1angvei.castk2.type.OutType;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by j1angvei on 2016/11/29.
 */
public class SwUtil {
    public static final int THREAD_NUMBER = 10;
    private static ConfUtil CONF = ConfUtil.getInstance();

    public static String getPythonVersion() {
        return "2.7";
    }



    public static void parseQcZip(Experiment experiment) {
        String fastqFileNamePrefix = experiment.getFastq1().substring(0, experiment.getFastq1().lastIndexOf('.'));
        String outDir = CONF.getOutDirectory(OutType.PARSE_ZIP);
        String phred = "-phred64";
        try {
            //open zip file
            ZipFile zip = new ZipFile(CONF.getOutDirectory(OutType.QC_RAW) + fastqFileNamePrefix + "_fastqc.zip");
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

}
