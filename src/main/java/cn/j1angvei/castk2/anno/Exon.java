package cn.j1angvei.castk2.anno;

/**
 * Created by Wayne on 2/26 0026.
 */
public class Exon extends Region {
    public Exon(String chromosome, long start, long end) {
        super(chromosome, start, end);
        type = "exon";
    }

    public Exon(String chromosome, String strStart, String strEnd) {
        super(chromosome, strStart, strEnd);
        type = "exon";
    }
}
