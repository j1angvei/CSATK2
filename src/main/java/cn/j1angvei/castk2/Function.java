package cn.j1angvei.castk2;

/**
 * Created by j1angvei on 2016/12/7.
 */
public enum Function {
    GENOME_IDX("gi"),
    QC_RAW("qr"),
    TRIM("tm"),
    QC_CLEAN("qc"),
    ALIGNMENT("al"),
    CONVERT_SAM("cs"),
    SORT_BAM("sb"),
    QC_BAM("qb"),
    RMDUP_BAM("rb"),
    UNIQUE_BAM("ub"),
    PEAK_CALLING("pc"),
    PEAK_ANNOTATION("pa"),
    MOTIF("mt"),
    GO("go"),
    PATHWAY("pw"),
    ANNOTATION_REGROUP("ar");

    private String keyword;

    Function(String keyword) {
        this.keyword = keyword;
    }

    public static Function fromKeyword(String keyword) {
        for (Function function : Function.values()) {
            if (keyword.equalsIgnoreCase(function.getKeyword())) {
                return function;
            }
        }
        throw new IllegalArgumentException("No Function with keyword " + keyword + " found");
    }

    public String getKeyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return this.keyword;
    }
}
