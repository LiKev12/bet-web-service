package com.bet.betwebservice.common;

public class RequestValidatorTask {
    
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
        return RequestValidatorCommon.stringRequired(nameInput, Constants.TASK_NAME_MIN_LENGTH_CHARACTERS, Constants.TASK_NAME_MAX_LENGTH_CHARACTERS);
    }

    public static boolean description(String descriptionInput) {
        return RequestValidatorCommon.stringOptional(descriptionInput, Constants.TASK_DESCRIPTION_MIN_LENGTH_CHARACTERS, Constants.TASK_DESCRIPTION_MAX_LENGTH_CHARACTERS);
    }

    public static boolean numberOfPoints(Integer numberOfPointsInput) {
        if (numberOfPointsInput < Constants.TASK_NUMBER_OF_POINTS_MIN || numberOfPointsInput > Constants.TASK_NUMBER_OF_POINTS_MAX) {
            return false;
        }
        return true;
    }

    public static boolean imageAsBase64String(String imageAsBase64StringInput) {
        return RequestValidatorCommon.imageOptional(imageAsBase64StringInput);
    }

    public static boolean timestampUpdate(Integer timestamp) {
        return RequestValidatorCommon.timestampRequired(timestamp);
    }

    public static boolean datetimeTarget(String datetime) {
        return RequestValidatorCommon.datetimeOptional(datetime);
    }

    public static boolean idPod(String idPodInput) {
        return true;
    }

    public static boolean noteText(String noteTextInput) {
        return RequestValidatorCommon.stringOptional(noteTextInput, Constants.TASK_NOTE_TEXT_MIN_LENGTH_CHARACTERS, Constants.TASK_NOTE_TEXT_MAX_LENGTH_CHARACTERS);
    }

    public static boolean noteImageAsBase64String(String noteImageAsBase64StringInput) {
        return RequestValidatorCommon.imageOptional(noteImageAsBase64StringInput);
    }

    public static boolean idTaskComment(String idTaskCommentInput) {
        return RequestValidatorCommon._uuid(idTaskCommentInput);
    }

    public static boolean idTaskCommentReply(String idTaskCommentReplyInput) {
        return RequestValidatorCommon._uuid(idTaskCommentReplyInput);
    }

    public static boolean reactionType(String reactionTypeInput) {
        if (reactionTypeInput == null) {
            return true;
        }
        if (!reactionTypeInput.equals("LIKE") &&
            !reactionTypeInput.equals("LOVE") &&
            !reactionTypeInput.equals("LAUGH") &&
            !reactionTypeInput.equals("WOW") &&
            !reactionTypeInput.equals("SAD") &&
            !reactionTypeInput.equals("ANGRY")
        ) {
            return false;
        }
        return true;
    }

    public static boolean commentText(String commentTextInput) {
        return RequestValidatorCommon.stringRequired(commentTextInput, Constants.TASK_COMMENT_TEXT_MIN_LENGTH_CHARACTERS, Constants.TASK_COMMENT_TEXT_MAX_LENGTH_CHARACTERS);
    }

    public static boolean commentImageAsBase64String(String commentImageAsBase64StringInput) {
        return RequestValidatorCommon.imageRequired(commentImageAsBase64StringInput);
    }

    public static boolean commentReplyText(String commentReplyTextInput) {
        return RequestValidatorCommon.stringRequired(commentReplyTextInput, Constants.TASK_COMMENT_REPLY_TEXT_MIN_LENGTH_CHARACTERS, Constants.TASK_COMMENT_REPLY_TEXT_MAX_LENGTH_CHARACTERS);
    }

    public static boolean commentReplyImageAsBase64String(String commentReplyImageAsBase64StringInput) {
        return RequestValidatorCommon.imageRequired(commentReplyImageAsBase64StringInput);
    }

    public static boolean numberOfReactionsLimit(int numberOfReactionsLimitInput) {
        return true;
    }
}
