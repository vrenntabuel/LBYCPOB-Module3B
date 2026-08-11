package ph.edu.dlsu.lbycpob.hellojavafx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MinimalistHelloApp extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello LBYCPOB! by Abuel and Malirong");

        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        Scene scene = new Scene(label, 320, 120);

        stage.setTitle("Minimalist Hello");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}