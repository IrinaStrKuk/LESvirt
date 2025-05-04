package org.example.lesvirt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();

        LabStandController controller = loader.getController();
        controller.initialize();

        primaryStage.setTitle("Лабораторный стенд ЛЭС-5 (профессиональная версия)");
        primaryStage.setScene(new Scene(root, 1300, 900));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
