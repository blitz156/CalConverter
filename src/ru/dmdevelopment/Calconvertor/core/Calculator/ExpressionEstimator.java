package ru.dmdevelopment.Calconvertor.core.Calculator;

import java.math.BigInteger;

/**
 * ***************************************************
 * Copyright (c/c++) 2013-doomsday by Alexey Slovesnov
 * homepage http://slovesnov.narod.ru/indexe.htm
 * email slovesnov@yandex.ru
 * All rights reserved.
 * ****************************************************
 * Add PERCENT & FACTORIAL & LB func, and remake POW, LN func by blitz
 * ****************************************************
 * */

public class ExpressionEstimator {

    private static TRIGONOMETRIC_OPTIONS trigonometricVal = TRIGONOMETRIC_OPTIONS.RAD; // default RAD

    private enum OPERATOR {
        //note OPERATOR enums is case sensitive (cause use OPERATOR.valueof())! use only capital characters,names equal to parsing string
        PLUS, MINUS, MULTIPLY, DIVIDE, POW, PERCENT, LEFT_BRACKET, RIGHT_BRACKET, LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET, LEFT_CURLY_BRACKET, RIGHT_CURLY_BRACKET, COMMA //PLUS should be first in enum. Common operators should go in a row. Order is important.
        , SIN, COS, FACT, TAN, COT, SEC, CSC, ASIN, ACOS, ATAN, ACOT, ASEC, ACSC, SINH, COSH, TANH, COTH, SECH, CSCH, ASINH, ACOSH, ATANH, ACOTH, ASECH, ACSCH, RANDOM, CEIL, FLOOR, ROUND, ABS, EXP, LG, LB, LN, SQRT, ATAN2, MIN, MAX//Functions from 'sin' to 'max' (should go in a row), first function should be 'sin', last should be 'max'
        , X, NUMBER, UNARY_MINUS, END
    }
    //POW,ATAN2,MIN,MAX should go in a row see parse3 function

    private enum CONSTANT_NAME {
        PI, E, SQRT2, SQRT1_2, LN2, LN10, LOG2E, LOG10E //Constants should go in a row. Order is important
    }

    private static final double CONSTANT_VALUE[] = {
            Math.PI, Math.E, Math.sqrt(2), Math.sqrt(.5), Math.log(2), Math.log(10), 1. / Math.log(2), 1. / Math.log(10)
    };

    public enum  TRIGONOMETRIC_OPTIONS  {
        RAD, DEG
    }

    private Node root = null;
    private byte[] expression;
    private double tokenValue;
    private OPERATOR operator;
    private int position;
    private double[] argument;
    private int arguments;

    private class Node {
        OPERATOR operator;
        double value;
        Node left, right;
        boolean persentFlag;

        private void init(OPERATOR operator, double value, Node left) {
            this.operator = operator;
            this.value = value;
            this.left = left;
        }

        Node(OPERATOR operator, Node left) {
            init(operator, 0, left);
        }

        Node(OPERATOR operator) {
            init(operator, 0, null);
        }

        Node(OPERATOR operator, double value) {
            init(operator, value, null);
        }

        double calculate() throws Exception {
            double x;
            switch (operator) {

                case NUMBER:
                    return value;

                case PLUS: {
                    if (persentFlag) {
                        return left.calculate() + (left.calculate() * right.calculate() / 100);
                    }
                    else return left.calculate() + right.calculate();
                }

                case MINUS: {
                    if (persentFlag) {
                        return left.calculate() - (left.calculate() * right.calculate() / 100);
                    }
                    else return left.calculate() - right.calculate();
                }

                case MULTIPLY:
                    if (persentFlag) {
                        return left.calculate() * (right.calculate() / 100);
                    }
                    return left.calculate() * right.calculate();

                case DIVIDE:
                    if (persentFlag) {
                        return left.calculate() / (right.calculate() / 100);
                    }
                    return left.calculate() / right.calculate();

                case POW:
                    return Math.pow(left.calculate(), right.calculate());

                case FACT:
                    return factor((int)left.calculate());

                case UNARY_MINUS:
                    return -left.calculate();

                case SIN:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.sin(left.calculate() / 180 * Math.PI);
                    }
                    return Math.sin(left.calculate());

                case COS:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.cos(left.calculate() / 180 * Math.PI);
                    }
                    return Math.cos(left.calculate());

