package ph.edu.dlsu.lbycpob.formulamenu;/*
@author cobalt-mkc
date created: 8/5/2019
last modified: 8/17/2022
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static javafx.stage.Modality.WINDOW_MODAL;


public class FormulaApp extends Application {
    private final boolean isLogicOnly = false; /* Setting the app to naked design w/o CSS */
    private double screenWidth;
    private double screenHeight;

    private TextField firstInput;
    private TextField secondInput;
    private TextField thirdInput;
    private TextField lblResultValue;

    private IFormula formula;
    private String[] buttonText;

    public static void main(String[] args) {
        launch(args);
    }

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) {
        // Get Screen size
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        screenWidth = screen.getWidth();
        screenHeight = screen.getHeight();

        // Create buttons
        Button btnOne = new Button("Formula 1: Ideal Gas Law");
        Button btnTwo = new Button("Formula 2: Electrical Energy");
        Button btnClose = new Button("Exit");

        // Set button handlers
        btnOne.setOnAction(e -> handleFormula(primaryStage, 1));
        btnTwo.setOnAction(e -> handleFormula(primaryStage, 2));
        btnClose.setOnAction(e -> Platform.exit());

        // Label
        Label menu = new Label("MAIN MENU");

        // Create the scene
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.getChildren().addAll(menu, btnOne, btnTwo, btnClose);
        scene = new Scene(root, screenWidth, screenHeight);
        if (!isLogicOnly) {
            scene.getStylesheets().add(getClass().getResource("/mystyle.css").toExternalForm());
        }

        // Create the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Formula App");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }


    private void handleFormula(Stage primaryStage, int formulaID) {
        Stage stage = new Stage();

        formula = (formulaID == 1)
                ? new IdealGas()
                : new ElectricalEnergy();

        buttonText = formula.getParameterList();

        // Create variable selection buttons
        Button btnFirst = new Button("Compute " + buttonText[0]);
        btnFirst.setOnAction(e -> handleVariable(stage, 0));

        Button btnSecond = new Button("Compute " + buttonText[1]);
        btnSecond.setOnAction(e -> handleVariable(stage, 1));

        Button btnThird = new Button("Compute " + buttonText[2]);
        btnThird.setOnAction(e -> handleVariable(stage, 2));

        Button btnFourth = new Button("Compute " + buttonText[3]);
        btnFourth.setOnAction(e -> handleVariable(stage, 3));

        Button btnClose = new Button("Go Back To Main");
        btnClose.setOnAction(e -> stage.close());

        Label menu = new Label("VARIABLE MENU");

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.getChildren().addAll(
                menu,
                btnFirst,
                btnSecond,
                btnThird,
                btnFourth,
                btnClose
        );

        Scene scene = new Scene(root, screenWidth, screenHeight);

        if (!isLogicOnly) {
            scene.getStylesheets().add(
                    getClass().getResource("/mystyle.css").toExternalForm()
            );
        }

        stage.initOwner(primaryStage);
        stage.initModality(WINDOW_MODAL);
        stage.setScene(scene);

        stage.setTitle(
                formulaID == 1
                        ? "Ideal Gas Law"
                        : "Electrical Energy"
        );

        stage.setFullScreen(true);
        stage.show();
    }

    private void handleVariable(Stage stage, int varIndex) {
        Stage lastStage = new Stage();

        lastStage.initOwner(stage);
        lastStage.initModality(WINDOW_MODAL);

        // Create labels and text fields for the three input variables
        int firstIndex = (varIndex + 1) % 4;
        int secondIndex = (varIndex + 2) % 4;
        int thirdIndex = (varIndex + 3) % 4;

        Label lblFirst = new Label(buttonText[firstIndex]);
        firstInput = new TextField("0.0");

        Label lblSecond = new Label(buttonText[secondIndex]);
        secondInput = new TextField("0.0");

        Label lblThird = new Label(buttonText[thirdIndex]);
        thirdInput = new TextField("0.0");

        Label lblResult = new Label(buttonText[varIndex]);

        lblResultValue = new TextField("0.0");
        lblResultValue.setEditable(false);

        Button btnCompute = new Button("Compute");

        Button btnClose = new Button("Exit to Selection");

        btnCompute.setOnAction(e -> {
            try {
                String[] args = {
                        firstInput.getText(),
                        secondInput.getText(),
                        thirdInput.getText()
                };

                double result = formula.compute(
                        buttonText[varIndex],
                        args
                );

                lblResultValue.setText(
                        String.format("%.2f", result)
                );

            } catch (NumberFormatException ex) {
                lblResultValue.setText("Invalid Input");
            }
        });

        btnClose.setOnAction(e -> lastStage.close());

        // Create layout
        GridPane gridPane = new GridPane();

        gridPane.setHgap(10);
        gridPane.setVgap(20);

        gridPane.add(lblFirst, 0, 0);
        gridPane.add(firstInput, 1, 0);

        gridPane.add(lblSecond, 0, 1);
        gridPane.add(secondInput, 1, 1);

        gridPane.add(lblThird, 0, 2);
        gridPane.add(thirdInput, 1, 2);

        gridPane.add(btnCompute, 1, 3);

        gridPane.add(lblResult, 0, 4);
        gridPane.add(lblResultValue, 1, 4);

        gridPane.add(btnClose, 1, 5);

        gridPane.setAlignment(Pos.CENTER);

        VBox root = new VBox(gridPane);

        root.setSpacing(20);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(
                root,
                screenWidth / 2,
                screenHeight
        );

        if (!isLogicOnly) {
            scene.getStylesheets().add(
                    getClass().getResource("/mystyle.css").toExternalForm()
            );
        }

        lastStage.setScene(scene);
        lastStage.initStyle(StageStyle.UNDECORATED);
        lastStage.show();
    }


}
