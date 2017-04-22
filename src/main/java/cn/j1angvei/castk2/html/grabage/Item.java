package cn.j1angvei.castk2.html.grabage;

/**
 * Created by Wayne on 4/22 0022.
 */
public abstract class Item {
    private String expCode;
    String[] info;

    public Item(String line) {
        info = line.split("\t");
        expCode = info[0];
    }


    public String getExpCode() {
        return expCode;
    }
}
