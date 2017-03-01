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

/**
 * Created by mjian on 2016/11/29.
 */
public class ConfUtil {
    private static final Gson GSON = new Gson();
    private static ConfUtil INSTANCE;
    private Config config;
    private Input input;

    private ConfUtil() {
        try {
            config = GSON.fromJson(FileUtil.readConfig(), Config.class);
        } catch (JsonSyntaxException e) {
            System.err.println("ERROR: something wrong with " + SwUtil.CONFIG_JSON + ", go check it!");
        }
        try {
            input = GSON.fromJson(FileUtil.readInput(), Input.class);
        } catch (JsonSyntaxException e) {
            System.err.println("ERROR: something wrong with " + SwUtil.INPUT_JSON + ", go check it!");
        }
    }

    public static ConfUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfUtil();
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
        return getDirectory(SubType.ARCHIVE) + software.getArchive();
    }

    public String getSoftwareFolder(SwType type) {
        Software software = config.getSoftware().get(type.ordinal());
        return getDirectory(SubType.SOFTWARE) + software.getFolder();
    }

    public String getSoftwareExecutable(SwType type) {
        Software software = config.getSoftware().get(type.ordinal());
        return getSoftwareFolder(type) + software.getExecutable();
    }

    public String getDirectory(SubType type) {
        int index = type.ordinal();
        return FileUtil.getWorkDir() + config.getDirectory().getSub()[index] + File.separator;
    }

    public String getDirectory(OutType type) {
        int index = type.ordinal();
        return getDirectory(SubType.OUTPUT) + config.getDirectory().getOut()[index] + File.separator;
    }

    public String getLib(LibType type) {
        return getDirectory(SubType.LIB) + type.getFileName();
    }

    public Input getInput() {
        return input;
    }

    public List<Genome> getGenomes() {
        return input.getGenome();
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
        return input.getExperiment();
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
