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

    public static boolean isValid(String str) {
        return !isInvalid(str);
    }

    public static boolean isInvalid(String str) {
        return str == null || str.isEmpty() || str.matches("^$") || str.toLowerCase().equals("null");
    }

//    public static Double[] strArrayToDouble(String[] in) {
//
//    }
}
