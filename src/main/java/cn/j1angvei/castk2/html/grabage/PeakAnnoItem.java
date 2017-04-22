package cn.j1angvei.castk2.html.grabage;

/**
 * Created by Wayne on 4/22 0022.
 */
public class PeakAnnoItem extends Item {
    private String intergenic;
    private String exon;
    private String promoterTSS;
    private String TTS;
    private String intron;

    public PeakAnnoItem(String line) {
        super(line);
        intergenic = info[1];
        exon = info[2];
        promoterTSS = info[3];
        TTS = info[4];
        intron = info[5];
    }

    public String getIntergenic() {
        return intergenic;
    }

    public String getExon() {
        return exon;
    }

    public String getPromoterTSS() {
        return promoterTSS;
    }

    public String getTTS() {
        return TTS;
    }

    public String getIntron() {
        return intron;
    }
}
