package cn.j1angvei.castk2;

/**
 * Created by j1angvei on 2016/12/7.
 */
public enum Function {
    //genome analysis
    GENOME_IDX("gi", "genome index", "build genome index using bwa"),
    GENOME_SIZE("gs", "genome size", "generate genome size file using SAMTools faidx"),
    GENOME_TSS("gt", "genome tss", "calculate TSS position using ucsc executable"),
    //raw data qc analysis
    QC_RAW("qr", "qc raw reads", "do quality control of raw reads using FastQC"),
    PARSE_RAW("ar", "parse raw reads", "parse key information from raw reads' qc result"),
    TRIM("tm", "trim reads", "filter adapter and bad quality reads using Trimmomatic"),
    QC_CLEAN("qc", "qc clean reads", "do quality control of filtered reads using FastQC"),
    PARSE_CLEAN("ac", "parse clean reads", "parse key information from filtered reads' qc result"),
    //alignment analysis
    ALIGNMENT("al", "alignment", "align reads using bwa(BWA-MEM for > 70 bp, BWA-ALN for < 70bp)"),
    CONVERT_SAM("cs", "convert sam", "convert sam file to bam file using SAMTools"),
    SORT_BAM("sb", "sort bam", "sort converted bam files using SAMTools"),
    QC_BAM("qb", "qc bam", "do quality control of sorted bam file using QualiMap"),
    RMDUP_BAM("rb", "rmdup bam", "remove PCR amplified reads from bam file using SAMTools"),
    UNIQUE_BAM("ub", "unique bam", "filter reads those mapping quality >30 using SAMTools"),
    FLAGSTAT("fs", "flagstat bam", "count reads in all bam files using SAMTools flagstat"),
    BW_DT("bd", "bam to bigwig", "generate bigwig file using Deeptools"),
    //peak analysis
    PEAK_CALLING("pc", "peak calling", "do peak calling using MACS2"),
    PEAK_ANNOTATION("pa", "peak annotation", "do peak annotation using Homer"),
    PEAK_HEATMAP("ph", "peak heatmap", "plot peak heatmap"),
    MOTIF("mt", "find motif", "find motifs using Homer"),
    //gene analysis
    GENE_LIST("gl", "parse gene list", "get annotated gene from annotation bed file"),
    TSS_PROFILE("tp", "tss plot", "plot tss profile & heatmap information using deeptools"),
    GO_PATHWAY("gp", "go & pathway", "do GO & Pathway analysis using PANTHERDB.org(Network connection needed!)"),
    //experiments analysis
    CHIP_QUALITY("cq", "experiment quality", "calculate ChIP quality using deeptools fingerprint"),
    CORRELATION("cl", "experiment correlation", "calculate different experiments's correlation using deeptools's spearman algorithm"),
    //statistics
    STATISTIC("st", "statistics", "do a statistics from output"),
    HTML("ht", "html report", "plot the statistic in HTML format");

    private String keyword;
    private String title;
    private String hint;

    Function(String keyword, String title, String hint) {
        this.keyword = keyword;
        this.title = title;
        this.hint = hint;
    }

    public static Function fromKeyword(String keyword) {
        for (Function function : Function.values()) {
            if (keyword.equalsIgnoreCase(function.getKeyword())) {
                return function;
            }
        }
        throw new IllegalArgumentException("Function using keyword " + keyword + " not found");
    }

    public static String getFunctionUsage() {
        StringBuilder sb = new StringBuilder();
//        sb.append("\nGenome function:\n");
        for (Function function : Function.values()) {
            sb.append(function.toString())
                    .append("\n");
//            if (function == GENOME_TSS) {
//                sb.append("\nQC functions:\n");
//            } else if (function == PARSE_CLEAN) {
//                sb.append("\nAlignment functions:\n");
//            } else if (function == BW_DT) {
//                sb.append("\nPeak functions\n");
//            } else if (function == MOTIF) {
//                sb.append("\nGene functions:\n");
//            } else if (function == GO_PATHWAY) {
//                sb.append("\nExperiments functions:\n");
//            } else if (function == CORRELATION) {
//                sb.append("\nStatistics functions:\n");
//            }
        }
        return sb.toString();
    }

    public static String assemblePipelineKeywords() {
        StringBuilder builder = new StringBuilder();
        for (Function function : Function.values()) {
            builder.append(function.getKeyword())
                    .append(",");
        }
        String keywords = builder.toString();
        System.out.println("pipeline " + keywords);
        return keywords.substring(0, keywords.length() - 1);
    }

    public String getKeyword() {
        return keyword;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public String toString() {
        return "\t" + this.keyword + " [" + this.title + "]" + ",\t" + this.hint;
    }
}
