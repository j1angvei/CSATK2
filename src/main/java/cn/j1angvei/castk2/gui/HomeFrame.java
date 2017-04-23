package cn.j1angvei.castk2.gui;

import cn.j1angvei.castk2.util.FileUtil;

import javax.swing.*;

/**
 * Created by Wayne on 4/23 0023.
 */
public class HomeFrame extends JFrame {
    private JTabbedPane toolTabs;
    private WelcomePanel welcomePanel;

    public static void initiate() {
        HomeFrame homeFrame = new HomeFrame();

        homeFrame.setVisible(true);
    }

    private HomeFrame() {
        //set title in title bar
        setTitle("CSATK GUI");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //set logo in title bar
        ImageIcon smallLogo = new ImageIcon(FileUtil.readResouce("logo_48.png"));
        setIconImage(smallLogo.getImage());

        //set window size
        setSize(800, 600);
        setLocationRelativeTo(null);
        //display all configuration
        toolTabs = new JTabbedPane(JTabbedPane.TOP);

        welcomePanel = new WelcomePanel();
        setContentPane(welcomePanel);
        //set menu
        setJMenuBar(new CSATKMenuBar(this));

    }

    public void generateInputJson() {
    }

    public void parseQCZip() {
    }

    public void goPathway() {
    }

    public void about() {
    }

    public void exit() {
        System.exit(0);
    }


}
