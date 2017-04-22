package cn.j1angvei.castk2.stat;

import cn.j1angvei.castk2.util.FileUtil;

/**
 * Created by Wayne on 4/22 0022.
 */
public class MotifColumn implements Column {
    private static final String SUFFIX = ".logo.png";
    private String expCode;
    private String filePrefix;
    private int number;
    private String[] pngEncode;

    public MotifColumn(String expCode, String filePrefix, int number) {
        this.expCode = expCode;
        this.filePrefix = filePrefix;
        this.number = number;
        pngEncode = new String[number];
        for (int i = 1; i <= number; i++) {
            pngEncode[i-1] = FileUtil.encodeToBase64(filePrefix + i + SUFFIX);
        }

    }

    public String getExpCode() {
        return expCode;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public int getNumber() {
        return number;
    }

    public String[] getPngEncode() {
        return pngEncode;
    }

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(expCode);
        for (int i = 0; i < number; i++) {
            builder.append("\t")
                    .append(pngEncode[i]);
        }
        return builder.toString()+"\n";

    }
}
