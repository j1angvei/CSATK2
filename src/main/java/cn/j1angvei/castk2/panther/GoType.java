package cn.j1angvei.castk2.panther;

/**
 * Created by Wayne on 3/10 0010.
 */

public enum GoType {
    MOLECULAR_FUNCTION(1, "Molecular Function"),
    BIOLOGICAL_PROCESS(2, "Biological Process"),
    PATHWAY(3, "Pathway"),
    CELLULAR_COMPONENT(4, "Cellular Component");
    private int type;
    private String description;

    GoType(int type, String description) {
        this.type = type;
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}