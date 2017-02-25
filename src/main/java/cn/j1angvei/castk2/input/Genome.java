package cn.j1angvei.castk2.input;

/**
 * Created by mjian on 2016/11/29.
 */
public class Genome {
    private int code;
    private String name;
    private long size;
    private String fasta;
    private String annotation;

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

    public void setSize(long size) {
        this.size = size;
    }

    public String getFasta() {
        return fasta;
    }

    public String getAnnotation() {
        return annotation;
    }

    @Override
    public String toString() {
        return "Genome{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", fasta='" + fasta + '\'' +
                ", annotation='" + annotation + '\'' +
                '}';
    }
}