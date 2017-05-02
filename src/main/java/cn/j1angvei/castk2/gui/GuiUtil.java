package cn.j1angvei.castk2.gui;

import javafx.scene.control.Alert;

/**
 * Created by Wayne on 5/2 0002.
 */
public class GuiUtil {
    public static void createAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
