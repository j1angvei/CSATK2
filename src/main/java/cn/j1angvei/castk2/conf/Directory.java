package cn.j1angvei.castk2.conf;

/**
 * enum representing all folders in CSATK
 * Created by Wayne on 3/14 0014.
 */
public class Directory {
    public enum Out {
        IDX_GENOME("01_genome_idx"),
        QC_RAW("02_qc_raw"),
        PARSE_RAW("03_parse_raw"),
        TRIM("04_trim"),
        QC_CLEAN("05_qc_clean"),
        PARSE_CLEAN("06_parse_clean"),
        ALIGNMENT("07_alignment"),
        BAM_CONVERTED("08_bam_converted"),
        BAM_SORTED("09_bam_sorted"),
        QC_BAM("10_bam_qc"),
        BAM_RMDUP("11_bam_rmdup"),
        BAM_UNIQUE("12_bam_unique"),
        PEAK_CALLING("13_peak_calling"),
        BIGWIG("14_bigwig"),
        ANNOTATION("15_peak_anno"),
        GENE_LIST("16_gene_list"),
        GO_PATHWAY("17_go_pathway"),
        MOTIF("18_motif"),
        STATISTICS("19_statistic"),
        HTML("20_html"),
        CHIP_QUALITY("21_chip_quality"),
        TSS_PROFILE("22_tss_profile"),
        CORRELATION("23_correlation"),
        BIGWIG_DT("24_bigwig_dt"),
        MATRIX("25_matrix"),
        PEAK_HEATMAP("26_peak_heatmap");

        private String dirName;

        Out(String dirName) {
            this.dirName = dirName;
        }

        public String getDirName() {
            return dirName;
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
        private String dirName;

        Sub(String dirName) {
            this.dirName = dirName;
        }

        public String getDirName() {
            return dirName;
        }
    }
}
