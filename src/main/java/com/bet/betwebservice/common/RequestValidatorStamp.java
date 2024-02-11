package com.bet.betwebservice.common;

import java.util.List;

public class RequestValidatorStamp {
    public static boolean id(String idInput) {
        return RequestValidatorCommon._uuid(idInput);
    }

    public static boolean timestampUnix(Integer timestamp) {
        return RequestValidatorCommon.timestampRequired(timestamp);
    }

    public static boolean idUserCreate(String idUserCreateInput) {
        return RequestValidatorCommon._uuid(idUserCreateInput);
    }

    public static boolean name(String nameInput) {
        return RequestValidatorCommon.stringRequired(nameInput, Constants.STAMP_NAME_MIN_LENGTH_CHARACTERS, Constants.STAMP_NAME_MAX_LENGTH_CHARACTERS);
    }

    public static boolean description(String descriptionInput) {
        return RequestValidatorCommon.stringOptional(descriptionInput, Constants.STAMP_DESCRIPTION_MIN_LENGTH_CHARACTERS, Constants.STAMP_DESCRIPTION_MAX_LENGTH_CHARACTERS);
    }

    public static boolean imageAsBase64String(String imageAsBase64StringInput) {
        return RequestValidatorCommon.imageOptional(imageAsBase64StringInput);
    }

    public static boolean idTasks(List<String> idTasksInput) {
        for (int i = 0; i < idTasksInput.size(); i++) {
            if (!RequestValidatorCommon._uuid(idTasksInput.get(i))) {
                return false;
            }
        }
        if (idTasksInput.size() == 0) {
            return false;
        }
        return true;
    }
}
