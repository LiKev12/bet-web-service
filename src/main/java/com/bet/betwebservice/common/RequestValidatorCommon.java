package com.bet.betwebservice.common;

import java.text.SimpleDateFormat;

import org.apache.commons.validator.routines.EmailValidator;

public class RequestValidatorCommon {
    public static boolean _uuid(String uuidInput) {
        if (uuidInput == null) {
            return false;
        }
        return true;
    }

    public static boolean timestampRequired(Integer timestampInput) {
        return Utilities.isValidTimestamp(timestampInput);
    }

    public static boolean timestampOptional(Integer timestampInput) {
        if (timestampInput == null) {
            return true;
        }
        return timestampInput > 0;
    }

    public static boolean datetimeOptional(String datetimeInput) {
        if (datetimeInput == null) {
            return true;
        }
        // https://stackoverflow.com/questions/33968333/how-to-check-if-a-string-is-date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(datetimeInput);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static boolean imageRequired(String imageInput) {
        if (imageInput == null || imageInput.isEmpty()) {
            return false;
        }
        if (!imageInput.startsWith("data:image")) {
            return false;
        }
        return true;
    }

    public static boolean imageOptional(String imageInput) {
        if (imageInput == null || imageInput.isEmpty()) {
            return true;
        }
        if (!imageInput.startsWith("data:image")) {
            return false;
        }
        return true;
    }
    
    public static boolean stringRequired(String stringInput, int stringInputLengthLowerBound, int stringInputLengthUpperBound) {
        if (stringInput == null || stringInput.isEmpty()) {
            return false;
        }
        if (stringInput.length() < stringInputLengthLowerBound || stringInput.length() > stringInputLengthUpperBound) {
            return false;
        }
        return true;
    }

    public static boolean stringOptional(String stringInput, int stringInputLengthLowerBound, int stringInputLengthUpperBound) {
        if (stringInput == null || stringInput.isEmpty()) {
            return true;
        }
        if (stringInput.length() < stringInputLengthLowerBound || stringInput.length() > stringInputLengthUpperBound) {
            return false;
        }
        return true;
    }

    public static boolean stringLowerCase(String stringInput) {
        if (stringInput == null || stringInput.isEmpty()) {
            return true;
        }
        return stringInput.toLowerCase().equals(stringInput);
    }



    public static boolean email(String emailInput) {
        return EmailValidator.getInstance().isValid(emailInput);
    }

}
