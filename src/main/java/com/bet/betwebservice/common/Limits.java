package com.bet.betwebservice.common;

public class Limits {
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

    public static int USER_USERNAME_MIN_LENGTH_CHARACTERS = 3;
    public static int USER_USERNAME_MAX_LENGTH_CHARACTERS = 30;
    public static int USER_PASSWORD_MIN_LENGTH_CHARACTERS = 6;
    public static int USER_PASSWORD_MAX_LENGTH_CHARACTERS = 50;
    public static int USER_NAME_MIN_LENGTH_CHARACTERS = 3;
    public static int USER_NAME_MAX_LENGTH_CHARACTERS = 50;
    public static int USER_BIO_MIN_LENGTH_CHARACTERS = 1;
    public static int USER_BIO_MAX_LENGTH_CHARACTERS = 1000;

    public static int LIMIT_NUMBER_OF_INCOMPLETE_TASKS_PERSONAL = 1000;
    public static int LIMIT_NUMBER_OF_TOTAL_TASKS_POD = 1000;
    public static int LIMIT_NUMBER_OF_TOTAL_TASKS_STAMP = 1000;
}
