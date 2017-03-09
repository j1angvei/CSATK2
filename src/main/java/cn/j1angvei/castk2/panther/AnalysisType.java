package cn.j1angvei.castk2.panther;

/**
 * Created by Wayne on 3/10 0010.
 */

public enum AnalysisType {
    MOLECULAR_FUNCTION(1, "MF"),
    BIOLOGICAL_PROCESS(2, "BP"),
    PATHWAY(3, "KEGG"),
    CELLULAR_COMPONENT(4, "CC");
    private int type;
    private String initial;

    AnalysisType(int type, String initial) {
        this.type = type;
        this.initial = initial;
    }

    public int getType() {
        return type;
    }

    public String getInitial() {
        return initial;
    }
}