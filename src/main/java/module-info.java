module org.example.lesvirt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens org.example.lesvirt to javafx.fxml;
    exports org.example.lesvirt;
}