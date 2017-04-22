package cn.j1angvei.castk2.html.grabage;

/**
 * Created by Wayne on 4/22 0022.
 */
public class PeakCallItem extends Item {
    private String type;
    private String avgLen;
    private String count;

    public PeakCallItem(String line) {
        super(line);
        type = info[1];
        avgLen = info[2];
        count = info[3];
    }

    public String getType() {
        return type;
    }

    public String getAvgLen() {
        return avgLen;
    }

    public String getCount() {
        return count;
    }
}
