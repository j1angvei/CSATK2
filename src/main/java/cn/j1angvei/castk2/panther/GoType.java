package cn.j1angvei.castk2.panther;

/**
 * gene ontology analysis type
 * Created by Wayne on 3/10 2017.
 */

public enum GoType {
    MOLECULAR_FUNCTION(1, "Molecular Function"),
    BIOLOGICAL_PROCESS(2, "Biological Process"),
    CELLULAR_COMPONENT(4, "Cellular Component"),
    PATHWAY(3, "Pathway");

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