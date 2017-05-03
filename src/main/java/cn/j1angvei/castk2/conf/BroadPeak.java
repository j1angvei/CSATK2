package cn.j1angvei.castk2.conf;

import java.util.List;

/**
 * Created by Wayne on 5/3 0003.
 */
public class BroadPeak {
    private List<String> broad;
    private List<String> narrow;
    private List<String> mix;

    public BroadPeak() {
    }

    public List<String> getBroad() {
        return broad;
    }

    public void setBroad(List<String> broad) {
        this.broad = broad;
    }

    public List<String> getNarrow() {
        return narrow;
    }

    public void setNarrow(List<String> narrow) {
        this.narrow = narrow;
    }

    public List<String> getMix() {
        return mix;
    }

    public void setMix(List<String> mix) {
        this.mix = mix;
    }

    @Override
    public String toString() {
        return String.format("Broad peaks:\t%s\n" +
                "Narrow peaks:\t%s\n" +
                "Mix peaks:\t%s\n", broad, narrow, mix);
    }
}
