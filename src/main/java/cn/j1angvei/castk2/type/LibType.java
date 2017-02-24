package cn.j1angvei.castk2.type;

/**
 * Created by j1angvei on 2/23 0023.
 */
public enum LibType {
    LIBBLAS("libblas.so"), LIBLAPACK("liblapack.so");

    private String fileName;


    LibType(String name) {
        fileName = name;
    }

    public String getFileName() {
        return fileName;
    }
}
