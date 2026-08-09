package ph.edu.dlsu.lbycpob.calculatorapp.model;

/**
 * ScientificCalculator extends the abstract Calculator class to provide both basic
 * arithmetic operations and advanced scientific mathematical functions.
 *
 * This class implements the Template Method pattern by providing concrete implementations
 * of the abstract methods defined in the Calculator base class. It supports:
 *
 * Basic Operations:
 * - Addition (+), Subtraction (-), Multiplication (*), Division (/)
 * - Exponentiation (^), Modulo (mod)
 *
 * Scientific Operations:
 * - Trigonometric functions (sin, cos, tan, asin, acos, atan)
 * - Logarithmic functions (log, ln)
 * - Root functions (sqrt, cbrt)
 * - Other mathematical functions (exp, abs, factorial, reciprocal)
 *
 * Key Features:
 * - Automatic angle conversion (degrees ↔ radians) for trigonometric functions
 * - Comprehensive error handling for invalid operations
 * - Support for both integer and floating-point calculations
 * - Factorial calculation for non-negative integers
 *
 */
public class ScientificCalculator extends Calculator {

    private boolean isShiftActive;

    /**
     * Performs basic arithmetic operations on two operands.
     *
     * This method implements the abstract performOperation method from the Calculator
     * base class. It handles all standard arithmetic operations with proper error
     * checking and uses modern Java switch expressions for clean, readable code.
     *
     * Supported Operations:
     * - "+" : Addition (operand1 + operand2)
     * - "-" : Subtraction (operand1 - operand2)
     * - "*" : Multiplication (operand1 × operand2)
     * - "/" : Division (operand1 ÷ operand2) with zero-division protection
     * - "^" : Exponentiation (operand1 raised to the power of operand2)
     * - "mod" : Modulo (remainder when operand1 is divided by operand2)
     *
     * Error Handling:
     * - Division by zero throws ArithmeticException
     * - Unsupported operations throw UnsupportedOperationException
     *
     * @param operation The operation symbol as a string ("+", "-", "*", "/", "^", "%")
     * @param operand1 The first number (left operand in the expression)
     * @param operand2 The second number (right operand in the expression)
     * @return The result of performing the specified operation
     * @throws ArithmeticException if division by zero is attempted
     * @throws UnsupportedOperationException if the operation is not recognized
     */
    @Override
    public double performOperation(String operation, double operand1, double operand2) {
        // Use modern Java switch expression for clean, readable code
        // The yield keyword is used when we need multiple statements in a case
        return switch (operation) {
            case "+" -> operand1 + operand2;           // Simple addition
            case "-" -> operand1 - operand2;           // Simple subtraction
            case "*" -> operand1 * operand2;           // Simple multiplication

            case "/" -> {
                // Division requires special handling to prevent division by zero
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                yield operand1 / operand2;  // yield is used instead of return in switch expressions
            }

            case "^" -> Math.pow(operand1, operand2);  // Exponentiation using Math.pow()
            case "%" -> operand1 % operand2;          // Modulo operation (remainder)

            // If we get here, the operation is not supported
            default -> throw new UnsupportedOperationException("Unsupported operation: " + operation);
        };
    }

