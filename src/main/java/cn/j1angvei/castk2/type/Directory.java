package cn.j1angvei.castk2.type;

/**
 * Created by Wayne on 3/14 0014.
 */
public class Directory {
    public enum Out {
        IDX_GENOME,
        QC_RAW, TRIM, QC_CLEAN, PARSE_ZIP,
        ALIGNMENT, BAM_CONVERTED, BAM_SORTED, QC_BAM,
        BAM_RMDUP, BAM_UNIQUE, PEAK_CALLING,
        MOTIF,
        ANNOTATION,
        GENE_LIST, GO_PATHWAY,
        BAM_STAT, PEAK_STAT, ANNO_STAT, DIFF_STAT,
        SUMMARY
    }

    public enum Sub {
        ARCHIVE, BACKUP, CONFIG, GENOME, INPUT, LIB, LOG, OUTPUT, SCRIPT, SOFTWARE
    }
}
