package cn.j1angvei.castk2.type;

/**
 * Created by Wayne on 2016/11/11.
 * represent all directories under output directory
 */
public enum OutType {
    IDX_GENOME,
    QC_RAW, PARSE_ZIP, TRIM, QC_CLEAN,
    ALIGNMENT, BAM_CONVERTED, BAM_SORTED, QC_BAM,
    BAM_RMDUP, BAM_UNIQUE, PEAK_CALLING,
    MOTIF,
    ANNOTATION,
    GENE_LIST, GO_PATHWAY,
    BAM_STAT, PEAK_STAT, ANNO_STAT, DIFF_STAT,
    SUMMARY
}
