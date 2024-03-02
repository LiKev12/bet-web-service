package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.entity.TaskUserTaskCommentReplyReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskCommentReplyReactionRepository extends JpaRepository<TaskUserTaskCommentReplyReactionEntity, UUID> {
    @Query("SELECT task_user_task_comment_reply_reaction \n" +
            "FROM TaskUserTaskCommentReplyReactionEntity task_user_task_comment_reply_reaction \n" +
            "WHERE BIN_TO_UUID(task_user_task_comment_reply_reaction.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(task_user_task_comment_reply_reaction.idTaskCommentReply) = :idTaskCommentReply")
    List<TaskUserTaskCommentReplyReactionEntity> findByIdTaskCommentReplyIdUser(
        @Param("idTaskCommentReply") String idTaskCommentReply, 
        @Param("idUser") String idUser
    );
}
