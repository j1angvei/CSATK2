package cn.j1angvei.castk2.run;

import cn.j1angvei.castk2.util.FileUtil;

import java.io.*;

/**
 * Created by j1angvei on 2016/11/30.
 */
public class Executor {
    public static int execute(String expCode, String function, String... cmd) {
        String fileName = expCode + "_" + function + ".sh";
        File script = createScript(fileName, cmd);
        int exitValue = 0;
        try {
            ProcessBuilder builder = new ProcessBuilder("sh", script.toString());
            builder.redirectErrorStream(true);
            Process process = builder.start();
            output(process.getInputStream());
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
        FileUtil.writeFile(content, fileName);
        return new File(fileName);
    }

    private static void output(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
