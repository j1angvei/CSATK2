package cn.j1angvei.castk2.qc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Wayne on 4/8 0008.
 */
public class QCInfo {
    private String encoding;//fastq encode format
    private long totalReads;//totalReads reads
    private int length;//reads length
    private int percentGC;// GC percentage
    private int headCrop;//the number of how many base show cut from the start
    private Map<String, String> overrepresentedSequence;//overrepresented sequences, sequence as key, sequence name as value
    private Set<String> adapter;//adapters exceed 5% of all sequences

    public QCInfo() {
        overrepresentedSequence = new HashMap<>();
        adapter = new HashSet<>();
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public long getTotalReads() {
        return totalReads;
    }

    public void setTotalReads(long totalReads) {
        this.totalReads = totalReads;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPercentGC() {
        return percentGC;
    }

    public void setPercentGC(int percentGC) {
        this.percentGC = percentGC;
    }

    public int getHeadCrop() {
        return headCrop;
    }

    public void setHeadCrop(int headCrop) {
        this.headCrop = headCrop;
    }

    public Map<String, String> getOverrepresentedSequence() {
        return overrepresentedSequence;
    }

    public void setOverrepresentedSequence(Map<String, String> overrepresentedSequence) {
        this.overrepresentedSequence = overrepresentedSequence;
    }

    public Set<String> getAdapter() {
        return adapter;
    }

    public void setAdapter(Set<String> adapter) {
        this.adapter = adapter;
    }
}
