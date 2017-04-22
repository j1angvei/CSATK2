package cn.j1angvei.castk2.stat;

/**
 * Created by Wayne on 4/22 0022.
 */
public class PathwayColumn implements Column, Comparable<PathwayColumn> {
    private String expCode;
    private String id;
    private int count;
    private String percentage;

    public PathwayColumn(String expCode, String id, int count, String percentage) {
        this.expCode = expCode;
        this.id = id;
        this.count = count;
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%d\t%s", expCode, id, count, percentage);
    }

    @Override
    public String getHeader() {
        return null;
    }

    public String getExpCode() {
        return expCode;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getPercentage() {
        return percentage;
    }

    @Override
    public int compareTo(PathwayColumn o) {
        if (!expCode.equals(o.getExpCode())) {
            return expCode.compareTo(o.getExpCode());
        } else {
            return o.getCount() - count;
        }
    }
}
