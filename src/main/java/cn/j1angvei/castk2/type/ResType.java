package cn.j1angvei.castk2.type;

/**
 * Created by Wayne on 3/10 0010.
 */
public enum ResType {
    CONFIG("config.json"), INPUT("input.json"), ADAPTER("adapters.txt"), SPECIES("species.txt");

    String fileName;

    ResType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
