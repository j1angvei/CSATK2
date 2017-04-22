package cn.j1angvei.castk2.stat;

/**
 * Created by Wayne on 4/22 0022.
 */
public class RawColumn implements Column {
    private String expCode;
    private String fastq1;
    private String fastq2;
    private String fileSize;
    private String species;

    public RawColumn(String expCode, String fastq1, String fastq2, String fileSize, String species) {
        this.expCode = expCode;
        this.fastq1 = fastq1;
        this.fastq2 = fastq2;
        this.fileSize = fileSize;
        this.species = species;
    }

    public String getExpCode() {
        return expCode;
    }

    public String getFastq1() {
        return fastq1;
    }

    public String getFastq2() {
        return fastq2;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getSpecies() {
        return species;
    }

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\n", expCode, fastq1, fastq2, fileSize, species);
    }
}
