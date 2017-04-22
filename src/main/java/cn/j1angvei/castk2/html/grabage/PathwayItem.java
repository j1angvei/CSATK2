package cn.j1angvei.castk2.html.grabage;

/**
 * Created by Wayne on 4/22 0022.
 */
public class PathwayItem extends Item {
    private String description;
    private String count;
    private String percentage;

    public PathwayItem(String line) {
        super(line);
        description = info[2];
        count = info[3];
        percentage = info[4];
    }

    public String getDescription() {
        return description;
    }

    public String getCount() {
        return count;
    }

    public String getPercentage() {
        return percentage;
    }
}
