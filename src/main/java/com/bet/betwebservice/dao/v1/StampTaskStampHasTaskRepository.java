package com.bet.betwebservice.dao.v1;

import com.bet.betwebservice.entity.StampTaskStampHasTaskEntity;
import com.bet.betwebservice.entity.TaskUserTaskPinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StampTaskStampHasTaskRepository extends JpaRepository<StampTaskStampHasTaskEntity, UUID> {
    @Query("SELECT stamp_task_stamp_has_task \n" +
            "FROM StampTaskStampHasTaskEntity stamp_task_stamp_has_task \n" +
            "WHERE BIN_TO_UUID(stamp_task_stamp_has_task.idStamp) = :idStamp AND\n" +
            "BIN_TO_UUID(stamp_task_stamp_has_task.idTask) = :idTask")
    List<StampTaskStampHasTaskEntity> findByIdStampIdTask(
        @Param("idStamp") String idStamp, 
        @Param("idTask") String idTask
    );

    @Query("SELECT stamp_task_stamp_has_task \n" +
        "FROM StampTaskStampHasTaskEntity stamp_task_stamp_has_task \n" +
        "WHERE BIN_TO_UUID(stamp_task_stamp_has_task.idStamp) = :idStamp")
    List<StampTaskStampHasTaskEntity> findByIdStamp(
        @Param("idStamp") String idStamp
    );
}
