package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.entity.TaskUserTaskCommentReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskCommentReactionRepository extends JpaRepository<TaskUserTaskCommentReactionEntity, UUID> {
    @Query("SELECT task_user_task_comment_reaction \n" +
            "FROM TaskUserTaskCommentReactionEntity task_user_task_comment_reaction \n" +
            "WHERE BIN_TO_UUID(task_user_task_comment_reaction.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(task_user_task_comment_reaction.idTaskComment) = :idTaskComment")
    List<TaskUserTaskCommentReactionEntity> findByIdTaskCommentIdUser(
        @Param("idTaskComment") String idTaskComment, 
        @Param("idUser") String idUser
    );
}
