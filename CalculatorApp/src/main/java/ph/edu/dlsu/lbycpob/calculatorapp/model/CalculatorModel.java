package ph.edu.dlsu.lbycpob.calculatorapp.model;

/**
 * CalculatorModel serves as the data model and business logic layer for a calculator application.
 * This class manages the calculator's state (current value, display text, result status) and
 * delegates mathematical operations to a Calculator implementation.
 *
 * The model follows the MVC (Model-View-Controller) pattern where this class represents
 * the Model component that handles data and business logic.
 *
 */
public class CalculatorModel {

    /**
     * Small epsilon value used for floating-point comparison to handle precision errors.
     * Values within ±EPSILON of zero are considered to be exactly zero.
     */
    private static final double EPSILON = 1e-12;

    /**
     * The current numerical value stored in the calculator.
     * This represents the main working value for calculations.
     */
    private double currentValue;

    /**
     * The text currently displayed on the calculator screen.
     * This may be different from currentValue during input or error states.
     */
    private String displayText;

    /**
     * Flag indicating whether the display is currently showing a calculation result.
     * When true, the next number input should start a new calculation.
     */
    private boolean isResultDisplayed;

    /**
     * The calculator engine that performs the actual mathematical operations.
     * Uses composition pattern - CalculatorModel "has-a" Calculator.
     */
    public ScientificCalculator calculator;

    /**
     * Constructs a new CalculatorModel with default initial state.
     * Initializes the calculator with a ScientificCalculator implementation,
     * sets current value to 0, display text to "0", and result flag to false.
     */
    public CalculatorModel() {
        // Create a scientific calculator instance for advanced operations
        this.calculator = new ScientificCalculator();

        // Initialize the calculator to a clean state
        this.currentValue = 0.0;           // Start with zero value
        this.displayText = "0";            // Display shows "0" initially
        this.isResultDisplayed = false;    // No result is currently displayed
    }

    // ==================== GETTER METHODS (Encapsulation) ====================
    // These methods provide read-only access to private fields

    /**
     * Gets the current numerical value stored in the calculator.
     *
     * @return the current value as a double
     */
    public double getCurrentValue() {
        return currentValue;
    }

    /**
     * Gets the text currently displayed on the calculator screen.
     *
     * @return the display text as a String
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Checks whether the display is currently showing a calculation result.
     *
     * @return true if a result is displayed, false otherwise
     */
    public boolean isResultDisplayed() {
        return isResultDisplayed;
    }

    // ==================== SETTER METHODS (Encapsulation) ====================
    // These methods provide controlled write access to private fields

    /**
     * Sets the current numerical value of the calculator.
     *
     * @param value the new current value to set
     */
    public void setCurrentValue(double value) {
        this.currentValue = value;
    }

    /**
     * Sets the text to be displayed on the calculator screen.
     *
     * @param text the new display text to set
     */
    public void setDisplayText(String text) {
        this.displayText = text;
    }

    /**
     * Sets the flag indicating whether a result is currently displayed.
     *
     * @param displayed true if a result is displayed, false otherwise
     */
    public void setResultDisplayed(boolean displayed) {
        this.isResultDisplayed = displayed;
    }

    // ==================== CALCULATOR OPERATIONS (Delegation Pattern) ====================
    // These methods delegate mathematical operations to the calculator instance

    /**
     * Performs a basic mathematical operation on two operands.
     * This method delegates the actual calculation to the calculator instance.
     *
     * @param operation the operation to perform (e.g., "+", "-", "*", "/")
     * @param operand1 the first operand
     * @param operand2 the second operand
     * @return the result of the operation
     * @throws ArithmeticException if division by zero or other mathematical errors occur
     */
    public double performOperation(String operation, double operand1, double operand2) {
        // Delegate the operation to the calculator engine
        return calculator.performOperation(operation, operand1, operand2);
    }

    /**
     * Performs a scientific mathematical operation on a single operand.
     * Only works if the calculator is a ScientificCalculator instance.
     *
     * @param operation the scientific operation to perform (e.g., "sin", "cos", "log")
     * @param operand the operand to perform the operation on
     * @return the result of the scientific operation, or the original operand if not supported
     */
    public double performScientificOperation(String operation, double operand) {
        // Check if our calculator supports scientific operations
        if (calculator instanceof ScientificCalculator) {
            // Cast to ScientificCalculator to access scientific methods
            return calculator.performScientificOperation(operation, operand);
        }
        // If not a scientific calculator, return the operand unchanged
        return operand;
    }

    /**
     * Evaluates a complete mathematical expression as a string.
     * Handles complex expressions with multiple operations and parentheses.
     *
     * @param expression the mathematical expression to evaluate (e.g., "2 + 3 * 4")
     * @return the result of evaluating the expression, rounded if close to zero
     * @throws IllegalArgumentException if the expression is malformed
     */
    public double evaluateExpression(String expression) {
        // Delegate to calculator and apply zero-rounding for cleaner results
        return roundIfCloseToZero(calculator.evaluateExpression(expression));
    }

    /**
     * Resets the calculator to its initial state.
     * Clears the current value, resets display text to "0", and clears the result flag.
     * This is equivalent to pressing a "Clear" or "Reset" button.
     */
    public void clear() {
        this.currentValue = 0.0;           // Reset value to zero
        this.displayText = "0";            // Reset display to show "0"
        this.isResultDisplayed = false;    // Clear the result flag
    }

    /**
     * Rounds values very close to zero to exactly 0.0 to handle floating-point precision errors.
     * This prevents displaying tiny values like 1.2345e-16 when the result should be 0.
     *
     * For example: Math.sin(Math.PI) theoretically equals 0, but due to floating-point
     * precision, it might return something like 1.2246467991473532e-16.
     *
     * @param value the value to check and potentially round
     * @return 0.0 if the absolute value is less than EPSILON, otherwise the original value
     */
    private double roundIfCloseToZero(double value) {
        // If the absolute value is smaller than our epsilon threshold, consider it zero
        return Math.abs(value) < EPSILON ? 0.0 : value;
    }

    /**
     * Formats a numerical result for display.
     * <p>
     * This method removes unnecessary decimal places for whole numbers
     * and limits precision for decimal numbers to keep the display clean.
     *
     * @param result The numerical result to format
     * @return A formatted string representation of the result
     */
    public String formatResult(double result) {
        if (!Double.isFinite(result)) {
            return Double.toString(result);
        }

        // Force scientific notation for very large or very small numbers
        double absResult = Math.abs(result);
        if (absResult >= 1e10 || (absResult > 0 && absResult < 1e-4)) {
            return String.format("%.10e", result)
                    .replaceAll("\\.?0*e", "e")            // Remove trailing zeros from mantissa
                    .replaceAll("e([+-])0+(\\d)", "e$1$2") // Remove leading zeros from exponent
                    .replaceAll("e([+-])0+$", "e$10");     // Handle exponent of just "0"
        }

        if (result == Math.floor(result)) { // Integer
            return String.format("%.0f", result);
        } else { // Decimal
            return String.format("%.10g", result).replaceAll("\\.?0*$", "");
        }
    }
}