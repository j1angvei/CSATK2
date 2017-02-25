package cn.j1angvei.castk2.anno;

/**
 * Created by Wayne on 2/26 0026.
 */
public class Upstream extends Region {
    public Upstream(String chromosome, long start, long end) {
        super(chromosome, start, end);
        setType("upstream");
    }

    public Upstream(Gene gene) {
        super(gene.getChromosome(), gene.getStart() - 2000, gene.getStart() - 1);
    }
}
