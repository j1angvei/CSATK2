package cn.j1angvei.castk2.qc;

/**
 * Created by Wayne on 4/8 0008.
 */
public enum Adapter {
    ILLUMINA_UNIVERSAL_ADAPTER("Illumina Universal Adapter", "AGATCGGAAGAG"),
    ILLUMINA_SMALL_RNA_3_ADAPTER("Illumina Small RNA 3' Adapter", "TGGAATTCTCGG"),
    ILLUMINA_SMALL_RNA_5_ADAPTER("Illumina Small RNA 5' Adapter", "GATCGTCGGACT"),
    NEXTERA_TRANSPOSASE_SEQUENCE("Nextera Transposase Sequence", "CTGTCTCTTATA"),
    SOLID_SMALL_RNA_ADAPTER("SOLID Small RNA Adapter", "CGCCTTGGCCGT");

    private String name;
    private String sequence;

    Adapter(String name, String sequence) {
        this.name = name;
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public String getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", name, sequence);
    }
}
