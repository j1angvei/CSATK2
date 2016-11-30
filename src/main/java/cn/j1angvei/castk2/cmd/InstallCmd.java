package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.type.PfType;
import cn.j1angvei.castk2.type.SubType;
import cn.j1angvei.castk2.type.SwType;
import cn.j1angvei.castk2.util.ConfUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j1angvei on 2016/11/30.
 */
public class InstallCmd {
    private static ConfUtil CONF = ConfUtil.getInstance();

    public static List<String> install(SwType type) {
        String archive = CONF.getSoftwareArchive(type);
        String swSubDir = CONF.getSubDirectory(SubType.SOFTWARE);
        String swFolder = CONF.getSoftwareFolder(type);
        List<String> cmd = new ArrayList<>();
        switch (type) {
            case FASTQC:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.addX(CONF.getSoftwareExecutable(type)));
                break;
            case BWA:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.make(CONF.getSoftwareFolder(type)));
                break;
            case TRIMMOMATIC:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                break;
            case SAMTOOLS:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.changeDir(swFolder));
                cmd.add(OsCmd.makeInstall(swFolder));
                break;
            case MACS2:
                cmd.add(OsCmd.addPythonPath(swFolder));
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.changeDir(swFolder));
                cmd.add(String.format("%s setup.py install --prefix %s",
                        CONF.getPlatform(PfType.PYTHON),
                        swFolder)
                );
                break;
            case HOMER:
                cmd.add(OsCmd.unpack(archive, swFolder));
                cmd.add(OsCmd.changeDir(swFolder));
                cmd.add(String.format("%s %s -make",
                        CONF.getSoftwareExecutable(type),
                        "configureHomer.pl")
                );
                break;
            case QUALIMAP:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                break;
            default:
                break;
        }
        return cmd;
    }
}
