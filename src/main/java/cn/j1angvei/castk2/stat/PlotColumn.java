package cn.j1angvei.castk2.stat;

import cn.j1angvei.castk2.util.FileUtil;

/**
 * @author j1angvei
 * @version 3.0
 * @since 2017-05-09 22:31
 */
public class PlotColumn implements Column {
    private String exeCode;
    private String imgEncode;

    public PlotColumn(String exeCode, String pngPath) {
        this.exeCode = exeCode;
        this.imgEncode = FileUtil.encodeToBase64(pngPath);
    }

    public String getExeCode() {
        return exeCode;
    }

    public void setExeCode(String exeCode) {
        this.exeCode = exeCode;
    }

    public String getImgEncode() {
        return imgEncode;
    }

    public void setImgEncode(String imgEncode) {
        this.imgEncode = imgEncode;
    }

    @Override
    public String getHeader() {
        return "Sample\tImage";
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\n", exeCode, imgEncode);
    }
}
