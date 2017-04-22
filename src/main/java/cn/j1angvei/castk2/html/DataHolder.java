package cn.j1angvei.castk2.html;

import cn.j1angvei.castk2.stat.StatType;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.StrUtil;

import java.util.*;

/**
 * Created by Wayne on 4/22 0022.
 */
public class DataHolder {

    private String statDir;
    private Map<StatType, String[][]> dataMap;

    public static synchronized DataHolder getInstance(String dir) {
        return new DataHolder(dir);
    }

    private DataHolder(String dir) {
        statDir = dir;
        readData();
    }

    private void readData() {
        dataMap = new HashMap<>();
        for (StatType type : StatType.values()) {
            dataMap.put(type, convert(statDir + type.getResFileName()));
        }
    }

    private String[][] convert(String filePath) {
        List<String> lines = FileUtil.readLines(filePath);
        int row = lines.size();
        int column = lines.get(0).split("\t").length;
        String[][] stat = new String[row][column];
        for (int i = 0; i < lines.size(); i++) {
            if (StrUtil.isValid(lines.get(i))) {
                stat[i] = lines.get(i).split("\t");
            }
        }
        return stat;
    }

    public Map<StatType, String[][]> getDataMap() {
        return dataMap;
    }
}
