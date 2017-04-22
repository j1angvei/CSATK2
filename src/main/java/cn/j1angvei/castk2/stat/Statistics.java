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
 * do statistics for all output files
 * Created by Wayne on 4/21 0021.
 */
public class Statistics {

    public static String start(Experiment exp, StatType type) {
        switch (type) {
            case RAW_DATA:
                String fastq1 = exp.getFastq1();
                boolean pairEnd = StrUtil.isValid(exp.getFastq2());
                String fastq2 = pairEnd ? exp.getFastq2() : "--";
                long size = FileUtil.getFileSize(ConfigInitializer.getPath(Directory.Sub.INPUT) + exp.getFastq1(), FileUtil.Unit.GB);
                String fileSize = size + FileUtil.Unit.GB.name() + (pairEnd ? " x2" : "");
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
                long peakLenSum = 0, peakAvgLen = 0;
                for (String line : lines) {
                    String[] info = line.split("\t");
                    peakLenSum += Long.parseLong(info[2]) - Long.parseLong(info[1]) + 1;
                }
                try {
                    peakAvgLen = peakLenSum / peakCount;
                } catch (ArithmeticException e) {
                    System.err.printf("peak file: %s" + peakFilePath);
                }
                PeakCallColumn callColumn = new PeakCallColumn(exp.getCode(), exp.isBroadPeak(), peakAvgLen, peakCount);
                return callColumn.toString();
            case PEAK_ANNO:
                PeakAnnoColumn annoColumn = new PeakAnnoColumn(exp.getCode());
                Map<String, Long> annoMap = annoColumn.getMap();
                String annoFilePath = ConfigInitializer.getPath(Directory.Out.ANNOTATION) + exp.getCode() + Constant.SFX_ANNO_BED;
                for (String line : FileUtil.readLines(annoFilePath)) {
                    //skip the first line
                    if (line.contains(Constant.EXE_HOMER_ANNOTATE_PEAK)) continue;
                    String[] info = line.split("\t");
                    String annoType = info[7].replaceAll("[(\\s].*$", "");
                    long count = annoMap.get(annoType);
                    annoMap.put(annoType, count + 1);
                }
                return annoColumn.toString();
            case GENE_ONTOLOGY:
                List<String> goPathLines = FileUtil.readLines(ConfigInitializer.getPath(Directory.Out.GO_PATHWAY) + exp.getCode() + Constant.SFX_GO_PATHWAY);
                StringBuilder goBuilder = new StringBuilder();
                String goDescription = null;
                for (String line : goPathLines) {
                    //skip empty line in go_pathway result
                    if (StrUtil.isInvalid(line)) {
                        continue;
                    }
                    //change the readFront as GoTypeStr is Pathway
                    if (line.startsWith("#")) {
                        goDescription = line.substring(1, line.length());
                        if (goDescription.equals(GoType.PATHWAY.getDescription())) {
                            break;
                        }
                        continue;
                    }

                    String[] goInfo = line.split("\t");
                    GOColumn goColumn = new GOColumn(exp.getCode(), goDescription,
                            goInfo[1], Integer.parseInt(goInfo[2]), goInfo[3]);
                    goBuilder.append(goColumn.toString()).append("\n");
                }
                return goBuilder.toString();
            case PATHWAY:
                List<String> pathwayPathLines = FileUtil.readLines(ConfigInitializer.getPath(Directory.Out.GO_PATHWAY) + exp.getCode() + Constant.SFX_GO_PATHWAY);
                boolean start = false;
                StringBuilder pathwayBuilder = new StringBuilder();
                String pathwayDesc;
                for (String line : pathwayPathLines) {
                    if (StrUtil.isInvalid(line)) {
                        continue;
                    }
                    if (line.startsWith("#")) {
                        pathwayDesc = line.substring(1, line.length());
                        if (pathwayDesc.equals(GoType.PATHWAY.getDescription())) {
                            start = true;
                        }
                        continue;
                    }
                    if (start) {
                        String[] pathwayInfo = line.split("\t");
                        PathwayColumn pathwayColumn = new PathwayColumn(exp.getCode(), pathwayInfo[1],
                                Integer.parseInt(pathwayInfo[2]), pathwayInfo[3]);
                        pathwayBuilder.append(pathwayColumn.toString()).append("\n");
                    }
                }
                return pathwayBuilder.toString();

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
        for (StatType type : StatType.values()) {
            String filePath = filePrefix + type.getResFileName();
            FileUtil.overwriteFile("", filePrefix + type.getResFileName());
            FileUtil.appendFile(type.getResFileHeader(), filePath, true);
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
}
