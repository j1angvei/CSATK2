package cn.j1angvei.castk2.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Created by Wayne on 5/3 0003.
 */
public class ExperimentEditDialogController {
    @FXML
    private TextField codeField;
    @FXML
    private TextField fastq1Field;
    @FXML
    private TextField fastq2Field;
    @FXML
    private TextField controlField;
    @FXML
    private TextField genomeCodeField;
    @FXML
    private CheckBox broadPeakBox;

    private Stage dialogStage;
    private ExperimentModel experimentModel;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setExperimentModel(ExperimentModel expModel) {
        this.experimentModel = expModel;
        codeField.setText(expModel.getCode());
        fastq1Field.setText(expModel.getFastq1());
        fastq2Field.setText(expModel.getFastq2());
        controlField.setText(expModel.getControl());
        genomeCodeField.setText(String.valueOf(expModel.getGenomeCode()));
        broadPeakBox.setSelected(expModel.isBroadPeak());
        codeField.setText(expModel.getCode());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            experimentModel.setCode(codeField.getText());
            experimentModel.setFastq1(fastq1Field.getText());
            experimentModel.setFastq2(fastq2Field.getText());
            experimentModel.setControl(controlField.getText());
            experimentModel.setGenomeCode(Integer.parseInt(genomeCodeField.getText()));
            experimentModel.setBroadPeak(broadPeakBox.isSelected());

            okClicked = true;
            dialogStage.close();
        } else {
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (codeField.getText() == null || codeField.getText().length() == 0) {
            errorMessage += "Experiment code is invalid\n";
        }
        if (fastq1Field.getText() == null || fastq1Field.getText().length() == 0) {
            errorMessage += "Experiment fastq 1 is invalid\n";
        }
        if (fastq2Field.getText() == null || fastq2Field.getText().length() == 0) {
            errorMessage += "Experiment fastq 2 is invalid\n";
        }
        if (controlField.getText() == null || controlField.getText().length() == 0) {
            errorMessage += "Experiment control is invalid\n";
        }
        if (genomeCodeField.getText() == null || genomeCodeField.getText().length() == 0) {
            errorMessage += "Experiment's genome code is invalid\n";
        }
        try {
            if (Integer.parseInt(genomeCodeField.getText()) <= 0) {
                errorMessage += "Experiment's genome code must be >0\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "Experiment's genome code must be an integer\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            GuiUtil.createAlert("Error!", errorMessage, Alert.AlertType.ERROR);
            return false;
        }


    }
}
