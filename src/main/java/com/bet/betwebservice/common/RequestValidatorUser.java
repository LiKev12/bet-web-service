package com.bet.betwebservice.common;

import java.util.List;
import java.util.UUID;

public class RequestValidatorUser {
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
        return RequestValidatorCommon.stringRequired(nameInput, 3, 50);
    }

    public static boolean username(String usernameInput) {
        return RequestValidatorCommon.stringRequired(usernameInput, 3, 30) && RequestValidatorCommon.stringLowerCase(usernameInput);
    }

    public static boolean bio(String bioInput) {
        return RequestValidatorCommon.stringOptional(bioInput, 1, 1000);
    }

    public static boolean email(String emailInput) {
        return RequestValidatorCommon.email(emailInput);
    }

    public static boolean imageAsBase64String(String imageAsBase64StringInput) {
        return RequestValidatorCommon.imageOptional(imageAsBase64StringInput);
    }
    
    public static boolean idUsersWithFollowRequestAccepted(List<String> idUsersWithFollowRequestAcceptedInput) {
        for (int i = 0; i < idUsersWithFollowRequestAcceptedInput.size(); i++) {
            if (!RequestValidatorCommon._uuid(idUsersWithFollowRequestAcceptedInput.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean idUsersWithFollowRequestDeclined(List<String> idUsersWithFollowRequestDeclinedInput) {
        for (int i = 0; i < idUsersWithFollowRequestDeclinedInput.size(); i++) {
            if (!RequestValidatorCommon._uuid(idUsersWithFollowRequestDeclinedInput.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean idNotification(String idNotificationInput) {
        return RequestValidatorCommon._uuid(idNotificationInput);
    }
}
