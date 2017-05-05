package cn.j1angvei.castk2;

/**
 * Created by Wayne on 2/28 0028.
 */
public class Constant {
    /**
     * all suffix for intermediate result file
     */
    public static final String SFX_NARROW_PEAKS = "_peaks.narrowPeak";
    public static final String SFX_BROAD_PEAKS = "_peaks.broadPeak";
    public static final String SFX_ANNO_BED = "_annotation.bed";
    public static final String SFX_CONVERTED_BAM = ".sam.bam";
    public static final String SFX_SORTED_BAM = ".sorted.bam";
    public static final String SFX_RMDUP_BAM = ".rmdup.bam";
    public static final String SFX_UNIQUE_BAM = ".unique.bam";
    public static final String SFX_GENE_LIST = "_geneList.txt";
    public static final String SFX_GO_PATHWAY = "_go_pathway.txt";
    public static final String SFX_BEDGRAPH = "_treat_pileup.bdg";
    /**
     * real executable in homer
     */
    public static final String EXE_HOMER_ANNOTATE_PEAK = "annotatePeaks.pl";
    public static final String EXE_HOMER_FIND_MOTIF = "findMotifsGenome.pl";
    public static final String EXE_HOMER_PARSE_GTF = "parseGTF.pl";
    /**
     * real executable in deeptools
     */
    //convert bam file to bigwig
    public static final String EXE_DT_BAMCOMPARE = "bamCompare";
    public static final String EXE_DT_BAMCOVERAGE = "bamCoverage";

    //calculate matrix, which will be used fro plotHeatmap and plotProfile
    public static final String EXE_DT_COMPUTEMATRIX = "computeMatrix";

    public static final String EXE_DT_PLOTFINGERPRINT = "plotFingerprint";
    public static final String EXE_DT_PLOTHEATMAP = "plotHeatmap";
    public static final String EXE_DT_PLOTPROFILE = "plotProfile";

    public static final String SFX_DT_BIG_WIG = ".deeptools.bw";
    public static final String SFX_DT_TSS_MATRIX = ".tss.matrix";

    //png suffix
    public static final String PNG_DT_PROFILE = ".profile.png";
    public static final String PNG_DT_HEATMAP = ".heatmap.png";

    /**
     * genome relevant suffix
     */
    public static final String SFX_GENOME_FAIDX = ".fai";
    public static final String SFX_GENOME_SIZES = ".sizes";
    public static final String SFX_GENOME_TSS = ".tss.bed";
    /**
     * real executable distributed by UCSC
     */
    public static final String EXE_UCSC_BED_SORT = "bedSort";
    public static final String EXE_UCSC_BEDGRAPH_2_BIGWIG = "bedGraphToBigWig";
    public static final String EXE_UCSC_BIGWIG_2_WIG = "bigWigToWig";
    /**
     * bigWig file relevant suffix
     */
    public static final String SFX_UCSC_BIG_WIG = ".ucsc.bw";
    public static final String SFX_UCSC_WIG = ".wig";
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
