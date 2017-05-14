package cn.j1angvei.castk2;

import cn.j1angvei.castk2.conf.*;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static cn.j1angvei.castk2.conf.Directory.Sub;

/**
 * Created by mjian on 2016/11/29.
 */
public class ConfigInitializer {
    private static ConfigInitializer INSTANCE;
    private Config config;
    private Input input;

    private ConfigInitializer(Gson gson) {
        try {
            config = gson.fromJson(FileUtil.readFromConfigFolder(Resource.CONFIG), Config.class);
            input = gson.fromJson(FileUtil.readFromConfigFolder(Resource.INPUT), Input.class);
            Collections.sort(input.getExperiments());
            Collections.sort(input.getGenomes());
        } catch (JsonSyntaxException e) {
            System.err.println("Error with " + Resource.CONFIG + " or " + Resource.INPUT + ", go check it!");
        }
    }

    public static synchronized ConfigInitializer getInstance() {
        if (INSTANCE == null) {
            Gson gson = GsonUtil.getGson();
            INSTANCE = new ConfigInitializer(gson);
        }
        return INSTANCE;
    }

    public static String getPath(Sub sub) {
        return FileUtil.getWorkDir() + sub.getDirName() + File.separator;
    }

    public static String getPath(Directory.Out out) {
        return getPath(Directory.Sub.OUTPUT) + out.getDirName() + File.separator;
    }

    public Config getConfig() {
        return config;
    }

    public String getJava() {
        return config.getJava();
    }

    public String getPerl() {
        return config.getPerl();
    }

    public String getPython() {
        return config.getPython();
    }

    public String getSwArchive(Software sw) {
        return getPath(Sub.ARCHIVE) + sw.getArchive();
    }

    public String getSwDestFolder(Software sw) {
        return getPath(Sub.SOFTWARE) + sw.getDestFolder() + File.separator;
    }

    public String getSwExecutable(Software sw) {
        return getSwDestFolder(sw) + sw.getExecutable();
    }

    public Input getInput() {
        return input;
    }

    public List<Genome> getGenomes() {
        return input.getGenomes();
    }

    public Genome getGenome(int genomeCode) {
        for (Genome genome : getGenomes()) {
            if (genome.getCode() == genomeCode) {
                return genome;
            }
        }
        throw new IllegalArgumentException("No Genome " + genomeCode + " found!");
    }

    public List<Experiment> getExperiments() {
        return input.getExperiments();
    }

    public Experiment getExperiment(String expCode) {
        for (Experiment experiment : getExperiments()) {
            if (expCode.equals(experiment.getCode())) {
                return experiment;
            }
        }
        throw new IllegalArgumentException("No Experiment " + expCode + " found!");
    }

}
