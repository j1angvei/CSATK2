package cn.j1angvei.castk2.util;

import cn.j1angvei.castk2.conf.Config;
import cn.j1angvei.castk2.conf.Platform;
import cn.j1angvei.castk2.conf.Software;
import cn.j1angvei.castk2.input.Experiment;
import cn.j1angvei.castk2.input.Genome;
import cn.j1angvei.castk2.input.Input;
import cn.j1angvei.castk2.type.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.List;

import static cn.j1angvei.castk2.type.Directory.Sub;

/**
 * Created by mjian on 2016/11/29.
 */
public class ConfUtil {
    private static ConfUtil INSTANCE;
    private Config config;
    private Input input;

    private ConfUtil(Gson gson) {
        try {
            config = gson.fromJson(FileUtil.readFromConfigFolder(ResType.CONFIG), Config.class);
            input = gson.fromJson(FileUtil.readFromConfigFolder(ResType.INPUT), Input.class);
        } catch (JsonSyntaxException e) {
            System.err.println("Error with " + ResType.CONFIG + " or " + ResType.INPUT + ", go check it!");
        }
    }

    public static ConfUtil getInstance() {
        if (INSTANCE == null) {
            Gson gson = GsonUtil.getGson();
            INSTANCE = new ConfUtil(gson);
        }
        return INSTANCE;
    }

    public Config getConfig() {
        return config;
    }

    public String getPlatform(PfType type) {
        Platform pf = config.getPlatform();
        switch (type) {
            case JAVA:
                return pf.getJava();
            case PERL:
                return pf.getPerl();
            case PYTHON:
                return pf.getPython();
            case R:
                return pf.getR();
            default:
                throw new IllegalArgumentException("No Platform " + type + " found!");
        }
    }

    public Software getSoftware(SwType type) {
        int index = type.ordinal();
        return config.getSoftware().get(index);
    }

    public String getSoftwareArchive(SwType type) {
        Software software = config.getSoftware().get(type.ordinal());
        return getPath(Sub.ARCHIVE) + software.getArchive();
    }

    public String getSoftwareFolder(SwType type) {
        Software software = config.getSoftware().get(type.ordinal());
        return getPath(Sub.SOFTWARE) + software.getFolder();
    }

    public String getSoftwareExecutable(SwType type) {
        Software software = config.getSoftware().get(type.ordinal());
        return getSoftwareFolder(type) + software.getExecutable();
    }

    public static String getPath(Directory.Sub sub) {
        return FileUtil.getWorkDir() + sub.getName() + File.separator;
    }

    public static String getPath(Directory.Out out) {
        return getPath(Directory.Sub.OUTPUT) + out.getName() + File.separator;
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
