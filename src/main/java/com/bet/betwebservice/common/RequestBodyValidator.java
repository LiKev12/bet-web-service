package com.bet.betwebservice.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.validator.routines.EmailValidator;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

public class RequestBodyValidator {

    public static void verifyRequiredFieldNamesExist(JsonNode rb, List<String> fieldNames) throws Exception {
        for (int i = 0; i < fieldNames.size(); i++) {
            if (!rb.has(fieldNames.get(i))) {
                throw new Exception("REQUEST_BODY_MISSING_REQUIRED_FIELD_NAME");
            }
        }
    }

    public static String uuid(String input) throws Exception {
        if (input == null) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }

    public static int intRequired(Integer input, int lowerLimit, int upperLimit) throws Exception {
        if (input == null) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        if (input < lowerLimit || input > upperLimit) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }

    public static Integer timestampRequired(Integer input) throws Exception {
        if (input == null) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        if (input <= 0) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }

    public static Integer timestampOptional(Integer input) throws Exception {
        if (input == null) {
            return input;
        }
        if (input <= 0) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }

    public static String datetimeOptional(String input) throws Exception {
        if (input == null) {
            return input;
        }
        // https://stackoverflow.com/questions/33968333/how-to-check-if-a-string-is-date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(input);
        } catch (Exception e) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }
    
    public static String imageRequired(String input) throws Exception {
        if (input == null) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        if (!input.startsWith("data:image")) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }

    public static String imageOptional(String input) throws Exception {
        if (input == null) {
            return input;
        }
        if (!input.startsWith("data:image")) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }
    
    public static String stringRequired(String input, int inputLengthLowerBound, int inputLengthUpperBound) throws Exception {
        if (input == null) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        if (input.length() < inputLengthLowerBound || input.length() > inputLengthUpperBound) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }

    public static String stringOptional(String input, int inputLengthLowerBound, int inputLengthUpperBound) throws Exception {
        if (input == null) {
            return input;
        }
        if (input.length() < inputLengthLowerBound || input.length() > inputLengthUpperBound) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }
    
    public static String stringLowerCase(String input) throws Exception {
        if (input == null) {
            return input;
        }
        if (!input.toLowerCase().equals(input)) {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
        return input;
    }

    public static String stringChoice(String input, Set<String> choices) throws Exception {
        if (choices.contains(input)) {
            return input;
        } else {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
    }

    public static String stringRegex(String input, String regexPattern) throws Exception {
        if (input.matches(regexPattern)) {
            return input;
        } else {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
    }

    public static String email(String input) throws Exception {
        if (EmailValidator.getInstance().isValid(input)) {
            return input;
        } else {
            throw new Exception("REQUEST_BODY_INVALID_VALUE");
        }
    }

}
