package cn.j1angvei.castk2.conf;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by mjian on 2016/11/29.
 */
public class Experiment implements Comparable<Experiment> {
    private String code;
    private String fastq1;
    private String fastq2;
    private String control;
    private int genomeCode;
    private boolean broadPeak;

    public Experiment() {
    }

    public String getCode() {
        return code;
    }

    public String getFastq1() {
        return fastq1;
    }

    public String getFastq2() {
        return fastq2;
    }

    public String getControl() {
        return control;
    }

    public int getGenomeCode() {
        return genomeCode;
    }

    public boolean isBroadPeak() {
        return broadPeak;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setFastq1(String fastq1) {
        this.fastq1 = fastq1;
    }

    public void setFastq2(String fastq2) {
        this.fastq2 = fastq2;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public void setGenomeCode(int genomeCode) {
        this.genomeCode = genomeCode;
    }

    public void setBroadPeak(boolean broadPeak) {
        this.broadPeak = broadPeak;
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

    @Override
    public int compareTo(Experiment o) {
        return code.compareTo(o.getCode());
    }
}
