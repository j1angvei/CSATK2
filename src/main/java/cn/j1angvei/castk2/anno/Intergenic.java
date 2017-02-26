package cn.j1angvei.castk2.anno;

/**
 * Created by Wayne on 2/26 0026.
 */
public class Intergenic extends Region {
    public Intergenic(String chromosome, long start, long end) {
        super(chromosome, start, end);
        type = "intergenic";
    }

    public Intergenic(DownStream downStream, Upstream upstream) {
        super(downStream.getChromosome(), downStream.getEnd() + 1, upstream.getStart() - 1);
        type = "intergenic";
    }
}
