package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.entity.TaskUserTaskStarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskStarRepository extends JpaRepository<TaskUserTaskStarEntity, UUID> {
    @Query("SELECT task_user_task_star \n" +
            "FROM TaskUserTaskStarEntity task_user_task_star \n" +
            "WHERE BIN_TO_UUID(task_user_task_star.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(task_user_task_star.idTask) = :idTask")
    List<TaskUserTaskStarEntity> findByIdTaskIdUser(
        @Param("idTask") String idTask, 
        @Param("idUser") String idUser
    );
}
