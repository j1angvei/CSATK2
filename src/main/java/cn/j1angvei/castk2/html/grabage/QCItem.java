package cn.j1angvei.castk2.html.grabage;

/**
 * Created by Wayne on 4/22 0022.
 */
public class QCItem extends Item {
    private String len;
    private String total;
    private String filtered;
    private String ratio;

    public QCItem(String line) {
        super(line);
        len = info[1];
        total = info[2];
        filtered = info[3];
        ratio = info[4];
    }

    public String getLen() {
        return len;
    }

    public String getTotal() {
        return total;
    }

    public String getFiltered() {
        return filtered;
    }

    public String getRatio() {
        return ratio;
    }
}
