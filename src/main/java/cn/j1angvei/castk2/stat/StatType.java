package cn.j1angvei.castk2.stat;

/**
 * Created by Wayne on 4/22 0022.
 */
public enum StatType {
    RAW_DATA("1_raw_data.stat", "Sample\tFastq 1\t Fastq 2\tFile size\tSpecies"),
    QUALITY_CONTROL("2_quality_control.stat", "Sampe Name\tReads length\tTotal reads\tFiltered reads\tRatio"),
    ALIGNMENT("3_alignment.stat", "Sampe\tAll reads\tMapped reads\tRmdup reads\tUnique reads"),
    PEAK_CALL("4_peak_calling.stat", "Sample\tPeak type\tAverage Length\tPeak Count"),
    PEAK_ANNO("5_peak_annotation.stat", PeakAnnoColumn.Type.asHeader()),
    GENE_ONTOLOGY("6_go.stat", "Sample\tGO type\tDescription\tCount\tPercent"),
    CHIP_QUALITY("9_chip_quality.stat", "ChIP quality"),
    CORRELATION("10_correlation.stat", "Correlation"),
    TSS_PROFILE("11_profile", "Sample\tImage"),
    //    PATHWAY("7_pathway.stat", "Sample\tDescription\tCount\tPercent"),
    MOTIF("8_motif.stat", "Sample\tMotif 1\tMotif 2\tMotif 3\tMotif 4\tMotif 5");

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
