package cn.j1angvei.castk2.html.grabage;

/**
 * Created by Wayne on 4/22 0022.
 */
public class RawItem extends Item {
    private String fastq1;
    private String fastq2;
    private String size;
    private String species;

    public RawItem(String line) {
        super(line);
        fastq1 = info[1];
        fastq2 = info[2];
        size = info[3];
        species = info[4];
    }

    public String getFastq1() {
        return fastq1;
    }

    public String getFastq2() {
        return fastq2;
    }

    public String getSize() {
        return size;
    }

    public String getSpecies() {
        return species;
    }
}
