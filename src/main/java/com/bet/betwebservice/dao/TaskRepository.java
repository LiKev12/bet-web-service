package com.bet.betwebservice.dao;

import com.bet.betwebservice.dto.TaskDTO;
import com.bet.betwebservice.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;



public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskDTO(\n" +
            "        task.id,\n" +
            "        task.name,\n" +
            "        task.description,\n" +
            "        task.image,\n" +
            "        task.numberOfPoints,\n" +
            "        task.idPod,\n" +
            "        CASE\n" +
            "            WHEN task_user_task_complete.id IS NOT NULL THEN TRUE\n" +
            "            ELSE FALSE\n" +
            "        END,\n" +
            "        CASE\n" +
            "            WHEN task_user_task_star.id IS NOT NULL THEN TRUE\n" +
            "            ELSE FALSE\n" +
            "        END,\n" +
            "        CASE\n" +
            "            WHEN task_user_task_pin.id IS NOT NULL THEN TRUE\n" +
            "            ELSE FALSE\n" +
            "        END,\n" +
            "        task.timestampUnix,\n" +
            "        task.timestampUpdate,\n" +
            "        task.timestampTarget,\n" +
            "        CASE\n" +
            "            WHEN task_user_task_complete.idTask IS NOT NULL THEN task_user_task_complete.timestampUnix\n" +
            "            ELSE NULL\n" +
            "        END\n" +
            "    ),\n" +
            "    CASE\n" +
            "        WHEN task_user_task_complete.id IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END AS isComplete,\n" +
            "    CASE\n" +
            "        WHEN task_user_task_star.id IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END AS isStar,\n" +
            "    CASE\n" +
            "        WHEN task_user_task_pin.id IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END AS isPin,\n" +
            "    CASE WHEN task.timestampUpdate IS NOT NULL THEN task.timestampUpdate ELSE task.timestampUnix END AS sortByTimestamp\n" +
            "FROM TaskEntity task\n" +
            "LEFT JOIN TaskUserTaskCompleteEntity task_user_task_complete ON task.id = task_user_task_complete.idTask\n" +
            "LEFT JOIN TaskUserTaskStarEntity task_user_task_star ON task.id = task_user_task_star.idTask\n" +
            "LEFT JOIN TaskUserTaskPinEntity task_user_task_pin ON task.id = task_user_task_pin.idTask\n" +
            "WHERE (task.idPod IS NULL AND BIN_TO_UUID(task.idUserCreate) = :idUser)\n" +
            "AND (task.name LIKE %:filterText% OR task.description LIKE %:filterText%)\n" +
            "AND (CASE\n" +
            "        WHEN task_user_task_complete.id IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END = :filterIsComplete OR CASE\n" +
            "        WHEN task_user_task_complete.id IS NOT NULL THEN FALSE\n" +
            "        ELSE TRUE\n" +
            "    END = :filterIsNotComplete)\n" +
            "AND (CASE\n" +
            "        WHEN task_user_task_star.id IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END = :filterIsStar OR CASE\n" +
            "        WHEN task_user_task_star.id IS NOT NULL THEN FALSE\n" +
            "        ELSE TRUE\n" +
            "    END = :filterIsNotStar)\n" +
            "AND (CASE\n" +
            "        WHEN task_user_task_pin.id IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END = :filterIsPin OR CASE\n" +
            "        WHEN task_user_task_pin.id IS NOT NULL THEN FALSE\n" +
            "        ELSE TRUE\n" +
            "    END = :filterIsNotPin)\n" +
            "ORDER BY isComplete ASC, sortByTimestamp DESC")
    List<TaskDTO> getTasksPersonal(
            String idUser,
            @Param("filterText") String filterText,
            @Param("filterIsComplete") boolean filterIsComplete,
            @Param("filterIsNotComplete") boolean filterIsNotComplete,
            @Param("filterIsStar") boolean filterIsStar,
            @Param("filterIsNotStar") boolean filterIsNotStar,
            @Param("filterIsPin") boolean filterIsPin,
            @Param("filterIsNotPin") boolean filterIsNotPin
    );

    @Query("SELECT \n" +
            "    new com.bet.betwebservice.dto.TaskDTO(\n" +
            "        task.id,\n" +
            "        task.name,\n" +
            "        task.description,\n" +
            "        task.image,\n" +
            "        task.numberOfPoints,\n" +
            "        task.idPod,\n" +
            "        CASE\n" +
            "            WHEN task_user_task_complete.id IS NOT NULL THEN TRUE\n" +
            "            ELSE FALSE\n" +
            "        END,\n" +
            "        CASE\n" +
            "            WHEN task_user_task_star.id IS NOT NULL THEN TRUE\n" +
            "            ELSE FALSE\n" +
            "        END,\n" +
            "        CASE\n" +
            "            WHEN task_user_task_pin.id IS NOT NULL THEN TRUE\n" +
            "            ELSE FALSE\n" +
            "        END,\n" +
            "        task.timestampUnix,\n" +
            "        task.timestampUpdate,\n" +
            "        task.timestampTarget,\n" +
            "        CASE\n" +
            "            WHEN task_user_task_complete.idTask IS NOT NULL THEN task_user_task_complete.timestampUnix\n" +
            "            ELSE NULL\n" +
            "        END\n" +
            "    ),\n" +
            "    CASE\n" +
            "        WHEN task_user_task_complete.idTask IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END AS isComplete,\n" +
            "    CASE WHEN task.timestampUpdate IS NOT NULL THEN task.timestampUpdate ELSE task.timestampUnix END AS sortByTimestamp\n" +
            "FROM TaskEntity task\n" +
            "LEFT JOIN TaskUserTaskCompleteEntity task_user_task_complete ON task.id = task_user_task_complete.idTask AND task_user_task_complete.idUser = UUID_TO_BIN(:idUser)\n" +
            "LEFT JOIN TaskUserTaskStarEntity task_user_task_star ON task.id = task_user_task_star.idTask AND task_user_task_star.idUser = UUID_TO_BIN(:idUser)\n" +
            "LEFT JOIN TaskUserTaskPinEntity task_user_task_pin ON task.id = task_user_task_pin.idTask AND task_user_task_pin.idUser = UUID_TO_BIN(:idUser)\n" +
            "WHERE BIN_TO_UUID(task.idPod) = :idPod\n" +
            "AND (task.name LIKE %:filterText% OR task.description LIKE %:filterText%)\n" +
            "AND (CASE\n" +
            "        WHEN task_user_task_complete.id IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END = :filterIsComplete OR CASE\n" +
            "        WHEN task_user_task_complete.id IS NOT NULL THEN FALSE\n" +
            "        ELSE TRUE\n" +
            "    END = :filterIsNotComplete)\n" +
            "AND (CASE\n" +
            "        WHEN task_user_task_star.id IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END = :filterIsStar OR CASE\n" +
            "        WHEN task_user_task_star.id IS NOT NULL THEN FALSE\n" +
            "        ELSE TRUE\n" +
            "    END = :filterIsNotStar)\n" +
            "AND (CASE\n" +
            "        WHEN task_user_task_pin.id IS NOT NULL THEN TRUE\n" +
            "        ELSE FALSE\n" +
            "    END = :filterIsPin OR CASE\n" +
            "        WHEN task_user_task_pin.id IS NOT NULL THEN FALSE\n" +
            "        ELSE TRUE\n" +
            "    END = :filterIsNotPin)\n" +
            "ORDER BY isComplete ASC, sortByTimestamp DESC")
    List<TaskDTO> getTasksInPod(
            String idPod,
            String idUser,
            @Param("filterText") String filterText,
            @Param("filterIsComplete") boolean filterIsComplete,
            @Param("filterIsNotComplete") boolean filterIsNotComplete,
            @Param("filterIsStar") boolean filterIsStar,
            @Param("filterIsNotStar") boolean filterIsNotStar,
            @Param("filterIsPin") boolean filterIsPin,
            @Param("filterIsNotPin") boolean filterIsNotPin
    );


}
