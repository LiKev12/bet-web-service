package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.entity.TaskUserTaskCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskCommentRepository extends JpaRepository<TaskUserTaskCommentEntity, UUID> {
    @Query("SELECT task_user_task_comment \n" +
            "FROM TaskUserTaskCommentEntity task_user_task_comment \n" +
            "WHERE BIN_TO_UUID(task_user_task_comment.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(task_user_task_comment.idTask) = :idTask")
    List<TaskUserTaskCommentEntity> findByIdTaskIdUser(
        @Param("idTask") String idTask, 
        @Param("idUser") String idUser
    );
}
