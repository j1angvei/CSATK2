package cn.j1angvei.castk2.html.grabage;

/**
 * Created by Wayne on 4/22 0022.
 */
public class AlignItem extends Item {
    private String all;
    private String mapped;
    private String rmdup;
    private String unique;

    public AlignItem(String line) {
        super(line);
        all = info[1];
        mapped = info[2];
        rmdup = info[3];
        unique = info[4];
    }

    public String getAll() {
        return all;
    }

    public String getMapped() {
        return mapped;
    }

    public String getRmdup() {
        return rmdup;
    }

    public String getUnique() {
        return unique;
    }
}
