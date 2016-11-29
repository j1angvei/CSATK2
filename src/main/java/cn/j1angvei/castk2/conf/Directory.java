package cn.j1angvei.castk2.conf;

import java.util.Arrays;

/**
 * Created by mjian on 2016/11/29.
 */
public class Directory {
    private String[] sub;
    private String[] out;

    public Directory() {
    }

    public String[] getSub() {
        return sub;
    }

    public String[] getOut() {
        return out;
    }

    @Override
    public String toString() {
        return "Directory{" +
                "sub=" + Arrays.toString(sub) +
                ", out=" + Arrays.toString(out) +
                '}';
    }
}
