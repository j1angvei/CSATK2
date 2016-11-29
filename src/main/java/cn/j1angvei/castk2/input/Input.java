package cn.j1angvei.castk2.input;

import java.util.List;

/**
 * Created by mjian on 2016/11/29.
 */
public class Input {
    private List<Genome> genome;
    private List<Experiment> experiment;

    public Input() {
    }

    public List<Genome> getGenome() {
        return genome;
    }

    public List<Experiment> getExperiment() {
        return experiment;
    }

    @Override
    public String toString() {
        return "Input{" +
                "genome=" + genome +
                ", experiment=" + experiment +
                '}';
    }
}
