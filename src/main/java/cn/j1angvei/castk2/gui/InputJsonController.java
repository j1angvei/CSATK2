package cn.j1angvei.castk2.gui;

import cn.j1angvei.castk2.conf.Input;
import cn.j1angvei.castk2.conf.Resource;
import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.GsonUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.io.File;


/**
 * Created by Wayne on 4/24 0024.
 */
public class InputJsonController {
    //genome tab
    @FXML
    private TableView<GenomeModel> genomeModelTable;
    @FXML
    private TableColumn<GenomeModel, Integer> codeGenomeCol;
    @FXML
    private TableColumn<GenomeModel, String> nameGenomeCol;
    @FXML
    private TableColumn<GenomeModel, String> sizeGenomeCol;
    @FXML
    private TableColumn<GenomeModel, String> fastaGenomeCol;
    @FXML
    private TableColumn<GenomeModel, String> gtfGenomeCol;
    @FXML
    private Label codeGenomeLabel;
    @FXML
    private Label nameGenomeLabel;
    @FXML
    private Label sizeGenomeLabel;
    @FXML
    private Label fastaGenomeLabel;
    @FXML
    private Label gtfGenomeLabel;

    //experiment tab
    @FXML
    private TableView<ExperimentModel> expModelTable;
    @FXML
    private TableColumn<ExperimentModel, String> codeExpCol;
    @FXML
    private TableColumn<ExperimentModel, String> fastq1ExpCol;
    @FXML
    private TableColumn<ExperimentModel, String> fastq2ExpCol;
    @FXML
    private TableColumn<ExperimentModel, String> controlExpCol;
    @FXML
    private TableColumn<ExperimentModel, Integer> genomeCodeExpCol;
    @FXML
    private TableColumn<ExperimentModel, Boolean> broadPeakExpCol;
    @FXML
    private Label codeExpLabel;
    @FXML
    private Label fastq1ExpLabel;
    @FXML
    private Label fastq2ExpLabel;
    @FXML
    private Label controlExpLabel;
    @FXML
    private Label genomeCodeExpLabel;
    @FXML
    private Label broadPeakExpLabel;

    //reference to main application
    private MainApp mainApp;

    public InputJsonController() {
    }

