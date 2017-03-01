package cn.j1angvei.castk2.peaks;

import cn.j1angvei.castk2.anno.Region;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wayne on 3/1 0001.
 */
public class Differentiate {
    public static void function(String[] args) {
        if (args.length != 2) {
            System.err.println("Missing arguments input and thermo");
            return;
        }
        List<Region> input = readRegion(args[0]);
        List<Region> thermo = readRegion(args[1]);
        long commonInput = calculate(input, thermo);
        long commonThermo = calculate(thermo, input);
        System.out.printf("common in Input: %d\ncommon in Thermo: %d\n", commonInput, commonThermo);

    }

    private static List<Region> readRegion(String fileName) {
        List<Region> regions = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] info = line.split("\t");
                Region region = new Region(info[0], Long.parseLong(info[1]), Long.parseLong(info[2]));
                regions.add(region);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return regions;
    }

    private static long calculate(List<Region> first, List<Region> second) {
        long common = 0;
        for (Region f : first) {
            for (Region s : second) {
                if (isOverlapped(f, s)) common++;
            }
        }
        return common;
    }

    private static boolean isOverlapped(Region a, Region b) {
        long lenA = a.getEnd() - a.getStart();
        long lenB = b.getEnd() - b.getStart();
        if (lenA < lenB && Region.isOverlapped(a, b)) {
            return lenB > lenA * 1.5f;
        } else if (lenB < lenA && Region.isOverlapped(a, b)) {
            return lenA > lenB * 1.5f;
        } else return false;
    }
}
