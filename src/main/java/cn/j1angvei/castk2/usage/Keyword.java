package cn.j1angvei.castk2.usage;

/**
 * Created by Wayne on 5/5 0005.
 */
public class Keyword {
    public enum Basic {
        HELP, INSTALL, PIPELINE, BACKUP, RESET, SPECIES_CODE, PEAK_TYPE
    }

    public enum Shell {
        QC, TRIM, ALIGNMENT, FLAGSTAT, PEAK_CALLING, PEAK_ANNOTATION, MOTIF, PLOT
    }

    public enum Builtin {
        PARSE_QC_ZIP, GENE_LIST, GO_PATHWAY,
    }
}
