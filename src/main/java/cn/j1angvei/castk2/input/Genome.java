package cn.j1angvei.castk2.input;

/**
 * Created by mjian on 2016/11/29.
 */
public class Genome {
    private int code;
    private String name;
    private long size;
    private String fasta;
    private String gtf;

    public Genome() {
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getFasta() {
        return fasta;
    }

    public String getGtf() {
        return gtf;
    }

    @Override
    public String toString() {
        return "Genome{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", fasta='" + fasta + '\'' +
                ", gtf='" + gtf + '\'' +
                '}';
    }
}
