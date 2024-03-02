package com.bet.betwebservice.dao;

import com.bet.betwebservice.entity.StampTaskStampHasTaskEntity;
import com.bet.betwebservice.entity.TaskUserTaskPinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StampTaskStampHasTaskRepositoryV2 extends JpaRepository<StampTaskStampHasTaskEntity, UUID> {
    
}