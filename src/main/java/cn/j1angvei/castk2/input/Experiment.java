package cn.j1angvei.castk2.input;

/**
 * Created by mjian on 2016/11/29.
 */
public class Experiment {
    private String code;
    private String fastq1;
    private String fastq2;
    private String control;
    private int genomeCode;

    public Experiment() {
    }

    public String getCode() {
        return code;
    }

    public String getFastq1() {
        return fastq1;
    }

    public String getFastq2() {
        return fastq2;
    }

    public String getControl() {
        return control;
    }

    public int getGenomeCode() {
        return genomeCode;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "code='" + code + '\'' +
                ", fastq1='" + fastq1 + '\'' +
                ", fastq2='" + fastq2 + '\'' +
                ", control='" + control + '\'' +
                ", genomeCode='" + genomeCode + '\'' +
                '}';
    }
}
