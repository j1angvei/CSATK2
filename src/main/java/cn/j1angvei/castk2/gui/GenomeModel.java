package cn.j1angvei.castk2.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Wayne on 4/24 0024.
 */
public class GenomeModel {
    private final IntegerProperty code;
    private final StringProperty name;
    private final StringProperty size;
    private final StringProperty fasta;
    private final StringProperty gtf;

    public GenomeModel() {
        this(0, null, null, null, null);
    }

    public GenomeModel(GenomeModel oldModel) {
        this(oldModel.getCode(), oldModel.getName(), oldModel.getSize(), oldModel.getFasta(), oldModel.getGtf());
    }

    public GenomeModel(int code, String name, String size, String fasta, String gtf) {
        this.code = new SimpleIntegerProperty(code);
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleStringProperty(size);
        this.fasta = new SimpleStringProperty(fasta);
        this.gtf = new SimpleStringProperty(gtf);
    }

    public int getCode() {
        return code.get();
    }

    public void setCode(int code) {
        this.code.set(code);
    }

    public IntegerProperty codeProperty() {
        return code;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public String getFasta() {
        return fasta.get();
    }

    public void setFasta(String fasta) {
        this.fasta.set(fasta);
    }

    public StringProperty fastaProperty() {
        return fasta;
    }

    public String getGtf() {
        return gtf.get();
    }

    public void setGtf(String gtf) {
        this.gtf.set(gtf);
    }

    public StringProperty gtfProperty() {
        return gtf;
    }
}
