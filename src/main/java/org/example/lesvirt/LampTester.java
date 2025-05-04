package org.example.lesvirt;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class LampTester extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Создаем тестовую лампу
        Circle lamp = new Circle(50);
        lamp.getStyleClass().add("lamp-circle");
        lamp.getStyleClass().add("lamp-off");

        // Обработчик клика
        lamp.setOnMouseClicked(event -> {
            if (lamp.getStyleClass().contains("lamp-off")) {
                lamp.getStyleClass().remove("lamp-off");
                lamp.getStyleClass().add("lamp-on");
                System.out.println("Лампа включена (CSS)");
            } else {
                lamp.getStyleClass().remove("lamp-on");
                lamp.getStyleClass().add("lamp-off");
                System.out.println("Лампа выключена (CSS)");
            }
        });

        // Создаем сцену с CSS
        StackPane root = new StackPane();
        root.getChildren().add(lamp);
        Scene scene = new Scene(root, 200, 200);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setTitle("Тестер лампы с CSS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}