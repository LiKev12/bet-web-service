package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.entity.TaskUserTaskCommentReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskCommentReplyRepository extends JpaRepository<TaskUserTaskCommentReplyEntity, UUID> {
    @Query("SELECT task_user_task_comment_reply \n" +
            "FROM TaskUserTaskCommentReplyEntity task_user_task_comment_reply \n" +
            "WHERE BIN_TO_UUID(task_user_task_comment_reply.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(task_user_task_comment_reply.idTaskComment) = :idTaskComment")
    List<TaskUserTaskCommentReplyEntity> findByIdTaskCommentIdUser(
        @Param("idTaskComment") String idTaskComment, 
        @Param("idUser") String idUser
    );
}
