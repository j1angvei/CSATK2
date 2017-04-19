package cn.j1angvei.castk2.util;

import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.ConfigInitializer;
import cn.j1angvei.castk2.conf.Resource;
import cn.j1angvei.castk2.conf.Software;

import java.util.List;

/**
 * Created by j1angvei on 2016/11/29.
 */
public class SwUtil {

    public static String getPythonVersion() {
        return "2.7";
    }

//    public static void parseQcZip(Experiment experiment) {
//        String phred = "-phred64";
//        try {
//            //open zip file
//            ZipEntry entry = zip.getEntry(fastqFileNamePrefix + "_fastqc/fastqc_data.txt");
//            BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
//            boolean inOverrepresentedBlock = false;
//            String line;
//            String faFileName = outDir + experiment.getCode() + ".fa";
//            //overwrite last generated fa file
//            FileUtil.overwriteFile("", faFileName);
//            while ((line = reader.readLine()) != null) {
//                //skip comment line
//                if (line.startsWith("#")) {
//                    continue;
//                }
//                //read encoding info
////                if (line.startsWith("Encoding")) {
////                    if (line.contains("Sanger")) {
////                        phred = "-phred33";
////                    } else if (line.contains("Illumina")) {
////                        String[] segment = line.split("[ \t/]");
////                        float value = Float.parseFloat(segment[segment.length - 1]);
////                        if (value >= 1.8f) {
////                            phred = "-phred33";
////                        }
////                    }
////                    FileUtil.overwriteFile(phred, outDir + experiment.getCode() + ".phred");
////                }
//                //read reads length
//                if (line.startsWith("Sequence length")) {
//                    String len = line.split("\t")[1];
//                    if (len.contains("-")) {
//                        len = len.split("-")[1];
//                    }
//                    len = String.valueOf(Integer.parseInt(len));
//                    FileUtil.overwriteFile(len, outDir + experiment.getCode() + ".len");
//                }
//                //in overrepresented block
//                if (line.startsWith(">>Overrepresented sequences")) {
//                    inOverrepresentedBlock = true;
//                    continue;
//                }
//                //exit block
//                if (inOverrepresentedBlock && line.startsWith(">>END_MODULE")) {
//                    inOverrepresentedBlock = false;
//                    continue;
//                }
//                if (inOverrepresentedBlock) {
//                    if (line.startsWith(">>") || line.startsWith("#"))
//                        continue;
//                    String[] seg = line.split("\t");
//                    String forward = seg[0];
//                    String name = seg[3].trim();
//                    //ignore No Hit overrepresented reads
//                    if (name.equals("No Hit")) {
//                        continue;
//                    }
//                    FileUtil.appendFile(String.format(">%s\n%s\n", name, forward), faFileName);
//                }
//            }
//            //if overrepresented block is empty,so that fa file never created, then newInstance a empty fa file
//            FileUtil.createFileIfNotExist(faFileName);
//            //append adapters to fa file
//            FileUtil.appendFile(FileUtil.readFromConfigFolder(Resource.ADAPTER), faFileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void extractGeneList(String annotatedBedFile, String geneListFile) {
        List<String> annotatedPeaks = FileUtil.readLineIntoList(annotatedBedFile);
        FileUtil.overwriteFile("", geneListFile);
        for (String line : annotatedPeaks) {
            //skip the first line
            if (line.contains(Constant.EXE_HOMER_ANNOTATE_PEAK)) continue;
            if (line.contains(")")) {
                String column8 = line.split("\t")[7];
                int endIndex = column8.lastIndexOf(')');
                if (column8.contains(",")) {
                    endIndex = column8.indexOf(',');
                }
                int startIndex = column8.indexOf('(') + 1;
                String geneId = column8.substring(startIndex, endIndex);
                FileUtil.appendFile(geneId + "\n", geneListFile);
            }
        }
    }

    public static void extractAnnotationFeature() {
    }

    public static String genomeCodeToSpecies(int genomeCode) {
        String speciesProp = FileUtil.readFromResourceFolder(Resource.SPECIES);
        for (String line : speciesProp.split("\n")) {
            String[] current = line.split("=");
            int code = Integer.parseInt(current[0]);
            if (code == genomeCode) {
                return current[1].trim();
            }
        }
        System.err.println("Check your genomeCode in conf.json");
        return null;
    }

    public static String getPath(Software sw) {
        String folder = ConfigInitializer.getInstance().getSwDestFolder(sw);
        switch (sw) {
            case FASTQC:
            case TRIMMOMATIC:
            case BWA:
            case SAMTOOLS:
            case MACS2:
            case QUALIMAP:
            case WEBLOGO:
                return folder;
            case HOMER:
                return folder + "bin";
            default:
                return "";
        }
    }

    public static String getPythonPath() {
        return null;
    }
}
