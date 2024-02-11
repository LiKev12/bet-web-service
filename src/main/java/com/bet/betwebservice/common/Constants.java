package com.bet.betwebservice.common;

public class Constants {
    public static int TASK_NAME_MIN_LENGTH_CHARACTERS = 1;
    public static int TASK_NAME_MAX_LENGTH_CHARACTERS = 100;
    public static int TASK_DESCRIPTION_MIN_LENGTH_CHARACTERS = 1;
    public static int TASK_DESCRIPTION_MAX_LENGTH_CHARACTERS = 1000;
    public static int TASK_NUMBER_OF_POINTS_MAX = 1000;
    public static int TASK_NUMBER_OF_POINTS_MIN = -1000;
    public static int TASK_NOTE_TEXT_MIN_LENGTH_CHARACTERS = 1;
    public static int TASK_NOTE_TEXT_MAX_LENGTH_CHARACTERS = 1000;
    public static int TASK_COMMENT_TEXT_MIN_LENGTH_CHARACTERS = 1;
    public static int TASK_COMMENT_TEXT_MAX_LENGTH_CHARACTERS = 1000;
    public static int TASK_COMMENT_REPLY_TEXT_MIN_LENGTH_CHARACTERS = 1;
    public static int TASK_COMMENT_REPLY_TEXT_MAX_LENGTH_CHARACTERS = 1000;

    public static int POD_NAME_MIN_LENGTH_CHARACTERS = 3;
    public static int POD_NAME_MAX_LENGTH_CHARACTERS = 50;
    public static int POD_DESCRIPTION_MIN_LENGTH_CHARACTERS = 1;
    public static int POD_DESCRIPTION_MAX_LENGTH_CHARACTERS = 1000;

    public static int STAMP_NAME_MIN_LENGTH_CHARACTERS = 3;
    public static int STAMP_NAME_MAX_LENGTH_CHARACTERS = 50;
    public static int STAMP_DESCRIPTION_MIN_LENGTH_CHARACTERS = 1;
    public static int STAMP_DESCRIPTION_MAX_LENGTH_CHARACTERS = 1000;

    public static String S3_BUCKET_NAME_ALPHA = "bet-app-io-alpha";
    public static String S3_FOLDER_NAME_TASK_IMAGE = "task-image";
    public static String S3_FOLDER_NAME_TASK_NOTE_IMAGE = "task-note-image";
    public static String S3_FOLDER_NAME_TASK_COMMENT_IMAGE = "task-comment-image";
    public static String S3_FOLDER_NAME_TASK_COMMENT_REPLY_IMAGE = "task-comment-reply-image";
    public static String S3_FOLDER_NAME_POD_IMAGE = "pod-image";
    public static String S3_FOLDER_NAME_STAMP_IMAGE = "stamp-image";
    public static String S3_FOLDER_NAME_USER_IMAGE = "user-image";
    public static String S3_BUCKET_ALPHA_ACCESS_KEY = "AKIAXCSRXEVRKDS4NPHR";
    public static String S3_BUCKET_ALPHA_SECRET_ACCESS_KEY = "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";

    public static String NOTIFICATION_TYPE_SENT_YOU_JOIN_POD_INVITE = "NOTIFICATION_TYPE_SENT_YOU_JOIN_POD_INVITE";
    public static String NOTIFICATION_TYPE_APPROVED_YOUR_BECOME_POD_MODERATOR_REQUEST = "NOTIFICATION_TYPE_APPROVED_YOUR_BECOME_POD_MODERATOR_REQUEST";
    public static String NOTIFICATION_TYPE_ADDED_YOU_AS_POD_MODERATOR = "NOTIFICATION_TYPE_ADDED_YOU_AS_POD_MODERATOR";
    public static String NOTIFICATION_TYPE_SENT_YOU_FOLLOW_REQUEST = "NOTIFICATION_TYPE_SENT_YOU_FOLLOW_REQUEST";
    public static String NOTIFICATION_TYPE_ACCEPTED_YOUR_FOLLOW_REQUEST = "NOTIFICATION_TYPE_ACCEPTED_YOUR_FOLLOW_REQUEST";
    public static String NOTIFICATION_LINK_PAGE_TYPE_POD = "POD";
    public static String NOTIFICATION_LINK_PAGE_TYPE_USER = "USER";
    public static String NOTIFICATION_LINK_PAGE_TYPE_STAMP = "STAMP";

}
