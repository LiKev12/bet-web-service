package com.bet.betwebservice.dao;

import com.bet.betwebservice.entity.TaskUserTaskStarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskUserTaskStarRepositoryV2 extends JpaRepository<TaskUserTaskStarEntity, UUID> {
    
}
