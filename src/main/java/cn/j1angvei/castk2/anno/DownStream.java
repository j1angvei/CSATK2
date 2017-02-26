package cn.j1angvei.castk2.anno;

/**
 * Created by Wayne on 2/26 0026.
 */
public class DownStream extends Region {
    public DownStream(String chromosome, long start, long end) {
        super(chromosome, start, end);
        type = "downstream";
    }


    public DownStream(Gene gene) {
        super(gene.getChromosome(), gene.getEnd() + 1, gene.getEnd() + 2000);
        type = "downstream";
    }
}
