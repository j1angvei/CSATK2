package cn.j1angvei.castk2.input;

import java.util.List;

/**
 * Created by mjian on 2016/11/29.
 */
public class Input {
    private List<Genome> genome;
    private List<Experiment> experiment;
    private List<String> broadPeaks;

    public Input() {
    }

    public List<Genome> getGenome() {
        return genome;
    }

    public List<Experiment> getExperiment() {
        return experiment;
    }

    public List<String> getBroadPeaks() {
        return broadPeaks;
    }

    public void initBroadPeaks() {
        for (Experiment e : this.experiment) {
            if (broadPeaks.contains(e.getCode())) {
                e.setBroadPeak(true);
            }
        }
    }

    @Override
    public String toString() {
        return "Input{" +
                "genome=" + genome +
                ", experiment=" + experiment +
                ", broadPeaks=" + broadPeaks +
                '}';
    }
}
