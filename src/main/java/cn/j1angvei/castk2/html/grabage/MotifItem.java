package cn.j1angvei.castk2.html.grabage;

/**
 * Created by Wayne on 4/22 0022.
 */
public class MotifItem extends Item {
    private String[] motifs;

    public MotifItem(String line) {
        super(line);
        motifs = new String[info.length - 1];
        System.arraycopy(info, 1, motifs, 0, info.length - 1);
    }

    public String[] getMotifs() {
        return motifs;
    }
}
