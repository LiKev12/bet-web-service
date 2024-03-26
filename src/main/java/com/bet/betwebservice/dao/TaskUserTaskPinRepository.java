package com.bet.betwebservice.dao;

import com.bet.betwebservice.entity.TaskUserTaskPinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskPinRepository extends JpaRepository<TaskUserTaskPinEntity, UUID> {
    @Query("SELECT task_user_task_pin \n" +
            "FROM TaskUserTaskPinEntity task_user_task_pin \n" +
            "WHERE BIN_TO_UUID(task_user_task_pin.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(task_user_task_pin.idTask) = :idTask")
    List<TaskUserTaskPinEntity> findByIdTaskIdUser(
        @Param("idTask") String idTask, 
        @Param("idUser") String idUser
    );
}
