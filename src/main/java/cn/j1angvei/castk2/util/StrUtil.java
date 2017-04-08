package cn.j1angvei.castk2.util;

/**
 * Created by j1angvei on 2/23 0023.
 */
public class StrUtil {
    public static String getSuffix(String fullName) {
        return fullName.substring(fullName.lastIndexOf('.') + 1, fullName.length());
    }

    public static String getPrefix(String fullName) {
        return fullName.substring(0, fullName.lastIndexOf('.'));
    }

    public static String encodingToPhred(String encoding) {
        String phred = "-phred64";
        if (encoding.startsWith("Encoding")) {
            if (encoding.contains("Sanger")) {
                phred = "-phred33";
            } else if (encoding.contains("Illumina")) {
                String[] segment = encoding.split("[ \t/]");
                float value = Float.parseFloat(segment[segment.length - 1]);
                if (value >= 1.8f) {
                    phred = "-phred33";
                }
            }
        }
        return phred;
    }
}
