package cn.j1angvei.castk2.input;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjian on 2016/11/29.
 */
public class Input {
    @SerializedName("genome")
    private List<Genome> genome;
    @SerializedName("experiment")
    private List<Experiment> experiment;
    @SerializedName("broadPeaks")
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
            e.setBroadPeak(broadPeaks.contains(e.getCode()));
//            if (broadPeaks.contains(e.getCode())) {
//                e.setBroadPeak(true);
//            } else {
//                e.setBroadPeak(false);
//            }
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
