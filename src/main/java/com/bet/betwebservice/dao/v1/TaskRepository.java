package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.dto.NumberOfPointsInTasksCompletedOverTimeVisualizationDTO;
import com.bet.betwebservice.dto.TaskCommentDTO;
import com.bet.betwebservice.dto.TaskIndividualPropertiesDTO;
import com.bet.betwebservice.dto.TaskReactionDTO;
import com.bet.betwebservice.dto.TaskSharedPropertiesDTO;
import com.bet.betwebservice.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;



public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {


    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskSharedPropertiesDTO(\n" +
            "        task.id,\n" +
            "        task.name,\n" +
            "        task.description,\n" +
            "        task.idImageKey,\n" +
            "        task.numberOfPoints,\n" +
            "        task.idPod,\n" +
            "        task.timestampUnix,\n" +
            "        task.timestampUpdate,\n" +
            "        task.datetimeTarget\n" +
            "    )\n" +
            "FROM TaskEntity task\n" +
            "WHERE (task.idPod IS NULL AND BIN_TO_UUID(task.idUserCreate) = :idUser) AND task.isArchived=FALSE AND\n" +
            "(LOWER(task.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(task.description) LIKE CONCAT('%',:filterNameOrDescription,'%'))")
    List<TaskSharedPropertiesDTO> getTasksPersonal_SharedProperties(
            @Param("idUser") String idUser,
            @Param("filterNameOrDescription") String filterNameOrDescription
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskIndividualPropertiesDTO(\n" +
            "        task.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_note.idUser) = :idUser THEN task_user_task_note.noteText\n" +
            "            ELSE NULL\n" +
            "        END) AS noteText,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_note.idUser) = :idUser THEN task_user_task_note.idNoteImageKey\n" +
            "            ELSE NULL\n" +
            "        END) AS idNoteImageKey,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_complete.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isComplete,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_star.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isStar,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_pin.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isPin,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_complete.idUser) = :idUser THEN task_user_task_complete.timestampUnix\n" +
            "            ELSE NULL\n" +
            "        END) as timestampComplete\n" +
            "    )\n" +
            "FROM TaskEntity task\n" +
            "LEFT JOIN TaskUserTaskNoteEntity task_user_task_note on task_user_task_note.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskCompleteEntity task_user_task_complete ON task_user_task_complete.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskStarEntity task_user_task_star ON task_user_task_star.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskPinEntity task_user_task_pin ON task_user_task_pin.idTask = task.id\n" +
            "WHERE (task.idPod IS NULL AND BIN_TO_UUID(task.idUserCreate) = :idUser) AND task.isArchived=FALSE AND\n" +
            "(LOWER(task.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(task.description) LIKE CONCAT('%',:filterNameOrDescription,'%'))\n" +
            "GROUP BY task.id")
    List<TaskIndividualPropertiesDTO> getTasksPersonal_IndividualProperties(
            @Param("idUser") String idUser,
            @Param("filterNameOrDescription") String filterNameOrDescription
    );


    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskSharedPropertiesDTO(\n" +
            "        task.id,\n" +
            "        task.name,\n" +
            "        task.description,\n" +
            "        task.idImageKey,\n" +
            "        task.numberOfPoints,\n" +
            "        task.idPod,\n" +
            "        task.timestampUnix,\n" +
            "        task.timestampUpdate,\n" +
            "        task.datetimeTarget\n" +
            "    )\n" +
            "FROM TaskEntity task\n" +
            "WHERE (BIN_TO_UUID(task.idPod) = :idPod) AND task.isArchived=FALSE AND\n" +
            "(LOWER(task.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(task.description) LIKE CONCAT('%',:filterNameOrDescription,'%'))\n")
    List<TaskSharedPropertiesDTO> getTasksAssociatedWithPod_SharedProperties(
            @Param("idPod") String idPod,
            @Param("filterNameOrDescription") String filterNameOrDescription
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskIndividualPropertiesDTO(\n" +
            "        task.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_note.idUser) = :idUser THEN task_user_task_note.noteText\n" +
            "            ELSE NULL\n" +
            "        END) AS noteText,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_note.idUser) = :idUser THEN task_user_task_note.idNoteImageKey\n" +
            "            ELSE NULL\n" +
            "        END) AS idNoteImageKey,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_complete.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isComplete,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_star.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isStar,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_pin.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isPin,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_complete.idUser) = :idUser THEN task_user_task_complete.timestampUnix\n" +
            "            ELSE NULL\n" +
            "        END) as timestampComplete\n" +
            "    )\n" +
            "FROM TaskEntity task\n" +
            "LEFT JOIN TaskUserTaskNoteEntity task_user_task_note on task_user_task_note.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskCompleteEntity task_user_task_complete ON task_user_task_complete.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskStarEntity task_user_task_star ON task_user_task_star.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskPinEntity task_user_task_pin ON task_user_task_pin.idTask = task.id\n" +
            "WHERE (BIN_TO_UUID(task.idPod) = :idPod) AND task.isArchived=FALSE AND\n" +
            "(LOWER(task.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(task.description) LIKE CONCAT('%',:filterNameOrDescription,'%'))\n" +
            "GROUP BY task.id")
    List<TaskIndividualPropertiesDTO> getTasksAssociatedWithPod_IndividualProperties(
            @Param("idPod") String idPod,
            @Param("idUser") String idUser,
            @Param("filterNameOrDescription") String filterNameOrDescription
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskSharedPropertiesDTO(\n" +
            "        task.id,\n" +
            "        task.name,\n" +
            "        task.description,\n" +
            "        task.idImageKey,\n" +
            "        task.numberOfPoints,\n" +
            "        task.idPod,\n" +
            "        task.timestampUnix,\n" +
            "        task.timestampUpdate,\n" +
            "        task.datetimeTarget\n" +
            "    )\n" +
            "FROM TaskEntity task\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "WHERE BIN_TO_UUID(stamp_task_stamp_has_task.idStamp) = :idStamp AND task.isArchived=FALSE AND\n" +
            "(LOWER(task.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(task.description) LIKE CONCAT('%',:filterNameOrDescription,'%'))")
    List<TaskSharedPropertiesDTO> getTasksAssociatedWithStamp_SharedProperties(
            @Param("idStamp") String idStamp,
            @Param("filterNameOrDescription") String filterNameOrDescription
    );


    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskIndividualPropertiesDTO(\n" +
            "        task.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_note.idUser) = :idUser THEN task_user_task_note.noteText\n" +
            "            ELSE NULL\n" +
            "        END) AS noteText,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_note.idUser) = :idUser THEN task_user_task_note.idNoteImageKey\n" +
            "            ELSE NULL\n" +
            "        END) AS idNoteImageKey,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_complete.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isComplete,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_star.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isStar,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_pin.idUser) = :idUser THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isPin,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_complete.idUser) = :idUser THEN task_user_task_complete.timestampUnix\n" +
            "            ELSE NULL\n" +
            "        END) as timestampComplete\n" +
            "    )\n" +
            "FROM TaskEntity task\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskNoteEntity task_user_task_note on task_user_task_note.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskCompleteEntity task_user_task_complete ON task_user_task_complete.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskStarEntity task_user_task_star ON task_user_task_star.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskPinEntity task_user_task_pin ON task_user_task_pin.idTask = task.id\n" +
            "WHERE BIN_TO_UUID(stamp_task_stamp_has_task.idStamp) = :idStamp AND task.isArchived=FALSE AND\n" +
            "(LOWER(task.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(task.description) LIKE CONCAT('%',:filterNameOrDescription,'%'))\n" +
            "GROUP BY task.id")
    List<TaskIndividualPropertiesDTO> getTasksAssociatedWithStamp_IndividualProperties(
            @Param("idStamp") String idStamp,
            @Param("idUser") String idUser,
            @Param("filterNameOrDescription") String filterNameOrDescription
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskSharedPropertiesDTO(\n" +
            "        task.id,\n" +
            "        task.name,\n" +
            "        task.description,\n" +
            "        task.idImageKey,\n" +
            "        task.numberOfPoints,\n" +
            "        task.idPod,\n" +
            "        task.timestampUnix,\n" +
            "        task.timestampUpdate,\n" +
            "        task.datetimeTarget\n" +
            "    )\n" +
            "FROM TaskEntity task\n" +
            "LEFT JOIN TaskUserTaskPinEntity task_user_task_pin ON task_user_task_pin.idTask = task.id\n" +
            "WHERE BIN_TO_UUID(task_user_task_pin.idUser) = :idUserProfile AND task.isArchived=FALSE AND\n" +
            "(LOWER(task.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(task.description) LIKE CONCAT('%',:filterNameOrDescription,'%'))\n" +
            "GROUP BY task.id")
    List<TaskSharedPropertiesDTO> getPinnedTasksAssociatedWithUser_SharedProperties(
            @Param("idUserProfile") String idUserProfile,
            @Param("filterNameOrDescription") String filterNameOrDescription
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskIndividualPropertiesDTO(\n" +
            "        task.id,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_note.idUser) = :idUserProfile THEN task_user_task_note.noteText\n" +
            "            ELSE NULL\n" +
            "        END) AS noteText,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_note.idUser) = :idUserProfile THEN task_user_task_note.idNoteImageKey\n" +
            "            ELSE NULL\n" +
            "        END) AS idNoteImageKey,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_complete.idUser) = :idUserProfile THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isComplete,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_star.idUser) = :idUserProfile THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isStar,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_pin.idUser) = :idUserProfile THEN 1\n" +
            "            ELSE 0\n" +
            "        END) = 1 AS isPin,\n" +
            "        MAX(CASE\n" +
            "            WHEN BIN_TO_UUID(task_user_task_complete.idUser) = :idUserProfile THEN task_user_task_complete.timestampUnix\n" +
            "            ELSE NULL\n" +
            "        END) as timestampComplete\n" +
            "    )\n" +
            "FROM TaskEntity task\n" +
            "LEFT JOIN TaskUserTaskNoteEntity task_user_task_note on task_user_task_note.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskCompleteEntity task_user_task_complete ON task_user_task_complete.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskStarEntity task_user_task_star ON task_user_task_star.idTask = task.id\n" +
            "LEFT JOIN TaskUserTaskPinEntity task_user_task_pin ON task_user_task_pin.idTask = task.id\n" +
            "WHERE BIN_TO_UUID(task_user_task_pin.idUser) = :idUserProfile AND task.isArchived=FALSE AND\n" +
            "(LOWER(task.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(task.description) LIKE CONCAT('%',:filterNameOrDescription,'%'))\n" +
            "GROUP BY task.id")
    List<TaskIndividualPropertiesDTO> getPinnedTasksAssociatedWithUser_IndividualProperties(
            @Param("idUserProfile") String idUserProfile,
            @Param("filterNameOrDescription") String filterNameOrDescription
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.NumberOfPointsInTasksCompletedOverTimeVisualizationDTO(\n" +
            "        task.numberOfPoints,\n" +
            "        task_user_task_complete.timestampUnix\n" +
            "    )\n" +
            "FROM TaskUserTaskCompleteEntity task_user_task_complete\n" +
            "LEFT JOIN TaskEntity task on task.id = task_user_task_complete.idTask\n" +
            "WHERE task.idPod IS NULL AND BIN_TO_UUID(task.idUserCreate) = :idUser AND BIN_TO_UUID(task_user_task_complete.idUser) = :idUser AND task.isArchived=FALSE AND\n" +
            "DATEDIFF(CAST(FROM_UNIXTIME(UNIX_TIMESTAMP(NOW())) AS DATE), CAST(FROM_UNIXTIME(task_user_task_complete.timestampUnix) AS DATE)) <= 400")
    List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(
            @Param("idUser") String idUser
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.NumberOfPointsInTasksCompletedOverTimeVisualizationDTO(\n" +
            "        task.numberOfPoints,\n" +
            "        task_user_task_complete.timestampUnix\n" +
            "    )\n" +
            "FROM TaskUserTaskCompleteEntity task_user_task_complete\n" +
            "LEFT JOIN TaskEntity task on task.id = task_user_task_complete.idTask\n" +
            "WHERE BIN_TO_UUID(task.idPod) = :idPod AND BIN_TO_UUID(task_user_task_complete.idUser) = :idUser AND task.isArchived=FALSE AND\n" +
            "DATEDIFF(CAST(FROM_UNIXTIME(UNIX_TIMESTAMP(NOW())) AS DATE), CAST(FROM_UNIXTIME(task_user_task_complete.timestampUnix) AS DATE)) <= 400")
    List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(
            @Param("idPod") String idPod,
            @Param("idUser") String idUser
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.NumberOfPointsInTasksCompletedOverTimeVisualizationDTO(\n" +
            "        task.numberOfPoints,\n" +
            "        task_user_task_complete.timestampUnix\n" +
            "    )\n" +
            "FROM TaskUserTaskCompleteEntity task_user_task_complete\n" +
            "LEFT JOIN TaskEntity task on task.id = task_user_task_complete.idTask\n" +
            "LEFT JOIN StampTaskStampHasTaskEntity stamp_task_stamp_has_task on stamp_task_stamp_has_task.idTask = task.id\n" +
            "WHERE BIN_TO_UUID(stamp_task_stamp_has_task.idStamp) = :idStamp AND BIN_TO_UUID(task_user_task_complete.idUser) = :idUser AND task.isArchived=FALSE AND\n" +
            "DATEDIFF(CAST(FROM_UNIXTIME(UNIX_TIMESTAMP(NOW())) AS DATE), CAST(FROM_UNIXTIME(task_user_task_complete.timestampUnix) AS DATE)) <= 400")
    List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(
            @Param("idStamp") String idStamp,
            @Param("idUser") String idUser
    );

    @Query("SELECT\n" +
            "    new com.bet.betwebservice.dto.TaskReactionDTO(\n" +
            "        task_user_task_reaction.id,\n" +
            "        task_user_task_reaction.idUser,\n" +
            "        task_user_task_reaction.timestampUnix,\n" +
            "        task_user_task_reaction.reactionType\n" +
            "    )\n" +
            "FROM TaskUserTaskReactionEntity task_user_task_reaction\n" +
            "WHERE BIN_TO_UUID(task_user_task_reaction.idTask) = :idTask")
    List<TaskReactionDTO> getUserBubblesReactionTask(@Param("idTask") String idTask);

    @Query("SELECT\n" +
            "    new com.bet.betwebservice.dto.TaskReactionDTO(\n" +
            "        task_user_task_comment_reaction.id,\n" +
            "        task_user_task_comment_reaction.idUser,\n" +
            "        task_user_task_comment_reaction.timestampUnix,\n" +
            "        task_user_task_comment_reaction.reactionType\n" +
            "    )\n" +
            "FROM TaskUserTaskCommentReactionEntity task_user_task_comment_reaction\n" +
            "WHERE BIN_TO_UUID(task_user_task_comment_reaction.idTaskComment) = :idTaskComment")
    List<TaskReactionDTO> getUserBubblesReactionTaskComment(@Param("idTaskComment") String idTaskComment);

    @Query("SELECT\n" +
            "    new com.bet.betwebservice.dto.TaskReactionDTO(\n" +
            "        task_user_task_comment_reply_reaction.id,\n" +
            "        task_user_task_comment_reply_reaction.idUser,\n" +
            "        task_user_task_comment_reply_reaction.timestampUnix,\n" +
            "        task_user_task_comment_reply_reaction.reactionType\n" +
            "    )\n" +
            "FROM TaskUserTaskCommentReplyReactionEntity task_user_task_comment_reply_reaction\n" +
            "WHERE BIN_TO_UUID(task_user_task_comment_reply_reaction.idTaskCommentReply) = :idTaskCommentReply")
    List<TaskReactionDTO> getUserBubblesReactionTaskCommentReply(@Param("idTaskCommentReply") String idTaskCommentReply);

    @Query("SELECT\n" +
            "    new com.bet.betwebservice.dto.TaskCommentDTO(\n" +
            "        task_user_task_comment.id,\n" +
            "        task_user_task_comment.timestampUnix,\n" +
            "        task_user_task_comment.idUser,\n" +
            "        task_user_task_comment.isText,\n" +
            "        task_user_task_comment.commentText,\n" +
            "        task_user_task_comment.isImage,\n" +
            "        task_user_task_comment.idCommentImageKey\n" +
            "    )\n" +
            "FROM TaskUserTaskCommentEntity task_user_task_comment\n" +
            "WHERE BIN_TO_UUID(task_user_task_comment.idTask) = :idTask")
    List<TaskCommentDTO> getTaskComments(@Param("idTask") String idTask);

    @Query("SELECT\n" +
            "    new com.bet.betwebservice.dto.TaskCommentDTO(\n" +
            "        task_user_task_comment_reply.id,\n" +
            "        task_user_task_comment_reply.timestampUnix,\n" +
            "        task_user_task_comment_reply.idUser,\n" +
            "        task_user_task_comment_reply.isText,\n" +
            "        task_user_task_comment_reply.commentReplyText,\n" +
            "        task_user_task_comment_reply.isImage,\n" +
            "        task_user_task_comment_reply.idCommentReplyImageKey\n" +
            "    )\n" +
            "FROM TaskUserTaskCommentReplyEntity task_user_task_comment_reply\n" +
            "WHERE BIN_TO_UUID(task_user_task_comment_reply.idTaskComment) = :idTaskComment")
    List<TaskCommentDTO> getTaskCommentReplies(@Param("idTaskComment") String idTaskComment);
}
