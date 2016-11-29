package cn.j1angvei.castk2.conf;

import java.io.File;

/**
 * Created by mjian on 2016/11/29.
 */
public class Software {
    private String name;
    private String archive;
    private String folder;
    private String executable;

    public Software() {
    }

    public String getName() {
        return name;
    }

    public String getArchive() {
        return archive;
    }

    public String getFolder() {
        return folder + File.separator;
    }

    public String getExecutable() {
        return executable;
    }

    @Override
    public String toString() {
        return "Software{" +
                "name='" + name + '\'' +
                ", archive='" + archive + '\'' +
                ", folder='" + folder + '\'' +
                ", executable='" + executable + '\'' +
                '}';
    }
}
