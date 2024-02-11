package com.bet.betwebservice.dao;

import com.bet.betwebservice.entity.TaskUserTaskNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskNoteRepository extends JpaRepository<TaskUserTaskNoteEntity, UUID> {
    @Query("SELECT task_user_task_note_text \n" +
            "FROM TaskUserTaskNoteEntity task_user_task_note_text \n" +
            "WHERE BIN_TO_UUID(task_user_task_note_text.idUser) = :idUser AND\n" +
            "BIN_TO_UUID(task_user_task_note_text.idTask) = :idTask")
    List<TaskUserTaskNoteEntity> findByIdTaskIdUser(
        @Param("idTask") String idTask, 
        @Param("idUser") String idUser
    );
}
