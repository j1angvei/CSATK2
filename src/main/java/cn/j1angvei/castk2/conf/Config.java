package cn.j1angvei.castk2.conf;

/**
 * a java bean support with conf.json file
 * Created by mjian on 2016/11/29.
 */
public class Config {
    private String java;
    private String python;
    private String perl;
    private int thread;
    private int mappingQuality;

    public Config() {
    }

    public String getJava() {
        return java;
    }

    public void setJava(String java) {
        this.java = java;
    }

    public String getPython() {
        return python;
    }

    public void setPython(String python) {
        this.python = python;
    }

    public String getPerl() {
        return perl;
    }

    public void setPerl(String perl) {
        this.perl = perl;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public int getMappingQuality() {
        return mappingQuality;
    }

    public void setMappingQuality(int mappingQuality) {
        this.mappingQuality = mappingQuality;
    }

    @Override
    public String toString() {
        return "Config{" +
                '}';
    }
}
