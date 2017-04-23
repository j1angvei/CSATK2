package cn.j1angvei.castk2.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by Wayne on 4/23 0023.
 */
public class CSATKMenuBar extends JMenuBar implements ActionListener {
    private HomeFrame homeFrame;

    public CSATKMenuBar(HomeFrame homeFrame) {
        this.homeFrame = homeFrame;

        //tool menu
        JMenu toolMenu = new JMenu("Tool");
        toolMenu.setMnemonic(KeyEvent.VK_T);

        //generate input.json in tool menu
        JMenuItem generateItem = new JMenuItem("Generate input.json");
        generateItem.setMnemonic(KeyEvent.VK_G);
        generateItem.setAccelerator(KeyStroke.getKeyStroke('G', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        generateItem.setActionCommand(CMD_GENERATE);
        generateItem.addActionListener(this);

        toolMenu.add(generateItem);
        toolMenu.addSeparator();

        //parse qc zip result of FastQC
        JMenuItem parseItem = new JMenuItem("Parse FastQC zip report");
        parseItem.setMnemonic(KeyEvent.VK_P);
        parseItem.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        parseItem.setActionCommand(CMD_PARSE);
        parseItem.addActionListener(this);

        toolMenu.add(parseItem);
        toolMenu.addSeparator();

        //do go & pathway analysis
        JMenuItem goItem = new JMenuItem("GO & Pathway analysis");
        goItem.setMnemonic(KeyEvent.VK_O);
        goItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        goItem.setActionCommand(CMD_GO);
        goItem.addActionListener(this);

        toolMenu.add(goItem);

        //add tool menu to menuBar
        add(toolMenu);

        //help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        //about menu item in help menu
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        aboutItem.setActionCommand(CMD_ABOUT);
        aboutItem.addActionListener(this);

        helpMenu.add(aboutItem);
        helpMenu.addSeparator();

        //exit menu item in help menu
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_E);
        exitItem.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitItem.setActionCommand(CMD_EXIT);
        exitItem.addActionListener(this);

        helpMenu.add(exitItem);

        //add help menu to menuBar
        add(helpMenu);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case CMD_PARSE:
                homeFrame.parseQCZip();
                break;
            case CMD_GENERATE:
                homeFrame.generateInputJson();
                break;
            case CMD_GO:
                homeFrame.goPathway();
                break;
            case CMD_ABOUT:
                homeFrame.about();
                break;
            case CMD_EXIT:
                homeFrame.exit();
        }
    }

    private static final String CMD_GENERATE = "generate";
    private static final String CMD_PARSE = "parse";
    private static final String CMD_GO = "go";
    private static final String CMD_ABOUT = "about";
    private static final String CMD_EXIT = "exit";
}
