package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.entity.TaskUserTaskReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskReactionRepository extends JpaRepository<TaskUserTaskReactionEntity, UUID> {
    @Query("SELECT task_user_task_reaction \n" +
            "FROM TaskUserTaskReactionEntity task_user_task_reaction \n" +
            "WHERE BIN_TO_UUID(task_user_task_reaction.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(task_user_task_reaction.idTask) = :idTask")
    List<TaskUserTaskReactionEntity> findByIdTaskIdUser(
        @Param("idTask") String idTask, 
        @Param("idUser") String idUser
    );
}
