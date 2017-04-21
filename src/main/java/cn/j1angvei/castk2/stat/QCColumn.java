package cn.j1angvei.castk2.stat;

/**
 * Created by Wayne on 4/21 0021.
 */
public class QCColumn implements Column {
    private String expCode;
    private int readLength;
    private long rawReads;
    private long filterReads;
    private double ratio;

    public QCColumn(String expCode, int readLength, long rawReads, long filterReads) {
        this.expCode = expCode;
        this.readLength = readLength;
        this.rawReads = rawReads;
        this.filterReads = filterReads;
        this.ratio = 1f * filterReads / rawReads;
    }

    @Override
    public String getHeader() {
        return "Sampe Name\tReads length\tTotal reads\tFiltered reads\tRatio";
    }

    @Override
    public String toString() {
        return String.format("%s\t%d\t%d\t%d\t%.2f%%", expCode, readLength, rawReads, filterReads, ratio * 100);
    }

    public String getExpCode() {
        return expCode;
    }

    public int getReadLength() {
        return readLength;
    }

    public long getRawReads() {
        return rawReads;
    }

    public long getFilterReads() {
        return filterReads;
    }

    public double getRatio() {
        return ratio;
    }
}
