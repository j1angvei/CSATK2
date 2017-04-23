package cn.j1angvei.castk2.gui;

import cn.j1angvei.castk2.util.FileUtil;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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

    public MainApp() {
        genomeModels.add(new GenomeModel(1, "homo sapines", "3.08e+9", "Homo_sapiens.GRCh38.87.gtf", "Homo_sapiens.GRCh38.dna.chromosome.all.fa"));
        genomeModels.add(new GenomeModel(11, "tair", "1.36e+8", "TAIR10_chr_all.fas", "TAIR10_GFF3_genes.gff"));
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
            FXMLLoader loader = new FXMLLoader();
            URL url = FileUtil.readResource("root.fxml");
            loader.setLocation(url);
            rootLayout = loader.load();

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
            URL url = FileUtil.readResource("input_json_overview.fxml");
            loader.setLocation(url);
            AnchorPane inputOverview = loader.load();
            rootLayout.setCenter(inputOverview);

            //controller
            InputJsonOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<GenomeModel> getGenomeModels() {
        return genomeModels;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
