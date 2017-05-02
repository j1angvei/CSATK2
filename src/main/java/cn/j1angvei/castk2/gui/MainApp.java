package cn.j1angvei.castk2.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Wayne on 4/24 0024.
 */
public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<GenomeModel> genomeModels = FXCollections.observableArrayList();
    private ObservableList<ExperimentModel> experimentModels = FXCollections.observableArrayList();

    public MainApp() {
        genomeModels.add(new GenomeModel(99, "sample name", "3.8e9", "reference_file_name", "genome_annotation_file"));
        experimentModels.add(new ExperimentModel("sample exp code", "fastq 1", "fastq 2", "control exp code", 18, false));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CSATK GUI");
        initLayout();
        showInputOverview();
    }

    private void initLayout() {
        try {
//            FXMLLoader loader = new FXMLLoader();
//            URL url = FileUtil.readResource("root.fxml");
//            loader.setLocation(url);
//            rootLayout = loader.load();
            rootLayout = FXMLLoader.load(getClass().getResource("/gui/root.fxml"));
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInputOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/gui/input_json.fxml");
            loader.setLocation(url);
            AnchorPane inputOverview = loader.load();
            rootLayout.setCenter(inputOverview);

            //controller
            InputJsonController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showGenomeEditDialog(GenomeModel model) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/gui/dialog_genome_edit.fxml");
            loader.setLocation(url);
            AnchorPane page = loader.load();

            //create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Genome");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //set the genome into the controller
            GenomeEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setGenomeModel(model);

            //show the dialog and wait until the user use it
            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showExperimentEditDialog(ExperimentModel model) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/gui/dialog_experiment_edit.fxml");
            loader.setLocation(url);
            AnchorPane page = loader.load();

            //create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Experiment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            //set the genome into the controller
            ExperimentEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setExperimentModel(model);

            //show the dialog and wait until the user use it
            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<GenomeModel> getGenomeModels() {
        return genomeModels;
    }

    public ObservableList<ExperimentModel> getExperimentModels() {
        return experimentModels;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
