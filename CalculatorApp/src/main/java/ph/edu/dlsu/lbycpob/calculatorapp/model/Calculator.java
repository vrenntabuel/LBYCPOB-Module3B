package ph.edu.dlsu.lbycpob.calculatorapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Abstract base class for calculator implementations using the Template Method design pattern.
 * <p>
 * This class provides the fundamental structure and algorithms for mathematical expression
 * evaluation while allowing subclasses to define specific operation implementations.
 * <p>
 * Key Features:
 * - Converts infix expressions (like "2 + 3 * 4") to postfix notation (like "2 3 4 * +")
 * - Evaluates postfix expressions using a stack-based algorithm
 * - Defines operator precedence rules (^ > *,/ > +,-)
 * - Uses Template Method pattern to allow different calculator types
 * <p>
 * The Template Method Pattern:
 * This class defines the skeleton of expression evaluation algorithms, but leaves
 * the specific operation implementations to subclasses. For example, a BasicCalculator
 * might implement simple arithmetic, while a ScientificCalculator might add
 * trigonometric functions.
 * <p>
 * Mathematical Expression Processing:
 * 1. Input: "2 + 3 * 4" (infix notation - operators between operands)
 * 2. Convert to: "2 3 4 * +" (postfix notation - operators after operands)
 * 3. Evaluate postfix using a stack to get the final result
 */
public abstract class Calculator {

    // Mathematical constants available to all calculator types

    /**
     * The mathematical constant π (pi) ≈ 3.14159
     */
    public static final double PI = Math.PI;

    /**
     * The mathematical constant e (Euler's number) ≈ 2.71828
     */
    protected static final double E = Math.E;

    /**
     * Abstract method that must be implemented by subclasses to define specific operations.
     * <p>
     * This is the core of the Template Method pattern - each calculator type
     * (Basic, Scientific, etc.) will implement this method differently to handle
     * the operations they support.
     * <p>
     * Examples of operations:
     * - Basic: "+", "-", "*", "/", "^"
     * - Scientific: might also handle "sin", "cos", "tan", etc.
     *
     * @param operation The operation symbol or name (e.g., "+", "sin")
     * @param operand1  The first number in the operation
     * @param operand2  The second number in the operation
     * @return The result of performing the operation on the two operands
     * @throws ArithmeticException if the operation is invalid or unsupported
     */
    public abstract double performOperation(String operation, double operand1, double operand2);

    public double evaluateExpression(String expression) {
        try {
            return evaluatePostfix(infixToPostfix(expression));
        } catch (Exception e) {
            throw new ArithmeticException("Invalid expression");
        }
    }

    public String infixToPostfix(String infix) {
        StringBuilder result = new StringBuilder();
        Stack<String> stack = new Stack<>(); // Changed to String to handle functions

        // Normalize and tokenize the input
        String[] tokens = tokenizeExpression(infix);

        for (String token : tokens) {
            if (isNumber(token)) {
                result.append(token).append(" ");
            } else if (isFunction(token)) {
                stack.push(token);
            } else if (isUnaryOperator(token)) {
                // Handle unary negation with highest precedence
                // Push it immediately since it has higher precedence than all operators
                while (!stack.isEmpty() && !stack.peek().equals("(") &&
                        precedence(token) <= precedence(stack.peek())) {
                    result.append(stack.pop()).append(" ");
                }
                stack.push(token);
            } else if (isOperator(token)) {
                if (token.equals("^")) {
                    // Right associative
                    while (!stack.isEmpty() && !stack.peek().equals("(") &&
                            !isFunction(stack.peek()) &&
                            precedence(token) < precedence(stack.peek())) {
                        result.append(stack.pop()).append(" ");
                    }
                } else {
                    // Left associative
                    while (!stack.isEmpty() && !stack.peek().equals("(") &&
                            !isFunction(stack.peek()) &&
                            precedence(token) <= precedence(stack.peek())) {
                        result.append(stack.pop()).append(" ");
                    }
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    result.append(stack.pop()).append(" ");
                }
                if (!stack.isEmpty()) {
                    stack.pop(); // Remove the '('
                }
                // If there's a function before the parentheses, pop it
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    result.append(stack.pop()).append(" ");
                }
            }
        }

        while (!stack.isEmpty()) {
            result.append(stack.pop()).append(" ");
        }

        return result.toString().trim();
    }

    private String[] tokenizeExpression(String expression) {
        // Enhanced tokenization to handle scientific functions
        expression = expression.replaceAll("\\s+", "");
        // Handle 'mod' keyword by replacing it with '%' for easier processing
        expression = expression.replaceAll("(?i)mod", "%");
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                current.append(c);
            } else if (Character.isLetter(c)) {
                if (current.length() > 0 && (Character.isDigit(current.charAt(current.length() - 1)) ||
                        current.charAt(current.length() - 1) == '.')) {
                    // Number followed by letter - add the number first
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                current.append(c);
            } else if (c == '-') {
                // Handle unary minus (negation)
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }

                // Check if this minus is unary (negation) or binary (subtraction)
                if (isUnaryMinus(tokens)) {
                    tokens.add("neg"); // Use "neg" to represent unary negation
                } else {
                    tokens.add("-"); // Binary subtraction
                }
            } else {
                // Operator or parenthesis
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                tokens.add(String.valueOf(c));
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens.toArray(new String[0]);
    }

    /**
     * Determines if a minus sign should be treated as unary negation
     *
     * @param tokens The list of tokens processed so far
     * @return true if the minus should be treated as unary negation
     */
    private boolean isUnaryMinus(List<String> tokens) {
        if (tokens.isEmpty()) {
            return true; // Minus at the beginning is unary
        }

        String lastToken = tokens.get(tokens.size() - 1);

        // Unary minus after: (, operators, or functions
        return lastToken.equals("(") ||
                isOperator(lastToken) ||
                isFunction(lastToken);
    }

    protected double evaluatePostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.trim().split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (isFunction(token)) {
                if (stack.isEmpty()) throw new ArithmeticException("Invalid expression");
                double operand = stack.pop();
                stack.push(performScientificOperation(token, operand));
            } else if (isUnaryOperator(token)) {
                // Handle unary negation
                if (stack.isEmpty()) throw new ArithmeticException("Invalid expression");
                double operand = stack.pop();
                stack.push(-operand);
            }  else if (isOperator(token)) {
                if (stack.size() < 2) throw new ArithmeticException("Invalid expression");
                double b = stack.pop();
                double a = stack.pop();
                stack.push(performOperation(token, a, b));
            } else if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            }
        }

        if (stack.size() != 1) throw new ArithmeticException("Invalid expression");
        return stack.pop();
    }

    // Abstract method for scientific operations
    public abstract double performScientificOperation(String operation, double operand);

    protected boolean isOperator(String token) {
        return token.matches("[+\\-*/%^]"); // Added % for modulo
    }

    protected boolean isFunction(String token) {
        return token.matches("sin|cos|tan|asin|acos|atan|log|ln|sqrt|cbrt|exp|abs|factorial");
    }

    // Add a separate method to check if token is a unary operator
    protected boolean isUnaryOperator(String token) {
        return token.equals("neg");
    }


    protected boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
            case "%": // Modulo has the same precedence as multiplication and division
                return 2;
            case "^":
                return 3;
            case "neg":
                return 4; // Unary negation has higher precedence
            default:
                return -1;
        }
    }
}