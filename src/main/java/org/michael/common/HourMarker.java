package org.michael.common;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Map;

/**
 * Created on 2019-09-16 11:20
 * Author : Michael.
 */
public class HourMarker {

    private final static String LEFT_BRACE = "{";
    private final static char RIGHT_BRACE = '}';

    public static void parse(String text, StringBuilder out, Map<Object, Object> context, String hourVariable) {
        int currentIndex = 0;
        while (currentIndex < text.length()) {
            if (text.charAt(currentIndex) != '$') {
                out.append(text.charAt(currentIndex));
                currentIndex++;
                continue;
            } else {
                char dollar = text.charAt(currentIndex);
                currentIndex++;
                ImmutablePair<Match, Integer> result = readHourVariable(text, currentIndex, hourVariable);
                if (result.left == null) {
                    out.append(dollar);
                    continue;
                } else {
                    String str = result.left.generateString(context, hourVariable);
                    out.append(str);
                    currentIndex = result.right;
                    continue;
                }
            }
        }
    }

    private static ImmutablePair<Match, Integer> readHourVariable(String text, int beginIndex, String hourVariable) {
        int currentIndex = beginIndex;

        String leftBrace = null;
        if (currentIndex < text.length()) {
            leftBrace = String.valueOf(text.charAt(currentIndex));
        }

        if (!LEFT_BRACE.equals(leftBrace)) {
            return ImmutablePair.of(Match.nullMatch(), -1);
        }

        currentIndex++;
        currentIndex = skipWhites(text, currentIndex);
        if (currentIndex >= text.length()) {
            return ImmutablePair.of(Match.nullMatch(), -1);
        }

        ImmutablePair<Boolean, Integer> readDateString = readHourString(text, currentIndex, hourVariable);
        if (readDateString.left == false) {
            return ImmutablePair.of(Match.nullMatch(), -1);
        }

        currentIndex = readDateString.right;
        if (currentIndex >= text.length()) {
            return ImmutablePair.of(Match.nullMatch(), -1);
        }

        currentIndex = skipWhites(text, currentIndex);
        if (currentIndex >= text.length()) {
            return ImmutablePair.of(Match.nullMatch(), -1);
        }

        String opStr = null;
        int delta = -1;

        if (isOperator(text, currentIndex)) {
            char op = readOperator(text, currentIndex);
            currentIndex++;
            currentIndex = skipWhites(text, currentIndex);
            ImmutablePair<String, Integer> digitsResult = readDigits(text, currentIndex);

            if (digitsResult.left == null) {
                return ImmutablePair.of(Match.nullMatch(), -1);
            } else {
                opStr = String.valueOf(op);
                delta = Integer.parseInt(digitsResult.left);
                currentIndex = digitsResult.right;
            }
        }

        currentIndex = skipWhites(text, currentIndex);
        if (currentIndex >= text.length()) {
            return ImmutablePair.of(Match.nullMatch(), -1);
        }

        if (text.charAt(currentIndex) != RIGHT_BRACE) {
            return ImmutablePair.of(Match.nullMatch(), -1);
        } else {
            currentIndex++;
            Match match = new Match();
            match.delta = delta;
            match.op = opStr;
            return ImmutablePair.of(match, currentIndex);
        }
    }

    private static char readOperator(String text, int beginIndex) {
        char op = text.charAt(beginIndex);
        beginIndex++;
        return op;
    }

    private static boolean isOperator(String text, int index) {
        return text.charAt(index) == '-' || text.charAt(index) == '+';
    }

    private static ImmutablePair<String, Integer> readDigits(String text, int beginIndex) {
        int currentIndex = beginIndex;
        StringBuilder builder = new StringBuilder();

        while (currentIndex < text.length()) {
            if (isDigit(text.charAt(currentIndex))) {
                builder.append(text.charAt(currentIndex));
                currentIndex++;
            } else {
                break;
            }
        }
        String digitStr = builder.length() == 0 ? null : builder.toString();
        return ImmutablePair.of(digitStr, currentIndex);
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static ImmutablePair<Boolean, Integer> readHourString(String text, int beginIndex, String hourVariable) {
        int currentIndex = beginIndex;

        if ((currentIndex + hourVariable.length()) > text.length()) {
            return ImmutablePair.of(false, -1);
        }
        StringBuilder builder = new StringBuilder();

        for (int i = currentIndex; i < (beginIndex + hourVariable.length()); i++) {
            builder.append(text.charAt(i));
            currentIndex++;
        }
        String readStr = builder.toString();
        if (readStr.equalsIgnoreCase(hourVariable)) {
            return ImmutablePair.of(true, currentIndex);
        } else {
            return ImmutablePair.of(false, 1);
        }
    }

    private static int skipWhites(String text, int whiteBeginIndex) {
        int currentIndex = whiteBeginIndex;
        while (currentIndex < text.length()) {
            if (text.charAt(currentIndex) == ' ' || text.charAt(currentIndex) == '\t' || text.charAt(currentIndex) == '\n'
                    || text.charAt(currentIndex) == '\r') {
                currentIndex++;
            } else {
                break;
            }
        }
        return currentIndex;
    }

    private static class Match {
        int delta = -1;
        String op = null;

        public static Match nullMatch() {
            return null;
        }

        public String generateString(Map<Object, Object> context, String hourKey) {
            Object value = context.get(hourKey);
            if (value == null) {
                return "null";
            } else {
                String str = value.toString();
                if (op == null) {
                    return str;
                }

                if (delta == -1) {
                    throw new RuntimeException("Operator[ " + op + " ] with delta -1 ");
                }

                delta = delta % 24;
                int hourInt = Integer.parseInt(str);
                hourInt = hourInt + 24;

                if (op.equalsIgnoreCase("-")) {
                    hourInt = hourInt - delta;
                } else {
                    hourInt = hourInt + delta;
                }

                hourInt = hourInt % 24;
                return String.valueOf(hourInt);
            }
        }
    }

}
