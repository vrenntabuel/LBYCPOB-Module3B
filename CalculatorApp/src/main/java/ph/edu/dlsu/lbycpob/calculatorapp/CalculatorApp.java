package ph.edu.dlsu.lbycpob.calculatorapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ph.edu.dlsu.lbycpob.calculatorapp.controller.CalculatorController;
import ph.edu.dlsu.lbycpob.calculatorapp.model.CalculatorModel;
import ph.edu.dlsu.lbycpob.calculatorapp.view.CalculatorView;

import java.util.Objects;

/**
 * Main Application class for the Scientific Calculator
 * Follows MVC pattern with proper separation of concerns
 */
public class CalculatorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create MVC components
        CalculatorModel model = new CalculatorModel();
        CalculatorView view = new CalculatorView();
        CalculatorController controller = new CalculatorController(model, view);
        controller.run();

        // Setup scene
        Scene scene = new Scene(view.getRoot(), 400, 740);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        // Configure stage
        primaryStage.setTitle("Malirong and Abuel Calculator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Calculator has fixed size
        primaryStage.show();
    }

    static void main(String[] args) {
        launch(args);
    }
}