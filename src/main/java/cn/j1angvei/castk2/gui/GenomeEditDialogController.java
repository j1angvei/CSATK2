package cn.j1angvei.castk2.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Created by Wayne on 5/2 0002.
 */
public class GenomeEditDialogController {
    @FXML
    private TextField codeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField genomeSizeField;
    @FXML
    private TextField fastaField;
    @FXML
    private TextField gtfField;

    private Stage dialogStage;
    private GenomeModel genomeModel;
    private boolean okClicked = false;

    @FXML
    private void initialize() {

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setGenomeModel(GenomeModel genomeModel) {
        this.genomeModel = genomeModel;
        codeField.setText(String.valueOf(genomeModel.getCode()));
        nameField.setText(genomeModel.getName());
        genomeSizeField.setText(genomeModel.getSize());
        fastaField.setText(genomeModel.getFasta());
        gtfField.setText(genomeModel.getGtf());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            genomeModel.setCode(Integer.parseInt(codeField.getText()));
            genomeModel.setName(nameField.getText());
            genomeModel.setSize(genomeSizeField.getText());
            genomeModel.setFasta(fastaField.getText());
            genomeModel.setGtf(gtfField.getText());

            okClicked = true;
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
            errorMessage += "Genome code is invalid\n";
        }
        try {
            if (Integer.parseInt(codeField.getText()) <= 0) {
                errorMessage += "Genome code must be >0\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "Genome code must be an integer\n";
        }
        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "Genome name is invalid\n";
        }
        if (genomeSizeField.getText() == null || genomeSizeField.getText().length() == 0) {
            errorMessage += "Genome size is invalid\n";
        }
        if (fastaField.getText() == null || fastaField.getText().length() == 0) {
            errorMessage += "Genome fasta(reference) is invalid\n";
        }
        if (gtfField.getText() == null || gtfField.getText().length() == 0) {
            errorMessage += "Genome gtf(annotation) is invalid\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            GuiUtil.createAlert("Error!", errorMessage, Alert.AlertType.ERROR);
            return false;
        }
    }
}
