package cn.j1angvei.castk2.html;

import cn.j1angvei.castk2.panther.GoType;
import cn.j1angvei.castk2.util.FileUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wayne on 5/6 0006.
 */
public class DataPlot {
    private String statDir;

    public DataPlot(String statDir) {
        this.statDir = statDir;
    }

    public String qcBarChart(String[][] table) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] aTable : table) {
            //add all reads
            String code = aTable[0];
            String labelAll = "All", labelFiltered = "Filtered";
            double allReads = Double.parseDouble(aTable[2].replaceAll(",", "")),
                    filteredReads = Double.parseDouble(aTable[3].replaceAll(",", ""));
            dataset.addValue(allReads, labelAll, code);
            //add filtered reads
            dataset.addValue(filteredReads, labelFiltered, code);
        }
        JFreeChart barChart = ChartFactory.createBarChart(
                "Quality Control",
                "Experiments",
                "Reads count",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false
        );
        setBackground(barChart);
        File qcBarChartPng = new File(statDir + "qc_bar_chart.png");
        try {
            int[] size = getWidthAndHeight(table.length);
            ChartUtilities.saveChartAsPNG(qcBarChartPng, barChart, size[0], size[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileUtil.encodeToBase64(qcBarChartPng.getAbsolutePath());
    }

    public String alignmentBarChart(String[][] table) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] label = {"All", "Mapped", "Rmdup", "Unique"};
        for (String[] item : table) {
            String code = item[0];
            for (int i = 0; i < label.length; i++) {
                double value = Double.parseDouble(item[i + 1].replaceAll(",", ""));
                dataset.addValue(value, label[i], code);
            }
        }
        JFreeChart alignBarChart = ChartFactory.createBarChart(
                "Alignment",
                "Experiments",
                "Reads count",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false
        );
        setBackground(alignBarChart);
        File pngAlignment = new File(statDir + "alignment_bar_chart.png");
        int[] size = getWidthAndHeight(table.length);
        try {
            ChartUtilities.saveChartAsPNG(pngAlignment, alignBarChart, size[0], size[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileUtil.encodeToBase64(pngAlignment.getAbsolutePath());
    }

    public String[] peakCallingBarChart(String[][] table) {
        DefaultCategoryDataset avgDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset countDataset = new DefaultCategoryDataset();

        String avgLabel = "Length", countLabel = "Count";
        for (String[] item : table) {
            String code = item[0];
            double avgLen = Double.parseDouble(item[2].replaceAll(",", ""));
            avgDataset.addValue(avgLen, avgLabel, code);
            double count = Double.parseDouble(item[3].replaceAll(",", ""));
            countDataset.addValue(count, countLabel, code);
        }
        File avgPeakLenPng = new File(statDir + "peak_length.png");
        File peakCountPng = new File(statDir + "peak_count.png");

        JFreeChart avgPeakLenBarChart = ChartFactory.createBarChart(
                "Peak Average Length",
                "Experiments",
                "Peak length",
                avgDataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false
        );
        setBackground(avgPeakLenBarChart);
        JFreeChart peakCountBarChart = ChartFactory.createBarChart(
                "Peak Count",
                "Experiments",
                "Peak count",
                countDataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false
        );
        setBackground(peakCountBarChart);
        int[] size = getWidthAndHeight(table.length);
        try {
            ChartUtilities.saveChartAsPNG(avgPeakLenPng, avgPeakLenBarChart, size[0], size[1]);
            ChartUtilities.saveChartAsPNG(peakCountPng, peakCountBarChart, size[0], size[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{
                FileUtil.encodeToBase64(avgPeakLenPng.getAbsolutePath()),
                FileUtil.encodeToBase64(peakCountPng.getAbsolutePath())
        };
    }

    public String[] annotationPieChart(String[] header, String[][] table) {
        DefaultPieDataset[] pieDatasets = new DefaultPieDataset[table.length];
        String[] annoEncodeArr = new String[table.length];
        for (int i = 0; i < table.length; i++) {
            String code = table[i][0];
            pieDatasets[i] = new DefaultPieDataset();
            for (int j = 1; j < table[i].length; j++) {
                pieDatasets[i].setValue(header[j],
                        Double.parseDouble(table[i][j].replaceAll(",", "")));
            }
            JFreeChart chart = ChartFactory.createPieChart(
                    code + " peak annotation",
                    pieDatasets[i],
                    true,
                    true,
                    false
            );
            setBackground(chart);
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}={2}\n{1}"));
            File file = new File(statDir + "peak_anno_pie_chart_" + code + ".png");
            int width = 480, height = 360;
            try {
                ChartUtilities.saveChartAsPNG(file, chart, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
            annoEncodeArr[i] = FileUtil.encodeToBase64(file.getAbsolutePath());
        }
        return annoEncodeArr;
    }

    public String[] goBarChart(String[][] table) {
        //organize data
        Arrays.sort(table, new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                if (!o1[1].equals(o2[1])) {
                    //sort go type first, such as BP<CC<MF
                    return o1[1].compareTo(o2[1]);
                } else if (!o1[0].equals(o2[0])) {
                    //then sort by sample code
                    return o1[0].compareTo(o2[0]);
                } else {
                    //at last sort by count
                    return Integer.parseInt(o2[3].replaceAll(",", "")) -
                            Integer.parseInt(o1[3].replaceAll(",", ""));
                }
            }
        });

        Map<GoType, DefaultCategoryDataset> datasetMap = new HashMap<>();
        for (GoType type : GoType.values()) {
            datasetMap.put(type, new DefaultCategoryDataset());
        }
        String[] base64 = new String[GoType.values().length];

        for (String[] item : table) {
            GoType type = GoType.fromDescription(item[1]);
            double count = Double.parseDouble(item[3].replaceAll(",", ""));
            if (count <= 3) continue;
            datasetMap.get(type).setValue(
                    count,
                    item[0],
                    item[2]
            );
        }
        int goIndex = 0;
        for (GoType goType : GoType.values()) {
            JFreeChart chart = ChartFactory.createBarChart(
                    goType.getDescription() + " Analysis",
                    "ID",
                    "Count",
                    datasetMap.get(goType),
                    PlotOrientation.HORIZONTAL,
                    true,
                    true,
                    false

            );
            setBackground(chart);
            File png = new File(statDir + goType.name().toLowerCase() + "_bar_chart.png");
            int[] size = getWidthAndHeight(datasetMap.get(goType).getColumnCount());
            try {
                ChartUtilities.saveChartAsPNG(png, chart, size[0], size[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            base64[goIndex++] = FileUtil.encodeToBase64(png.getAbsolutePath());
        }
        return base64;
    }

    private int[] getWidthAndHeight(int category) {
        int width = 80 * category;
        int height = 60 * category;
        return new int[]{width, height};
    }

    private void setBackground(JFreeChart chart) {
//        chart.getPlot().setBackgroundPaint(new Color(238, 238, 238));
        chart.getPlot().setBackgroundPaint(new Color(255, 255, 255));
    }
}
