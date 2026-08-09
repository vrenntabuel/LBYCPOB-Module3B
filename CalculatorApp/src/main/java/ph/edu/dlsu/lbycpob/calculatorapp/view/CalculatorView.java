package ph.edu.dlsu.lbycpob.calculatorapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * The CalculatorView class represents the user interface (View) component of the calculator
 * following the MVC (Model-View-Controller) design pattern.
 *
 * This class is responsible for:
 * - Creating and organizing all visual components (buttons, display, layout)
 * - Managing the appearance and styling of the calculator
 * - Providing access to UI components for the controller
 * - Updating the display when calculations are performed
 *
 * The view does NOT handle any business logic or calculations - it only manages
 * the visual presentation and provides interfaces for user interaction.
 *
 */
public class CalculatorView {
    /** The main container that holds all calculator components */
    private VBox root;

    /** The text field that shows numbers, expressions, and results */
    private TextField display;

    /** A map that stores all buttons with their labels as keys for easy access */
    private Map<String, Button> buttons;

    /** Define the layout of buttons as a 2D array (rows and columns) */
    private static final String[][] buttonLayout = new String[][]{
            {"(", ")", "C", "CE"},
            {"shft", "sin", "cos", "tan"},
            {"π", "!", "√", "%"},
            {"+/-", "ln", "log", "^"},
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"0", ".", "=", "+"}
    };

    /**
     * Constructor that creates a new CalculatorView instance.
     *
     * This automatically sets up all the visual components and arranges them
     * in the proper layout. After creating a CalculatorView, it's ready to be
     * added to a JavaFX scene.
     */
    public CalculatorView() {
        // Set up all the UI components (display, buttons, styling)
        initializeComponents();

        // Arrange components in the proper visual layout
        setupLayout();
    }

    /**
     * Initializes all the main UI components of the calculator.
     *
     * This method creates the display field, sets up the main container,
     * applies initial styling, and creates all the calculator buttons.
     */
    private void initializeComponents() {
        // Create the main container (VBox = Vertical Box - stacks items vertically)
        root = new VBox(20);  // n pixels spacing between child elements
        root.setPadding(new Insets(15));  // 15 pixels padding around the edges
        root.getStyleClass().add("calculator-body");  // Add CSS class for styling

        // Create the display area where numbers and results are shown
        display = new TextField("0");  // Start with "0" displayed
        display.setEditable(false);    // Users can't type directly in the display
        display.setAlignment(Pos.CENTER_RIGHT);  // Numbers align to the right (like real calculators)
        display.getStyleClass().add("display");  // Add CSS class for styling
        display.setFont(Font.font("Monospace", 24));  // Use monospace font, size 24

        // Initialize the map that will store all our buttons
        // HashMap allows us to quickly find buttons by their label (like "+" or "7")
        buttons = new HashMap<>();

        // Create all the calculator buttons
        createButtons();
    }

    /**
     * Creates all calculator buttons and stores them in the buttons map.
     *
     * This method defines the layout of buttons, creates each button with proper
     * styling, and categorizes them by type (number, operator, scientific, function).
     */
    private void createButtons() {
        // Loop through each row of buttons
        for (String[] row : buttonLayout) {
            // Loop through each button in the current row
            for (String label : row) {
                // Create a new button with the specified label
                Button button = new Button(label);
                // Set the button size (80 pixels wide, 60 pixels tall)
                button.setPrefSize(80, 60);
                // Apply different CSS styling based on button type
                // This makes different types of buttons look different
                if (isOperator(label)) {
                    button.getStyleClass().add("operator-button");
                } else if (isScientific(label)) {
                    button.getStyleClass().add("scientific-button");
                } else if (isNumber(label)) {
                    button.getStyleClass().add("number-button");
                } else {
                    button.getStyleClass().add("function-button");
                }
                // Store the button in our map using its label as the key
                // This allows us to quickly find buttons later (e.g., buttons.get("+"))
                buttons.put(label, button);
            }
        }
    }

    /**
     * Arranges all components in their proper visual layout.
     *
     * This method adds the display to the top, then creates rows of buttons
     * and adds them below the display. The final result is a calculator layout.
     */
    private void setupLayout() {
        // Add top label
        Label topLabel = new Label("LBYCPOB - E2x+");
        topLabel.setAlignment(Pos.TOP_RIGHT);
        topLabel.getStyleClass().add("top-label");
        root.getChildren().add(topLabel);

        // Add display
        display.positionCaret(display.getText().length()); // Ensure recent char is shown
        VBox.setMargin(display, new Insets(10, 0, 10, 0));
        root.getChildren().add(display);

        for (String[] row : buttonLayout) {
            HBox buttonRow = new HBox(5);
            buttonRow.setAlignment(Pos.CENTER);

            for (String label : row) {
                buttonRow.getChildren().add(buttons.get(label));
                if (label.equalsIgnoreCase("shft")) {
                    HBox hbox = setupShiftLabels();
                    root.getChildren().add(hbox);
                }
            }
            root.getChildren().add(buttonRow);
        }
        Region bottomSpacer = new Region();
        bottomSpacer.setMinHeight(40);
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
        root.getChildren().add(bottomSpacer);
    }

    private HBox setupShiftLabels() {
        HBox hbox = new HBox(50); // 10px spacing
        hbox.setPadding(new Insets(0, 0, -5, 30)); // 30px left indent
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(
                new Label("    "),
                new Label("sin⁻¹"),
                new Label("cos⁻¹"),
                new Label("tan⁻¹")
        );
        VBox.setMargin(hbox, new Insets(-20, 0, -20, 0));
        return hbox;
    }

    // Encapsulated getters
    public VBox getRoot() {
        return root;
    }

    public TextField getDisplay() {
        return display;
    }

    public Button getButton(String label) {
        return buttons.get(label);
    }

    public Map<String, Button> getAllButtons() {
        return new HashMap<>(buttons); // Return defensive copy
    }

    // Helper methods for button classification
    private boolean isOperator(String text) {
        return text.matches("[+\\-*/=^%]");
    }

    private boolean isScientific(String text) {
        return text.matches("sin|cos|tan|ln|log|!|√");
    }

    private boolean isNumber(String text) {
        return text.matches("[0-9.]");
    }

    public void updateDisplay(String text) {
        display.setText(text);
    }
}