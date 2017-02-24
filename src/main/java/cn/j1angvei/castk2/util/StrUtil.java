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
}
