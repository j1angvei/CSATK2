package cn.j1angvei.castk2.util;

import cn.j1angvei.castk2.qc.QCInfo;
import com.google.gson.Gson;

/**
 * using gson convert object to text, or vice versa
 * Created by Wayne on 4/8 2018.
 */
public class GsonUtil {
    private static final Gson GSON = new Gson();

    public static String toJson(QCInfo qcInfo) {
        return GSON.toJson(qcInfo);
    }
}
