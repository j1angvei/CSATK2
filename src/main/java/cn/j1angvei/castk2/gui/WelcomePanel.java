package cn.j1angvei.castk2.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Wayne on 4/23 0023.
 */
public class WelcomePanel extends JPanel {
    public WelcomePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.99;

        add(new JPanel(), gbc);
        gbc.gridy++;
        gbc.weighty = 0.01;

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;

        add(new CSATKTitlePanel(), gbc);
        gbc.gridy++;
        gbc.weighty = 0.5;

        add(new JLabel("Press Alt + T to open Tool menu, and use them."), gbc);

        gbc.gridy++;
        gbc.weighty = 0.99;
        add(new JPanel(), gbc);
    }
}
