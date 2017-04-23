package cn.j1angvei.castk2;

/**
 * Created by Wayne on 2/28 0028.
 */
public class Constant {
    public static final String SFX_NARROW_PEAKS = "_peaks.narrowPeak";
    public static final String SFX_BROAD_PEAKS = "_peaks.broadPeak";
    public static final String SFX_ANNO_BED = "_annotation.bed";
    public static final String SFX_CONVERTED_BAM = ".sam.bam";
    public static final String SFX_SORTED_BAM = ".sorted.bam";
    public static final String SFX_RMDUP_BAM = ".rmdup.bam";
    public static final String SFX_UNIQUE_BAM = ".unique.bam";
    public static final String SFX_GENE_LIST = "_geneList.txt";
    public static final String SFX_GO_PATHWAY = "_go_pathway.txt";
    public static final String EXE_HOMER_ANNOTATE_PEAK = "annotatePeaks.pl";
    public static final String EXE_HOMER_FIND_MOTIF = "findMotifsGenome.pl";
    /**
     * FastQC generate zip report file name is, FastQ file name + "_fastqc.zip";
     */
    public static final String QC_ZIP_SFX = "_fastqc.zip";
    /**
     * some regular file name suffix
     */
    public static final String JSON_SFX = ".json";
    public static final String FA_SFX = ".fa";
    public static final String SAM_SFX = ".sam";
    public static final String SAI_SFX = ".sai";

    /**
     * specific suffix for intermediate file generated in analysis
     */
    public static final String FLAGSTAT_SFX = ".flagstat";

    /**
     * specific folder in output or software
     */
    public static final String FOLDER_KNOWN_MOTIF = "homerResults";

}
