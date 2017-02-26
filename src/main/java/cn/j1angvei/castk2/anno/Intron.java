package cn.j1angvei.castk2.anno;

/**
 * Created by Wayne on 2/26 0026.
 */
public class Intron extends Region {
    public Intron(String chromosome, long start, long end) {
        super(chromosome, start, end);
        type = "intron";
    }

    public Intron(Exon upExon, Exon downExon) {
        super(upExon.getChromosome(), upExon.getEnd() + 1, downExon.getStart() - 1);
        type = "intron";
    }
}
