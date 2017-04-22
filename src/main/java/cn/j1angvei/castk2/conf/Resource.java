package cn.j1angvei.castk2.conf;

/**
 * Created by Wayne on 3/10 0010.
 */
public enum Resource {
    CONFIG("config.json"),
    INPUT("input.json"),
    ADAPTER("adapters.fa"),
    SPECIES("species.properties"),
    BROAD_PEAKS("broadPeaks.json"),
    TEMPLATE_HTML("template.html");

    String fileName;

    Resource(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
