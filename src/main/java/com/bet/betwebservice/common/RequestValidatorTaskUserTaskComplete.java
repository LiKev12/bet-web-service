package com.bet.betwebservice.common;

import java.util.UUID;

public class RequestValidatorTaskUserTaskComplete {
    
    public static boolean id(String idInput) {
        return RequestValidatorCommon._uuid(idInput);
    }

    public static boolean timestampUnix(Integer timestamp) {
        return RequestValidatorCommon.timestampRequired(timestamp);
    }

    public static boolean idUser(String idUserInput) {
        return RequestValidatorCommon._uuid(idUserInput);
    }

    public static boolean idTask(String idTaskInput) {
        return RequestValidatorCommon._uuid(idTaskInput);
    }
}
