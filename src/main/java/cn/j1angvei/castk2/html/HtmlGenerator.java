package cn.j1angvei.castk2.html;

import cn.j1angvei.castk2.ConfigInitializer;
import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.stat.StatType;
import cn.j1angvei.castk2.util.FileUtil;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.text.DateFormat;
import java.util.*;

/**
 * Created by Wayne on 4/22 0022.
 */
public class HtmlGenerator {
    private Map<StatType, String[][]> dataMap;

    public static HtmlGenerator getInstance(String statDir) {
        return new HtmlGenerator(statDir);
    }

    private HtmlGenerator(String statDir) {
        dataMap = DataHolder.getInstance(statDir).getDataMap();
    }

    public void generate() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setSuffix(".html");
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        final Context context = new Context(Locale.getDefault());
        String title = "CSATK Report";
        String date = DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA).format(new Date());
        context.setVariable("title", title);
        context.setVariable("date", date);
        for (StatType type : StatType.values()) {
            fillData(type, context);
        }
        final String html = engine.process("template", context);
        FileUtil.overwriteFile(html, ConfigInitializer.getPath(Directory.Out.HTML) + "csatk_result.html");

    }

    private void fillData(StatType type, Context context) {
        String[][] dataOriginal = dataMap.get(type);
        String[] header = dataOriginal[0];
        int row = dataOriginal.length;
        int column = dataOriginal[0].length;
        String[][] dataNoHeader = new String[row - 1][column];
        for (int i = 1; i < row; i++) {
            System.arraycopy(dataOriginal[i], 0, dataNoHeader[i - 1], 0, column);
        }
        try {
            Arrays.sort(dataNoHeader, new Comparator<String[]>() {
                @Override
                public int compare(String[] o1, String[] o2) {
                    if (o1 == null) return 1;
                    if (o2 == null) return -1;
                    return o1[0].compareTo(o2[0]);
                }
            });
        } catch (NullPointerException e) {
            System.out.printf("type\t%s\n", type);
        }

        String headerKey = "", dataKey = "";
        switch (type) {
            case RAW_DATA:
                headerKey = "raw_header";
                dataKey = "raw_data";
                break;
            case QUALITY_CONTROL:
                headerKey = "qc_header";
                dataKey = "qc_data";
                break;
            case ALIGNMENT:
                headerKey = "align_header";
                dataKey = "align_data";
                break;
            case PEAK_CALL:
                headerKey = "call_header";
                dataKey = "call_data";
                break;
            case PEAK_ANNO:
                headerKey = "anno_header";
                dataKey = "anno_data";
                break;
            case GENE_ONTOLOGY:
                headerKey = "go_header";
                dataKey = "go_data";
                break;
            case PATHWAY:
                headerKey = "pathway_header";
                dataKey = "pathway_data";
                break;
            case MOTIF:
                headerKey = "motif_header";
                dataKey = "motif_data";
            default:
                break;
        }
        context.setVariable(headerKey, header);
        context.setVariable(dataKey, dataNoHeader);
    }

}
