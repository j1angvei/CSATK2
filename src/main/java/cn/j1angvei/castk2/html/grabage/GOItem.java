package cn.j1angvei.castk2.html.grabage;

/**
 * Created by Wayne on 4/22 0022.
 */
public class GOItem extends Item {
    private String type;
    private String description;
    private String count;
    private String percent;

    public GOItem(String line) {
        super(line);
        type = info[1];
        description = info[2];
        count = info[3];
        percent = info[4];
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getCount() {
        return count;
    }

    public String getPercent() {
        return percent;
    }
}
