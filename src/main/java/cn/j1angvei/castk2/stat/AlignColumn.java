package cn.j1angvei.castk2.stat;

/**
 * Created by Wayne on 4/21 0021.
 */
public class AlignColumn implements Column {
    private String expCode;
    private long allReads;
    private long sortedReads;
    private long rmdupReads;
    private long uniqueReads;

    public AlignColumn(String expCode, long allReads, long sortedReads, long rmdupReads, long uniqueReads) {
        this.expCode = expCode;
        this.allReads = allReads;
        this.sortedReads = sortedReads;
        this.rmdupReads = rmdupReads;
        this.uniqueReads = uniqueReads;
    }

    public String getExpCode() {
        return expCode;
    }

    public long getAllReads() {
        return allReads;
    }

    public long getSortedReads() {
        return sortedReads;
    }

    public long getRmdupReads() {
        return rmdupReads;
    }

    public long getUniqueReads() {
        return uniqueReads;
    }

    @Override
    public String getHeader() {
        return "Sampe\tAll reads\tMapped reads\tRmdup reads\tUnique reads";
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s", expCode, allReads, sortedReads, rmdupReads, uniqueReads);
    }
}
