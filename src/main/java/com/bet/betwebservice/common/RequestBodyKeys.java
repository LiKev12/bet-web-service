package com.bet.betwebservice.common;

public class RequestBodyKeys {
    public static String CREATE_TASK_NAME = "name";
    public static String CREATE_TASK_DESCRIPTION = "description";
    public static String CREATE_TASK_NUMBER_OF_POINTS = "numberOfPoints";
    public static String CREATE_TASK_DATETIME_TARGET = "datetimeTarget";
    public static String CREATE_TASK_ID_POD = "idPod";

    public static String CREATE_POD_NAME = "name";
    public static String CREATE_POD_DESCRIPTION = "description";
    public static String CREATE_POD_IS_PUBLIC = "isPublic";

    public static String CREATE_STAMP_NAME = "name";
    public static String CREATE_STAMP_DESCRIPTION = "description";
    public static String CREATE_STAMP_ID_TASKS = "idTasks";

    public static String UPDATE_TASK_ID = "id";
    public static String UPDATE_TASK_NAME = "name";
    public static String UPDATE_TASK_DESCRIPTION = "description";
    public static String UPDATE_TASK_NUMBER_OF_POINTS = "numberOfPoints";
    public static String UPDATE_TASK_IMAGE = "imageAsBase64String";
    public static String UPDATE_TASK_DATETIME_TARGET = "datetimeTarget";
    public static String UPDATE_TASK_IS_COMPLETE = "isComplete";
    public static String UPDATE_TASK_IS_STAR = "isStar";
    public static String UPDATE_TASK_IS_PIN = "isPin";
    public static String UPDATE_TASK_NOTE_TEXT = "noteText";
    public static String UPDATE_TASK_NOTE_IMAGE = "noteImageAsBase64String";

    public static String UPDATE_POD_ID = "id";
    public static String UPDATE_POD_NAME = "name";
    public static String UPDATE_POD_DESCRIPTION = "description";
    public static String UPDATE_POD_IMAGE = "imageAsBase64String";

    public static String UPDATE_STAMP_ID = "id";
    public static String UPDATE_STAMP_NAME = "name";
    public static String UPDATE_STAMP_DESCRIPTION = "description";
    public static String UPDATE_STAMP_IMAGE = "imageAsBase64String";
    public static String UPDATE_STAMP_ID_TASKS = "idTasks";

    public static String UPDATE_USER_ID = "id";
    public static String UPDATE_USER_USERNAME = "username";
    public static String UPDATE_USER_NAME = "name";
    public static String UPDATE_USER_BIO = "bio";
    public static String UPDATE_USER_IMAGE = "imageAsBase64String";

    public static String UPDATE_POD_MEMBERSHIP_POD_ID = "idPod";

    public static String SEND_JOIN_POD_INVITE_ID_USERS_RECEIVE_INVITE = "idUsersReceiveInvite";
    public static String SEND_JOIN_POD_INVITE_POD_ID = "idPod";
    public static String SEND_BECOME_POD_MODERATOR_REQUEST_POD_ID = "idPod";
    public static String APPROVE_BECOME_POD_MODERATOR_REQUESTS_POD_ID = "idPod";
    public static String APPROVE_BECOME_POD_MODERATOR_REQUESTS_ID_USERS_WITH_REQUESTS_APPROVED = "idUsersWithBecomeModeratorRequestApproved";
    public static String REJECT_BECOME_POD_MODERATOR_REQUESTS_POD_ID = "idPod";
    public static String REJECT_BECOME_POD_MODERATOR_REQUESTS_ID_USERS_WITH_REQUESTS_REJECTED = "idUsersWithBecomeModeratorRequestRejected";
    public static String JOIN_POD_POD_ID = "idPod";
    public static String ACCEPT_JOIN_POD_INVITE_POD_ID = "idPod";
    public static String DECLINE_JOIN_POD_INVITE_POD_ID = "idPod";
    public static String ADD_POD_MODERATORS_ID_USERS_TO_BECOME_MODERATOR = "idUsersToBecomeModerator";
    public static String ADD_POD_MODERATORS_ID_POD = "idPod";
    public static String LEAVE_POD_POD_ID = "idPod";

    public static String SEND_FOLLOW_USER_REQUEST_ID_USER_RECEIVE_FOLLOW_REQUEST = "idUserReceiveFollowRequest";
    public static String ACCEPT_FOLLOW_USER_REQUESTS_ID_USERS_SEND_FOLLOW_REQUEST = "idUsersWithFollowRequestAccepted";
    public static String DECLINE_FOLLOW_USER_REQUESTS_ID_USERS_SEND_FOLLOW_REQUEST = "idUsersWithFollowRequestDeclined";

    public static String GET_TASK_COMMENTS_AND_REACTIONS_ID_TASK = "idTask"; // TODO: deprecate this
    public static String UPDATE_TASK_REACTION_ID_TASK = "idTask";
    public static String UPDATE_TASK_REACTION_REACTION_TYPE = "reactionType";
    public static String UPDATE_TASK_COMMENT_REACTION_ID_TASK_COMMENT = "idTaskComment";
    public static String UPDATE_TASK_COMMENT_REACTION_REACTION_TYPE = "reactionType";
    public static String UPDATE_TASK_COMMENT_REPLY_REACTION_ID_TASK_COMMENT_REPLY = "idTaskCommentReply";
    public static String UPDATE_TASK_COMMENT_REPLY_REACTION_REACTION_TYPE = "reactionType";
    public static String CREATE_TASK_COMMENT_ID_TASK = "idTask";
    public static String CREATE_TASK_COMMENT_COMMENT_TEXT = "commentText";
    public static String CREATE_TASK_COMMENT_COMMENT_IMAGE = "commentImageAsBase64String";
    public static String CREATE_TASK_COMMENT_REPLY_ID_TASK_COMMENT = "idTaskComment";
    public static String CREATE_TASK_COMMENT_REPLY_COMMENT_TEXT = "commentReplyText";
    public static String CREATE_TASK_COMMENT_REPLY_COMMENT_IMAGE = "commentReplyImageAsBase64String";

    public static String GET_TASK_REACTIONS_ID_TASK = "idTask";
    public static String GET_TASK_REACTIONS_NUMBER_OF_REACTIONS_LIMIT = "numberOfReactionsLimit";
    public static String GET_TASK_COMMENT_REACTIONS_ID_TASK_COMMENT = "idTaskComment";
    public static String GET_TASK_COMMENT_REACTIONS_NUMBER_OF_REACTIONS_LIMIT = "numberOfReactionsLimit";
    public static String GET_TASK_COMMENT_REPLY_REACTIONS_ID_TASK_COMMENT_REPLY = "idTaskCommentReply";
    public static String GET_TASK_COMMENT_REPLY_REACTIONS_NUMBER_OF_REACTIONS_LIMIT = "numberOfReactionsLimit";

    public static String GET_TASK_COMMENTS_ID_TASK = "idTask";
    public static String GET_TASK_COMMENT_REPLIES_ID_TASK_COMMENT = "idTaskComment";

    public static String DISMISS_NOTIFICATION_ID_USER = "idUser";
    public static String DISMISS_NOTIFICATION_ID_NOTIFICATION = "idNotification";

    public static String COLLECT_STAMP_ID = "idStamp";
}