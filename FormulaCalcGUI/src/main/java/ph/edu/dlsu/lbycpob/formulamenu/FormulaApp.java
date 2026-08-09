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
        Button btnOne = new Button("Formula 1: Box Volume");
        Button btnTwo = new Button("Formula 2: Coulombs Law");
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

        formula = (formulaID == 1) ? new Volume() : new Coulomb();
        buttonText = formula.getParameterList();

        // Create buttons
        Button btnFirst = new Button("Compute " + buttonText[3]);
        btnFirst.setOnAction(e -> handleVariable(stage, 0));

        Button btnSecond = new Button("Compute " + buttonText[0]);
        btnSecond.setOnAction(e -> handleVariable(stage, 1));

        Button btnThird = new Button("Compute " + buttonText[1]);
        btnThird.setOnAction(e -> handleVariable(stage, 2));

        Button btnFourth = new Button("Compute " + buttonText[2]);
        btnFourth.setOnAction(e -> handleVariable(stage, 3));

        Button btnClose = new Button("Go Back To Main");
        btnClose.setOnAction(e -> stage.close());

        // Label
        Label menu = new Label("VARIABLE MENU");

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.getChildren().addAll(menu, btnFirst, btnSecond, btnThird, btnFourth, btnClose);

        Scene scene = new Scene(root, screenWidth, screenHeight);
        if (!isLogicOnly) {
            scene.getStylesheets().add("mystyle.css");
//            scene.getStylesheets().add(getClass().getResource("resources/mystyle.css").toExternalForm());
        }

        stage.initOwner(primaryStage);
        stage.initModality(WINDOW_MODAL);
        stage.setScene(scene);
        stage.setTitle("Formula1 Sub-Menu");
        stage.setFullScreen(true);
        stage.show();
    }

    private void handleVariable(Stage stage, int varIndex) {
        Stage lastStage = new Stage();
        lastStage.initOwner(stage);
        lastStage.initModality(WINDOW_MODAL);

        // Create labels with textfields
        Label lblLength = new Label(buttonText[varIndex++ % 4]);
        firstInput = new TextField("0.0");
        Label lblWidth = new Label(buttonText[varIndex++ % 4]);
        secondInput = new TextField("0.0");
        Label lblHeight = new Label(buttonText[varIndex++ % 4]);
        thirdInput = new TextField("0.0");
        Label lblResult = new Label(buttonText[varIndex % 4]);
        lblResultValue = new TextField("0.0");
        lblResultValue.setEditable(false);

        // Create button nodes
        final int query = varIndex % 4;
        Button btnCompute = new Button("Compute");
        Button btnClose = new Button("Exit to Selection");
        btnCompute.setOnAction(e -> {
            String[] args = {firstInput.getText(), secondInput.getText(), thirdInput.getText()};
            lblResultValue.setText(String.format("%.2f", formula.compute(buttonText[query], args)));
        });
        btnClose.setOnAction(e -> lastStage.close());

        // Create the scene
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(20);
        gridPane.add(new HBox(lblLength), 0, 0);
        gridPane.add(firstInput, 1, 0);
        gridPane.add(new HBox(lblWidth), 0, 1);
        gridPane.add(secondInput, 1, 1);
        gridPane.add(new HBox(lblHeight), 0, 2);
        gridPane.add(thirdInput, 1, 2);
        gridPane.add(btnCompute, 1, 3);
        gridPane.add(new HBox(lblResult), 0, 4);
        gridPane.add(lblResultValue, 1, 4);
        gridPane.add(btnClose, 1, 5);
        gridPane.setAlignment(Pos.CENTER);

        VBox root = new VBox(gridPane);
        root.setSpacing(20);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, screenWidth / 2, screenHeight);
        if (!isLogicOnly) {
            scene.getStylesheets().add("mystyle.css");
//            scene.getStylesheets().add(getClass().getResource("resources/mystyle.css").toExternalForm());
        }

        // Set the final stage
        lastStage.setScene(scene);
        lastStage.initStyle(StageStyle.UNDECORATED);
        lastStage.show();
    }


}
