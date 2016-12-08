package cn.j1angvei.castk2.conf;

import java.util.List;

/**
 * Created by mjian on 2016/11/29.
 */
public class Config {
    private Platform platform;
    private List<Software> software;
    private Directory directory;

    public Config() {
    }

    public Platform getPlatform() {
        return platform;
    }

    public List<Software> getSoftware() {
        return software;
    }

    public Directory getDirectory() {
        return directory;
    }

    @Override
    public String toString() {
        return "Config{" +
                "platform=" + platform +
                ", software=" + software +
                ", directory=" + directory +
                '}';
    }
}
