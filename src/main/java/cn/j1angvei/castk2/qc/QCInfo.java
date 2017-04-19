package cn.j1angvei.castk2.qc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * a Object store info retrieved from FastQC zip report
 * Created by Wayne on 4/8 2017.
 */
public class QCInfo {
    private long totalReads;//totalReads reads
    private int length;//reads length
    private int percentGC;// GC percentage
    private int headCrop;//the number of how many base show cut from the start
    private Map<String, String> overrepresentedSeq;//overrepresented sequences, sequence as key, sequence name as value
    private Set<Adapter> adapter;//adapters exceed 5% of all sequences
    private String faFilePath;//store overrepresented sequences in FASTA/FA format
    private String phred; // phred33 or phred64

    public QCInfo() {
        overrepresentedSeq = new HashMap<>();
        adapter = new HashSet<>();
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

    public Map<String, String> getOverrepresentedSeq() {
        return overrepresentedSeq;
    }

    public void setOverrepresentedSeq(Map<String, String> overrepresentedSeq) {
        this.overrepresentedSeq = overrepresentedSeq;
    }

    public Set<Adapter> getAdapter() {
        return adapter;
    }

    public void setAdapter(Set<Adapter> adapter) {
        this.adapter = adapter;
    }

    public String getFaFilePath() {
        return faFilePath;
    }

    public void setFaFilePath(String faFilePath) {
        this.faFilePath = faFilePath;
    }

    public String getPhred() {
        return phred;
    }

    public void setPhred(String phred) {
        this.phred = phred;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
