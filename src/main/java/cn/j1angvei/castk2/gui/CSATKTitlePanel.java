package cn.j1angvei.castk2.gui;

import cn.j1angvei.castk2.util.FileUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Wayne on 4/23 0023.
 */
public class CSATKTitlePanel extends JPanel {
    public CSATKTitlePanel() {
        setLayout(new BorderLayout());

        //add logo panel
        ImageIcon logo = new ImageIcon(FileUtil.readResouce("logo_144.png"));
        JPanel logoPanel = new JPanel();
        logoPanel.add(new JLabel("", logo, JLabel.CENTER));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        add(logoPanel, BorderLayout.WEST);

        //add title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        //title label
        JLabel titleLabel = new JLabel("ChIP-Seq Analysis Toolkit");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setForeground(new Color(200, 0, 0));
        titlePanel.add(titleLabel);

        //version label
        JLabel versionLabel = new JLabel("Version: 2-170419");
        versionLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        versionLabel.setForeground(new Color(0, 0, 200));
        titlePanel.add(versionLabel);

        //author label
        JLabel authorLabel = new JLabel("Author: Man Jiangwei");
        authorLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        authorLabel.setForeground(new Color(0, 0, 0));
        titlePanel.add(authorLabel);

        add(titlePanel, BorderLayout.CENTER);
    }
}
