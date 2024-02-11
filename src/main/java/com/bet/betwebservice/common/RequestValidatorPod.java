package com.bet.betwebservice.common;

import java.util.List;
import java.util.UUID;

public class RequestValidatorPod {
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
        return RequestValidatorCommon.stringRequired(nameInput, Constants.POD_NAME_MIN_LENGTH_CHARACTERS, Constants.POD_NAME_MAX_LENGTH_CHARACTERS);
    }

    public static boolean description(String descriptionInput) {
        return RequestValidatorCommon.stringOptional(descriptionInput, Constants.POD_DESCRIPTION_MIN_LENGTH_CHARACTERS, Constants.POD_DESCRIPTION_MAX_LENGTH_CHARACTERS);
    }

    public static boolean isPublic(boolean isPublicInput) {
        return true;
    }

    public static boolean imageAsBase64String(String imageAsBase64StringInput) {
        return RequestValidatorCommon.imageOptional(imageAsBase64StringInput);
    }
    
    public static boolean idUsersToBecomeModerator(List<String> idUsersBecomeModeratorInput) {
        for (int i = 0; i < idUsersBecomeModeratorInput.size(); i++) {
            if (!RequestValidatorCommon._uuid(idUsersBecomeModeratorInput.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean idUsersReceiveJoinPodInvite(List<String> idUsersReceiveJoinPodInviteInput) {
        for (int i = 0; i < idUsersReceiveJoinPodInviteInput.size(); i++) {
            if (!RequestValidatorCommon._uuid(idUsersReceiveJoinPodInviteInput.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean idUsersWithApprovedBecomeModeratorRequest(List<String> idUsersWithApprovedBecomeModeratorRequestInput) {
        for (int i = 0; i < idUsersWithApprovedBecomeModeratorRequestInput.size(); i++) {
            if (!RequestValidatorCommon._uuid(idUsersWithApprovedBecomeModeratorRequestInput.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean idUsersWithRejectedBecomeModeratorRequest(List<String> idUsersWithRejectedBecomeModeratorRequestInput) {
        for (int i = 0; i < idUsersWithRejectedBecomeModeratorRequestInput.size(); i++) {
            if (!RequestValidatorCommon._uuid(idUsersWithRejectedBecomeModeratorRequestInput.get(i))) {
                return false;
            }
        }
        return true;
    }
}
