package cn.j1angvei.castk2.stat;

/**
 * Created by Wayne on 4/21 0021.
 */
public class PeakCallColumn implements Column {
    private String expCode;
    private boolean broadPeak;
    private long avgLength;
    private long peakCount;

    public PeakCallColumn(String expCode, boolean broadPeak, long avgLength, long peakCount) {
        this.expCode = expCode;
        this.broadPeak = broadPeak;
        this.avgLength = avgLength;
        this.peakCount = peakCount;
    }

    public String getExpCode() {
        return expCode;
    }


    public boolean isBroadPeak() {
        return broadPeak;
    }

    public long getPeakCount() {
        return peakCount;
    }

    @Override
    public String getHeader() {
        return "Sample\thistone\\TF\tPeak type\tPeak Count";
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%,d\t%,d", expCode, broadPeak ? "broad" : "narrow", avgLength, peakCount);
    }
}
