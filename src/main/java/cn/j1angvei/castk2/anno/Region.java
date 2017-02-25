package cn.j1angvei.castk2.anno;

/**
 * Created by Wayne on 2/26 0026.
 */
public class Region implements Comparable<Region> {
    private String chromosome;
    private long start;
    private long end;
    private String type;

    public Region(String chromosome, long start, long end) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
    }

    public Region(String chromosome, String strStart, String strEnd) {
        this.chromosome = chromosome;
        try {
            this.start = Long.parseLong(strStart);
            this.end = Long.parseLong(strEnd);
        } catch (NumberFormatException e) {
            //this means the region is invalid
            e.printStackTrace();
            this.start = -1;
            this.end = -1;
        }
    }

    public String getChromosome() {
        return chromosome;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(Region o) {
        if (o == null) return 1;
        if (!this.getChromosome().equals(o.getChromosome())) {
            return this.getChromosome().compareTo(o.getChromosome());
        } else if (o.getStart() != this.getStart()) {
            return (int) (this.getStart() - o.getStart());
        } else return (int) (this.getEnd() - o.getEnd());
    }

    public static boolean isOverlapped(Region a, Region b) {
        //if at least one of the region is null, not overlapped
        //if chromosome not the same, not overlapped
        //If one region's end is before the other's start, not overlapped
        return !(a == null || b == null) && a.getChromosome().equals(b.getChromosome()) && a.getEnd() >= b.getStart() && b.getEnd() >= a.getStart();
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%d\t%d", chromosome, type, start, end);
    }
}
