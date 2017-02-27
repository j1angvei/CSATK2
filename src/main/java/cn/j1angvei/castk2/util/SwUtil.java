package cn.j1angvei.castk2.util;

import cn.j1angvei.castk2.input.Experiment;
import cn.j1angvei.castk2.type.OutType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by j1angvei on 2016/11/29.
 */
public class SwUtil {
    public static final int THREAD_NUMBER = 10;
    public static final String INPUT_JSON = "input.json";
    public static final String CONFIG_JSON = "config.json";
    public static final String ADAPTERS_TXT = "adapters.txt";

    private static ConfUtil CONF = ConfUtil.getInstance();

    public static String getPythonVersion() {
        return "2.7";
    }

    public static void parseQcZip(Experiment experiment) {
        String fastqFileNamePrefix = StrUtil.getPrefix(experiment.getFastq1());
        String outDir = CONF.getDirectory(OutType.PARSE_ZIP);
        String phred = "-phred64";
        try {
            //open zip file
            ZipFile zip = new ZipFile(CONF.getDirectory(OutType.QC_RAW) + fastqFileNamePrefix + "_fastqc.zip");
            ZipEntry entry = zip.getEntry(fastqFileNamePrefix + "_fastqc/fastqc_data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
            boolean inOverrepresentedBlock = false;
            String line;
            String faFileName = outDir + experiment.getCode() + ".fa";
            //overwrite last generated fa file
            FileUtil.overwriteFile("", faFileName);
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
                    FileUtil.overwriteFile(phred, outDir + experiment.getCode() + ".phred");
                }
                //read reads length
                if (line.startsWith("Sequence length")) {
                    String len = line.split("\t")[1];
                    if (len.contains("-")) {
                        len = len.split("-")[0];
                    }
                    len = String.valueOf(Integer.parseInt(len) / 3);
                    FileUtil.overwriteFile(len, outDir + experiment.getCode() + ".len");
                }
                //in overrepresented block
                if (line.startsWith(">>Overrepresented sequences")) {
                    inOverrepresentedBlock = true;
                    continue;
                }
                //exit block
                if (inOverrepresentedBlock && line.startsWith(">>END_MODULE")) {
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
                    FileUtil.appendFile(String.format(">%s\n%s\n", name, forward), faFileName);
                }
            }
            //if overrepresented block is empty,so that fa file never created, then create a empty fa file
            FileUtil.createFileIfNotExist(faFileName);
            //append adapters to fa file
            FileUtil.appendFile(FileUtil.readAdapter(), faFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
