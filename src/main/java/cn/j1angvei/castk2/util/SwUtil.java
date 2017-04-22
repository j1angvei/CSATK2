package cn.j1angvei.castk2.util;

import cn.j1angvei.castk2.ConfigInitializer;
import cn.j1angvei.castk2.Constant;
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
                FileUtil.appendFile(geneId, geneListFile, true);
            }
        }
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
        System.err.println("Check your genomeCode in config.json");
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

}
