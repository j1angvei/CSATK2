package cn.j1angvei.castk2.type;

/**
 * Created by Wayne on 3/14 0014.
 */
public class Directory {
    public enum Out {
        IDX_GENOME("01_genome_idx"),
        QC_RAW("02_qc_raw"),
        PARSE_ZIP("03_parse_zip"),
        TRIM("04_trim"),
        QC_CLEAN("05_qc_clean"),
        ALIGNMENT("06_alignment"),
        BAM_CONVERTED("07_bam_converted"),
        BAM_SORTED("08_bam_sorted"),
        QC_BAM("10_bam_qc"),
        BAM_RMDUP("11_bam_rmdup"),
        BAM_UNIQUE("12_bam_unique"),
        PEAK_CALLING("13_peak_calling"),
        ANNOTATION("14_peak_anno"),
        GENE_LIST("15_gene_list"),
        GO_PATHWAY("16_go_pathway"),
        MOTIF("17_motif"),
        SUMMARY("18_summary"),
        HTML("19_html");

        private String name;

        Out(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Sub {
        ARCHIVE("archive"),
        BACKUP("backup"),
        CONFIG("config"),
        GENOME("genome"),
        INPUT("input"),
        LOG("log"),
        OUTPUT("output"),
        SCRIPT("script"),
        SOFTWARE("software");
        private String name;

        Sub(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
