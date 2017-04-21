package cn.j1angvei.castk2.stat;

import cn.j1angvei.castk2.ConfigInitializer;
import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.conf.Experiment;
import cn.j1angvei.castk2.qc.QCInfo;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * statistics type
 * Created by Wayne on 4/21 0021.
 */
public class Statistics {
//    private static ConfigInitializer initializer = ConfigInitializer.getInstance();

    public static String start(Experiment exp, Type type) {

        switch (type) {
            case QUALITY_CONTROL:
                QCInfo rawInfo = GsonUtil.fromJsonFilePath(ConfigInitializer.getPath(Directory.Out.PARSE_RAW) + exp.getCode() + Constant.JSON_SFX);
                QCInfo cleanInfo = GsonUtil.fromJsonFilePath(ConfigInitializer.getPath(Directory.Out.PARSE_CLEAN) + exp.getCode() + Constant.JSON_SFX);
                QCColumn qcColumn = new QCColumn(exp.getCode(), rawInfo.getLength(), rawInfo.getTotalReads(), cleanInfo.getTotalReads());
                return qcColumn.toString();
            case ALIGNMENT:
                long[] sortedReads = readFlagstatCount(Directory.Out.BAM_SORTED, exp.getCode());
                long rmdupReads = readFlagstatCount(Directory.Out.BAM_RMDUP, exp.getCode())[0];
                long uniqueReads = readFlagstatCount(Directory.Out.BAM_UNIQUE, exp.getCode())[0];
                AlignColumn alignColumn = new AlignColumn(exp.getCode(), sortedReads[0], sortedReads[1], rmdupReads, uniqueReads);
                return alignColumn.toString();
            case PEAK_CALL:
                String peakFilePath = ConfigInitializer.getPath(Directory.Out.PEAK_CALLING) + exp.getCode() +
                        (exp.isBroadPeak() ? Constant.SFX_BROAD_PEAKS : Constant.SFX_NARROW_PEAKS);

                List<String> lines = FileUtil.readLines(peakFilePath);
                long peakCount = lines.size();
                long peakLenSum = 0, peakAvgLen;
                for (String line : lines) {
                    String[] info = line.split("\t");
                    peakLenSum += Long.parseLong(info[2]) - Long.parseLong(info[1]) + 1;
                }

                peakAvgLen = peakLenSum / peakCount;
                PeakCallColumn callColumn = new PeakCallColumn(exp.getCode(), exp.isBroadPeak(), peakAvgLen, peakCount);
                return callColumn.toString();
            case PEAK_ANNO:
                PeakAnnoColumn annoColumn = new PeakAnnoColumn(exp.getCode());
                Map<String, Long> annoMap = annoColumn.getMap();
                String annoFilePath = ConfigInitializer.getPath(Directory.Out.ANNOTATION) + exp.getCode() + Constant.SUFFIX_ANNO_BED;
                for (String line : FileUtil.readLines(annoFilePath)) {
                    //skip the first line
                    if (line.contains(Constant.EXE_HOMER_ANNOTATE_PEAK)) continue;
                    String[] info = line.split("\t");
                    String annoType = info[7].replaceAll("[(\\s].*$", "");
                    long count = annoMap.get(annoType);
                    annoMap.put(annoType, count + 1);
                }
                return annoColumn.toString();
            default:
                return type.name() + "under construction";

        }

    }

    public static void initStatisticsFile() {
        String filePrefix = ConfigInitializer.getPath(Directory.Out.STATISTICS);
        for (Type type : Type.values()) {
            String filePath = filePrefix + type.getResFileName();
            FileUtil.overwriteFile("", filePrefix + type.getResFileName());
            FileUtil.appendFile(type.getResFileHeader(), filePath);
        }
    }


    private static long[] readFlagstatCount(Directory.Out out, String expCode) {
        if (!out.equals(Directory.Out.BAM_SORTED) && !out.equals(Directory.Out.BAM_RMDUP) && !out.equals(Directory.Out.BAM_UNIQUE)) {
            System.err.println("Wrong Directory.Out type as parameter");
            throw new IllegalArgumentException();
        }
        String filePath = ConfigInitializer.getPath(out) + expCode + Constant.FLAGSTAT_SFX;
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(new File(filePath), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lines == null || lines.size() < 5) {
            System.err.println(filePath + " content is missing some parts");
            throw new IllegalArgumentException();
        }
        long[] readsCount = new long[2];
        readsCount[0] = Long.parseLong(lines.get(0).replaceAll("\\s.*$", ""));
        readsCount[1] = Long.parseLong(lines.get(4).replaceAll("\\s.*$", ""));
        return readsCount;

    }

    public enum Type {
        QUALITY_CONTROL("quality_control.stat", "Sampe Name\tReads length\tTotal reads\tFiltered reads\tRatio"),
        ALIGNMENT("alignment.stat", "Sampe\tAll reads\tMapped reads\tRmdup reads\tUnique reads"),
        READS_STAT("reads.stat", "reads stat header"),
        PEAK_CALL("peak_calling.stat", "Sample\tPeak type\tAverage Length\tPeak Count"),
        PEAK_ANNO("peak_annotation.stat", String.format("Sample\t%s\t%s\t%s\t%s\t%s",
                PeakAnnoColumn.Type.INTERGENIC.getTypeName(),
                PeakAnnoColumn.Type.EXON.getTypeName(),
                PeakAnnoColumn.Type.PROMOTER_TSS.getTypeName(),
                PeakAnnoColumn.Type.TTS.getTypeName(),
                PeakAnnoColumn.Type.INTRON.getTypeName())),
        GO_PATHWAY("go_pathway.stat", "go pathway header"),
        MOTIF("motif.stat", "motif header"),
        TSS_PLOT("tss_plot.stat", "tss-plot header");
        private String resFileName;
        private String resFileHeader;

        Type(String resFileName, String resFileHeader) {
            this.resFileName = resFileName;
            this.resFileHeader = resFileHeader;
        }

        public String getResFileHeader() {
            return resFileHeader;
        }

        public String getResFileName() {
            return resFileName;
        }
    }
}
