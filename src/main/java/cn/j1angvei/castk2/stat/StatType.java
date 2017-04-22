package cn.j1angvei.castk2.stat;

/**
 * Created by Wayne on 4/22 0022.
 */
public enum StatType {
    RAW_DATA("raw_data.stat", "Sample\tFastq 1\t Fastq 2\tFile size"),
    QUALITY_CONTROL("quality_control.stat", "Sampe Name\tReads length\tTotal reads\tFiltered reads\tRatio"),
    ALIGNMENT("alignment.stat", "Sampe\tAll reads\tMapped reads\tRmdup reads\tUnique reads"),
    PEAK_CALL("peak_calling.stat", "Sample\tPeak type\tAverage Length\tPeak Count"),
    PEAK_ANNO("peak_annotation.stat", PeakAnnoColumn.Type.asHeader()),
    GO_PATHWAY("go_pathway.stat", "Sample\tGO type\tDescription\tCount\tPercent"),
    MOTIF("motif.stat", "Sample\tMotif 1\tMotif 2\tMotif 3\tMotif 4\tMotif 5\t...");

    private String resFileName;
    private String resFileHeader;

    StatType(String resFileName, String resFileHeader) {
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
