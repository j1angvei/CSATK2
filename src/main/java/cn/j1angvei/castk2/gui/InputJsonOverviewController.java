package cn.j1angvei.castk2.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;


/**
 * Created by Wayne on 4/24 0024.
 */
public class InputJsonOverviewController {
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

    //reference to main application
    private MainApp mainApp;

    public InputJsonOverviewController() {
    }

    @FXML
    private void initialize() {
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
        });

        //clear person details
        showGenomeDetail(null);
        genomeModelTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GenomeModel>() {
            @Override
            public void changed(ObservableValue<? extends GenomeModel> observable, GenomeModel oldValue, GenomeModel newValue) {
                showGenomeDetail(newValue);
            }
        });
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        //add observable data to table
        genomeModelTable.setItems(mainApp.getGenomeModels());
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
}
