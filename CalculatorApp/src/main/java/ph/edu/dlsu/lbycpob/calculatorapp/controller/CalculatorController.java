package ph.edu.dlsu.lbycpob.calculatorapp.controller;

import javafx.scene.control.Button;
import ph.edu.dlsu.lbycpob.calculatorapp.model.CalculatorModel;
import ph.edu.dlsu.lbycpob.calculatorapp.view.CalculatorView;

/**
 * The CalculatorController class serves as the intermediary between the calculator's
 * user interface (view) and its mathematical logic (model). It follows the MVC
 * (Model-View-Controller) design pattern.
 * <p>
 * This controller handles all user interactions, processes button clicks, manages
 * input validation, and coordinates between the view and model components.
 */
public class CalculatorController {
    // Instance variables - these store the state of our calculator controller

    /**
     * The model component that handles all mathematical calculations
     */
    private CalculatorModel model;

    /**
     * The view component that manages the user interface
     */
    private CalculatorView view;

    /**
     * Stores the current mathematical expression being entered by the user
     */
    private StringBuilder currentInput;

    /**
     * Flag to track if we're waiting for a new number after an operation
     */
    private boolean waitingForOperand;

    /**
     * Constructor to create a new CalculatorController instance.
     * <p>
     * This sets up the controller with references to the model and view,
     * and initializes the input tracking variables.
     *
     * @param model The CalculatorModel that will handle mathematical operations
     * @param view  The CalculatorView that manages the user interface
     */
    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;
        this.currentInput = new StringBuilder();
        this.waitingForOperand = false;
    }

    /**
     * Starts the calculator by setting up all button event handlers.
     * This method should be called after the controller is created to make
     * the calculator functional.
     */
    public void run() {
        initializeEventHandlers();
    }

    /**
     * Sets up event handlers for all calculator buttons.
     * <p>
     * This method connects each button in the view to its corresponding
     * action method. When a user clicks a button, the appropriate handler
     * method will be called.
     */
    private void initializeEventHandlers() {
        // Set up number buttons (0-9) and decimal point
        // We use an array to avoid repeating similar code for each number
        for (String num : new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "."}) {
            Button button = view.getButton(num);
            // Check if the button exists before setting up the handler
            if (button != null) {
                // Lambda expression: when button is clicked, call handleNumberInput
                button.setOnAction(e -> handleNumberInput(num));
            }
        }

        // Set up basic arithmetic operation buttons (+, -, *, /)
        for (String op : new String[]{"+", "-", "*", "/", "%"}) {
            Button button = view.getButton(op);
            if (button != null) {
                // When an operation button is clicked, handle it as a basic operation
                button.setOnAction(e -> handleBasicOperation(op));
            }
        }

        // Set up scientific function buttons (sin, cos, tan, etc.)
        setupScientificButtons();

        // Set up the equals button (=) - calculates and displays the result
        Button equalsButton = view.getButton("=");
        if (equalsButton != null) {
            equalsButton.setOnAction(e -> handleEquals());
        }

        // Set up the negative toggle button (+/-) - sets the value to negative
        Button negativeButton = view.getButton("+/-");
        if (negativeButton != null) {
            negativeButton.setOnAction(e -> handleNegativeButton());
        }

        Button piButton = view.getButton("π");
        if (piButton != null) {
            piButton.setOnAction(e -> handlePiButton());
        }

        // Set up the Shift button
        Button clearButton = view.getButton("C");
        if (clearButton != null) {
            clearButton.setOnAction(e -> handleClear());
        }


        // Set up the Shift button
        Button shiftButton = view.getButton("shft");
        if (shiftButton != null) {
            shiftButton.setOnAction(e -> handleShift());
        }

        // Set up the clear entry button (CE) - clears current input only
        Button clearEntryButton = view.getButton("CE");
        if (clearEntryButton != null) {
            clearEntryButton.setOnAction(e -> handleClearEntry());
        }

        // Parentheses
        Button leftParenButton = view.getButton("(");
        if (leftParenButton != null) {
            leftParenButton.setOnAction(e -> handleInput("("));
        }

        // Set up parentheses buttons for grouping expressions
        Button rightParenButton = view.getButton(")");
        if (rightParenButton != null) {
            rightParenButton.setOnAction(e -> handleInput(")"));
        }
    }

    private void handleShift() {
        model.calculator.toggleShift();
        if (model.calculator.isShiftActive()) {
            view.getButton("shft").setStyle("-fx-text-fill: black;");
        } else {
            view.getButton("shft").setStyle("-fx-text-fill: white;");
        }
    }

    private void handlePiButton() {
        // If we just displayed a result, clear the input to start fresh
        if (model.isResultDisplayed()) {
            currentInput.setLength(0); // Clear the StringBuilder
            model.setResultDisplayed(false);
        }

        // If we were waiting for a new number after an operation
        if (waitingForOperand) {
            // currentInput.setLength(0); // Bug 1: Don't clear currentInput here
            waitingForOperand = false;
        }

        // Add the number/decimal to our current input
        currentInput.append(model.calculator.PI);

        // Update what the user sees on the display
        updateDisplay();

    }


    /**
     * Sets up event handlers for all scientific calculator buttons.
     * <p>
     * This method handles trigonometric functions, logarithms, square root,
     * factorial, and exponentiation operations.
     */
    private void setupScientificButtons() {
        setupScientificButton("sin", "sin"); // 1st arg: buttonLabel; 2nd arg: operation
        setupScientificButton("cos", "cos");
        setupScientificButton("tan", "tan");
        setupScientificButton("ln", "ln");
        setupScientificButton("log", "log");
        setupScientificButton("√", "sqrt");
        setupScientificButton("!", "factorial");
        setupScientificButton("^", "^");
    }

    /**
     * Helper method to set up individual scientific function buttons.
     *
     * @param buttonLabel The text displayed on the button
     * @param operation   The mathematical operation to perform
     */
    private void setupScientificButton(String buttonLabel, String operation) {
        Button button = view.getButton(buttonLabel);
        if (button != null) {
            // Exponentiation (^) is handled differently because it's a binary operation
            if (operation.equals("^")) {
                // ^ needs two operands, so we treat it like regular input
                button.setOnAction(e -> handleInput("^"));
            } else {
                // Other scientific operations work on a single number
                button.setOnAction(e -> handleScientificOperation(operation));
            }
        }
    }

    /**
     * Handles input when a number button (0-9) or decimal point is clicked.
     * <p>
     * This method manages the logic for building numbers, handling decimal points,
     * and updating the display appropriately.
     *
     * @param number The digit or decimal point that was clicked
     */
    private void handleNumberInput(String number) {
        // If we just displayed a result, clear the input to start fresh
        if (model.isResultDisplayed()) {
            currentInput.setLength(0); // Clear the StringBuilder
            model.setResultDisplayed(false);
        }

        // If we were waiting for a new number after an operation
        if (waitingForOperand) {
            // currentInput.setLength(0); // Bug 1: Don't clear currentInput here
            waitingForOperand = false;
        }

        // Prevent multiple decimal points in the same number
        if (number.equals(".")) {
            String currentToken = getCurrentToken();
            if (currentToken.contains(".")) return; // Prevent multiple decimal points in the same number
        }

        // Add the number/decimal to our current input
        currentInput.append(number);

        // Update what the user sees on the display
        updateDisplay();
    }

    /**
     * Handles general input (like parentheses or operators).
     * <p>
     * This method is used for input that doesn't require special number handling.
     *
     * @param input The character or string to add to the current expression
     */
    private void handleInput(String input) {
        if (model.isResultDisplayed() && !input.matches("[+\\-*/^()]")) {
            currentInput.setLength(0);
            model.setResultDisplayed(false);
        }

        currentInput.append(input);
        updateDisplay();
    }

    /**
     * Handles basic arithmetic operations (+, -, *, /).
     * <p>
     * This method adds the operation to the current expression and prepares
     * for the next operand.
     *
     * @param operation The arithmetic operation symbol clicked
     */
    private void handleBasicOperation(String operation) {
        if (model.isResultDisplayed()) {
            // Start new expression with the result
            String result = String.valueOf(model.getCurrentValue());
            currentInput.setLength(0);
            currentInput.append(result);
            model.setResultDisplayed(false);
        }

        // Add the operation with spaces for better readability
        // currentInput.append(" ").append(operation).append(" ");
        currentInput.append(operation);

        // Set flag that we're now waiting for the next number
        waitingForOperand = true;

        updateDisplay();
    }

    /**
     * Handles scientific operations (sin, cos, tan, ln, log, sqrt, factorial).
     * <p>
     * These operations work on a single number and immediately calculate the result.
     *
     * @param functionName The name of the scientific operation to perform
     */
    private void handleScientificOperation(String functionName) {
        if (model.isResultDisplayed()) {
            currentInput.setLength(0);
            model.setResultDisplayed(false);
        }

        if (!model.calculator.isShiftActive()){
            currentInput.append(functionName);
        } else { // SHIFT functions
            currentInput.append("a" + functionName);
        }
        // Add function with opening parenthesis
        currentInput.append("(");

        updateDisplay();
    }

    /**
     * Handles the negative toggle button (+/-) - toggles the sign of the current number.
     * <p>
     * This method can work in several scenarios:
     * 1. If a result is displayed, it negates that result
     * 2. If currently entering a number, it toggles the sign of that number
     * 3. If at the start of an expression or after an operator, it adds a negative sign
     */
    private void handleNegativeButton() {
        // If a result is currently displayed, negate it and start fresh
        if (model.isResultDisplayed()) {
            double currentValue = model.getCurrentValue();
            double negatedValue = -currentValue;

            // Update the model and display with the negated result
            model.setCurrentValue(negatedValue);
            String resultText = model.formatResult(negatedValue);
            view.updateDisplay(resultText);
            model.setDisplayText(resultText);

            // Update currentInput to reflect the negated value
            currentInput.setLength(0);
            currentInput.append(negatedValue);
            return;
        }

        // Get the current token (last number being entered)
        String currentToken = getCurrentToken();
        String inputStr = currentInput.toString();

        // Case 1: Empty input or just waiting for operand - add negative sign
        if (currentInput.isEmpty() || waitingForOperand ||
                inputStr.endsWith("+") || inputStr.endsWith("-") ||
                inputStr.endsWith("*") || inputStr.endsWith("/") ||
                inputStr.endsWith("^") || inputStr.endsWith("(")) {

            currentInput.append("-");
            waitingForOperand = false;
        }
        // Case 2: Currently entering a number - toggle its sign
        else if (!currentToken.isEmpty()) {
            // Find where the current token starts in the input
            int tokenStart = inputStr.lastIndexOf(currentToken);

            if (tokenStart > 0 && inputStr.charAt(tokenStart - 1) == '-') {
                // Check if the minus sign is actually a negative sign (not subtraction)
                char charBeforeMinus = inputStr.charAt(tokenStart - 1);
                if (charBeforeMinus == '(' || charBeforeMinus == '+' ||
                        charBeforeMinus == '-' || charBeforeMinus == '*' ||
                        charBeforeMinus == '/' || charBeforeMinus == '^') {

                    // Remove the negative sign
                    currentInput.deleteCharAt(tokenStart - 1);
                } else {
                    // This minus is subtraction, so add negative to current token
                    currentInput.insert(tokenStart, "-");
                }
            } else {
                // Add negative sign before current token
                currentInput.insert(tokenStart, "-");
            }
        }
        // Case 3: Special handling for standalone negative at the end
        else if (inputStr.endsWith("-") && inputStr.length() == 1) {
            // Remove the standalone negative
            currentInput.setLength(0);
        }
        updateDisplay();
    }


    /**
     * Handles the equals button click - evaluates and displays the final result.
     * <p>
     * This method takes the current mathematical expression, evaluates it,
     * and displays the result. It also includes debug output for troubleshooting.
     */
    private void handleEquals() {
        try {
            // Debug output to help with troubleshooting
            System.out.println("Input expression: '" + currentInput + "'");
            if (!currentInput.isEmpty()) {
                String expression = currentInput.toString();

                // Convert infix notation to postfix for evaluation
                String postfix = (model.calculator).infixToPostfix(currentInput.toString());
                System.out.println("Postfix: '" + postfix + "'");

                // Use the Calculator model's built-in expression evaluation
                // which already supports negation through its tokenizeExpression method
                double result = (model.calculator).evaluateExpression(expression);
                System.out.println("Result: " + model.formatResult(result));
                displayResult(result);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            view.updateDisplay("Error");
            model.setResultDisplayed(true);
        }
    }

    /**
     * Handles the clear button (C) - resets the calculator to initial state.
     * <p>
     * This clears all memory, input, and resets all flags.
     */
    private void handleClear() {
        model.clear();
        currentInput.setLength(0);
        waitingForOperand = false;
        view.updateDisplay("0");
    }

    /**
     * Handles the clear entry button (CE) - clears only the current input.
     * <p>
     * This is less drastic than clear - it only removes what's currently
     * being typed, not the entire calculation history.
     */
    private void handleClearEntry() {
        currentInput.setLength(0);
        view.updateDisplay("0");
        waitingForOperand = false;
    }

    /**
     * Displays a calculated result and updates the calculator state.
     * <p>
     * This method formats the result appropriately, updates the display,
     * and prepares the calculator for the next operation.
     *
     * @param result The numerical result to display
     */
    private void displayResult(double result) {
        model.setCurrentValue(result);
        String resultText = model.formatResult(result);
        view.updateDisplay(resultText);
        model.setDisplayText(resultText);
        model.setResultDisplayed(true);

        // Keep the result in currentInput for potential chaining
        currentInput.setLength(0);
        currentInput.append(result);
    }

    /**
     * Updates the calculator display with the current input.
     * <p>
     * This method is called whenever we need to refresh what the user sees.
     */
    private void updateDisplay() {
        String displayText = !currentInput.isEmpty() ? currentInput.toString() : "0";
        view.updateDisplay(displayText);
        model.setDisplayText(displayText);
    }

    private String getCurrentToken() {
        String input = currentInput.toString();
        String[] tokens = input.split("[+\\-*/^()]");
        return tokens.length > 0 ? tokens[tokens.length - 1] : "";
    }

    /**
     * Extracts the current numerical value from the display.
     * <p>
     * This method parses the display text to find the last number entered,
     * which is useful for scientific operations that work on single numbers.
     *
     * @return The current numerical value, or 0.0 if none can be found
     */
    private double getCurrentDisplayValue() {
        try {
            String displayText = view.getDisplay().getText();
            if (displayText.equals("Error") || displayText.isEmpty()) {
                return 0.0;
            }

            // Extract the last number from the display
            String[] parts = displayText.split("\\s+");
            for (int i = parts.length - 1; i >= 0; i--) {
                try {
                    return Double.parseDouble(parts[i]);
                } catch (NumberFormatException ignored) {
                }
            }

            return model.getCurrentValue();
        } catch (Exception e) {
            return 0.0;
        }
    }
}