                case TAN:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.tan(left.calculate() / 180 * Math.PI);
                    }
                    return Math.tan(left.calculate());

                case COT:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return 1 / Math.tan(left.calculate() / 180 * Math.PI);
                    }
                    return 1 / Math.tan(left.calculate());

                case SEC:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return 1 / Math.cos(left.calculate() / 180 * Math.PI);
                    }
                    return 1 / Math.cos(left.calculate());

                case CSC:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return 1 / Math.sin(left.calculate() / 180 * Math.PI);
                    }
                    return 1 / Math.sin(left.calculate());

                case ASIN:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.asin(left.calculate() / 180 * Math.PI);
                    }
                    return Math.asin(left.calculate());

                case ACOS:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.acos(left.calculate() / 180 * Math.PI);
                    }
                    return Math.acos(left.calculate());

                case ATAN:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.atan(left.calculate() / 180 * Math.PI);
                    }
                    return Math.atan(left.calculate());

                case ACOT:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.PI / 2 - Math.atan(left.calculate() / 180 * Math.PI);
                    }
                    return Math.PI / 2 - Math.atan(left.calculate());

                case ASEC:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.acos((1 / left.calculate()) / 180 * Math.PI);
                    }
                    return Math.acos(1 / left.calculate());

                case ACSC:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.asin((1 / left.calculate()) / 180 * Math.PI);
                    }
                    return Math.asin(1 / left.calculate());

                case SINH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return (Math.exp(x) - Math.exp(-x)) / 2;

                case COSH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return (Math.exp(x) + Math.exp(-x)) / 2;

                case TANH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return (Math.exp(2 * x) - 1) / (Math.exp(2 * x) + 1);

                case COTH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return (Math.exp(2 * x) + 1) / (Math.exp(2 * x) - 1);

                case SECH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return 2 / (Math.exp(x) + Math.exp(-x));

                case CSCH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return 2 / (Math.exp(x) - Math.exp(-x));

                case ASINH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return Math.log(x + Math.sqrt(x * x + 1));

                case ACOSH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return Math.log(x + Math.sqrt(x * x - 1));

                case ATANH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return Math.log((1 + x) / (1 - x)) / 2;

                case ACOTH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return Math.log((x + 1) / (x - 1)) / 2;

                case ASECH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return Math.log((1 + Math.sqrt(1 - x * x)) / x);

                case ACSCH:
                    x = left.calculate();
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        x = x / 180 * Math.PI;
                    }
                    return Math.log(1 / x + Math.sqrt(1 + x * x) / Math.abs(x));

                case RANDOM:
                    return Math.random();

                case CEIL:
                    return Math.ceil(left.calculate());

                case FLOOR:
                    return Math.floor(left.calculate());

                case ROUND:
                    return Math.round(left.calculate());

                case ABS:
                    return Math.abs(left.calculate());

                case EXP:
                    return Math.exp(left.calculate());

                case LN:
                    return Math.log(left.calculate());

                case LB:
                    return Math.log(left.calculate()) / Math.log(2.0);

                case LG:
                    return Math.log10(left.calculate());

                case SQRT:
                    return Math.sqrt(left.calculate());

                case ATAN2:
                    if (trigonometricVal == TRIGONOMETRIC_OPTIONS.DEG) {
                        return Math.atan2(left.calculate() / 180 * Math.PI, right.calculate() / 180 * Math.PI);
                    }
                    return Math.atan2(left.calculate(), right.calculate());

                case MIN:
                    return Math.min(left.calculate(), right.calculate());

                case MAX:
                    return Math.max(left.calculate(), right.calculate());

                case X:
                    return argument[(int) value];

                default:
                    throw new Exception("Node.calculate error");
            }
        }
    }

    private double factor(int x) {
        BigInteger fact = new BigInteger("1");
        for (int i = 1; i <= x; i++) {
            fact = fact.multiply(new BigInteger(i + ""));
        }
        return Double.parseDouble(fact.toString());
    }

    private boolean isLetter() {
        return Character.isLetter(expression[position]);
    }

    private boolean isDigit() {
        return Character.isDigit(expression[position]);
    }

    private boolean isPoint() {
        return expression[position] == '.';
    }

    private boolean isFunctionSymbol() {
        byte c = expression[position];
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private void getToken() throws Exception {
        int i;

        if (position == expression.length - 1) {
            operator = OPERATOR.END;
        } else if ((i = "+-*/^%()[]{},".indexOf(expression[position])) != -1) {
            position++;
            operator = OPERATOR.values()[i];
        } else if (isLetter()) {
            for (i = position++; isFunctionSymbol(); position++) ;
            String token = new String(expression, i, position - i);
            ;

            try {
                if (token.charAt(0) == 'X' && token.length() == 1) {
                    throw new Exception("unknown keyword");
                } else if (token.charAt(0) == 'X' && token.length() > 1 && Character.isDigit(token.charAt(1))) {
                    i = Integer.parseInt(token.substring(1));
                    if (i < 0) {
                        throw new Exception("index of 'x' should be nonnegative integer number");
                    }
                    if (arguments < i + 1) {
                        arguments = i + 1;
                    }
                    operator = OPERATOR.X;
                    tokenValue = i;
                } else {
                    operator = OPERATOR.valueOf(token);
                    i = operator.ordinal();
                    if (i < OPERATOR.SIN.ordinal() || i > OPERATOR.MAX.ordinal()) {
                        throw new IllegalArgumentException();
                    }
                }
            } catch (IllegalArgumentException _ex) {
                try {
                    tokenValue = CONSTANT_VALUE[CONSTANT_NAME.valueOf(token).ordinal()];
                    operator = OPERATOR.NUMBER;
                } catch (IllegalArgumentException ex) {
                    throw new Exception("unknown keyword");
                }
            }
        } else if (isDigit() || isPoint()) {
            for (i = position++; isDigit() || isPoint() || expression[position] == 'E'
                    || expression[position - 1] == 'E' && "+-".indexOf(expression[position]) != -1; position++)
                ;
            tokenValue = Double.parseDouble(new String(expression, i, position - i));
            operator = OPERATOR.NUMBER;
        } else {
            throw new Exception("unknown symbol");
        }

    }

    public static void setTrigonometricOptions(TRIGONOMETRIC_OPTIONS val) {
        trigonometricVal = val;
    }

    public void compile(String expression) throws Exception {
        position = 0;
        arguments = 0;
        String s = expression.toUpperCase();//for OPERATOR.valueof()

        String from[] = {" ", "\t"};
        for (int i = 0; i < from.length; i++) {
            s = s.replace(from[i], "");
        }
        this.expression = (s + '\0').getBytes();

        getToken();
        if (operator == OPERATOR.END) {
            throw new Exception("unexpected end of expression");
        }
        root = parse();
        if (operator != OPERATOR.END) {
            throw new Exception("end of expression expected");
        }
    }

    private Node parse() throws Exception {
        Node node = parse1();
        while (operator == OPERATOR.PLUS || operator == OPERATOR.MINUS) {
            node = new Node(operator, node);
            getToken();
            if (operator == OPERATOR.PLUS || operator == OPERATOR.MINUS) {
                throw new Exception("two operators in a row");
            }
            node.right = parse1();
            if (operator == OPERATOR.PERCENT) {
                node.persentFlag = true;
                getToken();
            }
        }
        return node;
    }

    private Node parse1() throws Exception {
        Node node = parse2();
        while (operator == OPERATOR.MULTIPLY || operator == OPERATOR.DIVIDE
                || operator == OPERATOR.POW) {
            node = new Node(operator, node);
            getToken();
            if (operator == OPERATOR.PLUS || operator == OPERATOR.MINUS) {
                throw new Exception("two operators in a row");
            }
            node.right = parse2();
            if (operator == OPERATOR.PERCENT) {
                node.persentFlag = true;
                getToken();
            }
        }
        return node;
    }

    private Node parse2() throws Exception {
        Node node;
        if (operator == OPERATOR.MINUS) {
            getToken();
            node = new Node(OPERATOR.UNARY_MINUS, parse3());
        } else {
            if (operator == OPERATOR.PLUS) {
                getToken();
            }
            node = parse3();
        }
        return node;
    }

    private Node parse3() throws Exception {
        Node node = null;
        OPERATOR open;

        if (operator.ordinal() >= OPERATOR.SIN.ordinal() && operator.ordinal() <= OPERATOR.MAX.ordinal()) {
            int arguments;
            if (operator.ordinal() >= OPERATOR.ATAN2.ordinal() && operator.ordinal() <= OPERATOR.MAX.ordinal()) {
                arguments = 2;
            } else {
                arguments = operator == OPERATOR.RANDOM ? 0 : 1;
            }

            node = new Node(operator);

            getToken();
            open = operator;
            if (operator != OPERATOR.LEFT_BRACKET && operator != OPERATOR.LEFT_SQUARE_BRACKET && operator != OPERATOR.LEFT_CURLY_BRACKET) {
                throw new Exception("open bracket expected");
            }
            getToken();

            if (arguments > 0) {
                node.left = parse();

                if (arguments == 2) {
                    if (operator != OPERATOR.COMMA) {
                        throw new Exception("comma expected");
                    }
                    getToken();
                    node.right = parse();
                }
            }
            checkBracketBalance(open);
        } else {
            switch (operator) {

                case X:
                case NUMBER:
                    node = new Node(operator, tokenValue);
                    break;

                case LEFT_BRACKET:
                case LEFT_SQUARE_BRACKET:
                case LEFT_CURLY_BRACKET:
                    open = operator;
                    getToken();
                    node = parse();
                    checkBracketBalance(open);
                    break;

                default:
                    throw new Exception("unexpected operator");
            }
        }
        getToken();
        return node;
    }

    private void checkBracketBalance(OPERATOR open) throws Exception {
        if (open == OPERATOR.LEFT_BRACKET && operator != OPERATOR.RIGHT_BRACKET ||
                open == OPERATOR.LEFT_SQUARE_BRACKET && operator != OPERATOR.RIGHT_SQUARE_BRACKET ||
                open == OPERATOR.LEFT_CURLY_BRACKET && operator != OPERATOR.RIGHT_CURLY_BRACKET) {
            throw new Exception("close bracket expected or another type of close bracket");
        }
    }

    public double calculate(double[] x) throws Exception {
        this.argument = x;
        return calculate();
    }

    public double calculate() throws Exception {
        if (root == null) {
            throw new Exception("using of calculate() without compile()");
        }
        int length = argument == null ? 0 : argument.length;
        if (length != arguments) {
            throw new Exception("invalid number of expression arguments");
        }
        return root.calculate();
    }

    /**
     * @return number of expression arguments
     */
    public int getArguments() {
        return arguments;
    }

    public static double calculate(String s) throws Exception {
        ExpressionEstimator estimator = new ExpressionEstimator();
        estimator.compile(s);
        estimator.argument = null;//clear all arguments
        return estimator.calculate();
    }
}
