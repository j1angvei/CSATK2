package cn.j1angvei.castk2.anno;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Wayne on 2/26 0026.
 */
public class Gene extends Region {
    private Set<Exon> mExons;
    private Set<Intron> mIntrons;

    public Gene(String chromosome, String strStart, String strEnd) {
        super(chromosome, strStart, strEnd);
        mExons = new TreeSet<>();
        mIntrons = new TreeSet<>();
        type = "gene";
    }

    public void addExon(Exon exon) {
        mExons.add(exon);
    }

    public void addIntron(Intron intron) {
        mIntrons.add(intron);
    }

    public Set<Exon> getExons() {
        return mExons;
    }

    public Set<Intron> getIntrons() {
        return mIntrons;
    }
}
