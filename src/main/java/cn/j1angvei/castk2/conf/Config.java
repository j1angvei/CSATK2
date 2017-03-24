package cn.j1angvei.castk2.conf;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mjian on 2016/11/29.
 */
public class Config {
    @SerializedName("platform")
    private Platform platform;
    @SerializedName("software")
    private List<Software> software;
    @SerializedName("directory")
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
