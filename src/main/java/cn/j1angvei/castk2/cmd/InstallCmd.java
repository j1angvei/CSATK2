package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.conf.Directory;
import cn.j1angvei.castk2.conf.Software;
import cn.j1angvei.castk2.ConfigInitializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j1angvei on 2016/11/30.
 */
public class InstallCmd {

    public static String[] install(Software sw) {
        String archive = ConfigInitializer.getInstance().getSwArchive(sw);
        String swSubDir = ConfigInitializer.getPath(Directory.Sub.SOFTWARE);
        String swFolder = ConfigInitializer.getInstance().getSwDestFolder(sw);
        List<String> cmd = new ArrayList<>();
        switch (sw) {
            case FASTQC:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.addX(ConfigInitializer.getInstance().getSwExecutable(sw)));
                break;
            case BWA:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.make(ConfigInitializer.getInstance().getSwDestFolder(sw)));
                break;
            case SAMTOOLS:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.changeDir(swFolder));
                cmd.add(OsCmd.makeInstall(swFolder));
                break;
            case MACS2:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.changeDir(swFolder));
                String install = String.format("%s setup.py install --prefix %s",
                        ConfigInitializer.getInstance().getPython(),
                        swFolder);
                cmd.add(install);
                cmd.add(OsCmd.addPythonPath(swFolder));
                cmd.add(install);
                break;
            case HOMER:
                cmd.add(OsCmd.unpack(archive, swFolder));
                cmd.add(OsCmd.changeDir(swFolder));
                cmd.add(String.format("%s %s -make",
                        ConfigInitializer.getInstance().getPerl(),
                        "configureHomer.pl")
                );
                cmd.add(OsCmd.addX(ConfigInitializer.getInstance().getSwExecutable(Software.HOMER) + "*"));
                break;
            case TRIMMOMATIC:
            case QUALIMAP:
            case WEBLOGO:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                break;
            default:
                break;
        }
        return cmd.toArray(new String[cmd.size()]);
    }
}
