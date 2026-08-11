package ph.edu.dlsu.lbycpob.hellojavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OPattern extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/ph/edu/dlsu/lbycpob/hellojavafx/opattern.fxml"
                )
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("O-Pattern");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}