    @FXML
    private void initialize() {
        //initialize genome column
        codeGenomeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GenomeModel, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<GenomeModel, Integer> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getCode());
            }
        });
        nameGenomeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GenomeModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<GenomeModel, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getName());
            }
        });
        sizeGenomeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GenomeModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<GenomeModel, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getSize());
            }
        });
        fastaGenomeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GenomeModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<GenomeModel, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getFasta());
            }
        });
        gtfGenomeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GenomeModel, String>, ObservableValue<String>>() {
                                             @Override
                                             public ObservableValue<String> call(TableColumn.CellDataFeatures<GenomeModel, String> param) {
                                                 return new ReadOnlyObjectWrapper<>(param.getValue().getGtf());
                                             }
                                         }
        );

        //clear genome details
        showGenomeDetail(null);
        //show genome detail when genome item is selected
        genomeModelTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GenomeModel>() {
            @Override
            public void changed(ObservableValue<? extends GenomeModel> observable, GenomeModel oldValue, GenomeModel newValue) {
                showGenomeDetail(newValue);
            }
        });

        //initialize experiment column
        codeExpCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ExperimentModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ExperimentModel, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getCode());
            }
        });
        genomeCodeExpCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ExperimentModel, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<ExperimentModel, Integer> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getGenomeCode());
            }
        });
        fastq1ExpCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ExperimentModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ExperimentModel, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getFastq1());
            }
        });
        fastq2ExpCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ExperimentModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ExperimentModel, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getFastq2());
            }
        });
        controlExpCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ExperimentModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ExperimentModel, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getControl());
            }
        });
        broadPeakExpCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ExperimentModel, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<ExperimentModel, Boolean> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().isBroadPeak());
            }
        });
        //clear experiment detail
        showExperimentDetail(null);
        //show experiment detail when experiment item is selected
        expModelTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExperimentModel>() {
            @Override
            public void changed(ObservableValue<? extends ExperimentModel> observable, ExperimentModel oldValue, ExperimentModel newValue) {
                showExperimentDetail(newValue);
            }
        });

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        //add observable data to table
        genomeModelTable.setItems(mainApp.getGenomeModels());
        expModelTable.setItems(mainApp.getExperimentModels());
    }

    @FXML
    private void handleDeleteGenome() {
        int index = genomeModelTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            genomeModelTable.getItems().remove(index);
        } else {
            GuiUtil.createAlert("Warning", "There is no genome in the table", Alert.AlertType.WARNING);
        }

    }

    @FXML
    private void handleDeleteExperiment() {
        int index = expModelTable.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            expModelTable.getItems().remove(index);
        } else {
            GuiUtil.createAlert("Warning", "There is no experiment in the table", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleCopyGenome() {
        GenomeModel oldModel = genomeModelTable.getSelectionModel().getSelectedItem();
        if (oldModel != null) {
            GenomeModel newModel = new GenomeModel(oldModel);
            mainApp.getGenomeModels().add(newModel);
        } else {
            GuiUtil.createAlert("Error", "No genome is selected", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCopyExperiment() {
        ExperimentModel oldModel = expModelTable.getSelectionModel().getSelectedItem();
        if (oldModel != null) {
            ExperimentModel newModel = new ExperimentModel(oldModel);
            mainApp.getExperimentModels().add(newModel);
        } else {

            GuiUtil.createAlert("Error", "No experiment is selected", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleNewGenome() {
        GenomeModel model = new GenomeModel();

        boolean okClicked = mainApp.showGenomeEditDialog(model);
        if (okClicked) {
            mainApp.getGenomeModels().add(model);
        }
    }

    @FXML
    private void handleNewExperiment() {
        ExperimentModel model = new ExperimentModel();
        boolean okClicked = mainApp.showExperimentEditDialog(model);
        if (okClicked) {
            mainApp.getExperimentModels().add(model);
        }
    }

    @FXML
    private void handleEditGenome() {
        GenomeModel model = genomeModelTable.getSelectionModel().getSelectedItem();
        if (model != null) {
            boolean okClicked = mainApp.showGenomeEditDialog(model);
            if (okClicked) {
                showGenomeDetail(model);
                genomeModelTable.refresh();
            }
        } else {
            GuiUtil.createAlert("Warning!", "No genome is selected", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleEditExperiment() {
        ExperimentModel model = expModelTable.getSelectionModel().getSelectedItem();
        if (model != null) {
            boolean okClicked = mainApp.showExperimentEditDialog(model);
            if (okClicked) {
                showExperimentDetail(model);
                expModelTable.refresh();
            }
        } else {
            GuiUtil.createAlert("Warning", "No experiment is selected", Alert.AlertType.WARNING);
        }
    }

    private void showGenomeDetail(GenomeModel genomeModel) {
        if (genomeModel != null) {
            codeGenomeLabel.setText(String.valueOf(genomeModel.getCode()));
            nameGenomeLabel.setText(genomeModel.getName());
            sizeGenomeLabel.setText(genomeModel.getSize());
            fastaGenomeLabel.setText(genomeModel.getFasta());
            gtfGenomeLabel.setText(genomeModel.getGtf());
        } else {
            codeGenomeLabel.setText("");
            nameGenomeLabel.setText("");
            sizeGenomeLabel.setText("");
            fastaGenomeLabel.setText("");
            gtfGenomeLabel.setText("");
        }

    }

    @FXML
    private void handleGenerate() {
        Input input = InputConvector.getInstance().convert(mainApp.getGenomeModels(), mainApp.getExperimentModels());
        String inputJson = GsonUtil.toJson(input);
        String fileName = Resource.INPUT.getFileName();
        FileUtil.overwriteFile(inputJson, fileName);
        File file = new File(fileName);
        String message = String.format("Configuration file %s has been successfully created!\nIt is located at: %s.\n", fileName, file.getAbsoluteFile());
        GuiUtil.createAlert("Success!", message, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleReset() {
        mainApp.getGenomeModels().clear();
        mainApp.getExperimentModels().clear();
        GuiUtil.createAlert("Information", "Genomes and experiments are reset!", Alert.AlertType.INFORMATION);
    }

    private void showExperimentDetail(ExperimentModel model) {
        if (model != null) {
            codeExpLabel.setText(model.getCode());
            genomeCodeExpLabel.setText(String.valueOf(model.getGenomeCode()));
            fastq1ExpLabel.setText(model.getFastq1());
            fastq2ExpLabel.setText(model.getFastq2());
            controlExpLabel.setText(model.getControl());
            broadPeakExpLabel.setText(String.valueOf(model.isBroadPeak()));
        } else {
            codeExpLabel.setText("");
            genomeCodeExpLabel.setText("");
            fastq1ExpLabel.setText("");
            fastq2ExpLabel.setText("");
            controlExpLabel.setText("");
            broadPeakExpLabel.setText("");
        }
    }
}
