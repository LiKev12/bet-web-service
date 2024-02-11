package com.bet.betwebservice.dao;

import com.bet.betwebservice.entity.TaskUserTaskCompleteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskCompleteRepository extends JpaRepository<TaskUserTaskCompleteEntity, UUID> {

    @Query("SELECT task_user_task_complete \n" +
            "FROM TaskUserTaskCompleteEntity task_user_task_complete \n" +
            "WHERE BIN_TO_UUID(task_user_task_complete.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(task_user_task_complete.idTask) = :idTask")
    List<TaskUserTaskCompleteEntity> findByIdTaskIdUser(
        @Param("idTask") String idTask, 
        @Param("idUser") String idUser
    );
}
