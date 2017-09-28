package com.mikaelkulma.kata.stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class StringCalculator {

    private Logger logger;
    private ExceptionWebService webService;

    StringCalculator(Logger sumLogger, ExceptionWebService exceptionWebService) {
        logger = sumLogger;
        webService = exceptionWebService;
    }

    int add(String input) {

        int sum = 0;

        if (input.isEmpty()) {
            return sum;
        }

        try {
            final List<String> delimiters = inputToDelimiterList(input);
            final List<String> addends = splitInputToAddends(input, delimiters);
            sum = calculateSum(addends);

            logger.log(Integer.toString(sum));
        } catch (Exception e) {
            webService.notifyOfException("Logging has failed:" + e.getMessage());
            throw e;
        }
        return sum;
    }

    private List<String> inputToDelimiterList(String input) {
        List<String> delimiters = getDefaultDelimiters();
        delimiters.addAll(getUSerDefinedDelimiters(input));
        return delimiters;
    }

    private List<String> getDefaultDelimiters() {
        return new ArrayList<>(Arrays.asList(",", "\n"));
    }

    private List<String> getUSerDefinedDelimiters(String input) {
        List<String> delimiters = new ArrayList<>();
        if (inputContainsADelimiterDefinitionMarker(input)) {
            delimiters.addAll(inputToDelimiterDefinitionToList(input));
        }
        return delimiters;
    }

    private boolean inputContainsADelimiterDefinitionMarker(String input) {
        return input.startsWith("//");
    }

    private List<String> inputToDelimiterDefinitionToList(String input) {
        String delimiterDefinition = readFirstLineOfInputAfterMarker(input);
        return delimiterDefinitionToDelimiterList(delimiterDefinition);
    }

    private String readFirstLineOfInputAfterMarker(String input) {
        return input.substring(2, input.indexOf("\n"));
    }

    private List<String> delimiterDefinitionToDelimiterList(String delimiterDefinition) {
        List<String> delimiters = new ArrayList<>();
        if (delimiterDefinitionContainsMultipleDelimiters(delimiterDefinition)) {
            delimiters.addAll(delimiterDefinitionToMultipleDelimiters(delimiterDefinition));
        } else {
            delimiters.add(delimiterDefinition);
        }
        return delimiters;
    }

    private boolean delimiterDefinitionContainsMultipleDelimiters(String delimiterDefinition) {
        return delimiterDefinition.contains("[");
    }

    private List<String> delimiterDefinitionToMultipleDelimiters(String delimiterDefinition) {
        return Arrays.asList(delimiterDefinition.replaceAll("^\\[|\\]$", "")
                .split("\\]\\["));
    }

    private List<String> splitInputToAddends(String input, List<String> delimiters) {
        if (inputContainsADelimiterDefinitionMarker(input)) {
            input = skipFirstLineOfInput(input);
        }
        return splitInputByDelimiters(input, delimiters);
    }

    private String skipFirstLineOfInput(String input) {
        return input.substring(input.indexOf("\n") + 1);
    }

    private List<String> splitInputByDelimiters(String input, List<String> delimiters) {
        String regexString = delimitersToRegex(delimiters);
        return splitInputByRegex(input, regexString);
    }

    private String delimitersToRegex(List<String> delimiters) {
        StringBuilder regexString = new StringBuilder(delimiters.toString());
        return regexString.insert(0, "[").append("]").toString();
    }

    private List<String> splitInputByRegex(String input, String regexString) {
        return Arrays.asList(input.split(regexString));
    }

    private int calculateSum(List<String> addends) {
        int sum = 0;

        for (String addend : addends) {
            sum += getAddendValue(addend);
        }
        return sum;
    }

    private int getAddendValue(String addend) {
        int value = castAddendToInt(addend);
        value = zeroValueIfAbove1k(value);
        return value;
    }

    private int castAddendToInt(String addend) {
        return Integer.parseInt(0 + addend);
    }

    private int zeroValueIfAbove1k(int value) {
        return  (value >= 1000)? 0:value;
    }
}