    /**
     * Performs advanced scientific mathematical operations on a single operand.
     *
     * This method handles complex mathematical functions that typically require only
     * one input value. It includes trigonometric, logarithmic, and other advanced
     * mathematical operations commonly found on scientific calculators.
     *
     * Trigonometric Functions (input/output in degrees):
     * - "sin", "cos", "tan" : Standard trigonometric functions
     * - "asin", "acos", "atan" : Inverse trigonometric functions (arcsine, etc.)
     *
     * Logarithmic Functions:
     * - "log" : Base-10 logarithm (common logarithm)
     * - "ln" : Natural logarithm (base-e logarithm)
     *
     * Root Functions:
     * - "sqrt" : Square root (√x)
     * - "cbrt" : Cube root (∛x)
     *
     * Other Functions:
     * - "exp" : Exponential function (e^x)
     * - "abs" : Absolute value (|x|)
     * - "factorial" : Factorial (x!) for non-negative integers
     * - "reciprocal" : Reciprocal (1/x) with zero-division protection
     *
     * @param operation The name of the scientific operation (case-insensitive)
     * @param operand The number to perform the operation on
     * @return The result of the scientific operation
     * @throws ArithmeticException if division by zero occurs (reciprocal of 0)
     * @throws IllegalArgumentException if factorial is called with invalid input
     * @throws UnsupportedOperationException if the operation is not recognized
     */
    public double performScientificOperation(String operation, double operand) {
        // Convert operation to lowercase for case-insensitive matching
        // This allows users to enter "SIN", "sin", or "Sin" - all will work
        return switch (operation.toLowerCase()) {

            // Trigonometric functions - convert degrees to radians for calculation
            case "sin" -> Math.sin(Math.toRadians(operand));  // sine of angle in degrees
            case "cos" -> Math.cos(Math.toRadians(operand));  // cosine of angle in degrees
            case "tan" -> Math.tan(Math.toRadians(operand));  // tangent of angle in degrees

            // Inverse trigonometric functions - convert result from radians to degrees; No button yet
            case "asin" -> Math.toDegrees(Math.asin(operand));  // arcsine, result in degrees
            case "acos" -> Math.toDegrees(Math.acos(operand));  // arccosine, result in degrees
            case "atan" -> Math.toDegrees(Math.atan(operand));  // arctangent, result in degrees

            // Logarithmic functions
            case "log" -> Math.log10(operand);  // Base-10 logarithm (common log)
            case "ln" -> Math.log(operand);     // Natural logarithm (base-e log)

            // Root functions
            case "sqrt" -> Math.sqrt(operand);  // Square root
            case "cbrt" -> Math.cbrt(operand);  // Cube root; No button yet

            // Exponential and other functions
            case "exp" -> Math.exp(operand);    // e raised to the power of operand
            case "abs" -> Math.abs(operand);    // Absolute value (distance from zero); // no button yet

            // Special functions requiring custom implementation
            case "factorial" -> factorial(operand);  // n! = n × (n-1) × (n-2) × ... × 1

            case "reciprocal" -> { // no button yet
                // Reciprocal (1/x) with protection against division by zero
                if (operand == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                yield 1.0 / operand;
            }

            // Unknown scientific operation
            default -> throw new UnsupportedOperationException("Unsupported scientific operation: " + operation);
        };
    }

    /**
     * Calculates the factorial of a non-negative integer.
     *
     * Factorial (n!) is the product of all positive integers less than or equal to n.
     * For example: 5! = 5 × 4 × 3 × 2 × 1 = 120
     *
     * Mathematical Definition:
     * - 0! = 1 (by definition)
     * - 1! = 1
     * - n! = n × (n-1) × (n-2) × ... × 2 × 1 for n > 1
     *
     * This implementation uses an iterative approach rather than recursion
     * to avoid stack overflow issues with large numbers.
     *
     * Input Validation:
     * - Only accepts non-negative numbers
     * - Only accepts integers (no decimal values like 5.5!)
     * - Throws exception for invalid inputs
     *
     * @param n The number to calculate factorial for (must be non-negative integer)
     * @return The factorial of n (n!)
     * @throws IllegalArgumentException if n is negative or not an integer
     */
    private double factorial(double n) {
        // Validate input: factorial is only defined for non-negative integers
        if (n < 0 || n != Math.floor(n)) {
            throw new IllegalArgumentException("Factorial is only defined for non-negative integers");
        }

        // Initialize result to 1 (this handles the case of 0! = 1)
        double result = 1;

        // Multiply by each integer from 2 up to n
        // We start from 2 because multiplying by 1 doesn't change the result
        for (int i = 2; i <= n; i++) {
            result *= i;
        }

        return result;
    }

    public boolean isShiftActive() {
        return isShiftActive;
    }

    public void setShiftActive(boolean shiftActive) {
        isShiftActive = shiftActive;
    }

    public void toggleShift() {
        setShiftActive(!isShiftActive);
    }
}