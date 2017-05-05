package cn.j1angvei.castk2.cmd;

import java.util.List;

/**
 * Created by Wayne on 5/5 0005.
 */
public interface Command {
    enum Type {
        OS, INSTALL, SHELL
    }

    List<String> getCommands(Type type);
}
