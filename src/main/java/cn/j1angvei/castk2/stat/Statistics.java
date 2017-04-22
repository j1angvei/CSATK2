package cn.j1angvei.castk2.stat;

import cn.j1angvei.castk2.ConfigInitializer;
import cn.j1angvei.castk2.Constant;
import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.conf.Experiment;
import cn.j1angvei.castk2.panther.GoType;
import cn.j1angvei.castk2.qc.QCInfo;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;
import cn.j1angvei.castk2.util.StrUtil;
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

    public static String start(Experiment exp, Type type) {
        switch (type) {
            case RAW_DATA:
                String fastq1 = exp.getFastq1();
                boolean pairEnd = StrUtil.isValid(exp.getFastq2());
                String fastq2 = pairEnd ? exp.getFastq2() : "--";
                long size = FileUtil.getFileSize(ConfigInitializer.getPath(Directory.Sub.INPUT) + exp.getFastq1(), FileUtil.Unit.GB);
                String fileSize = size + FileUtil.Unit.GB.name() + (pairEnd ? "*2" : "");
                String species = ConfigInitializer.getInstance().getGenome(exp.getGenomeCode()).getName();
                RawColumn column = new RawColumn(exp.getCode(), fastq1, fastq2, fileSize, species);
                return column.toString();
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
            case GO_PATHWAY:
                List<String> goPathLines = FileUtil.readLines(ConfigInitializer.getPath(Directory.Out.GO_PATHWAY) + exp.getCode() + Constant.SUFFIX_GO_PATHWAY);
                StringBuilder builder = new StringBuilder();
                String goType = "";
                for (String line : goPathLines) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (line.startsWith("#")) {
                        goType = line.substring(1, line.length());
                        continue;
                    }
                    String[] goInfo = line.split("\t");
                    GOPathwayColumn goPathwayColumn = new GOPathwayColumn(exp.getCode(), GoType.fromDescription(goType), goInfo[1], Integer.parseInt(goInfo[2]), goInfo[3]);
                    builder.append(goPathwayColumn.toString()).append("\n");
                }
                return builder.toString();
            case MOTIF:
                String motifPngPfx = ConfigInitializer.getPath(Directory.Out.MOTIF) + exp.getCode()
                        + File.separator + Constant.FOLDER_KNOWN_MOTIF + File.separator + "known";
                MotifColumn motifColumn = new MotifColumn(exp.getCode(), motifPngPfx, 5);
                return motifColumn.toString();
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
        RAW_DATA("raw_data.stat", "Sample\tFastq 1\t Fastq 2\tFile size"),
        QUALITY_CONTROL("quality_control.stat", "Sampe Name\tReads length\tTotal reads\tFiltered reads\tRatio"),
        ALIGNMENT("alignment.stat", "Sampe\tAll reads\tMapped reads\tRmdup reads\tUnique reads"),
        PEAK_CALL("peak_calling.stat", "Sample\tPeak type\tAverage Length\tPeak Count"),
        PEAK_ANNO("peak_annotation.stat", PeakAnnoColumn.Type.asHeader()),
        GO_PATHWAY("go_pathway.stat", "Sample\tGO type\tDescription\tCount\tPercent"),
        MOTIF("motif.stat", "Sample\tMotif 1\tMotif 2\tMotif 3\tMotif 4\tMotif 5\t...");

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
