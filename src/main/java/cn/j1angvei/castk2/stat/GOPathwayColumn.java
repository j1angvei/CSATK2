package cn.j1angvei.castk2.stat;

import cn.j1angvei.castk2.panther.GoType;

/**
 * Created by Wayne on 4/22 0022.
 */
public class GOPathwayColumn implements Column {
    private String expCode;
    private GoType goType;
    private String description;
    private int count;
    private String percentage;

    public GOPathwayColumn(String expCode, GoType goType, String description, int count, String percentage) {
        this.expCode = expCode;
        this.goType = goType;
        this.description = description;
        this.count = count;
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%d\t%s", expCode, goType.getDescription(), description, count, percentage);
    }

    @Override
    public String getHeader() {
        return null;
    }

    public String getExpCode() {
        return expCode;
    }

    public GoType getGoType() {
        return goType;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }

    public String getPercentage() {
        return percentage;
    }
}
