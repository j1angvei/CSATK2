package cn.j1angvei.castk2.run;

import cn.j1angvei.castk2.type.SubType;
import cn.j1angvei.castk2.util.ConfUtil;
import cn.j1angvei.castk2.util.FileUtil;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by j1angvei on 2016/11/30.
 */
public class Executor {
    private static final ConfUtil CONF = ConfUtil.getInstance();

    public static int execute(String prefixName, String... cmd) {
        String timestamp = FileUtil.getTimestamp();
        String scriptFile = CONF.getDirectory(SubType.SCRIPT) + timestamp + "_" + prefixName + ".sh";
        String logFile = CONF.getDirectory(SubType.LOG) + timestamp + "_" + prefixName + ".log";
        File script = createScript(scriptFile, cmd);
        int exitValue = 0;
        try {
            ProcessBuilder builder = new ProcessBuilder("sh", script.toString());
            builder.redirectErrorStream(true);
            Process process = builder.start();
            String log = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
            FileUtil.overwriteFile(log, logFile);
            exitValue = process.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return exitValue;
    }

    private static File createScript(String fileName, String... cmd) {
        String content = "";
        for (String c : cmd) {
            content += c + "\n";
        }
        FileUtil.overwriteFile(content, fileName);
        return new File(fileName);
    }
}
