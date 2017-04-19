package cn.j1angvei.castk2.input;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by mjian on 2016/11/29.
 */
public class Genome {
    private int code;
    private String name;
    private String size;
    private String fasta;
    private String annotation;

    public Genome() {
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setFasta(String fasta) {
        this.fasta = fasta;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


    public String getFasta() {
        return fasta;
    }

    public String getAnnotation() {
        return annotation;
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
