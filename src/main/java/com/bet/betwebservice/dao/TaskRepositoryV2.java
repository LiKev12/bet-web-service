package com.bet.betwebservice.dao;

import com.bet.betwebservice.dto.NumberOfPointsInTasksCompletedOverTimeVisualizationDTO;
import com.bet.betwebservice.dto.TaskCommentDTO;
import com.bet.betwebservice.dto.TaskIndividualPropertiesDTO;
import com.bet.betwebservice.dto.TaskReactionDTO;
import com.bet.betwebservice.dto.TaskSharedPropertiesDTO;
import com.bet.betwebservice.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;



public interface TaskRepositoryV2 extends JpaRepository<TaskEntity, UUID> {

}
