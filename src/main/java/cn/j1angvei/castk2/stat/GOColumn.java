package cn.j1angvei.castk2.stat;

/**
 * Created by Wayne on 4/22 0022.
 */
public class GOColumn implements Column {
    private String expCode;
    private String type;
    private String id;
    private int count;
    private String percentage;

    public GOColumn(String expCode, String type, String id, int count, String percentage) {
        this.expCode = expCode;
        this.type = type;
        this.id = id;
        this.count = count;
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%,d\t%s", expCode, type, id, count, percentage);
    }

    @Override
    public String getHeader() {
        return null;
    }

    public String getExpCode() {
        return expCode;
    }

    public String getType() {
        return type;
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
}
