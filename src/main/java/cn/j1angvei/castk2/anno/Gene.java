package cn.j1angvei.castk2.anno;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wayne on 2/26 0026.
 */
public class Gene extends Region {
    private List<Exon> mExons;
    private List<Intron> mIntrons;

    public Gene(String chromosome, String strStart, String strEnd) {
        super(chromosome, strStart, strEnd);
        mExons = new ArrayList<>();
        mIntrons = new ArrayList<>();
        setType("gene");
    }

    public void addExon(Exon exon) {
        mExons.add(exon);
    }

    public void addIntron(Intron intron) {
        mIntrons.add(intron);
    }

    public List<Exon> getExons() {
        return mExons;
    }

    public List<Intron> getIntrons() {
        return mIntrons;
    }
}
