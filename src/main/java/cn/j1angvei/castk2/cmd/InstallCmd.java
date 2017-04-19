package cn.j1angvei.castk2.cmd;

import cn.j1angvei.castk2.type.Directory;
import cn.j1angvei.castk2.type.PfType;
import cn.j1angvei.castk2.type.SwType;
import cn.j1angvei.castk2.util.ConfUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j1angvei on 2016/11/30.
 */
public class InstallCmd {

    public static String[] install(SwType type) {
        String archive = ConfUtil.getInstance().getSoftwareArchive(type);
        String swSubDir = ConfUtil.getPath(Directory.Sub.SOFTWARE);
        String swFolder = ConfUtil.getInstance().getSoftwareFolder(type);
        List<String> cmd = new ArrayList<>();
        switch (type) {
            case FASTQC:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.addX(ConfUtil.getInstance().getSoftwareExecutable(type)));
                break;
            case BWA:
                cmd.add(OsCmd.unpack(archive, swSubDir));
                cmd.add(OsCmd.make(ConfUtil.getInstance().getSoftwareFolder(type)));
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
                        ConfUtil.getInstance().getPlatform(PfType.PYTHON),
                        swFolder);
                cmd.add(install);
                cmd.add(OsCmd.addPythonPath(swFolder));
                cmd.add(install);
                break;
            case HOMER:
                cmd.add(OsCmd.unpack(archive, swFolder));
                cmd.add(OsCmd.changeDir(swFolder));
                cmd.add(String.format("%s %s -make",
                        ConfUtil.getInstance().getPlatform(PfType.PERL),
                        "configureHomer.pl")
                );
                cmd.add(OsCmd.addX(ConfUtil.getInstance().getSoftwareExecutable(SwType.HOMER) + "*"));
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
